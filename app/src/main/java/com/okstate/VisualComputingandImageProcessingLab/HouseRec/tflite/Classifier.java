package com.okstate.VisualComputingandImageProcessingLab.HouseRec.tflite;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.lang.Math.min;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Trace;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.ClassifierActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.ModelActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;

import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.nnapi.NnApiDelegate;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeOp.ResizeMethod;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;


/** A classifier specialized to label images using TensorFlow Lite. */
public abstract class Classifier {
    public static final String TAG = "ClassifierWithSupport";

    /** The model type used for classification. */
    public enum Model {
        FLOAT_MOBILENET,
        QUANTIZED_MOBILENET,
        FLOAT_EFFICIENTNET,
        QUANTIZED_EFFICIENTNET,
        MY_MODEL
    }

    /** The runtime device type used for executing classification. */
    public enum Device {
        CPU,
        NNAPI,
        GPU
    }

    /** Number of results to show in the UI. */
    private static final int MAX_RESULTS = 3;

    /** The loaded TensorFlow Lite model. */

    /** Image size along the x axis. */
    private final int imageSizeX;

    /** Image size along the y axis. */
    private final int imageSizeY;

    /** Optional GPU delegate for accleration. */
    private GpuDelegate gpuDelegate = null;

    /** Optional NNAPI delegate for accleration. */
    private NnApiDelegate nnApiDelegate = null;

    /** An instance of the driver class to run model inference with Tensorflow Lite. */
    protected Interpreter tflite;

    /** Options for configuring the Interpreter. */
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();

    /** Labels corresponding to the output of the vision model. */
    private final List<String> labels;

    /** Input image TensorBuffer. */
    private TensorImage inputImageBuffer;

    /** Output probability TensorBuffer. */
    private final TensorBuffer outputProbabilityBuffer;

    /** Processer to apply post processing of the output probability. */
    private final TensorProcessor probabilityProcessor;

    /**
     * Creates a classifier with the provided configuration.
     *
     * @param activity The current Activity.
     * @param model The model to use for classification.
     * @param device The device to use for classification.
     * @param numThreads The number of threads to use for classification.
     * @return A classifier with the desired configuration.
     */
    public static Classifier create(Activity activity, Model model, Device device, int numThreads, boolean mymodel,String layoutName)
            throws IOException {
        if (model == Model.QUANTIZED_MOBILENET) {
            return new ClassifierQuantizedMobileNet(activity, device, numThreads, false,layoutName);
        } else if (model == Model.FLOAT_MOBILENET) {
            return new ClassifierFloatMobileNet(activity, device, numThreads,false,layoutName);
        } else if (model == Model.FLOAT_EFFICIENTNET) {
            return new ClassifierFloatEfficientNet(activity, device, numThreads,false,layoutName);
        } else if (model == Model.QUANTIZED_EFFICIENTNET) {
            return new ClassifierQuantizedEfficientNet(activity, device, numThreads,false,layoutName);
        } else if(model == Model.MY_MODEL){
            return new ClassifierMyModel(activity, device, numThreads,true,layoutName);
        } else {
            throw new UnsupportedOperationException();
        }


    }

    /** An immutable result returned by a Classifier describing what was recognized. */
    public static class Recognition {
        /**
         * A unique identifier for what has been recognized. Specific to the class, not the instance of
         * the object.
         */
        private final String id;

        /** Display name for the recognition. */
        private final String title;

        /**
         * A sortable score for how good the recognition is relative to others. Higher should be better.
         */
        private final Float confidence;

        /** Optional location within the source image for the location of the recognized object. */
        private RectF location;

        public Recognition(
                final String id, final String title, final Float confidence, final RectF location) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
            this.location = location;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Float getConfidence() {
            return confidence;
        }

        public RectF getLocation() {
            return new RectF(location);
        }

        public void setLocation(RectF location) {
            this.location = location;
        }

        @Override
        public String toString() {
            String resultString = "";
            if (id != null) {
                resultString += "[" + id + "] ";
            }

            if (title != null) {
                resultString += title + " ";
            }

            if (confidence != null) {
                resultString += String.format("(%.1f%%) ", confidence * 100.0f);
            }

            if (location != null) {
                resultString += location + " ";
            }

            return resultString.trim();
        }
    }

