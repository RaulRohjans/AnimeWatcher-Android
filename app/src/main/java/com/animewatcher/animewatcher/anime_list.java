package com.animewatcher.animewatcher;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ReportFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link anime_list#newInstance} factory method to
 * create an instance of this fragment.
 */
public class anime_list extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public anime_list() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment anime_list.
     */
    // TODO: Rename and change types and number of parameters
    public static anime_list newInstance(String param1, String param2) {
        anime_list fragment = new anime_list();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    SwipeRefreshLayout refreshLayout;
    RequestQueue requestQueue;
    String URL;

    RecyclerView animeList;
    RecycleAdapter adapter;
    List<anime_class> animes;

    boolean isLoading = false, load_complete = false;
    int current_page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_anime_list, container, false);

        animes = new ArrayList<>();
        animeList = v.findViewById(R.id.recycle_anime_list);
        refreshLayout = v.findViewById(R.id.anime_list_swiperefresh);
        refreshLayout.setOnRefreshListener(this);

        //Get all the animes in the DB
        URL = getText(R.string.website_link) + "api/get-all-anime";
        Submit();

        // Inflate the layout for this fragment
        return v;
    }

    private void Submit()
    {
        requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
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

                    if (getActivity()!=null) {
                        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                            adapter = new RecycleAdapter(getActivity(), animes);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
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
                                            URL = getText(R.string.website_link) + "api/get-all-anime?p=" + current_page;
                                            FetchMoreAnime();
                                        }
                                    }
                                }
                            });
                        }
                        else
                        {
                            adapter = new RecycleAdapter(getActivity(), animes);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
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
                                            URL = getText(R.string.website_link) + "api/get-all-anime?p=" + current_page;
                                            FetchMoreAnime();
                                        }
                                    }
                                }
                            });
                        }


                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), R.string.login_communication_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void FetchMoreAnime()
    {
        requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
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

                    if (getActivity()!=null) {
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), R.string.login_communication_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse.statusCode == 404){
                    String response = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    try {
                        JSONObject objres = new JSONObject(response);
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
                                Toast.makeText(getActivity(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(getActivity(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getActivity(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(stringRequest);
    }

    @Override
    public void onRefresh() {

        isLoading = false;
        load_complete = false;
        current_page = 1;

        URL = getText(R.string.website_link) + "api/get-all-anime";
        Submit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 2000);
    }
}