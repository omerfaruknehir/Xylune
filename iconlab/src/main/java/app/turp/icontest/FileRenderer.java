package app.turp.icontest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Xml;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

final class FileRenderer {
    interface Callback {
        void onSuccess(Bitmap bitmap, String kind);
        void onError(String message);
    }

    private static final int OUTPUT_SIZE = 768;
    private static final int MAX_BYTES = 8 * 1024 * 1024;
    private static final Executor IO = Executors.newSingleThreadExecutor();

    private FileRenderer() {}

    static void render(Context context, Uri uri, String displayName, Callback callback) {
        IO.execute(() -> {
            try {
                byte[] bytes = readLimited(context, uri);
                String type = detect(bytes, displayName);
                if ("PNG".equals(type)) {
                    Bitmap bitmap = renderPng(bytes);
                    postSuccess(callback, bitmap, type);
                } else if ("ANDROID_XML".equals(type)) {
                    Bitmap bitmap = renderAndroidXml(context, bytes);
                    postSuccess(callback, bitmap, "Android drawable XML");
                } else if ("SVG".equals(type)) {
                    String svg = new String(bytes, StandardCharsets.UTF_8);
                    new Handler(Looper.getMainLooper()).post(() -> renderSvg(context, svg, callback));
                } else {
                    postError(callback, "Unsupported file. Choose a PNG, SVG, or Android drawable XML file.");
                }
            } catch (Exception error) {
                String message = error.getMessage();
                postError(callback, message == null || message.trim().isEmpty() ? error.getClass().getSimpleName() : message);
            }
        });
    }

    private static byte[] readLimited(Context context, Uri uri) throws IOException {
        try (InputStream input = context.getContentResolver().openInputStream(uri)) {
            if (input == null) throw new IOException("Could not open the selected file.");
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[16 * 1024];
            int total = 0;
            int read;
            while ((read = input.read(buffer)) != -1) {
                total += read;
                if (total > MAX_BYTES) throw new IOException("File is larger than 8 MB.");
                output.write(buffer, 0, read);
            }
            return output.toByteArray();
        }
    }

    private static String detect(byte[] bytes, String displayName) {
        if (bytes.length >= 8
                && (bytes[0] & 0xff) == 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4e && bytes[3] == 0x47
                && bytes[4] == 0x0d && bytes[5] == 0x0a && bytes[6] == 0x1a && bytes[7] == 0x0a) {
            return "PNG";
        }
        String text = new String(bytes, 0, Math.min(bytes.length, 8192), StandardCharsets.UTF_8)
                .replace("\uFEFF", "")
                .trim()
                .toLowerCase(Locale.ROOT);
        if (text.contains("<svg") || (displayName != null && displayName.toLowerCase(Locale.ROOT).endsWith(".svg"))) {
            return "SVG";
        }
        if (text.startsWith("<?xml") || text.startsWith("<vector") || text.startsWith("<adaptive-icon")
                || (displayName != null && displayName.toLowerCase(Locale.ROOT).endsWith(".xml"))) {
            return "ANDROID_XML";
        }
        return "UNKNOWN";
    }

    private static Bitmap renderPng(byte[] bytes) throws IOException {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bounds);
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) throw new IOException("Invalid PNG file.");

        int max = Math.max(bounds.outWidth, bounds.outHeight);
        int sample = 1;
        while (max / sample > 2048) sample *= 2;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sample;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        if (decoded == null) throw new IOException("Android could not decode this PNG.");
        return LauncherUtil.squareBitmap(decoded, OUTPUT_SIZE);
    }

    private static Bitmap renderAndroidXml(Context context, byte[] bytes) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8.name());
        int event;
        do {
            event = parser.next();
        } while (event != XmlPullParser.START_TAG && event != XmlPullParser.END_DOCUMENT);
        if (event != XmlPullParser.START_TAG) throw new IOException("XML has no drawable root element.");

        Drawable drawable;
        try {
            drawable = Drawable.createFromXml(context.getResources(), parser, context.getTheme());
        } catch (Exception e) {
            throw new IOException("Could not inflate this drawable XML. Self-contained VectorDrawable XML works; XML that references resources not included in this app cannot be resolved.", e);
        }
        if (drawable == null) throw new IOException("Android did not recognize this drawable XML.");
        Bitmap bitmap = Bitmap.createBitmap(OUTPUT_SIZE, OUTPUT_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        int iw = drawable.getIntrinsicWidth();
        int ih = drawable.getIntrinsicHeight();
        if (iw <= 0) iw = OUTPUT_SIZE;
        if (ih <= 0) ih = OUTPUT_SIZE;
        float scale = Math.min((float) OUTPUT_SIZE / iw, (float) OUTPUT_SIZE / ih);
        int width = Math.max(1, Math.round(iw * scale));
        int height = Math.max(1, Math.round(ih * scale));
        int left = (OUTPUT_SIZE - width) / 2;
        int top = (OUTPUT_SIZE - height) / 2;
        drawable.setBounds(left, top, left + width, top + height);
        drawable.draw(canvas);
        return bitmap;
    }

    private static void renderSvg(Context context, String rawSvg, Callback callback) {
        String svg = rawSvg
                .replaceFirst("(?is)^\\s*<\\?xml[^>]*>\\s*", "")
                .replaceFirst("(?is)<!DOCTYPE[^>]*>\\s*", "");
        if (!svg.toLowerCase(Locale.ROOT).contains("<svg")) {
            callback.onError("This file does not contain an SVG root element.");
            return;
        }

        WebView webView = new WebView(context);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setDomStorageEnabled(false);
        try { settings.setBlockNetworkLoads(true); } catch (Exception ignored) {}

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Uri url = request.getUrl();
                if (url != null && !"about".equalsIgnoreCase(url.getScheme()) && !"data".equalsIgnoreCase(url.getScheme())) {
                    return new WebResourceResponse("text/plain", "UTF-8", new ByteArrayInputStream(new byte[0]));
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.postDelayed(() -> {
                    try {
                        int spec = android.view.View.MeasureSpec.makeMeasureSpec(OUTPUT_SIZE, android.view.View.MeasureSpec.EXACTLY);
                        view.measure(spec, spec);
                        view.layout(0, 0, OUTPUT_SIZE, OUTPUT_SIZE);
                        Bitmap bitmap = Bitmap.createBitmap(OUTPUT_SIZE, OUTPUT_SIZE, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        view.draw(canvas);
                        callback.onSuccess(bitmap, "SVG");
                    } catch (Exception e) {
                        callback.onError("Could not render SVG: " + e.getMessage());
                    } finally {
                        view.destroy();
                    }
                }, 80);
            }
        });

        String html = "<!doctype html><html><head><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">"
                + "<style>html,body{margin:0;width:100%;height:100%;overflow:hidden;background:transparent}"
                + "body{display:flex;align-items:center;justify-content:center}svg{display:block;max-width:100%;max-height:100%;width:100%;height:100%}</style>"
                + "</head><body>" + svg + "</body></html>";
        webView.loadDataWithBaseURL("about:blank", html, "text/html", "UTF-8", null);
    }

    private static void postSuccess(Callback callback, Bitmap bitmap, String type) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(bitmap, type));
    }

    private static void postError(Callback callback, String message) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onError(message));
    }
}
