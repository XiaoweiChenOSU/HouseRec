package com.okstate.VisualComputingandImageProcessingLab.HouseRec;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout.ItemPhotosActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ModelActivity extends AppCompatActivity {
    /** Get model from server */
    private Button get_model;
    public Handler handler = new Handler();
    public boolean isFinish = false;
    public String layoutName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if ( (String) b.get(ItemPhotosActivity.LAYOUT_NAME) != null){
            layoutName = (String) b.get(ItemPhotosActivity.LAYOUT_NAME);
        }

//        get_model = (Button)findViewById(R.id.get_model);
//        get_model.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                updateModel(layoutName);
//            }
//        });

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                try {
                    readModel(layoutName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
    public void updateModel(String layoutname){
        getNewModel(layoutname);
        getNewLabel(layoutname);
//        getLayoutImage(layoutname);
    }

    public void getNewModel(String layoutname){
        //10.203.8.185:5001 for outside of okstate domain
        String requestUrl = "http://10.203.8.185:5001/downloadModel/"+layoutname;
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder()
                .get()
                .url(requestUrl)
                .build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.d("FAIL Model download", e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                File layoutdir = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/layout/" );
                if (!layoutdir.exists()) {
                    layoutdir.mkdirs();
                }
                File file = new File(layoutdir + "/" +"model" + ".tflite");
                file.createNewFile();
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    Log.e("downloadModelFile", "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
//                        Log.e("downloadModelFile", "current------>" + current);
                    }
                    fos.flush();
                } catch (IOException e) {
                    Log.e("downloadModelFile", e.toString());
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e("downloadModelFile", e.toString());
                    }
                }
            }
        });
    }

    public void getNewLabel(String layoutname){
        // Need to modify depend on our server
        String requestUrl = "http://10.203.8.185:5001/downloadCatagories/"+layoutname;
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder()
                .get()
                .url(requestUrl)
                .build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("FAIL Label download", e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                File layoutdir = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/layout/" );
                if (!layoutdir.exists()) {
                    layoutdir.mkdirs();
                }
                File file = new File(layoutdir + "/" +"Catagories" + ".txt");
                file.createNewFile();
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    Log.e("downloadLabelFile", "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
//                        Log.e("downloadLabelFile", "current------>" + current);
                    }
                    fos.flush();
                } catch (IOException e) {
                    Log.e("downloadLabelFile", e.toString());
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e("downloadLabelFile", e.toString());
                    }
                }
            }
        });
    }
    public void getLayoutImage(String layoutname){
        //10.203.8.185:5001 for outside of okstate domain
        String requestUrl = "http://10.203.8.185:5001/downloadLayoutImg/"+layoutname;
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder()
                .get()
                .url(requestUrl)
                .build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.d("FAIL Model download", e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                File layoutdir = new File(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutname + "/layout/" );
                if (!layoutdir.exists()) {
                    layoutdir.mkdirs();
                }
                File file = new File(layoutdir + "/" +layoutname + ".jpg");
//                File file = new File(layoutdir + "/coffeemaker.MP4");
                file.createNewFile();
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    Log.e("downloadModelFile", "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
//                        Log.e("downloadModelFile", "current------>" + current);
                    }
                    fos.flush();
                } catch (IOException e) {
                    Log.e("downloadModelFile", e.toString());
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e("downloadModelFile", e.toString());
                    }
                }
            }
        });
    }
    public void trainModel(String layoutname){
        isFinish = false;
        String requestUrl = "http://10.203.8.185:5001/trainModel/" + layoutname;
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder()
                .get()
                .url(requestUrl)
                .build();
        OkHttpClient copy = okHttpClient.newBuilder()
                .readTimeout(500, TimeUnit.SECONDS)
                .build();
        Call call=copy.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("FAIL Train download", e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //将响应数据转化为输入流数据
                Log.d("Success","server download model sucessfully");
                Message msg=Message.obtain();
                isFinish = true;
//                msg.obj=bitmap;
//                handler.sendMessage(msg);
            }
        });
    }

    public MappedByteBuffer readModel(String layoutName) throws IOException{
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
//                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
//        }
        FileInputStream f_input_stream = new FileInputStream(new File (Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutName + "/layout/" + "model" + ".tflite"));
//        FileInputStream f_input_stream = new FileInputStream(new File (Environment.getExternalStorageDirectory() + "/HouseRec/model/" + "model" + ".tflite"));
        FileChannel f_channel = f_input_stream.getChannel();
        MappedByteBuffer tfliteModel = f_channel.map(FileChannel.MapMode.READ_ONLY, 0, f_channel .size());
        return  tfliteModel;
    }

    public List<String> readLabels(String layoutName) throws IOException{
        List<String> result = new ArrayList();
        try(FileReader f = new FileReader(Environment.getExternalStorageDirectory() + "/HouseRec/" + layoutName + "/layout/" + "Catagories" + ".txt")){
            StringBuffer sb = new StringBuffer();
            while(f.ready()){
                char c = (char)f.read();
                if(c == '\n'){
                    result.add(sb.toString());
                    sb = new StringBuffer();
                }else{
                    sb.append(c);
                }
            }
            if(sb.length() > 0){
                result.add(sb.toString());
            }
        }
        return result;

    }

}
