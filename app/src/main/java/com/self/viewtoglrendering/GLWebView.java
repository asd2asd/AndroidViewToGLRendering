package com.self.viewtoglrendering;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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
    private Paint paint;


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
        paint = new Paint();
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

        long timeStart = System.currentTimeMillis();
        //returns canvas attached to gl texture to draw on
        Canvas glAttachedCanvas;
        glAttachedCanvas= mViewToGLRenderer.onDrawViewBegin();
        if(glAttachedCanvas != null) {
            //translate canvas to reflect view scrolling
            float xScale = glAttachedCanvas.getWidth() / (float)canvas.getWidth();
            float yScale = glAttachedCanvas.getHeight()/(float)canvas.getHeight();
            int scrollX = getScrollX();
            int scrollY = getScrollY();
//            xScale = xScale/2;
//            yScale/=2;
            glAttachedCanvas.scale(xScale, yScale);
            int save = canvas.save();
            glAttachedCanvas.translate(-scrollX, -scrollY);

//            glAttachedCanvas.clipRect(scrollX,scrollY,scrollX+getWidth(),scrollY+getHeight());
//            glAttachedCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

            superDraw(glAttachedCanvas);
//            glAttachedCanvas.restoreToCount(save);
//            glAttachedCanvas.translate(-scrollX+canvas.getWidth()/2 , -scrollY);
//            superDraw(glAttachedCanvas);
            // notify the canvas is updated
            mViewToGLRenderer.onDrawViewEnd();
            if(glSurfaceView!=null) glSurfaceView.requestRender();
//            super.draw(canvas);
        }
//        Log.e("draw","draw");
//        glSurfaceView.requestRender();

//        glAttachedCanvas = mViewToGLRenderer.onDrawViewBegin();
//        if(glAttachedCanvas != null) {
//            //translate canvas to reflect view scrolling
//            float xScale = glAttachedCanvas.getWidth() / (float)canvas.getWidth();
//            float yScale = glAttachedCanvas.getHeight()/(float)canvas.getHeight();
//            xScale = xScale/2;
//            yScale/=2;
//            glAttachedCanvas.scale(xScale, yScale);
//            glAttachedCanvas.translate(-getScrollX() + canvas.getWidth() , -getScrollY());
//
//            int scrollX = getScrollX();
//            int scrollY = getScrollY();
//            glAttachedCanvas.clipRect(scrollX,scrollY,scrollX+getWidth(),scrollY+getHeight());
////            glAttachedCanvas.clipRect(canvas.getClipBounds());
//
//            superDraw(glAttachedCanvas);
//            // notify the canvas is updated
//            mViewToGLRenderer.onDrawViewEnd();
//            if(glSurfaceView!=null) glSurfaceView.requestRender();
////            super.draw(canvas);
//        }


        long during = System.currentTimeMillis() - timeStart;
        Log.e("webview draw during",during+"");
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
//            Log.e("webview draw during",during+"");

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
