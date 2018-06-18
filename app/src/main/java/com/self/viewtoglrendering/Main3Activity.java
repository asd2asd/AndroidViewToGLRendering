package com.self.viewtoglrendering;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

public class Main3Activity extends Activity{

    NewGlWebView glWebView;
    NewGlWebView glWebView2;
    TextureViewPager textureViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
//        cubeSurfaceView = (CubeSurfaceView) findViewById(R.id.cube_surfaceView);
//        glWebView = (NewGlWebView) findViewById(R.id.cube_webview);
//        cubeSurfaceView.init();
//        glWebView.setWebChromeClient(new WebChromeClient());
//        glWebView.setWebViewClient(new WebViewClient());
//        glWebView.getSettings().setJavaScriptEnabled(true);
//        glWebView.loadUrl("https://hao.360.cn");


        textureViewPager = (TextureViewPager) findViewById(R.id.texture_view_pager);

        glWebView = (NewGlWebView) findViewById(R.id.cube_webview);
        glWebView2 = (NewGlWebView) findViewById(R.id.cube_webview2);


        glWebView.setWebChromeClient(new WebChromeClient());
        glWebView.setWebViewClient(new WebViewClient());
        glWebView.getSettings().setJavaScriptEnabled(true);
        glWebView.loadUrl("https://hao.360.cn");

        glWebView2.setWebChromeClient(new WebChromeClient());
        glWebView2.setWebViewClient(new WebViewClient());
        glWebView2.getSettings().setJavaScriptEnabled(true);
        glWebView2.loadUrl("https://m.hupu.com");
//        glWebView2.setOnTouchListener(this);

        textureViewPager.addTextureDrawView(glWebView);
        textureViewPager.addTextureDrawView(glWebView2);

    }


    @Override
    protected void onPause() {
        super.onPause();
//        cubeSurfaceView.pause();
        textureViewPager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        cubeSurfaceView.resume(1, new CubeSurfaceView.CubeSurfaceEventListener() {
//            @Override
//            public void OnSurfaceAvaiable() {
//                glWebView.setPreviewTexture(cubeSurfaceView.getRectSurfaceTexture(0));
//            }
//        });
        textureViewPager.resume();
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

