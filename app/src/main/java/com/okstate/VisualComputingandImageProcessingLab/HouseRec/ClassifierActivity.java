/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.okstate.VisualComputingandImageProcessingLab.HouseRec;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Size;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemPhotosActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.customview.OverlayView;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.customview.OverlayView.DrawCallback;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.env.BorderedText;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.env.ImageUtils;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.env.Logger;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.tflite.Classifier;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.tflite.Classifier.Device;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.tflite.Classifier.Model;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.tracking.MultiBoxTracker;

import org.tensorflow.lite.examples.detection.tflite.Detector;
import org.tensorflow.lite.examples.detection.tflite.TFLiteObjectDetectionAPIModel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public  class  ClassifierActivity extends CameraActivityBackupRT implements OnImageAvailableListener {
  private static final Logger LOGGER = new Logger();
  private static final Size DESIRED_PREVIEW_SIZE = new Size(40, 180);
  private static final float TEXT_SIZE_DIP = 10;
  private Bitmap rgbFrameBitmap = null;
//  private long lastProcessingTimeMs;
  private Integer sensorOrientation;
  private Classifier classifier;
  private BorderedText borderedText;
  /** Input image size of the model along x axis. */
  private int imageSizeX;
  /** Input image size of the model along y axis. */
  private int imageSizeY;
  /** Get model from server */
  private Button get_model;
  OverlayView trackingOverlay;
  public String layoutName;
  private boolean computingDetection = false;
  private static final String TF_OD_API_MODEL_FILE = "detect.tflite";
  private static final String TF_OD_API_LABELS_FILE = "labelmap.txt";
  private static final int TF_OD_API_INPUT_SIZE = 300;
  private static final boolean TF_OD_API_IS_QUANTIZED = true;
  private long timestamp = 0;
  private Detector detector;
  private Bitmap FrameCopyBitmap = null;
  private Matrix cropToFrameTransform;
  private Matrix frameToCropTransform;
  private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
  private MultiBoxTracker tracker;
  private static final boolean MAINTAIN_ASPECT = false;
  private static final boolean SAVE_PREVIEW_BITMAP = false;
  int cropSize = TF_OD_API_INPUT_SIZE;
  @Override
  protected int getLayoutId() {
    return R.layout.camera_connection_fragment;
  }

  @Override
  protected Size getDesiredPreviewFrameSize() {
    return DESIRED_PREVIEW_SIZE;
  }

  @Override
  public void onPreviewSizeChosen(final Size size, final int rotation) {
    Intent iin= getIntent();
    Bundle b = iin.getExtras();
    if ( (String) b.get(ItemPhotosActivity.LAYOUT_NAME) != null){
      layoutName = (String) b.get(ItemPhotosActivity.LAYOUT_NAME);
    }
    final float textSizePx =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    borderedText = new BorderedText(textSizePx);
    borderedText.setTypeface(Typeface.MONOSPACE);

    recreateClassifier(getModel(), getDevice(), getNumThreads(),isMyModel(),layoutName);
    if (classifier == null) {
      LOGGER.e("No classifier on preview!");
      return;
    }

    try {
      detector =
              TFLiteObjectDetectionAPIModel.create(
                      this,
                      TF_OD_API_MODEL_FILE,
                      TF_OD_API_LABELS_FILE,
                      TF_OD_API_INPUT_SIZE,
                      TF_OD_API_IS_QUANTIZED);
              cropSize = TF_OD_API_INPUT_SIZE;
    } catch (final IOException e) {
      e.printStackTrace();
      LOGGER.e(e, "Exception initializing Detector!");
      Toast toast =
              Toast.makeText(
                      getApplicationContext(), "Detector could not be initialized", Toast.LENGTH_SHORT);
      toast.show();
      finish();
    }

    previewWidth = size.getWidth();
    previewHeight = size.getHeight();

    sensorOrientation = rotation - getScreenOrientation();
    LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

    LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
    rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);

    frameToCropTransform =
            ImageUtils.getTransformationMatrix(
                    previewWidth, previewHeight,
                    previewWidth, previewHeight,
                    sensorOrientation, MAINTAIN_ASPECT);

    cropToFrameTransform = new Matrix();
    frameToCropTransform.invert(cropToFrameTransform);

//    tracker = new MultiBoxTracker(this);

//    trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
//    trackingOverlay.addCallback(
//            new OverlayView.DrawCallback() {
//              @Override
//              public void drawCallback(final Canvas canvas) {
//                tracker.draw(canvas);
//                if (isDebug()) {
//                  tracker.drawDebug(canvas);
//                }
//              }
//            });

//    tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);

  }

  @Override
  protected void processImage() {

    ++timestamp;
    final long currTimestamp = timestamp;
    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
    final int cropSize = Math.min(previewWidth, previewHeight);
//    trackingOverlay.postInvalidate();
    // No mutex needed as this method is not reentrant.
    if (computingDetection) {
      readyForNextImage();
      return;
    }
    computingDetection = true;
    LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

    readyForNextImage();

    final Canvas canvas = new Canvas(rgbFrameBitmap);
    canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
    // For examining the actual TF input.
    if (SAVE_PREVIEW_BITMAP) {
      ImageUtils.saveBitmap(rgbFrameBitmap);
    }

    runInBackground(
            new Runnable() {
              @Override
              public void run() {
                //Get the object detection result
                LOGGER.i("Running detection on image " + currTimestamp);
                final long startTime = SystemClock.uptimeMillis();
                final List<Detector.Recognition> results = detector.recognizeImage(rgbFrameBitmap);
//            lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

//                FrameCopyBitmap = Bitmap.createBitmap(rgbFrameBitmap);
//                final Canvas canvas = new Canvas(FrameCopyBitmap);
//                final Paint paint = new Paint();
//                paint.setColor(Color.RED);
//                paint.setStyle(Paint.Style.STROKE);
//                paint.setStrokeWidth(2.0f);


                float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;

                final List<Detector.Recognition> mappedRecognitions =
                        new ArrayList<Detector.Recognition>();

                for (final Detector.Recognition result : results) {
                  final RectF location = result.getLocation();
                  if (location != null && result.getConfidence() >= minimumConfidence) {
//                    canvas.drawRect(location, paint);

//                    cropToFrameTransform.mapRect(location);

                    result.setLocation(location);
                    mappedRecognitions.add(result);
                  }
                }

                computingDetection = false;
                //Get classifier result
                if(ActivityCompat.shouldShowRequestPermissionRationale(ClassifierActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                  ActivityCompat.requestPermissions(ClassifierActivity.this,
                          new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }

                if (classifier != null) {
                  final List<Classifier.Recognition> resultsClassifer =
                          classifier.recognizeImage(rgbFrameBitmap, sensorOrientation);
                  LOGGER.v("Detect: %s", resultsClassifer);

                  runOnUiThread(
                          new Runnable() {
                            @Override
                            public void run() {
//                              showResultsInBottomSheet(resultsClassifer,mappedRecognitions);
                            }
                          });
                }
                readyForNextImage();
              }
            });
  }

  @Override
  protected void onInferenceConfigurationChanged() {
    Intent iin= getIntent();
    Bundle b = iin.getExtras();
    if ( (String) b.get(ItemPhotosActivity.LAYOUT_NAME) != null){
      layoutName = (String) b.get(ItemPhotosActivity.LAYOUT_NAME);
    }

    if (rgbFrameBitmap == null) {
      // Defer creation until we're getting camera frames.
      return;
    }
    final Device device = getDevice();
    final Model model = getModel();
    final int numThreads = getNumThreads();
    final boolean isMymodel = isMyModel();
    runInBackground(new Runnable() {
      @Override
      public void run() {
        ClassifierActivity.this.recreateClassifier(model, device, numThreads,isMymodel,layoutName);
      }
    });
  }

  private void recreateClassifier(Model model, Device device, int numThreads,boolean isMymodel,String layoutName) {

    if (classifier != null) {
      LOGGER.d("Closing classifier.");
      classifier.close();
      classifier = null;
    }
    if (device == Device.GPU
        && (model == Model.QUANTIZED_MOBILENET || model == Model.QUANTIZED_EFFICIENTNET)) {
      LOGGER.d("Not creating classifier: GPU doesn't support quantized models.");
      runOnUiThread(
              new Runnable() {
                @Override
                public void run() {
                  Toast.makeText(ClassifierActivity.this, R.string.tfe_ic_gpu_quant_error, Toast.LENGTH_LONG).show();
                }
              });
      return;
    }
    try {
      LOGGER.d(
          "Creating classifier (model=%s, device=%s, numThreads=%d)", model, device, numThreads);
      classifier = Classifier.create(this, model, device, numThreads,isMymodel,layoutName);
    } catch (IOException | IllegalArgumentException e) {
      LOGGER.e(e, "Failed to create classifier.");
      runOnUiThread(
              new Runnable() {
                @Override
                public void run() {
                  Toast.makeText(ClassifierActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
              });
      return;
    }


    // Updates the input image size.
    imageSizeX = classifier.getImageSizeX();
    imageSizeY = classifier.getImageSizeY();
  }
  // Which detection model to use: by default uses Tensorflow Object Detection API frozen
  // checkpoints.
  private enum DetectorMode {
    TF_OD_API;
  }




}
