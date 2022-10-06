package com.okstate.VisualComputingandImageProcessingLab.HouseRec.fragment;

import static android.content.Context.SENSOR_SERVICE;

import android.annotation.SuppressLint;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.orientation.OrientationProvider;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.sensor.GyroscopeSensorProvider;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.view.CubeRenderer;

public class OrientationFragment extends Fragment {
    private SensorManager sensorManager;
    private int currentSensor;

    private GLSurfaceView mGLSurfaceView;
    private CubeRenderer mRenderer;
    private OrientationProvider currentOrientationProvider;

    public OrientationFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        currentOrientationProvider.start();
        mGLSurfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        currentOrientationProvider.stop();
        mGLSurfaceView.onPause();
    }

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        currentOrientationProvider = new GyroscopeSensorProvider((SensorManager) getActivity().getSystemService(SENSOR_SERVICE));

        mRenderer = new CubeRenderer();
        mRenderer.setOrientationProvider(currentOrientationProvider);
        mGLSurfaceView = new GLSurfaceView(getActivity());
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGLSurfaceView.setRenderer(mRenderer);

        mGLSurfaceView.setOnLongClickListener(v -> {
            mRenderer.toggleShowCubeInsideOut();
            return true;
        });

        return mGLSurfaceView;
    }
}
