package com.animewatcher.animewatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class submit_new_anime extends AppCompatActivity {

    private static final String PREF_NAME = "PreName";
    int PRIVATE_MODE = 0;

    RequestQueue requestQueue;
    String URL;

    EditText txt_username, txt_email, txt_anime, txt_body;
    Button btn_submit;
    ImageView btn_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_new_anime);

        txt_username = findViewById(R.id.txt_subAnime_username);
        txt_username.setInputType(InputType.TYPE_NULL);
        txt_email = findViewById(R.id.txt_subAnime_email);
        txt_anime = findViewById(R.id.txt_subAnime_anime);
        txt_body = findViewById(R.id.txt_subAnime_body);
        btn_submit = findViewById(R.id.btn_subAnime);
        btn_close = findViewById(R.id.topnav_submit_new_anime_close);

        //If the user is logged in, prefill fields
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        if(pref.getString("Token", null) != null)
        {
            txt_username.setText(pref.getString("username", ""));
            txt_email.setText(pref.getString("email", ""));
        }

        btn_close.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                        finish();
                    }
                }
        );

        btn_submit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                        if(!txt_username.getText().toString().isEmpty() && !txt_email.getText().toString().isEmpty() && !txt_anime.getText().toString().isEmpty())
                        {
                            if(Patterns.EMAIL_ADDRESS.matcher(txt_email.getText().toString()).matches())
                            {
                                Date time = Calendar.getInstance().getTime();
                                String formTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(time);
                                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                                URL = getText(R.string.website_link) + "api/submit-anime";


                                if(!txt_body.getText().toString().isEmpty())
                                {
                                    String data = "{"+
                                            "\"mAnimeName\":" + "\"" + txt_anime.getText().toString() + "\","+
                                            "\"mUserEmail\":" + "\"" + txt_email.getText().toString() + "\","+
                                            "\"mUsername\":" + "\"" + txt_username.getText().toString() + "\","+
                                            "\"mMoreInfo\":" + "\"" + txt_body.getText().toString() + "\","+
                                            "\"mSubDate\":" + "\"" + date + "T" + formTime + "\""+
                                            "}";

                                    Submit(data);
                                }
                                else
                                {
                                    String data = "{"+
                                            "\"mAnimeName\":" + "\"" + txt_anime.getText().toString() + "\","+
                                            "\"mUserEmail\":" + "\"" + txt_email.getText().toString() + "\","+
                                            "\"mUsername\":" + "\"" + txt_username.getText().toString() + "\","+
                                            "\"mSubDate\":" + "\"" + date + "T" + formTime + "\""+
                                            "}";
                                    Submit(data);
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), R.string.subAnime_email_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), R.string.subAnime_input_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
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
                    if (objres.getString("Status").equals("error"))
                    {
                        Toast.makeText(getApplicationContext(), objres.getString("Error"), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        if(objres.getString("Status").equals("Success"))
                        {
                            Toast.makeText(getApplicationContext(), R.string.subAnime_success, Toast.LENGTH_SHORT).show();

                            txt_anime.setText("");
                            txt_body.setText("");
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_LONG).show();

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