    /** Initializes a {@code Classifier}. */
    protected Classifier(Activity activity, Device device, int numThreads,boolean myModel, String layoutName) throws IOException {
        //This line need to be modified.
        MappedByteBuffer tfliteModel;
        if(myModel == true){
            ModelActivity m = new ModelActivity();
            tfliteModel = m.readModel(layoutName);
            labels = m.readLabels(layoutName);
        }else{
            tfliteModel = FileUtil.loadMappedFile(activity, getModelPath());
            labels = FileUtil.loadLabels(activity, getLabelPath());
        }


//        if(ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//            FileInputStream  f_input_stream = new FileInputStream(new File (Environment.getExternalStorageDirectory() + "/model/" + "model" + ".tflite"));
//            FileChannel  f_channel = f_input_stream.getChannel();
//            MappedByteBuffer tfliteModel = f_channel.map(FileChannel.MapMode.READ_ONLY, 0, f_channel .size());
//        }

//        FileInputStream  f_input_stream = new FileInputStream(new File (Environment.getExternalStorageDirectory() + "/model/" + "model" + ".tflite"));
//        FileChannel  f_channel = f_input_stream.getChannel();
//        MappedByteBuffer tfliteModel = f_channel.map(FileChannel.MapMode.READ_ONLY, 0, f_channel .size());


        switch (device) {
            case NNAPI:
                nnApiDelegate = new NnApiDelegate();
                tfliteOptions.addDelegate(nnApiDelegate);
                break;
            case GPU:
                gpuDelegate = new GpuDelegate();
                tfliteOptions.addDelegate(gpuDelegate);
                break;
            case CPU:
                break;
        }
        tfliteOptions.setNumThreads(numThreads);
        // Try to get model file from path
        tflite = new Interpreter(tfliteModel, tfliteOptions);

        // Loads labels out from the label file.
        //labels = FileUtil.loadLabels(activity, getLabelPath());

        // Reads type and shape of input and output tensors, respectively.
        int imageTensorIndex = 0;
        int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
        imageSizeY = imageShape[1];
        imageSizeX = imageShape[2];
        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();
        int probabilityTensorIndex = 0;
        int[] probabilityShape =
                tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
        DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

        // Creates the input tensor.
        inputImageBuffer = new TensorImage(imageDataType);

        // Creates the output tensor and its processor.
        outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);

        // Creates the post processor for the output probability.
        probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

        Log.d(TAG, "Created a Tensorflow Lite Image Classifier.");
    }



    /** Runs inference and returns the classification results. */
    public List<Recognition> recognizeImage(final Bitmap bitmap, int sensorOrientation) {
        // Logs this method so that it can be analyzed with systrace.
        Trace.beginSection("recognizeImage");

        Trace.beginSection("loadImage");
        long startTimeForLoadImage = SystemClock.uptimeMillis();
        inputImageBuffer = loadImage(bitmap, sensorOrientation);
        long endTimeForLoadImage = SystemClock.uptimeMillis();
        Trace.endSection();
        Log.v(TAG, "Timecost to load the image: " + (endTimeForLoadImage - startTimeForLoadImage));

        // Runs the inference call.
        Trace.beginSection("runInference");
        long startTimeForReference = SystemClock.uptimeMillis();
        tflite.run(inputImageBuffer.getBuffer(), outputProbabilityBuffer.getBuffer().rewind());
        long endTimeForReference = SystemClock.uptimeMillis();
        Trace.endSection();
        Log.v(TAG, "Timecost to run model inference: " + (endTimeForReference - startTimeForReference));

        // Gets the map of label and probability.
        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();
        Trace.endSection();

        // Gets top-k results.
        return getTopKProbability(labeledProbability);
    }

    /** Closes the interpreter and model to release resources. */
    public void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
        if (gpuDelegate != null) {
            gpuDelegate.close();
            gpuDelegate = null;
        }
        if (nnApiDelegate != null) {
            nnApiDelegate.close();
            nnApiDelegate = null;
        }
    }

    /** Get the image size along the x axis. */
    public int getImageSizeX() {
        return imageSizeX;
    }

    /** Get the image size along the y axis. */
    public int getImageSizeY() {
        return imageSizeY;
    }

    /** Loads input image, and applies preprocessing. */
    private TensorImage loadImage(final Bitmap bitmap, int sensorOrientation) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);

        // Creates processor for the TensorImage.
        int cropSize = min(bitmap.getWidth(), bitmap.getHeight());
        int numRotation = sensorOrientation / 90;
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        // TODO(b/169379396): investigate the impact of the resize algorithm on accuracy.
                        // To get the same inference results as lib_task_api, which is built on top of the Task
                        // Library, use ResizeMethod.BILINEAR.
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeMethod.NEAREST_NEIGHBOR))
                        .add(new Rot90Op(numRotation))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    /** Gets the top-k results. */
    private static List<Recognition> getTopKProbability(Map<String, Float> labelProb) {
        // Find the best classifications.
        PriorityQueue<Recognition> pq =
                new PriorityQueue<>(
                        MAX_RESULTS,
                        new Comparator<Recognition>() {
                            @Override
                            public int compare(Recognition lhs, Recognition rhs) {
                                // Intentionally reversed to put high confidence at the head of the queue.
                                return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                            }
                        });

        for (Map.Entry<String, Float> entry : labelProb.entrySet()) {
            pq.add(new Recognition("" + entry.getKey(), entry.getKey(), entry.getValue(), null));
        }

        final ArrayList<Recognition> recognitions = new ArrayList<>();
        int recognitionsSize = min(pq.size(), MAX_RESULTS);
        for (int i = 0; i < recognitionsSize; ++i) {
            recognitions.add(pq.poll());
        }
        return recognitions;
    }




