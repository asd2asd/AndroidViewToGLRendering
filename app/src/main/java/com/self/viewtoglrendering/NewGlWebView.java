package com.self.viewtoglrendering;

import android.content.Context;
import android.hardware.Camera;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.webkit.WebView;

/**
 * Created by jose on 2018/4/13.
 */

public class NewGlWebView extends WebView {

    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private OnFrameAvailableListener onFrameAvailableListener;
    private OnScrollListener onScrollListener;

    public NewGlWebView(Context context) {
        super(context);
    }

    public NewGlWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewGlWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NewGlWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void setPreviewTexture(SurfaceTexture surfaceTexture)
    {
        releaseSurface();
        mSurfaceTexture = surfaceTexture;
        mSurface = new Surface(mSurfaceTexture);
    }


    public void releaseSurface(){
        if(mSurface != null){
            mSurface.release();
        }
//        if(mSurfaceTexture != null){
//            mSurfaceTexture.release();
//        }
        mSurface = null;
        mSurfaceTexture = null;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec-paddingBottom);
    }

    // draw magic
    @Override
    public void draw( Canvas canvas ) {
//        super.draw(canvas);

//        Log.e("webview","draw");
//        Log.e("draw",canvas.getHeight()+"");

        canResizeHeight = false;
        long startTime = System.currentTimeMillis();

        if(null!=onFrameAvailableListener) onFrameAvailableListener.onFrameAvailable(mSurfaceTexture);

        if(canvas.getHeight() == originHeight - paddingBottom) canResizeHeight = true;


        Canvas canvas1 = null;
        if(mSurface!=null)
        {
            canvas1 = mSurface.lockHardwareCanvas();
            originHeight = canvas1.getHeight();
            canvas1.translate(-getScrollX(), -getScrollY());
//            canvas1.clipRect(0,0,1440,1440);
            super.draw(canvas1);
            mSurface.unlockCanvasAndPost(canvas1);
        }
        long endTime = System.currentTimeMillis() - startTime;
//        Log.e("during ",endTime+"");


    }

    public void setOnFrameAvailableListener(OnFrameAvailableListener listener)
    {
        onFrameAvailableListener = listener;
    }


    public void setOnScrollListener(OnScrollListener listener)
    {
        onScrollListener = listener;
    }


    private final int MAX_SCROLL_DISTANCE = 200;
    private final int SCROLL_DIVISOR = 5;
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        int scrollDistance = t - oldt;
//		if (null != onScrollListener)
//		{
//			if(scrollDistance<0) {
//				onScrollListener.OnScrollUp(this, oldt, t);
//			}
//			else if (scrollDistance > 0) {
//				onScrollListener.OnScrollDown(this, oldt, t);
//			}
//		}
        long currentTime = System.currentTimeMillis();
        long during = currentTime - resizeTime;
        if(during>1000)
        {
            resizeTime = currentTime;
        }
        else
        {
            float v = ((t-lastTop)*1000.0f/during);
            Log.e("v",v+"");
//            if(v>7000)
//            {
//                resizeTime = currentTime;
//                lastTop = t;
//                scrollChange(oldt,oldt+MAX_SCROLL_DISTANCE*SCROLL_DIVISOR);
//                if(canResizeHeight)this.requestLayout();
//
//            }
//            else if(v<-7000)
//            {
//                resizeTime = currentTime;
//                lastTop = t;
//                scrollChange(oldt,oldt-MAX_SCROLL_DISTANCE*SCROLL_DIVISOR);
//                if(canResizeHeight)this.requestLayout();
//            }
//            else
                if(during>30)
            {
                resizeTime = currentTime;
                lastTop = t;
                scrollChange(oldt,t);
//                if(canResizeHeight)
                {
                    this.requestLayout();
//                    canResizeHeight = false;
                }
            }

        }
//        if(during>300)
//        {
//            resizeTime = currentTime;
//            this.requestLayout();
//        }

        super.onScrollChanged(l, t, oldl, oldt);
    }

    private void resizeHeight()
    {

    }

    private int paddingBottom = 0;
    private long resizeTime = 0;
    private int lastTop = 0;
    private int originHeight;
    private boolean canResizeHeight = false;

    private void scrollChange(int oldt,int t)
    {
        int scrollDistance = t - oldt;

        int oldPadding = paddingBottom;
        int newPadding = oldPadding - (scrollDistance)/SCROLL_DIVISOR;
        if(newPadding>MAX_SCROLL_DISTANCE) newPadding = MAX_SCROLL_DISTANCE;
        if(newPadding<0) newPadding = 0;
//        Log.e(oldPadding+"",newPadding+"");
        paddingBottom = newPadding;
    }

    public interface OnScrollListener
    {

        void OnScrollUp(WebView webView,int oldt,int top);
        void OnScrollDown(WebView webView,int oldt,int top);
    }

    public interface OnFrameAvailableListener
    {
        void onFrameAvailable(SurfaceTexture surfaceTexture);
    }

}
