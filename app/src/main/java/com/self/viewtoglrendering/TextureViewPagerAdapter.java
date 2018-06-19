package com.self.viewtoglrendering;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 2018/6/19.
 */

public class TextureViewPagerAdapter extends PagerAdapter {

    List<CubeSurfaceView.DrawTextureView> viewList;


    public  TextureViewPagerAdapter()
    {
        viewList = new ArrayList<>();
    }

    public void addView(CubeSurfaceView.DrawTextureView view)
    {
        viewList.add(view);
    }
    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return viewList.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return false;
    }
}
