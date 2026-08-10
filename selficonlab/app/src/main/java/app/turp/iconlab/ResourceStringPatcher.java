package app.turp.iconlab;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

final class ResourceStringPatcher {
    static final String PLACEHOLDER = "ICONLAB_LABEL_SLOT_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
    private static final int RES_STRING_POOL_TYPE = 0x0001;
    private static final int UTF8_FLAG = 0x00000100;

    private ResourceStringPatcher() {}

    static byte[] patch(byte[] arsc, String replacement) {
        byte[] out = Arrays.copyOf(arsc, arsc.length);
        int topHeader = u16(out, 2);
        int topSize = u32(out, 4);
        int end = Math.min(out.length, topSize);
        for (int pos = topHeader; pos + 8 <= end;) {
            int type = u16(out, pos);
            int size = u32(out, pos + 4);
            if (size < 8 || pos + size > end) break;
            if (type == RES_STRING_POOL_TYPE && patchPool(out, pos, size, replacement)) return out;
            pos += size;
        }
        throw new IllegalStateException("Patchable launcher label slot was not found in resources.arsc");
    }

    private static boolean patchPool(byte[] data, int base, int chunkSize, String replacement) {
        int headerSize = u16(data, base + 2);
        int stringCount = u32(data, base + 8);
        int flags = u32(data, base + 16);
        int stringsStart = u32(data, base + 20);
        int stylesStart = u32(data, base + 24);
        if (headerSize < 28 || stringCount < 0 || stringCount > 100000) return false;
        int offsetsBase = base + headerSize;
        int stringsBase = base + stringsStart;
        boolean utf8 = (flags & UTF8_FLAG) != 0;

        for (int i = 0; i < stringCount; i++) {
            int rel = u32(data, offsetsBase + i * 4);
            int start = stringsBase + rel;
            Decoded decoded = utf8 ? decodeUtf8(data, start) : decodeUtf16(data, start);
            if (decoded == null || !PLACEHOLDER.equals(decoded.text)) continue;

            int nextRel = stylesStart != 0 ? stylesStart - stringsStart : chunkSize - stringsStart;
            for (int j = 0; j < stringCount; j++) {
                int candidate = u32(data, offsetsBase + j * 4);
                if (candidate > rel && candidate < nextRel) nextRel = candidate;
            }
            int capacity = nextRel - rel;
            byte[] encoded = utf8 ? encodeUtf8(replacement) : encodeUtf16(replacement);
            if (encoded.length > capacity) {
                throw new IllegalArgumentException("Launcher name is too long for the reserved resource slot");
            }
            Arrays.fill(data, start, start + capacity, (byte) 0);
            System.arraycopy(encoded, 0, data, start, encoded.length);
            return true;
        }
        return false;
    }

    private static Decoded decodeUtf8(byte[] data, int pos) {
        try {
            Len a = readLen8(data, pos);
            Len b = readLen8(data, pos + a.bytes);
            int textPos = pos + a.bytes + b.bytes;
            if (textPos + b.value > data.length) return null;
            return new Decoded(new String(data, textPos, b.value, StandardCharsets.UTF_8));
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static Decoded decodeUtf16(byte[] data, int pos) {
        try {
            Len len = readLen16(data, pos);
            int textPos = pos + len.bytes;
            int byteCount = len.value * 2;
            if (textPos + byteCount > data.length) return null;
            return new Decoded(new String(data, textPos, byteCount, StandardCharsets.UTF_16LE));
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static byte[] encodeUtf8(String s) {
        byte[] body = s.getBytes(StandardCharsets.UTF_8);
        byte[] utf16Len = writeLen8(s.length());
        byte[] utf8Len = writeLen8(body.length);
        byte[] out = new byte[utf16Len.length + utf8Len.length + body.length + 1];
        int p = 0;
        System.arraycopy(utf16Len, 0, out, p, utf16Len.length); p += utf16Len.length;
        System.arraycopy(utf8Len, 0, out, p, utf8Len.length); p += utf8Len.length;
        System.arraycopy(body, 0, out, p, body.length);
        return out;
    }

    private static byte[] encodeUtf16(String s) {
        byte[] body = s.getBytes(StandardCharsets.UTF_16LE);
        byte[] len = writeLen16(s.length());
        byte[] out = new byte[len.length + body.length + 2];
        System.arraycopy(len, 0, out, 0, len.length);
        System.arraycopy(body, 0, out, len.length, body.length);
        return out;
    }

    private static Len readLen8(byte[] d, int p) {
        int first = d[p] & 0xff;
        if ((first & 0x80) == 0) return new Len(first, 1);
        return new Len(((first & 0x7f) << 8) | (d[p + 1] & 0xff), 2);
    }

    private static byte[] writeLen8(int len) {
        if (len <= 0x7f) return new byte[] {(byte) len};
        if (len > 0x7fff) throw new IllegalArgumentException("String too long");
        return new byte[] {(byte) ((len >> 8) | 0x80), (byte) len};
    }

    private static Len readLen16(byte[] d, int p) {
        int first = u16(d, p);
        if ((first & 0x8000) == 0) return new Len(first, 2);
        return new Len(((first & 0x7fff) << 16) | u16(d, p + 2), 4);
    }

    private static byte[] writeLen16(int len) {
        if (len <= 0x7fff) return new byte[] {(byte) len, (byte) (len >> 8)};
        return new byte[] {(byte) ((len >> 16) & 0xff), (byte) (((len >> 24) & 0x7f) | 0x80), (byte) len, (byte) (len >> 8)};
    }

    private static int u16(byte[] d, int p) {
        return (d[p] & 0xff) | ((d[p + 1] & 0xff) << 8);
    }

    private static int u32(byte[] d, int p) {
        return (d[p] & 0xff) | ((d[p + 1] & 0xff) << 8) | ((d[p + 2] & 0xff) << 16) | ((d[p + 3] & 0xff) << 24);
    }

    private static final class Len {
        final int value, bytes;
        Len(int value, int bytes) { this.value = value; this.bytes = bytes; }
    }
    private static final class Decoded {
        final String text;
        Decoded(String text) { this.text = text; }
    }
}
