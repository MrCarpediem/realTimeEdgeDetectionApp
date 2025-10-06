package com.wintershade.opengltest;
//package com.example.realtimeedges;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Button;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

@SuppressWarnings("deprecation")
public class MyGLRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private Context context;
    private SurfaceTexture surfaceTexture;
    private Camera camera;
    private int textureId;
    private int program;

    private int positionHandle;
    private int texCoordHandle;
    private int texMatrixHandle;
    private int texOffsetHandle;

    private long lastTime = System.nanoTime();
    private int frameCount = 0;
    private float fps = 0;
    private Button fpsButton;

    private FloatBuffer vertexBuffer, textureBuffer;

    private final float[] squareCoords = {
            -1.0f,  1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f,  1.0f
    };

    private final float[] textureCoords = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    };

    private final String vertexShaderCode =
            "uniform mat4 textureTransform;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec2 vTexCoord;" +
                    "varying vec2 texCoord;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "  texCoord = (textureTransform * vec4(vTexCoord, 0.0, 1.0)).xy;" +
                    "}";

    private final String fragmentShaderCode =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "uniform vec2 texOffset;\n" +
                    "varying vec2 texCoord;\n" +
                    "void main() {\n" +
                    "  float kernelX[9];\n" +
                    "  float kernelY[9];\n" +
                    "  kernelX[0]=-1.0; kernelX[1]=0.0; kernelX[2]=1.0;\n" +
                    "  kernelX[3]=-2.0; kernelX[4]=0.0; kernelX[5]=2.0;\n" +
                    "  kernelX[6]=-1.0; kernelX[7]=0.0; kernelX[8]=1.0;\n" +
                    "  kernelY[0]=-1.0; kernelY[1]=-2.0; kernelY[2]=-1.0;\n" +
                    "  kernelY[3]=0.0;  kernelY[4]=0.0;  kernelY[5]=0.0;\n" +
                    "  kernelY[6]=1.0;  kernelY[7]=2.0;  kernelY[8]=1.0;\n" +
                    "  vec3 sample[9];\n" +
                    "  int k = 0;\n" +
                    "  for(int i=-1;i<=1;i++) {\n" +
                    "    for(int j=-1;j<=1;j++) {\n" +
                    "      sample[k++] = texture2D(sTexture, texCoord + vec2(float(i)*texOffset.x, float(j)*texOffset.y)).rgb;\n" +
                    "    }\n" +
                    "  }\n" +
                    "  float gx=0.0;\n" +
                    "  float gy=0.0;\n" +
                    "  for(int i=0;i<9;i++){\n" +
                    "    float intensity = length(sample[i]);\n" +
                    "    gx += kernelX[i]*intensity;\n" +
                    "    gy += kernelY[i]*intensity;\n" +
                    "  }\n" +
                    "  float g = sqrt(gx*gx + gy*gy);\n" +
                    "  gl_FragColor = vec4(vec3(g),1.0);\n" +
                    "}";

    private final float[] mtx = new float[16];

    private int cameraWidth = 1280;
    private int cameraHeight = 720;

    public MyGLRenderer(Context context) {
        this.context = context;

        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        ByteBuffer tb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        tb.order(ByteOrder.nativeOrder());
        textureBuffer = tb.asFloatBuffer();
        textureBuffer.put(textureCoords);
        textureBuffer.position(0);
    }

    public void setFpsButton(Button button) {
        this.fpsButton = button;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Create external texture
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        textureId = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // SurfaceTexture
        surfaceTexture = new SurfaceTexture(textureId);
        surfaceTexture.setOnFrameAvailableListener(this);

        // Compile shaders
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        texCoordHandle = GLES20.glGetAttribLocation(program, "vTexCoord");
        texMatrixHandle = GLES20.glGetUniformLocation(program, "textureTransform");
        texOffsetHandle = GLES20.glGetUniformLocation(program, "texOffset");

        GLES20.glClearColor(0, 0, 0, 1);

        // Start camera
        try {
            camera.setPreviewTexture(surfaceTexture);
            Camera.Parameters params = camera.getParameters();
            params.setPreviewSize(cameraWidth, cameraHeight);
            camera.setParameters(params);
            camera.setDisplayOrientation(90); // portrait
            camera.startPreview();
        } catch (Exception e) {
            Log.e("MyGLRenderer", "Camera preview failed: " + e.getMessage());
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Update camera texture
        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(mtx);

        GLES20.glUseProgram(program);

        // Pass texture transform
        GLES20.glUniformMatrix4fv(texMatrixHandle, 1, false, mtx, 0);

        // Pass texOffset (1/width, 1/height)
        GLES20.glUniform2f(texOffsetHandle, 1.0f / cameraWidth, 1.0f / cameraHeight);

        // Enable attributes
        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        textureBuffer.position(0);
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);

        frameCount++;
        long currentTime = System.nanoTime();
        if (currentTime - lastTime >= 1_000_000_000) { // every 1 second
            fps = frameCount * 1e9f / (currentTime - lastTime);
            frameCount = 0;
            lastTime = currentTime;

            if (fpsButton != null) {
                fpsButton.post(() -> fpsButton.setText(String.format("FPS: %.1f", fps)));
            }
        }

        // Bind camera texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);

        // Draw quad
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);

        // Disable attributes
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);

    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        // Request render
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }
}

