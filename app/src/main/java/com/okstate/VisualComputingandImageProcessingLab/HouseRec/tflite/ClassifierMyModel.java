package com.okstate.VisualComputingandImageProcessingLab.HouseRec.tflite;

import android.app.Activity;
import android.os.Environment;

import java.io.IOException;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.ops.NormalizeOp;

public class ClassifierMyModel extends Classifier{

//    private static final float IMAGE_MEAN = 127.5f;
//    private static final float IMAGE_STD = 127.5f;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 255.0f;

    /**
     * Float model does not need dequantization in the post-processing. Setting mean and std as 0.0f
     * and 1.0f, repectively, to bypass the normalization.
     */
    private static final float PROBABILITY_MEAN = 0.0f;

    private static final float PROBABILITY_STD = 1.0f;

    /**
     * Initializes a {@code ClassifierFloatMobileNet}.
     *
     * @param activity
     */
    public ClassifierMyModel(Activity activity, Device device, int numThreads,boolean myModel,String layoutName)
            throws IOException {
        super(activity, device, numThreads,true,layoutName);
    }


    @Override
    protected String getModelPath() {
        // you can download this file from http://10.203.8.185:5001/downloadModel
        // see build.gradle for where to obtain this file. It should be auto
        // downloaded into assets.
        return "model.tflite";
    }

    @Override
    protected String getLabelPath() {
        return "Catagories.txt";
        //return Environment.getExternalStorageDirectory().getAbsolutePath() + "/label/" + "Catagories" + ".txt";
    }

    @Override
    protected TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }

    @Override
    protected TensorOperator getPostprocessNormalizeOp() {
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }

}
