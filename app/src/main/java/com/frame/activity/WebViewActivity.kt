package com.frame.activity

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import com.blankj.utilcode.util.ObjectUtils
import com.frame.R
import com.frame.utils.CU
import com.frame.utils.ViewUtil
import kotlinx.android.synthetic.main.activity_webview.*
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * 对于Android调用JS代码的方法有2种：
 * 1. 通过WebView的loadUrl（）
 * 2. 通过WebView的evaluateJavascript（）
 *
 *
 * 对于JS调用Android代码的方法有3种：
 * 1. 通过WebView的addJavascriptInterface（）进行对象映射
 * 2. 通过 WebViewClient 的shouldOverrideUrlLoading ()方法回调拦截 url
 * 3. 通过 WebChromeClient 的onJsAlert()、onJsConfirm()、onJsPrompt()方法回调拦截JS对话框alert()、confirm()、prompt()
 */
/**
 * WebView, 不传title则隐藏
 */
class WebViewActivity : BaseTitleActivity() {

    private val mIsBlockNetworkImage = true // 是否先加载网页内容，后加载图片
    private var mPostData: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        initControl()
        initWebView()
        loadWebViewData()
    }

    private fun initControl() {
        val title = intent.getStringExtra("title")
        if (ObjectUtils.isEmpty(title)) {
            setTitleBarHide()
        } else {
            setTitleText(title)
        }
    }

    private fun initWebView() {
        val webSettings = webView?.settings //声明WebSettings子类

        //设置自适应屏幕，两者合用
        webSettings?.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings?.loadWithOverviewMode = true // 缩放至屏幕的大小

        //缩放操作
        webSettings?.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings?.builtInZoomControls = false //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings?.displayZoomControls = false //隐藏原生的缩放控件

        //缓存模式如下：
        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        webSettings?.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings?.allowFileAccess = true //设置可以访问文件
        webSettings?.javaScriptEnabled = true // 支持javascript
        webSettings?.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings?.loadsImagesAutomatically = true //支持自动加载图片
        webSettings?.defaultTextEncodingName = "utf-8" //设置编码格式
        webSettings?.domStorageEnabled = true //开启 DOM storage API 功能
        webSettings?.databaseEnabled = true //开启 database storage API 功能
        webSettings?.setAppCacheEnabled(true) //开启 Application Caches 功能
        val cacheDirPath = filesDir.absolutePath + "APP_CACAHE_DIRNAME"
        webSettings?.setAppCachePath(cacheDirPath) //设置  Application Caches 缓存目录
        webView?.addJavascriptInterface(WebViewJsInterface(mBActivity, webView), "handler")
        webView?.webChromeClient = CustomWebChromeClient()
        webView?.webViewClient = CustomWebViewClient()

        //调试WebView需要满足安卓系统版本为Android 4.4+已上, 并且需要APP内配置相应的代码
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
    }

    private fun loadWebViewData() {
        val type = intent.getStringExtra(TYPE)
        val url = intent.getStringExtra("url")
        if (ObjectUtils.isNotEmpty(url)) CU.synCookies(this, url)
        when (type) {
            TYPE_LOAD_URL -> webView?.loadUrl(url)
            TYPE_POST_URL -> webView?.postUrl(url, CU.getBytes(mPostData, "BASE64"))
            TYPE_LOAD_CONTENT -> {
                val data = intent.getStringExtra("data")
                webView?.loadDataWithBaseURL(null, data, "text/html", "utf-8", null)
            }
            TYPE_LOAD_FILE -> webView?.loadUrl("file:///android_asset/js_test.html")
        }
    }

    inner class WebViewJsInterface(private val context: Context, private val webView: WebView?) : Any() {
        @JavascriptInterface
        fun handleMessage(message: String?) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        @JavascriptInterface
        fun callAndroid(message: String?) {
            // 特别注意：JS代码调用一定要在 onPageFinished（）回调之后才能调用。
            val script = ViewUtil.formatScript("callJS", message)
            ViewUtil.callJavaScriptFunction(webView, script)
        }

        @JavascriptInterface
        fun setWebViewTextCallback() {
            val script = ViewUtil.formatScript("setText", "This is a text from Android which is set in the html")
            ViewUtil.callJavaScriptFunction(webView, script)
        }

        @JavascriptInterface
        fun getSource(html: String?) { //获取网页内容
        }
    }

    internal inner class CustomWebChromeClient : WebChromeClient() {
        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            val b = AlertDialog.Builder(mBActivity)
            b.setTitle("Alert")
            b.setMessage(message)
            b.setPositiveButton(android.R.string.ok) { dialog: DialogInterface?, which: Int -> result.confirm() }
            b.setCancelable(false)
            b.create().show()
            return true // false: 弹系统框
        }

        override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
            return super.onJsConfirm(view, url, message, result)
        }

        override fun onJsPrompt(view: WebView, url: String, message: String, defaultValue: String, result: JsPromptResult): Boolean {

            // 根据协议的参数，判断是否是所需要的url(原理同方式2)
            // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
            //假定传入进来的 url = "js://demo?arg1=111&arg2=222"（同时也是约定好的需要拦截的）
            val uri = Uri.parse(message)

            // 如果url的协议 = 预先约定的 js 协议, 就解析往下解析参数
            if (uri.scheme == "js") {

                // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                // 所以拦截url,下面JS开始调用Android需要的方法
                if (uri.authority == "demo") {

                    // 执行JS所需要调用的逻辑
                    println("js调用了Android的方法")

                    // 解析协议上的参数
                    val collection = uri.queryParameterNames
                    val iterator: Iterator<*> = collection.iterator()
                    while (iterator.hasNext()) {
                        val value = uri.getQueryParameter(iterator.next() as String?)
                    }

                    //参数result:代表消息框的返回值(输入值)
                    result.confirm("js调用了Android的方法成功啦")
                }
                return true
            }
            return super.onJsPrompt(view, url, message, defaultValue, result)
        }

        override fun onProgressChanged(view: WebView, newProgress: Int) { // 加载进度变化
            pbWebView?.progress = newProgress
            pbWebView?.visibility = if (newProgress == 100) View.GONE else View.VISIBLE
            super.onProgressChanged(view, newProgress)
        }

        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
        }
    }

    inner class CustomWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            view?.settings?.blockNetworkImage = mIsBlockNetworkImage // 为了加快加载速度，先阻止图片加载
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String) {
            view?.settings?.blockNetworkImage = !mIsBlockNetworkImage // 网页加载完毕，开启加载图片
            //            view.loadUrl("javascript:window.handler.getSource('<head>'+"
//                    + "document.getElementsByTagName('html')[0].innerHTML+'</head>');"); //获取网页内容时打开
            super.onPageFinished(view, url)
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            super.onReceivedError(view, errorCode, description, failingUrl)
        }

        @TargetApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            super.onReceivedError(view, request, error)
        }

        // 重载此方法使得打开网页时不调用系统浏览器，而是在本WebView中显示，拦截处理url亦在此
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return false // 返回true由应用的代码处理该url; 返回false则由WebView处理该url，即用WebView加载该url

