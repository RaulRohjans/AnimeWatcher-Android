package com.animewatcher.animewatcher;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link episodes_extra#newInstance} factory method to
 * create an instance of this fragment.
 */
public class episodes_extra extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public episodes_extra() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment episodes_extra.
     */
    // TODO: Rename and change types and number of parameters
    public static episodes_extra newInstance(String param1, String param2) {
        episodes_extra fragment = new episodes_extra();
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

    RecyclerView rcv;
    EpisodeRecycleAdapter adapter;
    List<episode_class> episodes;

    String URL, AnimeName;

    TextView lbl_no_extra;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_episodes_extra, container, false);

        pref = Objects.requireNonNull(getActivity()).getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        if(pref.getString("temporaryAnimeName", null) != null)
            AnimeName = pref.getString("temporaryAnimeName", null);
        else
            Toast.makeText(getActivity(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();

        rcv = v.findViewById(R.id.anime_extra_episode_page_rcv);
        lbl_no_extra = v.findViewById(R.id.lbl_no_extra_episodes);

        episodes = new ArrayList<>();

        //Check if there are special only
        URL = getText(R.string.website_link) + "api/check-special-only";
        String data = "{"+
                "\"mNameEN\":" + "\"" + AnimeName + "\"" +
                "}";

        CheckSpecialOny(data);

        return v;
    }

    private void Submit(String data)
    {
        final String savedata = data;

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    episodes.clear();
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject episode = jsonArray.getJSONObject(i);

                        if(episode.getBoolean("mIsSpecial")){
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
                            e.setmIsSpecial(episode.getBoolean("mIsSpecial"));
                            episodes.add(e);
                        }
                    }

                    if(!episodes.isEmpty())
                        lbl_no_extra.setVisibility(View.GONE);
                    else
                        lbl_no_extra.setVisibility(View.VISIBLE);

                    adapter = new EpisodeRecycleAdapter(getActivity(), episodes, getText(R.string.lbl_title_episode).toString(), getText(R.string.lbl_title_extra).toString(), getText(R.string.no_image_available_url).toString());
                    rcv.setLayoutManager(new LinearLayoutManager(getActivity()));
                    rcv.setAdapter(adapter);

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), R.string.login_communication_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
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

    private void CheckSpecialOny(String data)
    {
        final String savedata = data;

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objres = new JSONObject(response);

                    if(!objres.getBoolean("Status"))
                    {
                        URL = getText(R.string.website_link) + "api/get-episodes";
                        String data = "{"+
                                "\"mNameEN\":" + "\"" + AnimeName + "\"" +
                                "}";

                        Submit(data);
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), R.string.login_communication_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
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