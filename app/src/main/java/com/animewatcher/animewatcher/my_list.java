package com.animewatcher.animewatcher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link my_list#newInstance} factory method to
 * create an instance of this fragment.
 */
public class my_list extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public my_list() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment my_list.
     */
    // TODO: Rename and change types and number of parameters
    public static my_list newInstance(String param1, String param2) {
        my_list fragment = new my_list();
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

    private static final String PREF_NAME = "PreName";
    int PRIVATE_MODE = 0;
    SharedPreferences pref;

    SwipeRefreshLayout refreshLayout;
    TextView txt_loginError;

    RequestQueue requestQueue;
    String URL;

    RecyclerView animeList;
    RecycleAdapter adapter;
    List<anime_class> animes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_list, container, false);

        txt_loginError = v.findViewById(R.id.lbl_mylist_loginError);
        animeList = v.findViewById(R.id.recycle_mylist);
        refreshLayout = v.findViewById(R.id.my_list_swiperefresh);
        refreshLayout.setOnRefreshListener(this);

        pref = Objects.requireNonNull(getActivity()).getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        //If the user is not logged in, take him to the login activity
        if(pref.getString("Token", null) != null)
        {
            txt_loginError.setVisibility(View.INVISIBLE);

            animes = new ArrayList<>();

            //Get all the animes in the DB
            URL = getText(R.string.website_link) + "api/get-mylist-anime";
            String data = "{"+
                    "\"Token\":" + "\"" + pref.getString("Token", "") + "\""+
                    "}";
            Submit(data);
        }
        else
        {
            txt_loginError.setVisibility(View.VISIBLE);
            Intent i = new Intent(getActivity(), login.class);
            startActivity(i);
        }


        // Inflate the layout for this fragment
        return v;
    }

    private void Submit(String data)
    {
        final String savedata= data;

        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject anime = jsonArray.getJSONObject(i);

                        anime_class a = new anime_class();
                        a.setmNameEN(capitalizeLetters(anime.getString("mNameEN")));
                        a.setmNameJP(capitalizeLetters(anime.getString("mNameJP")));
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
                            animeList.setLayoutManager(gridLayoutManager);
                            animeList.setAdapter(adapter);
                        }
                        else
                        {
                            adapter = new RecycleAdapter(getActivity(), animes);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
                            animeList.setLayoutManager(gridLayoutManager);
                            animeList.setAdapter(adapter);
                        }
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), R.string.login_communication_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse.statusCode == 404)
                {
                    txt_loginError.setText(R.string.mylist_no_items);
                    txt_loginError.setVisibility(View.VISIBLE);
                }
                else
                {
                    if(error.networkResponse.statusCode == 500)
                    {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.clear();
                        editor.apply();
                        Toast.makeText(getActivity(), R.string.session_expired_error, Toast.LENGTH_SHORT).show();
                    }
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

    @Override
    public void onRefresh() {

        pref = Objects.requireNonNull(getActivity()).getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        //If the user is not logged in, take him to the login activity
        if(pref.getString("Token", null) != null)
        {
            txt_loginError.setVisibility(View.INVISIBLE);

            animes = new ArrayList<>();

            //Get all the animes in the DB
            URL = getText(R.string.website_link) + "api/get-mylist-anime";
            String data = "{"+
                    "\"Token\":" + "\"" + pref.getString("Token", "") + "\""+
                    "}";
            Submit(data);
        }
        else
        {
            txt_loginError.setVisibility(View.VISIBLE);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
            }
        }, 2000);
    }
}