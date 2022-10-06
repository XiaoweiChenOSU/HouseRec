package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Utility;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetServerData {
    private Context mContext;
    private static String murl = "http://10.203.8.185:5001/";


    public static void getRequest(String postUrl) {

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
//                Toast.makeText(getContext(), "Failed to Connect to Server. Please Try Again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                try{
                    Log.d("Success","Server Operate Successfully");
                } finally {
                    if (response != null){
                        response.close();
                    }
                }
            }
        });
    }

    public static void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Log.d("FAIL", e.getMessage());
//                Toast.makeText(getContext(), "Failed to Connect to Server. Please Try Again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                try {
                    // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                    Log.d("Success", "server upload sucessfully");
                    //                Toast.makeText(getContext(), "Server's Response\n" + response.body().string(), Toast.LENGTH_SHORT).show();
                }finally{
                    if (response != null){
                        response.close();
                    }
                }
            }
        });
    }

}
