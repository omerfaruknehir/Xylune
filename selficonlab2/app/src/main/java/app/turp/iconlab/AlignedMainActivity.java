package app.turp.iconlab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AlignedMainActivity extends Activity {
    private static final int PICK = 52;
    private static final ExecutorService WORK = Executors.newSingleThreadExecutor();
    private EditText name;
    private ImageView preview;
    private TextView fileState, state;
    private Button apply;
    private Bitmap selected;
    private boolean resumeApply;

    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(ui());
        name.setText(getString(R.string.app_label).replace("\u200B", "").trim());
        try { preview.setImageDrawable(getPackageManager().getApplicationIcon(getPackageName())); } catch (Exception ignored) {}
        permissionState();
    }

    private View ui() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL); root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(24), dp(26), dp(24), dp(42));
        scroll.addView(root, new ScrollView.LayoutParams(-1, -2));
        root.addView(txt("Icon Lab", 30, true), lp());
        TextView info = txt("Choose a PNG, SVG, or standalone Android drawable XML. Apply modifies this APK's real compiled launcher resources, signs the result with the same embedded test key, then updates this same package.", 15, false);
        info.setPadding(0, dp(8), 0, dp(22)); root.addView(info, lp());
        preview = new ImageView(this); preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        GradientDrawable bg = new GradientDrawable(); bg.setColor(0xfff0e9f1); bg.setCornerRadius(dp(28)); preview.setBackground(bg);
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(dp(176), dp(176)); pp.bottomMargin = dp(20); root.addView(preview, pp);
        root.addView(txt("Launcher name", 13, true), lp());
        name = new EditText(this); name.setSingleLine(true); name.setTextSize(18); name.setHint("Icon Lab");
        LinearLayout.LayoutParams np = lp(); np.bottomMargin = dp(14); root.addView(name, np);
        Button pick = new Button(this); pick.setText("Choose PNG / SVG / Android XML"); pick.setAllCaps(false); pick.setOnClickListener(v -> pick()); root.addView(pick, lp());
        fileState = txt("No new icon selected — you can still change only the launcher name.", 13, false); fileState.setPadding(0, dp(8), 0, dp(18)); root.addView(fileState, lp());
        apply = new Button(this); apply.setText("Apply to this app"); apply.setAllCaps(false); apply.setTextSize(17); apply.setOnClickListener(v -> apply());
        LinearLayout.LayoutParams ap = lp(); ap.height = dp(56); root.addView(apply, ap);
        state = txt("", 13, false); state.setPadding(0, dp(12), 0, 0); root.addView(state, lp());
        TextView note = txt("The first update needs the one-time ‘Install unknown apps’ permission. Android 12+ is asked to perform later self-updates without another confirmation, although device policy may still require one.", 12, false);
        note.setPadding(0, dp(24), 0, 0); root.addView(note, lp());
        return scroll;
    }

    private void pick() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("*/*");
        i.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/png", "image/svg+xml", "text/xml", "application/xml"});
        startActivityForResult(i, PICK);
    }

    @Override protected void onActivityResult(int req, int result, Intent data) {
        super.onActivityResult(req, result, data);
        if (req != PICK || result != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData(); fileState.setText("Rendering selected file…");
        IconRenderer.render(this, uri, new IconRenderer.Callback() {
            @Override public void onReady(Bitmap bitmap) { if (selected != null && selected != bitmap) selected.recycle(); selected = bitmap; preview.setImageBitmap(bitmap); fileState.setText("Icon ready — this will become the packaged adaptive foreground."); }
            @Override public void onError(Throwable error) { fileState.setText("Could not render: " + useful(error)); }
        });
    }

    private void apply() {
        String label = name.getText().toString().trim();
        if (label.isEmpty()) { name.setError("Enter a launcher name"); return; }
        if (Build.VERSION.SDK_INT >= 26 && !getPackageManager().canRequestPackageInstalls()) {
            resumeApply = true; state.setText("Allow ‘Install unknown apps’ for Icon Lab, then return.");
            startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()))); return;
        }
        build(label);
    }

    private void build(String label) {
        apply.setEnabled(false); state.setText("Patching, aligning, and signing self-update…"); Bitmap icon = selected;
        WORK.execute(() -> {
            try {
                File apk = AlignedSelfApkBuilder.build(this, label, icon);
                SelfUpdater.install(this, apk);
                runOnUiThread(() -> state.setText("Update submitted. Android is replacing this package now."));
            } catch (Throwable t) {
                runOnUiThread(() -> { apply.setEnabled(true); state.setText("Apply failed: " + useful(t)); });
            }
        });
    }

    @Override protected void onResume() {
        super.onResume(); if (state != null) permissionState();
        if (resumeApply && Build.VERSION.SDK_INT >= 26 && getPackageManager().canRequestPackageInstalls()) { resumeApply = false; String label = name.getText().toString().trim(); if (!label.isEmpty()) build(label); }
    }

    private void permissionState() {
        if (state == null) return;
        if (Build.VERSION.SDK_INT >= 26 && !getPackageManager().canRequestPackageInstalls()) state.setText("Self-install permission: not granted yet");
        else if (!state.getText().toString().startsWith("Patching") && !state.getText().toString().startsWith("Update")) state.setText("Self-install permission: ready");
    }

    private TextView txt(String s, int sp, boolean bold) { TextView v = new TextView(this); v.setText(s); v.setTextSize(sp); v.setTextColor(0xff2b222b); if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD); return v; }
    private LinearLayout.LayoutParams lp() { return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); }
    private int dp(int n) { return Math.round(n * getResources().getDisplayMetrics().density); }
    private static String useful(Throwable t) { Throwable r=t; while (r.getCause()!=null && r.getCause()!=r) r=r.getCause(); return r.getMessage()==null ? r.getClass().getSimpleName() : r.getMessage(); }
}
