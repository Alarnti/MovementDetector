package com.atlasov.albert.movementdetection;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.opencv.core.Mat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by albert on 21.6.17.
 */

public class Utils {

    public static OkHttpClient client = new OkHttpClient();

    public static void sendRequest() {
        Request request = new Request.Builder()
                .url("https://api.telegram.org/bot<your_bot_code>/sendMessage?chat_id=168882991&text=Somewhere_Here")
                .build();
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
//                        Toast.makeText(applicationContext,"Not sended", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
//                        Toast.makeText(applicationContext,"Signal sended", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
