package com.self.viewtoglrendering;

import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by justs on 2018/1/31.
 */

public interface ViewDrawInterface {


    public Canvas onDrawViewBegin();

    public void onDrawViewEnd();

}
