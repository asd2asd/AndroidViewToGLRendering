package com.self.viewtoglrendering;

import android.util.Log;

/**
 * Created by justs on 2018/1/21.
 */

public class ScrollAnimaitonStrategy implements IAnimationStrategy {
    private static final float DECELERATION = 0.01f;
    /**
     * 起始X坐标
     */
    private int startX;
    /**
     * 起始Y坐标
     */
    private int startY;
    /**
     * 起始点到终点的Y轴位移。
     */
    private int shift;
    /**
     * X Y坐标。
     */
    private double currentX, currentY;
    /**
     * 动画开始时间。
     */
    private long startTime;
    /**
     * 循环时间
     */
    private long cyclePeriod;
    /**
     * 动画正在进行时值为true，反之为false。
     */
    private boolean doing;

    /**
     * 瞬时加速度
     */
    private float velocityX;

    /**
     * 进行动画展示的view
     */
//    private AnimationSurfaceView animationSurfaceView;

    private boolean transEnd;


//    public ScrollAnimaitonStrategy(AnimationSurfaceView animationSurfaceView, int shift, long cyclePeriod) {
//        this.animationSurfaceView = animationSurfaceView;
//        this.shift = shift;
//        this.cyclePeriod = cyclePeriod;
//        initParams();
//    }
    public ScrollAnimaitonStrategy(int x,int y)
    {
        initParams(x,y);
    }

    /**
     * 开始滑动动画
     * @param velocityX 瞬时速度
     * @param limitDistance 最大允许的滑动距离
     */
    @Override
    public void updateFling(float velocityX,int limitDistance)
    {

        limitDistance = Math.abs(limitDistance);
        float absV = Math.abs(velocityX);
        this.velocityX = velocityX;
        cyclePeriod = (int) (absV / DECELERATION);
        shift = (int) (absV*absV/(2*DECELERATION));
        if(shift>limitDistance) shift = limitDistance;
        shift = (velocityX>=0?1:-1) *shift;


//        Log.e("scroll",this.cyclePeriod+","+shift);
        if(this.cyclePeriod<=0) this.cyclePeriod = 1;
        start();
    }

    @Override
    public void update(int shift, int cyclePeriod) {
        this.cyclePeriod = cyclePeriod;
        this.shift = shift;
        start();
    }

    @Override
    public void updateSwitchItem(float velocityX, int distance) {

        float absV = (float) Math.sqrt(Math.abs(distance)*2*DECELERATION);
        this.velocityX = (distance>=0?1:-1) * absV;
        cyclePeriod = (int) (absV / DECELERATION);
        shift = distance;
        start();
    }

    public void start() {
        startTime = System.currentTimeMillis();
        startX = (int) currentX;
        doing = true;
        transEnd = false;


//        Log.e("anim","start");
    }

    /**
     * 设置起始位置坐标
     */
    private void initParams(int x,int y) {
//        int[] position = new int[2];
//        animationSurfaceView.getLocationInWindow(position);
//        this.startX = position[0];
//        this.startY = position[1];
        startX = x;
        startY = y;
        transEnd = true;
    }


    /**
     * 根据当前时间计算小球的X/Y坐标。
     */
    public void compute() {
        if(transEnd) return;
        long intervalTime = (System.currentTimeMillis() - startTime);
        int dist;
        if(cyclePeriod>0) {
            if (cyclePeriod == 1) {
                dist = shift;
            } else {
                float absV = Math.abs(velocityX);
                dist = (int)((velocityX>=0?1:-1)*
                        (absV * intervalTime - DECELERATION * intervalTime * intervalTime / 2));

            }
//            Log.e("asd","shift:"+shift+","+dist+"");
            boolean isEnd = false;
            if(intervalTime>=cyclePeriod)
            {
                isEnd = true;
            }
            else if (Math.abs(dist) >= Math.abs(shift)) {
                isEnd = true;
            }
            if(isEnd)
            {
                dist = shift;
                if (!transEnd) {
//                    Log.e("ratio", "trans end");
                    transEnd = true;
//                    Log.e("change", transEnd ? "success" : "not");
//                animationSurfaceView.onTransEnd(startX + shift);
                }
            }
            doing = true;
            currentX = startX + dist;
        }

//        float ratio = (float)intervalTime / cyclePeriod;
//        if(ratio>=1.0f)
//        {
//            ratio = 1.0f;
//            if(!transEnd)
//            {
//                Log.e("ratio","trans end");
//                transEnd = true;
//                Log.e("change",transEnd?"success":"not");
////                animationSurfaceView.onTransEnd(startX + shift);
//            }
//        }
//
//        doing = true;
//        int x = (int) (shift * ratio);
//
//        currentX = startX + x;
    }

//    public void compute() {
//        long intervalTime = (System.currentTimeMillis() - startTime) % cyclePeriod;
//        double angle = Math.toRadians(360 * 1.0d * intervalTime / cyclePeriod);
//        int x = (int) (shift / 2 * Math.cos(angle));
//        x = Math.abs(x - shift/2);
//        currentX = startX + x;
//        doing = true;
//    }

    @Override
    public boolean doing() {
        return doing;
    }

    public double getX() {
        return currentX;
    }

    public double getY() {
        return currentY;
    }


    public void cancel() {
        doing = false;
    }
}
