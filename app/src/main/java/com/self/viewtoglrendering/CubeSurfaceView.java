package com.self.viewtoglrendering;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.self.viewtoglrendering.gles.EglCore;
import com.self.viewtoglrendering.gles.GlUtil;
import com.self.viewtoglrendering.gles.Sprite2d;
import com.self.viewtoglrendering.gles.Texture2dProgram;
import com.self.viewtoglrendering.gles.WindowSurface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 2018/6/16.
 */

public class CubeSurfaceView extends SurfaceView implements SurfaceHolder.Callback {


    private static final String TAG = "CubeSurfaceview";

    private static final int DRAW_FRAME_DELAY = 1000/60;



    // The holder for our SurfaceView.  The Surface can outlive the Activity (e.g. when
    // the screen is turned off and back on with the power button).
    //
    // This becomes non-null after the surfaceCreated() callback is called, and gets set
    // to null when surfaceDestroyed() is called.
    private SurfaceHolder sSurfaceHolder;

    // Thread that handles rendering and controls the camera.  Started in onResume(),
    // stopped in onPause().
    private RenderThread mRenderThread;


    public CubeSurfaceView(Context context) {
        super(context);
    }

    public CubeSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CubeSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }




    public void init()
    {
        SurfaceHolder sh = getHolder();
        sh.setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
        sh.addCallback(this);
    }


    public void resume(CubeSurfaceEventListener listener) {

        mRenderThread = new RenderThread(listener);
        mRenderThread.setName("TexFromCam Render");
        mRenderThread.start();
        mRenderThread.waitUntilReady();


        RenderHandler rh = mRenderThread.getHandler();
//        rh.sendZoomValue(mZoomBar.getProgress());
//        rh.sendDisplayAreaValue(mSizeBar.getProgress());
//        rh.sendRotateValue(mRotateBar.getProgress());

        if (sSurfaceHolder != null) {
            Log.d(TAG, "Sending previous surface");
            rh.sendSurfaceAvailable(sSurfaceHolder, false);


        } else {
            Log.d(TAG, "No previous surface");
        }
        Log.d(TAG, "onResume END");

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(mRenderThread==null) return;
                while (null==mRenderThread.getSurface(0));
                mRenderThread.onFrameAvailable(null);
            }
        }).start();

    }

    public void pause() {
        RenderHandler rh = mRenderThread.getHandler();
        rh.sendShutdown();
        try {
            mRenderThread.join();
        } catch (InterruptedException ie) {
            // not expected
            throw new RuntimeException("join was interrupted", ie);
        }
        mRenderThread = null;
    }





    @Override   // SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated holder=" + holder + " (static=" + sSurfaceHolder + ")");
        if (sSurfaceHolder != null) {
            throw new RuntimeException("sSurfaceHolder is already set");
        }

        sSurfaceHolder = holder;

        if (mRenderThread != null) {
            // Normal case -- render thread is running, tell it about the new surface.
            RenderHandler rh = mRenderThread.getHandler();
            rh.sendSurfaceAvailable(holder, true);


        } else {
            // Sometimes see this on 4.4.x N5: power off, power on, unlock, with device in
            // landscape and a lock screen that requires portrait.  The surface-created
            // message is showing up after onPause().
            //
            // Chances are good that the surface will be destroyed before the activity is
            // unpaused, but we track it anyway.  If the activity is un-paused and we start
            // the RenderThread, the SurfaceHolder will be passed in right after the thread
            // is created.
            Log.d(TAG, "render thread not running");
        }
    }

    @Override   // SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged fmt=" + format + " size=" + width + "x" + height +
                " holder=" + holder);

        if (mRenderThread != null) {
            RenderHandler rh = mRenderThread.getHandler();
            rh.sendSurfaceChanged(format, width, height);
        } else {
            Log.d(TAG, "Ignoring surfaceChanged");
            return;
        }
    }

    @Override   // SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder holder) {
        // In theory we should tell the RenderThread that the surface has been destroyed.
        if (mRenderThread != null) {
            RenderHandler rh = mRenderThread.getHandler();
            rh.sendSurfaceDestroyed();
        }
        Log.d(TAG, "surfaceDestroyed holder=" + holder);
        sSurfaceHolder = null;
    }



    /**
     * Thread that handles all rendering and camera operations.
     */
    private static class RenderThread extends Thread implements
            SurfaceTexture.OnFrameAvailableListener {
        // Object must be created on render thread to get correct Looper, but is used from
        // UI thread, so we need to declare it volatile to ensure the UI thread sees a fully
        // constructed object.
        private volatile RenderHandler mHandler;

        // Used to wait for the thread to start.
        private Object mStartLock = new Object();
        private boolean mReady = false;

//        private MainHandler mMainHandler;

//        private NewGlWebView mCamera;
//        private int mCameraPreviewWidth, mCameraPreviewHeight;

        private EglCore mEglCore;
        private WindowSurface mWindowSurface;
        private int mWindowSurfaceWidth;
        private int mWindowSurfaceHeight;

        // Receives the output from the camera preview.
//        private SurfaceTexture mCameraTexture;

        // Orthographic projection matrix.
        private float[] mDisplayProjectionMatrix = new float[16];

        private Texture2dProgram mTexProgram;
//        private List<SurfaceTexture> mSurfaceTextureList = new ArrayList<>();
//        private List<ScaledDrawable2d> scaledDrawable2dList = new ArrayList();
//        private List<Sprite2d> mRectList = new ArrayList<>();
////        private final ScaledDrawable2d mRectDrawable =
////                new ScaledDrawable2d(Drawable2d.Prefab.RECTANGLE);
////        private final Sprite2d mRect = new Sprite2d(mRectDrawable);
//
//        private int mZoomPercent = DEFAULT_ZOOM_PERCENT;
//        private int mSizePercent = DEFAULT_SIZE_PERCENT;
//        private int mRotatePercent = DEFAULT_ROTATE_PERCENT;
//        private float mPosX, mPosY;
        private List<RectBean> mRectList;

        private CubeSurfaceEventListener surfaceEventListener;



        /**
         * Constructor.  Pass in the MainHandler, which allows us to send stuff back to the
         * Activity.
         */
//        public RenderThread(MainHandler handler) {
//            mMainHandler = handler;
//        }

        public RenderThread(CubeSurfaceEventListener listener)
        {
            mRectList = new ArrayList<>();
            surfaceEventListener = listener;
        }

        /**
         * Thread entry point.
         */
        @Override
        public void run() {
            Looper.prepare();

            // We need to create the Handler before reporting ready.
            mHandler = new RenderHandler(this);
            synchronized (mStartLock) {
                mReady = true;
                mStartLock.notify();    // signal waitUntilReady()
            }

            // Prepare EGL and open the camera before we start handling messages.
            mEglCore = new EglCore(null, 0);
//            openCamera(REQ_CAMERA_WIDTH, REQ_CAMERA_HEIGHT, REQ_CAMERA_FPS);

            Looper.loop();

            Log.d(TAG, "looper quit");
//            releaseCamera();
            releaseGl();
            mEglCore.release();

            synchronized (mStartLock) {
                mReady = false;
            }
        }

        /**
         * Waits until the render thread is ready to receive messages.
         * <p>
         * Call from the UI thread.
         */
        public void waitUntilReady() {
            synchronized (mStartLock) {
                while (!mReady) {
                    try {
                        mStartLock.wait();
                    } catch (InterruptedException ie) { /* not expected */ }
                }
            }
        }

        /**
         * Shuts everything down.
         */
        private void shutdown() {
            Log.d(TAG, "shutdown");
            Looper.myLooper().quit();
        }

        /**
         * Returns the render thread's Handler.  This may be called from any thread.
         */
        public RenderHandler getHandler() {
            return mHandler;
        }

        public List<RectBean> getRectList()
        {
            return mRectList;
        }

        public void addRect()
        {
            initRect();
            if(null!=mTexProgram)
                allocRect(mRectList.get(mRectList.size()-1));
        }

        public void subtractRect()
        {
            if(mRectList.size()<=1) return;
            mRectList.remove(mRectList.size() -1);
        }

        private void initRect()
        {
            mRectList.add(new RectBean());
        }

        private SurfaceTexture allocRect(RectBean rectBean) {


            int textureId = mTexProgram.createTextureObject();
            SurfaceTexture cameraTexture = new SurfaceTexture(textureId);
            rectBean.getRect().setTexture(textureId);
            rectBean.setSurfaceTexture(cameraTexture);

            return cameraTexture;
        }

        public Surface getSurface(int index)
        {
            if(index<0||index>=mRectList.size()) return null;
            return mRectList.get(index).getSurface();
        }

        /**
         * Handles the surface-created callback from SurfaceView.  Prepares GLES and the Surface.
         */
        private void surfaceAvailable(SurfaceHolder holder, boolean newSurface) {
            Surface surface = holder.getSurface();
            mWindowSurface = new WindowSurface(mEglCore, surface, false);
            mWindowSurface.makeCurrent();

            // Create and configure the SurfaceTexture, which will receive frames from the
            // camera.  We set the textured rect's program to render from it.
            mTexProgram = new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT);
//            int textureId = mTexProgram.createTextureObject();
//            mCameraTexture = new SurfaceTexture(textureId);
//            mRect.setTexture(textureId);


            for(int i=0;i<mRectList.size();i++)
            {
                allocRect(mRectList.get(i));
            }

            surfaceEventListener.OnSurfaceAvaiable();

            if (!newSurface) {
                // This Surface was established on a previous run, so no surfaceChanged()
                // message is forthcoming.  Finish the surface setup now.
                //
                // We could also just call this unconditionally, and perhaps do an unnecessary
                // bit of reallocating if a surface-changed message arrives.
                mWindowSurfaceWidth = mWindowSurface.getWidth();
                mWindowSurfaceHeight = mWindowSurface.getHeight();
                finishSurfaceSetup();
            }

//            mCameraTexture.setOnFrameAvailableListener(this);
//            mCamera.setOnFrameAvailableListener(this);
        }

        /**
         * Releases most of the GL resources we currently hold (anything allocated by
         * surfaceAvailable()).
         * <p>
         * Does not release EglCore.
         */
        private void releaseGl() {
            GlUtil.checkGlError("releaseGl start");

            if (mWindowSurface != null) {
                mWindowSurface.release();
                mWindowSurface = null;
            }
            if (mTexProgram != null) {
                mTexProgram.release();
                mTexProgram = null;
            }
            GlUtil.checkGlError("releaseGl done");

            mEglCore.makeNothingCurrent();
        }

        /**
         * Handles the surfaceChanged message.
         * <p>
         * We always receive surfaceChanged() after surfaceCreated(), but surfaceAvailable()
         * could also be called with a Surface created on a previous run.  So this may not
         * be called.
         */
        private void surfaceChanged(int width, int height) {
            Log.d(TAG, "RenderThread surfaceChanged " + width + "x" + height);

            mWindowSurfaceWidth = width;
            mWindowSurfaceHeight = height;
            finishSurfaceSetup();
        }

        /**
         * Handles the surfaceDestroyed message.
         */
        private void surfaceDestroyed() {
            // In practice this never appears to be called -- the activity is always paused
            // before the surface is destroyed.  In theory it could be called though.
            Log.d(TAG, "RenderThread surfaceDestroyed");
            mRectList.clear();
            releaseGl();
        }

        /**
         * Sets up anything that depends on the window size.
         * <p>
         * Open the camera (to set mCameraAspectRatio) before calling here.
         */
        private void finishSurfaceSetup() {
            int width = mWindowSurfaceWidth;
            int height = mWindowSurfaceHeight;
//            Log.d(TAG, "finishSurfaceSetup size=" + width + "x" + height +
//                    " camera=" + mCameraPreviewWidth + "x" + mCameraPreviewHeight);

            // Use full window.
            GLES20.glViewport(0, 0, width, height);

            // Simple orthographic projection, with (0,0) in lower-left corner.
            Matrix.orthoM(mDisplayProjectionMatrix, 0, 0, width, 0, height, -1, 1);

            // Default position is center of screen.'
            for(int i=0;i<mRectList.size();i++) {
                RectBean rectBean = mRectList.get(i);
                rectBean.setPosX(width / 2.0f);
                rectBean.setPosY(height / 2.0f);
                updateGeometry(i);
            }


            for(int i=0;i<mRectList.size();i++) {
                mRectList.get(i).getSurfaceTexture().setDefaultBufferSize(width, height);
            }
            // Ready to go, start the camera.
//                mCamera.updatePreviewSurface(mCameraTexture);

        }

        /**
         * Updates the geometry of mRect, based on the size of the window and the current
         * values set by the UI.
         */
        private void updateGeometry(int index) {
            RectBean rectBean = mRectList.get(index);
            int width = mWindowSurfaceWidth;
            int height = mWindowSurfaceHeight;

//            int smallDim = Math.min(width, height);
            // Max scale is a bit larger than the screen, so we can show over-size.
//            float scaled = smallDim * (mSizePercent / 100.0f) * 1.25f;
//            float cameraAspect = (float) mCameraPreviewWidth / mCameraPreviewHeight;
//            int newWidth = Math.round(scaled * cameraAspect);
//            int newHeight = Math.round(scaled);

            float displayAreaFactor = 1.0f - (rectBean.getDisplayAreaPercent()/100.0f);
            float zoomFactor = 1.0f - (rectBean.getZoomPercent() / 100.0f);
            int rotAngle = Math.round(360 * (rectBean.getRotatePercent() / 100.0f));

            float finalScale = (zoomFactor*displayAreaFactor);
            float newWidth = width*finalScale;
            float newHeight = height*finalScale;

            Sprite2d rect = rectBean.getRect();
            rect.setScale(newWidth,newHeight);
            rect.setPosition(rectBean.getPosX(),rectBean.getPosY());
            rect.setRotation(rotAngle);

            rectBean.getSurfaceTexture().setDefaultBufferSize((int)newWidth, (int)newHeight);
//            rectBean.getScaledDrawable2d().setScale(finalScale);


//            mMainHandler.sendRectSize(newWidth, newHeight);
//            mMainHandler.sendZoomArea(Math.round(mCameraPreviewWidth * zoomFactor),
//                    Math.round(mCameraPreviewHeight * zoomFactor));
//            mMainHandler.sendRotateDeg(rotAngle);
        }

        @Override   // SurfaceTexture.OnFrameAvailableListener; runs on arbitrary thread
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {

            mHandler.sendFrameAvailable();
//            mMainHandler.sendFrameAvailable();

        }

        /**
         * Handles incoming frame of data from the camera.
         */
        private void frameAvailable() {

            long startTime = System.currentTimeMillis();
            for(int i=0;i<mRectList.size();i++)
            {
                if(!mRectList.get(i).isEnable())
                    continue;
                SurfaceTexture surfaceTexture = mRectList.get(i).getSurfaceTexture();
                if (null == surfaceTexture)
                    return;
                surfaceTexture.updateTexImage();
            }

//            long startTime = System.currentTimeMillis();

            surfaceEventListener.BeforeDrawFrame();
            draw();
//            long endTime = System.currentTimeMillis() - startTime;
//            Log.e("during ",endTime+"");

//            fpsCount++;


            int during = (int) (System.currentTimeMillis() - startTime);

            int delay = DRAW_FRAME_DELAY - during;
            if(delay<0) delay = 0;
            mHandler.sendFrameAvailableDelayed(delay);
        }

        /**
         * Draws the scene and submits the buffer.
         */

        private long drawTime;
        private void draw() {
            if(mWindowSurface==null) return;
            long startTime = System.currentTimeMillis();
//            GlUtil.checkGlError("draw start");

//            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

//            Log.e("opengl","draw");
            for(int i=0;i<mRectList.size();i++) {
                RectBean rectBean = mRectList.get(i);
                if(rectBean.isEnable())
                    rectBean.getRect().draw(mTexProgram, mDisplayProjectionMatrix);
            }
            mWindowSurface.swapBuffers();
//            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

//            GlUtil.checkGlError("draw done");
            long curTime = System.currentTimeMillis();
            long endTime = curTime - startTime;
            long during = curTime - drawTime;
            drawTime = curTime;
//            if(during>20)
//                Log.e("opengl draw",during+","+endTime);
        }

        private void setZoom(int index,int percent) {
            mRectList.get(index).setZoomPercent(percent);
            updateGeometry(index);
        }

        private void setDisplayArea(int index, int percent) {
            mRectList.get(index).setDisplayAreaPercent(percent);
            updateGeometry(index);
        }

        private void setRotate(int index,int percent) {
            mRectList.get(index).setRotatePercent(percent);
            updateGeometry(index);
        }

        private void setPosition(int index,int x, int y) {
            mRectList.get(index).setPosX(x);
            mRectList.get(index).setPosY(mWindowSurfaceHeight - y);   // GLES is upside-down
            updateGeometry(index);
        }

        /**
         * Opens a camera, and attempts to establish preview mode at the specified width
         * and height with a fixed frame rate.
         * <p>
         * Sets mCameraPreviewWidth / mCameraPreviewHeight.
         */
        private void openView(int desiredWidth, int desiredHeight, int desiredFps) {
        }

        /**
         * Stops camera preview, and releases the camera to the system.
         */
        private void releaseView() {
        }


