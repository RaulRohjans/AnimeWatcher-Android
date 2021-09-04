package com.animewatcher.animewatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

public class account_settings extends AppCompatActivity {

    private static final String PREF_NAME = "PreName";
    int PRIVATE_MODE = 0;
    SharedPreferences pref;

    RequestQueue requestQueue;
    String URL;

    TextView lbl_username, lbl_email;
    RelativeLayout current_user, current_email, profile_settings, change_password, donations, privacy_policy, disclaimer, logout;
    ImageView img_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        lbl_username = findViewById(R.id.lbl_current_user_extra);
        lbl_email = findViewById(R.id.lbl_current_email_extra);

        current_user = findViewById(R.id.option_current_user);
        current_email = findViewById(R.id.option_current_email);
        profile_settings = findViewById(R.id.option_change_account);
        change_password = findViewById(R.id.option_change_password);
        donations = findViewById(R.id.option_donate);
        privacy_policy = findViewById(R.id.option_privacy_policy);
        disclaimer = findViewById(R.id.option_disclaimer);
        logout = findViewById(R.id.option_logout);
        img_search = findViewById(R.id.topnav_search);

        //Get account info and set it to TextViews
        pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        lbl_username.setText(pref.getString("username", ""));
        lbl_email.setText(pref.getString("email", ""));

        //Listeners
        profile_settings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
                        String Token = pref.getString("Token", "");

                        Intent privacy_policy = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.website_link) + "account-settings/" + Token +"/"));
                        startActivity(privacy_policy);
                    }
                }
        );

        change_password.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
                        String Token = pref.getString("Token", "");

                        Intent privacy_policy = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.website_link) + "change-password/" + Token +"/"));
                        startActivity(privacy_policy);
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

        privacy_policy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent privacy_policy = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.website_link) + "privacy-policy/"));
                        startActivity(privacy_policy);
                    }
                }
        );

        disclaimer.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent disclaimer = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.website_link) + "disclaimer/"));
                        startActivity(disclaimer);
                    }
                }
        );

        logout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear();
                        editor.apply();
                        finish();
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
}
