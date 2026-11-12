package com.rekaciptaselera.generatorpo;

import android.app.Activity;
import android.os.Bundle;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;

public class MainActivity extends Activity {
    private static final String PREFS_NAME = "generator_po_webapp";
    private static final String PREF_URL = "web_app_url";
    private static final int NAVY = Color.rgb(20, 43, 69);
    private static final int GOLD = Color.rgb(251, 188, 4);
    private EditText urlInput;
    private TextView status;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        createUi();
    }

    private int dp(int px) {
        return (int) (getResources().getDisplayMetrics().density * px + 0.5f);
    }

    private GradientDrawable shape(int color, int cornerDp) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(dp(cornerDp));
        return bg;
    }

    private void createUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(247, 249, 252));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(22), dp(28), dp(22), dp(22));
        scroll.addView(content);

        TextView title = new TextView(this);
        title.setText("Generator PO");
        title.setTextColor(NAVY);
        title.setTextSize(28);
        title.setTypeface(null, Typeface.BOLD);
        content.addView(title);

        TextView small = new TextView(this);
        small.setText("PT Reka Cipta Selera • Google Apps Script Web App");
        small.setTextSize(13);
        small.setTextColor(Color.rgb(90, 111, 128));
        content.addView(small);

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(16), dp(16), dp(16), dp(16));
        panel.setBackground(shape(Color.WHITE, 14));
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pp.topMargin = dp(26);
        content.addView(panel, pp);

        TextView label = new TextView(this);
        label.setText("URL Web App Google Apps Script");
        label.setTextColor(NAVY);
        label.setTypeface(null, Typeface.BOLD);
        label.setTextSize(15);
        panel.addView(label);

        TextView guide = new TextView(this);
        guide.setText("Tempel URL deployment yang berakhiran /exec. Aplikasi memakai login Google yang sudah aktif di Chrome.");
        guide.setTextColor(Color.rgb(95, 113, 128));
        guide.setTextSize(12);
        LinearLayout.LayoutParams gp = new LinearLayout.LayoutParams(-1, -2);
        gp.topMargin = dp(8);
        panel.addView(guide, gp);

        urlInput = new EditText(this);
        urlInput.setTextSize(12);
        urlInput.setSingleLine(false);
        urlInput.setMinLines(2);
        urlInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_URI | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        urlInput.setHint("https://script.google.com/macros/s/…/exec");
        urlInput.setBackground(shape(Color.rgb(239, 244, 249), 8));
        urlInput.setPadding(dp(10), dp(10), dp(10), dp(10));
        urlInput.setText(getPreferences().getString(PREF_URL, ""));
        LinearLayout.LayoutParams up = new LinearLayout.LayoutParams(-1, -2);
        up.topMargin = dp(10);
        panel.addView(urlInput, up);

        Button open = button("Buka Generator PO", NAVY, Color.WHITE);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, dp(50));
        bp.topMargin = dp(14);
        panel.addView(open, bp);
        open.setOnClickListener(v -> launchWebApp());

        Button save = button("Simpan URL", Color.rgb(233, 240, 248), NAVY);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, dp(48));
        sp.topMargin = dp(8);
        panel.addView(save, sp);
        save.setOnClickListener(v -> saveUrl());

        status = new TextView(this);
        status.setTextSize(12);
        status.setTextColor(Color.rgb(88, 105, 120));
        status.setText("Versi Android Beta • Pembaruan logika dilakukan di Apps Script, bukan APK.");
        LinearLayout.LayoutParams st = new LinearLayout.LayoutParams(-1, -2);
        st.topMargin = dp(18);
        content.addView(status, st);

        TextView footer = new TextView(this);
        footer.setText("Internet diperlukan. Backup JSON, login Google, dan cetak PO berjalan di browser yang digunakan.");
        footer.setTextColor(Color.rgb(93, 109, 124));
        footer.setTextSize(12);
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(-1, -2);
        fp.topMargin = dp(18);
        content.addView(footer, fp);

        setContentView(scroll);
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private Button button(String text, int color, int textColor) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(14);
        b.setAllCaps(false);
        b.setTypeface(null, Typeface.BOLD);
        b.setTextColor(textColor);
        b.setBackground(shape(color, 8));
        return b;
    }

    private String checkUrl() {
        String url = urlInput.getText().toString().trim();
        // URL dibatasi pada deployment resmi Google Apps Script; tidak menerima sembarang situs.
        if (!url.matches("https://script\\.google\\.com/macros/s/[^\\s/?#]+/exec(\\?[^\\s]*)?")) {
            status.setText("URL tidak valid. Salin URL Web app yang berakhiran /exec dari Apps Script > Manage deployments.");
            Toast.makeText(this, "URL harus berupa Apps Script /exec", Toast.LENGTH_LONG).show();
            return null;
        }
        return url;
    }

    private void saveUrl() {
        String url = checkUrl();
        if (url == null) return;
        getPreferences().edit().putString(PREF_URL, url).apply();
        status.setText("URL disimpan di perangkat ini. Klik Buka Generator PO.");
        Toast.makeText(this, "URL Generator PO tersimpan", Toast.LENGTH_SHORT).show();
    }

    private void launchWebApp() {
        String url = checkUrl();
        if (url == null) return;
        getPreferences().edit().putString(PREF_URL, url).apply();
        Uri uri = Uri.parse(url);
        try {
            CustomTabColorSchemeParams colors = new CustomTabColorSchemeParams.Builder()
                .setToolbarColor(NAVY)
                .setNavigationBarColor(NAVY)
                .build();
            CustomTabsIntent intent = new CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colors)
                .setShowTitle(true)
                .setUrlBarHidingEnabled(true)
                .build();
            intent.launchUrl(this, uri);
            status.setText("Generator PO dibuka lewat browser. Gunakan akun Google yang telah diberi izin akses.");
        } catch (ActivityNotFoundException ex) {
            Intent fallback = new Intent(Intent.ACTION_VIEW, uri);
            try { startActivity(fallback); }
            catch (Exception ignored) {
                status.setText("Tidak ada browser untuk membuka Generator PO. Pasang atau aktifkan Chrome.");
            }
        } catch (Exception ex) {
            status.setText("Gagal membuka Web App: " + ex.getMessage());
        }
    }
}
