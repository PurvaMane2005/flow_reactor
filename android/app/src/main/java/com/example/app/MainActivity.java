package com.example.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.JavascriptInterface;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


public class MainActivity extends AppCompatActivity {

    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Make sure activity_main.xml exists with a WebView

        webView = findViewById(R.id.webview);  // WebView should have id="webview"

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());  // Ensures links open in WebView

        // Inject the Java interface into JS
        webView.addJavascriptInterface(new AndroidUsage(this), "AndroidUsage");

        // Load your local HTML file
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.loadUrl("file:///android_asset/index.html");

        webView.setWebContentsDebuggingEnabled(true);

    }

    public class AndroidUsage {
        Context context;

        AndroidUsage(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public String getScreenTime() {
            if (!hasUsageAccess()) {
                openUsageAccessSettings();
                return "Permission required";
            }

            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 3600 * 24, time);

            if (stats == null || stats.isEmpty()) {
                return "No usage data available.";
            }

            long totalTime = 0;
            for (UsageStats usageStats : stats) {
                totalTime += usageStats.getTotalTimeInForeground();
            }

            long totalSeconds = totalTime / 1000;
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;

            return "Total screen time (last 24h): " + hours + "h " + minutes + "m";
        }

        private boolean hasUsageAccess() {
            try {
                UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                long time = System.currentTimeMillis();
                List<UsageStats> stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 3600, time);
                return stats != null && !stats.isEmpty();
            } catch (Exception e) {
                return false;
            }
        }

        private void openUsageAccessSettings() {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
