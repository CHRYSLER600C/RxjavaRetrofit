package com.frame.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blankj.utilcode.util.ObjectUtils;
import com.frame.R;
import com.frame.utils.CU;
import com.frame.utils.ViewUtil;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import androidx.annotation.Nullable;
import butterknife.BindView;

/**
 * 对于Android调用JS代码的方法有2种：
 * 1. 通过WebView的loadUrl（）
 * 2. 通过WebView的evaluateJavascript（）
 * <p>
 * 对于JS调用Android代码的方法有3种：
 * 1. 通过WebView的addJavascriptInterface（）进行对象映射
 * 2. 通过 WebViewClient 的shouldOverrideUrlLoading ()方法回调拦截 url
 * 3. 通过 WebChromeClient 的onJsAlert()、onJsConfirm()、onJsPrompt()方法回调拦截JS对话框alert()、confirm()、prompt()
 */

/**
 * WebView, 不传title则隐藏
 */
public class WebViewActivity extends BaseTitleActivity {

    public static final String TYPE = "type";
    public static final String TYPE_LOAD_URL = "type_load_url";                 // 加载网络url(包括本地文件)
    public static final String TYPE_POST_URL = "type_post_url";                 // 加载网络url
    public static final String TYPE_LOAD_CONTENT = "type_load_content";         // 直接加载内容
    public static final String TYPE_LOAD_FILE = "type_load_file";               // 直接加载文件

    @BindView(R.id.pbWebView)
    ProgressBar mPbWebView;
    @BindView(R.id.webView)
    WebView mWebView;

