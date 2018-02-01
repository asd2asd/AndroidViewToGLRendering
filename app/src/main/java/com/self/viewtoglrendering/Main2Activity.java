package com.self.viewtoglrendering;

import android.app.Activity;
import android.os.Bundle;
import android.view.TextureView;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

public class Main2Activity extends Activity {

    private GLWebView glWebView;
    private TextureView textureView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

//        glWebView = (GLWebView) findViewById(R.id.main2_glwebview);
//        textureView = (TextureView) findViewById(R.id.main2_textureview);
//        ViewToTextureView  viewToGlRenderer = new ViewToTextureView();
//        textureView.setSurfaceTextureListener(viewToGlRenderer);
//
//
//        glWebView.setViewToGLRenderer(viewToGlRenderer);
//
//        glWebView.setWebViewClient(new WebViewClient());
//        glWebView.setWebChromeClient(new WebChromeClient());
//        glWebView.loadUrl("http://stackoverflow.com/questions/12499396/is-it-possible-to-render-an-android-view-to-an-opengl-fbo-or-texture");

    }
}
