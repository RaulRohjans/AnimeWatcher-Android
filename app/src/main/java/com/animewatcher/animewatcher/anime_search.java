package com.animewatcher.animewatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class anime_search extends AppCompatActivity{

    private static final String PREF_NAME = "PreName";
    int PRIVATE_MODE = 0;
    SharedPreferences pref;

    RequestQueue requestQueue;
    String URL;

    ImageView img_back;
    EditText txt_search;
    RecyclerView animeSearch;
    SearchRecycleAdapter adapter;
    List<anime_class> animes;
    TextView lbl_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_search);

        img_back = findViewById(R.id.search_back);
        txt_search = findViewById(R.id.txt_searchField);
        lbl_error = findViewById(R.id.search_lbl_error);

        animes = new ArrayList<>();
        animeSearch = findViewById(R.id.rcv_search);
        URL = getText(R.string.website_link) + "api/get-search-anime";

        //Set focus on search bar and open keyboard
        txt_search.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(txt_search, InputMethodManager.SHOW_FORCED);

        img_back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(txt_search.getText().toString().isEmpty())
                    lbl_error.setVisibility(View.INVISIBLE);

                String data = "{"+
                        "\"Keywords\":" + "\"" + txt_search.getText().toString() + "\"" +
                        "}";

                Submit(data);
            }
        });

        txt_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    switch (i){
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_SEARCH:
                        case KeyEvent.KEYCODE_ENTER:
                            if(txt_search.getText().toString().isEmpty())
                                lbl_error.setVisibility(View.INVISIBLE);

                            String data = "{"+
                                    "\"Keywords\":" + "\"" + txt_search.getText().toString() + "\"" +
                                    "}";

                            Submit(data);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    private void Submit(String data)
    {
        final String savedata= data;

        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    lbl_error.setVisibility(View.INVISIBLE);
                    animes.clear();
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject anime = jsonArray.getJSONObject(i);

                        anime_class a = new anime_class();
                        a.setmNameEN(anime.getString("mNameEN"));
                        a.setmNameJP(anime.getString("mNameJP"));
                        a.setmDescription(anime.getString("mDescription"));
                        a.setmThumbnail(anime.getString("mThumbnail"));
                        a.setmEpisodeCount(anime.getInt("mEpisodeCount"));
                        a.setmOnGoing(anime.getBoolean("mOnGoing"));

                        //Add Categories
                        List<String> cats = new ArrayList<>();
                        JSONArray catArray = anime.getJSONArray("mCategories");
                        for (int i2 = 0; i2 < catArray.length(); i2++)
                        {
                            cats.add(catArray.get(i2).toString());
                        }
                        a.setmCategories(cats);
                        animes.add(a);
                    }

                    if (getApplication()!=null) {
                        adapter = new SearchRecycleAdapter(getApplicationContext(), animes);
                        animeSearch.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        animeSearch.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse.statusCode == 404)
                {
                    animes.clear();
                    adapter = new SearchRecycleAdapter(anime_search.this, animes);
                    animeSearch.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    animeSearch.setAdapter(adapter);
                    lbl_error.setVisibility(View.VISIBLE);
                }
                else
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

}