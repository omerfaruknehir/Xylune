package app.turp.iconlab;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.apksig.ApkSigner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

final class ApkCustomizer {
    private static final String STORE_PASSWORD = "iconlab-test-only";
    private static final String KEY_ALIAS = "iconlab";

    private ApkCustomizer() {}

    static File build(Context context, String label, Bitmap icon) throws Exception {
        String cleaned = label == null ? "" : label.trim();
        if (cleaned.isEmpty()) throw new IllegalArgumentException("Launcher name cannot be empty");

        File work = new File(context.getCacheDir(), "self-update");
        if (!work.exists() && !work.mkdirs()) throw new IllegalStateException("Cannot create update workspace");
        File unsigned = new File(work, "iconlab-unsigned.apk");
        File signed = new File(work, "iconlab-update.apk");
        if (unsigned.exists()) unsigned.delete();
        if (signed.exists()) signed.delete();

        byte[] png = icon == null ? null : bitmapToPng(icon);
        rewrite(context.getApplicationInfo().sourceDir, unsigned, cleaned, png);
        sign(context, unsigned, signed);
        if (!signed.isFile() || signed.length() == 0) throw new IllegalStateException("Signing produced no APK");
        return signed;
    }

    private static void rewrite(String sourcePath, File outFile, String label, byte[] iconPng) throws Exception {
        boolean patchedLabel = false;
        boolean patchedIcon = iconPng == null;
        try (ZipFile source = new ZipFile(sourcePath);
             ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFile))) {
            Enumeration<? extends ZipEntry> entries = source.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (isSignatureEntry(name)) continue;

                byte[] bytes;
                try (InputStream in = source.getInputStream(entry)) {
                    bytes = readAll(in);
                }
                if ("resources.arsc".equals(name)) {
                    bytes = ResourceStringPatcher.patch(bytes, label);
                    patchedLabel = true;
                }
                if (name.endsWith("/icon_foreground.png") && iconPng != null) {
                    bytes = iconPng;
                    patchedIcon = true;
                }
                writeEntry(out, entry, bytes);
            }
        }
        if (!patchedLabel) throw new IllegalStateException("Could not patch launcher label");
        if (!patchedIcon) throw new IllegalStateException("Could not find packaged icon_foreground.png");
    }

    private static void writeEntry(ZipOutputStream out, ZipEntry original, byte[] bytes) throws Exception {
        ZipEntry replacement = new ZipEntry(original.getName());
        replacement.setTime(original.getTime());
        replacement.setComment(original.getComment());
        replacement.setExtra(original.getExtra());
        if (original.getMethod() == ZipEntry.STORED) {
            CRC32 crc = new CRC32();
            crc.update(bytes);
            replacement.setMethod(ZipEntry.STORED);
            replacement.setSize(bytes.length);
            replacement.setCompressedSize(bytes.length);
            replacement.setCrc(crc.getValue());
        } else {
            replacement.setMethod(ZipEntry.DEFLATED);
        }
        out.putNextEntry(replacement);
        out.write(bytes);
        out.closeEntry();
    }

    private static boolean isSignatureEntry(String name) {
        String upper = name.toUpperCase(Locale.ROOT);
        if (!upper.startsWith("META-INF/")) return false;
        return upper.equals("META-INF/MANIFEST.MF") || upper.endsWith(".RSA") || upper.endsWith(".DSA") || upper.endsWith(".EC") || upper.endsWith(".SF");
    }

    private static byte[] bitmapToPng(Bitmap source) throws Exception {
        Bitmap scaled = Bitmap.createBitmap(432, 432, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas canvas = new android.graphics.Canvas(scaled);
        canvas.drawColor(android.graphics.Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
        float sx = 432f / source.getWidth();
        float sy = 432f / source.getHeight();
        float scale = Math.min(sx, sy);
        float w = source.getWidth() * scale;
        float h = source.getHeight() * scale;
        android.graphics.RectF dst = new android.graphics.RectF((432f - w) / 2f, (432f - h) / 2f, (432f + w) / 2f, (432f + h) / 2f);
        android.graphics.Paint paint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG | android.graphics.Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(source, null, dst, paint);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        if (!scaled.compress(Bitmap.CompressFormat.PNG, 100, buffer)) throw new IllegalStateException("PNG encoding failed");
        if (scaled != source) scaled.recycle();
        return buffer.toByteArray();
    }

    private static void sign(Context context, File input, File output) throws Exception {
        KeyStore store = KeyStore.getInstance("PKCS12");
        try (InputStream in = context.getAssets().open("selfsign.p12")) {
            store.load(in, STORE_PASSWORD.toCharArray());
        }
        Key key = store.getKey(KEY_ALIAS, STORE_PASSWORD.toCharArray());
        if (!(key instanceof PrivateKey)) throw new IllegalStateException("Embedded signing private key is missing");
        Certificate[] chain = store.getCertificateChain(KEY_ALIAS);
        List<X509Certificate> certificates = new ArrayList<>();
        if (chain != null) {
            for (Certificate certificate : chain) certificates.add((X509Certificate) certificate);
        }
        if (certificates.isEmpty()) throw new IllegalStateException("Embedded signing certificate is missing");

        ApkSigner.SignerConfig signer = new ApkSigner.SignerConfig.Builder(KEY_ALIAS, (PrivateKey) key, certificates).build();
        new ApkSigner.Builder(Collections.singletonList(signer))
                .setInputApk(input)
                .setOutputApk(output)
                .setMinSdkVersion(26)
                .setV1SigningEnabled(false)
                .setV2SigningEnabled(true)
                .setV3SigningEnabled(true)
                .build()
                .sign();
    }

    private static byte[] readAll(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[64 * 1024];
        int n;
        while ((n = in.read(buffer)) != -1) out.write(buffer, 0, n);
        return out.toByteArray();
    }
}
