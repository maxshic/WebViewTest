package uni.UNIEB96C19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private WebView webview = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.createWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void createWebView(){
        Log.e("进入app", "start");
        this.webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webview.getSettings().setAllowFileAccess(true);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String referer = "https://qa.aohuahealth.com";
                Log.e("调试url", url);
                if(url.startsWith("tel:")){
                    Log.e("电话", url.substring(4));
                    Intent myIntent = new Intent(Intent.ACTION_CALL);
                    Uri data = Uri.parse(url);
                    myIntent.setData(data);
                    if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                        startActivity(myIntent);
                    }else{
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{  Manifest.permission.CALL_PHONE }, 1);
                    }
                    return true;
                }

                if(url.startsWith("https://wx.tenpay.com")){
                    Log.e("跳转支付1", url);
                    Map<String, String> extraHeaders = new HashMap<>();
                    extraHeaders.put("Referer", referer);
                    view.loadUrl(url, extraHeaders);
                    return true;
                }

                try {
                    if(url.startsWith("weixin://")){
                        Log.e("调起支付", url);
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                }catch(Exception e){
                    return false;
                }


                view.loadUrl(url);
                return true;
            }

//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                Log.e("调试", request.getUrl().toString());
//                return super.shouldOverrideUrlLoading(view, request);
//            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.e("error", error.toString());
                handler.proceed();
            }
        });
        webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                Log.e("打开", "新页面");
                try {
                    WebView.HitTestResult result = webview.getHitTestResult();
                    String data = result.getExtra();
                    Log.e("打开", data);
                }catch(Exception e){
                    Log.e("exception", e.toString());
                }

                return true;
            }
        });
        webview.setOnKeyListener(new View.OnKeyListener(){

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()){
                    webview.goBack();
                    return true;
                }
                return false;
            }
        });
//        String url = "https://m.shark-online.io/#/";
//        String url = "http://192.168.31.148:5500/index.html";
        String url = "https://qa.aohuahealth.com/static/h5/core.html";
//        String url = "https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?prepay_id=wx18004654542642af4bd357d38c7bad0000&package=1032438673";

//        Map<String, String> extraHeaders = new HashMap<>();
//        extraHeaders.put("Referer", "https://qa.aohuahealth.com");
//        webview.loadUrl(url, extraHeaders);
        webview.loadUrl(url);
    }

    @Override
    public void onBackPressed(){
        if(this.webview.canGoBack()){
            this.webview.goBack();
        }else{
            super.onBackPressed();
        }
    }
}