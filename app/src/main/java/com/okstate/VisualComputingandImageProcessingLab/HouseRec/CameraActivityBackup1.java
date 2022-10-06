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
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutDbHelper;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemPhotosActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.CourseAdapter;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.CourseModal;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.ToDoItem;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.VoiceProcessing.TTS;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.content.AppContent;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.env.ImageUtils;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.env.Logger;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.tflite.Classifier.Device;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.tflite.Classifier.Model;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.tflite.Classifier.Recognition;
import com.pierfrancescosoffritti.androidyoutubeplayer.utils.YouTubePlayerTracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;
import io.hamed.floatinglayout.FloatingLayout;
import io.hamed.floatinglayout.callback.FloatingListener;

import static com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemPhotosActivity.LAYOUT_NAME;
import static com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemPhotosActivity.getContext;
import static com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.LayoutWrapContentUpdater.wrapContentAgain;

public abstract class CameraActivityBackup1 extends AppCompatActivity
        implements OnImageAvailableListener,
        Camera.PreviewCallback,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener {
  private static final Logger LOGGER = new Logger();

  private static final int PERMISSIONS_REQUEST = 1;

  private File layoutfile;
  private File locationfile;
  public String layoutName;

  private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
  protected int previewWidth = 0;
  protected int previewHeight = 0;
  private Handler handler;
  private HandlerThread handlerThread;
  private boolean useCamera2API;
  private boolean isProcessingFrame = false;
  private byte[][] yuvBytes = new byte[3][];
  private int[] rgbBytes = null;
  private int yRowStride;
  private Runnable postInferenceCallback;
  private Runnable imageConverter;
  private LinearLayout bottomSheetLayout;
  private LinearLayout gestureLayout;
  private BottomSheetBehavior<LinearLayout> sheetBehavior;
  protected TextView recognitionTextView,
          recognition1TextView,
          recognition2TextView,
          recognitionValueTextView,
          recognition1ValueTextView,
          recognition2ValueTextView;
  //  protected TextView frameValueTextView,
//      cropValueTextView,
//      cameraResolutionTextView,
//      rotationTextView,
//      inferenceTimeTextView;
  protected LinearLayout layoutImageView;
  protected ImageView bottomSheetArrowImageView;
  protected Button locationShow;
//  private ImageView plusImageView, minusImageView;
//  private Spinner modelSpinner;
//  private Spinner deviceSpinner;
//  private TextView threadsTextView;

  private Model model = Model.MY_MODEL;
  private Device device = Device.CPU;
  private int numThreads = -1;
  boolean isMymodel;
  private int numFrame = 0;
  private double maxConfidence = 0;
  //  private String[] estimateName = new String[5];
  private ArrayList<String> estimateName = new ArrayList<>();
  private ArrayList<Float> estConfidence = new ArrayList<Float>();
  private TextToSpeech textToSpeech;
  WindowManager windowManager;
  WindowManager.LayoutParams rootParams, buttonParams;
  RelativeLayout rootLayout;
  ImageView captureButton, closeButton;


  Camera camera = null;
  Camera.Parameters cameraParams;
  MediaRecorder mMediaRecorder;
  private CameraPreview mPreview;
  public static final int MEDIA_TYPE_IMAGE = 1;
  public static final int MEDIA_TYPE_VIDEO = 2;

  int xMargin = 0;
  int yMargin = 0;
  int statusBarHeight = 0;
  private boolean isRecording = false;
  boolean dragFlag = false;
  //private SpeechRecognition recognitionListener;
  SpeechRecognizer mRecognizer;
  public static String esFinalTemp = "null";

  private String lastEsfinal = null;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    LOGGER.d("onCreate " + this);
    super.onCreate(null);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    setContentView(R.layout.activity_main_test);

    //recognitionListener = new SpeechRecognition();

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
              Manifest.permission.RECORD_AUDIO)) {

      } else {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
      }
    }
    //startRecording();
    //runRecognizerSetup();
    Button startButton = (Button) findViewById(R.id.start_camera);

    startButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View view) {
//        Intent intent = new Intent(CameraActivityBackup1.this, CameraService.class);
//        startService(intent);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        statusBarHeight = (int) (24 * getResources().getDisplayMetrics().density);

        rootLayout = new RelativeLayout(CameraActivityBackup1.this);
        rootLayout.setId(View.generateViewId());
        rootParams = new WindowManager.LayoutParams(360, 480, android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        rootParams.gravity = Gravity.TOP | Gravity.START;

//        if (hasPermission()) {
//          setFragment();
//        } else {
//          requestPermission();
//        }

        camera = Camera.open(); //figure out how to fix
        camera.setDisplayOrientation(90);
        camera.setPreviewCallback(CameraActivityBackup1.this::onPreviewFrame);
        mPreview = new CameraPreview(CameraActivityBackup1.this, camera);
        rootLayout.addView(mPreview);
        windowManager.addView(rootLayout, rootParams);
        addDragFunction();
        //mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        //recognitionListener.reset(getApplicationContext());
      }
    });

    Button hideButton = (Button) findViewById(R.id.stop_camera);

    hideButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        if (rootLayout != null) {
          rootLayout.setVisibility(View.GONE);
        }
      }
    });




    /* Text to speach module */
    textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
      @Override
      public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
          int ttsLang = textToSpeech.setLanguage(Locale.US);

          if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                  || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "The Language is not supported!");
          } else {
            Log.i("TTS", "Language Supported.");
          }
          Log.i("TTS", "Initialization success.");
        } else {
          Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
        }
      }
    });


    bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
    gestureLayout = findViewById(R.id.gesture_layout);
