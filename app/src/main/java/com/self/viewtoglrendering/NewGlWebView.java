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

    // draw magic
    @Override
    public void draw( Canvas canvas ) {
//        super.draw(canvas);

//        Log.e("webview","draw");
//        Log.e("draw",canvas.getHeight()+"");

        if(null!=onFrameAvailableListener) onFrameAvailableListener.onFrameAvailable(mSurfaceTexture);
        Canvas canvas1 = null;
        if(mSurface!=null)
        {
            canvas1 = mSurface.lockHardwareCanvas();
            canvas1.translate(-getScrollX(), -getScrollY());
//            canvas1.clipRect(0,0,1440,1440);
            super.draw(canvas1);
            mSurface.unlockCanvasAndPost(canvas1);
        }

    }

    public void setOnFrameAvailableListener(OnFrameAvailableListener listener)
    {
        onFrameAvailableListener = listener;
    }


    public void setOnScrollListener(OnScrollListener listener)
    {
        onScrollListener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//		if(oldt==t) Toast.makeText(mContext,"scroll stop",Toast.LENGTH_SHORT).show();
        int scrollDistance = t - oldt;
		if (null != onScrollListener)
		{
//			if (t==0||scrollDistance < -20)
			if(scrollDistance<0) {
				onScrollListener.OnScrollUp(this, oldt, t);
//				lastScrollUp = 1;
			}
			else if (scrollDistance > 0) {
				onScrollListener.OnScrollDown(this, oldt, t);
//				lastScrollUp = 2;
			}
		}
//        this.setPadding(0,0,0,t);
        super.onScrollChanged(l, t, oldl, oldt);
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
