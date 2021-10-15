package com.animewatcher.animewatcher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class register_account extends AppCompatActivity {

    RequestQueue requestQueue;
    String URL;

    Context c;

    Button btn_register;
    EditText txt_username, txt_email, txt_password, txt_repeat_password;
    CheckBox chk_terms;
    TextView lbl_terms, lbl_login_now;
    ImageView btn_close;
    ProgressBar pgb_password;

    boolean reveal_pass = false, reveal_repeat_pass = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        c = this;

        btn_register = findViewById(R.id.btn_register);
        txt_username = findViewById(R.id.txt_register_username);
        txt_email = findViewById(R.id.txt_register_email);
        txt_password = findViewById(R.id.txt_register_password);
        txt_repeat_password = findViewById(R.id.txt_register_repeat_password);
        chk_terms = findViewById(R.id.chk_register_terms);
        lbl_terms = findViewById(R.id.lbl_register_terms_of_service);
        lbl_login_now = findViewById(R.id.lbl_register_login_now);
        btn_close = findViewById(R.id.topnav_register_account_close);
        pgb_password = findViewById(R.id.pgb_register_password);

        setProgressMax(pgb_password ,pgb_password.getMax());

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

        lbl_terms.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                        Intent i = new Intent(getApplicationContext(), disclaimer_privacy_policy.class);
                        startActivity(i);
                    }
                }
        );

        lbl_login_now.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                        Intent i = new Intent(getApplicationContext() ,login.class);
                        finish();
                        startActivity(i);
                    }
                }
        );

        txt_password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                int char_count = txt_password.getText().length();
                if (char_count >= 8)
                {
                    setProgressAnimate(pgb_password, 8);
                    LayerDrawable progressBarDrawable = (LayerDrawable) pgb_password.getProgressDrawable();
                    Drawable progressDrawable = progressBarDrawable.getDrawable(1);

                    if(char_count <= 32){
                        if (checkString(txt_password.getText().toString()))
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
                        setProgressAnimate(pgb_password, 0);
                    }
                    else{
                        setProgressAnimate(pgb_password, char_count);
                    }

                    LayerDrawable progressBarDrawable = (LayerDrawable) pgb_password.getProgressDrawable();
                    Drawable progressDrawable = progressBarDrawable.getDrawable(1);
                    progressDrawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.progress_red), PorterDuff.Mode.SRC_IN);
                }

                return false;
            }
        });

        txt_password.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;

                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (txt_password.getRight() - txt_password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(!reveal_pass){
                            txt_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_visibility_24, 0);
                            txt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                            txt_password.setSelection(txt_password.getText().length());
                            reveal_pass = true;
                        }
                        else{
                            txt_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_visibility_off_24, 0);
                            txt_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            txt_password.setSelection(txt_password.getText().length());
                            reveal_pass = false;
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        txt_repeat_password.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_RIGHT = 2;

                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(motionEvent.getRawX() >= (txt_repeat_password.getRight() - txt_repeat_password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(!reveal_repeat_pass){
                            txt_repeat_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_visibility_24, 0);
                            txt_repeat_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                            txt_repeat_password.setSelection(txt_repeat_password.getText().length());
                            reveal_repeat_pass = true;
                        }
                        else{
                            txt_repeat_password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_visibility_off_24, 0);
                            txt_repeat_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            txt_repeat_password.setSelection(txt_repeat_password.getText().length());
                            reveal_repeat_pass = false;
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        btn_register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(txt_email.getText().toString().length() > 0 && txt_username.getText().toString().length() > 0 && txt_password.getText().toString().length() > 0 && txt_repeat_password.getText().toString().length() > 0)
                        {
                            if(isValidEmail(txt_email.getText().toString())){
                                if(chk_terms.isChecked()){
                                    if(txt_password.getText().toString().equals(txt_repeat_password.getText().toString())){
                                        if(txt_password.getText().toString().length() >= 8 && txt_password.getText().toString().length() <= 32 && checkString(txt_password.getText().toString())){
                                            URL = getText(R.string.website_link) + "api/register-new-user";
                                            String data = "{"+
                                                    "\"username\":" + "\"" + txt_username.getText().toString() + "\","+
                                                    "\"email\":" + "\"" + txt_email.getText().toString() + "\","+
                                                    "\"password\":" + "\"" + txt_password.getText().toString() + "\""+
                                                    "}";
                                            Submit(data);
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), R.string.lbl_password_no_requirements, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), R.string.lbl_register_password_match_error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), R.string.lbl_register_terms_error, Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(), R.string.lbl_register_email_syntax_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getApplicationContext(), R.string.lbl_register_empty_fields_error, Toast.LENGTH_SHORT).show();
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
                    if (objres.getString("Status").equals("success"))
                    {
                        new AlertDialog.Builder(c)
                                .setTitle(getText(R.string.lbl_register_success_title))
                                .setMessage(getText(R.string.lbl_register_success_message))
                                .setPositiveButton(getText(R.string.lbl_register_success_okay_option), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(getApplicationContext(), login.class);
                                finish();
                                startActivity(i);
                            }
                        }).create().show();
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
                if(error.networkResponse.statusCode == 401)
                {
                    String response = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    try {
                        JSONObject objres = new JSONObject(response);
                        if(objres.getString("Status").equals("error")){
                            if(objres.getString("Error").equals("email is taken")){
                                Toast.makeText(getApplicationContext(), R.string.lbl_register_email_in_use_error, Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                if(objres.getString("Error").equals("username is taken")){
                                    Toast.makeText(getApplicationContext(), R.string.lbl_register_username_is_taken_error, Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
                                }
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

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
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
}