//    sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
//    bottomSheetArrowImageView = findViewById(R.id.bottom_sheet_arrow);
    layoutImageView = findViewById(R.id.layout_pic);
    locationShow = findViewById(R.id.locationShow);

    Intent iin = getIntent();
    Bundle b = iin.getExtras();
    if ((String) b.get(ItemPhotosActivity.LAYOUT_NAME) != null) {
      layoutName = (String) b.get(ItemPhotosActivity.LAYOUT_NAME);
    }
//
//    layoutName = ItemPhotosActivity.LAYOUT_NAME;

    layoutfile = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutName + "/layout/" + layoutName + ".jpg");


    Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(layoutfile));
//    int width = bitmap.getWidth();
//    int height = bitmap.getHeight();
//    float scaleWidth= (float) 1.0;
//    float scaleHeight= (float) 1.0;
//    Matrix matrix = new Matrix();
//    matrix.postScale(scaleWidth,scaleHeight);
//    bitmap=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
    BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);
    layoutImageView.setBackground(bd);


    recognitionTextView = findViewById(R.id.detected_item);
    recognitionValueTextView = findViewById(R.id.detected_item_value);


    model = Model.valueOf("MY_MODEL");
    device = Device.valueOf("CPU");
    numThreads = Integer.parseInt("1");


  }

  protected int[] getRgbBytes() {
    imageConverter.run();
    return rgbBytes;
  }

  protected int getLuminanceStride() {
    return yRowStride;
  }

  protected byte[] getLuminance() {
    return yuvBytes[0];
  }

  /**
   * Callback for android.hardware.Camera API
   */
  @Override
  public void onPreviewFrame(final byte[] bytes, final Camera camera) {
    if (isProcessingFrame) {
      LOGGER.w("Dropping frame!");
      return;
    }

    try {
      // Initialize the storage bitmaps once when the resolution is known.
      if (rgbBytes == null) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        previewHeight = previewSize.height;
        previewWidth = previewSize.width;
        checkModel();
        rgbBytes = new int[previewWidth * previewHeight];
        onPreviewSizeChosen(new Size(previewSize.width, previewSize.height), 90);
      }
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      return;
    }

    isProcessingFrame = true;
    yuvBytes[0] = bytes;
    yRowStride = previewWidth;

    imageConverter =
            new Runnable() {
              @Override
              public void run() {
                ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
              }
            };

    postInferenceCallback =
            new Runnable() {
              @Override
              public void run() {
                camera.addCallbackBuffer(bytes);
                isProcessingFrame = false;
              }
            };
    processImage();
  }

  /**
   * Callback for Camera2 API
   */
  @Override
  public void onImageAvailable(final ImageReader reader) {
    // We need wait until we have some size from onPreviewSizeChosen
    if (previewWidth == 0 || previewHeight == 0) {
      return;
    }
    if (rgbBytes == null) {
      rgbBytes = new int[previewWidth * previewHeight];
    }
    try {
      final Image image = reader.acquireLatestImage();

      if (image == null) {
        return;
      }

      if (isProcessingFrame) {
        image.close();
        return;
      }
      isProcessingFrame = true;
      Trace.beginSection("imageAvailable");
      final Plane[] planes = image.getPlanes();
      fillBytes(planes, yuvBytes);
      yRowStride = planes[0].getRowStride();
      final int uvRowStride = planes[1].getRowStride();
      final int uvPixelStride = planes[1].getPixelStride();

      imageConverter =
              new Runnable() {
                @Override
                public void run() {
                  ImageUtils.convertYUV420ToARGB8888(
                          yuvBytes[0],
                          yuvBytes[1],
                          yuvBytes[2],
                          previewWidth,
                          previewHeight,
                          yRowStride,
                          uvRowStride,
                          uvPixelStride,
                          rgbBytes);
                }
              };

      postInferenceCallback =
              new Runnable() {
                @Override
                public void run() {
                  image.close();
                  isProcessingFrame = false;
                }
              };

      processImage();
    } catch (final Exception e) {
      LOGGER.e(e, "Exception!");
      Trace.endSection();
      return;
    }
    Trace.endSection();
  }

  @Override
  public synchronized void onStart() {
    LOGGER.d("onStart " + this);
    super.onStart();
  }

  @Override
  public synchronized void onResume() {
    LOGGER.d("onResume " + this);
    super.onResume();
    startBackgroundThread();
    handlerThread = new HandlerThread("inference");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
  }

  @Override
  public synchronized void onPause() {
    LOGGER.d("onPause " + this);

    handlerThread.quitSafely();
    try {
      handlerThread.join();
      handlerThread = null;
      handler = null;
    } catch (final InterruptedException e) {
      LOGGER.e(e, "Exception!");
    }

    super.onPause();
  }

  @Override
  public synchronized void onStop() {
    LOGGER.d("onStop " + this);
    super.onStop();
    stopBackgroundThread();



  }

  @Override
  public synchronized void onDestroy() {
    LOGGER.d("onDestroy " + this);
    if (rootLayout != null) {
      rootLayout.setVisibility(View.GONE);
    }
    super.onDestroy();
  }

  protected synchronized void runInBackground(final Runnable r) {
    if (handler != null) {
      handler.post(r);
    }
  }

  @Override
  public void onRequestPermissionsResult(
          final int requestCode, final String[] permissions, final int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSIONS_REQUEST) {
      if (allPermissionsGranted(grantResults)) {
        setFragment();
      } else {
        requestPermission();
      }
    }
  }

  private static boolean allPermissionsGranted(final int[] grantResults) {
    for (int result : grantResults) {
      if (result != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  private boolean hasPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED;
    } else {
      return true;
    }
  }


  private void requestPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
        Toast.makeText(
                CameraActivityBackup1.this,
                "Camera permission is required for this demo",
                Toast.LENGTH_LONG)
                .show();
      }
      if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
        Toast.makeText(
                CameraActivityBackup1.this,
                "Camera permission is required for this demo",
                Toast.LENGTH_LONG)
                .show();
      }
      requestPermissions(new String[]{PERMISSION_CAMERA}, PERMISSIONS_REQUEST);

    }
  }

  // Returns true if the device supports the required hardware level, or better.
  private boolean isHardwareLevelSupported(
          CameraCharacteristics characteristics, int requiredLevel) {
    int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
    if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
      return requiredLevel == deviceLevel;
    }
    // deviceLevel is not LEGACY, can use numerical sort
    return requiredLevel <= deviceLevel;
  }

  public void checkModel() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
      // Do the file write
      try {
        ModelActivity m = new ModelActivity();
        m.readModel(layoutName);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      // Request permission from the user
      ActivityCompat.requestPermissions(this,
              new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
    }
  }

  private String chooseCamera() {
    final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    try {
      for (final String cameraId : manager.getCameraIdList()) {
        final CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

        // We don't use a front facing camera in this sample.
        final Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
          continue;
        }

        final StreamConfigurationMap map =
                characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        if (map == null) {
          continue;
        }

        // Fallback to camera1 API for internal cameras that don't have full support.
        // This should help with legacy situations where using the camera2 API causes
        // distorted or otherwise broken previews.
        useCamera2API =
                (facing == CameraCharacteristics.LENS_FACING_EXTERNAL)
                        || isHardwareLevelSupported(
                        characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
        LOGGER.i("Camera API lv2?: %s", useCamera2API);
        return cameraId;
      }
    } catch (CameraAccessException e) {
      LOGGER.e(e, "Not allowed to access camera");
    }

    return null;
  }

  protected void setFragment() {
    String cameraId = chooseCamera();

    Fragment fragment;
    if (useCamera2API) {
      CameraConnectionFragment camera2Fragment =
              CameraConnectionFragment.newInstance(
                      new CameraConnectionFragment.ConnectionCallback() {
                        @Override
                        public void onPreviewSizeChosen(final Size size, final int rotation) {
                          previewHeight = size.getHeight();
                          previewWidth = size.getWidth();
                          CameraActivityBackup1.this.onPreviewSizeChosen(size, rotation);
                        }
                      },
                      this,
                      getLayoutId(),
                      getDesiredPreviewFrameSize());

      camera2Fragment.setCamera(cameraId);
      fragment = camera2Fragment;
    } else {
      fragment =
              new LegacyCameraConnectionFragment(this, getLayoutId(), getDesiredPreviewFrameSize());
    }

    getFragmentManager().beginTransaction().replace(rootLayout.getId(), fragment).commit();

  }

  protected void fillBytes(final Plane[] planes, final byte[][] yuvBytes) {
    // Because of the variable row stride it's not possible to know in
    // advance the actual necessary dimensions of the yuv planes.
    for (int i = 0; i < planes.length; ++i) {
      final ByteBuffer buffer = planes[i].getBuffer();
      if (yuvBytes[i] == null) {
        LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
        yuvBytes[i] = new byte[buffer.capacity()];
      }
      buffer.get(yuvBytes[i]);
    }
  }

  protected void readyForNextImage() {
    if (postInferenceCallback != null) {
      postInferenceCallback.run();
    }
  }

  protected int getScreenOrientation() {
    switch (getWindowManager().getDefaultDisplay().getRotation()) {
      case Surface.ROTATION_270:
        return 270;
      case Surface.ROTATION_180:
        return 180;
      case Surface.ROTATION_90:
        return 90;
      default:
        return 0;
    }
  }

  @UiThread
  protected void showResultsInBottomSheet(List<Recognition> results) {
    if (results != null && results.size() >= 3) {
      String esFinal = null;
      Recognition recognition = results.get(0);
      if (recognition.getConfidence() != null && recognition.getConfidence() > 0.7) {
        if (recognition != null) {
          numFrame = numFrame + 1;
          estimateName.add(recognition.getTitle().toLowerCase());
          estConfidence.add(recognition.getConfidence());
        }
        int sizeest = estimateName.size();
        if (numFrame <= 30) {
          Map<String, Integer> esMap = new HashMap<>();
          for (String str : estimateName) {
            Integer num = esMap.get(str);
            esMap.put(str, num == null ? 1 : num + 1);
          }
          Set set = esMap.entrySet();
          Iterator it = set.iterator();
          int moreTLo = 0;
          while (it.hasNext()) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
            if (entry.getValue() >= moreTLo) {
              moreTLo = entry.getValue();
              esFinal = entry.getKey();
              esFinalTemp = entry.getKey();
            }
          }
          maxConfidence = Collections.max(estConfidence);
        } else {
          Map<String, Integer> esMap = new HashMap<>();
          for (String str : estimateName.subList(sizeest - 30, sizeest)) {
            Integer num = esMap.get(str);
            esMap.put(str, num == null ? 1 : num + 1);
          }
          Set set = esMap.entrySet();
          Iterator it = set.iterator();
          int moreTLo = 0;
          while (it.hasNext()) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
            if (entry.getValue() >= moreTLo) {
              moreTLo = entry.getValue();
              esFinal = entry.getKey();
              esFinalTemp = entry.getKey();
            }
          }
          maxConfidence = Collections.max(estConfidence.subList(sizeest - 30, sizeest));
        }
      }
      if (esFinal != null) {
        Integer itemNum = AppContent.getInstance().LAYOUT_MAP.get(layoutName).size();
        recognitionTextView.setText("Your location: ");
        recognitionValueTextView.setText(esFinal + " " + String.format("%.5f", maxConfidence));
          /*
          if (!esFinal.equals(lastEsfinal) || esFinal.equals(null)){
            setTextToSpeech("The location in front of you is " + esFinal);

          }
          */


        TTS.setTextToSpeech1((" "));
        //Activates microphone on camera frame change
        reset(this);
        locationfile = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutName + "/layout/" + layoutName + ".txt");
        boolean flag = false;
        for (int i = 0; i < itemNum; i++) {
          String tempName = AppContent.getInstance().LAYOUT_MAP.get(layoutName).get(i).name.toLowerCase();
          if (esFinal.indexOf(tempName) != -1) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
              try {
                BufferedReader in = new BufferedReader(new FileReader(locationfile));
                String line;
                int lineCount = 0;
                while ((line = in.readLine()) != null) {
                  // process the line.
                  if (lineCount == i) {
                    locationShow.setVisibility(View.VISIBLE);
                    String locationArray[] = line.split("-");
                    locationShow.setX(Float.parseFloat(locationArray[1]));
                    locationShow.setY(Float.parseFloat(locationArray[2]));
                    locationShow.setText(tempName);
                    locationShow.setTextColor(Color.BLACK);
                    flag = true;
                    break;
                  }
                  lineCount++;
                }
                in.close();
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          }
          lastEsfinal = esFinal;
          if (flag) break;
        }
      }
    } else if (maxConfidence < 0.25) {
      recognitionValueTextView.setText(
              "Unknown Place");
    }
  }

  public static String getLocationName() {

    return esFinalTemp;
  }


  protected Model getModel() {
    return model;
  }

  private void setModel(Model model) {
    if (this.model != model) {
      LOGGER.d("Updating  model: " + model);
      this.model = model;
      if (this.model == Model.MY_MODEL) {
        isMymodel = true;
      } else {
        isMymodel = false;
      }
      onInferenceConfigurationChanged();
    }
  }

  protected Device getDevice() {
    return device;
  }

  private void setDevice(Device device) {
    if (this.device != device) {
      LOGGER.d("Updating  device: " + device);
      this.device = device;
      final boolean threadsEnabled = device == Device.CPU;
//      plusImageView.setEnabled(threadsEnabled);
//      minusImageView.setEnabled(threadsEnabled);
//      threadsTextView.setText(threadsEnabled ? String.valueOf(numThreads) : "N/A");
      onInferenceConfigurationChanged();
    }
  }

  protected int getNumThreads() {
    return numThreads;
  }

  protected boolean isMyModel() {
    return isMymodel;
  }

  private void setNumThreads(int numThreads) {
    if (this.numThreads != numThreads) {
      LOGGER.d("Updating  numThreads: " + numThreads);
      this.numThreads = numThreads;
      onInferenceConfigurationChanged();
    }
  }

  /* Function to utilize tts module */
  private void setTextToSpeech(String s) {
    int speechStatus = textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);

    if (speechStatus == TextToSpeech.ERROR) {
      Log.e("TTS", "Error in converting Text to Speech!");
    }

  }


  protected abstract void processImage();

  protected abstract void onPreviewSizeChosen(final Size size, final int rotation);

  protected abstract int getLayoutId();

  protected abstract Size getDesiredPreviewFrameSize();

  protected abstract void onInferenceConfigurationChanged();

  @Override
  public void onClick(View v) {
//    if (v.getId() == R.id.plus) {
//      String threads = threadsTextView.getText().toString().trim();
//      int numThreads = Integer.parseInt(threads);
//      if (numThreads >= 9) return;
//      setNumThreads(++numThreads);
//      threadsTextView.setText(String.valueOf(numThreads));
//    } else if (v.getId() == R.id.minus) {
//      String threads = threadsTextView.getText().toString().trim();
//      int numThreads = Integer.parseInt(threads);
//      if (numThreads == 1) {
//        return;
//      }
//      setNumThreads(--numThreads);
//      threadsTextView.setText(String.valueOf(numThreads));
//    }
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//    if (parent == modelSpinner) {

    setModel(Model.valueOf("MY_MODEL"));

//    } else if (parent == deviceSpinner) {
    setDevice(Device.valueOf("CPU"));
//    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }

  void addDragFunction() {
    rootLayout.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
          xMargin = (int) motionEvent.getX();
          yMargin = (int) motionEvent.getY();
          dragFlag = rootParams.height - yMargin < dpToPx(20) && rootParams.width - xMargin < dpToPx(20);
        }

        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
          int rawX = (int) motionEvent.getRawX();
          int rawY = (int) motionEvent.getRawY();
          int x = (int) motionEvent.getX();
          int y = (int) motionEvent.getY();
          WindowManager.LayoutParams rootParams = (WindowManager.LayoutParams) rootLayout.getLayoutParams();

          if (dragFlag && !isRecording) {
            rootParams.width = x;
            rootParams.height = y;
          } else {
            rootParams.x = rawX - xMargin;
            rootParams.y = rawY - yMargin - statusBarHeight;
          }
          windowManager.updateViewLayout(rootLayout, rootParams);

        }

        return true;
      }
    });
  }

  public int dpToPx(int dp) {
    return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
  }

  private void releaseCamera() {
    if (camera != null) {
      camera.release();        // release the camera for other applications
      camera = null;
    }
  }


  //varibles added here
  public static final int SYSTEM_ALERT_WINDOW_PERMISSION = 7;
  private Uri Video_Location;
  private static Uri Video_Location_Static;
  private Spinner spinner_video;
  private ToDoItem mPhotoLayoutPath;
  private String ITEM_ID;
  public static final String LAYOUT_ITEM = "LAYOUT_ITEM";
  public static final String ARG_ITEM_ID = "ITEM_ID";
  public EditText Url;
  //LinearLayout videoContainer;
  //ArrayList<DummyContents> dummyContents = new ArrayList<>();
  ScrollView scrollView;
  YouTubePlayerTracker mTracker = null;
  private Boolean IsFullScreen;
  private ArrayList<CourseModal> courseModalArrayList;

  // private EditText Url = findViewById(R.id.url_prompt);
  String TestType = "Working";
  private FloatingLayout floatingLayout;
  private VideoView videoView;
  public int video_to_play;
  private RelativeLayout videoContainer;
  private FrameLayout RelativelayoutContainer;

  private Button addBtn, saveBtn;
  private EditText courseNameEdt, courseDescEdt;
  //private Button addBtn, saveBtn;
  private RecyclerView courseRV;
  private int seekForwardTime; // 5000 milliseconds
  private int seekBackwardTime; // 5000 milliseconds
  // variable for our adapter class and array list
  private CourseAdapter adapter;
  //MediaController m;
  private ImageButton btn_close, resume, btnfoward, btnback, pause, fullscreen, replay;
  private MediaPlayer mp;
  private boolean IsFloating = false;
  private View test_view;
  private MediaController mediaControls;
  private MediaPlayer mediaPlayer;
  private List<String> labels;
  private HandlerThread backgroundThread;
  private Handler backgroundHandler;
  private Thread recordingThread;
  //varibles addeded stopped


  //Extra Code added below here


  private FloatingListener floatingListener = new FloatingListener() {
    @Override
    public void onCreateListener(View view) {
      initVideoView(view);
      btn_close = view.findViewById(R.id.btn_close);
      pause = view.findViewById(R.id.test_button);
      resume = view.findViewById(R.id.resume_play);
      btnfoward = view.findViewById(R.id.fowardbtn);
      btnback = view.findViewById(R.id.backbtn);
      fullscreen = view.findViewById(R.id.FullscreenButton);
      replay = view.findViewById(R.id.replay);
      test_view = view;
      IsFullScreen = false;


      btn_close.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Close_Video();
        }
      });


      pause.setOnClickListener(new View.OnClickListener() {


        @Override
        public void onClick(View v) {
          Pause_Video();
        }
      });
      resume.setOnClickListener(new View.OnClickListener() {


        @Override
        public void onClick(View v) {
          Play_Video();
        }
      });
      btnfoward.setOnClickListener(new View.OnClickListener() {


        @Override
        public void onClick(View v) {
          Skip_Forward();
        }
      });
      btnback.setOnClickListener(new View.OnClickListener() {


        @Override
        public void onClick(View v) {
          Skip_Back();
        }
      });
      fullscreen.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          FullScreen();
        }
      });

      replay.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          replay();
        }
      });


      //view.findViewById(R.id.backbtn).setOnClickListener(CameraActivityBackup1.this);
      //view.findViewById(R.id.btn_close).setOnClickListener(CameraActivityBackup1.this);
      // view.findViewById(R.id.fowardbtn).setOnClickListener(CameraActivityBackup1.this);
      //  view.findViewById(R.id.test_button).setOnClickListener(CameraActivityBackup1.this);
      //view.findViewById(R.id.resume_play).setOnClickListener(CameraActivityBackup1.this);
      //view.findViewById(R.id.fullscreen_2).setOnClickListener(AddItems_TempTest.this);
      // view.findViewById(R.id.replay).setOnClickListener(CameraActivityBackup1.this);
      //  view.findViewById(R.id.FullscreenButton).setOnClickListener(CameraActivityBackup1.this);
      resume.setVisibility(View.INVISIBLE);
      pause.setVisibility(View.VISIBLE);

      //  loop();

      IsFloating = true;

    }

    @Override
    public void onCloseListener() {
      //videoView.stopPlayback();
    }


  };


  private void initVideoView(View view) {
    videoView = view.findViewById(R.id.video_player);

    videoContainer = view.findViewById(R.id.root_container);
    RelativelayoutContainer = view.findViewById(R.id.FrameLayout_VideoView_container);
    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + video_to_play);
    //Changed uri to Video_Location for testing
    videoView.setVideoURI(Video_Location);
    videoView.start();
  }

  private void Start_Video(View view) {
    hideKeyboard(view);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    if (IsFloating) {
      floatingLayout.destroy();
    }
    if (!isNeedPermission()) {

      showFloating();

    } else {
      requestPermission_2();
    }

  }

  private void Close_Video() {

    floatingLayout.destroy();
    IsFloating = false;
    //showSystemUI();
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  }

  private void replay() {
    RestartVideo();
    Play();

  }

  private void Pause_Video() {

    Pause();
    pause.setVisibility(View.INVISIBLE);
    resume.setVisibility(View.VISIBLE);


  }

  private void Play_Video() {
    Play();
    resume.setVisibility(View.INVISIBLE);
    pause.setVisibility(View.VISIBLE);

  }

  private void Skip_Forward() {
    Forward();
    Play();
  }

  private void Skip_Back() {

    Back();
    Play();
  }

  private void Change_Fullscreen() {
    FullScreen();
  }

  private void Initialize_Video() {
    //change to speech input
    String valueFromSpinner = null;

    LayoutDbHelper db = new LayoutDbHelper(getApplicationContext());

    String directory = db.getDirectory(valueFromSpinner,LAYOUT_NAME);
    if (directory.equals("null")) {
      Toast.makeText((getApplicationContext()), (CharSequence) "No Directory Found. Using Sample Video.", Toast.LENGTH_SHORT).show();
      video_to_play = R.raw.matrix;
      Video_Location = Uri.parse("android.resource://" + getPackageName() + "/" + video_to_play);
    } else {
      Toast.makeText((getApplicationContext()), (CharSequence) "Video Ready to Play", Toast.LENGTH_SHORT).show();

      Video_Location = Uri.parse(directory);
    }
  }


  private void hideSystemUI() {
    // Enables regular immersive mode.
    // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
    // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    View decorView = getWindow().getDecorView();
    decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);


  }

  private void showSystemUI() {
    View decorView = getWindow().getDecorView();
    decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

  }

  private boolean isNeedPermission() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this);
  }

  private void requestPermission_2() {
    Intent intent = new Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + getPackageName())
    );
    startActivityForResult(intent, 25);
  }

  private void showFloating() {
    floatingLayout = new FloatingLayout(getApplicationContext(), R.layout.video_layout3);
    floatingLayout.setFloatingListener(floatingListener);
    floatingLayout.create();
  }


  private void FullScreen() {


    if (!(IsFullScreen)) {

      //hideSystemUI();
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
      //  DisplayMetrics metrics = new DisplayMetrics();
      //    getWindowManager().getDefaultDisplay().getMetrics(metrics);
      //    videoContainer.setLayoutParams(new FrameLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels));


      DisplayMetrics metrics = new DisplayMetrics();
      getWindowManager().getDefaultDisplay().getMetrics(metrics);
      android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) videoContainer.getLayoutParams();
      params.height = (metrics.widthPixels - (int) (50 * this.getResources().getDisplayMetrics().density));
      params.width = (metrics.heightPixels - (int) (10 * this.getResources().getDisplayMetrics().density));
      //params.leftMargin = 0;
      videoContainer.setLayoutParams(params);


      IsFullScreen = true;

    } else if (IsFullScreen) {
      //showSystemUI();
      //float pixels =  dp * this.getResources().getDisplayMetrics().density;
      FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoContainer.getLayoutParams();
      //  float factor = holder.itemView.getContext().getResources().getDisplayMetrics().density;
      params.height = (int) (200 * this.getResources().getDisplayMetrics().density);
      params.width = (int) (250 * this.getResources().getDisplayMetrics().density);
      videoContainer.setLayoutParams(params);
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      IsFullScreen = false;
    }
    wrapContentAgain(RelativelayoutContainer);
  }


  private void hideKeyboard(View v) {
    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
  }

  public void RuntimePermissionForUser() {

    Intent PermissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + getPackageName()));

    startActivityForResult(PermissionIntent, SYSTEM_ALERT_WINDOW_PERMISSION);
  }

  private void initializeUI() {


    LayoutDbHelper db = new LayoutDbHelper(getApplicationContext());


    labels = db.getAllLabels();

    db.close();

  }

  private void Pause() {

    videoView.pause();

  }

  private void Play() {

    videoView.start();

  }

  private void Back() {

    videoView.seekTo(videoView.getCurrentPosition());
    int currentPosition = videoView.getCurrentPosition();
    // check if seekForward time is lesser than song duration
    seekForwardTime = 10000;
    // forward song
    videoView.seekTo(currentPosition - seekForwardTime);

  }

  private void Forward() {

    videoView.seekTo(videoView.getCurrentPosition());
    int currentPosition = videoView.getCurrentPosition();
    // check if seekForward time is lesser than song duration
    seekForwardTime = 10000;
    // forward song
    videoView.seekTo(currentPosition + seekForwardTime);

  }

  private void RestartVideo() {

    videoView.seekTo(0);

  }


  public void label_found(String label) {
    LayoutDbHelper db = new LayoutDbHelper(getContext());

    String directory = db.getDirectory(label,LAYOUT_NAME);

    Video_Location = Uri.parse(directory);

  }

  public void play_video_2() {
    //hideKeyboard(view);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    if (IsFloating) {
      floatingLayout.destroy();
    }
    if (!isNeedPermission()) {

      showFloating();

    } else {
      requestPermission();
    }


  }


  public void video_check(boolean start) {
    if (start) {

      play_video_2();
    }

  }


