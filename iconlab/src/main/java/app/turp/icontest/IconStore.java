package app.turp.icontest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

final class IconStore {
    static final String SHORTCUT_ID = "custom_launcher";
    static final String PREFS = "icon_lab";
    static final String KEY_NAME = "launcher_name";
    static final String KEY_ADAPTIVE = "adaptive_icon";
    static final String KEY_HIDE_STOCK = "hide_stock_after_pin";
    static final String KEY_HAS_ICON = "has_icon";
    static final String KEY_PINNED = "pinned_once";
    static final String ICON_FILE = "launcher_icon.png";

    private IconStore() {}

    static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    static String name(Context context) {
        String value = prefs(context).getString(KEY_NAME, "Icon Lab");
        return value == null || value.trim().isEmpty() ? "Icon Lab" : value.trim();
    }

    static boolean adaptive(Context context) {
        return prefs(context).getBoolean(KEY_ADAPTIVE, true);
    }

    static boolean hideStock(Context context) {
        return prefs(context).getBoolean(KEY_HIDE_STOCK, true);
    }

    static boolean hasCustomIcon(Context context) {
        return prefs(context).getBoolean(KEY_HAS_ICON, false) && iconFile(context).isFile();
    }

    static File iconFile(Context context) {
        return new File(context.getFilesDir(), ICON_FILE);
    }

    static Bitmap loadBitmap(Context context) {
        if (!hasCustomIcon(context)) return null;
        return BitmapFactory.decodeFile(iconFile(context).getAbsolutePath());
    }

    static void save(Context context, Bitmap bitmap, String name, boolean adaptive, boolean hideStock) throws IOException {
        File target = iconFile(context);
        File temp = new File(context.getFilesDir(), ICON_FILE + ".tmp");
        try (FileOutputStream output = new FileOutputStream(temp)) {
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                throw new IOException("Android could not encode the icon as PNG.");
            }
            output.flush();
        }
        if (target.exists() && !target.delete()) {
            temp.delete();
            throw new IOException("Could not replace the stored icon.");
        }
        if (!temp.renameTo(target)) {
            temp.delete();
            throw new IOException("Could not store the imported icon.");
        }
        prefs(context).edit()
                .putString(KEY_NAME, name.trim())
                .putBoolean(KEY_ADAPTIVE, adaptive)
                .putBoolean(KEY_HIDE_STOCK, hideStock)
                .putBoolean(KEY_HAS_ICON, true)
                .apply();
    }

    static void saveOptions(Context context, String name, boolean adaptive, boolean hideStock) {
        prefs(context).edit()
                .putString(KEY_NAME, name.trim())
                .putBoolean(KEY_ADAPTIVE, adaptive)
                .putBoolean(KEY_HIDE_STOCK, hideStock)
                .apply();
    }
}
