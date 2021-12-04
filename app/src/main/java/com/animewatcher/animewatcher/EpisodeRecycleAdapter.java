package com.animewatcher.animewatcher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class EpisodeRecycleAdapter extends RecyclerView.Adapter<EpisodeRecycleAdapter.ViewHolder> {

    static final int PERMISSION_REQUEST_CODE = 100;
    boolean file_exists = false;

    RequestQueue requestQueue;
    String URL;

    BroadcastReceiver onComplete;

    Context mContext;
    List<episode_class> mEpisodes;
    LayoutInflater inflater;
    String EpisodeString, mExtra, mNoImageURL;

    public EpisodeRecycleAdapter(Context context, List<episode_class> Episodes, String epString, String extra, String noImageURL){
        this.mEpisodes = Episodes;
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.EpisodeString = epString;
        this.mExtra = extra;
        this.mNoImageURL = noImageURL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.anime_page_recycle_layout, parent, false);
        return new EpisodeRecycleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int pos) {

        if(mEpisodes.get(pos).ismIsSpecial())
        {
            holder.title.setText(mExtra + " " + String.valueOf(mEpisodes.get(pos).getmEpisodeNumber()));
        }
        else
        {
            holder.title.setText(EpisodeString + " " + String.valueOf(mEpisodes.get(pos).getmEpisodeNumber()));
        }

        if(mEpisodes.get(pos).getmNameEN().length() > 65){
            holder.epName.setText(capitalizeLetters(mEpisodes.get(pos).getmNameEN().substring(0, 64)) + "...");
        }
        else{
            holder.epName.setText(capitalizeLetters(mEpisodes.get(pos).getmNameEN()));
        }


        if(mEpisodes.get(pos).getmThumbnail().length() > 0)
            Glide.with(mContext).load(mEpisodes.get(pos).getmThumbnail()).transition(DrawableTransitionOptions.withCrossFade()).into(holder.thumbnail);
        else
            Glide.with(mContext).load(mNoImageURL).transition(DrawableTransitionOptions.withCrossFade()).into(holder.thumbnail);

        //Check if the episode is downloading
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterByStatus(DownloadManager.STATUS_PAUSED|DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_PENDING);
        DownloadManager dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor c = dm.query(q);
        boolean is_downloading = false;
        if((c != null) && (c.getCount() > 0)){
            c.moveToFirst();
            do{
                String[] str = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE)).split(" - ");
                if(str[0].equals(String.valueOf(mEpisodes.get(pos).getmID()))){
                    file_exists = true;
                    is_downloading = true;
                    holder.download_btn.setVisibility(View.INVISIBLE);
                    holder.pgb_downloading.setVisibility(View.VISIBLE);
                    break;
                }
            }while (c.moveToNext());
            c.close();
        }

        if(!is_downloading){
            File file = new File( Environment.getExternalStorageDirectory(), Environment.DIRECTORY_MOVIES + "/AnimeWatcher/" + mEpisodes.get(pos).getmID() + ".mp4");
            if(file.exists()){
                holder.pgb_downloading.setVisibility(View.GONE);
                holder.download_btn.setVisibility(View.VISIBLE);
                holder.download_btn.setImageResource(R.drawable.ic_outline_cloud_done_24);
                file_exists = true;
            }
            else{
                //Remove from DB
                SQLiteDatabase downloadDB = mContext.openOrCreateDatabase("downloadDB", Context.MODE_PRIVATE, null);
                downloadDB.execSQL("CREATE TABLE IF NOT EXISTS episode(mID Integer primary key, mEpisodeNumber Integer, " +
                        "mNameEN Text, mThumbnail Text, mAnime_id Text, mIsSpecial Boolean)");

                downloadDB.execSQL("delete from episode where mID='" + mEpisodes.get(pos).getmID() + "'");
                downloadDB.close();
            }
        }

        holder.rl.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, video_player.class);
                        i.putExtra("mID", mEpisodes.get(pos).getmID());
                        i.putExtra("mAnime", mEpisodes.get(pos).getmAnime());
                        i.putExtra("mEpisodeNumber", mEpisodes.get(pos).getmEpisodeNumber());
                        i.putExtra("mNameEN", mEpisodes.get(pos).getmNameEN());
                        i.putExtra("mVideoFileLink", mEpisodes.get(pos).getmVideoFileLink());
                        i.putExtra("isVCDN", mEpisodes.get(pos).isMisVCDN());
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(i);
                    }
                }
        );

        holder.download_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_click));

                        if(holder.download_btn.getVisibility() == View.VISIBLE){
                            File file = new File( Environment.getExternalStorageDirectory(), Environment.DIRECTORY_MOVIES + "/AnimeWatcher/" + mEpisodes.get(pos).getmID() + ".mp4");
                            if(!file.exists()) {

                                boolean allowed = true;

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    if (!Environment.isExternalStorageManager()) {
                                        allowed = false;
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                        mContext.startActivity(intent);
                                    }
                                }

                                if (mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                    allowed = false;
                                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                                    ActivityCompat.requestPermissions((Activity) mContext, permissions, PERMISSION_REQUEST_CODE);
                                } else {
                                    if (allowed = true) {
                                        //Get Video Description
                                        String desc;

                                        //Set Listener
                                        onComplete = new BroadcastReceiver() {
                                            public void onReceive(Context ctxt, Intent intent) {
                                                Bundle extras = intent.getExtras();
                                                DownloadManager.Query q = new DownloadManager.Query();
                                                long downloadID = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
                                                q.setFilterById(downloadID);
                                                DownloadManager dm = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                                                Cursor c = dm.query(q);
                                                if (c.moveToFirst()) {
                                                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                                                    holder.pgb_downloading.setVisibility(View.GONE);
                                                    holder.download_btn.setVisibility(View.VISIBLE);
                                                    if (status == 8) {
                                                        holder.download_btn.setImageResource(R.drawable.ic_outline_cloud_done_24);
                                                        file_exists = true;
                                                    } else {
                                                        holder.download_btn.setImageResource(R.drawable.ic_baseline_arrow_circle_down_24);
                                                        file_exists = false;
                                                    }
                                                }
                                                else{ /* If the download was canceled */
                                                    holder.pgb_downloading.setVisibility(View.GONE);
                                                    holder.download_btn.setVisibility(View.VISIBLE);
                                                    holder.download_btn.setImageResource(R.drawable.ic_baseline_arrow_circle_down_24);
                                                    file_exists = false;

                                                    //Remove from DB
                                                    SQLiteDatabase downloadDB = mContext.openOrCreateDatabase("downloadDB", Context.MODE_PRIVATE, null);
                                                    downloadDB.execSQL("CREATE TABLE IF NOT EXISTS episode(mID Integer primary key, mEpisodeNumber Integer, " +
                                                            "mNameEN Text, mThumbnail Text, mAnime_id Text, mIsSpecial Boolean)");

                                                    String[] str = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE)).split(" - ");
                                                    downloadDB.execSQL("delete from episode where mID='" + str[0] + "'");
                                                    downloadDB.close();
                                                }
                                                c.close();

                                            }
                                        };

                                        if (mEpisodes.get(pos).ismIsSpecial()) {
                                            desc = mEpisodes.get(pos).getmAnime() + " " + mContext.getText(R.string.lbl_title_extra) + ": " + mEpisodes.get(pos).getmEpisodeNumber();
                                        } else {
                                            desc = mEpisodes.get(pos).getmAnime() + " " + mContext.getText(R.string.lbl_title_episode) + ": " + mEpisodes.get(pos).getmEpisodeNumber();
                                        }

                                        if (mEpisodes.get(pos).isMisVCDN()) {
                                            //Start the player
                                            String last = mEpisodes.get(pos).getmVideoFileLink().substring(mEpisodes.get(pos).getmVideoFileLink().lastIndexOf("/") + 1);

                                            URL = "https://vcdn2.space/api/source/" + last;
                                            String data = "r=&d=vcdn2.space";

                                            SubmitVCDN(data, String.valueOf(mEpisodes.get(pos).getmID()), desc, holder, String.valueOf(mEpisodes.get(pos).getmEpisodeNumber()),
                                                    mEpisodes.get(pos).getmNameEN(), mEpisodes.get(pos).getmThumbnail(), mEpisodes.get(pos).getmAnime(), mEpisodes.get(pos).ismIsSpecial());
                                        } else {
                                            //Get Video File Extension
                                            int index = mEpisodes.get(pos).getmVideoFileLink().lastIndexOf('.');

                                            boolean res = download_episode(mEpisodes.get(pos).getmVideoFileLink(), String.valueOf(mEpisodes.get(pos).getmID()),
                                                    String.valueOf(mEpisodes.get(pos).getmEpisodeNumber()), mEpisodes.get(pos).getmNameEN(), mEpisodes.get(pos).getmThumbnail(),
                                                    mEpisodes.get(pos).getmAnime(), mEpisodes.get(pos).ismIsSpecial(), mEpisodes.get(pos).getmVideoFileLink().substring(index), desc);
                                            if (res) {
                                                holder.download_btn.setVisibility(View.INVISIBLE);
                                                holder.pgb_downloading.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else{
                            Toast.makeText(mContext, R.string.file_already_exists, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return mEpisodes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, epName;
        ImageView thumbnail, download_btn;
        ProgressBar pgb_downloading;
        RelativeLayout rl;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.anime_page_episode_number);
            epName = itemView.findViewById(R.id.anime_page_episode_name);
            thumbnail = itemView.findViewById(R.id.anime_page_episode_cover);
            rl = itemView.findViewById(R.id.anime_page_episode);
            download_btn = itemView.findViewById(R.id.episode_download_button);
            pgb_downloading = itemView.findViewById(R.id.pgb_downloading);
        }
    }

    private String capitalizeLetters(String str)
    {
        if(str.length() == 0)
            return " ";

        String[] strArray = str.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }

        return builder.toString();
    }

    private boolean download_episode(String url, String ID, String epNumber, String epName, String thumbnail, String anime, boolean is_special, String extension, String description){
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setTitle("Download");
            request.setTitle(ID + " - " + description);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            File f = new File(Environment.DIRECTORY_MOVIES + "/AnimeWatcher/");
            if(!f.exists())
                f.mkdir();

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES,"/AnimeWatcher/" + ID + " - " + description.replaceAll("[^a-zA-Z0-9_ ]", "") + extension);

            DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            mContext.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            manager.enqueue(request);

            //Add download to DB
            SQLiteDatabase downloadDB = mContext.openOrCreateDatabase("downloadDB", Context.MODE_PRIVATE, null);
            downloadDB.execSQL("CREATE TABLE IF NOT EXISTS episode(mID Integer primary key, mEpisodeNumber Integer, mNameEN Text, mThumbnail Text," +
                    "mAnime_id Text, mIsSpecial Boolean)");

            downloadDB.execSQL("Insert into episode values('" + ID + "', '" + epNumber + "', '" + epName + "', '" + thumbnail + "', '" + anime + "', '" + is_special + "')");
            downloadDB.close();
            Toast.makeText(mContext, R.string.download_started, Toast.LENGTH_SHORT).show();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, mContext.getText(R.string.dir_create_exception).toString() + " " + e.toString(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void SubmitVCDN(String data, String ID, String description, ViewHolder holder, String epNumber, String epName, String thumbnail, String anime, boolean is_special)
    {
        final String savedata= data;

        requestQueue = Volley.newRequestQueue(Objects.requireNonNull(mContext));
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

                    boolean res = download_episode(vcdn_video_url, ID, epNumber, epName, thumbnail, anime, is_special, ".mp4", description);

                    if(res){
                        holder.download_btn.setVisibility(View.INVISIBLE);
                        holder.pgb_downloading.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    Toast.makeText(mContext, R.string.login_communication_error, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

}
