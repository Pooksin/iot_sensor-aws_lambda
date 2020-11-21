package com.example.iot_app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.*;

public class bluetooth extends AppCompatActivity {
    public Button send_button, open_button, time_button;
    public TextView pwd;
    public EditText count;
    static public String api_url;

    public String text;

    private URL pwd_url, open_url;
    private HttpURLConnection pwd_con, open_con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tooth);

        Intent intent = new Intent(this.getIntent());
        String s = intent.getStringExtra("text");
        Log.d("text", s);

        pwd = (TextView)findViewById(R.id.pwd);
        count = (EditText) findViewById(R.id.count);

        send_button = (Button) findViewById(R.id.btnSendData);
        open_button = (Button) findViewById(R.id.btnOpen);
        time_button = (Button) findViewById(R.id.btnTime);

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth.HttpThread pwd_thread = new bluetooth.HttpThread("send");
                pwd_thread.start();
            }
        });
        open_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth.HttpThread open_thread = new bluetooth.HttpThread("open");
                open_thread.start();
            }
        });
        time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetooth.HttpThread open_thread = new bluetooth.HttpThread("time");
                open_thread.start();
                try {
                    open_thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count.setText(text);
            }
        });
        Log.d("End Setup", "end setup");
    }

    private class HttpThread extends Thread {
        private Calendar c = Calendar.getInstance();
        private String subject;

        HttpThread(String subject){

            this.subject = subject;

            if(subject == "open")
                api_url = "https://m7s08vfrq4.execute-api.ap-northeast-2.amazonaws.com/iot_project_AndroidPW";
            else if(subject == "send")
                api_url = "https://ut7on9k64a.execute-api.ap-northeast-2.amazonaws.com/iot_project_newPW";
            else if(subject == "time")
                api_url = "https://c2dv4leymb.execute-api.ap-northeast-2.amazonaws.com/iot_project_EnterCount";
        }

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

                    JSONObject jsonParam = new JSONObject();

                    if(subject == "time")
                        jsonParam.put("timeStamp", c.getTimeInMillis() / 1000);
                    else
                        jsonParam.put("PW", String.valueOf(pwd.getText()));

                    Log.i("json", String.valueOf(jsonParam));

                    OutputStreamWriter os = new OutputStreamWriter(connection.getOutputStream());
                    os.write(jsonParam.toString()); //전송
                    os.close();

                    if(subject == "time"){
                        InputStreamReader in = new InputStreamReader(connection.getInputStream(), "utf-8");
                        BufferedReader bf = new BufferedReader(in);

                        String line, temp = "";
                        while ((line = bf.readLine()) != null) {
                            temp += line;
                        }

                        JSONObject json = new JSONObject(temp);
                        text = "  " + json.get("enterCount").toString() + " 회";
                    }

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