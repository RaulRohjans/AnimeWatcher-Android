package com.animewatcher.animewatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.LayoutDirection;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class anime_page extends AppCompatActivity {

    private static final String PREF_NAME = "PreName";
    int PRIVATE_MODE = 0;

    SharedPreferences pref;

    AnimePagePagerAdapter pager_adapter;

    String URL;

    ImageView img_back, img_cover;
    CollapsingToolbarLayout ctl_;
    TextView gradient;
    AppBarLayout appBarLayout;

    RelativeLayout btn_add_mylist;
    TextView lbl_add_mylist;
    ImageView img_mylist, custom_swipe_image;

    Boolean added = false, clicked = false;

    String AnimeName, AnimeCover;

    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_page);

        //Get intent info
        AnimeName = getIntent().getStringExtra("mNameEN");
        AnimeCover = getIntent().getStringExtra("mThumbnail");

        img_back = findViewById(R.id.anime_page_back);
        img_cover = findViewById(R.id.anime_page_cover);
        custom_swipe_image = findViewById(R.id.custom_swipe_toast_img);

        pager = findViewById(R.id.anime_episode_pager);

        btn_add_mylist = findViewById(R.id.btn_add_mylist);
        lbl_add_mylist = findViewById(R.id.lbl_add_mylist);
        img_mylist = findViewById(R.id.img_heart_mylist);

        appBarLayout = findViewById(R.id.anime_page_appbar);
        gradient = findViewById(R.id.lbl_anime_page_gradient);
        ctl_ = findViewById(R.id.anime_page_collapsing_toolbar);

        //Set activity properties
        Glide.with(this).load(AnimeCover).into(img_cover);
        ctl_.setTitle(capitalizeLetters(AnimeName));

        img_back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        appBarLayout.addOnOffsetChangedListener(
                new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        float percentage = (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange();
                        if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                            //  Collapsed
                            //Hide your TextView here
                            gradient.setVisibility(View.GONE);
                        } else if (verticalOffset == 0) {
                            //Expanded
                            //Show your TextView here
                            gradient.setVisibility(View.VISIBLE);
                        } else {
                            //In Between
                            gradient.setVisibility(View.VISIBLE);
                            gradient.animate().alpha(percentage);
                        }
                    }
                }
        );

        //Check if Anime is in user's List
        pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        if(pref.getString("Token", null) != null)
        {
            URL = getText(R.string.website_link) + "api/my-list-check";
            String data = "{"+
                    "\"Token\":" + "\"" + pref.getString("Token", null) + "\","+
                    "\"mNameEN\":" + "\"" + AnimeName + "\"" +
                    "}";
            CheckMyList(data);
        }

        //Set View pager pages
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("temporaryAnimeName", AnimeName);
        editor.apply();

        List<Fragment> list = new ArrayList<>();
        list.add(new episodes_normal());
        list.add(new episodes_extra());

        pager_adapter = new AnimePagePagerAdapter(getSupportFragmentManager(), list);
        pager.setAdapter(pager_adapter);

        //Show swipe toast if first time
        int nightModeFlags =
                getApplicationContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                custom_swipe_image.setImageResource(R.drawable.swipe_right_24_white);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                custom_swipe_image.setImageResource(R.drawable.swipe_right_24_black);
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                custom_swipe_image.setImageResource(R.drawable.swipe_right_24_black);
                break;
        }

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_swipe_toast, (ViewGroup) findViewById(R.id.custom_swipe_toast));
        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

        btn_add_mylist.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(pref.getString("Token", null) != null) {
                            if (!clicked) {
                                String mylistData = "{" +
                                        "\"Token\":" + "\"" + pref.getString("Token", null) + "\"," +
                                        "\"mNameEN\":" + "\"" + AnimeName + "\"" +
                                        "}";

                                if (!added) {
                                    URL = getText(R.string.website_link) + "api/my-list-add";
                                    AddMyList(mylistData);
                                } else {
                                    URL = getText(R.string.website_link) + "api/my-list-remove";
                                    RemoveMyList(mylistData);
                                }

                                clicked = true;
                                btn_add_mylist.setForeground(null);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        clicked = false;

                                        //Set relative layout clickable again
                                        TypedValue outValue = new TypedValue();
                                        getApplicationContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                                        btn_add_mylist.setForeground(getDrawable(outValue.resourceId));
                                    }
                                }, 750);
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), R.string.anime_page_mylist_login_error, Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), login.class);
                            startActivity(i);
                        }
                    }
                }
        );

    }


    private void AddMyList(String data)
    {
        final String savedata= data;

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getString("Status").equals("Success"))
                    {
                        Toast.makeText(getApplicationContext(), R.string.mylist_success_message, Toast.LENGTH_SHORT).show();
                        lbl_add_mylist.setText(getText(R.string.remove_from_list_text));
                        img_mylist.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_24_filled));
                        added = true;
                    }
                    else
                    {
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
                error.printStackTrace();
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

    private void RemoveMyList(String data)
    {
        final String savedata= data;

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getString("Status").equals("Success"))
                    {
                        Toast.makeText(getApplicationContext(), R.string.mylist_remove_sucess_message, Toast.LENGTH_SHORT).show();
                        lbl_add_mylist.setText(getText(R.string.add_to_list_text));
                        img_mylist.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_24_border));
                        added = false;
                    }
                    else
                    {
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
                error.printStackTrace();
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

    private void CheckMyList(String data)
    {
        final String savedata= data;

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getBoolean("Added"))
                    {
                        lbl_add_mylist.setText(getText(R.string.remove_from_list_text));
                        img_mylist.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_24_filled));
                        added = true;
                    }
                    else
                    {
                        lbl_add_mylist.setText(getText(R.string.add_to_list_text));
                        img_mylist.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_24_border));
                        added = false;
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
                error.printStackTrace();
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

    private String capitalizeLetters(String str)
    {
        String[] strArray = str.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }

        return builder.toString();
    }
}