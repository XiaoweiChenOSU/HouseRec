package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutContract;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.DataBase.LayoutDbHelper;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility.ToDoItem;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.content.AppContent;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.content.HomeContext;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private boolean isNet = false;
    /**
     * The content content this fragment is presenting.
     */
    private HomeContext mItem;

    private ToDoItem mPhotoLayoutItem;

    private LayoutDbHelper HouseRec;

    Spinner spin;
    EditText nameField;
    EditText textField;
    EditText videoURLField;
    EditText audioURLField;
    TextView videoPathField;
    TextView audioPathField;
    EditText itemDetail;
    TextureView preview;
    Button capture;
    Button photos;
    private Button video_select,video_save,audio_select,audio_save;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private String cameraId;
    CameraDevice cameraDevice;
    CameraCaptureSession cameraCaptureSession;
    CaptureRequest captureRequest;
    CaptureRequest.Builder captureRequestBuilder;
    private InputStream inputStream;
    private AssetFileDescriptor afd;
    private Size imageDimensions;
    private ImageReader imageReader;
    private File file;
    Handler mBackgroundHandler;
    HandlerThread mBackgroundThread;


    private String layoutname;


    private File layoutpath;
    private File layoutDataPath;

    private String State;
    private Uri URI_Audio,URI_Audio_2,URI_Video_2;
    private static Uri URI_Video;
    private String path = "";
    private static String filePath = "";
    private static String TempName;

    private EditText video_prompt, audio_prompt;

    private LayoutDbHelper dbHelper;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPhotoLayoutItem =(ToDoItem) getActivity().getIntent().getSerializableExtra(ItemPhotosActivity.LAYOUT_ITEM);


        layoutname = mPhotoLayoutItem.getToDoText().replaceAll(" ","");

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the content content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = AppContent.getInstance().LAYOUT_MAP.get(layoutname).get(Integer.parseInt(getArguments().getString(ARG_ITEM_ID)));
        }

        layoutpath = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/" + mItem.name+"/" );

        if (!layoutpath.exists()){
            layoutpath.mkdirs();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        // Show the content content as text in a TextView.
        if (mItem != null) {

            nameField = rootView.findViewById(R.id.name);
            nameField.setText(mItem.name);
            File list[] = layoutpath.listFiles();
            if(list.length != 0){
                Toast.makeText(getContext(), "This location name cannot be changed!", Toast.LENGTH_LONG).show();
                nameField.setFocusable(false);
                nameField.setFocusableInTouchMode(false);
            }else {
                nameField.setFocusable(true);
                nameField.setFocusableInTouchMode(true);
                nameField.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        mItem.name = s.toString();
                        AppContent.getInstance();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }



            textField = rootView.findViewById(R.id.text_prompt);
            textField.setText(mItem.text);
            textField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mItem.text = s.toString();
                    AppContent.getInstance();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            videoURLField = rootView.findViewById(R.id.video_prompt);
//            videoURLField.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    mItem.videoURL = s.toString();
//                    AppContent.getInstance();
//                }
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });


            preview = rootView.findViewById(R.id.preview);
            preview.setSurfaceTextureListener(textureListener);
            capture = rootView.findViewById(R.id.capture);
            capture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        takePicture();
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            });

            videoPathField = rootView.findViewById(R.id.video_path);
            video_select = rootView.findViewById(R.id.video_select);
            video_select.setOnClickListener(v2 -> {
                selectVideoFromStorage();
            });



            Button saveButton = rootView.findViewById(R.id.saveInfo);
            saveButton.setOnClickListener(v -> {
                String postUrl = "mkclasses/"+ layoutname +"/" + mItem.name;
                new Thread(new Runnable(){
                    @Override
                    public void run(){
                        try{
                            getRequest(postUrl);
                            if(!isNet){
                                Looper.prepare();
                                Toast.makeText(getContext(), "Failed to Connect to Server.", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }else{
                                layoutpath.renameTo(new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/" +mItem.name+"/"));
                                layoutDataPath = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/layout/" );
                                AppContent.findOrSave(layoutDataPath);


                                String temp_text = videoURLField.getText().toString();




                                if(!path.equals("") & !temp_text.equals("")){
                                    HouseRec = new LayoutDbHelper(getContext());
                                    SQLiteDatabase db = HouseRec.getWritableDatabase();
                                    ContentValues values = new ContentValues();
                                    try {
                                        String query = LayoutContract.LayoutTable.COLUMN_NAME + " = ? ";
                                        String[] selArgs = { layoutname };
                                        Cursor cursor = db.query(LayoutContract.LayoutTable.TABLE_NAME, new String[]{"_id"}, query, selArgs,null,null,null,null);
                                        cursor.moveToFirst();
                                        Integer LayoutId = cursor.getInt(0);
                                        String Temp_Value = mItem.name.toLowerCase();
                                        Temp_Value = Temp_Value.replaceAll("\\s","");
                                        temp_text = temp_text.trim();
                                        values.put(LayoutContract.LayoutMaterial.COLUMN_LAYOUTID, LayoutId.toString());
                                        values.put(LayoutContract.LayoutMaterial.COLUMN_LOCATION, Temp_Value);
                                        values.put(LayoutContract.LayoutMaterial.COLUMN_DIRECTORY, path);
                                        values.put(LayoutContract.LayoutMaterial.COLUMN_KEYWORDS, temp_text.toLowerCase());
                                        values.put(LayoutContract.LayoutMaterial.COLUMN_CREATETIME, String.valueOf(Calendar.getInstance().getTime()));
                                        long newRowId = db.insert(LayoutContract.LayoutMaterial.TABLE_NAME, null, values);
                                    }finally {
                                        db.close();
                                        Looper.prepare();
                                        Toast.makeText((getContext()), (CharSequence)"Save Video Info Successfully", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }

                                    video_prompt.getText().clear();
                                    path = "";

                                }
                                else if(temp_text.equals("")){
                                    Looper.prepare();
                                    Toast.makeText((getContext()), (CharSequence)"Missing Video Keyword", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                else if(path.equals("")){
                                    Looper.prepare();
                                    Toast.makeText((getContext()), (CharSequence)"Missing Video Directory Path", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            });
        }
        return rootView;
    }
    private final void selectVideoFromStorage() {
//        Toast.makeText((getContext()), (CharSequence)"Select Video", Toast.LENGTH_LONG).show();
        Intent browseStorage = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        browseStorage.setType("video/*");
        browseStorage.addCategory("android.intent.category.OPENABLE");
        this.startActivityForResult(Intent.createChooser(browseStorage, (CharSequence)"PICKFILE_RESULT_CODE"), 99);
    }

    private final void selectAudioFromStorage() {
        Toast.makeText((getContext()), (CharSequence)"Select Audio", Toast.LENGTH_LONG).show();
        Intent browseStorage = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        browseStorage.setType("video/*");
        browseStorage.addCategory("android.intent.category.OPENABLE");
        this.startActivityForResult(Intent.createChooser(browseStorage, (CharSequence)"PICKFILE_RESULT_CODE"), 99);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && resultCode == -1 && data != null) {
            String Uri_String;
            String Message;
            URI_Video = data.getData();

            //String FilePath = data.getData().getPath();
          // String FileName = data.getData().getLastPathSegment();
            //getContentResolver().openInputStream(URI_Video)

           // path = new File(URI_Video.getPath());
            Uri_String = URI_Video.toString();
            URI_Video.getScheme();
            DocumentFile test = DocumentFile.fromSingleUri(getContext(),URI_Video);

            TempName = test.getName(); //Gives file name now just need the path.

            Uri tempURi = test.getUri();
            try {
                inputStream = getActivity().getContentResolver().openInputStream(URI_Video);
                pass = 1;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                afd = getActivity().getContentResolver().openAssetFileDescriptor(URI_Video, "r");
                pass = 1;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


           // Uri docUri = DocumentsContract.buildDocumentUriUsingTree(URI_Video,
        //            DocumentsContract.getTreeDocumentId(URI_Video));
         //  String path_2 = Utils.getPath(String.valueOf(docUri), test.getName());

            path = getPath(getContext(), URI_Video);
            filePath= path.toString();
            String[] filename = filePath.split("/");
//            Toast.makeText((getContext()), (CharSequence) TempName, Toast.LENGTH_LONG).show();
            videoPathField.setText(filename[filename.length-1]);

        }

    }




int pass;


    public AssetFileDescriptor Get_AFD(){

        return afd;
    }
    public InputStream Get_Input_Stream(){

        return inputStream;
    }

    public static Uri Get_Uri_Video(){

        return URI_Video;

    }
    public static String Get_Path(){

        return filePath;
    }

    public static String Get_File_Name(){

        return TempName;
    }





    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && ((Cursor) cursor).moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }







    private void takePicture() throws CameraAccessException {

        if (cameraDevice == null) return;

        CameraManager manager = (CameraManager)getActivity().getSystemService(Context.CAMERA_SERVICE);

        CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());

        Size[] jpegSizes = null;

        jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);

        int width = 640;
        int height = 480;

        if (jpegSizes != null && jpegSizes.length > 0) {
            width = jpegSizes[0].getWidth();
            height = jpegSizes[0].getHeight();
        }

        ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
        List<Surface> outputSurfaces = new ArrayList<>(2);
        outputSurfaces.add(reader.getSurface());

        outputSurfaces.add(new Surface(preview.getSurfaceTexture()));

        final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

        captureBuilder.addTarget(reader.getSurface());

        captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

//        File dir = new File(Environment.getExternalStorageDirectory() + "/classes/" + mItem.name + "/");
//        if (!dir.exists()) dir.mkdirs();

        file = new File(layoutpath + "/" + ts + ".jpg");

        ImageReader.OnImageAvailableListener readerListener = reader1 -> {
            Image image = null;

            image = reader1.acquireLatestImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();

            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            try {
                save(bytes);
                String postUrl = "up_trainphoto/"+ layoutname +"/" + mItem.name;
                MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

                long time= System.currentTimeMillis();

                multipartBodyBuilder.addFormDataPart("image" + time, "_train.jpg", RequestBody.create(MediaType.parse("image/*jpg"), bytes));

                RequestBody postBodyImage = multipartBodyBuilder.build();
                postRequest(postUrl, postBodyImage);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (image != null) {
                    image.close();
                }
            }

        };

        reader.setOnImageAvailableListener(readerListener,mBackgroundHandler);





        final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
                getActivity().runOnUiThread((Runnable) () -> {
                    try {
                        createCameraPreview();
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                });
            }
        };

        cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(CameraCaptureSession session) {
                try {
                    session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(CameraCaptureSession session) {

            }
        }, mBackgroundHandler);

    }

    private void save(byte[] bytes) throws IOException {
        OutputStream outputStream = null;

        outputStream = new FileOutputStream(file);
        outputStream.write(bytes);

        outputStream.close();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getContext(), "Sorry, camera permission is necessary.", Toast.LENGTH_LONG).show();
            }
        }

    }

    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            try {
                openCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            try {
                createCameraPreview();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;

        }
    };

    private void createCameraPreview() throws CameraAccessException {
        SurfaceTexture texture = preview.getSurfaceTexture();
        texture.setDefaultBufferSize(imageDimensions.getWidth(), imageDimensions.getHeight());
        preview.setLayoutParams(new LinearLayout.LayoutParams(
                imageDimensions.getWidth(), imageDimensions.getHeight(), Gravity.CENTER));

        Surface surface = new Surface(texture);
        captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        captureRequestBuilder.addTarget(surface);

        cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                if (cameraDevice == null) {
                    return;
                }

                cameraCaptureSession = session;
                try {
                    updatePreview();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                Toast.makeText(getContext(), "Configuration Changed", Toast.LENGTH_LONG).show();
            }
        }, null);
    }

    private void updatePreview() throws CameraAccessException {
        if (cameraDevice == null) return;

        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);

    }

    private void openCamera() throws CameraAccessException {
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);

        cameraId = manager.getCameraIdList()[0];

        CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        imageDimensions = map.getOutputSizes(SurfaceTexture.class)[0];

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            return;
        }

        manager.openCamera(cameraId, stateCallback, null);
    }

    @Override
    public void onResume() {
        super.onResume();

        startBackgroundThread();

        if (preview.isAvailable()) {
            try {
                openCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            preview.setSurfaceTextureListener(textureListener);
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    @Override
    public void onPause() {
        try {
            stopBackgroundThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onPause();


    }

    protected void stopBackgroundThread() throws InterruptedException {
        mBackgroundThread.quitSafely();

        mBackgroundThread.join();
        mBackgroundThread = null;
        mBackgroundHandler = null;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        switch (pos) {
            case 0:
                mItem.type = HomeContext.PromptType.TEXT;
                break;
            case 1:
                mItem.type = HomeContext.PromptType.VIDEO;
                break;
            case 2:
                mItem.type = HomeContext.PromptType.AUDIO;
                break;
        }

        AppContent.getInstance();

    }

    public void getRequest(String postUrl) {

        String murl = "http://10.203.8.185:5001/";

        String finalUrl =  murl + postUrl;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Log.d("FAIL", e.getMessage());
                isNet = false;
                Looper.prepare();
                Toast toast =Toast.makeText(getContext(), "Failed to Connect to Server. Please Try Again", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                try{
                    Log.d("Success","Server Operate Successfully");
                    isNet = true;
                    Looper.prepare();
//                    Toast toast = Toast.makeText(getContext(), response.body().string(), Toast.LENGTH_SHORT);
//                    toast.setGravity(Gravity.CENTER, 0, 0);
//                    toast.show();
                    Looper.loop();
                } finally {
                    if (response != null){
                        response.close();
                    }
                }
            }
        });
    }

    public void postRequest(String postUrl, RequestBody postBody) {

        String murl = "http://10.203.8.185:5001/";

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
                Looper.prepare();
                Toast.makeText(getContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        });
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
