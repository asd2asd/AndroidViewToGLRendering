package com.self.viewtoglrendering;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by jose on 2018/4/13.
 */

public class NewGlWebView extends WebView implements CubeSurfaceView.DrawTextureView {

    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener;
    private OnScrollListener onScrollListener;
    private Paint paint;

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


    @Override
    public void setPreviewTexture(SurfaceTexture surfaceTexture)
    {
        releaseSurface();
        mSurfaceTexture = surfaceTexture;
        mSurface = new Surface(mSurfaceTexture);
        paint = new Paint();
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

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec-paddingBottom);
//    }

    private long drawTime;
    // draw magic
    @Override
    public void onDraw( Canvas canvas ) {

//        Log.e("webview","draw");
//        Log.e("draw",canvas.getHeight()+"");


        canResizeHeight = false;
        long startTime = System.currentTimeMillis();

        if(canvas.getHeight() == originHeight - paddingBottom) canResizeHeight = true;

//        if(!afterResizeDraw)
        {
//            Log.e("after resize draw "+(System.currentTimeMillis() - resizeTime)+"",canvas.getHeight()+","+lastResizeHeight);
            afterResizeDraw = true;
        }

        long drawTimeStart = System.currentTimeMillis();
//        int state = canvas.save();
//        canvas.translate(-getScrollX(), -getScrollY());
////            canvas1.clipRect(0,0,1440,1440);
//        canvas.drawColor(Color.WHITE);

//        canvas.restoreToCount(state);
//        canvas.translate(-getScrollX(), -getScrollY());
//        canvas.drawText(System.currentTimeMillis()+"",500+getScrollX(),500+getScrollY(),paint);
        Canvas canvas1 = null;
        if(mSurface!=null)
        {
            canvas1 = mSurface.lockHardwareCanvas();
            originHeight = canvas1.getHeight();
            int state = canvas1.save();
            canvas1.translate(-getScrollX(), -getScrollY());
            canvas1.clipRect(canvas.getClipBounds());
            canvas1.drawColor(Color.WHITE);
            super.onDraw(canvas1);
            canvas1.restoreToCount(state);
//            canvas1.drawText(System.currentTimeMillis()+"",500+getScrollX(),500+getScrollY(),paint);
            mSurface.unlockCanvasAndPost(canvas1);
        }

//        super.onDraw(canvas);
//        if(null!=onFrameAvailableListener) onFrameAvailableListener.onFrameAvailable(mSurfaceTexture);
        long endTime = System.currentTimeMillis() - startTime;

        long during = System.currentTimeMillis() - drawTime;
        drawTime = System.currentTimeMillis();
//        if(during>20)
//        Log.e("webview draw during ",during+","+endTime);



//        if(
////                during<30&&
////                canChangeHeight&&
//                lastResizeHeight>10&&lastResizeHeight!=canvas.getHeight())this.layout(0,0,getWidth(),lastResizeHeight);
//        if(canChangeHeight)
//            Log.e((during)+"",(System.currentTimeMillis() - drawTimeStart)+","+canvas.getHeight()+","+lastResizeHeight);

    }

    public void setOnFrameAvailableListener(SurfaceTexture.OnFrameAvailableListener listener)
    {
        onFrameAvailableListener = listener;
    }


    public void setOnScrollListener(OnScrollListener listener)
    {
        onScrollListener = listener;
    }


    private final int MAX_SCROLL_DISTANCE = 200;
    private final int SCROLL_DIVISOR = 2;
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
////        int scrollDistance = t - oldt;
////		if (null != onScrollListener)
////		{
////			if(scrollDistance<0) {
////				onScrollListener.OnScrollUp(this, oldt, t);
////			}
////			else if (scrollDistance > 0) {
////				onScrollListener.OnScrollDown(this, oldt, t);
////			}
////		}
//
//        if(originHeight==0&getHeight()>0) originHeight = getHeight();
//        long currentTime = System.currentTimeMillis();
//        long during = currentTime - resizeTime;
//        if(during>1000)
//        {
//            resizeTime = currentTime;
//        }
//        else
//        {
//            float v = ((t-lastTop)*1000.0f/during);
////            Log.e("v",v+"");
////            if(v>7000)
////            {
////                resizeTime = currentTime;
////                lastTop = t;
////                scrollChange(oldt,oldt+MAX_SCROLL_DISTANCE*SCROLL_DIVISOR);
////                if(canResizeHeight)this.requestLayout();
////
////            }
////            else if(v<-7000)
////            {
////                resizeTime = currentTime;
////                lastTop = t;
////                scrollChange(oldt,oldt-MAX_SCROLL_DISTANCE*SCROLL_DIVISOR);
////                if(canResizeHeight)this.requestLayout();
////            }
////            else
//                if(during>30)
//            {
//                resizeTime = currentTime;
//                lastTop = t;
//                scrollChange(oldt,t);
//                if(t>oldt)
//                {
//                    if(testPadding>=3)
//                        testPadding -= 3;
//                }
//                else
//                {
//                    testPadding += 3;
//                }
////                if(canResizeHeight)
//                {
////                    this.forceLayout();
////                    this.getParent().requestLayout();
////                    this.measure(this.getWidth(),this.getHeight());
//                    long startTime = System.currentTimeMillis();
////                    this.invalidate();
//                    int resizeHeight = originHeight - paddingBottom;
//                    afterResizeDraw = false;
//                    lastResizeHeight = resizeHeight;
////                    if(!denyResizeHeight)
////                    this.layout(0,0,getWidth(),resizeHeight);
////                    this.invalidate();
//                    long endTime = System.currentTimeMillis() - startTime;
////                    Log.e("layout during",endTime+"");
////                    this.requestLayout();
//
////                    int  width =View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
////                    int  height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
////                    this.measure(width,height);
////                    canResizeHeight = false;
//                }
//            }
//
//        }
////        if(during>300)
////        {
////            resizeTime = currentTime;
////            this.requestLayout();
////        }

        super.onScrollChanged(l, t, oldl, oldt);
    }

    private void resizeHeight()
    {

    }

    private boolean canChangeHeight = false;
    private boolean afterResizeDraw = false;
    private int lastResizeHeight = 0;

    private int paddingBottom = 0;
    private int testPadding = 0;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction())
        {

            case MotionEvent.ACTION_DOWN:
                canChangeHeight = true;
                break;
            case MotionEvent.ACTION_UP:
                canChangeHeight = false;
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface OnScrollListener
    {

        void OnScrollUp(WebView webView,int oldt,int top);
        void OnScrollDown(WebView webView,int oldt,int top);
    }

}
