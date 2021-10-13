package com.animewatcher.animewatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {

    private static final String PREF_NAME = "PreName";
    int PRIVATE_MODE = 0;

    RequestQueue requestQueue;
    String URL;

    ImageView img_close;
    TextView lbl_register, lbl_forgot_pass;
    EditText txt_username, txt_password;
    Button btn_login;

    boolean reveal_password = false;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        img_close = findViewById(R.id.topnav_login_close);
        lbl_register = findViewById(R.id.lbl_login_register_now);
        lbl_forgot_pass = findViewById(R.id.lbl_login_forgotPassword);
        txt_username = findViewById(R.id.txt_login_username);
        txt_password = findViewById(R.id.txt_login_password);
        btn_login = findViewById(R.id.btn_login);

        img_close.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));

                        finish();
                    }
                }
        );

        txt_password.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;

                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (txt_password.getRight() - txt_password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(!reveal_password){
                            txt_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_visibility_24, 0);
                            txt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                            txt_password.setSelection(txt_password.getText().length());
                            reveal_password = true;
                        }
                        else{
                            txt_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_visibility_off_24, 0);
                            txt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            txt_password.setSelection(txt_password.getText().length());
                            reveal_password = false;
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        txt_username.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    switch (i){
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            if(!txt_password.getText().toString().equals("") && !txt_username.getText().toString().equals(""))
                            {
                                URL = getText(R.string.website_link) + "api/authenticate-user";
                                String data = "{"+
                                        "\"username\":" + "\"" + txt_username.getText().toString() + "\","+
                                        "\"password\":" + "\"" + txt_password.getText().toString() + "\""+
                                        "}";
                                Submit(data);

                            }
                            else
                            {
                                if(txt_username.getText().toString().equals("") && !txt_password.getText().toString().equals(""))
                                {
                                    txt_username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                                    Toast.makeText(getApplicationContext(), R.string.login_username_error, Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    if(txt_password.getText().toString().equals("") && !txt_username.getText().toString().equals(""))
                                    {
                                        txt_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                                        Toast.makeText(getApplicationContext(), R.string.login_password_error, Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        txt_username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                                        txt_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                                        Toast.makeText(getApplicationContext(), R.string.login_fields_error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        txt_password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    switch (i){
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            if(!txt_password.getText().toString().equals("") && !txt_username.getText().toString().equals(""))
                            {
                                URL = getText(R.string.website_link) + "api/authenticate-user";
                                String data = "{"+
                                        "\"username\":" + "\"" + txt_username.getText().toString() + "\","+
                                        "\"password\":" + "\"" + txt_password.getText().toString() + "\""+
                                        "}";
                                Submit(data);

                            }
                            else
                            {
                                if(txt_username.getText().toString().equals("") && !txt_password.getText().toString().equals(""))
                                {
                                    txt_username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                                    Toast.makeText(getApplicationContext(), R.string.login_username_error, Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    if(txt_password.getText().toString().equals("") && !txt_username.getText().toString().equals(""))
                                    {
                                        txt_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                                        Toast.makeText(getApplicationContext(), R.string.login_password_error, Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        txt_username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                                        txt_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                                        Toast.makeText(getApplicationContext(), R.string.login_fields_error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        lbl_register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));

                        Intent i = new Intent(getApplicationContext(), register_account.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }
        );

        lbl_forgot_pass.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));

                        Intent i = new Intent(getApplicationContext(), forgot_password.class);
                        startActivity(i);

                    }
                }
        );

        btn_login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                        if(!txt_password.getText().toString().equals("") && !txt_username.getText().toString().equals(""))
                        {
                            URL = getText(R.string.website_link) + "api/authenticate-user";
                            String data = "{"+
                                    "\"username\":" + "\"" + txt_username.getText().toString() + "\","+
                                    "\"password\":" + "\"" + txt_password.getText().toString() + "\""+
                                    "}";
                            Submit(data);

                        }
                        else
                        {
                            if(txt_username.getText().toString().equals("") && !txt_password.getText().toString().equals(""))
                            {
                                txt_username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                                Toast.makeText(getApplicationContext(), R.string.login_username_error, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                if(txt_password.getText().toString().equals("") && !txt_username.getText().toString().equals(""))
                                {
                                    txt_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                                    Toast.makeText(getApplicationContext(), R.string.login_password_error, Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    txt_username.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                                    txt_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_error_24, 0);
                                    Toast.makeText(getApplicationContext(), R.string.login_fields_error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
        );
    }

    private void Submit(String data)
    {
        final String savedata= data;

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
                        if(objres.getString("Status").equals("Authenticated"))
                        {
                            SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("Token", objres.getString("Token"));
                            editor.putInt("id", objres.getInt("id"));
                            editor.putString("username", objres.getString("username"));
                            editor.putString("password", objres.getString("password"));
                            editor.putString("first_name", objres.getString("first_name"));
                            editor.putString("email", objres.getString("email"));
                            editor.putString("last_name", objres.getString("last_name"));
                            editor.putBoolean("is_staff", objres.getBoolean("is_staff"));
                            editor.putBoolean("is_superuser", objres.getBoolean("is_superuser"));
                            editor.putBoolean("is_active", objres.getBoolean("is_active"));
                            editor.putString("last_login", objres.getString("last_login"));
                            editor.putString("date_joined", objres.getString("date_joined"));
                            editor.apply();


                            txt_username.setText("");
                            txt_password.setText("");
                            finish();
                        }
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