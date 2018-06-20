package com.self.viewtoglrendering;

/**
 * Created by justs on 2018/1/21.
 */

public interface IAnimationStrategy {

    void compute();

    boolean doing();

    void start();

    double getX();

    double getY();

    void cancel();

    void updateFling(float velocityX,int limitDistance);
    void update(int shift,int cyclePeriod);
    void updateSwitchItem(float velocityX,int distance);

}
