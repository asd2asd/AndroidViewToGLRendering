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

        if(null!=onFrameAvailableListener) onFrameAvailableListener.onFrameAvailable(mSurfaceTexture);
        Canvas canvas1 = null;
        if(mSurface!=null)
        {
            canvas1 = mSurface.lockHardwareCanvas();
            canvas1.translate(-getScrollX(), -getScrollY());
            super.draw(canvas1);
            mSurface.unlockCanvasAndPost(canvas1);
        }

    }

    public void setOnFrameAvailableListener(OnFrameAvailableListener listener)
    {
        onFrameAvailableListener = listener;
    }

    public interface OnFrameAvailableListener
    {
        void onFrameAvailable(SurfaceTexture surfaceTexture);
    }

}
