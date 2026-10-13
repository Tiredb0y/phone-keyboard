package com.phonekeyboard.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.WindowManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * یک WebView ساده که صفحهٔ کیبورد کامپیوتر را (از طریق adb reverse یا tethering) باز می‌کند.
 * اگر کامپیوتر هنوز آماده نباشد هر ۲ ثانیه خودکار دوباره تلاش می‌کند.
 */
class MainActivity : Activity() {

    private lateinit var web: WebView
    private val handler = Handler(Looper.getMainLooper())
    private var url: String = DEFAULT_URL

    private val retry = Runnable { web.loadUrl(url) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        web = WebView(this)
        setContentView(web)

        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true      // توکن در localStorage می‌ماند
            cacheMode = WebSettings.LOAD_NO_CACHE
            mediaPlaybackRequiresUserGesture = false
        }
        web.setBackgroundColor(0xFF10131A.toInt())

        web.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView, request: WebResourceRequest, error: WebResourceError
            ) {
                if (request.isForMainFrame) showWaiting()
            }

            @Deprecated("older API levels")
            override fun onReceivedError(
                view: WebView, errorCode: Int, description: String?, failingUrl: String?
            ) {
                if (failingUrl == url) showWaiting()
            }
        }

        url = resolveUrl(intent)
        web.loadUrl(url)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        url = resolveUrl(intent)
        handler.removeCallbacks(retry)
        web.loadUrl(url)
    }

    private fun resolveUrl(i: Intent?): String {
        val prefs = getSharedPreferences("pk", Context.MODE_PRIVATE)
        val data: Uri? = i?.data
        if (data != null && (data.scheme == "http" || data.scheme == "https")) {
            val u = data.toString()
            prefs.edit().putString("url", u).apply()
            return u
        }
        return prefs.getString("url", DEFAULT_URL) ?: DEFAULT_URL
    }

    private fun showWaiting() {
        val html = """
            <html><body style="background:#10131a;color:#8d97ad;font-family:sans-serif;
            display:flex;align-items:center;justify-content:center;height:100vh;margin:0;
            text-align:center"><div>
            <div style="font-size:20px;color:#e8ecf4">Waiting for PC…</div>
            <p>python phone_keyboard.py</p></div></body></html>
        """.trimIndent()
        web.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
        handler.removeCallbacks(retry)
        handler.postDelayed(retry, 2000)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true)      // با Back اپ بسته نشود تا اتصال بماند
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        handler.removeCallbacks(retry)
        web.destroy()
        super.onDestroy()
    }

    companion object {
        const val DEFAULT_URL = "http://localhost:8765/"
    }
}
