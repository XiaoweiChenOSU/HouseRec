package com.okstate.VisualComputingandImageProcessingLab.HouseRec.view;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Cube {
    private final FloatBuffer mVertexBuffer;
    private final FloatBuffer mColorBuffer;
    private final ByteBuffer mIndexBuffer;
    public Cube() {
//
//        final float vertices[] = {  -1f, -1f, -1f,
//                1f, -1f, -1f,
//                1f, 1f, -1f,
//                -1f, 1f, -1f,
//                -1, -1, 1,
//                1, -1, 1,
//                1, 1, 1,
//                -1, 1, 1,};

        final float vertices[] = {  -0.5f, 0.25f, -1f,
                0.5f, 0.25f, -1f,
                0.5f, 0.5f, -1f,
                -0.5f, 0.5f, -1f,
                -0.5f, 0.25f, 0.5f,
                0.5f, 0.25f, 0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,
                -1.5f, 0.25f, -1f,
                1.5f, 0.25f, -1f,
                0, 0.25f, -3,
                1.5f, 0.5f, -1,
                -1.5f, 0.5f, -1,
                0, 0.5f, -3f};


//        final float vertices[] = {  -1f, 0.5f, -2f,
//                                    1f, 0.5f, -2f,
//                                    1f, 1f, -2f,
//                                    -1f, 1f, -2f,
//                                    -1, 0.5f, 1,
//                                    1, 0.5f, 1,
//                                    1, 1, 1,
//                                    -1, 1, 1,
//                                    -3f, 0.5f, -2f,
//                                    3f, 0.5f, -2f,
//                                    0, 0.5f, -6,
//                                    3, 1, -2,
//                                    -3, 1, -2,
//                                    0, 1, -6};
//
//        final float colors[] = {0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0,
//                1, 1, 1,};

        final float colors[] = {
                1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1,
                1, 0, 0, 1
                , 1, 0, 0, 1
                , 1, 0, 0, 1
                , 1, 0, 0, 1
                , 1, 0, 0, 1
                , 1, 0, 0, 1};

        final byte indices[] = {
                0, 4, 5, 0, 5, 1, 1, 5, 6, 1, 6, 2, 2, 6, 7, 2, 7, 3, 3, 7, 4, 3, 4, 0, 4, 7, 6, 4, 6,
                5, 3, 0, 1, 3, 1, 2,
                8, 9, 10, 11, 12, 13, 8, 12, 10, 10, 12 ,13, 9, 10, 11, 10, 13, 11, 8, 12, 11, 8, 11, 9};

//        final byte indices[] = {1, 5, 6, 1, 6, 2};

//        final float vertices[] = {
//                -0.25f, -0f, -0.25f,	 0.25f, -0f, -0.25f,
//                0.25f,  1, -0.25f,	    -0.25f,  1, -0.25f,
//                -0.25f, -0f,  0.25f,      0.25f, -0f,  0.25f,
//                0.25f,  1,  0.25f,     -0.25f,  1,  0.25f,
//                -0.5f, -0f, -0.5f,    0.5f, -0f, -0.5f,
//                -0.5f, 0f, 0.5f,           0.5f, -0f, 0.5f,
//                0, -1.25f, 0
//        };
//        final float colors[] = {
//                1, 0, 0, 1,  1, 0, 0, 1,
//                1, 0, 0, 1,  1, 0, 0, 1,
//                1, 0, 0, 1,  1, 0, 0, 1,
//                1, 0, 0, 1,  1, 0, 0, 1,
//                1, 0, 0, 1,  1, 0, 0, 1,
//                1, 0, 0, 1,  1, 0, 0, 1,
//                1, 0, 0, 1,  1, 0, 0, 1,
//                1, 0, 0, 1,  1, 0, 0, 1,
//        };
//        final byte indices[] = {
//                0, 4, 5,    0, 5, 1,
//                1, 5, 6,    1, 6, 2,
//                2, 6, 7,    2, 7, 3,
//                3, 7, 4,    3, 4, 0,
//                4, 7, 6,    4, 6, 5,
//                3, 0, 1,    3, 1, 2,
//                4, 0, 8,    9, 1, 5,
//                8, 0, 4,    5, 1, 9,
//                9, 5, 8,    8, 4, 9,
//                8, 5, 9,    9, 4, 8,
//                10, 0, 4,   11, 1, 5,
//                4, 0, 10,   5, 1, 11,
//                8, 4, 10,   9, 5, 11,
//                10, 4, 8,   11, 5, 9,
//                10, 0, 11,  11, 1, 10,
//                11, 0, 10,  10, 1, 11,
//                10, 12, 8,   11, 12, 9,
//                8, 12, 10,   9, 12, 11,
//                10, 12, 11,  11, 12, 10,
//                9, 12, 8,    8, 12, 9,
//        };


        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
    }

    public void draw(GL10 gl) {
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glFrontFace(GL10.GL_CW);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLES, 60, GL10.GL_UNSIGNED_BYTE, mIndexBuffer);
    }
}
