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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DownloadsMainRecycleAdapter extends RecyclerView.Adapter<DownloadsMainRecycleAdapter.ViewHolder> {

    List<String> downloaded_anime;

    Context mContext;
    LayoutInflater inflater;

    public DownloadsMainRecycleAdapter(Context c, List<String> downloaded_anime)
    {
        this.mContext = c;
        this.inflater = LayoutInflater.from(c);
    }

    @NonNull
    @Override
    public DownloadsMainRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.downloads_main_recycler_item, parent, false);
        return new DownloadsMainRecycleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return downloaded_anime.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView lbl_tite;
        ImageView goto_anime;
        RecyclerView rcv_episodes;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            lbl_tite = itemView.findViewById(R.id.lbl_download_anime_title);
            goto_anime = itemView.findViewById(R.id.img_goto_download_anime);
            rcv_episodes = itemView.findViewById(R.id.crv_download_episodes);
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
