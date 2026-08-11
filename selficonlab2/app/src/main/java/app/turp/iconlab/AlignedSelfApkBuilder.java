package app.turp.iconlab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;

import com.android.apksig.ApkSigner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

final class AlignedSelfApkBuilder {
    private static final char[] PASSWORD = "iconlab-test-only".toCharArray();

    private AlignedSelfApkBuilder() {}

    static File build(Context context, String requestedLabel, Bitmap requestedIcon) throws Exception {
        String label = requestedLabel == null ? "" : requestedLabel.trim();
        if (label.isEmpty()) throw new IllegalArgumentException("Launcher name cannot be empty");
        File dir = new File(context.getCacheDir(), "self-update-aligned");
        if (!dir.exists() && !dir.mkdirs()) throw new IllegalStateException("Cannot create update workspace");
        File unsigned = new File(dir, "unsigned.apk");
        File signed = new File(dir, "signed.apk");
        unsigned.delete(); signed.delete();
        String currentLabel = context.getString(R.string.app_label);
        byte[] icon = requestedIcon == null ? null : png(requestedIcon);
        rewrite(context.getApplicationInfo().sourceDir, unsigned, currentLabel, label, icon);
        sign(context, unsigned, signed);
        return signed;
    }

    private static void rewrite(String input, File output, String currentLabel, String label, byte[] icon) throws Exception {
        boolean labelDone = false;
        boolean iconDone = icon == null;
        try (ZipFile source = new ZipFile(input);
             CountingOutputStream counter = new CountingOutputStream(new FileOutputStream(output));
             ZipOutputStream zip = new ZipOutputStream(counter)) {
            Enumeration<? extends ZipEntry> entries = source.entries();
            while (entries.hasMoreElements()) {
                ZipEntry old = entries.nextElement();
                String name = old.getName();
                if (isSignature(name)) continue;
                byte[] data;
                try (InputStream in = source.getInputStream(old)) { data = readAll(in); }
                if ("resources.arsc".equals(name)) {
                    data = ResourceSlotPatcher.patch(data, currentLabel, label);
                    labelDone = true;
                }
                if (icon != null && name.endsWith("/icon_foreground.png")) {
                    data = icon;
                    iconDone = true;
                }
                putAligned(zip, counter, old, data);
            }
        }
        if (!labelDone) throw new IllegalStateException("Launcher label resource was not patched");
        if (!iconDone) throw new IllegalStateException("Packaged adaptive foreground PNG was not found");
    }

    private static void putAligned(ZipOutputStream zip, CountingOutputStream counter, ZipEntry old, byte[] data) throws Exception {
        ZipEntry entry = new ZipEntry(old.getName());
        if (old.getComment() != null) entry.setComment(old.getComment());
        if (old.getMethod() == ZipEntry.STORED) {
            CRC32 crc = new CRC32(); crc.update(data);
            entry.setMethod(ZipEntry.STORED);
            entry.setSize(data.length);
            entry.setCompressedSize(data.length);
            entry.setCrc(crc.getValue());
            byte[] previous = old.getExtra() == null ? new byte[0] : old.getExtra();
            byte[] name = old.getName().getBytes(StandardCharsets.UTF_8);
            int paddingPayload = (int) ((4 - ((counter.count + 30L + name.length + previous.length + 4L) & 3L)) & 3L);
            byte[] extra = new byte[previous.length + 4 + paddingPayload];
            System.arraycopy(previous, 0, extra, 0, previous.length);
            int p = previous.length;
            extra[p] = 0x35; extra[p + 1] = (byte) 0xD9;
            extra[p + 2] = (byte) paddingPayload; extra[p + 3] = 0;
            entry.setExtra(extra);
        } else {
            entry.setMethod(ZipEntry.DEFLATED);
            if (old.getExtra() != null) entry.setExtra(old.getExtra());
        }
        zip.putNextEntry(entry);
        zip.write(data);
        zip.closeEntry();
    }

    private static boolean isSignature(String name) {
        String n = name.toUpperCase(Locale.ROOT);
        return n.startsWith("META-INF/") && (n.equals("META-INF/MANIFEST.MF") || n.endsWith(".SF") || n.endsWith(".RSA") || n.endsWith(".DSA") || n.endsWith(".EC"));
    }

    private static void sign(Context context, File input, File output) throws Exception {
        KeyStore store = KeyStore.getInstance("PKCS12");
        try (InputStream in = context.getAssets().open("selfsign.p12")) { store.load(in, PASSWORD); }
        Key key = store.getKey("iconlab", PASSWORD);
        if (!(key instanceof PrivateKey)) throw new IllegalStateException("Embedded private signing key missing");
        Certificate[] chain = store.getCertificateChain("iconlab");
        List<X509Certificate> certs = new ArrayList<>();
        if (chain != null) for (Certificate certificate : chain) certs.add((X509Certificate) certificate);
        if (certs.isEmpty()) throw new IllegalStateException("Embedded signing certificate missing");
        ApkSigner.SignerConfig config = new ApkSigner.SignerConfig.Builder("iconlab", (PrivateKey) key, certs).build();
        new ApkSigner.Builder(Collections.singletonList(config))
                .setInputApk(input).setOutputApk(output).setMinSdkVersion(26)
                .setV1SigningEnabled(false).setV2SigningEnabled(true).setV3SigningEnabled(true)
                .build().sign();
    }

    private static byte[] png(Bitmap source) throws Exception {
        Bitmap out = Bitmap.createBitmap(432, 432, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        float scale = Math.min(432f / source.getWidth(), 432f / source.getHeight());
        float w = source.getWidth() * scale, h = source.getHeight() * scale;
        RectF dst = new RectF((432f - w) / 2f, (432f - h) / 2f, (432f + w) / 2f, (432f + h) / 2f);
        canvas.drawBitmap(source, null, dst, new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (!out.compress(Bitmap.CompressFormat.PNG, 100, bytes)) throw new IllegalStateException("PNG encoding failed");
        out.recycle();
        return bytes.toByteArray();
    }

    private static byte[] readAll(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[65536]; int n;
        while ((n = in.read(buffer)) != -1) out.write(buffer, 0, n);
        return out.toByteArray();
    }

    private static final class CountingOutputStream extends FilterOutputStream {
        long count;
        CountingOutputStream(OutputStream out) { super(out); }
        @Override public void write(int b) throws java.io.IOException { out.write(b); count++; }
        @Override public void write(byte[] b, int off, int len) throws java.io.IOException { out.write(b, off, len); count += len; }
    }
}