    private final boolean mIsBlockNetworkImage = true; // 是否先加载网页内容，后加载图片
    private String mPostData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        initControl();
        initWebView();
        loadWebViewData();
    }

    private void initControl() {
        String title = getIntent().getStringExtra("title");
        if (ObjectUtils.isEmpty(title)) {
            setTitleBarHide();
        } else {
            setTitleText(title);
        }
    }

    private void initWebView() {
        WebSettings webSettings = mWebView.getSettings();            //声明WebSettings子类

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true);                        //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true);                   // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true);                            //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(false);                   //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false);                   //隐藏原生的缩放控件

        //缓存模式如下：
        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webSettings.setAllowFileAccess(true);                        //设置可以访问文件
        webSettings.setJavaScriptEnabled(true);                      // 支持javascript
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);  //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true);               //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");             //设置编码格式
        webSettings.setDomStorageEnabled(true);                      //开启 DOM storage API 功能
        webSettings.setDatabaseEnabled(true);                        //开启 database storage API 功能
        webSettings.setAppCacheEnabled(true);                        //开启 Application Caches 功能
        String cacheDirPath = getFilesDir().getAbsolutePath() + "APP_CACAHE_DIRNAME";
        webSettings.setAppCachePath(cacheDirPath);                   //设置  Application Caches 缓存目录

        mWebView.addJavascriptInterface(new WebViewJsInterface(mBActivity, mWebView), "handler");
        mWebView.setWebChromeClient(new CustomWebChromeClient());
        mWebView.setWebViewClient(new CustomWebViewClient());

        //调试WebView需要满足安卓系统版本为Android 4.4+已上, 并且需要APP内配置相应的代码
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    private void loadWebViewData() {
        String type = getIntent().getStringExtra(TYPE);
        String url = getIntent().getStringExtra("url");
        if (ObjectUtils.isNotEmpty(url)) CU.synCookies(this, url);

        switch (type) {
            case TYPE_LOAD_URL:
                mWebView.loadUrl(url);
                break;
            case TYPE_POST_URL:
                mWebView.postUrl(url, CU.getBytes(mPostData, "BASE64"));
                break;
            case TYPE_LOAD_CONTENT:
                String data = getIntent().getStringExtra("data");
                mWebView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
                break;
            case TYPE_LOAD_FILE:
                mWebView.loadUrl("file:///android_asset/js_test.html");
                break;
        }
    }

    public class WebViewJsInterface extends Object {

        private WebView webView;
        private Context context;

        public WebViewJsInterface(Context context, WebView webView) {
            this.webView = webView;
            this.context = context;
        }

        @JavascriptInterface
        public void handleMessage(String message) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public void callAndroid(String message) {
            // 特别注意：JS代码调用一定要在 onPageFinished（）回调之后才能调用。
            String script = ViewUtil.formatScript("callJS", message);
            ViewUtil.callJavaScriptFunction(webView, script);
        }

        @JavascriptInterface
        public void setWebViewTextCallback() {
            String script = ViewUtil.formatScript("setText", "This is a text from Android which is set in the html");
            ViewUtil.callJavaScriptFunction(webView, script);
        }

        @JavascriptInterface
        public void getSource(String html) { //获取网页内容

        }
    }

    class CustomWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, final String message, final JsResult result) {
            AlertDialog.Builder b = new AlertDialog.Builder(mBActivity);
            b.setTitle("Alert");
            b.setMessage(message);
            b.setPositiveButton(android.R.string.ok, (dialog, which) -> result.confirm());
            b.setCancelable(false);
            b.create().show();
            return true; // false: 弹系统框
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult
                result) {

            // 根据协议的参数，判断是否是所需要的url(原理同方式2)
            // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
            //假定传入进来的 url = "js://demo?arg1=111&arg2=222"（同时也是约定好的需要拦截的）
            Uri uri = Uri.parse(message);

            // 如果url的协议 = 预先约定的 js 协议, 就解析往下解析参数
            if (uri.getScheme().equals("js")) {

                // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                // 所以拦截url,下面JS开始调用Android需要的方法
                if (uri.getAuthority().equals("demo")) {

                    // 执行JS所需要调用的逻辑
                    System.out.println("js调用了Android的方法");

                    // 解析协议上的参数
                    Set<String> collection = uri.getQueryParameterNames();
                    Iterator iterator = collection.iterator();
                    while (iterator.hasNext()) {
                        String value = uri.getQueryParameter((String) iterator.next());
                    }

                    //参数result:代表消息框的返回值(输入值)
                    result.confirm("js调用了Android的方法成功啦");
                }
                return true;
            }
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) { // 加载进度变化
            if (mPbWebView != null) {
                mPbWebView.setProgress(newProgress);
                mPbWebView.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    }

    public class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setBlockNetworkImage(mIsBlockNetworkImage);  // 为了加快加载速度，先阻止图片加载
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public final void onPageFinished(WebView view, String url) {
            view.getSettings().setBlockNetworkImage(!mIsBlockNetworkImage); // 网页加载完毕，开启加载图片
//            view.loadUrl("javascript:window.handler.getSource('<head>'+"
//                    + "document.getElementsByTagName('html')[0].innerHTML+'</head>');"); //获取网页内容时打开
            super.onPageFinished(view, url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override // 重载此方法使得打开网页时不调用系统浏览器，而是在本WebView中显示，拦截处理url亦在此
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false; // 返回true由应用的代码处理该url; 返回false则由WebView处理该url，即用WebView加载该url

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

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();               // 表示接受所有网站的证书
//            handler.cancel();              // 表示挂起连接，为默认方式
//            handler.handleMessage(null);   // 进行其他处理
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (ObjectUtils.isNotEmpty(mPostData)) {
                String url = getIntent().getStringExtra("url");
                try {
                    URL mUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("version", "blue"); // add to header
                    DataOutputStream os = new DataOutputStream(connection.getOutputStream());
//    implementation 'org.apache.httpcomponents:httpcore:4.4.10'
//    os.write(EncodingUtils.getBytes(mPostData, "BASE64")); // add to body
                    os.write(CU.getBytes(mPostData, "BASE64")); // add to body
                    os.flush();
                    mPostData = null;
                    return new WebResourceResponse("text/html", connection.getContentEncoding(), connection
                            .getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mPostData = null;
                }
            }
            return super.shouldInterceptRequest(view, request);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}