package app.turp.icontest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import com.android.apksig.ApkSigner;
import com.android.apksig.ApkVerifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

final class SelfUpdateBuilder {
    interface Callback {
        void onStage(String message);
        void onCommitted();
        void onError(String message);
    }

    static final String ACTION_INSTALL_RESULT = "app.turp.icontest.INSTALL_RESULT";
    private static final String KEY_ASSET = "iconlab-test-key.p12";
    private static final char[] KEY_PASSWORD = "iconlab-test".toCharArray();
    private static final Executor IO = Executors.newSingleThreadExecutor();
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private SelfUpdateBuilder() {}

    static void buildAndInstall(Context context, Bitmap bitmap, String label, Callback callback) {
        Context app = context.getApplicationContext();
        IO.execute(() -> {
            try {
                stage(callback, "Rendering packaged launcher artwork…");
                byte[] iconPng = toPng(bitmap);

                File work = new File(app.getCacheDir(), "self-update");
                if (!work.exists() && !work.mkdirs()) throw new IOException("Could not create update workspace.");
                File unsignedApk = new File(work, "iconlab-patched-unsigned.apk");
                File signedApk = new File(work, "iconlab-patched-signed.apk");
                unsignedApk.delete();
                signedApk.delete();

                stage(callback, "Patching real APK resources…");
                rewriteApk(new File(app.getApplicationInfo().sourceDir), unsignedApk, iconPng, label);

                stage(callback, "Signing update with this installation's test key…");
                signApk(app, unsignedApk, signedApk);

                ApkVerifier.Result verify = new ApkVerifier.Builder(signedApk).build().verify();
                if (!verify.isVerified()) throw new IOException("The rebuilt APK did not pass signature verification.");

                stage(callback, "Committing package update…");
                install(app, signedApk);
                MAIN.post(callback::onCommitted);
            } catch (Throwable error) {
                String message = error.getMessage();
                if (message == null || message.trim().isEmpty()) message = error.getClass().getSimpleName();
                String finalMessage = message;
                MAIN.post(() -> callback.onError(finalMessage));
            }
        });
    }

    private static void stage(Callback callback, String message) {
        MAIN.post(() -> callback.onStage(message));
    }

