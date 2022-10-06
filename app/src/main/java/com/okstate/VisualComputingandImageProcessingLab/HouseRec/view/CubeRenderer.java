package com.okstate.VisualComputingandImageProcessingLab.HouseRec.view;

import android.opengl.GLSurfaceView;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.orientation.OrientationProvider;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.sensor.Quaternion;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CubeRenderer implements GLSurfaceView.Renderer {
    private Cube mCube;
    private OrientationProvider orientationProvider = null;
    private Quaternion quaternion = new Quaternion();

    public CubeRenderer() {
        mCube = new Cube();
    }

    public void setOrientationProvider(OrientationProvider orientationProvider) {
        this.orientationProvider = orientationProvider;
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        if (showCubeInsideOut) {
            float dist = 3;
            gl.glTranslatef(0, 0, -dist);

            if (orientationProvider != null) {
                orientationProvider.getQuaternion(quaternion);
                gl.glRotatef((float) (2.0f * Math.acos(quaternion.getW()) * 180.0f / Math.PI), quaternion.getX(), quaternion.getY(), quaternion.getZ());
            }

            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

            mCube.draw(gl);
        } else {

            if (orientationProvider != null) {
                orientationProvider.getQuaternion(quaternion);
                gl.glRotatef((float) (2.0f * Math.acos(quaternion.getW()) * 180.0f / Math.PI), quaternion.getX(), quaternion.getY(), quaternion.getZ());
            }

            float dist = 3;
            drawTranslatedCube(gl, 0, 0, -dist);
            drawTranslatedCube(gl, 0, 0, dist);
            drawTranslatedCube(gl, 0, -dist, 0);
            drawTranslatedCube(gl, 0, dist, 0);
            drawTranslatedCube(gl, -dist, 0, 0);
            drawTranslatedCube(gl, dist, 0, 0);
        }

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        mCube.draw(gl);
    }

    private void drawTranslatedCube(GL10 gl, float translateX, float translateY, float translateZ) {
        gl.glPushMatrix();
        gl.glTranslatef(translateX, translateY, translateZ);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        mCube.draw(gl);
        gl.glPopMatrix();
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glDisable(GL10.GL_DITHER);
        gl.glClearColor(0, 0, 0, 1);
    }

    private boolean showCubeInsideOut = true;

    public void toggleShowCubeInsideOut() {
        this.showCubeInsideOut = !showCubeInsideOut;
    }
}

