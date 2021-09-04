package com.animewatcher.animewatcher;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class HomeRecycleAdapter extends RecyclerView.Adapter<HomeRecycleAdapter.ViewHolder>  {

    List<episode_class> mEpisodes;
    Context mContext;
    LayoutInflater inflater;
    String mEp;

    public HomeRecycleAdapter(Context context, List<episode_class> episodes, String ep){
        this.mEpisodes = episodes;
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mEp = ep;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.home_episode_rcv_item, parent, false);
        return new HomeRecycleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.epNumber.setText(capitalizeLetters(mEpisodes.get(position).getmNameEN()));
        holder.title.setText(capitalizeLetters(mEpisodes.get(position).getmAnime()) + "\n" + mEp + " " + mEpisodes.get(position).getmEpisodeNumber());
        Glide.with(mContext).load(mEpisodes.get(position).getmThumbnail()).into(holder.thumbnail);

        holder.crv_.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, video_player.class);
                        i.putExtra("mID", mEpisodes.get(position).getmID());
                        i.putExtra("mAnime", mEpisodes.get(position).getmAnime());
                        i.putExtra("mEpisodeNumber", mEpisodes.get(position).getmEpisodeNumber());
                        i.putExtra("mNameEN", mEpisodes.get(position).getmNameEN());
                        i.putExtra("mVideoFileLink", mEpisodes.get(position).getmVideoFileLink());
                        i.putExtra("isVCDN", mEpisodes.get(position).isMisVCDN());
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
        TextView epNumber, title;
        ImageView thumbnail;
        CardView crv_;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.lbl_home_episode_number);
            epNumber = itemView.findViewById(R.id.lbl_home_episode_title);
            thumbnail = itemView.findViewById(R.id.img_home_episode_cover);
            crv_ = itemView.findViewById(R.id.crv_home_episode);
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
