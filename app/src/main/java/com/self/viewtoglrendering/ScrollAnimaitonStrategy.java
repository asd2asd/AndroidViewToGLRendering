package com.self.viewtoglrendering;

import android.util.Log;

/**
 * Created by justs on 2018/1/21.
 */

public class ScrollAnimaitonStrategy implements IAnimationStrategy {
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
    public ScrollAnimaitonStrategy()
    {
        initParams();
    }

    @Override
    public void update(int shift)
    {
        this.shift = shift;
        this.cyclePeriod = Math.abs(shift)/500;
        Log.e("scroll",this.cyclePeriod+","+shift);
        if(this.cyclePeriod<=0) this.cyclePeriod = 1;
        start();
    }

    public void start() {
        startTime = System.currentTimeMillis();
        startX = (int) currentX;
        doing = true;
        transEnd = false;


        Log.e("anim","start");
    }

    /**
     * 设置起始位置坐标
     */
    private void initParams() {
//        int[] position = new int[2];
//        animationSurfaceView.getLocationInWindow(position);
//        this.startX = position[0];
//        this.startY = position[1];
        startX = 0;
        startY = 0;
    }

    /**
     * 根据当前时间计算小球的X/Y坐标。
     */
    public void compute() {
        long intervalTime = (System.currentTimeMillis() - startTime);
        float ratio = (float)intervalTime / cyclePeriod;
        if(ratio>=1.0f)
        {
            ratio = 1.0f;
            if(!transEnd)
            {
                Log.e("ratio","trans end");
                transEnd = true;
                Log.e("change",transEnd?"success":"not");
//                animationSurfaceView.onTransEnd(startX + shift);
            }
        }

        doing = true;
        int x = (int) (shift * ratio);

        currentX = startX + x;
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
