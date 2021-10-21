package com.animewatcher.animewatcher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class account_settings extends AppCompatActivity {

    private static final String PREF_NAME = "PreName";
    int PRIVATE_MODE = 0;

    RequestQueue requestQueue;
    String URL;

    SharedPreferences pref;

    RelativeLayout change_user, change_email, change_password, donations, submit_anime, disclaimer_privacy_policy, logout, full_logout, about_us;
    ImageView img_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        change_user = findViewById(R.id.option_change_user);
        change_email = findViewById(R.id.option_change_email);
        change_password = findViewById(R.id.option_change_password);
        donations = findViewById(R.id.option_donate);
        disclaimer_privacy_policy = findViewById(R.id.option_disclaimer_privacy_policy);
        about_us = findViewById(R.id.option_about_us);
        logout = findViewById(R.id.option_logout);
        img_search = findViewById(R.id.topnav_search);
        submit_anime = findViewById(R.id.option_submit_anime);
        full_logout = findViewById(R.id.option_full_logout);

        //Listeners
        change_user.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), change_user.class);
                        startActivity(i);
                    }
                }
        );

        change_email.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), change_email.class);
                        startActivity(i);
                    }
                }
        );

        change_password.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(), change_password.class);
                        startActivityForResult(i, 5831);
                    }
                }
        );

        submit_anime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), submit_new_anime.class);
                        startActivity(i);
                    }
                }
        );

        donations.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), donate.class);
                        startActivity(i);
                    }
                }
        );

        disclaimer_privacy_policy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(), disclaimer_privacy_policy.class);
                        startActivity(i);
                    }
                }
        );

        about_us.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), about_us.class);
                        startActivity(i);
                    }
                }
        );

        logout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
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
                        finish();
                    }
                }
        );

        Context c = this;
        full_logout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(c)
                                .setTitle(getText(R.string.lbl_full_logout_warning_title))
                                .setMessage(getText(R.string.lbl_full_logout_warning_message))
                                .setPositiveButton(getText(R.string.LeaveYes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
                                URL = getText(R.string.website_link) + "api/logout";

                                String data = "{"+
                                        "\"Token\":" + "\"" + pref.getString("Token", "") + "\""+
                                        "}";
                                AllDevicesLogout(data);
                            }
                        }).setNegativeButton(getText(R.string.LeaveNo), null).show();
                    }
                }
        );

        img_search.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(), anime_search.class);
                        startActivity(i);
                    }
                }
        );
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5831) {
            if (resultCode == RESULT_OK) {
                if(data.getBooleanExtra("password-changed", false)){
                    pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.clear();
                    editor.apply();
                    finish();
                }
            }
        }
    }

    private void AllDevicesLogout(String data)
    {
        final String savedata = data;

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres = new JSONObject(response);
                    if (objres.getString("Status").equals("success"))
                    {
                        pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
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
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), R.string.lbl_server_bad_response_error, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
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
}
