package com.animewatcher.animewatcher;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.CaptionStyleCompat;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.List;
import java.util.Objects;

public class video_player extends AppCompatActivity {

    PlayerView playerView;
    ProgressBar progressBar;
    SimpleExoPlayer player;
    Uri videoUri;

    ImageView img_fullscreen, img_back;
    Button all_episodes;
    RelativeLayout player_page, next_episode, video_container;

    ImageView img_NextThumb;
    TextView lbl_anime, lbl_epTitle, lbl_nextEpNumber, lbl_nextEpName, lbl_nextEp;

    RequestQueue requestQueue;
    String URL;
    boolean last = false;

    int video_container_height;

    boolean isFullscreen = false;

    String AnimeName, NameEN, VideoLink;
    boolean isVDCN;
    int EpisodeNumber, EpisodeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        //Get intent info
        EpisodeID = getIntent().getIntExtra("mID", -1);
        AnimeName = getIntent().getStringExtra("mAnime");
        EpisodeNumber = getIntent().getIntExtra("mEpisodeNumber", -1);
        NameEN = getIntent().getStringExtra("mNameEN");
        VideoLink = getIntent().getStringExtra("mVideoFileLink");
        isVDCN = getIntent().getBooleanExtra("isVCDN", false);

        lbl_nextEp = findViewById(R.id.lbl_video_player_next);
        lbl_nextEpName = findViewById(R.id.video_player_episode_name_next);
        lbl_nextEpNumber = findViewById(R.id.video_player_episode_number_next);
        lbl_epTitle = findViewById(R.id.lbl_video_player_anime_EpTitle);
        lbl_anime = findViewById(R.id.lbl_video_player_anime_name);

        img_back = findViewById(R.id.video_player_back);
        img_NextThumb = findViewById(R.id.video_player_episode_cover_next);
        img_fullscreen = findViewById(R.id.video_fullscreen);

        video_container = findViewById(R.id.video_container);
        next_episode = findViewById(R.id.video_player_next);
        player_page = findViewById(R.id.page_container);

        all_episodes = findViewById(R.id.btn_video_player_allEpisodes);

        playerView = findViewById(R.id.video_player);
        progressBar = findViewById(R.id.video_progress_bar);

        //Get video container height from layout
        RelativeLayout.LayoutParams rel = (RelativeLayout.LayoutParams) video_container.getLayoutParams();
        video_container_height = rel.height;

        //Add a view to the episode
        URL = getText(R.string.website_link) + "api/episode-add-view";
        String data = "{"+
                "\"mID\":" + "\"" + EpisodeID + "\"" +
                "}";

        AddView(data);

        //Get Next Episode Info
        URL = getText(R.string.website_link) + "api/get-episode-next";
        data = "{"+
                "\"mID\":" + "\"" + EpisodeID + "\"" +
                "}";

        SubmitNext(data);

        //Load Activity with data
        lbl_anime.setText(capitalizeLetters(AnimeName));
        lbl_epTitle.setText(getText(R.string.lbl_title_episode) + " " + String.valueOf(EpisodeNumber) + " - " + capitalizeLetters(NameEN));

        //Start the player
        if(isVDCN)
        {
            //Start the player
            String last = VideoLink.substring(VideoLink.lastIndexOf("/") + 1);

            URL = "https://vcdn2.space/api/source/" + last;
            data = "r=&d=vcdn2.space";

            SubmitVCDN(data);
        }
        else
        {
            //Start the player
            //Set default audio track to japanese
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(getApplicationContext());
            trackSelector.setParameters(
                    trackSelector
                            .buildUponParameters()
                            .setMaxVideoSizeSd()
                            .setPreferredAudioLanguage("jap"));

            player = new SimpleExoPlayer.Builder(getApplicationContext()).setTrackSelector(trackSelector).build();
            playerView.setPlayer(player);

            videoUri = Uri.parse(VideoLink);

            MediaItem mediaItem = new MediaItem.Builder().setUri(videoUri).build();

            player.setMediaItem(mediaItem);

            playerView.setKeepScreenOn(true);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.CONTENT_TYPE_MOVIE)
                    .build();

            player.setAudioAttributes(audioAttributes, true);

            player.prepare();
            player.setPlayWhenReady(true);

