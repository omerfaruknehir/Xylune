package app.turp.iconlab;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Xml;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class IconRenderer {
    interface Callback {
        void onReady(Bitmap bitmap);
        void onError(Throwable error);
    }

    private static final int SIZE = 432;
    private static final ExecutorService IO = Executors.newSingleThreadExecutor();

    private IconRenderer() {}

    static void render(Activity activity, Uri uri, Callback callback) {
        IO.execute(() -> {
            try {
                byte[] data;
                try (InputStream in = activity.getContentResolver().openInputStream(uri)) {
                    if (in == null) throw new IllegalArgumentException("Cannot open selected file");
                    data = readAll(in);
                }
                String text = looksText(data) ? new String(data, StandardCharsets.UTF_8).trim() : "";
                if (text.startsWith("<svg") || text.contains("<svg ") || text.contains("<svg>")) {
                    activity.runOnUiThread(() -> renderSvg(activity, text, callback));
                } else if (text.startsWith("<")) {
                    Bitmap bitmap = renderAndroidXml(activity, data);
                    activity.runOnUiThread(() -> callback.onReady(bitmap));
                } else {
                    Bitmap decoded = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (decoded == null) throw new IllegalArgumentException("Not a PNG, SVG, or Android drawable XML file");
                    Bitmap bitmap = fit(decoded);
                    if (bitmap != decoded) decoded.recycle();
                    activity.runOnUiThread(() -> callback.onReady(bitmap));
                }
            } catch (Throwable t) {
                activity.runOnUiThread(() -> callback.onError(t));
            }
        });
    }

    private static Bitmap renderAndroidXml(Activity activity, byte[] data) throws Exception {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new ByteArrayInputStream(data), "UTF-8");
        Drawable drawable = Drawable.createFromXml(activity.getResources(), parser, activity.getTheme());
        if (drawable == null) throw new IllegalArgumentException("Android XML did not produce a drawable");
        Bitmap raw = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(raw);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        int iw = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : SIZE;
        int ih = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : SIZE;
        float scale = Math.min((float) SIZE / iw, (float) SIZE / ih);
        int w = Math.max(1, Math.round(iw * scale));
        int h = Math.max(1, Math.round(ih * scale));
        int l = (SIZE - w) / 2;
        int t = (SIZE - h) / 2;
        drawable.setBounds(l, t, l + w, t + h);
        drawable.draw(canvas);
        return raw;
    }

    private static void renderSvg(Activity activity, String svg, Callback callback) {
        WebView web = new WebView(activity);
        web.setBackgroundColor(Color.TRANSPARENT);
        web.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        web.getSettings().setJavaScriptEnabled(false);
        web.getSettings().setAllowFileAccess(false);
        web.getSettings().setAllowContentAccess(false);
        web.getSettings().setLoadsImagesAutomatically(true);
        web.setVerticalScrollBarEnabled(false);
        web.setHorizontalScrollBarEnabled(false);
        web.setTranslationX(-5000f);
        activity.addContentView(web, new ViewGroup.LayoutParams(SIZE, SIZE));

        web.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String scheme = request.getUrl().getScheme();
                if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme) || "file".equalsIgnoreCase(scheme) || "content".equalsIgnoreCase(scheme)) {
                    return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream(new byte[0]));
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.postDelayed(() -> {
                    try {
                        view.measure(
                                android.view.View.MeasureSpec.makeMeasureSpec(SIZE, android.view.View.MeasureSpec.EXACTLY),
                                android.view.View.MeasureSpec.makeMeasureSpec(SIZE, android.view.View.MeasureSpec.EXACTLY));
                        view.layout(0, 0, SIZE, SIZE);
                        Bitmap bitmap = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        view.draw(canvas);
                        callback.onReady(bitmap);
                    } catch (Throwable t) {
                        callback.onError(t);
                    } finally {
                        ViewGroup parent = (ViewGroup) view.getParent();
                        if (parent != null) parent.removeView(view);
                        view.destroy();
                    }
                }, 120);
            }
        });

        String html = "<!doctype html><html><head><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><style>html,body{margin:0;width:100%;height:100%;overflow:hidden;background:transparent}svg{display:block;width:100%!important;height:100%!important;max-width:100%;max-height:100%}</style></head><body>" + svg + "</body></html>";
        web.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    private static Bitmap fit(Bitmap source) {
        Bitmap out = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        float scale = Math.min((float) SIZE / source.getWidth(), (float) SIZE / source.getHeight());
        float w = source.getWidth() * scale;
        float h = source.getHeight() * scale;
        RectF dst = new RectF((SIZE - w) / 2f, (SIZE - h) / 2f, (SIZE + w) / 2f, (SIZE + h) / 2f);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(source, null, dst, paint);
        return out;
    }

    private static boolean looksText(byte[] data) {
        if (data.length == 0) return false;
        int checked = Math.min(data.length, 256);
        for (int i = 0; i < checked; i++) if (data[i] == 0) return false;
        return true;
    }

    private static byte[] readAll(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[32 * 1024];
        int n;
        while ((n = in.read(buffer)) != -1) out.write(buffer, 0, n);
        return out.toByteArray();
    }
}
