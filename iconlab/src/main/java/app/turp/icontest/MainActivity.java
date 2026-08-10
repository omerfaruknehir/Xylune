package app.turp.icontest;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public final class MainActivity extends Activity {
    private static final int PICK_ICON = 1001;

    private ImageView previewIcon;
    private TextView previewName;
    private TextView fileInfo;
    private TextView status;
    private EditText nameInput;
    private CheckBox adaptiveCheck;
    private CheckBox hideStockCheck;
    private Bitmap currentBitmap;
    private String currentType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.page_bg));
        window.setNavigationBarColor(getColor(R.color.page_bg));
        buildUi();
        loadState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (status != null) updateLauncherStatus();
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(getColor(R.color.page_bg));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(24), dp(20), dp(32));
        scroll.addView(root, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView title = text("Icon Lab", 30, getColor(R.color.text_primary));
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(title);

        TextView subtitle = text(
                "Import an Android drawable XML, SVG, or PNG, then test it as a home-screen launcher icon and launch splash.",
                15, getColor(R.color.text_secondary));
        subtitle.setPadding(0, dp(8), 0, dp(20));
        root.addView(subtitle);

        LinearLayout previewCard = new LinearLayout(this);
        previewCard.setGravity(Gravity.CENTER_VERTICAL);
        previewCard.setPadding(dp(18), dp(18), dp(18), dp(18));
        previewCard.setBackground(roundRect(getColor(R.color.surface), 22, getColor(R.color.outline), 1));
        root.addView(previewCard, lpMatchWrap(0, 16));

        FrameLayout iconSurface = new FrameLayout(this);
        iconSurface.setBackground(roundRect(getColor(R.color.surface_alt), 25, Color.TRANSPARENT, 0));
        previewCard.addView(iconSurface, new LinearLayout.LayoutParams(dp(96), dp(96)));
        previewIcon = new ImageView(this);
        previewIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        int iconPad = dp(10);
        previewIcon.setPadding(iconPad, iconPad, iconPad, iconPad);
        iconSurface.addView(previewIcon, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout previewText = new LinearLayout(this);
        previewText.setOrientation(LinearLayout.VERTICAL);
        previewText.setPadding(dp(16), 0, 0, 0);
        previewCard.addView(previewText, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView previewLabel = text("LAUNCHER PREVIEW", 12, getColor(R.color.text_secondary));
        previewLabel.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        previewText.addView(previewLabel);
        previewName = text("Icon Lab", 20, getColor(R.color.text_primary));
        previewName.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        previewName.setPadding(0, dp(7), 0, 0);
        previewText.addView(previewName);

        addSectionLabel(root, "Launcher name");
        nameInput = new EditText(this);
        nameInput.setTextSize(16);
        nameInput.setSingleLine(true);
        nameInput.setTextColor(getColor(R.color.text_primary));
        nameInput.setHintTextColor(getColor(R.color.text_secondary));
        nameInput.setHint("e.g. Turp");
        nameInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        nameInput.setPadding(dp(14), 0, dp(14), 0);
        nameInput.setBackground(roundRect(getColor(R.color.surface), 14, getColor(R.color.outline), 1));
        root.addView(nameInput, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(54)));
        nameInput.setOnFocusChangeListener((v, focused) -> refreshPreviewName());
        nameInput.addTextChangedListener(new SimpleTextWatcher(this::refreshPreviewName));

        addSectionLabel(root, "Artwork");
        Button choose = secondaryButton("Choose XML / SVG / PNG");
        choose.setOnClickListener(v -> chooseFile());
        root.addView(choose, lpMatchWrap(0, 8));
        fileInfo = text("No custom icon imported yet", 13, getColor(R.color.text_secondary));
        fileInfo.setPadding(dp(2), 0, dp(2), 0);
        root.addView(fileInfo);

        adaptiveCheck = checkBox("Use adaptive launcher mask");
        adaptiveCheck.setPadding(0, dp(18), 0, 0);
        root.addView(adaptiveCheck);

        hideStockCheck = checkBox("Replace stock entry after pinning");
        root.addView(hideStockCheck);

        TextView caution = text(
                "Replacement hides Icon Lab’s built-in launcher entry after the custom shortcut is pinned. Use Restore stock entry before deleting the custom shortcut if you still want a normal launcher entry.",
                12, getColor(R.color.text_secondary));
        caution.setPadding(dp(2), dp(2), dp(2), dp(18));
        root.addView(caution);

        Button apply = primaryButton("Apply / update launcher icon");
        apply.setOnClickListener(v -> applyLauncher());
        root.addView(apply, lpMatchWrap(0, 10));

        Button testSplash = secondaryButton("Test splash screen");
        testSplash.setOnClickListener(v -> testSplash());
        root.addView(testSplash, lpMatchWrap(0, 10));

        Button restore = secondaryButton("Restore stock launcher entry");
        restore.setOnClickListener(v -> {
            LauncherUtil.setStockLauncherEnabled(this, true);
            hideStockCheck.setChecked(false);
            IconStore.saveOptions(this, cleanName(), adaptiveCheck.isChecked(), false);
            updateLauncherStatus();
            Toast.makeText(this, "Stock Icon Lab launcher entry restored.", Toast.LENGTH_SHORT).show();
        });
        root.addView(restore, lpMatchWrap(0, 18));

        status = text("", 13, getColor(R.color.text_secondary));
        status.setPadding(dp(14), dp(12), dp(14), dp(12));
        status.setBackground(roundRect(getColor(R.color.surface_alt), 14, Color.TRANSPARENT, 0));
        root.addView(status);

        TextView limitation = text(
                "Android does not let an installed app rewrite arbitrary manifest icon/name resources at runtime. Icon Lab therefore uses Android’s pinned-shortcut API for the custom launcher entry. The launch splash immediately uses your imported artwork and name.",
                12, getColor(R.color.text_secondary));
        limitation.setPadding(dp(2), dp(16), dp(2), 0);
        root.addView(limitation);

        setContentView(scroll);
    }

    private void loadState() {
        nameInput.setText(IconStore.name(this));
        adaptiveCheck.setChecked(IconStore.adaptive(this));
        hideStockCheck.setChecked(IconStore.hideStock(this));
        Bitmap stored = IconStore.loadBitmap(this);
        if (stored != null) {
            currentBitmap = stored;
            currentType = "Stored PNG rendering";
            previewIcon.setImageBitmap(stored);
            fileInfo.setText("Stored custom icon • ready");
        } else {
            previewIcon.setImageResource(R.drawable.default_icon);
        }
        refreshPreviewName();
        updateLauncherStatus();
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/png", "image/svg+xml", "text/xml", "application/xml"});
        startActivityForResult(intent, PICK_ICON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != PICK_ICON || resultCode != RESULT_OK || data == null || data.getData() == null) return;
        Uri uri = data.getData();
        String displayName = displayName(uri);
        fileInfo.setText("Rendering " + displayName + " …");
        FileRenderer.render(this, uri, displayName, new FileRenderer.Callback() {
            @Override
            public void onSuccess(Bitmap bitmap, String kind) {
                currentBitmap = bitmap;
                currentType = kind;
                previewIcon.setImageBitmap(bitmap);
                fileInfo.setText(displayName + " • " + kind + " • rendered");
                try {
                    persistCurrent();
                    updateLauncherStatus();
                } catch (IOException e) {
                    showError(e.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                fileInfo.setText(displayName + " • failed");
                showError(message);
            }
        });
    }

    private void applyLauncher() {
        if (!validateReady()) return;
        try {
            persistCurrent();
        } catch (IOException e) {
            showError(e.getMessage());
            return;
        }

        LauncherUtil.ApplyResult result = LauncherUtil.apply(
                this,
                currentBitmap,
                cleanName(),
                adaptiveCheck.isChecked(),
                hideStockCheck.isChecked()
        );
        switch (result) {
            case PIN_REQUESTED:
                status.setText("Confirm the launcher prompt. After it is pinned, the stock entry will be hidden if replacement is enabled.");
                break;
            case UPDATED:
                status.setText("Pinned launcher entry updated. Some launchers may briefly cache the old artwork.");
                break;
            case UNSUPPORTED:
                status.setText("This launcher does not expose Android’s pinned-shortcut flow. Try a launcher that supports pinned shortcuts.");
                break;
        }
        updateLauncherStatusDelayed();
    }

    private void testSplash() {
        if (!validateReady()) return;
        try {
            persistCurrent();
        } catch (IOException e) {
            showError(e.getMessage());
            return;
        }
        Intent intent = new Intent(this, DynamicLaunchActivity.class)
                .putExtra(DynamicLaunchActivity.EXTRA_TEST_ONLY, true);
        startActivity(intent);
    }

    private boolean validateReady() {
        if (cleanName().isEmpty()) {
            showError("Enter a launcher name.");
            nameInput.requestFocus();
            return false;
        }
        if (currentBitmap == null) {
            showError("Choose an XML, SVG, or PNG icon first.");
            return false;
        }
        return true;
    }

    private void persistCurrent() throws IOException {
        if (currentBitmap != null) {
            IconStore.save(this, currentBitmap, cleanName(), adaptiveCheck.isChecked(), hideStockCheck.isChecked());
        } else {
            IconStore.saveOptions(this, cleanName(), adaptiveCheck.isChecked(), hideStockCheck.isChecked());
        }
    }

    private String cleanName() {
        return nameInput.getText() == null ? "" : nameInput.getText().toString().trim();
    }

    private void refreshPreviewName() {
        if (previewName == null || nameInput == null) return;
        String name = cleanName();
        previewName.setText(name.isEmpty() ? "Icon Lab" : name);
    }

    private void updateLauncherStatusDelayed() {
        status.postDelayed(this::updateLauncherStatus, 800);
    }

    private void updateLauncherStatus() {
        boolean pinned = LauncherUtil.isPinned(this);
        boolean stock = LauncherUtil.stockLauncherEnabled(this);
        String imported = currentBitmap == null ? "no imported icon" : (currentType.isEmpty() ? "custom icon ready" : currentType + " ready");
        status.setText("Custom pinned entry: " + (pinned ? "yes" : "not detected")
                + "\nStock launcher entry: " + (stock ? "visible" : "hidden")
                + "\nArtwork: " + imported);
    }

    private String displayName(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    String value = cursor.getString(index);
                    if (value != null && !value.trim().isEmpty()) return value;
                }
            }
        } catch (Exception ignored) {}
        String last = uri.getLastPathSegment();
        return last == null ? "selected file" : last;
    }

    private void showError(String message) {
        Toast.makeText(this, message == null ? "Unknown error" : message, Toast.LENGTH_LONG).show();
    }

    private void addSectionLabel(LinearLayout root, String label) {
        TextView view = text(label, 13, getColor(R.color.text_secondary));
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setPadding(dp(2), dp(20), 0, dp(8));
        root.addView(view);
    }

    private TextView text(String value, int sp, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(sp);
        view.setTextColor(color);
        view.setLineSpacing(0, 1.12f);
        return view;
    }

    private CheckBox checkBox(String label) {
        CheckBox check = new CheckBox(this);
        check.setText(label);
        check.setTextSize(14);
        check.setTextColor(getColor(R.color.text_primary));
        return check;
    }

    private Button primaryButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextColor(getColor(R.color.accent_on));
        button.setTextSize(14);
        button.setAllCaps(false);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setMinHeight(dp(54));
        button.setBackground(roundRect(getColor(R.color.accent), 16, Color.TRANSPARENT, 0));
        return button;
    }

    private Button secondaryButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextColor(getColor(R.color.text_primary));
        button.setTextSize(14);
        button.setAllCaps(false);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setMinHeight(dp(52));
        button.setBackground(roundRect(getColor(R.color.surface), 16, getColor(R.color.outline), 1));
        return button;
    }

    private GradientDrawable roundRect(int fill, int radiusDp, int strokeColor, int strokeDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(dp(radiusDp));
        if (strokeDp > 0) drawable.setStroke(dp(strokeDp), strokeColor);
        return drawable;
    }

    private LinearLayout.LayoutParams lpMatchWrap(int top, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(top);
        params.bottomMargin = dp(bottom);
        return params;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private static final class SimpleTextWatcher implements android.text.TextWatcher {
        private final Runnable runnable;
        SimpleTextWatcher(Runnable runnable) { this.runnable = runnable; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { runnable.run(); }
        @Override public void afterTextChanged(android.text.Editable s) {}
    }
}
