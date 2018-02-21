package com.self.viewtoglrendering;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.self.viewtoglrendering.cuberenerer.CubeGLRenderer;


public class MainActivity extends ActionBarActivity {

    private GLSurfaceView mGLSurfaceView;
    private GLRenderable mGLLinearLayout;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

//    private void initViews() {
//        setContentView(R.layout.activity_main);
//
//        ViewToGLRenderer viewToGlRenderer = new CubeGLRenderer(this);
//
//        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surface_view);
//        mGLLinearLayout = (GLRenderable) findViewById(R.id.gl_layout);
//        mWebView = (WebView) findViewById(R.id.web_view);
//
//        mGLSurfaceView.setEGLContextClientVersion(2);
//        mGLSurfaceView.setRenderer(viewToGlRenderer);
//
//        mGLLinearLayout.setViewToGLRenderer(viewToGlRenderer);
//
//        mWebView.setWebViewClient(new WebViewClient());
//        mWebView.setWebChromeClient(new WebChromeClient());
//        mWebView.loadUrl("http://stackoverflow.com/");
//    }


    private void initViews() {

        RelativeLayout relativeLayout = new RelativeLayout(this);
        this.setContentView(relativeLayout);

        ViewToGLRenderer viewToGlRenderer = new CubeGLRenderer(this);

        mGLSurfaceView = new GLSurfaceView(this);
        GLWebView glWebView = new GLWebView(this);

        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mGLSurfaceView.setZOrderOnTop(true);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(viewToGlRenderer);
//        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        View2TextureView textureView = new View2TextureView(this);


        relativeLayout.addView(glWebView);
        relativeLayout.addView(mGLSurfaceView);
        relativeLayout.addView(textureView);

        glWebView.setTextureView(textureView);

        glWebView.setViewToGLRenderer(viewToGlRenderer);
        glWebView.setGlSurfaceView(mGLSurfaceView);

        glWebView.setWebViewClient(new WebViewClient());
        glWebView.setWebChromeClient(new WebChromeClient());
        glWebView.loadUrl("https://m.smzdm.com/");
    }


}