//            // 根据协议的参数，判断是否是所需要的url
//            // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
//            //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）
//            Uri uri = Uri.parse(url);
//
//            // 如果url的协议 = 预先约定的 js 协议 就解析往下解析参数
//            if ( uri.getScheme().equals("js")) {
//
//                // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
//                // 所以拦截url,下面JS开始调用Android需要的方法
//                if (uri.getAuthority().equals("webview")) {
//                    // 执行JS所需要调用的逻辑
//                    // 如果JS想要得到Android方法的返回值，只能通过 WebView 的 loadUrl()去执行 JS 方法把返回值传递回去
//                    System.out.println("js调用了Android的方法");
//                }
//                return true;
//            }
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            handler.proceed() // 表示接受所有网站的证书
//            handler.cancel();              // 表示挂起连接，为默认方式
//            handler.handleMessage(null);   // 进行其他处理
        }

        override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
            if (ObjectUtils.isNotEmpty(mPostData)) {
                val url = intent.getStringExtra("url")
                try {
                    val mUrl = URL(url)
                    val connection = mUrl.openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.doOutput = true
                    connection.useCaches = false
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("version", "blue") // add to header
                    val os = DataOutputStream(connection.outputStream)
//    implementation 'org.apache.httpcomponents:httpcore:4.4.10'
//    os.write(EncodingUtils.getBytes(mPostData, "BASE64")); // add to body
                    os.write(CU.getBytes(mPostData, "BASE64")) // add to body
                    os.flush()
                    mPostData = null
                    return WebResourceResponse("text/html", connection.contentEncoding, connection.inputStream)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    mPostData = null
                }
            }
            return super.shouldInterceptRequest(view, request)
        }
    }

    override fun onBackPressed() {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        if (webView != null) {
            webView?.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            webView?.clearHistory()
            (webView?.parent as ViewGroup).removeView(webView)
            webView?.destroy()
        }
        super.onDestroy()
    }

    companion object {
        const val TYPE = "type"
        const val TYPE_LOAD_URL = "type_load_url" // 加载网络url(包括本地文件)
        const val TYPE_POST_URL = "type_post_url" // 加载网络url
        const val TYPE_LOAD_CONTENT = "type_load_content" // 直接加载内容
        const val TYPE_LOAD_FILE = "type_load_file" // 直接加载文件
    }
}