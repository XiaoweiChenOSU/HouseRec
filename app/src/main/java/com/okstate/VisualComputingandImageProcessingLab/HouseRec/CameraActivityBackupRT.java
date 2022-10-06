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
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.AudioManager;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Message;
import android.os.SystemClock;
import android.widget.MediaController;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Trace;
import android.provider.Settings;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutDbHelper;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemDetailFragment;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemPhotosActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Main.MainActivity;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.CourseAdapter;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.CourseModal;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.ToDoItem;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.VoiceProcessing.TTS;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.content.AppContent;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.env.ImageUtils;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.env.Logger;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.fragment.OrientationFragment;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.tflite.Classifier;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.tflite.Classifier.Device;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.tflite.Classifier.Model;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.tflite.Classifier.Recognition;
import com.pierfrancescosoffritti.androidyoutubeplayer.utils.YouTubePlayerTracker;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.fragment.OrientationFragment;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.hamed.floatinglayout.FloatingLayout;
import io.hamed.floatinglayout.callback.FloatingListener;

import static com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemPhotosActivity.LAYOUT_NAME;
import static com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemPhotosActivity.getContext;
import static com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.LayoutWrapContentUpdater.wrapContentAgain;

import org.jetbrains.annotations.NotNull;
import org.tensorflow.lite.examples.detection.tflite.Detector;

import github.com.vikramezhil.dks.speech.Dks;
import github.com.vikramezhil.dks.speech.DksListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class CameraActivityBackupRT extends AppCompatActivity
        implements OnImageAvailableListener,
        Camera.PreviewCallback,
        View.OnClickListener
        {
    private static final Logger LOGGER = new Logger();

    private static final int PERMISSIONS_REQUEST = 1;

    private File layoutfile;
    private File locationfile;
    public String layoutName;

    private Integer frameFlag = 0;

    private String preObj;
    private String nowObj;
    private int objCon = 1;
    private boolean startFlag = true;

    private boolean debug = false;

    public TextToSpeech myTTS;

    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    protected int previewWidth = 0;
    protected int previewHeight = 0;
    private Handler handler;
    private HandlerThread handlerThread;
    private boolean useCamera2API;
    private boolean isProcessingFrame = false;
    private byte[][] yuvBytes = new byte[3][];
    private int[] rgbBytes = null;
    private byte[] baos = null;
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
            objectDetectTextView,
            recognition2ValueTextView;
    //  protected TextView frameValueTextView,
//      cropValueTextView,
//      cameraResolutionTextView,
//      rotationTextView,
//      inferenceTimeTextView;
    protected LinearLayout layoutImageView;
    protected ImageView locationIcon;
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
    private int obnumFrame = 0;
    private double maxConfidence = 0;
    //  private String[] estimateName = new String[5];
    private ArrayList<String> estimateName = new ArrayList<>();
    private ArrayList<Float> estConfidence = new ArrayList<Float>();
    private ArrayList<String> estimateObject = new ArrayList<>();
    private ArrayList<Float> ObConfidence = new ArrayList<Float>();
    private TextToSpeech textToSpeech;
    WindowManager windowManager;
    WindowManager.LayoutParams rootParams, buttonParams;
    RelativeLayout rootLayout;
    ImageView captureButton, closeButton;
    private boolean isNet = false;

    private String lastloc = null;

    private String locRecString;
    private String objRecString;

    Camera camera = null;
    Camera.Parameters cameraParams;
    MediaRecorder mMediaRecorder;
    private CameraPreview mPreview;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public Handler mHandler;
    int xMargin = 0;
    int yMargin = 0;
    int statusBarHeight = 0;
    private boolean isRecording = false;
    boolean dragFlag = false;
    //private SpeechRecognition recognitionListener;
    SpeechRecognizer mRecognizer;
    public static String esFinalTemp = "null";
    private AudioManager audioManager =null ;
    private String lastEsfinal = null;
    private Dks dks;

    private VideoView videoView;
    private MediaController mediaController;
    private Handler myHandler;
    private String[] results;

    private SensorManager sensorManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        LOGGER.d("onCreate " + this);
        super.onCreate(null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_assistant);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            }
        }


        mHandler = new Handler();
        new Thread(){
            @Override
            public void run(){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Start_Camera();
                    }
                });
            }
        }.start();


        mHandler = new Handler();
        new Thread(){
            @Override
            public void run(){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

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
                        //                    textToSpeech.getVoices();
                                    Voice voiceobj = new Voice("it-it-x-kda#female_2-local",
                                            Locale.getDefault(), 1, 1, true, null);
                                    textToSpeech.setVoice(voiceobj);
                                    Log.i("TTS", "Initialization success.");

                                } else {
                                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

//                        InitializeRTSpeech();
//                        dks.startSpeechRecognition();
                    }
                });
            }
        }.start();






        recognitionTextView = findViewById(R.id.detected_item);
        recognitionValueTextView = findViewById(R.id.detected_item_value);
        objectDetectTextView = findViewById(R.id.detected_object_value);
        locationIcon = findViewById(R.id.location_icon);


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


