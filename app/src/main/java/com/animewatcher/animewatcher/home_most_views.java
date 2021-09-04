package com.animewatcher.animewatcher;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home_most_views#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home_most_views extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public home_most_views() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home_most_views.
     */
    // TODO: Rename and change types and number of parameters
    public static home_most_views newInstance(String param1, String param2) {
        home_most_views fragment = new home_most_views();
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

    RequestQueue requestQueue;
    String URL;

    List<episode_class> episodes;
    RecyclerView rcv_;
    HomeRecycleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_most_views, container, false);

        rcv_ = v.findViewById(R.id.rcv_home_most_views);
        episodes = new ArrayList<>();
        URL = getText(R.string.website_link) + "api/get-episodes-most-views";

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
                    episodes.clear();
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject episode = jsonArray.getJSONObject(i);

                        episode_class e = new episode_class();
                        e.setmID(episode.getInt("mID"));
                        e.setmEpisodeNumber(episode.getInt("mEpisodeNumber"));
                        e.setmNameEN(episode.getString("mNameEN"));
                        e.setmNameJP(episode.getString("mNameJP"));
                        e.setmLengthSecs(episode.getInt("mLengthSecs"));
                        e.setmViews(episode.getInt("mViews"));

                        String dateTime[] = episode.getString("mReleaseDate").split("T");
                        e.setmReleaseDate(Date.valueOf(dateTime[0]));

                        e.setmThumbnail(episode.getString("mThumbnail"));
                        e.setmVideoFileLink(episode.getString("mVideoFileLink"));
                        e.setmAnime(episode.getString("mAnime"));
                        e.setMisVCDN(episode.getBoolean("mVCDN"));
                        episodes.add(e);

                        if (getActivity()!=null) {
                            if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                                adapter = new HomeRecycleAdapter(getActivity(), episodes, getText(R.string.lbl_title_episode).toString());
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
                                rcv_.setLayoutManager(gridLayoutManager);
                                rcv_.setAdapter(adapter);
                            }
                            else
                            {
                                adapter = new HomeRecycleAdapter(getActivity(), episodes, getText(R.string.lbl_title_episode).toString());
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
                                rcv_.setLayoutManager(gridLayoutManager);
                                rcv_.setAdapter(adapter);
                            }
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


}