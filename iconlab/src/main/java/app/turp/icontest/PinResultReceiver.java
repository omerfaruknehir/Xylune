package app.turp.icontest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public final class PinResultReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean hideStock = intent != null && intent.getBooleanExtra(LauncherUtil.EXTRA_HIDE_STOCK, false);
        IconStore.prefs(context).edit().putBoolean(IconStore.KEY_PINNED, true).apply();
        if (hideStock) LauncherUtil.setStockLauncherEnabled(context, false);
    }
}