//        Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(layoutfile));




        model = Model.valueOf("MY_MODEL");
        device = Device.valueOf("CPU");
        numThreads = Integer.parseInt("1");


        mHandler = new Handler();
        new Thread(){
            @Override
            public void run(){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        videoView = (VideoView) findViewById(R.id.videoView);
                        mediaControls = new MediaController(CameraActivityBackupRT.this);
                        video_to_play = R.raw.coffee;
                        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + video_to_play);
                        videoView.setVideoURI(uri);
                        videoView.setMediaController(mediaController);
                        mediaControls.setMediaPlayer(videoView);
                        videoView.requestFocus();

                        resume = (ImageButton) findViewById(R.id.resume_play);

                        resume.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                videoView.setVisibility(View.VISIBLE);
                                videoView.start();
                            }
                        });

                        btn_close =  (ImageButton) findViewById(R.id.close_play);

                        btn_close.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                videoView.pause();
                                videoView.setVisibility(View.GONE);
                            }
                        });videoView = (VideoView) findViewById(R.id.videoView);
                        mediaControls = new MediaController(CameraActivityBackupRT.this);
                        video_to_play = R.raw.coffee;
                        uri = Uri.parse("android.resource://" + getPackageName() + "/" + video_to_play);
                        videoView.setVideoURI(uri);
                        videoView.setMediaController(mediaController);
                        mediaControls.setMediaPlayer(videoView);
                        videoView.requestFocus();

                        resume = (ImageButton) findViewById(R.id.resume_play);

                        resume.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                videoView.setVisibility(View.VISIBLE);
                                videoView.start();
                            }
                        });

                        btn_close =  (ImageButton) findViewById(R.id.close_play);

                        btn_close.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                videoView.pause();
                                videoView.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        }.start();




//        myHandler = new Handler() {
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case 1:
//                        getResults();
//                        break;
//                }
//                super.handleMessage(msg);
//            }
//        };


        mHandler = new Handler();
        new Thread(){
            @Override
            public void run(){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

                        if (checkSensorAvailability(Sensor.TYPE_GYROSCOPE)) {
                        } else {
                            displayHardwareMissingWarning();
                        }

                        if (savedInstanceState == null) {
                            OrientationFragment themeFragment = new OrientationFragment();
                            forwardFragment(themeFragment);
                        }
                    }
                });
            }
        }.start();

    }

    protected int[] getRgbBytes() {
        imageConverter.run();
        return rgbBytes;
    }

    protected byte[] getBytes() {
        imageConverter.run();
        return baos;
    }

