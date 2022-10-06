package com.okstate.VisualComputingandImageProcessingLab.HouseRec.sensor;


public class Quaternion extends Vector4f {

    private Vector4f tmpVector = new Vector4f();

    public Quaternion() {
        super();
        loadIdentityQuat();
    }

    public void normalise() {
        float mag = (float) Math.sqrt(points[3] * points[3] + points[0] * points[0] + points[1] * points[1] + points[2]
                * points[2]);
        points[3] = points[3] / mag;
        points[0] = points[0] / mag;
        points[1] = points[1] / mag;
        points[2] = points[2] / mag;
    }

    @Override
    public void normalize() {
        normalise();
    }

    public void set(Quaternion quat) {
        copyVec4(quat);
    }

    public void multiplyByQuat(Quaternion input, Quaternion output) {

        if (input != output) {
            output.points[3] = (points[3] * input.points[3] - points[0] * input.points[0] - points[1] * input.points[1] - points[2]
                    * input.points[2]);
            output.points[0] = (points[3] * input.points[0] + points[0] * input.points[3] + points[1] * input.points[2] - points[2]
                    * input.points[1]);
            output.points[1] = (points[3] * input.points[1] + points[1] * input.points[3] + points[2] * input.points[0] - points[0]
                    * input.points[2]);
            output.points[2] = (points[3] * input.points[2] + points[2] * input.points[3] + points[0] * input.points[1] - points[1]
                    * input.points[0]);
        } else {
            tmpVector.points[0] = input.points[0];
            tmpVector.points[1] = input.points[1];
            tmpVector.points[2] = input.points[2];
            tmpVector.points[3] = input.points[3];

            output.points[3] = (points[3] * tmpVector.points[3] - points[0] * tmpVector.points[0] - points[1]
                    * tmpVector.points[1] - points[2] * tmpVector.points[2]);
            output.points[0] = (points[3] * tmpVector.points[0] + points[0] * tmpVector.points[3] + points[1]
                    * tmpVector.points[2] - points[2] * tmpVector.points[1]);
            output.points[1] = (points[3] * tmpVector.points[1] + points[1] * tmpVector.points[3] + points[2]
                    * tmpVector.points[0] - points[0] * tmpVector.points[2]);
            output.points[2] = (points[3] * tmpVector.points[2] + points[2] * tmpVector.points[3] + points[0]
                    * tmpVector.points[1] - points[1] * tmpVector.points[0]);
        }
    }

    public void multiplyByScalar(float scalar) {
        multiplyByScalar(scalar);
    }

    public void loadIdentityQuat() {
        setX(0);
        setY(0);
        setZ(0);
        setW(1);
    }

    @Override
    public String toString() {
        return "{X: " + getX() + ", Y:" + getY() + ", Z:" + getZ() + ", W:" + getW() + "}";
    }
}
