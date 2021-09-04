package com.animewatcher.animewatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "PreName";
    int PRIVATE_MODE = 0;

    SharedPreferences pref;

    RequestQueue requestQueue;
    String URL;

    BottomNavigationView bottom_nav;
    ImageView topnav_user, topnav_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom_nav = findViewById(R.id.bottom_nav);
        topnav_search = findViewById(R.id.topnav_search);
        topnav_user = findViewById(R.id.topnav_account);

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

        //Bottom Nav Events
        final int bottomNavigationPosition = savedInstanceState == null ? R.id.nav_home :
                savedInstanceState.getInt("opened_fragment", R.id.nav_home);

        bottom_nav.setSelectedItemId(bottomNavigationPosition);
        if(bottomNavigationPosition == R.id.nav_home)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new home()).commit();

        bottom_nav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

                            case R.id.nav_sub_anime:
                                selectFrag = new submit_anime();
                                break;
                        }
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
                    if(!objres.getString("Status").equals("Success"))
                    {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear();
                        editor.apply();
                        Toast.makeText(getApplicationContext(), R.string.session_expired_error, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), R.string.session_expired_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.apply();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("opened_fragment", bottom_nav.getSelectedItemId());
        super.onSaveInstanceState(outState);
    }
}