//    protected String[] getResults() {
//        results = new String[2];
//        results[0] = locRecString;
//        results[1] = objRecString;
////        results[0] = "XiaoweiDesk";
////        results[1] = "CoffeeMaker";
//        return results;
//    }

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

        while(frameFlag <=10){
            frameFlag = frameFlag + 1;
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
        frameFlag = 0;
//        while(frameFlag <= 10){
//            frameFlag = frameFlag + 1;
//        }
//
//        byte[] baos = convertYuvToJpeg(bytes, camera);


//        String postUrl = "locDetection/"+ layoutName;
//        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//
//        long time= System.currentTimeMillis();
//
//        multipartBodyBuilder.addFormDataPart("image" + time, "_train.jpg", RequestBody.create(MediaType.parse("image/*jpg"), baos));
//
//        RequestBody postBodyImage = multipartBodyBuilder.build();
//        postRequest(postUrl, postBodyImage);
//
//        frameFlag = 0;
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
//            final Plane[] planes = image.getPlanes();
//            fillBytes(planes, yuvBytes);
//            yRowStride = planes[0].getRowStride();
//            final int uvRowStride = planes[1].getRowStride();
//            final int uvPixelStride = planes[1].getPixelStride();

            ByteBuffer buffer = image.getPlanes()[0].getBuffer();

            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            byte[] baos = convertYuvToJpeg(bytes, camera);

//            imageConverter =
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            ImageUtils.convertYUV420ToARGB8888(
//                                    yuvBytes[0],
//                                    yuvBytes[1],
//                                    yuvBytes[2],
//                                    previewWidth,
//                                    previewHeight,
//                                    yRowStride,
//                                    uvRowStride,
//                                    uvPixelStride,
//                                    rgbBytes);
//                        }
//                    };
//
//            postInferenceCallback =
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            image.close();
//                            isProcessingFrame = false;
//                        }
//                    };
//            processImage();

            String postUrl = "locDetection/"+ layoutName;
            MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            long time= System.currentTimeMillis();

            multipartBodyBuilder.addFormDataPart("image" + time, "_train.jpg", RequestBody.create(MediaType.parse("image/*jpg"), bytes));

            RequestBody postBodyImage = multipartBodyBuilder.build();
            postRequest(postUrl, postBodyImage);
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
//        dks.startSpeechRecognition();
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
//                setFragment();
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
                        CameraActivityBackupRT.this,
                        "Camera permission is required for this demo",
                        Toast.LENGTH_LONG)
                        .show();
            }
            if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
                Toast.makeText(
                        CameraActivityBackupRT.this,
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

                // Fallback to camera1 API for intmnnnnnnj bbbbbbbbbbb][ernal cameras that don't have full support.
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
                                    CameraActivityBackupRT.this.onPreviewSizeChosen(size, rotation);
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
    int count = 0;

//    @UiThread
//    protected void showResultsInBottomSheet(String locRecString, String objRecString) {
//
//        if (locRecString != null) {
//            String esFinal = locRecString;
//            recognitionTextView.setText(null);
//            locationIcon.setVisibility(View.VISIBLE);
//            recognitionValueTextView.setText(esFinal + " " );
//        }
//
//
//        if (objRecString != null) {
//            String obFinal = objRecString;
//            recognitionTextView.setText(null);
//            objectDetectTextView.setText(" " + obFinal);
//        }
//    }

    @UiThread
    protected void showResultsInBottomSheet(List<Recognition> results,List<Detector.Recognition> objectDetResults) {
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
                int framenum = 3;
                if (numFrame == framenum) {
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
                } else if (numFrame > framenum) {
                    Map<String, Integer> esMap = new HashMap<>();
                    for (String str : estimateName.subList(sizeest - framenum, sizeest)) {
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
                    maxConfidence = Collections.max(estConfidence.subList(sizeest - framenum, sizeest));
                }
            }
            if (esFinal != null && maxConfidence > 0.001) {
                recognitionTextView.setText(null);
                locationIcon.setVisibility(View.VISIBLE);
                recognitionValueTextView.setText(esFinal + " " );
                if(!esFinal.equals(lastloc)){
                    setTextToSpeech("The location in front of you is " + esFinal);
                }
                if (objectDetResults != null) {
                    String obFinal = null;
                    if (objectDetResults.size()>0) {
                        Detector.Recognition obrecognition = objectDetResults.get(0);
                        if (obrecognition.getConfidence() != null && obrecognition.getConfidence() > 0.2) {
                            obFinal = obrecognition.getTitle();
                        }
                    }
                    if (obFinal != null) {
                        String loc = CameraActivityBackupRT1210.getLocationName();
                        LayoutDbHelper db = new LayoutDbHelper(getApplicationContext());
                        List keyword = db.getKeyword(loc,layoutName);
                        Log.d("Current Location", loc);
                        Log.d("Current object", obFinal);
                        recognitionTextView.setText(null);
                        objectDetectTextView.setText(" " + obFinal);
                    }
                }
                lastloc = esFinal;
            }
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

            resume.setVisibility(View.INVISIBLE);
            pause.setVisibility(View.VISIBLE);


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
        resume.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.VISIBLE);
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

        String directory = db.getDirectory(valueFromSpinner, layoutName);
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

        String directory = db.getDirectory(label,layoutName);

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




    private ArrayList<String> voice_reset(ArrayList<String> matches){
        for (int l = 0; l < matches.size(); l++) {
            matches.set(l, "nullpoint");
        }
        return matches;

    }
    private boolean checkLayout(String keyword){

        LayoutDbHelper db = new LayoutDbHelper(getContext());

        String templocation = db.getLocation(keyword,layoutName);

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
        LayoutDbHelper db = new LayoutDbHelper(getApplication());
        return db.getKeyword(Location,layoutName);
    }


    private String getCurrentLocation(String CurrentLocation){

        return CurrentLocation;

    }
    private String getKeywordLocation(String keyword){
        LayoutDbHelper db = new LayoutDbHelper(getApplication());
        String templocation = db.getLocation(keyword,layoutName);
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
                               // reset(getContext());
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


    //Realtime Recognition round 3

    boolean command;
    int video = 0;
    //SpeechRecognizer mRecognizer;
    private static final String[] keywords_1 = new String[]{"hello", "hi"};
    private static final String[] keywords_2 = new String[]{ "begin", "start camera", "begin camera", "begin system","start system","camera","system"};
    private static final String[] keywords_3 = new String[]{"location", "where", "position"};
    private static final String[] keywords_4_1 = new String[]{"resume", "continue", "unpause", "play"};
    private static final String[] keywords_4_2 = new String[]{"pause", "stop"};
    private static final String[] keywords_5 = new String[]{"rewind", "reset", "restart"};
    private static final String[] keywords_6 = new String[]{"quit", "exit", "close"};
    private static final String[] keywords_7 = new String[]{"skip", "forward", "fast"};
    private static final String[] keywords_8 = new String[]{"back", "backwards"};
    private static final String[] keywords_9 = new String[]{"activities", "activity", "do"};
    private static final String[] keywords_10 = new String[]{"end", "shut down","shutdown"};
    private static final String[] keywords_11 = new String[]{"no", "negative"};
    private static final String[] keywords_12 = new String[]{"yes", "correct","positive"};


    private void InitializeRTSpeech(){
//        TTS.setTextToSpeech1(" ");
        HashMap<String, String> onlineSpeech = new HashMap<>();
        onlineSpeech.put(TextToSpeech.Engine.KEY_FEATURE_NETWORK_SYNTHESIS, "true");
        textToSpeech.speak("Hello welcome to the House Rec App", 1, onlineSpeech);
        initializeUI();
        dks = new Dks(getApplication(), getSupportFragmentManager(), new DksListener() {
            @Override
            public void onDksLiveSpeechResult(@NotNull String liveSpeechResult) {
                Log.d(getPackageName(), "Speech result - " + liveSpeechResult);
            }

            @Override
            public void onDksFinalSpeechResult(@NotNull String speechResult) {
                Log.d(getPackageName(), "Final Speech result - " + speechResult);
                speechResult= speechResult.toLowerCase();
                Log.d(getPackageName(), "Final Speech result Lower Case - " + speechResult);
                HashMap<String, String> onlineSpeech = new HashMap<>();
                onlineSpeech.put(TextToSpeech.Engine.KEY_FEATURE_NETWORK_SYNTHESIS, "true");

                command = false;
                ArrayList<String> matches = new ArrayList<String>(Arrays.asList(speechResult.split(" "))) ;


                if (matches != null && matches.size() > 0) {
                    System.out.println("-----------------------");
                    System.out.println(matches);

                    for (int i = 0; i < matches.size(); i++) {
                        // if (video == 0) {

                        if (!matches.isEmpty()){

                            if(Check_Keyword_Lists(speechResult, keywords_1)){
                                Log.d(getPackageName(), "Attempting to run TTS for Keyword_1");
                                String id_2 = "hello";
                                new Thread(new Runnable(){
                                    @Override
                                    public void run(){
                                        try{
                                            textToSpeech.speak("Hello welcome to the House Rec App", 1, onlineSpeech);
                                            //textToSpeech.speak("Hello welcome to the House Rec App",1,null,id_2);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                //ThreadSleep(5000);
                                Manage_TTS(id_2);
                                matches.clear();
                                command = true;
                            }else if(Check_Keyword_Lists(speechResult, keywords_2)){

                                Log.d(getPackageName(), "start camera");
                                String id_2 = "camera";
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            textToSpeech.speak("After the camera opens, you can tell me what you want to do",1,onlineSpeech );
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                //ThreadSleep(5000);
                                Manage_TTS(id_2);
                                mHandler = new Handler();
                                new Thread(){
                                    @Override
                                    public void run(){
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Start_Camera();
                                            }
                                        });
                                    }
                                }.start();

                                matches.clear();
                                command = true;
                            }else if(Check_Keyword_Lists(speechResult, keywords_3)){
                                Log.d(getPackageName(), "Location Recognition");
                                String id_2 = "location";
                                String loc = CameraActivityBackupRT.getLocationName();
                                String TempLocation = CameraActivityBackupRT.getLocationName();
                                TempLocation = TempLocation.replaceAll("\\s", "");
                                Log.d(getPackageName(), TempLocation);

                                LayoutDbHelper db = new LayoutDbHelper(getApplicationContext());
                                //List<String> keyw = db.getKeyword(TempLocation);
                                List<String> keyw = getCurrentLocationLabels(loc);

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            textToSpeech.speak("The place in front of you is " + loc,1,onlineSpeech );
                                            //ThreadSleep(3000);
                                            if(keyw.size() == 0) {
                                                textToSpeech.speak(" Here there are no activities available ", 1, onlineSpeech);
                                                storevalue = 1;
                                            }
                                            else{
                                                textToSpeech.speak(" Here you can do something about ", 1, onlineSpeech);
                                                KeyWord_loop(keyw, id_2);
                                                if (keyw.size() == 1) {
                                                    textToSpeech.speak("do you want to play a video about it ", 1, onlineSpeech);
                                                    label_found(keyw.get(0));
                                                    video = 1;


                                                } else{
                                                    textToSpeech.speak("please specify a video you wish to play ", 1, onlineSpeech);
                                                }
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();

                                Manage_TTS(id_2);
                                matches.clear();
                            }else if(Check_Keyword_Lists(speechResult, (String[]) labels.toArray(new String[0]))){
                                String id_2 = "keyword";
                                //String speechr = matches.get(i);
                                String speechr =Keyword_Found;
                                String loc = CameraActivityBackupRT.getLocationName();
                                String Rloc = getKeywordLocation(speechr);
                                if (checkLayout(speechr)) {
                                    Log.d(getPackageName(), "Attempting to run TTS for " + speechr);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if(video != 1) {
                                                    textToSpeech.speak("Would you like to play the video for " + speechr, 1, onlineSpeech);
                                                    label_found(speechr);
                                                    video = 1;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                    matches.clear();
                                    Manage_TTS(id_2);
                                    //ThreadSleep(4000);
                                    command = true;
                                } else {
                                    Log.d(getPackageName(), "Attempting to run TTS for getKeywordLocation using " + speechr);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if(Rloc!= "null") {
                                                    textToSpeech.speak("You need to be in the " + Rloc + " location ", 1, onlineSpeech);
//                                                        video = 1;
                                                }
                                                else{
                                                    textToSpeech.speak(" There is no activity named "+speechr, 1, onlineSpeech);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                    Manage_TTS(id_2);
                                    //ThreadSleep(3000);
                                    matches.clear();
                                    command = true;
                                }
                            }else if(Check_Keyword_Lists(speechResult, keywords_9)) {
                                String loc = CameraActivityBackupRT.getLocationName();
                                List<String> tempActivies = getCurrentLocationLabels(loc);
                                //Toast.makeText(getContext(), String.valueOf(tempActivies.size()), Toast.LENGTH_SHORT).show();
                                String id_2 = "activity";
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (tempActivies.size() > 0) {
                                                textToSpeech.speak("You can do the activity ", 1, onlineSpeech);
                                                KeyWord_loop(tempActivies, id_2);
                                                //ThreadSleep(3000);
                                                if (tempActivies.size() == 1) {
                                                    textToSpeech.speak(" do you want to play a video for it ", 1, onlineSpeech);
                                                    label_found(tempActivies.get(0));
                                                    video = 1;
                                                } else {
                                                    textToSpeech.speak(" please specify a video you wish to play ", 1, onlineSpeech);
                                                }
                                            } else {
                                                textToSpeech.speak(" There are no activities at this location ", 1, onlineSpeech);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                matches.clear();
                                Manage_TTS(id_2);
                            }else if (Check_Keyword_Lists(speechResult, keywords_10)) {
                                String id_2 = "end";
                                Log.d(getPackageName(), "stop camera and speech listening");

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            textToSpeech.speak("The system is closing, you will go back to the home page", textToSpeech.QUEUE_FLUSH, onlineSpeech);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                Manage_TTS(id_2);
                                //ThreadSleep(5000);
                                speechResult = "null";
                                matches.clear();
                                dks.closeSpeechOperations();
                                //go back to
                                //adapt code further
                                Stop_Camera();
                                Intent intent = new Intent();
                                intent.setClass(getApplication(), MainActivity.class);
                                //intent.putExtra(ITEMLayout, mUserToDoItem);
                                startActivity(intent);

                                command = true;
                            }else if (video == 1) {
                                String id_2 = "video";
                                //Toast.makeText(getContext(), i, Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getContext(), k, Toast.LENGTH_SHORT).show();
                                if (Check_Keyword_Lists(speechResult, keywords_12)) {
                                    //Toast.makeText(getContext(), k, Toast.LENGTH_SHORT).show();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                textToSpeech.speak("Playing Video",1,onlineSpeech);
                                                play_video_2();
                                                video = 0;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                    matches.clear();
                                    Manage_TTS(id_2);
                                    //ThreadSleep(1000);
                                    command = true;
                                } else if (Check_Keyword_Lists(speechResult, keywords_11)) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                textToSpeech.speak("Not Playing Video",1,onlineSpeech);
                                                video = 0;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                    Manage_TTS(id_2);
                                    //ThreadSleep(2000);
                                    matches.clear();
                                    command = true;
                                }
                            }else if (IsFloating) {
                                if(Check_Keyword_Lists(speechResult, keywords_4_1)){
                                    Play_Video();
                                    matches.clear();
                                    ThreadSleep(500);
                                    command = true;
                                }else if(Check_Keyword_Lists(speechResult, keywords_4_2)){
                                    Pause_Video();
                                    matches.clear();
                                    ThreadSleep(500);
                                    command = true;
                                }else if(Check_Keyword_Lists(speechResult, keywords_5)){
                                    replay();
                                    matches.clear();
                                    ThreadSleep(500);
                                    command = true;
                                }else if(Check_Keyword_Lists(speechResult, keywords_6)){
                                    Close_Video();
                                    matches.clear();
                                    ThreadSleep(500);
                                    command = true;
                                }else if(Check_Keyword_Lists(speechResult, keywords_7)){
                                    Skip_Forward();
                                    matches.clear();
                                    ThreadSleep(500);
                                    command = true;
                                }else if(Check_Keyword_Lists(speechResult, keywords_8)){
                                    Skip_Back();
                                    matches.clear();
                                    ThreadSleep(500);
                                    command = true;
                                }
                            }else{
                                String id_2 = "again";
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            textToSpeech.speak("Sorry, I do not get you, can you speak again", textToSpeech.QUEUE_FLUSH, onlineSpeech);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                Manage_TTS(id_2);
                                matches.clear();
                                command = true;
                            }
                        }
                    }
                }
            }


            @Override
            public void onDksLiveSpeechFrequency(float frequency) {}

            @Override
            public void onDksLanguagesAvailable(@org.jetbrains.annotations.Nullable String defaultLanguage, @org.jetbrains.annotations.Nullable ArrayList<String> supportedLanguages) {
                Log.d(getPackageName(), "defaultLanguage - " + defaultLanguage);
                Log.d(getPackageName(), "supportedLanguages - " + supportedLanguages);

                if (supportedLanguages != null && supportedLanguages.contains("en-IN")) {
                    // Setting the speech recognition language to english india if found
                    dks.setCurrentSpeechLanguage("en-IN");
                }
            }

            @Override
            public void onDksSpeechError(@NotNull String errMsg) {
                Toast.makeText(getApplication(), errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }



    public static final String ITEMLayout = "com.okstate.VisualComputingandImageProcessingLab.HouseRec.ItemLayout";

    //camera test

    private void Start_Camera(){
        //        Intent intent = new Intent(CameraActivityBackupRT.this, CameraService.class);
//        startService(intent);
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
                                    CameraActivityBackupRT.this.onPreviewSizeChosen(size, rotation);
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

        getFragmentManager().beginTransaction().replace(R.id.cameraContainer, fragment).commit();
    }
    private void Stop_Camera(){
        super.onStop();

//        rootLayout.setVisibility(View.GONE);
//        rootLayout.removeView(mPreview);
//        windowManager.removeView(rootLayout);
//        rootLayout.setVisibility(View.VISIBLE);
    }

    private int storevalue = 0;

    private void Manage_TTS(String id){
        AudioManager audioManager = (AudioManager)CameraActivityBackupRT.this.getSystemService(Context.AUDIO_SERVICE);
        ThreadSleep(100);

        audioManager.setRingerMode(AudioManager.ADJUST_UNMUTE);
        while(textToSpeech.isSpeaking()){
            audioManager.setRingerMode(AudioManager.ADJUST_UNMUTE);
            ThreadSleep(1);
        }
        textToSpeech.speak("",textToSpeech.QUEUE_FLUSH,null,id);
        //ThreadSleep(7000);

    }
    String Keyword_Found = "" ;

    private boolean Check_Keyword_Lists(String speechResults, String[] keyword_list){
        for(int j=0; j < keyword_list.length; ++j){
            Log.d(getPackageName(),speechResults);
            Log.d(getPackageName(), String.valueOf(keyword_list.length));
            Log.d(getPackageName(), String.valueOf(j));
            Log.d(getPackageName(),keyword_list[j]);
            String tempKeyword = keyword_list[j].replace(" ","");
            String tempSpeech = speechResults.replace(" ","");
            String pattern = "\\b"+tempKeyword+"\\b";
            Pattern p = Pattern.compile(pattern);
            Matcher m1 = p.matcher(tempSpeech);
            Matcher m2 = p.matcher(speechResults);
            if (m1.find() || m2.find()){
                Keyword_Found = keyword_list[j];
                Log.d(getPackageName(),"true");
                return true;
            }
            else{
                Log.d(getPackageName(),"false");
            }
        }
        return false;
    }
    private void KeyWord_loop(List<String> keyword_temp, String id){
        for(int i = 0; i<keyword_temp.size();i++){
            if(i==0){
                textToSpeech.speak(" "+keyword_temp.get(i) ,1,null,id);
            }
            else{
                textToSpeech.speak(" and "+keyword_temp.get(i) ,1,null,id);
            }

        }

    }


    public void postRequest(String postUrl, RequestBody postBody) {

//        String murl = "http://192.168.1.3:5001/";
//        String murl = "http://192.168.1.3:5001/";

        String murl = "http://192.168.1.44:5001/";

        String finalUrl =  murl + postUrl;


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Log.d("FAIL", e.getMessage());
                isNet = false;
                Looper.prepare();
                Toast.makeText(getContext(), "Failed to Connect to Server. Please Try Again", Toast.LENGTH_SHORT).show();
                Looper.loop();//                Toast.makeText(getContext(), "Failed to Connect to Server. Please Try Again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                Log.d("Success","server upload sucessfully");
                isNet = true;
                locRecString = response.body().string();
                objRecString = "test";
                Message message = new Message();
                message.obj=locRecString;
                Looper.prepare();
                Toast.makeText(getContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                Looper.loop();

//                readyForNextImage();
//                runInBackground(
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            runOnUiThread(
//                                    new Runnable() {
//                                        @Override
//                                        public void run() {
////                                            showResultsInBottomSheet(locRecString,objRecString);
//                                        }
//                                    });
//                            readyForNextImage();
//                        }
//                });
            }
        });
    }

    protected void save(byte[] bytes) throws IOException {
        OutputStream outputStream = null;

        File file = new File(Environment.getExternalStorageDirectory() + "/HouseRec/test.jpg" );
        outputStream = new FileOutputStream(file);
        outputStream.write(bytes);

        outputStream.close();
    }

    public byte[] convertYuvToJpeg(byte[] data, Camera camera) {

        YuvImage image = new YuvImage(data, ImageFormat.NV21,
                camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 80; //set quality
        image.compressToJpeg(new Rect(0, 0, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height), quality, baos);//this line decreases the image quality


        return baos.toByteArray();
    }

    public void forwardFragment(final androidx.fragment.app.Fragment fragment) {
        new Handler().postDelayed(() -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.main_fragment, fragment);
            transaction.addToBackStack(fragment.getClass().getName());
            transaction.commitAllowingStateLoss();
        }, 200);
    }

    private void displayHardwareMissingWarning() {
        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setCancelable(false);
        ad.setTitle(getResources().getString(R.string.gyroscope_missing));
        ad.setMessage(getResources().getString(R.string.gyroscope_missing_message));
        ad.setButton(DialogInterface.BUTTON_NEUTRAL, getResources().getString(R.string.OK), (dialog, which) -> dialog.dismiss());
        ad.show();
    }

    public boolean checkSensorAvailability(int sensorType) {
        boolean isSensor = false;
        if (sensorManager.getDefaultSensor(sensorType) != null) {
            isSensor = true;
        }
        return isSensor;
    }

    public boolean isDebug() {
        return debug;
    }


}


