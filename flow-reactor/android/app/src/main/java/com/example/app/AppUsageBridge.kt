package com.example.app

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class AppUsageBridge(private val context: Context, private val webView: WebView) {

    @JavascriptInterface
    fun getUsageStats() {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000 * 60 * 60 * 24 // last 24 hrs

        val usageStatsList: List<UsageStats> =
            usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            )

        val jsonArray = JSONArray()

        for (usageStats in usageStatsList) {
            val json = JSONObject()
            json.put("packageName", usageStats.packageName)
            json.put("totalTimeForeground", usageStats.totalTimeInForeground)
            jsonArray.put(json)
        }

        val jsCode = "window.receiveUsageData(${jsonArray.toString()});"

        webView.post {
            webView.evaluateJavascript(jsCode, null)
        }
    }
}