//        public void setContentView(NewGlWebView contentView)
//        {
//            this.mCamera = contentView;
//        }
    }

    public interface CubeSurfaceEventListener
    {
        void OnSurfaceAvaiable();
        void BeforeDrawFrame();
    }


    /**
     * Handler for RenderThread.  Used for messages sent from the UI thread to the render thread.
     * <p>
     * The object is created on the render thread, and the various "send" methods are called
     * from the UI thread.
     */
    private static class RenderHandler extends Handler {
        private static final int MSG_SURFACE_AVAILABLE = 0;
        private static final int MSG_SURFACE_CHANGED = 1;
        private static final int MSG_SURFACE_DESTROYED = 2;
        private static final int MSG_SHUTDOWN = 3;
        private static final int MSG_FRAME_AVAILABLE = 4;
        private static final int MSG_ZOOM_VALUE = 5;
        private static final int MSG_DISPLAY_AREA_VALUE = 6;
        private static final int MSG_ROTATE_VALUE = 7;
        private static final int MSG_POSITION = 8;
        private static final int MSG_REDRAW = 9;

        // This shouldn't need to be a weak ref, since we'll go away when the Looper quits,
        // but no real harm in it.
        private WeakReference<RenderThread> mWeakRenderThread;

        /**
         * Call from render thread.
         */
        public RenderHandler(RenderThread rt) {
            mWeakRenderThread = new WeakReference<RenderThread>(rt);
        }

        /**
         * Sends the "surface available" message.  If the surface was newly created (i.e.
         * this is called from surfaceCreated()), set newSurface to true.  If this is
         * being called during Activity startup for a previously-existing surface, set
         * newSurface to false.
         * <p>
         * The flag tells the caller whether or not it can expect a surfaceChanged() to
         * arrive very soon.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceAvailable(SurfaceHolder holder, boolean newSurface) {
            sendMessage(obtainMessage(MSG_SURFACE_AVAILABLE,
                    newSurface ? 1 : 0, 0, holder));
        }

        /**
         * Sends the "surface changed" message, forwarding what we got from the SurfaceHolder.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceChanged(@SuppressWarnings("unused") int format, int width,
                                       int height) {
            // ignore format
            sendMessage(obtainMessage(MSG_SURFACE_CHANGED, width, height));
        }

        /**
         * Sends the "shutdown" message, which tells the render thread to halt.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceDestroyed() {
            sendMessage(obtainMessage(MSG_SURFACE_DESTROYED));
        }

        /**
         * Sends the "shutdown" message, which tells the render thread to halt.
         * <p>
         * Call from UI thread.
         */
        public void sendShutdown() {
            sendMessage(obtainMessage(MSG_SHUTDOWN));
        }

        /**
         * Sends the "frame available" message.
         * <p>
         * Call from UI thread.
         */
        public void sendFrameAvailable() {
            sendMessage(obtainMessage(MSG_FRAME_AVAILABLE));
        }


        public void sendFrameAvailableDelayed(int millSecond) {
            sendMessageDelayed(obtainMessage(MSG_FRAME_AVAILABLE),millSecond);
        }

        /**
         * Sends the "zoom value" message.  "progress" should be 0-100.
         * <p>
         * Call from UI thread.
         */
        public void sendZoomValue(int index,int progress) {
            sendMessage(obtainMessage(MSG_ZOOM_VALUE, index, progress));
        }

        /**
         * Sends the "size value" message.  "progress" should be 0-100.
         * <p>
         * Call from UI thread.
         */
        public void sendDisplayAreaValue(int index, int progress) {
            sendMessage(obtainMessage(MSG_DISPLAY_AREA_VALUE,index, progress));
        }

        /**
         * Sends the "rotate value" message.  "progress" should be 0-100.
         * <p>
         * Call from UI thread.
         */
        public void sendRotateValue(int index,int progress) {
            sendMessage(obtainMessage(MSG_ROTATE_VALUE,index, progress));
        }

        /**
         * Sends the "position" message.  Sets the position of the rect.
         * <p>
         * Call from UI thread.
         */
        public void sendPosition(int index,int x, int y) {
            sendMessage(obtainMessage(MSG_POSITION,index, x, y));
        }

        /**
         * Sends the "redraw" message.  Forces an immediate redraw.
         * <p>
         * Call from UI thread.
         */
        public void sendRedraw() {
            sendMessage(obtainMessage(MSG_REDRAW));
        }

        @Override  // runs on RenderThread
        public void handleMessage(Message msg) {
            int what = msg.what;
            //Log.d(TAG, "RenderHandler [" + this + "]: what=" + what);

            RenderThread renderThread = mWeakRenderThread.get();
            if (renderThread == null) {
                Log.w(TAG, "RenderHandler.handleMessage: weak ref is null");
                return;
            }

            switch (what) {
                case MSG_SURFACE_AVAILABLE:
                    renderThread.surfaceAvailable((SurfaceHolder) msg.obj, msg.arg1 != 0);
                    break;
                case MSG_SURFACE_CHANGED:
                    renderThread.surfaceChanged(msg.arg1, msg.arg2);
                    break;
                case MSG_SURFACE_DESTROYED:
                    renderThread.surfaceDestroyed();
                    break;
                case MSG_SHUTDOWN:
                    renderThread.shutdown();
                    break;
                case MSG_FRAME_AVAILABLE:
                    renderThread.frameAvailable();
                    break;
                case MSG_ZOOM_VALUE:
                    renderThread.setZoom(msg.arg1,msg.arg2);
                    break;
                case MSG_DISPLAY_AREA_VALUE:
                    renderThread.setDisplayArea(msg.arg1,msg.arg2);
                    break;
                case MSG_ROTATE_VALUE:
                    renderThread.setRotate(msg.arg1,msg.arg2);
                    break;
                case MSG_POSITION:
                    renderThread.setPosition(msg.arg1, msg.arg2,(int)msg.obj);
                    break;
                case MSG_REDRAW:
                    renderThread.draw();
                    break;
                default:
                    throw new RuntimeException("unknown message " + what);
            }
        }
    }

    public SurfaceTexture.OnFrameAvailableListener getFrameAvailableListener()
    {
        return mRenderThread;
    }

    public Surface getRectSurface(int index)
    {
        return mRenderThread.getSurface(index);
    }

    public void setPosition(int index,int posX,int posY)
    {
        mRenderThread.getHandler().sendPosition(index,posX,posY);
    }


    public void setZoom(int index,int percent) {
        mRenderThread.getHandler().sendZoomValue(index,percent);
    }

    public void setDisplayArea(int index, int percent) {
        mRenderThread.getHandler().sendDisplayAreaValue(index,percent);
    }

    public void setRotate(int index,int percent) {
        mRenderThread.getHandler().sendRotateValue(index,percent);
    }

    public int getRectCount()
    {
        return mRenderThread.getRectList().size();
    }


    public interface DrawTextureView
    {
        boolean updatePreviewSurface(final Surface surface);
        void drawOpenGlTexture(boolean draw);
    }

    public void addRect()
    {
        mRenderThread.addRect();
    }

    public void subtractRect()
    {
        mRenderThread.subtractRect();
    }

    public boolean isRectEnable(int index)
    {
        return mRenderThread.getRectList().get(index).isEnable();
    }

    public void setRectEnable(int index,boolean enable)
    {
        mRenderThread.getRectList().get(index).setEnable(enable);
    }

    public void updateDrawViewIndex(int rectIndex,int drawviewIndex)
    {
        mRenderThread.getRectList().get(rectIndex).setDrawViewIndex(drawviewIndex);
    }

    public int getDrawViewIndex(int rectIndex)
    {
        return mRenderThread.getRectList().get(rectIndex).getDrawViewIndex();
    }
}
