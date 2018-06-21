package com.self.viewtoglrendering;
import android.os.Handler;

public class BrowserHandler {
    public static BrowserHandler sInstance;
    private Handler mHandler;
    public static BrowserHandler getInstance(){
        if (sInstance == null){
            sInstance = new BrowserHandler();
        }
        return sInstance;
    }

    public void handlerPostDelayed(Runnable runnable , long time) {
        mHandler.postDelayed(runnable, time);
    }

    public void handlerPost(Runnable runnable)
    {
        handlerPostDelayed(runnable,0);
    }

    public void init()
    {

        if (mHandler == null){
            mHandler = new Handler();
        }
    }
}
