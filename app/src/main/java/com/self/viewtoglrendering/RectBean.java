package com.self.viewtoglrendering;

import android.graphics.SurfaceTexture;
import android.view.Surface;

import com.self.viewtoglrendering.gles.Drawable2d;
import com.self.viewtoglrendering.gles.ScaledDrawable2d;
import com.self.viewtoglrendering.gles.Sprite2d;

/**
 * Created by justs on 2018/6/17.
 */

public class RectBean {


    private static final int DEFAULT_ZOOM_PERCENT = 0;      // 0-100
    private static final int DEFAULT_DISPLAY_AREA_PERCENT = 20;     // 0-100
    private static final int DEFAULT_ROTATE_PERCENT = 0;    // 0-100


    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private ScaledDrawable2d scaledDrawable2d;
    private Sprite2d rect;
    private int zoomPercent = DEFAULT_ZOOM_PERCENT;
    private int displayAreaPercent = DEFAULT_DISPLAY_AREA_PERCENT;
    private int rotatePercent = DEFAULT_ROTATE_PERCENT;
    private float posX, posY;
    private boolean enable;
    private int drawViewIndex;

    public RectBean()
    {
        scaledDrawable2d =
                new ScaledDrawable2d(Drawable2d.Prefab.RECTANGLE);
        rect = new Sprite2d(scaledDrawable2d);
        drawViewIndex = -1;
    }

    @Override
    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }

    public void release()
    {
        if(null!=surface)
            surface.release();
        if(null!=surfaceTexture) {
            surfaceTexture.release();
//            surfaceTexture.releaseTexImage();
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        if(this.surfaceTexture!=surfaceTexture)
        {
            release();
            this.surfaceTexture = surfaceTexture;
            setSurface(new Surface(surfaceTexture));
        }
    }

    public ScaledDrawable2d getScaledDrawable2d() {
        return scaledDrawable2d;
    }

    public void setScaledDrawable2d(ScaledDrawable2d scaledDrawable2d) {
        this.scaledDrawable2d = scaledDrawable2d;
    }

    public Sprite2d getRect() {
        return rect;
    }

    public void setRect(Sprite2d rect) {
        this.rect = rect;
    }

    public int getZoomPercent() {
        return zoomPercent;
    }

    public void setZoomPercent(int zoomPercent) {
        this.zoomPercent = zoomPercent;
    }

    public int getDisplayAreaPercent() {
        return displayAreaPercent;
    }

    public void setDisplayAreaPercent(int displayAreaPercent) {
        this.displayAreaPercent = displayAreaPercent;
    }

    public int getRotatePercent() {
        return rotatePercent;
    }

    public void setRotatePercent(int rotatePercent) {
        this.rotatePercent = rotatePercent;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getDrawViewIndex() {
        return drawViewIndex;
    }

    public void setDrawViewIndex(int drawViewIndex) {
        this.drawViewIndex = drawViewIndex;
    }

    public Surface getSurface() {
        return surface;
    }

    private void setSurface(Surface surface) {
        this.surface = surface;
    }
}
