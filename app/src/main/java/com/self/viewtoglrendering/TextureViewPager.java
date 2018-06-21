package com.self.viewtoglrendering;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by justs on 2018/6/17.
 */

public class TextureViewPager extends LinearLayout implements View.OnTouchListener,CubeSurfaceView.CubeSurfaceEventListener {

    private CubeSurfaceView cubeSurfaceView;
    private int zoom;
    private int displayArea;
    private int textureViewCount;
    /**
     * 当前页面的下标，从0开始
     */
    private int mCurItem;
    /**
     * 按下屏幕的X轴的坐标
     */
    private int firstX;
    /**
     * 手势处理探测器两边
     */
    private GestureDetector mDetector;

    private IAnimationStrategy animationStrategy;
    /**
     * 简单控制页面变换回调的变量
     */
    private OnPageChangeListener onPageChangeListener = null;
    /**
     * 接口以实现回调，可用于实现滑动指示条的功能
     */
    public interface OnPageChangeListener {
        /**
         * 页面变换是回调这个方法
         *
         * @param position 处于屏幕的Fragment的标号，从0开始
         */
        void onPageSelected(int position);
    }



    /**
     * 适配器变量
     */
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

        mDetector = new GestureDetector(getContext(), new GestureListener());
        cubeSurfaceView.setOnTouchListener(this);
        zoom = 0;
        displayArea = 10;
//        currentScrollX = 0;
        animationStrategy = new ScrollAnimaitonStrategy(0,0);
    }


    @Override
    public void OnSurfaceAvaiable() {

        setZoom(zoom);
        setDisplayArea(displayArea);
//        setScrollX(getScrollXCorrect());
        applyScrollX();
    }


    public void setAdapter(PagerAdapter adapter)
    {
        this.adapter = adapter;
    }

    public void pause()
    {
        for(int i=0;i<adapter.getCount();i++)
        {
            ((CubeSurfaceView.DrawTextureView)adapter.instantiateItem(this,i)).updatePreviewSurface(null);
        }
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
        int surfaceWidth = cubeSurfaceView.getWidth();
        textureViewCount = (int) Math.ceil(1.0f * surfaceWidth / itemWidth) + 1;

        int offset = textureViewCount - cubeSurfaceView.getRectCount();
        if (offset > 0)
            for (int i = 0; i < offset; i++) {
                cubeSurfaceView.addRect();

//            drawTextureViewList.get(i).updatePreviewSurface(cubeSurfaceView.getRectSurface(i));
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

    private int getMinScrollX()
    {
        return  -(cubeSurfaceView.getWidth() - getItemWidth())/2;
    }

    private int getMaxScrollX()
    {
        return getItemWidth() * (adapter.getCount()-1) + getMinScrollX();
    }

    private void applyScrollX()
    {
        int itemWidth = getItemWidth();
        int currentScrollX = getScrollXCorrect();
        int firstVisibleItemIndex = getFirstVisibleItemIndex();
        int lastVisibleItemIndex = getLastVisibleItemIndex();
        int startX = itemWidth/2;
        if(currentScrollX<0) startX -= currentScrollX;
        else startX -= currentScrollX%itemWidth;
        int textureViewStartIndex = firstVisibleItemIndex%textureViewCount;


        for(int i=0;i<adapter.getCount();i++)
        {

                ((CubeSurfaceView.DrawTextureView)adapter.instantiateItem(this,i)).updatePreviewSurface(null);
        }

        for(int i=0,j=firstVisibleItemIndex,textureIndex = textureViewStartIndex;i<textureViewCount;i++,j++) {
            if (textureIndex >= textureViewCount) textureIndex = 0;
            if (j <= lastVisibleItemIndex) {
//                int oldDrawViewIndex = cubeSurfaceView.getDrawViewIndex(textureIndex);
//                if(oldDrawViewIndex!=j)
                {
//                    if(oldDrawViewIndex>=0)
//                        ((CubeSurfaceView.DrawTextureView) adapter.instantiateItem(this, oldDrawViewIndex)).
//                                updatePreviewSurface(null);
                    ((CubeSurfaceView.DrawTextureView) adapter.instantiateItem(this, j)).
                            updatePreviewSurface(cubeSurfaceView.getRectSurface(textureIndex));
                    cubeSurfaceView.updateDrawViewIndex(textureIndex,j);

                }
                cubeSurfaceView.setRectEnable(textureIndex, true);
                cubeSurfaceView.setPosition(textureIndex, itemWidth * i + startX,
                        cubeSurfaceView.getHeight()/2);
            } else {

//                ((NewGlWebView) adapter.instantiateItem(this, j)).releaseSurface();
                cubeSurfaceView.setRectEnable(textureIndex, false);
            }

            textureIndex++;
            if (textureIndex == textureViewStartIndex) break;
        }

//        for(int i=0;i<adapter.getCount();i++)
//        {
//            if(!drawList.contains(adapter.instantiateItem(this,i)))
////                ((NewGlWebView)adapter.instantiateItem(this,i)).releaseSurface();
//                ((CubeSurfaceView.DrawTextureView)adapter.instantiateItem(this,i)).drawOpenGlTexture(false);
//        }
    }

    public int getScrollXCorrect()
    {
        return (int) animationStrategy.getX();
    }

    private int getItemWidth()
    {
        return (int) (cubeSurfaceView.getWidth()*(100 - zoom)/100.0f);
    }

    private int getFirstVisibleItemIndex()
    {
        return findItemIndexAtScrollX(getScrollXCorrect());
    }

    private int getLastVisibleItemIndex()
    {
        return findItemIndexAtScrollX(getScrollXCorrect() + cubeSurfaceView.getWidth());
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
        // 使用工具来解析触摸事件
        boolean result = mDetector.onTouchEvent(event);
        //事件被处理就直接返回
        if (result) return result;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int position;
                int secondX = (int) event.getX();
                if (secondX - firstX > getItemWidth() / 2) {
                    position = mCurItem - 1;
                } else if (firstX - secondX > getItemWidth() / 2) {
                    position = mCurItem + 1;
                } else {
                    position = mCurItem;
                }

//                switchToItem(position);

                break;
            case MotionEvent.ACTION_DOWN:
                firstX = (int) event.getX();
                break;
        }
        return true;
    }

    @Override
    public void scrollTo(int x, int y) {

        int minScrollX = getMinScrollX();
        int maxScrollX = getMaxScrollX();
        if(x<minScrollX) x = minScrollX;
        else if(x>maxScrollX) x = maxScrollX;
        int distanceX = (int) (x - animationStrategy.getX());
        animationStrategy.update(distanceX,1);
    }

    @Override
    public void scrollBy(int x, int y) {
//        setScrollX(currentScrollX+x);
        scrollTo((int) (x + animationStrategy.getX()),y);
    }

    /**
     * 惯性滑行
     * @param velocityX 手松开是瞬时速度
     */

    public void flingTo(float velocityX)
    {
        int limit = velocityX>=0?getMaxScrollX():getMinScrollX();
        limit = (int) (limit - animationStrategy.getX());

        animationStrategy.updateFling(velocityX,limit);
    }

    @Override
    public void BeforeDrawFrame() {
//        for(int i=0;i<adapter.getCount();i++)
//        {
//
//            ((NewGlWebView)adapter.instantiateItem(this,i)).postInvalidate();
//        }
        int oldScrollX = (int) animationStrategy.getX();
        animationStrategy.compute();
        if(oldScrollX!=animationStrategy.getX())
            applyScrollX();
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
//            if (x < 0 || x > getItemWidth() * (adapter.getCount() - 1))
//                return false;
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
            int position;
            if (e1.getX() < e2.getX()) {
                position = mCurItem - 1;
            } else {
                position = mCurItem + 1;
            }

//            switchToItem(position,-velocityX/1000);
            flingTo(- velocityX/1000);


            return true;
        }
    }




    public void switchToItem(int position,float velocityX)
    {


        position = position < 0 ? 0 :
                (position >= adapter.getCount() ? adapter.getCount() - 1 : position);
        if (position != mCurItem) {
            mCurItem = position;
            //页面变换，在设置变换监听的时候回调该函数
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageSelected(mCurItem);
            }
        }

        int itemWidth = getItemWidth();
        int sufaceWidth = cubeSurfaceView.getWidth();
        int itemOffset = (sufaceWidth - itemWidth)/2;
        int newScrollX = position*itemWidth-itemOffset;
        int scrollDistance = (int) (newScrollX - animationStrategy.getX());
        animationStrategy.updateSwitchItem(velocityX,scrollDistance);


//        mScrollDistance = new ScrollDistance(getScrollXCorrect(),
//                mCurItem * getItemWidth() - getScrollXCorrect());
//        computeViewScroll();


    }


//    public void computeViewScroll() {
//        //调用invalidate()会回调此方法，在这里实现动画效果
//        if (mScrollDistance != null && !mScrollDistance.computeScrollOffset()) {
//            int newX = (int) mScrollDistance.currentX;
//            Log.e("acjiji", "computeScroll" + newX);
//            scrollTo(newX, 0);
//            // 再次刷新
////            refresh();
//            computeViewScroll();
//        }
//    }

}
