package com.self.viewtoglrendering;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

public class Main3Activity extends Activity {

    CubeSurfaceView cubeSurfaceView;
    NewGlWebView glWebView;
    CubeSurfaceView cubeSurfaceView2;
    NewGlWebView glWebView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        cubeSurfaceView = (CubeSurfaceView) findViewById(R.id.cube_surfaceView);
        glWebView = (NewGlWebView) findViewById(R.id.cube_webview);
        cubeSurfaceView.init();
        glWebView.setWebChromeClient(new WebChromeClient());
        glWebView.setWebViewClient(new WebViewClient());
        glWebView.getSettings().setJavaScriptEnabled(true);
        glWebView.loadUrl("https://hao.360.cn");


        cubeSurfaceView2 = (CubeSurfaceView) findViewById(R.id.cube_surfaceView2);
        glWebView2 = (NewGlWebView) findViewById(R.id.cube_webview2);
        cubeSurfaceView2.init();
        glWebView2.setWebChromeClient(new WebChromeClient());
        glWebView2.setWebViewClient(new WebViewClient());
        glWebView2.getSettings().setJavaScriptEnabled(true);
        glWebView2.loadUrl("https://m.hupu.com");
    }


    @Override
    protected void onPause() {
        super.onPause();
        cubeSurfaceView.pause();
        cubeSurfaceView2.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cubeSurfaceView.resume(glWebView);
        cubeSurfaceView2.resume(glWebView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(glWebView2.canGoBack());
            {
                glWebView2.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

