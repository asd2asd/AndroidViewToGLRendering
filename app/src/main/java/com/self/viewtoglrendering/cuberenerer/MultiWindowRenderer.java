package com.self.viewtoglrendering.cuberenerer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.self.viewtoglrendering.R;
import com.self.viewtoglrendering.ViewToGLRenderer;
import com.self.viewtoglrendering.gles.Drawable2d;
import com.self.viewtoglrendering.gles.ScaledDrawable2d;
import com.self.viewtoglrendering.gles.Sprite2d;
import com.self.viewtoglrendering.gles.Texture2dProgram;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by user on 3/15/15.
 */
public class MultiWindowRenderer extends ViewToGLRenderer implements View.OnTouchListener {


    private Context mContext;

    private int touchOffsetX;
    private int touchOffsetY;
    private int viewWidth,viewHeight;



    public MultiWindowRenderer(Context context) {
        mContext = context;

    }


    // Orthographic projection matrix.
    private float[] mDisplayProjectionMatrix = new float[16];

    private Texture2dProgram mTexProgram;
    private final ScaledDrawable2d mRectDrawable =
            new ScaledDrawable2d(Drawable2d.Prefab.RECTANGLE);
    private final Sprite2d mRect = new Sprite2d(mRectDrawable);

    private int mZoomPercent = 0;
    private int mSizePercent = 100;
    private int mRotatePercent = 0;
    private float mPosX, mPosY;


    private void onend()
    {

        if (mTexProgram != null) {
            mTexProgram.release();
            mTexProgram = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        onend();
        super.finalize();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);


//        // Set the background clear color to black.
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//
//        // Use culling to remove back faces.
//        GLES20.glEnable(GLES20.GL_CULL_FACE);
//
//        // Enable depth testing
//        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.
        // Enable texture mapping
        // GLES20.glEnable(GLES20.GL_TEXTURE_2D);


        // Create and configure the SurfaceTexture, which will receive frames from the
        // camera.  We set the textured rect's program to render from it.
        mTexProgram = new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT);

//        if (!newSurface) {
//            // This Surface was established on a previous run, so no surfaceChanged()
//            // message is forthcoming.  Finish the surface setup now.
//            //
//            // We could also just call this unconditionally, and perhaps do an unnecessary
//            // bit of reallocating if a surface-changed message arrives.
//            mWindowSurfaceWidth = mWindowSurface.getWidth();
//            mWindowSurfaceHeight = mWindowSurface.getHeight();
//            finishSurfaceSetup();
//        }

//        mCameraTexture.setOnFrameAvailableListener(this);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);


        mRect.setTexture(getGLSurfaceTexture());
        viewHeight = height;
        viewWidth = width;

        finishSurfaceSetup();
    }

    private void finishSurfaceSetup() {
        int width = viewWidth;
        int height = viewHeight;

        // Use full window.
        GLES20.glViewport(0, 0, width, height);

        // Simple orthographic projection, with (0,0) in lower-left corner.
        Matrix.orthoM(mDisplayProjectionMatrix, 0, 0, width, 0, height, -1, 1);

        // Default position is center of screen.
        mPosX = width / 2.0f;
        mPosY = height / 2.0f;

        updateGeometry();

        // Ready to go, start the camera.
    }


    /**
     * Updates the geometry of mRect, based on the size of the window and the current
     * values set by the UI.
     */
    private void updateGeometry() {
        int width = viewWidth;
        int height = viewHeight;

        int smallDim = Math.max(width, height);
        // Max scale is a bit larger than the screen, so we can show over-size.
        float scaled = smallDim * (mSizePercent / 100.0f) ;
        float cameraAspect = (float) viewWidth / viewHeight;
        int newWidth = Math.round(scaled * cameraAspect);
        int newHeight = Math.round(scaled);

        float zoomFactor = 1.0f - (mZoomPercent / 100.0f);
        int rotAngle = Math.round(360 * (mRotatePercent / 100.0f));

        mRect.setScale(newWidth, newHeight);
        mRect.setPosition(mPosX, mPosY);
        mRect.setRotation(rotAngle);
        mRectDrawable.setScale(zoomFactor);

    }


    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mRect.draw(mTexProgram, mDisplayProjectionMatrix);
    }



    private void setZoom(int percent) {
        mZoomPercent = percent;
        updateGeometry();
    }

    private void setSize(int percent) {
        mSizePercent = percent;
        updateGeometry();
    }

    private void setRotate(int percent) {
        mRotatePercent = percent;
        updateGeometry();
    }

    private void setPosition(float x, float y) {
        mPosX = x;
        mPosY = viewHeight - y;   // GLES is upside-down
        updateGeometry();
    }



    private float downX,downY;
    private float downPosX,downPosY;
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN )
        {
            downX = event.getX();
            downY = event.getY();
            downPosX = mPosX;
            downPosY = mPosY;
        }
        else if(action == MotionEvent.ACTION_MOVE)
        {

            setPosition(event.getX()-downX + downPosX,mPosY);
        }
        else if(action == MotionEvent.ACTION_UP|| action == MotionEvent.ACTION_CANCEL)
        {
            setPosition(viewWidth/2,(int)mPosY);
        }
        return true;
    }
}
