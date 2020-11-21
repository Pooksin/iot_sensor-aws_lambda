package com.example.iot_app;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.jar.JarException;

public class get_info  extends AppCompatActivity {

    public Button button;
    public EditText livingroom_1, livingroom_2, livingroom_3, livingroom_4;
    public EditText kitchen_1, kitchen_2, kitchen_3;
    public EditText toilet_1, toilet_2, toilet_3, toilet_4;
    public EditText body;
    public ImageView siren_1, siren_2;

    public Livingroom liv;
    public Kitchen kit;
    public Toilet to;
    public Body bd;

    private URL url;
    private HttpURLConnection con;

    public Activity act = this;

    String temp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_info);

        Intent intent = new Intent(this.getIntent());
        String s = intent.getStringExtra("text");
        Log.d("text", s);

        livingroom_1 = (EditText) findViewById(R.id.living_room_humid_info); livingroom_2 = (EditText) findViewById(R.id.living_room_temp_info);
        livingroom_3 = (EditText) findViewById(R.id.living_room_motion_info); //livingroom_4 = (EditText) findViewById(R.id.living_room_emergency_info);

        kitchen_1 = (EditText) findViewById(R.id.kitchen_humid_info); kitchen_2 = (EditText) findViewById(R.id.kitchen_temp_info);
        kitchen_3 = (EditText) findViewById(R.id.kitchen_gas_info);

        toilet_1 = (EditText) findViewById(R.id.toilet_humid_info); toilet_2 = (EditText) findViewById(R.id.toilet_temp_info);
        //toilet_4 = (EditText) findViewById(R.id.toilet_emergency_info);

        body = (EditText) findViewById(R.id.heart_info);

        siren_1 = (ImageView) findViewById(R.id.living_room_siren);
        siren_2 = (ImageView) findViewById(R.id.toilet_siren);


        button = (Button) findViewById(R.id.get_info);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_info.HttpThread thread = new get_info.HttpThread();
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                image_change();
            }

            public void image_change(){

                livingroom_1.setText(liv.humid); livingroom_2.setText(liv.temperature);
                if(liv.motion.equals("1")){
                    livingroom_3.setText("열림"); //livingroom_4.setText(liv.emergency);
                }
                else{
                    livingroom_3.setText("닫힘");
                }
                kitchen_1.setText(kit.humid); kitchen_2.setText(kit.temp); kitchen_3.setText(kit.gas);

                toilet_1.setText(to.humid); toilet_2.setText(to.temperature); //toilet_4.setText(to.emergency);

                body.setText(bd.heart);

                if(liv.emergency.equals("1"))  {
                    Glide.with(get_info.this).load(R.drawable.siren_gif).into(siren_1);
                }
                else {
                    siren_1.setImageResource(R.drawable.siren);
                }

                if(to.emergency.equals("1"))  {
                    Glide.with(get_info.this).load(R.drawable.siren_gif).into(siren_2);
                }
                else {
                    siren_2.setImageResource(R.drawable.siren);
                }
            }
        });
        Log.d("End Setup", "end setup");
    }

    private class HttpThread extends Thread {
        private static final String TAG = "HttpThread";
        String api_url = "https://vq42ehvnpl.execute-api.ap-northeast-2.amazonaws.com/info/get-info";

        public void run() {
            //TEXT 전송을 하는 쓰레드 POST 방식 JSON -> BODY 에 받아 옴.
            {
                try {
                    URL url = new URL(api_url);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setUseCaches(false); // 캐시 사용 안 함
                    connection.setRequestMethod("POST"); //전송방식
                    connection.setDoOutput(true);       //데이터를 쓸 지 설정
                    connection.setDoInput(true);        //데이터를 읽어올지 설정

                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    //connection.setRequestProperty("User-Agent", api_url);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("db1","iot_project_livingroom");
                    jsonParam.put("db2","iot_project_kitchen");
                    jsonParam.put("db3","iot_project_toilet");
                    jsonParam.put("db4","iot_project_body");

                    Log.i("json", String.valueOf(jsonParam));

                    OutputStreamWriter os = new OutputStreamWriter(connection.getOutputStream());
                    Log.d("test", "test");
                    os.write(jsonParam.toString()); //전송
                    os.close();

                    InputStreamReader in = new InputStreamReader(connection.getInputStream(), "utf-8");
                    BufferedReader bf = new BufferedReader(in);

                    String line;
                    while ((line = bf.readLine()) != null) {
                        temp += line;
                    }

                    JSONObject json = new JSONObject(temp);
                    Gson gson = new Gson();
                    Log.d("test", temp);
                    liv = gson.fromJson(json.get("result_1").toString(), Livingroom.class);
                    kit = gson.fromJson(json.get("result_2").toString(), Kitchen.class);
                    to = gson.fromJson(json.get("result_3").toString(), Toilet.class);
                    bd = gson.fromJson(json.get("result_4").toString(), Body.class);

                    Log.i("RsponseCode", String.valueOf(connection.getResponseCode()));
                    Log.i("MSG", connection.getResponseMessage());

                    connection.disconnect();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
