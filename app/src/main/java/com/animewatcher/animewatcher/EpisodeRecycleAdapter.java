package com.animewatcher.animewatcher;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EpisodeRecycleAdapter extends RecyclerView.Adapter<EpisodeRecycleAdapter.ViewHolder> {

    Context mContext;
    List<episode_class> mEpisodes;
    LayoutInflater inflater;
    String EpisodeString;

    public EpisodeRecycleAdapter(Context context, List<episode_class> Episodes, String epString){
        this.mEpisodes = Episodes;
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.EpisodeString = epString;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.anime_page_recycle_layout, parent, false);
        return new EpisodeRecycleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        holder.title.setText(EpisodeString + " " + String.valueOf(mEpisodes.get(pos).getmEpisodeNumber()));
        holder.epName.setText(capitalizeLetters(mEpisodes.get(pos).getmNameEN()));
        Glide.with(mContext).load(mEpisodes.get(pos).getmThumbnail()).into(holder.thumbnail);

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
    }

    @Override
    public int getItemCount() {
        return mEpisodes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, epName;
        ImageView thumbnail;
        RelativeLayout rl;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.anime_page_episode_number);
            epName = itemView.findViewById(R.id.anime_page_episode_name);
            thumbnail = itemView.findViewById(R.id.anime_page_episode_cover);
            rl = itemView.findViewById(R.id.anime_page_episode);
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
