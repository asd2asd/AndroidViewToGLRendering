package com.self.viewtoglrendering;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhenghao.qi
 * @version 1.0
 * @time 2015年11月09日15:24:15
 */
//public class AnimationSurfaceView extends TextureView implements TextureView.SurfaceTextureListener, Runnable {
//
//    private static final String TAG = "AnimationSurfaceView";
//    private static final long REFRESH_INTERVAL_TIME = 1l;//每间隔15ms刷一帧
//    private Surface mSurfaceHolder;
//    private List<Bitmap> mBitmapList;                               //动画图标
//    private IAnimationStrategy mIAnimationStrategy;       //动画执行算法策略
//    private OnStausChangedListener mStausChangedListener; //动画状态改变监听事件
//
//    private int marginLeft;
//    private int marginTop;
//
//    private boolean isSurfaceDestoryed = true;            //默认未创建，相当于Destory
//    private Thread mThread;                               //动画刷新线程
//
//    private boolean visible;
//
//    public AnimationSurfaceView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        init();
//    }
//
//    public AnimationSurfaceView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//    }
//
//    public AnimationSurfaceView(Context context) {
//        super(context);
//        init();
//    }
//
//    //初始化
//    private void init() {
//        visible = true;
////        mSurfaceHolder = getHolder();
////        mSurfaceHolder.addCallback(this);
////        setZOrderOnTop(true);//设置画布背景透明
////        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
//
//
//        setOpaque(false);//设置背景透明，记住这里是[是否不透明]
//        this.setSurfaceTextureListener(this);
//    }
//
////    @Override
////    public void surfaceCreated(SurfaceHolder holder) {
////        isSurfaceDestoryed = false;
////    }
////
////    @Override
////    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
////
////    }
////
////    @Override
////    public void surfaceDestroyed(SurfaceHolder holder) {
////        isSurfaceDestoryed = true;
////        if (mIAnimationStrategy != null)//如果surfaceView创建后，没有执行setStrategy,就被销毁，会空指针异常
////            mIAnimationStrategy.cancel();
////    }
//
//
//    @Override
//    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//        isSurfaceDestoryed = false;
//        mSurfaceHolder = new Surface(surface);
//        mThread = new Thread(this);
//        startAnimation();
//    }
//
//    @Override
//    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//
//    }
//
//    @Override
//    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//        isSurfaceDestoryed = true;
//        if (mIAnimationStrategy != null)//如果surfaceView创建后，没有执行setStrategy,就被销毁，会空指针异常
//            mIAnimationStrategy.cancel();
//        return false;
//    }
//
//    @Override
//    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//
//    }
//
//    //执行
//    private void executeAnimationStrategy() {
//        Canvas canvas = null;
//
//        Paint tempPaint = new Paint();
//        tempPaint.setAntiAlias(true);
//        tempPaint.setColor(Color.TRANSPARENT);
//
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setColor(Color.CYAN);
//        Rect drawRect = new Rect();
//        if (mStausChangedListener != null) {
//            mStausChangedListener.onAnimationStart(this);
//        }
//        mIAnimationStrategy.start();
//        while (mIAnimationStrategy.doing()) {
//            try {
//                if(null==mBitmapList) continue;
//                mIAnimationStrategy.compute();
//
//                canvas = mSurfaceHolder.lockCanvas(drawRect);
//                canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);// 设置画布的背景为透明
//
//                if(visible) {
//                    // 绘上新图区域
//                    float x = (float) mIAnimationStrategy.getX() + marginLeft;
//                    float y = (float) mIAnimationStrategy.getY() + marginTop;
//
//                    int lastBitmapWidth = 0;
//                    for (int i = 0; i < mBitmapList.size(); i++) {
//                        Bitmap mBitmap = mBitmapList.get(i);
//                        canvas.drawRect(x + lastBitmapWidth, y, x + lastBitmapWidth + mBitmap.getWidth(), y + mBitmap.getHeight(), tempPaint);
//                        canvas.drawBitmap(mBitmap, x + lastBitmapWidth, y, paint);
//                        lastBitmapWidth += mBitmap.getWidth();
//                    }
//                }
//
//                mSurfaceHolder.unlockCanvasAndPost(canvas);
//                Thread.sleep(REFRESH_INTERVAL_TIME);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        // clear屏幕内容
//        if (isSurfaceDestoryed == false) {// 如果直接按Home键回到桌面，这时候SurfaceView已经被销毁了，lockCanvas会返回为null。
//            canvas = mSurfaceHolder.lockCanvas(drawRect);
//            canvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
//            mSurfaceHolder.unlockCanvasAndPost(canvas);
//        }
//
//        if (mStausChangedListener != null) {
//            mStausChangedListener.onAnimationEnd(this);
//        }
//    }
//
//    /**
//     * 开始播放动画
//     */
//    public void startAnimation() {
//        if (mThread.getState() == Thread.State.NEW) {
//            mThread.start();
//        } else if (mThread.getState() == Thread.State.TERMINATED) {
//            mThread = new Thread(this);
//            mThread.start();
//        }
//    }
//
//    /**
//     * 是否正在播放动画
//     */
//    public boolean isShow() {
//        return mIAnimationStrategy.doing();
//    }
//
//    /**
//     * 结束动画
//     */
//    public void endAnimation() {
//        mIAnimationStrategy.cancel();
//    }
//
//    /**
//     * 设置要播放动画的bitmap
//     *
//     * @param bitmaps
//     */
//    public void setIcon(List<Bitmap> bitmaps) {
//        this.mBitmapList = bitmaps;
//    }
//
//    /**
//     * 获取要播放动画的bitmap
//     */
//    public Bitmap getIcon(int index) {
//        return mBitmapList.get(index);
//    }
//
//    public void addView(Bitmap bitmap)
//    {
//        if(null==mBitmapList) mBitmapList = new ArrayList<>();
//        mBitmapList.add(bitmap);
//    }
//
//    @Override
//    public void scrollTo(int x, int y) {
//        int distanceX = (int) (-x - mIAnimationStrategy.getX());
//        mIAnimationStrategy.update(distanceX);
//    }
//
//    @Override
//    public void scrollBy(int x, int y) {
////        mIAnimationStrategy.update(-x);
//    }
//
//
//    void onTransEnd(int currentX)
//    {
//
//    }
//
//    /**
//     * 设置margin left 像素
//     *
//     * @param marginLeftPx
//     */
//    public void setMarginLeft(int marginLeftPx) {
//        this.marginLeft = marginLeftPx;
//    }
//
//    /**
//     * 设置margin left 像素
//     *
//     * @param marginTopPx
//     */
//    public void setMarginTop(int marginTopPx) {
//        this.marginTop = marginTopPx;
//    }
//
//    /**
//     * 设置动画状态改变监听器
//     */
//    public void setOnAnimationStausChangedListener(OnStausChangedListener listener) {
//        this.mStausChangedListener = listener;
//    }
//
//    @Override
//    public void run() {
//        executeAnimationStrategy();
//    }
//
//
//    public interface OnStausChangedListener {
//        void onAnimationStart(AnimationSurfaceView view);
//
//        void onAnimationEnd(AnimationSurfaceView view);
//    }
//
//    /**
//     * 设置动画执行算法策略
//     *
//     * @param strategy
//     */
//    public void setStrategy(IAnimationStrategy strategy) {
//        this.mIAnimationStrategy = strategy;
//    }
//
//
//    public int getScrollXCorrect()
//    {
//        return (int) -mIAnimationStrategy.getX();
//    }
//
//    @Override
//    public void setVisibility(int visibility) {
//        if(visibility==VISIBLE)
//        {
//            visible = true;
//        }
//        else
//        {
//            visible = false;
//        }
//    }
//}