//Kinda out of ideas and need this done today so we are speech recongnition into a method

  boolean command;
  int video = 0;
  //SpeechRecognizer mRecognizer;
  private static final String[] keywords_1 = new String[]{"hello", "hi"};
  private static final String[] keywords_2 = new String[]{"location", "where", "position"};
  private static final String[] keywords_3 = new String[]{"pause", "stop"};
  private static final String[] keywords_4 = new String[]{"resume", "continue", "unpause", "play"};
  private static final String[] keywords_5 = new String[]{"rewind", "reset", "restart"};
  private static final String[] keywords_6 = new String[]{"quit", "exit", "close"};
  private static final String[] keywords_7 = new String[]{"skip", "forward", "fast"};
  private static final String[] keywords_8 = new String[]{"back", "backwards"};
  private static final String[] keywords_9 = new String[]{"activities", "activity", "do"};
  //private static final String[] keywords_3 = new String[]{"yes", "no"};
  //private List<String> labels;
  private final RecognitionListener recognitionListener = new RecognitionListener() {
    @Override
    public void onReadyForSpeech(Bundle bundle) {
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float v) {
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
    }

    @Override
    public void onEndOfSpeech() {
      mRecognizer.destroy();

    }

    @Override
    public void onError(int i) {

    }


    @Override
    public void onPartialResults(Bundle partialResults) {
    }


    @Override
    public void onResults(Bundle results) {
      command = false;
      ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);


      if (matches != null && matches.size() > 0) {
        System.out.println(matches);
        for (int i = 0; i < matches.size(); i++) {
          // if (video == 0) {
          for (String k : keywords_1) {
            if (matches.get(i).contains(k.toLowerCase())) {
              TTS.setTextToSpeech1("Hello welcome to the House Rec App");
              matches = voice_reset(matches);
              ThreadSleep(4000);
              command = true;
              mRecognizer.destroy();
              //break;
            }


          }
          for (String k : keywords_2) {
            if (matches.get(i).contains(k.toLowerCase())) {

              TTS.setTextToSpeech1("The place in front of you is " + CameraActivityBackup1.getLocationName());
              matches = voice_reset(matches);
              ThreadSleep(3000);
              command = true;
              mRecognizer.destroy();
              //break;
            }
          }
          for (String k : labels) {

            //Toast.makeText(getContext(), esFinalTemp, Toast.LENGTH_SHORT).show();

            if (matches.get(i).contains(k)) {
              if (checkLayout(k) == true) {
                TTS.setTextToSpeech1("Would you like to play the video for " + k);
                video = 1;
                label_found(k);
                matches = voice_reset(matches);
                ThreadSleep(3000);
                command = true;

                mRecognizer.destroy();

              } else {
                TTS.setTextToSpeech1("You need to be in the" + getKeywordLocation(k));
                ThreadSleep(3000);
                matches = voice_reset(matches);
                command = true;
                mRecognizer.destroy();
              }

            }

          }
          if (video == 1) {

            //Toast.makeText(getContext(), i, Toast.LENGTH_SHORT).show();
            //Toast.makeText(getContext(), k, Toast.LENGTH_SHORT).show();
            if (matches.get(i).equals("yes")) {
              //Toast.makeText(getContext(), k, Toast.LENGTH_SHORT).show();

              TTS.setTextToSpeech1("Playing Video");
              video = 0;

              play_video_2();
              matches = voice_reset(matches);
              ThreadSleep(1000);
              command = true;
              mRecognizer.destroy();
              //break;
            } else if (matches.get(i).equals("no")) {
              TTS.setTextToSpeech1("Not Playing Video");
              video = 0;
              matches = voice_reset(matches);
              command = true;
              ThreadSleep(1000);
              mRecognizer.destroy();
              //break;

            }


          }
          if (IsFloating) {
            for (String k : keywords_3) {
              if (matches.get(i).contains(k.toLowerCase())) {
                Pause_Video();
                matches = voice_reset(matches);
                ThreadSleep(1000);
                command = true;
                mRecognizer.destroy();
                //break;
              }
            }

            for (String k : keywords_4) {
              if (matches.get(i).contains(k.toLowerCase())) {
                Play_Video();
                matches = voice_reset(matches);
                ThreadSleep(1000);
                command = true;
                mRecognizer.destroy();
                //break;
              }
            }
            for (String k : keywords_5) {
              if (matches.get(i).contains(k.toLowerCase())) {
                replay();
                matches = voice_reset(matches);
                ThreadSleep(1000);
                command = true;
                mRecognizer.destroy();
                //break;
              }
            }
            for (String k : keywords_6) {
              if (matches.get(i).contains(k.toLowerCase())) {
                Close_Video();
                matches = voice_reset(matches);
                ThreadSleep(1000);

                command = true;
                mRecognizer.destroy();
                //break;
              }
            }
            for (String k : keywords_7) {
              if (matches.get(i).contains(k.toLowerCase())) {
                Skip_Forward();
                matches = voice_reset(matches);
                ThreadSleep(1000);
                command = true;
                mRecognizer.destroy();
                //break;
              }
            }
            for (String k : keywords_8) {
              if (matches.get(i).contains(k.toLowerCase())) {
                Skip_Back();
                matches = voice_reset(matches);
                ThreadSleep(1000);
                command = true;
                mRecognizer.destroy();
                //break;
              }
            }
          }

          for (String k : keywords_9) {
            //Toast.makeText(getContext(), i, Toast.LENGTH_SHORT).show();
            if (matches.get(i).contains(k.toLowerCase())) {
              String tempesFinal = esFinalTemp.toLowerCase();

              tempesFinal = tempesFinal.replaceAll("\\s", "");
              //Toast.makeText(getContext(), tempesFinal, Toast.LENGTH_SHORT).show();
              List<String> tempActivies = getCurrentLocationLabels(tempesFinal);
              //Toast.makeText(getContext(), String.valueOf(tempActivies.size()), Toast.LENGTH_SHORT).show();
              matches = voice_reset(matches);
              if (tempActivies.size() == (1)) {

                TTS.setTextToSpeech1("You can do the activity ");
                ThreadSleep(1700);
                for (int p = 0; p < tempActivies.size(); p++) {
                  //Fix issue with texttospeech1

                  TTS.setTextToSpeech1(tempActivies.get(p));
                  ThreadSleep(750);
                }
              } else if (tempActivies.size() > 1) {

                TTS.setTextToSpeech1("You can do the activities ");
                ThreadSleep(1700);
                for (int p = 0; p < tempActivies.size(); p++) {
                  //Fix issue with texttospeech1

                  TTS.setTextToSpeech1(tempActivies.get(p));
                  ThreadSleep(750);
                }

              } else {
                TTS.setTextToSpeech1("there are no activities at this location");
                ThreadSleep(1000);
              }
              mRecognizer.destroy();
            }
          }

        }
      }
      //command = true;
      mRecognizer.destroy();
    }


    @Override
    public void onEvent(int i, Bundle bundle) {

    }
  };






  public void reset(Context context){

    ThreadSleep(500);

    command = false;
    initializeUI();

    mRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
    mRecognizer.setRecognitionListener(recognitionListener);

    Intent intent_voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent_voice.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName());
    intent_voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
    intent_voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
    intent_voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.getDefault().toString());
    intent_voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString());
    intent_voice.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,4);
    intent_voice.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,1000);
    intent_voice.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,1500);
    intent_voice.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,1000);

    mRecognizer.startListening(intent_voice);

  }


  private ArrayList<String> voice_reset(ArrayList<String> matches){
    for (int l = 0; l < matches.size(); l++) {
      matches.set(l, "null");
    }
    return matches;

  }
  private boolean checkLayout(String keyword){

    LayoutDbHelper db = new LayoutDbHelper(getContext());

    String templocation = db.getLocation(keyword,LAYOUT_NAME);

    templocation = templocation.toLowerCase();
    String tempesFinal = esFinalTemp.toLowerCase();

    tempesFinal = tempesFinal.replaceAll("\\s", "");
    templocation = templocation.replaceAll("\\s", "");

    if((tempesFinal).equalsIgnoreCase(templocation)){
      //Toast.makeText(getContext(), "True", Toast.LENGTH_SHORT).show();
      return true;

    }

    else{
      //Toast.makeText(getContext(), "False", Toast.LENGTH_SHORT).show();
      return false;
    }

  }

  private List<String> getCurrentLocationLabels(String Location){
      LayoutDbHelper db = new LayoutDbHelper(getContext());

      return db.getKeyword(Location,LAYOUT_NAME);
  }


  private String getCurrentLocation(String CurrentLocation){

    return CurrentLocation;

  }
  private String getKeywordLocation(String keyword){
    LayoutDbHelper db = new LayoutDbHelper(getContext());

    String templocation = db.getLocation(keyword,LAYOUT_NAME);
    return templocation;

  }

  private void ThreadSleep (int time) {

    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();

    }
  }
  private static final String HANDLE_THREAD_NAME = "CameraBackground";
  private void startBackgroundThread() {

    backgroundThread = new HandlerThread(HANDLE_THREAD_NAME);
    backgroundThread.start();
    backgroundHandler = new Handler(backgroundThread.getLooper());
  }


  private void stopBackgroundThread() {
    backgroundThread.quitSafely();
    try {
      backgroundThread.join();
      backgroundThread = null;
      backgroundHandler = null;
    } catch (InterruptedException e) {
      Log.e("amlan", "Interrupted when stopping background thread", e);
    }
  }


  public synchronized void startRecording() {
    if (recordingThread != null) {
      return;
    }
    //shouldContinueRecognition = true;
    recordingThread =
            new Thread(
                    new Runnable() {
                      @Override
                      public void run() {
                        reset(getContext());
                      }
                    });
    recordingThread.start();
  }
  public synchronized void stopRecognition() {
    if (recordingThread == null) {
      return;
    }
    //shouldContinueRecognition = false;
    recordingThread = null;
  }

