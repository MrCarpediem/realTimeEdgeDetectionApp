package com.wintershade.opengltest;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;

@SuppressWarnings("deprecation")
public class MainActivityGL extends Activity implements SurfaceHolder.Callback {

    private Camera camera;
    private GLSurfaceView glSurfaceView;
    private MyGLRenderer renderer;
    private Button fpsButton;

    static {
        if (!OpenCVLoader.initDebug()) {
            throw new RuntimeException("OpenCV failed to initialize");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        renderer = new MyGLRenderer(this);
        renderer.setFpsButton(fpsButton);
        glSurfaceView.setRenderer(renderer);

        setContentView(glSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera = Camera.open();
        renderer.setCamera(camera);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) { }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { }
}
