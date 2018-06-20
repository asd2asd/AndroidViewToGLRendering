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

    void update(float velocity,int limitDistance);
    void update(int shift,int cyclePeriod);

}