//    private byte[] getModelContent(String path) throws IOException{
//        File file = new File(path);
//        if(!file.exists()) {
//            Log.e(TAG,"File doesn't exist!");
//            return null;
//        }
//        try {
//            FileInputStream in = new FileInputStream(file);
//            long inSize = in.getChannel().size();//判断FileInputStream中是否有内容
//            if (inSize == 0) {
//                Log.d(TAG,"The FileInputStream has no content!");
//                return null;
//            }
//
//            byte[] buffer = new byte[in.available()];//in.available() 表示要读取的文件中的数据长度
//            in.read(buffer);  //将文件中的数据读到buffer中
//            return buffer;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        } finally {
//            try {
//                in.close();
//            } catch (IOException e) {
//                return null;
//            }
//            //或IoUtils.closeQuietly(in);
//        }
//
//
//    }
//    private MappedByteBuffer getModelContent(String path) throws IOException{
//        File file = new File(path);
//        MappedByteBuffer content = null;
//        if (!file.isDirectory()) {  //检查此路径名的文件是否是一个目录(文件夹)
//            if (file.getName().endsWith("tflite")) {//文件格式为""文件
//                FileInputStream inputStream = new FileInputStream(file);
//                FileChannel fileChannel = inputStream.getChannel();
//                content = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
//
//                inputStream.close();
//            }
//        }
//
//        return content;
//
//    }

//    private MappedByteBuffer getFileContent(File file) {
//        MappedByteBuffer var9 = null;
//        if (!file.isDirectory()) {  //检查此路径名的文件是否是一个目录(文件夹)
//            if (file.getName().endsWith("tflite")) {//文件格式为""文件
//                try {
//                    FileInputStream inputStream = new FileInputStream(file);
//
//                    try {
//                        FileChannel fileChannel = inputStream.getChannel();
//                        var9 = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
//                    } catch (Throwable var12) {
//                        try {
//                            inputStream.close();
//                        } catch (Throwable var11) {
//                            var12.addSuppressed(var11);
//                        }
//
//                        throw var12;
//                    }
//
//                    inputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return var9;
//    }

    /** Gets the name of the model file stored in Assets. */
    protected abstract String getModelPath();

    /** Gets the name of the label file stored in Assets. */
    protected abstract String getLabelPath();

    /** Gets the TensorOperator to nomalize the input image in preprocessing. */
    protected abstract TensorOperator getPreprocessNormalizeOp();

    /**
     * Gets the TensorOperator to dequantize the output probability in post processing.
     *
     * <p>For quantized model, we need de-quantize the prediction with NormalizeOp (as they are all
     * essentially linear transformation). For float model, de-quantize is not required. But to
     * uniform the API, de-quantize is added to float model too. Mean and std are set to 0.0f and
     * 1.0f, respectively.
     */
    protected abstract TensorOperator getPostprocessNormalizeOp();
}