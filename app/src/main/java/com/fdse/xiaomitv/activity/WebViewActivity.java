package com.fdse.xiaomitv.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.alibaba.fastjson.JSONObject;
import com.danikula.videocache.HttpProxyCacheServer;
import com.fdse.xiaomitv.R;
import com.fdse.xiaomitv.constant.UrlConstant;
import com.fdse.xiaomitv.http.HttpUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private VideoView videoView;
    private ProgressBar progressBar;
    private HttpProxyCacheServer proxy;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //html
                    videoView.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl(String.valueOf(msg.obj));
                    break;
                case 1:
                    //video
                    webView.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                    String proxyUrl = proxy.getProxyUrl(String.valueOf(msg.obj));
                    videoView.setVideoPath(proxyUrl);
//                    videoView.setVideoPath(String.valueOf(msg.obj));
                    videoView.start();
                    break;
                case 2:
                    //image

                    break;
                case 3:
                    //word
                    videoView.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    webView.loadData(getHtmlData(String.valueOf(msg.obj)), "text/html;charset=utf-8", "utf-8");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initView();


        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getTVTask();
            }
        }, 1000, 20000);
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                refresh();
//            }
//        }, 1000, 300000);

//        Timer refreshTimer = new Timer();
//        refreshTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                refresh();
//            }
//        }, 1000, 300000);

    }

    private void initView() {
        webView = (WebView) findViewById(R.id.wv_tv);
        webView.getSettings().setJavaScriptEnabled(true);
        //支持屏幕缩放
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        //不显示webview缩放按钮
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);
        videoView = (VideoView) findViewById(R.id.videoview_view);//进度条
        videoView.setMediaController(new MediaController(this));
        //监听视频播放完的代码
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mPlayer) {
                mPlayer.start();
                mPlayer.setLooping(true);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressbar);//进度条


        //poxy
        proxy = newProxy();

    }

    @SuppressLint("LongLogTag")
    private void syncCookie(Context context, String url) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeSessionCookie();// 移除旧的[可以省略]
        String oldCookie = cookieManager.getCookie(url);
        if(oldCookie != null){
            Log.d("Nat: webView.syncCookieOutter.oldCookie", oldCookie);
        }

        StringBuilder sbCookie = new StringBuilder();
        sbCookie.append(String.format("JSESSIONID=%s","INPUT YOUR JSESSIONID STRING"));
        sbCookie.append(String.format(";domain=%s", "INPUT YOUR DOMAIN STRING"));
        sbCookie.append(String.format(";path=%s","INPUT YOUR PATH STRING"));

        String cookieValue = sbCookie.toString();
        cookieManager.setCookie(url, cookieValue);

        CookieSyncManager.getInstance().sync();// To get instant sync instead of waiting for the timer to trigger, the host can call this.
    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("ansen", "拦截url:" + url);
            if (url.equals("http://www.google.com/")) {
                Toast.makeText(WebViewActivity.this, "国内不能访问google,拦截该url", Toast.LENGTH_LONG).show();
                return true;//表示我已经处理过了
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

    };

    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient = new WebChromeClient() {
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定", null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();

            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.i("ansen", "网页标题:" + title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            progressBar.setProgress(newProgress);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("ansen", "是否有上一个页面:" + webView.canGoBack());
        if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {//点击返回按钮的时候判断有没有上一页
            webView.goBack(); // goBack()表示返回webView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * JS调用android的方法
     *
     * @param str
     * @return
     */
    @JavascriptInterface //仍然必不可少
    public void getClient(String str) {
        Log.i("ansen", "html调用客户端:" + str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //释放资源
        webView.destroy();
        webView = null;
    }

    //保存任务信息
    private void getTVTask() {

        String serviceURL = UrlConstant.getAppBackEndServiceURL(UrlConstant.APP_BACK_END_TV_GET_TV_TASK);
        HttpUtil.sendOkHttpRequestByGet(serviceURL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responceData = response.body().string();
                JSONObject responceDataJson = JSONObject.parseObject(responceData);
                if ("1".equals(responceDataJson.get("state"))) {
                    Message message = new Message();
                    message.obj = responceDataJson.get("url");
                    if ("html".equals(responceDataJson.get("type"))) {
                        message.what = 0;
                    } else if ("video".equals(responceDataJson.get("type"))) {
                        message.what = 1;
                    } else if ("image".equals(responceDataJson.get("type"))) {
                        message.what = 2;
                    } else if ("word".equals(responceDataJson.get("type"))) {
                        message.what = 3;
                    }

                    mHandler.sendMessage(message);
                }
            }
        });
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)       // 1 Gb for cache
                .build();
    }

    private String getHtmlData(String bodyHTML) {
        String html = "<html><body>" + bodyHTML + "</body></html>";
        return html;
    }

    private void refresh() {
        finish();
        Intent intent = new Intent(WebViewActivity.this, WebViewActivity.class);
        startActivity(intent);
    }

}