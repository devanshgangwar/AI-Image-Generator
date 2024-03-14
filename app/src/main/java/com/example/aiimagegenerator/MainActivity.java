package com.example.aiimagegenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


// 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
public class MainActivity extends AppCompatActivity {

    ImageView image_view;
    ProgressBar waiting_bar;
    MaterialButton fun_btn;
    EditText inputTxt;
    OkHttpClient client  = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json;charset=utf-8");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputTxt = findViewById(R.id.inputtxt);
        fun_btn = findViewById(R.id.fun_btn);
        waiting_bar = findViewById(R.id.waiting_bar);
        image_view = findViewById(R.id.image_view);

        fun_btn.setOnClickListener((v)->{
            String ipt = inputTxt.getText().toString().trim();
            if(ipt.isEmpty()){
                inputTxt.setError("Please enter text");
                return;
            }
            chatGPTapi(ipt);
        });
    }

    private void chatGPTapi(String ipt) {
        setWaiting(true);
        JSONObject json = new JSONObject();
        try{
            json.put("prompt" , ipt);
            json.put("size" , "256x256");

        }
        catch(Exception e){
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(json.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/image/generations")
                .header("Authorization","Bearer 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")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getApplicationContext(),"Failed tp call Chatgpt" , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
              try{
                  JSONObject jsonObject = new JSONObject(response.body().string());
                  String imgUrl = jsonObject.getJSONArray("data").getJSONObject(0).getString("url");
                  loadImage(imgUrl);
                  setWaiting(false);
              }
              catch (Exception e){
                  e.printStackTrace();
              }
            }
        });
    }

    private void setWaiting(boolean b) {
        runOnUiThread(()->{
            if(b){
                waiting_bar.setVisibility(View.VISIBLE);
                fun_btn.setVisibility(View.GONE);
            }else{

                waiting_bar.setVisibility(View.GONE);
                fun_btn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadImage(String imgUrl) {
        runOnUiThread(()->{
            Picasso.get().load(imgUrl).into(image_view);
        });
    }
}