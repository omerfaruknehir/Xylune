package app.turp.icontest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class DynamicLaunchActivity extends Activity {
    static final String EXTRA_TEST_ONLY = "test_only";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.splash_bg));
        window.setNavigationBarColor(getColor(R.color.splash_bg));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(24), dp(24), dp(24), dp(24));
        root.setBackgroundColor(getColor(R.color.splash_bg));

        ImageView icon = new ImageView(this);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Bitmap bitmap = IconStore.loadBitmap(this);
        if (bitmap != null) icon.setImageBitmap(bitmap);
        else icon.setImageResource(R.drawable.default_icon);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(132), dp(132));
        root.addView(icon, iconParams);

        TextView name = new TextView(this);
        name.setText(IconStore.name(this));
        name.setTextSize(20);
        name.setTextColor(getColor(R.color.text_primary));
        name.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        name.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameParams.topMargin = dp(18);
        root.addView(name, nameParams);

        icon.setAlpha(0f);
        icon.setScaleX(.92f);
        icon.setScaleY(.92f);
        name.setAlpha(0f);
        setContentView(root);
        icon.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(220).start();
        name.animate().alpha(1f).setDuration(220).setStartDelay(70).start();

        boolean testOnly = getIntent() != null && getIntent().getBooleanExtra(EXTRA_TEST_ONLY, false);
        root.postDelayed(() -> {
            if (!testOnly) {
                startActivity(new Intent(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            }
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, testOnly ? 1100 : 650);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
