package app.turp.icontest;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Icon;

import java.util.Collections;

final class LauncherUtil {
    static final String EXTRA_HIDE_STOCK = "hide_stock";

    enum ApplyResult {
        PIN_REQUESTED,
        UPDATED,
        UNSUPPORTED
    }

    private LauncherUtil() {}

    static ApplyResult apply(Context context, Bitmap source, String rawName, boolean adaptive, boolean hideStock) {
        ShortcutManager manager = context.getSystemService(ShortcutManager.class);
        if (manager == null) return ApplyResult.UNSUPPORTED;

        String name = rawName == null ? "Icon Lab" : rawName.trim();
        if (name.isEmpty()) name = "Icon Lab";
        if (name.length() > 40) name = name.substring(0, 40);

        Bitmap bitmap = squareBitmap(source, 512);
        Icon icon = adaptive ? Icon.createWithAdaptiveBitmap(bitmap) : Icon.createWithBitmap(bitmap);
        Intent launchIntent = new Intent(context, DynamicLaunchActivity.class)
                .setAction(Intent.ACTION_VIEW)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        ShortcutInfo shortcut = new ShortcutInfo.Builder(context, IconStore.SHORTCUT_ID)
                .setShortLabel(name)
                .setLongLabel(name)
                .setIcon(icon)
                .setIntent(launchIntent)
                .build();

        boolean alreadyPinned = manager.getPinnedShortcuts().stream()
                .anyMatch(item -> IconStore.SHORTCUT_ID.equals(item.getId()));

        if (alreadyPinned) {
            manager.updateShortcuts(Collections.singletonList(shortcut));
            IconStore.prefs(context).edit().putBoolean(IconStore.KEY_PINNED, true).apply();
            setStockLauncherEnabled(context, !hideStock);
            return ApplyResult.UPDATED;
        }

        if (!hideStock) setStockLauncherEnabled(context, true);
        if (!manager.isRequestPinShortcutSupported()) return ApplyResult.UNSUPPORTED;

        Intent callback = new Intent(context, PinResultReceiver.class)
                .putExtra(EXTRA_HIDE_STOCK, hideStock);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                901,
                callback,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        boolean requested = manager.requestPinShortcut(shortcut, pendingIntent.getIntentSender());
        return requested ? ApplyResult.PIN_REQUESTED : ApplyResult.UNSUPPORTED;
    }

    static boolean isPinned(Context context) {
        ShortcutManager manager = context.getSystemService(ShortcutManager.class);
        if (manager == null) return false;
        return manager.getPinnedShortcuts().stream()
                .anyMatch(item -> IconStore.SHORTCUT_ID.equals(item.getId()));
    }

    static void setStockLauncherEnabled(Context context, boolean enabled) {
        ComponentName component = new ComponentName(context, "app.turp.icontest.StockLauncher");
        context.getPackageManager().setComponentEnabledSetting(
                component,
                enabled
                        ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        );
    }

    static boolean stockLauncherEnabled(Context context) {
        ComponentName component = new ComponentName(context, "app.turp.icontest.StockLauncher");
        int state = context.getPackageManager().getComponentEnabledSetting(component);
        return state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
    }

    static Bitmap squareBitmap(Bitmap source, int size) {
        if (source.getWidth() == size && source.getHeight() == size) return source;
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        float scale = Math.min((float) size / source.getWidth(), (float) size / source.getHeight());
        float width = source.getWidth() * scale;
        float height = source.getHeight() * scale;
        float left = (size - width) / 2f;
        float top = (size - height) / 2f;
        canvas.drawBitmap(source, null,
                new android.graphics.RectF(left, top, left + width, top + height), null);
        return output;
    }
}
