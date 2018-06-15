package com.self.viewtoglrendering;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.MutableInt;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.self.viewtoglrendering.cuberenerer.CubeGLRenderer;
import com.self.viewtoglrendering.cuberenerer.MultiWindowRenderer;


public class MainActivity extends Activity {

    private GLSurfaceView mGLSurfaceView;
    private GLRenderable mGLLinearLayout;
    private GLWebView mWebView;

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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(mWebView.canGoBack());
            {
                mWebView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }



    private void initViews() {

        RelativeLayout relativeLayout = new RelativeLayout(this);
        this.setContentView(relativeLayout);

        ViewToGLRenderer viewToGlRenderer = new CubeGLRenderer(this);

        mGLSurfaceView = new GLSurfaceView(this);
//        mGLSurfaceView.setOnTouchListener((MultiWindowRenderer)viewToGlRenderer);
//        mGLSurfaceView.setFocusable(true);
        mWebView = new GLWebView(this);
//        glWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);

        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mGLSurfaceView.setZOrderOnTop(true);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(viewToGlRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);



        relativeLayout.addView(mWebView);
        relativeLayout.addView(mGLSurfaceView);


        mWebView.setViewToGLRenderer(viewToGlRenderer);
        mWebView.setGlSurfaceView(mGLSurfaceView);

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.loadUrl("https://hao.360.cn/");
    }


}
