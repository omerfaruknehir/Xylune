package app.turp.iconlab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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

public final class MainActivity extends Activity {
    private static final int PICK_ICON = 41;
    private static final ExecutorService WORKER = Executors.newSingleThreadExecutor();

    private EditText nameField;
    private ImageView preview;
    private TextView fileStatus;
    private TextView installStatus;
    private Button applyButton;
    private Bitmap selectedBitmap;
    private boolean waitingForUnknownSources;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(buildUi());
        nameField.setText(visibleLabel(getString(R.string.app_label)));
        try {
            preview.setImageDrawable(getPackageManager().getApplicationIcon(getPackageName()));
        } catch (Exception ignored) {}
        refreshPermissionStatus();
    }

    private View buildUi() {
        int pad = dp(24);
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, dp(24), pad, dp(40));
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        scroll.addView(root, new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView title = text("Icon Lab", 30, true);
        root.addView(title, matchWrap());
        TextView intro = text("Import a PNG, SVG, or standalone Android drawable XML. Apply rebuilds this installed APK, re-signs it with its embedded test key, and installs it over itself so the launcher name, launcher icon, and Android system splash all use the real packaged resources.", 15, false);
        intro.setPadding(0, dp(8), 0, dp(22));
        root.addView(intro, matchWrap());

        preview = new ImageView(this);
        preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        GradientDrawable previewBg = new GradientDrawable();
        previewBg.setColor(0xFFF0E9F1);
        previewBg.setCornerRadius(dp(28));
        preview.setBackground(previewBg);
        LinearLayout.LayoutParams previewLp = new LinearLayout.LayoutParams(dp(176), dp(176));
        previewLp.bottomMargin = dp(22);
        root.addView(preview, previewLp);

        TextView nameLabel = text("Launcher name", 13, true);
        root.addView(nameLabel, matchWrap());
        nameField = new EditText(this);
        nameField.setSingleLine(true);
        nameField.setTextSize(18);
        nameField.setHint("Icon Lab");
        LinearLayout.LayoutParams fieldLp = matchWrap();
        fieldLp.bottomMargin = dp(16);
        root.addView(nameField, fieldLp);

        Button choose = new Button(this);
        choose.setText("Choose PNG / SVG / Android XML");
        choose.setAllCaps(false);
        choose.setOnClickListener(v -> chooseIcon());
        root.addView(choose, matchWrap());

        fileStatus = text("No new icon selected — Apply can still change only the launcher name.", 13, false);
        fileStatus.setPadding(0, dp(8), 0, dp(20));
        root.addView(fileStatus, matchWrap());

        applyButton = new Button(this);
        applyButton.setText("Apply to this app");
        applyButton.setAllCaps(false);
        applyButton.setTextSize(17);
        applyButton.setOnClickListener(v -> apply());
        LinearLayout.LayoutParams applyLp = matchWrap();
        applyLp.height = dp(56);
        root.addView(applyButton, applyLp);

        installStatus = text("", 13, false);
        installStatus.setPadding(0, dp(12), 0, 0);
        root.addView(installStatus, matchWrap());

        TextView note = text("The first self-update needs Android's one-time ‘Install unknown apps’ permission for Icon Lab. Later updates request the no-user-action PackageInstaller path; Android/OEM policy can still force a confirmation in exceptional cases.", 12, false);
        note.setPadding(0, dp(24), 0, 0);
        root.addView(note, matchWrap());
        return scroll;
    }

    private void chooseIcon() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/png", "image/svg+xml", "text/xml", "application/xml"});
        startActivityForResult(intent, PICK_ICON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != PICK_ICON || resultCode != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        try {
            getContentResolver().takePersistableUriPermission(uri, data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
        } catch (Exception ignored) {}
        fileStatus.setText("Rendering selected file…");
        IconRenderer.render(this, uri, new IconRenderer.Callback() {
            @Override public void onReady(Bitmap bitmap) {
                if (selectedBitmap != null && selectedBitmap != bitmap) selectedBitmap.recycle();
                selectedBitmap = bitmap;
                preview.setImageBitmap(bitmap);
                fileStatus.setText("Icon ready. It will be packaged as the real adaptive-icon foreground.");
            }
            @Override public void onError(Throwable error) {
                fileStatus.setText("Could not render file: " + useful(error));
            }
        });
    }

    private void apply() {
        String name = nameField.getText().toString().trim();
        if (name.isEmpty()) {
            nameField.setError("Enter a launcher name");
            return;
        }
        if (Build.VERSION.SDK_INT >= 26 && !getPackageManager().canRequestPackageInstalls()) {
            waitingForUnknownSources = true;
            installStatus.setText("Allow ‘Install unknown apps’ for Icon Lab, then return here.");
            Intent settings = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
            startActivity(settings);
            return;
        }
        buildAndInstall(name);
    }

    private void buildAndInstall(String name) {
        applyButton.setEnabled(false);
        installStatus.setText("Patching installed APK and signing update…");
        Bitmap icon = selectedBitmap;
        WORKER.execute(() -> {
            try {
                File apk = SelfApkBuilder.build(this, name, icon);
                SelfUpdater.install(this, apk);
                runOnUiThread(() -> installStatus.setText("Update submitted. The app may briefly disappear while Android replaces it."));
            } catch (Throwable t) {
                runOnUiThread(() -> {
                    applyButton.setEnabled(true);
                    installStatus.setText("Apply failed: " + useful(t));
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (installStatus != null) refreshPermissionStatus();
        if (waitingForUnknownSources && Build.VERSION.SDK_INT >= 26 && getPackageManager().canRequestPackageInstalls()) {
            waitingForUnknownSources = false;
            String name = nameField.getText().toString().trim();
            if (!name.isEmpty()) buildAndInstall(name);
        }
    }

    private void refreshPermissionStatus() {
        if (installStatus == null) return;
        if (Build.VERSION.SDK_INT >= 26 && !getPackageManager().canRequestPackageInstalls()) {
            installStatus.setText("Self-install permission: not granted yet");
        } else if (!installStatus.getText().toString().startsWith("Patching") && !installStatus.getText().toString().startsWith("Update")) {
            installStatus.setText("Self-install permission: ready");
        }
    }

    private TextView text(String value, int sp, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(sp);
        view.setTextColor(0xFF2B222B);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private static String visibleLabel(String label) {
        return label == null ? "" : label.replace("\u200B", "").trim();
    }

    private static String useful(Throwable t) {
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) root = root.getCause();
        String message = root.getMessage();
        return message == null || message.trim().isEmpty() ? root.getClass().getSimpleName() : message;
    }
}
