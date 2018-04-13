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

import javax.microedition.khronos.opengles.GL;

/**
 * Created by user on 3/15/15.
 */
public class GLWebView extends WebView implements GLRenderable{

    private ViewToGLRenderer mViewToGLRenderer;
    private GLSurfaceView glSurfaceView;
    private boolean recordFps;
    private int fpsCount;


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

        this.getSettings().setJavaScriptEnabled(true);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//		if(oldt==t) Toast.makeText(mContext,"scroll stop",Toast.LENGTH_SHORT).show();
		int scrollDistance = t - oldt;
//		if (null != mActionListener&&mCanToolsBarScroll)
//		{
////			if (t==0||scrollDistance < -20)
//			if(scrollDistance<0) {
//				mActionListener.OnScrollUp(this, oldt, t);
//				lastScrollUp = 1;
//			}
//			else if (scrollDistance > 0) {
//				mActionListener.OnScrollDown(this, oldt, t);
//				lastScrollUp = 2;
//			}
//		}
//        this.setPadding(0,0,0,t);
		super.onScrollChanged(l, t, oldl, oldt);
    }


    // draw magic
    @Override
    public void draw( Canvas canvas ) {

        long timeStart = System.currentTimeMillis();
//        super.draw(canvas);
        draw1(canvas);
//        superDraw(canvas);
//        long during = System.currentTimeMillis() - timeStart;
//        Log.e("draw during",during+"");
    }

    private void draw0(Canvas canvas)
    {
        Bitmap bitmap = Bitmap.createBitmap(1440,2480, Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(bitmap);
        canvas1.clipRect(0,0,1440,2480);
        superDraw(canvas1);

    }

    private void draw1(Canvas canvas)
    {
        //returns canvas attached to gl texture to draw on
        final Canvas glAttachedCanvas = mViewToGLRenderer.onDrawViewBegin();
        if(glAttachedCanvas != null) {
            //translate canvas to reflect view scrolling
            float xScale = glAttachedCanvas.getWidth() / (float)canvas.getWidth();
            glAttachedCanvas.scale(xScale, xScale);
            glAttachedCanvas.translate(-getScrollX(), -getScrollY());

            //draw the view to provided canvas
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    GLWebView.this.superDraw(glAttachedCanvas);
//                }
//            }).start();
            superDraw(glAttachedCanvas);
            // notify the canvas is updated
            mViewToGLRenderer.onDrawViewEnd();
        }
        else
        {
//            superDraw(canvas);
        }
//        Log.e("draw","draw");
        glSurfaceView.requestRender();
    }


    private long lastDrawTime = System.currentTimeMillis();
    private void superDraw(Canvas canvas)
    {

        fpsCount++;

        long timeStart = System.currentTimeMillis();
        super.draw(canvas);
//        super.onDraw(canvas);
        long during = System.currentTimeMillis() - timeStart;
//        if(during>15)
            Log.e("draw during",during+"");

//        Log.e("fps",""+(1000.0f/(System.currentTimeMillis()-lastDrawTime)));
        lastDrawTime = System.currentTimeMillis();

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
//                Log.e("fps",fpsCount+"");
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
