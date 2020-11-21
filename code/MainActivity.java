package com.example.iot_app;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    public Button button_info, button_tooth;
    public String Result;

    private URL url;
    private HttpURLConnection con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_info = (Button) findViewById(R.id.button_info);
        button_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_info = new Intent(MainActivity.this, get_info.class);
                intent_info.putExtra("text", String.valueOf(button_info.getText()));
                startActivity(intent_info);
            }
        });

        button_tooth = (Button)findViewById(R.id.button_bluetooth);
        button_tooth.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent_tooth = new Intent(MainActivity.this, bluetooth.class);
                intent_tooth.putExtra("text", String.valueOf(button_tooth.getText()));
                startActivity(intent_tooth);
            }
        });
        Log.d("End Setup", "end setup");
    }


}

