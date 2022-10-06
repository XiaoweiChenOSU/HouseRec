package com.okstate.VisualComputingandImageProcessingLab.HouseRec.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.orientation.OrientationProvider;

public class GyroscopeSensorProvider extends OrientationProvider {

    private static final float NS2S = 1.0f / 1000000000.0f;

    private final Quaternion deltaQuaternion = new Quaternion();

    private long timestamp;

    private static final double EPSILON = 0.1f;

    private final Quaternion correctedQuaternion = new Quaternion();

    public GyroscopeSensorProvider(SensorManager sensorManager) {
        super(sensorManager);

        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            if (timestamp != 0) {
                final float dT = (event.timestamp - timestamp) * NS2S;
                float axisX = event.values[0];
                float axisY = event.values[1];
                float axisZ = event.values[2];

                double gyroscopeRotationVelocity = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

                if (gyroscopeRotationVelocity > EPSILON) {
                    axisX /= gyroscopeRotationVelocity;
                    axisY /= gyroscopeRotationVelocity;
                    axisZ /= gyroscopeRotationVelocity;
                }

                double thetaOverTwo = gyroscopeRotationVelocity * dT / 2.0f;
                double sinThetaOverTwo = Math.sin(thetaOverTwo);
                double cosThetaOverTwo = Math.cos(thetaOverTwo);
                deltaQuaternion.setX((float) (sinThetaOverTwo * axisX));
                deltaQuaternion.setY((float) (sinThetaOverTwo * axisY));
                deltaQuaternion.setZ((float) (sinThetaOverTwo * axisZ));
                deltaQuaternion.setW(-(float) cosThetaOverTwo);

                synchronized (synchronizationToken) {
                    deltaQuaternion.multiplyByQuat(currentOrientationQuaternion, currentOrientationQuaternion);
                }

                correctedQuaternion.set(currentOrientationQuaternion);
                correctedQuaternion.w(-correctedQuaternion.w());

                synchronized (synchronizationToken) {
                    SensorManager.getRotationMatrixFromVector(currentOrientationRotationMatrix.matrix,
                            correctedQuaternion.array());
                }
            }
            timestamp = event.timestamp;
        }
    }
}
