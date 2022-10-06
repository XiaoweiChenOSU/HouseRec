package com.okstate.VisualComputingandImageProcessingLab.HouseRec.orientation;

import android.opengl.Matrix;

public class MatrixF4x4 {

    public float[] matrix;

    public MatrixF4x4() {
        this.matrix = new float[16];
        Matrix.setIdentityM(this.matrix, 0);
    }

}
