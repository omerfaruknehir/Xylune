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
import java.io.InputStream;
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

final class SelfApkBuilder {
    private static final char[] PASSWORD = "iconlab-test-only".toCharArray();

    private SelfApkBuilder() {}

    static File build(Context context, String newLabel, Bitmap newIcon) throws Exception {
        String label = newLabel == null ? "" : newLabel.trim();
        if (label.isEmpty()) throw new IllegalArgumentException("Launcher name cannot be empty");

        String currentLabel = context.getString(R.string.app_label);
        File dir = new File(context.getCacheDir(), "self-update");
        if (!dir.exists() && !dir.mkdirs()) throw new IllegalStateException("Cannot create update workspace");
        File unsigned = new File(dir, "unsigned.apk");
        File signed = new File(dir, "signed.apk");
        unsigned.delete();
        signed.delete();

        byte[] replacementPng = newIcon == null ? null : toPng(newIcon);
        repack(context.getApplicationInfo().sourceDir, unsigned, currentLabel, label, replacementPng);
        sign(context, unsigned, signed);
        return signed;
    }

    private static void repack(String sourceApk, File output, String currentLabel, String newLabel, byte[] iconPng) throws Exception {
        boolean labelDone = false;
        boolean iconDone = iconPng == null;
        try (ZipFile zip = new ZipFile(sourceApk); ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output))) {
            Enumeration<? extends ZipEntry> all = zip.entries();
            while (all.hasMoreElements()) {
                ZipEntry old = all.nextElement();
                String name = old.getName();
                if (isOldSignature(name)) continue;
                byte[] bytes;
                try (InputStream in = zip.getInputStream(old)) { bytes = readAll(in); }

                if ("resources.arsc".equals(name)) {
                    bytes = ResourceSlotPatcher.patch(bytes, currentLabel, newLabel);
                    labelDone = true;
                }
                if (iconPng != null && name.endsWith("/icon_foreground.png")) {
                    bytes = iconPng;
                    iconDone = true;
                }
                put(out, old, bytes);
            }
        }
        if (!labelDone) throw new IllegalStateException("Launcher label resource was not patched");
        if (!iconDone) throw new IllegalStateException("Launcher foreground resource was not found in the APK");
    }

    private static void put(ZipOutputStream out, ZipEntry old, byte[] bytes) throws Exception {
        ZipEntry entry = new ZipEntry(old.getName());
        entry.setTime(old.getTime());
        if (old.getComment() != null) entry.setComment(old.getComment());
        if (old.getExtra() != null) entry.setExtra(old.getExtra());
        if (old.getMethod() == ZipEntry.STORED) {
            CRC32 crc = new CRC32(); crc.update(bytes);
            entry.setMethod(ZipEntry.STORED);
            entry.setSize(bytes.length);
            entry.setCompressedSize(bytes.length);
            entry.setCrc(crc.getValue());
        } else {
            entry.setMethod(ZipEntry.DEFLATED);
        }
        out.putNextEntry(entry);
        out.write(bytes);
        out.closeEntry();
    }

    private static boolean isOldSignature(String name) {
        String n = name.toUpperCase(Locale.ROOT);
        if (!n.startsWith("META-INF/")) return false;
        return n.equals("META-INF/MANIFEST.MF") || n.endsWith(".SF") || n.endsWith(".RSA") || n.endsWith(".DSA") || n.endsWith(".EC");
    }

    private static void sign(Context context, File input, File output) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream in = context.getAssets().open("selfsign.p12")) { keyStore.load(in, PASSWORD); }
        Key key = keyStore.getKey("iconlab", PASSWORD);
        if (!(key instanceof PrivateKey)) throw new IllegalStateException("Embedded private signing key is unavailable");
        Certificate[] chain = keyStore.getCertificateChain("iconlab");
        List<X509Certificate> certs = new ArrayList<>();
        if (chain != null) for (Certificate c : chain) certs.add((X509Certificate) c);
        if (certs.isEmpty()) throw new IllegalStateException("Embedded signing certificate is unavailable");

        ApkSigner.SignerConfig config = new ApkSigner.SignerConfig.Builder("iconlab", (PrivateKey) key, certs).build();
        new ApkSigner.Builder(Collections.singletonList(config))
                .setInputApk(input)
                .setOutputApk(output)
                .setMinSdkVersion(26)
                .setV1SigningEnabled(false)
                .setV2SigningEnabled(true)
                .setV3SigningEnabled(true)
                .build()
                .sign();
    }

    private static byte[] toPng(Bitmap source) throws Exception {
        Bitmap out = Bitmap.createBitmap(432, 432, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        float scale = Math.min(432f / source.getWidth(), 432f / source.getHeight());
        float w = source.getWidth() * scale;
        float h = source.getHeight() * scale;
        RectF dst = new RectF((432f - w) / 2f, (432f - h) / 2f, (432f + w) / 2f, (432f + h) / 2f);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(source, null, dst, paint);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (!out.compress(Bitmap.CompressFormat.PNG, 100, bytes)) throw new IllegalStateException("Could not encode launcher PNG");
        out.recycle();
        return bytes.toByteArray();
    }

    private static byte[] readAll(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[65536];
        int n;
        while ((n = in.read(buffer)) != -1) out.write(buffer, 0, n);
        return out.toByteArray();
    }
}
