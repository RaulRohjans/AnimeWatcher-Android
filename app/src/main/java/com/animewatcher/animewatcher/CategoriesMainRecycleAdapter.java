package com.animewatcher.animewatcher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
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

public class CategoriesMainRecycleAdapter extends RecyclerView.Adapter<CategoriesMainRecycleAdapter.ViewHolder> {

    RecyclerView.RecycledViewPool catAnime = new RecyclerView.RecycledViewPool();


    RequestQueue requestQueue;
    String URL, domain;

    List<String> mCats;
    Context mContext;
    LayoutInflater inflater;

    public CategoriesMainRecycleAdapter(Context c, List<String> Cats, String Domain)
    {
        this.mCats = Cats;
        this.inflater = LayoutInflater.from(c);
        this.mContext = c;
        this.domain = Domain;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.categories_main_recycler_item, parent, false);
        return new CategoriesMainRecycleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(capitalizeLetters(mCats.get(position)));
        holder.viewAll.setImageResource(R.drawable.ic_baseline_arrow_forward_24_black);

        holder.lbl_error.setText(R.string.no_anime_with_category_error);
        holder.lbl_error.setVisibility(View.INVISIBLE);

        holder.viewAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));

                        Intent i = new Intent(mContext, anime_category.class);
                        i.putExtra("Category", mCats.get(position));
                        mContext.startActivity(i);
                    }
                }
        );

        //Start the other recycler views
        URL = domain + "api/get-category-anime";

        String data = "{"+
                "\"mCategoryName\":" + "\"" + mCats.get(position) + "\"" +
                "}";

        Submit(data, holder);

    }

    private void Submit(String data, ViewHolder holder)
    {
        final String savedata= data;

        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(mContext));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    holder.lbl_error.setVisibility(View.INVISIBLE);
                    List<anime_class> animes = new ArrayList<>();
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
                    if (mContext!=null) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                                holder.rcv.getContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                        );
                        linearLayoutManager.setInitialPrefetchItemCount(animes.size());
                        CategoriesSubRecycleAdapter adapter = new CategoriesSubRecycleAdapter(mContext, animes);

                        holder.rcv.setLayoutManager(linearLayoutManager);

                        holder.rcv.setAdapter(adapter);
                        holder.rcv.getRecycledViewPool().clear();
                        holder.rcv.setRecycledViewPool(catAnime);
                        holder.rcv.setNestedScrollingEnabled(false);
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    Toast.makeText(mContext, R.string.login_communication_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse.statusCode == 404)
                {
                    List<anime_class> animes = new ArrayList<>();

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                            holder.rcv.getContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                    );

                    linearLayoutManager.setInitialPrefetchItemCount(animes.size());
                    CategoriesSubRecycleAdapter adapter = new CategoriesSubRecycleAdapter(mContext, animes);

                    holder.rcv.setLayoutManager(linearLayoutManager);

                    holder.rcv.setAdapter(adapter);
                    holder.rcv.getRecycledViewPool().clear();
                    holder.rcv.setRecycledViewPool(catAnime);
                    holder.rcv.setNestedScrollingEnabled(false);
                    adapter.notifyDataSetChanged();
                    holder.lbl_error.setVisibility(View.VISIBLE);
                }
                else
                    Toast.makeText(mContext, R.string.login_communication_error, Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return mCats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, lbl_error;
        ImageView viewAll;
        RecyclerView rcv;


        public ViewHolder(@NonNull View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.lbl_category_title);
            viewAll = itemView.findViewById(R.id.img_seeAll_category);
            rcv = itemView.findViewById(R.id.crv_sub_categories);
            lbl_error = itemView.findViewById(R.id.lbl_error_subCategory);
        }
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
