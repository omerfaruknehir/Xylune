package app.turp.iconlab;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

final class ResourceSlotPatcher {
    private static final int RES_STRING_POOL_TYPE = 0x0001;
    private static final int UTF8_FLAG = 0x100;

    private ResourceSlotPatcher() {}

    static byte[] patch(byte[] arsc, String current, String replacement) {
        byte[] out = Arrays.copyOf(arsc, arsc.length);
        int headerSize = u16(out, 2);
        int totalSize = Math.min(out.length, u32(out, 4));
        for (int pos = headerSize; pos + 8 <= totalSize;) {
            int type = u16(out, pos);
            int size = u32(out, pos + 4);
            if (size < 8 || pos + size > totalSize) break;
            if (type == RES_STRING_POOL_TYPE && patchPool(out, pos, size, current, replacement)) return out;
            pos += size;
        }
        throw new IllegalStateException("Current launcher label was not found in the compiled resource string pool");
    }

    private static boolean patchPool(byte[] data, int base, int chunkSize, String current, String replacement) {
        int headerSize = u16(data, base + 2);
        int count = u32(data, base + 8);
        int flags = u32(data, base + 16);
        int stringsStart = u32(data, base + 20);
        int stylesStart = u32(data, base + 24);
        if (headerSize < 28 || count < 0 || count > 100000) return false;
        int offsets = base + headerSize;
        int strings = base + stringsStart;
        boolean utf8 = (flags & UTF8_FLAG) != 0;

        for (int i = 0; i < count; i++) {
            int rel = u32(data, offsets + i * 4);
            int start = strings + rel;
            String value = utf8 ? readUtf8(data, start) : readUtf16(data, start);
            if (!current.equals(value)) continue;

            int endRel = stylesStart != 0 ? stylesStart - stringsStart : chunkSize - stringsStart;
            for (int j = 0; j < count; j++) {
                int next = u32(data, offsets + j * 4);
                if (next > rel && next < endRel) endRel = next;
            }
            int capacity = endRel - rel;
            byte[] encoded = utf8 ? encodeUtf8(replacement) : encodeUtf16(replacement);
            if (encoded.length > capacity) {
                throw new IllegalArgumentException("Launcher name is too long. Use a shorter name.");
            }
            Arrays.fill(data, start, start + capacity, (byte) 0);
            System.arraycopy(encoded, 0, data, start, encoded.length);
            return true;
        }
        return false;
    }

    private static String readUtf8(byte[] d, int p) {
        try {
            Len utf16 = len8(d, p);
            Len bytes = len8(d, p + utf16.n);
            int start = p + utf16.n + bytes.n;
            if (start < 0 || bytes.value < 0 || start + bytes.value > d.length) return null;
            return new String(d, start, bytes.value, StandardCharsets.UTF_8);
        } catch (Throwable ignored) { return null; }
    }

    private static String readUtf16(byte[] d, int p) {
        try {
            Len len = len16(d, p);
            int start = p + len.n;
            int bytes = len.value * 2;
            if (start < 0 || bytes < 0 || start + bytes > d.length) return null;
            return new String(d, start, bytes, StandardCharsets.UTF_16LE);
        } catch (Throwable ignored) { return null; }
    }

    private static byte[] encodeUtf8(String s) {
        byte[] body = s.getBytes(StandardCharsets.UTF_8);
        byte[] a = putLen8(s.length());
        byte[] b = putLen8(body.length);
        byte[] out = new byte[a.length + b.length + body.length + 1];
        int p = 0;
        System.arraycopy(a, 0, out, p, a.length); p += a.length;
        System.arraycopy(b, 0, out, p, b.length); p += b.length;
        System.arraycopy(body, 0, out, p, body.length);
        return out;
    }

    private static byte[] encodeUtf16(String s) {
        byte[] body = s.getBytes(StandardCharsets.UTF_16LE);
        byte[] len = putLen16(s.length());
        byte[] out = new byte[len.length + body.length + 2];
        System.arraycopy(len, 0, out, 0, len.length);
        System.arraycopy(body, 0, out, len.length, body.length);
        return out;
    }

    private static Len len8(byte[] d, int p) {
        int first = d[p] & 0xff;
        return (first & 0x80) == 0 ? new Len(first, 1) : new Len(((first & 0x7f) << 8) | (d[p + 1] & 0xff), 2);
    }

    private static Len len16(byte[] d, int p) {
        int first = u16(d, p);
        return (first & 0x8000) == 0 ? new Len(first, 2) : new Len(((first & 0x7fff) << 16) | u16(d, p + 2), 4);
    }

    private static byte[] putLen8(int len) {
        if (len <= 0x7f) return new byte[] {(byte) len};
        if (len > 0x7fff) throw new IllegalArgumentException("String too long");
        return new byte[] {(byte) (0x80 | (len >> 8)), (byte) len};
    }

    private static byte[] putLen16(int len) {
        if (len <= 0x7fff) return new byte[] {(byte) len, (byte) (len >> 8)};
        throw new IllegalArgumentException("String too long");
    }

    private static int u16(byte[] d, int p) {
        return (d[p] & 0xff) | ((d[p + 1] & 0xff) << 8);
    }

    private static int u32(byte[] d, int p) {
        return (d[p] & 0xff) | ((d[p + 1] & 0xff) << 8) | ((d[p + 2] & 0xff) << 16) | ((d[p + 3] & 0xff) << 24);
    }

    private static final class Len {
        final int value, n;
        Len(int value, int n) { this.value = value; this.n = n; }
    }
}
