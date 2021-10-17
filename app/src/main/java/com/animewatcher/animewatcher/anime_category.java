package com.animewatcher.animewatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class anime_category extends AppCompatActivity {

    RequestQueue requestQueue;
    String URL;

    RecyclerView animeList;
    RecycleAdapter adapter;
    List<anime_class> animes;

    String category;

    TextView lbl_cat, lbl_error;
    ImageView img_back;

    boolean isLoading = false, load_complete = false;
    int current_page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_category);

        category = getIntent().getStringExtra("Category");

        animes = new ArrayList<>();
        animeList = findViewById(R.id.recycle_anime_category);
        lbl_cat = findViewById(R.id.lbl_anime_category_title);
        lbl_error = findViewById(R.id.lbl_error_anime_category);
        img_back = findViewById(R.id.topnav_anime_category_back);

        lbl_error.setVisibility(View.GONE);

        lbl_cat.setText(capitalizeLetters(category));

        img_back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));

                        finish();
                    }
                }
        );

        URL = getText(R.string.website_link) + "api/get-category-anime";
        String data = "{"+
                "\"mCategoryName\":" + "\"" + category + "\"" +
                "}";

        Submit(data);
    }

    private void Submit(String data)
    {
        final String savedata = data;

        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    lbl_error.setVisibility(View.GONE);
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
                        if(getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                            adapter = new RecycleAdapter(anime_category.this, animes);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(anime_category.this, 3, GridLayoutManager.VERTICAL, false);
                            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                @Override
                                public int getSpanSize(int position) {
                                    switch (adapter.getItemViewType(position)){
                                        case 0:
                                            return 3;
                                        case 1:
                                            return 1;
                                        default:
                                            return -1;
                                    }
                                }
                            });
                            animeList.setLayoutManager(gridLayoutManager);
                            animeList.setAdapter(adapter);

                            //Add On Scroll Listener
                            animeList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);
                                    GridLayoutManager layoutManager = (GridLayoutManager) animeList.getLayoutManager();
                                    if(!isLoading && !load_complete){
                                        if(layoutManager != null && layoutManager.findLastVisibleItemPosition() == animes.size()-1){
                                            isLoading = true;
                                            current_page++;
                                            animes.add(null);
                                            adapter.notifyItemInserted(animes.size()-1);

                                            URL = getText(R.string.website_link) + "api/get-category-anime?p=" + current_page;
                                            String data = "{"+
                                                    "\"mCategoryName\":" + "\"" + category + "\"" +
                                                    "}";
                                            FetchMoreAnime(data);
                                        }
                                    }
                                }
                            });
                        }
                        else
                        {
                            adapter = new RecycleAdapter(anime_category.this, animes);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(anime_category.this, 2, GridLayoutManager.VERTICAL, false);
                            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                @Override
                                public int getSpanSize(int position) {
                                    switch (adapter.getItemViewType(position)){
                                        case 0:
                                            return 2;
                                        case 1:
                                            return 1;
                                        default:
                                            return -1;
                                    }
                                }
                            });
                            animeList.setLayoutManager(gridLayoutManager);
                            animeList.setAdapter(adapter);

                            //Add On Scroll Listener
                            animeList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);
                                    GridLayoutManager layoutManager = (GridLayoutManager) animeList.getLayoutManager();
                                    if(!isLoading && !load_complete){
                                        if(layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == animes.size()-1){
                                            isLoading = true;
                                            current_page++;
                                            animes.add(null);
                                            adapter.notifyItemInserted(animes.size()-1);

                                            URL = getText(R.string.website_link) + "api/get-category-anime?p=" + current_page;
                                            String data = "{"+
                                                    "\"mCategoryName\":" + "\"" + category + "\"" +
                                                    "}";
                                            FetchMoreAnime(data);
                                        }
                                    }
                                }
                            });
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
                if(error.networkResponse.statusCode == 404)
                {
                    lbl_error.setVisibility(View.VISIBLE);
                }
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

    private void FetchMoreAnime(String data)
    {
        final String savedata = data;

        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    //Remove the last item
                    animes.remove(animes.size()-1);

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
                    adapter.notifyDataSetChanged();
                    isLoading = false;

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String response = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                try {
                    JSONObject objres = new JSONObject(response);
                    if(error.networkResponse.statusCode == 404){
                        if(objres.getString("Status").equals("error")){
                            if(objres.getString("Error").equals("no more pages")){
                                isLoading = false;
                                load_complete = true;
                                if (animes.get(animes.size()-1) == null) {
                                    animes.remove(animes.size()-1);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            else{
                                if(objres.getString("Error").equals("No items to show")) {
                                    lbl_error.setText(R.string.mylist_no_items);
                                    lbl_error.setVisibility(View.VISIBLE);
                                }
                            }
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