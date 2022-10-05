package com.albumi;

import androidx.appcompat.app.AppCompatActivity;


import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.appsflyer.AppsFlyerLib;

import java.util.Objects;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView = null;
    private Boolean firstLink = false;
    private String secondLink = null;
    private String link = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        link = getIntent().getStringExtra("builtLink");
        run();
    }
    //==============================================================================================
    private void run(){
        //==========================================================================================
        webView = findViewById(R.id.webview);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        webView.getSettings().setUserAgentString(String.format("%s [%s/%s]", webSettings.getUserAgentString(), "App Android", BuildConfig.VERSION_NAME));

        //==========================================================================================
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (Objects.equals(url, "https://icelucky.xyz"))
                    url = link;//ЗАГЛУШКА

                if (firstLink&&secondLink==null)
                    secondLink = url;
                else if (!firstLink)
                    firstLink = true;

                if (secondLink==null)
                    super.onPageFinished(webView, url);
                else
                    super.onPageFinished(webView, secondLink);
                CookieManager.getInstance().flush();
            }
            //===
        });
        //==========================================================================================
        webView.loadUrl(link);
    }
    //==============================================================================================
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}