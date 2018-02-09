package com.self.viewtoglrendering;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.TextureView;

/**
 * Created by jose on 2018/2/9.
 */

public class View2TextureView  extends TextureView implements TextureView.SurfaceTextureListener{
    public View2TextureView(Context context) {
        super(context);


        setSurfaceTextureListener(this);
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
