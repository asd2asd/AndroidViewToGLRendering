package com.self.viewtoglrendering;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.webkit.WebView;

import org.w3c.dom.Text;

/**
 * Created by user on 3/15/15.
 */
public class GLWebView extends WebView implements GLRenderable{

    private ViewToGLRenderer mViewToGLRenderer;
    private GLSurfaceView glSurfaceView;
    private boolean recordFps;
    private int fpsCount;

    View2TextureView textureView;

    // default constructors

    public GLWebView(Context context) {
        super(context);
        init();
    }

    public GLWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GLWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private  void init()
    {
        recordFps = true;
        fpsCount = 0;
        new Thread(fpsRunnable).start();
    }

    public void setTextureView(View2TextureView textureView){
        this.textureView = textureView;
    }

    // draw magic
    @Override
    public void draw( Canvas canvas ) {
        draw2(canvas);
    }

    private void draw2(Canvas canvas)
    {
        if(null==textureView) return;
        Canvas c = textureView.lockCanvas();

        long timeStart = System.currentTimeMillis();
        if(c!=null)
        {
            c.translate(-getScrollX(), -getScrollY());
            super.draw(c);
        }
        long during = System.currentTimeMillis() - timeStart;
        Log.e("draw during",during+"");

        textureView.unlockCanvasAndPost(c);
    }

    private void draw1(Canvas canvas)
    {
        //returns canvas attached to gl texture to draw on
        Canvas glAttachedCanvas = mViewToGLRenderer.onDrawViewBegin();
        if(glAttachedCanvas != null) {
            //translate canvas to reflect view scrolling
            float xScale = glAttachedCanvas.getWidth() / (float)canvas.getWidth();
//            glAttachedCanvas.scale(xScale, xScale);
            glAttachedCanvas.translate(-getScrollX(), -getScrollY());
            //draw the view to provided canvas
            long timeStart = System.currentTimeMillis();
            super.draw(glAttachedCanvas);
            long during = System.currentTimeMillis() - timeStart;
            Log.e("draw during",during+"");
        }
        // notify the canvas is updated
        mViewToGLRenderer.onDrawViewEnd();
//        Log.e("draw","draw");
//        glSurfaceView.requestRender();
        super.draw(canvas);
        fpsCount++;
    }



    public void setViewToGLRenderer(ViewToGLRenderer viewTOGLRenderer){
        mViewToGLRenderer = viewTOGLRenderer;
    }

    public void setGlSurfaceView(GLSurfaceView glSurfaceView)
    {
        this.glSurfaceView = glSurfaceView;
    }



    Runnable fpsRunnable = new Runnable() {

        @Override
        public void run() {
            while (recordFps)
            {
                Log.e("fps",fpsCount+"");
                fpsCount = 0;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    };

}
