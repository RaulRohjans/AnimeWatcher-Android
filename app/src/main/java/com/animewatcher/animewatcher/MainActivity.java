package com.animewatcher.animewatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "PreName";
    int PRIVATE_MODE = 0;

    SharedPreferences pref;
    Context context;

    RequestQueue requestQueue;
    String URL;

    BottomNavigationView bottom_nav;
    ImageView topnav_user, topnav_search;

    int bottomNavigationPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom_nav = findViewById(R.id.bottom_nav);
        topnav_search = findViewById(R.id.topnav_search);
        topnav_user = findViewById(R.id.topnav_account);
        context = this;

        //Bottom Nav Events
        bottomNavigationPosition = savedInstanceState == null ? R.id.nav_home :
                savedInstanceState.getInt("opened_fragment", R.id.nav_home);

        URL = getText(R.string.website_link) + "api/ping";
        PingServer();

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle(getText(R.string.LeaveTitle)).setMessage(getText(R.string.LeaveMessage)).setPositiveButton(getText(R.string.LeaveYes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
                System.exit(0);
            }
        }).setNegativeButton(getText(R.string.LeaveNo), null).create().show();
    }

    private void Submit(String data)
    {
        final String savedata= data;

        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres = new JSONObject(response);
                    if(!objres.getString("Status").equals("success"))
                    {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.remove("Token");
                        editor.remove("id");
                        editor.remove("username");
                        editor.remove("password");
                        editor.remove("first_name");
                        editor.remove("email");
                        editor.remove("last_name");
                        editor.remove("is_staff");
                        editor.remove("is_superuser");
                        editor.remove("is_active");
                        editor.remove("last_login");
                        editor.remove("date_joined");
                        editor.apply();
                        Toast.makeText(getApplicationContext(), R.string.session_expired_error, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), R.string.session_expired_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove("Token");
                    editor.remove("id");
                    editor.remove("username");
                    editor.remove("password");
                    editor.remove("first_name");
                    editor.remove("email");
                    editor.remove("last_name");
                    editor.remove("is_staff");
                    editor.remove("is_superuser");
                    editor.remove("is_active");
                    editor.remove("last_login");
                    editor.remove("date_joined");
                    editor.apply();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("Token");
                editor.remove("id");
                editor.remove("username");
                editor.remove("password");
                editor.remove("first_name");
                editor.remove("email");
                editor.remove("last_name");
                editor.remove("is_staff");
                editor.remove("is_superuser");
                editor.remove("is_active");
                editor.remove("last_login");
                editor.remove("date_joined");
                editor.apply();
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.session_expired_error, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return savedata == null ? null : savedata.getBytes(StandardCharsets.UTF_8);
            }

        };
        requestQueue.add(stringRequest);
    }

    private void StartStuff(){
        //If user was logged in, check if session is still valid
        pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        if(pref.getString("Token", null) != null)
        {
            URL = getText(R.string.website_link) + "api/check-token";

            String data = "{"+
                    "\"Token\":" + "\"" + pref.getString("Token", "") + "\""+
                    "}";
            Submit(data);
        }


        bottom_nav.setSelectedItemId(bottomNavigationPosition);
        if(bottomNavigationPosition == R.id.nav_home)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new home()).commit();

        bottom_nav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        URL = getText(R.string.website_link) + "api/ping";
                        SimpleServerPing();
                        Fragment selectFrag = null;

                        switch (item.getItemId()) {
                            case R.id.nav_anime_list:
                                selectFrag = new anime_list();
                                break;

                            case R.id.nav_categories:
                                selectFrag = new categories();
                                break;

                            case R.id.nav_home:
                                selectFrag = new home();
                                break;

                            case R.id.nav_my_list:
                                selectFrag = new my_list();
                                break;

                            case R.id.nav_download_anime:
                                selectFrag = new download_anime();
                                break;
                        }
                        assert selectFrag != null;
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectFrag).commit();

                        return true;
                    }
                }
        );

        //Top Nav Events
        topnav_user.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));

                        //Check if user is already logged in
                        pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
                        if(pref.getString("Token", null) == null)
                        {
                            Intent i = new Intent(getApplicationContext(), login.class);
                            startActivity(i);
                        }
                        else
                        {
                            Intent i = new Intent(getApplicationContext(), account_settings.class);
                            startActivity(i);
                        }
                    }
                }
        );

        topnav_search.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));

                        Intent i = new Intent(getApplicationContext(), anime_search.class);
                        startActivity(i);
                    }
                }
        );
    }

    private void CheckForUpdates(String data)
    {
        final String savedata= data;

        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres = new JSONObject(response);
                    if(objres.getString("Status").equals("success"))
                    {
                        StartStuff();
                    }
                    else{
                        if(objres.getString("Status").equals("update")){
                            new AlertDialog.Builder(context)
                                    .setTitle(getText(R.string.updates_alert_title))
                                    .setMessage(getText(R.string.updates_alert_message_part1) + " " + objres.getString("Version") + ". " + getText(R.string.updates_alert_message_part2))
                                    .setPositiveButton(getText(R.string.LeaveYes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(objres.getString("Download")));
                                        startActivity(i);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        StartStuff();
                                    }
                                }
                            }).setNegativeButton(getText(R.string.updates_alert_later), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
                                    SharedPreferences.Editor editor = pref.edit();

                                    Calendar calendar = Calendar.getInstance();
                                    calendar.add(Calendar.WEEK_OF_YEAR, 1);
                                    editor.putString("update_reminder_date", String.valueOf(calendar.getTime()));
                                    editor.apply();
                                }
                            }).create().show();

                            // Continue starting the app
                            StartStuff();
                        }
                        else{
                            StartStuff();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    StartStuff();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse.statusCode == 404){
                    String response = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    try {
                        JSONObject objres = new JSONObject(response);
                        if(objres.getString("Status").equals("error")){
                            if(objres.getString("Error").equals("app version not found")){
                                pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
                                if(!pref.getBoolean("dev_message_shown", false)){
                                    new AlertDialog.Builder(context)
                                            .setTitle(getText(R.string.dev_app_alert_title))
                                            .setMessage(getText(R.string.dev_app_alert_message))
                                            .setPositiveButton(getText(R.string.lbl_register_success_okay_option), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    SharedPreferences.Editor editor = pref.edit();
                                                    editor.putBoolean("dev_message_shown", true);
                                                    editor.apply();
                                                }
                                            })
                                            .create().show();
                                }
                                StartStuff();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
                                StartStuff();
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
                            StartStuff();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
                        StartStuff();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
                    StartStuff();
                }
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return savedata == null ? null : savedata.getBytes(StandardCharsets.UTF_8);
            }

        };
        requestQueue.add(stringRequest);
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
                        boolean need = false;
                        pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
                        if(pref.getString("update_reminder_date", null) != null){
                            Calendar calendarNow = Calendar.getInstance();
                            Calendar calendarThen = Calendar.getInstance();

                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                            calendarThen.setTime(Objects.requireNonNull(sdf.parse(pref.getString("update_reminder_date", null))));

                            if(calendarNow.getTimeInMillis() >= calendarThen.getTimeInMillis()){
                                need = true;
                            }
                        }
                        else{
                            need = true;
                        }

                        if(need){
                            // Check for app updates
                            URL = getText(R.string.website_link) + "api/check-app-updates";

                            try {
                                // Get app version
                                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                                String version = pInfo.versionName;
                                String data = "{"+
                                        "\"mAppVersion\":" + "\"" + version + "\","+
                                        "\"mPlatform\":" + "\"" + "android" + "\""+
                                        "}";
                                CheckForUpdates(data);

                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                                StartStuff();
                            }
                        }
                        else{
                            StartStuff();
                        }
                    }
                    else
                    {
                        Intent i = new Intent(getApplicationContext(), server_status_offline.class);
                        finish();
                        startActivity(i);
                    }

                } catch (JSONException | ParseException e) {
                    Intent i = new Intent(getApplicationContext(), server_status_offline.class);
                    finish();
                    startActivity(i);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Intent i = new Intent(getApplicationContext(), server_status_offline.class);
                finish();
                startActivity(i);
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void SimpleServerPing()
    {
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres = new JSONObject(response);
                    if (!objres.getString("Status").equals("success")) {
                        Intent i = new Intent(getApplicationContext(), server_status_offline.class);
                        finish();
                        startActivity(i);
                    }

                } catch (JSONException e) {
                    Intent i = new Intent(getApplicationContext(), server_status_offline.class);
                    finish();
                    startActivity(i);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Intent i = new Intent(getApplicationContext(), server_status_offline.class);
                finish();
                startActivity(i);
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("opened_fragment", bottom_nav.getSelectedItemId());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        URL = getText(R.string.website_link) + "api/ping";
        SimpleServerPing();

        //If user was logged in, check if session is still valid
        pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        if(pref.getString("Token", null) != null)
        {
            URL = getText(R.string.website_link) + "api/check-token";

            String data = "{"+
                    "\"Token\":" + "\"" + pref.getString("Token", "") + "\""+
                    "}";
            Submit(data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        URL = getText(R.string.website_link) + "api/ping";
        SimpleServerPing();

        //If user was logged in, check if session is still valid
        pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        if(pref.getString("Token", null) != null)
        {
            URL = getText(R.string.website_link) + "api/check-token";

            String data = "{"+
                    "\"Token\":" + "\"" + pref.getString("Token", "") + "\""+
                    "}";
            Submit(data);
        }
    }
}