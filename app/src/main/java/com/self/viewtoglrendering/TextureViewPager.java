package com.self.viewtoglrendering;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by justs on 2018/6/17.
 */

public class TextureViewPager extends LinearLayout implements View.OnTouchListener,CubeSurfaceView.CubeSurfaceEventListener {

    private CubeSurfaceView cubeSurfaceView;
    private int currentScrollX;
    private int zoom;
    private int displayArea;
    private int textureViewCount;
    private PagerAdapter adapter;

    public TextureViewPager(Context context) {
        super(context);
        init(context);
    }

    public TextureViewPager(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextureViewPager(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        cubeSurfaceView = new CubeSurfaceView(context);
        cubeSurfaceView.init();
        this.addView(cubeSurfaceView);
        cubeSurfaceView.setOnTouchListener(this);
        zoom = 50;
        displayArea = 10;
        currentScrollX = 0;
    }

    public void setAdapter(PagerAdapter adapter)
    {
        this.adapter = adapter;
    }

    public void pause()
    {
        cubeSurfaceView.pause();
    }

    public void resume()
    {
        cubeSurfaceView.resume(this);
    }


    private int getItemCount()
    {
        return adapter.getCount();
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;

        int itemWidth = getItemWidth();
        textureViewCount = (int) Math.ceil(1.0f * cubeSurfaceView.getWidth() / itemWidth) + 1;

        int offset = textureViewCount - cubeSurfaceView.getRectCount();
        if (offset > 0)
            for (int i = 0; i < offset; i++) {
                cubeSurfaceView.addRect();

//            drawTextureViewList.get(i).setPreviewTexture(cubeSurfaceView.getRectSurfaceTexture(i));
            }
        else if(offset<0) {
            for (int i = 0; i < -offset; i++) {
                cubeSurfaceView.subtractRect();
            }

        }

        for (int i = 0; i < textureViewCount; i++) {
            cubeSurfaceView.setZoom(i, zoom);
        }
    }

    public int getDisplayArea() {
        return displayArea;
    }

    public void setDisplayArea(int displayArea) {
        this.displayArea = displayArea;

        for(int i=0;i<textureViewCount;i++)
        {
            cubeSurfaceView.setDisplayArea(i,displayArea);
        }
    }

    public void setScrollX(int scrollX)
    {
        this.currentScrollX = scrollX;
        int firstVisibleItemIndex = getFirstVisibleItemIndex();
        int lastVisibleItemIndex = getLastVisibleItemIndex();
        int itemWidth = getItemWidth();
        int startX = itemWidth/2;
        if(scrollX<0) startX += scrollX;
        else startX += scrollX%itemWidth;

        for(int i=firstVisibleItemIndex,texIndex =0;i<=lastVisibleItemIndex;i++,texIndex++)
        {
            ((CubeSurfaceView.DrawTextureView)adapter.instantiateItem(this,i)).
                setPreviewTexture(cubeSurfaceView.getRectSurfaceTexture(texIndex));
        }

        for(int i=0,j=firstVisibleItemIndex;i<textureViewCount;i++,j++)
        {

            cubeSurfaceView.setPosition(i,itemWidth*i+startX,
                    cubeSurfaceView.getHeight()/2);
        }

    }

    public int getScrollXCorrect()
    {
        return currentScrollX;
    }

    private int getItemWidth()
    {
        return (int) (cubeSurfaceView.getWidth()*(100 - zoom)/100.0f);
    }

    private int getFirstVisibleItemIndex()
    {
        return findItemIndexAtScrollX(currentScrollX);
    }

    private int getLastVisibleItemIndex()
    {
        return findItemIndexAtScrollX(currentScrollX + cubeSurfaceView.getWidth());
    }

    private int findItemIndexAtScrollX(int scrollX)
    {
        int adapterCount = adapter.getCount();
        int index = scrollX/getItemWidth();
        if(index<0) index = 0;
        else if(index>=adapterCount) index = adapterCount - 1;
        return index;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                setScrollX((int)x);
//                cubeSurfaceView2.setPosition(0,(int)x,(int)y);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void OnSurfaceAvaiable() {

        setZoom(zoom);
        setDisplayArea(displayArea);
        setScrollX(currentScrollX);
    }


    /**
     * 内部类，用来辅助处理滑过一半距离或者快速或者的动画效果
     * */
    class ScrollDistance {
        public int startX;
        public int distanceX;
        public long startTime;
        public long currentX;
        public long duration = 250;
        public boolean isFinish = false;

        public ScrollDistance(int startX, int distanceX) {
            this.startX = startX;
            this.isFinish = false;
            this.duration = 250;
            this.startTime = SystemClock.uptimeMillis();
            this.distanceX = distanceX;
        }

        /**
         * 计算一下当前的运行状态
         *
         * @return true：表示运行结束; false：表示还在运行
         */
        public boolean computeScrollOffset() {
            if (isFinish) {
                return isFinish;
            }
            // 计算一下滑动运行了多久时间
            long passTime = SystemClock.uptimeMillis() - startTime;

            if (passTime < duration) {
                currentX = startX + distanceX * passTime / duration;
            } else {
                currentX = startX + distanceX;
                isFinish = true;
            }
            return false;
        }
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        /*
        * 必须总是实现onDown()方法，并返回true。这一步是必须的，因为所有的gestures都是从
        * onDown()开始的。如果你在onDown()里面返回false，系统会认为你想要忽略后续的gesture,
        * 那么GestureDetector.OnGestureListener的其他回调方法就不会被执行到了
        * */
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        /*
        * onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        * 在屏幕上拖动事件。无论是用手拖动view，或者是以抛的动作滚动，都会多次触发,这个方法
        * 在ACTION_MOVE动作发生时就会触发
        *
        * e1表示最开始触发本次这一系列Event事件的那个ACTION_DOWN事件，
        * e2表示触发本次Event事件的那个ACTION_MOVE事件，
        * distanceX、distanceY分别表示从上一次调用onScroll调用到这一次onScroll调用在
        * x和y方向上滑动的距离。这里需要稍微留意的是，distanceX、distanceY的正负并不是像之前
        * ViewGroup里坐标显示的正负那样，而是向左滑动值distanceX为正，向右滑动值为distanceX负
        * 滑屏：手指触动屏幕后，稍微滑动后立即松开
        *   onDown----->onScroll---->onScroll---->onScroll---->………----->onFling
        * */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            super.onScroll(e1, e2, distanceX, distanceY);
            int x = getScrollXCorrect();
            //第一页不能向右滑动，最后一页不能向左滑动，直接返回
            if (x < 0 || x > getItemWidth() * (adapter.getCount() - 1))
                return false;
            scrollBy((int) distanceX, 0);

            //返回false可以继续处理后续交互，比如滑动超过一半距离
            return false;
        }

        /*
         * onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) ：滑屏，用户按下
         * 触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
         * 参数解释：
         * e1：第1个ACTION_DOWN MotionEvent
         * e2：最后一个ACTION_MOVE MotionEvent
         * velocityX：X轴上的移动速度，像素/秒
         * velocityY：Y轴上的移动速度，像素/秒
         * */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            int position;
//            if (e1.getX() < e2.getX()) {
//                position = mCurItem - 1;
//            } else {
//                position = mCurItem + 1;
//            }
//
//            switchToItem(position);

            return true;
        }
    }

}
