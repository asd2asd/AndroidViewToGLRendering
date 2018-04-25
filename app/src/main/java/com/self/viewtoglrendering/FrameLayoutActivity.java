package com.self.viewtoglrendering;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

public class FrameLayoutActivity extends Activity {

    private NewGlWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_layout);

        webView = (NewGlWebView)findViewById(R.id.webview);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
//        webView.pauseTimers();
        webView.loadUrl("https://m.smzdm.com");
    }
}