            player.addListener(
                    new Player.Listener() {
                        @Override
                        public void onPlaybackStateChanged(int state) {
                            //Check if the video is loading
                            if (state == Player.STATE_BUFFERING) {
                                progressBar.setVisibility(View.VISIBLE);
                            } else {
                                if (state == Player.STATE_READY) {
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
            );
        }


        img_fullscreen.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                        if (isFullscreen) {

                            img_fullscreen.setImageResource(R.drawable.ic_baseline_fullscreen_24_white);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                getWindow().setDecorFitsSystemWindows(true);

                                if (getWindow().getInsetsController() != null) {
                                    getWindow().getInsetsController().show(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                                }
                            } else {
                                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                            }

                            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                                case Configuration.UI_MODE_NIGHT_YES:
                                    player_page.setBackgroundColor(getColor(R.color.darkTheme_bc));
                                    break;
                                case Configuration.UI_MODE_NIGHT_NO:
                                    player_page.setBackgroundColor(getColor(R.color.white));
                                    break;
                                default:
                                    player_page.setBackgroundColor(getColor(R.color.white));
                                    break;
                            }

                            RelativeLayout.LayoutParams rel = (RelativeLayout.LayoutParams) video_container.getLayoutParams();
                            rel.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            rel.height = video_container_height;
                            video_container.setLayoutParams(rel);

                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            isFullscreen = false;
                        } else {
                            img_fullscreen.setImageResource(R.drawable.ic_baseline_fullscreen_exit_24_white);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                getWindow().setDecorFitsSystemWindows(false);

                                if (getWindow().getInsetsController() != null) {
                                    getWindow().getInsetsController().hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                                    getWindow().getInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                                }
                            } else {
                                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                            }

                            player_page.setBackgroundColor(getColor(R.color.black));
                            RelativeLayout.LayoutParams rel = (RelativeLayout.LayoutParams) video_container.getLayoutParams();
                            rel.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            rel.height = ViewGroup.LayoutParams.MATCH_PARENT;
                            video_container.setLayoutParams(rel);

                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            isFullscreen = true;
                        }
                    }
                }
        );

        img_back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.image_click));
                        finish();
                    }
                }
        );

        all_episodes.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        player.setPlayWhenReady(false);
                        player.getPlaybackState();

                        URL = getText(R.string.website_link) + "api/get-anime-thumbnail";
                        String data = "{"+
                                "\"mNameEN\":" + "\"" + AnimeName + "\"" +
                                "}";

                        Submit(data);
                    }
                }
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);

        player.getPlaybackState();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        player.release();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if(!isFullscreen)
            {
                img_fullscreen.setImageResource(R.drawable.ic_baseline_fullscreen_exit_24_white);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    getWindow().setDecorFitsSystemWindows(false);

                    if (getWindow().getInsetsController() != null) {
                        getWindow().getInsetsController().hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                        getWindow().getInsetsController().setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                    }
                } else {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                }

                player_page.setBackgroundColor(getColor(R.color.black));
                RelativeLayout.LayoutParams rel = (RelativeLayout.LayoutParams) video_container.getLayoutParams();
                rel.width = ViewGroup.LayoutParams.MATCH_PARENT;
                rel.height = ViewGroup.LayoutParams.MATCH_PARENT;
                video_container.setLayoutParams(rel);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                isFullscreen = true;
            }
        }
    }

    private void SubmitVCDN(String data)
    {
        final String savedata= data;

        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray objArray = obj.getJSONArray("data");

                    String vcdn_video_url = "";
                    for(int i = 0; i < objArray.length(); i++)
                    {
                        JSONObject obj2 = (JSONObject) objArray.get(i);
                        vcdn_video_url = obj2.getString("file");
                    }

                    //Start the player
                    //Set default audio track to japanese
                    DefaultTrackSelector trackSelector = new DefaultTrackSelector(getApplicationContext());
                    trackSelector.setParameters(
                            trackSelector
                                    .buildUponParameters()
                                    .setMaxVideoSizeSd()
                                    .setPreferredAudioLanguage("jap"));

                    player = new SimpleExoPlayer.Builder(getApplicationContext()).setTrackSelector(trackSelector).build();
                    playerView.setPlayer(player);

                    videoUri = Uri.parse(vcdn_video_url);

                    MediaItem mediaItem = new MediaItem.Builder().setUri(videoUri).build();

                    player.setMediaItem(mediaItem);

                    playerView.setKeepScreenOn(true);

                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(C.USAGE_MEDIA)
                            .setContentType(C.CONTENT_TYPE_MOVIE)
                            .build();

                    player.setAudioAttributes(audioAttributes, true);

                    player.prepare();
                    player.setPlayWhenReady(true);

                    player.addListener(
                            new Player.Listener() {
                                @Override
                                public void onPlaybackStateChanged(int state) {
                                    //Check if the video is loading
                                    if (state == Player.STATE_BUFFERING) {
                                        progressBar.setVisibility(View.VISIBLE);
                                    } else {
                                        if (state == Player.STATE_READY) {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                    );

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

    private void SubmitNext(String data)
    {
        final String savedata= data;

        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);

                    lbl_nextEp.setVisibility(View.VISIBLE);
                    next_episode.setVisibility(View.VISIBLE);

                    Glide.with(getApplicationContext()).load(obj.getString("mThumbnail")).into(img_NextThumb);
                    lbl_nextEpNumber.setText(getText(R.string.lbl_title_episode) + " " + String.valueOf(obj.getInt("mEpisodeNumber")));
                    lbl_nextEpName.setText(capitalizeLetters(obj.getString("mNameEN")));

                    next_episode.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(getApplicationContext(), video_player.class);
                                    try {
                                        i.putExtra("mID", obj.getInt("mID"));
                                        i.putExtra("mAnime", obj.getString("mAnime"));
                                        i.putExtra("mEpisodeNumber", obj.getInt("mEpisodeNumber"));
                                        i.putExtra("mNameEN", obj.getString("mNameEN"));
                                        i.putExtra("mVideoFileLink", obj.getString("mVideoFileLink"));
                                        i.putExtra("isVCDN", obj.getBoolean("mVCDN"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(i);
                                    finish();
                                }
                            }
                    );



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
                    lbl_nextEp.setVisibility(View.GONE);
                    next_episode.setVisibility(View.GONE);
                    last = true;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), R.string.login_communication_error, Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
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

    private void Submit(String data)
    {
        final String savedata= data;

        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);

                    Intent i = new Intent(getApplicationContext(), anime_page.class);
                    i.putExtra("mNameEN", AnimeName);
                    i.putExtra("mThumbnail", obj.getString("mThumbnail"));
                    startActivity(i);

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

    private void AddView(String data)
    {
        final String savedata= data;

        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getApplicationContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);

                    if(!obj.getString("Status").equals("Success"))
                    {
                        Toast.makeText(getApplicationContext(), R.string.episode_view_error, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), R.string.episode_view_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), R.string.episode_view_error, Toast.LENGTH_LONG).show();
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