package com.self.viewtoglrendering;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Main3Activity extends Activity{

    TextureViewPager textureViewPager;
    RelativeLayout mainContainer;
    RelativeLayout textureViewContainer;
    Button controlButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BrowserHandler.getInstance().init();
        setContentView(R.layout.activity_main3);
//        cubeSurfaceView = (CubeSurfaceView) findViewById(R.id.cube_surfaceView);
//        glWebView = (NewGlWebView) findViewById(R.id.cube_webview);
//        cubeSurfaceView.init();
//        glWebView.setWebChromeClient(new WebChromeClient());
//        glWebView.setWebViewClient(new WebViewClient());
//        glWebView.getSettings().setJavaScriptEnabled(true);
//        glWebView.loadUrl("https://hao.360.cn");


        textureViewPager = new TextureViewPager(this);
        mainContainer = (RelativeLayout)findViewById(R.id.main3_container);
        textureViewContainer = (RelativeLayout)findViewById(R.id.textview_container);
        textureViewContainer.addView(textureViewPager);

        final NewGlWebView glWebView = new NewGlWebView(this);
        webviewInit(glWebView);
        final NewGlWebView glWebView2 = new NewGlWebView(this);
        webviewInit(glWebView2);
        final NewGlWebView glWebView3 = new NewGlWebView(this);
        webviewInit(glWebView3);
        final NewGlWebView glWebView4 = new NewGlWebView(this);
        webviewInit(glWebView4);
        final NewGlWebView glWebView5 = new NewGlWebView(this);
        webviewInit(glWebView5);
        final NewGlWebView glWebView6 = new NewGlWebView(this);
        webviewInit(glWebView6);
        final NewGlWebView glWebView7 = new NewGlWebView(this);
        webviewInit(glWebView7);
        final NewGlWebView glWebView8 = new NewGlWebView(this);
        webviewInit(glWebView8);


        mainContainer.addView(glWebView);
        mainContainer.addView(glWebView2);
        mainContainer.addView(glWebView3);
        mainContainer.addView(glWebView4);
        mainContainer.addView(glWebView5);
        mainContainer.addView(glWebView6);
        mainContainer.addView(glWebView7);
        mainContainer.addView(glWebView8);

        glWebView.loadUrl("https://m.hupu.com");
        glWebView2.loadUrl("https://hao.360.cn");
        glWebView3.loadUrl("https://m.smzdm.com");
        glWebView4.loadUrl("https://m.zhibo8.cc");
        glWebView5.loadUrl("https://www.hi-pda.com");
        glWebView6.loadUrl("https://www.renren.com");
        glWebView7.loadUrl("https://m.jd.com");
        glWebView8.loadUrl("http://bbs.flyme.cn");


        TextureViewPagerAdapter adapter = new TextureViewPagerAdapter();
        adapter.addView(glWebView);
        adapter.addView(glWebView2);
        adapter.addView(glWebView3);
        adapter.addView(glWebView4);
        adapter.addView(glWebView5);
        adapter.addView(glWebView6);
        adapter.addView(glWebView7);
        adapter.addView(glWebView8);

        textureViewPager.setAdapter(adapter);



        controlButton = (Button)findViewById(R.id.control_button);
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textureViewPager.isDraw()) textureViewPager.setDraw(false);
                else textureViewPager.setDraw(true);

                glWebView.onPause();
                glWebView2.onPause();
                glWebView3.onPause();
                glWebView4.onPause();
                glWebView5.onPause();
                glWebView6.onPause();
                glWebView7.onPause();
                glWebView8.onPause();
                mainContainer.removeAllViews();
            }
        });
    }

    private void webviewInit(NewGlWebView webView)
    {
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
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
//                glWebView.updatePreviewSurface(cubeSurfaceView.getRectSurface(0));
//            }
//        });
        textureViewPager.resume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

//        if(keyCode==KeyEvent.KEYCODE_BACK)
//        {
//            if(glWebView2.canGoBack());
//            {
//                glWebView2.goBack();
//                return true;
//            }
//        }
        return super.onKeyDown(keyCode, event);
    }




}

