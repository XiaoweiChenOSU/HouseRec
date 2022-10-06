package com.okstate.VisualComputingandImageProcessingLab.HouseRec.orientation;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.sensor.Quaternion;

import java.util.ArrayList;
import java.util.List;

public abstract class OrientationProvider implements SensorEventListener {

    protected final Object synchronizationToken = new Object();

    protected List<Sensor> sensorList = new ArrayList<Sensor>();

    protected final MatrixF4x4 currentOrientationRotationMatrix;

    protected final Quaternion currentOrientationQuaternion;

    protected SensorManager sensorManager;

    public OrientationProvider(SensorManager sensorManager) {
        this.sensorManager = sensorManager;

        currentOrientationRotationMatrix = new MatrixF4x4();
        currentOrientationQuaternion = new Quaternion();
    }

    public void start() {
        for (Sensor sensor : sensorList) {
            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void stop() {
        for (Sensor sensor : sensorList) {
            sensorManager.unregisterListener(this, sensor);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not doing anything
    }

    public void getQuaternion(Quaternion quaternion) {
        synchronized (synchronizationToken) {
            quaternion.set(currentOrientationQuaternion);
        }
    }

}
