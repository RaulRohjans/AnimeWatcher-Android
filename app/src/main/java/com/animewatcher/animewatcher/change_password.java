package com.animewatcher.animewatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

public class change_password extends AppCompatActivity {

    private static final String PREF_NAME = "PreName";
    int PRIVATE_MODE = 0;

    SharedPreferences pref;

    RequestQueue requestQueue;
    String URL;

    EditText txt_old, txt_new, txt_repeat_new;
    Button btn_save;
    ImageView btn_close;
    ProgressBar password_strength;

    boolean reveal_old = false, reveal_new = false, reveal_repeat_new = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        txt_new = findViewById(R.id.txt_change_password_new);
        txt_repeat_new = findViewById(R.id.txt_change_password_repeat_new);
        txt_old = findViewById(R.id.txt_change_password_old);
        btn_save = findViewById(R.id.btn_save_password_changes);
        btn_close = findViewById(R.id.topnav_change_password_close);
        password_strength = findViewById(R.id.pgb_change_password);

        setProgressMax(password_strength ,password_strength.getMax());

        //Listeners
        btn_close.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                        Intent data = new Intent();
                        data.putExtra("password-changed", false);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                }
        );

        btn_save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(txt_old.getText().toString().length() > 0 && txt_new.getText().toString().length() > 0 && txt_repeat_new.getText().toString().length() > 0)
                        {
                            if(txt_new.getText().toString().equals(txt_repeat_new.getText().toString())){
                                pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);

                                URL = getText(R.string.website_link) + "api/settings-change-password";
                                String data = "{"+
                                        "\"Token\":" + "\"" + pref.getString("Token", "") + "\","+
                                        "\"old_password\":" + "\"" + txt_old.getText().toString() + "\","+
                                        "\"new_password\":" + "\"" + txt_new.getText().toString() + "\""+
                                        "}";
                                Submit(data);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), R.string.lbl_passwords_no_match, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), R.string.lbl_empty_fields_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        txt_new.setOnKeyListener(
                new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        int char_count = txt_new.getText().length();
                        if (char_count >= 8)
                        {
                            setProgressAnimate(password_strength, 8);
                            LayerDrawable progressBarDrawable = (LayerDrawable) password_strength.getProgressDrawable();
                            Drawable progressDrawable = progressBarDrawable.getDrawable(1);

                            if(char_count <= 32){
                                if (checkString(txt_new.getText().toString()))
                                    progressDrawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.progress_green), PorterDuff.Mode.SRC_IN);
                                else
                                    progressDrawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.progress_red), PorterDuff.Mode.SRC_IN);
                            }
                            else{
                                progressDrawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.progress_red), PorterDuff.Mode.SRC_IN);
                            }

                        }
                        else
                        {
                            if(char_count <= 0){
                                setProgressAnimate(password_strength, 0);
                            }
                            else{
                                setProgressAnimate(password_strength, char_count);
                            }

                            LayerDrawable progressBarDrawable = (LayerDrawable) password_strength.getProgressDrawable();
                            Drawable progressDrawable = progressBarDrawable.getDrawable(1);
                            progressDrawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.progress_red), PorterDuff.Mode.SRC_IN);
                        }
                        return false;
                    }
                }
        );

        txt_old.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;

                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (txt_old.getRight() - txt_old.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(!reveal_old){
                            txt_old.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_visibility_24, 0);
                            txt_old.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                            txt_old.setSelection(txt_repeat_new.getText().length());
                            reveal_old = true;
                        }
                        else{
                            txt_old.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_visibility_off_24, 0);
                            txt_old.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            txt_old.setSelection(txt_repeat_new.getText().length());
                            reveal_old = false;
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        txt_new.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;

                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (txt_new.getRight() - txt_new.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(!reveal_new){
                            txt_new.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_visibility_24, 0);
                            txt_new.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                            txt_new.setSelection(txt_repeat_new.getText().length());
                            reveal_new = true;
                        }
                        else{
                            txt_new.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_visibility_off_24, 0);
                            txt_new.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            txt_new.setSelection(txt_repeat_new.getText().length());
                            reveal_new = false;
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        txt_repeat_new.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;

                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (txt_repeat_new.getRight() - txt_repeat_new.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(!reveal_repeat_new){
                            txt_repeat_new.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_visibility_24, 0);
                            txt_repeat_new.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                            txt_repeat_new.setSelection(txt_repeat_new.getText().length());
                            reveal_repeat_new = true;
                        }
                        else{
                            txt_repeat_new.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_visibility_off_24, 0);
                            txt_repeat_new.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            txt_repeat_new.setSelection(txt_repeat_new.getText().length());
                            reveal_repeat_new = false;
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private static boolean checkString(String str) {
        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;
        for(int i=0;i < str.length();i++) {
            ch = str.charAt(i);
            if( Character.isDigit(ch)) {
                numberFlag = true;
            }
            else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
            if(numberFlag && capitalFlag && lowerCaseFlag)
                return true;
        }
        return false;
    }

    private void setProgressMax(ProgressBar pb, int max) {
        pb.setMax(max * 1000);
    }

    private void setProgressAnimate(ProgressBar pb, int progressTo)
    {
        ObjectAnimator animation = ObjectAnimator.ofInt(pb, "progress", pb.getProgress(), progressTo * 1000);
        animation.setDuration(500);
        animation.setAutoCancel(true);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
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

                        URL = getText(R.string.website_link) + "api/logout";
                        String data = "{"+
                                "\"Token\":" + "\"" + pref.getString("Token", "") + "\""+
                                "}";
                        AllDevicesLogout(data);
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
                        Intent data = new Intent();
                        data.putExtra("password-changed", true);
                        setResult(RESULT_OK, data);
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

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("password-changed", false);
        setResult(RESULT_OK, data);
        finish();
    }

}