    private static byte[] toPng(Bitmap bitmap) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) throw new IOException("Could not encode icon PNG.");
        return out.toByteArray();
    }

    private static void rewriteApk(File source, File output, byte[] iconPng, String label) throws Exception {
        boolean iconReplaced = false;
        boolean manifestReplaced = false;
        try (ZipFile zip = new ZipFile(source); ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output))) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            byte[] buffer = new byte[32 * 1024];
            while (entries.hasMoreElements()) {
                ZipEntry old = entries.nextElement();
                String name = old.getName();
                if (old.isDirectory() || isSignatureEntry(name)) continue;

                byte[] replacement = null;
                if ("AndroidManifest.xml".equals(name)) {
                    replacement = BinaryLabelSlot.patch(readAll(zip.getInputStream(old)), label);
                    manifestReplaced = true;
                } else if (name.startsWith("res/") && name.endsWith("/icon_payload.png")) {
                    replacement = iconPng;
                    iconReplaced = true;
                }

                ZipEntry fresh = new ZipEntry(name);
                fresh.setTime(0L);
                out.putNextEntry(fresh);
                if (replacement != null) {
                    out.write(replacement);
                } else {
                    try (InputStream in = zip.getInputStream(old)) {
                        int read;
                        while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
                    }
                }
                out.closeEntry();
            }
        }
        if (!manifestReplaced) throw new IOException("Could not find AndroidManifest.xml in the installed APK.");
        if (!iconReplaced) throw new IOException("Could not find the replaceable icon resource in the installed APK.");
    }

    private static boolean isSignatureEntry(String name) {
        String upper = name.toUpperCase(java.util.Locale.ROOT);
        if (!upper.startsWith("META-INF/")) return false;
        return upper.equals("META-INF/MANIFEST.MF") || upper.endsWith(".SF") || upper.endsWith(".RSA")
                || upper.endsWith(".DSA") || upper.endsWith(".EC");
    }

    private static void signApk(Context context, File input, File output) throws Exception {
        KeyStore store = KeyStore.getInstance("PKCS12");
        try (InputStream raw = context.getAssets().open(KEY_ASSET)) {
            store.load(raw, KEY_PASSWORD);
        }
        KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) store.getEntry(
                "iconlab", new KeyStore.PasswordProtection(KEY_PASSWORD));
        if (entry == null) throw new IOException("Embedded self-update signing key is missing.");
        PrivateKey privateKey = entry.getPrivateKey();
        X509Certificate certificate = (X509Certificate) entry.getCertificate();

        List<X509Certificate> certificates = new ArrayList<>();
        certificates.add(certificate);
        ApkSigner.SignerConfig signer = new ApkSigner.SignerConfig.Builder("iconlab", privateKey, certificates).build();
        new ApkSigner.Builder(Collections.singletonList(signer))
                .setInputApk(input)
                .setOutputApk(output)
                .setMinSdkVersion(26)
                .setV1SigningEnabled(true)
                .setV2SigningEnabled(true)
                .setV3SigningEnabled(true)
                .setV4SigningEnabled(false)
                .setDebuggableApkPermitted(true)
                .build()
                .sign();
    }

    private static void install(Context context, File apk) throws Exception {
        PackageInstaller installer = context.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        params.setAppPackageName(context.getPackageName());
        params.setSize(apk.length());
        if (Build.VERSION.SDK_INT >= 31) {
            params.setRequireUserAction(PackageInstaller.SessionParams.USER_ACTION_NOT_REQUIRED);
        }

        int sessionId = installer.createSession(params);
        try (PackageInstaller.Session session = installer.openSession(sessionId);
             OutputStream out = session.openWrite("base.apk", 0, apk.length());
             InputStream in = new FileInputStream(apk)) {
            byte[] buffer = new byte[64 * 1024];
            int read;
            while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
            session.fsync(out);

            Intent result = new Intent(context, InstallResultReceiver.class)
                    .setAction(ACTION_INSTALL_RESULT)
                    .putExtra("session_id", sessionId);
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= 31) flags |= PendingIntent.FLAG_MUTABLE;
            PendingIntent pending = PendingIntent.getBroadcast(context, sessionId, result, flags);
            session.commit(pending.getIntentSender());
        }
    }

    private static byte[] readAll(InputStream input) throws IOException {
        try (InputStream in = input; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[16 * 1024];
            int read;
            while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
            return out.toByteArray();
        }
    }

    private static final class BinaryLabelSlot {
        private static final int STRING_POOL_TYPE = 0x0001;
        private static final byte[] UTF8_MARKER = markerUtf8();
        private static final byte[] UTF16_MARKER = markerUtf16();

        static byte[] patch(byte[] xml, String label) throws IOException {
            if (label == null || label.trim().isEmpty()) throw new IOException("Launcher name is empty.");
            byte[] copy = xml.clone();
            int firstHeader = u16(copy, 2);
            int offset = firstHeader;
            while (offset + 8 <= copy.length) {
                int type = u16(copy, offset);
                int headerSize = u16(copy, offset + 2);
                int chunkSize = i32(copy, offset + 4);
                if (chunkSize < headerSize || chunkSize <= 0 || offset + chunkSize > copy.length) break;
                if (type == STRING_POOL_TYPE && patchPool(copy, offset, headerSize, chunkSize, label)) return copy;
                offset += chunkSize;
            }
            throw new IOException("Compiled launcher-label slot was not found. Reinstall the original Icon Lab APK.");
        }

        private static boolean patchPool(byte[] data, int pool, int headerSize, int chunkSize, String label) throws IOException {
            int count = i32(data, pool + 8);
            int styleCount = i32(data, pool + 12);
            int flags = i32(data, pool + 16);
            int stringsStart = i32(data, pool + 20);
            int stylesStart = i32(data, pool + 24);
            boolean utf8 = (flags & 0x100) != 0;
            int offsetsBase = pool + headerSize;
            int stringsBase = pool + stringsStart;
            int stringsEnd = stylesStart != 0 ? pool + stylesStart : pool + chunkSize;
            if (count <= 0 || offsetsBase + count * 4 > data.length || stringsBase >= stringsEnd) return false;

            for (int i = 0; i < count; i++) {
                int rel = i32(data, offsetsBase + i * 4);
                if (rel < 0 || stringsBase + rel >= stringsEnd) continue;
                int next = stringsEnd - stringsBase;
                for (int j = 0; j < count; j++) {
                    int candidate = i32(data, offsetsBase + j * 4);
                    if (candidate > rel && candidate < next) next = candidate;
                }
                int start = stringsBase + rel;
                int limit = stringsBase + next;
                byte[] marker = utf8 ? UTF8_MARKER : UTF16_MARKER;
                int markerStart = findLast(data, start, limit, marker);
                if (markerStart < 0) continue;
                if (utf8) patchUtf8(data, start, markerStart, label);
                else patchUtf16(data, start, markerStart, label);
                return true;
            }
            return false;
        }

        private static void patchUtf8(byte[] data, int start, int markerStart, String label) throws IOException {
            Len a = readUtf8Length(data, start);
            Len b = readUtf8Length(data, start + a.bytes);
            int payload = start + a.bytes + b.bytes;
            byte[] encoded = label.getBytes(StandardCharsets.UTF_8);
            int chars = label.length();
            if (payload + encoded.length + 1 >= markerStart) throw new IOException("Launcher name is too long for the reserved manifest slot.");
            writeUtf8Length(data, start, a.bytes, chars);
            writeUtf8Length(data, start + a.bytes, b.bytes, encoded.length);
            System.arraycopy(encoded, 0, data, payload, encoded.length);
            data[payload + encoded.length] = 0;
            for (int i = payload + encoded.length + 1; i < markerStart; i++) data[i] = 0;
        }

        private static void patchUtf16(byte[] data, int start, int markerStart, String label) throws IOException {
            int first = u16(data, start);
            int lengthBytes = (first & 0x8000) != 0 ? 4 : 2;
            int payload = start + lengthBytes;
            byte[] encoded = label.getBytes(StandardCharsets.UTF_16LE);
            if (payload + encoded.length + 2 >= markerStart) throw new IOException("Launcher name is too long for the reserved manifest slot.");
            if (lengthBytes == 2) put16(data, start, label.length());
            else {
                int len = label.length();
                put16(data, start, 0x8000 | ((len >>> 16) & 0x7fff));
                put16(data, start + 2, len & 0xffff);
            }
            System.arraycopy(encoded, 0, data, payload, encoded.length);
            data[payload + encoded.length] = 0;
            data[payload + encoded.length + 1] = 0;
            for (int i = payload + encoded.length + 2; i < markerStart; i++) data[i] = 0;
        }

        private static Len readUtf8Length(byte[] data, int offset) {
            int first = data[offset] & 0xff;
            if ((first & 0x80) == 0) return new Len(first, 1);
            return new Len(((first & 0x7f) << 8) | (data[offset + 1] & 0xff), 2);
        }

        private static void writeUtf8Length(byte[] data, int offset, int bytes, int length) throws IOException {
            if (bytes == 1) {
                if (length >= 128) throw new IOException("Launcher name is too long.");
                data[offset] = (byte) length;
            } else {
                if (length >= 32768) throw new IOException("Launcher name is too long.");
                data[offset] = (byte) (0x80 | ((length >>> 8) & 0x7f));
                data[offset + 1] = (byte) (length & 0xff);
            }
        }

        private static int findLast(byte[] data, int start, int end, byte[] needle) {
            for (int i = end - needle.length; i >= start; i--) {
                boolean match = true;
                for (int j = 0; j < needle.length; j++) {
                    if (data[i + j] != needle[j]) { match = false; break; }
                }
                if (match) return i;
            }
            return -1;
        }

        private static byte[] markerUtf8() {
            byte[] one = "\u2060".getBytes(StandardCharsets.UTF_8);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (int i = 0; i < 16; i++) out.write(one, 0, one.length);
            return out.toByteArray();
        }

        private static byte[] markerUtf16() {
            byte[] one = "\u2060".getBytes(StandardCharsets.UTF_16LE);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (int i = 0; i < 16; i++) out.write(one, 0, one.length);
            return out.toByteArray();
        }

        private static int u16(byte[] data, int offset) {
            return (data[offset] & 0xff) | ((data[offset + 1] & 0xff) << 8);
        }

        private static int i32(byte[] data, int offset) {
            return ByteBuffer.wrap(data, offset, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }

        private static void put16(byte[] data, int offset, int value) {
            data[offset] = (byte) (value & 0xff);
            data[offset + 1] = (byte) ((value >>> 8) & 0xff);
        }

        private static final class Len {
            final int value;
            final int bytes;
            Len(int value, int bytes) { this.value = value; this.bytes = bytes; }
        }
    }
}