//Real Time recognition round 2


  //private final RecognitionListener recognitionListener2 = new RecognitionListener() {
    /* We only need the keyphrase to start recognition, one menu with list of choices,
       and one word that is required for method switchSearch - it will bring recognizer
       back to listening for the keyphrase*/
    private static final String KWS_SEARCH = "wakeup";
    private static final String MENU_SEARCH = "menu";
    /* Keyword we are looking for to activate recognition */
    private static final String KEYPHRASE = "oh mighty computer";

    /* Recognition object */
    /*
    private edu.cmu.pocketsphinx.SpeechRecognizer recognizer;


    private void runRecognizerSetup() {
      // Recognizer initialization is a time-consuming and it involves IO,
      // so we execute it in async task
      new AsyncTask<Void, Void, Exception>() {
        @Override
        protected Exception doInBackground(Void... params) {
          try {
            Assets assets = new Assets(CameraActivityBackup1.this);
            File assetDir = assets.syncAssets();
            setupRecognizer(assetDir);
          } catch (IOException e) {
            return e;
          }
          return null;
        }

        @Override
        protected void onPostExecute(Exception result) {
          if (result != null) {
            System.out.println(result.getMessage());
          } else {
            switchSearch(KWS_SEARCH);
          }
        }
      }.execute();
    }


    private void setupRecognizer(File assetsDir) throws IOException {
      recognizer = SpeechRecognizerSetup.defaultSetup()
              .setAcousticModel(new File(assetsDir, "en-us-ptm"))
              .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
              // Disable this line if you don't want recognizer to save raw
              // audio files to app's storage
              //.setRawLogDir(assetsDir)
              .getRecognizer();
      recognizer.addListener((edu.cmu.pocketsphinx.RecognitionListener) getContext());
      // Create keyword-activation search.
      recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
      // Create your custom grammar-based search
      File menuGrammar = new File(assetsDir, "mymenu.gram");
      recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
    }



    public void onPartialResult(Hypothesis hypothesis) {
      if (hypothesis == null)
        return;
      String text = hypothesis.getHypstr();
      if (text.equals(KEYPHRASE))
        switchSearch(MENU_SEARCH);
      else {
        System.out.println(hypothesis.getHypstr());
      }
      if (text.equals(KEYPHRASE)) {
        switchSearch(MENU_SEARCH);
      } else if (text.equals("hello")) {
        System.out.println("Hello to you too!");
      } else if (text.equals("good morning")) {
        System.out.println("Good morning to you too!");
      } else {
        System.out.println(hypothesis.getHypstr());
      }

    }



    public void onResult(Hypothesis hypothesis) {
      if (hypothesis != null) {
        System.out.println(hypothesis.getHypstr());
      }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }


    @Override
    public void onEndOfSpeech() {
      if (!recognizer.getSearchName().equals(KWS_SEARCH))
        switchSearch(KWS_SEARCH);
    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle results) {

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    private void switchSearch(String searchName) {
      recognizer.stop();
      if (searchName.equals(KWS_SEARCH))
        recognizer.startListening(searchName);
      else
        recognizer.startListening(searchName, 10000);
    }



    public void onTimeout() {
      switchSearch(KWS_SEARCH);
    }
    //if (recognizer != null) {
    //  recognizer.cancel();
    //  recognizer.shutdown();
    //}

  };
*/
}


