package com.animewatcher.animewatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class server_status_offline extends AppCompatActivity {

    RequestQueue requestQueue;
    String URL;

    Button btn_retry, btn_watch_offline;
    ProgressBar pgb_loading;

    Boolean isLoading = false;
    Thread loading_thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_status_offline);

        btn_retry = findViewById(R.id.btn_retry_connection);
        btn_watch_offline = findViewById(R.id.btn_watch_offline);
        pgb_loading = findViewById(R.id.pgb_connection_loading);

        loading_thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pgb_loading.setVisibility(View.GONE);
                            isLoading = false;
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        //Listeners
        btn_retry.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));

                        if(!isLoading && !loading_thread.isAlive()){
                            pgb_loading.setVisibility(View.VISIBLE);
                            isLoading = true;
                            URL = getText(R.string.website_link) + "api/ping";
                            PingServer();
                        }
                    }
                }
        );

        btn_watch_offline.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                        Toast.makeText(getApplicationContext(), "This feature is not available yet.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void PingServer()
    {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres = new JSONObject(response);
                    if(objres.getString("Status").equals("success"))
                    {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        finish();
                        startActivity(i);
                    }
                    else
                    {
                        try {
                            if(!loading_thread.isAlive()){
                                loading_thread.start();
                            }
                        }catch (IllegalThreadStateException ignored){

                        }
                    }

                } catch (JSONException e) {
                    try {
                        if(!loading_thread.isAlive()){
                            loading_thread.start();
                        }
                    }catch (IllegalThreadStateException ignored){

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    if(!loading_thread.isAlive()){
                        loading_thread.start();
                    }
                }catch (IllegalThreadStateException ignored){

                }
            }
        });
        requestQueue.add(stringRequest);
    }
}