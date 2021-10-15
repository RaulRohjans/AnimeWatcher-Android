package com.animewatcher.animewatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class change_email extends AppCompatActivity {

    private static final String PREF_NAME = "PreName";
    int PRIVATE_MODE = 0;

    SharedPreferences pref;

    RequestQueue requestQueue;
    String URL;

    ImageView btn_close;
    EditText txt_email;
    Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        btn_close = findViewById(R.id.topnav_change_email_close);
        btn_save = findViewById(R.id.btn_save_email_changes);
        txt_email = findViewById(R.id.txt_change_email);

        //Load Data
        pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);

        URL = getText(R.string.website_link) + "api/get-user-data";
        String data = "{"+
                "\"Token\":" + "\"" + pref.getString("Token", "") + "\""+
                "}";
        GetUserData(data);
    }

    private void GetUserData(String data)
    {
        final String savedata = data;

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres = new JSONObject(response);
                    if (!objres.has("Status"))
                    {
                        txt_email.setText(objres.getString("email"));

                        //Listeners
                        btn_close.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                                        finish();
                                    }
                                }
                        );

                        btn_save.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));

                                        if(!txt_email.getText().toString().equals("")){
                                            if(isValidEmail(txt_email.getText().toString())){
                                                URL = getText(R.string.website_link) + "api/settings-change-email";
                                                String data = "{"+
                                                        "\"Token\":" + "\"" + pref.getString("Token", "") + "\","+
                                                        "\"email\":" + "\"" + txt_email.getText().toString() + "\""+
                                                        "}";
                                                Submit(data);
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(), R.string.lbl_register_email_syntax_error, Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), R.string.lbl_empty_fields_error, Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }
                        );
                    }
                    else
                    {
                        if(objres.getString("Status").equals("error"))
                            Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_LONG).show();
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

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void Submit(String data)
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

                        //Insert new
                        editor.putString("email", txt_email.getText().toString());

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
                if(error.networkResponse.statusCode == 403){
                    String response = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    try {
                        JSONObject objres = new JSONObject(response);
                        if(objres.getString("Status").equals("error")){
                            if(objres.getString("Error").equals("email in use")){
                                Toast.makeText(getApplicationContext(), R.string.lbl_register_email_in_use_error, Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
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

}