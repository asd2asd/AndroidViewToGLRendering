package com.self.viewtoglrendering;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by justs on 2018/6/17.
 */

public class TextureViewPager extends LinearLayout {

    private CubeSurfaceView cubeSurfaceView;
    private List<CubeSurfaceView.DrawTextureView> drawTextureViewList;
    private int currentScrollX;

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
        drawTextureViewList = new ArrayList<>();
        this.addView(cubeSurfaceView);
    }

    public void addTextureDrawView(CubeSurfaceView.DrawTextureView drawTextureView)
    {
        drawTextureViewList.add(drawTextureView);
    }

    public void pause()
    {
        cubeSurfaceView.pause();
    }

    public void resume()
    {
        cubeSurfaceView.resume(drawTextureViewList.size(), new CubeSurfaceView.CubeSurfaceEventListener() {
            @Override
            public void OnSurfaceAvaiable() {
                int width = (int) (cubeSurfaceView.getWidth()*0.5);
                int startX = cubeSurfaceView.getWidth()/2;
                for(int i=0;i<drawTextureViewList.size();i++)
                {
                    drawTextureViewList.get(i).setPreviewTexture(cubeSurfaceView.getRectSurfaceTexture(i));
                    cubeSurfaceView.setZoom(i,50);
                    cubeSurfaceView.setDisplayArea(i,20);

                    cubeSurfaceView.setPosition(i,width*i+startX,cubeSurfaceView.getHeight()/2);
                }
            }
        });

    }

}
