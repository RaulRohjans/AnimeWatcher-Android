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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;
import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    final int VIEW_TYPE_LOADING = 0;
    final int VIEW_TYPE_ITEM = 1;

    List<anime_class> mAnime;
    Context mContext;
    LayoutInflater inflater;

    public RecycleAdapter(Context context, List<anime_class> anime){
        this.mAnime = anime;
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM){
            View view = inflater.inflate(R.layout.grid_layout, parent, false);
            return new ViewHolder(view, false);
        }
        else{
            View view = inflater.inflate(R.layout.item_loading, parent, false);
            return new ViewHolder(view, true);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleAdapter.ViewHolder holder, int position) {
        if(mAnime.get(position) != null){
            holder.title.setText(capitalizeLetters(mAnime.get(position).getmNameEN()));
            Glide.with(mContext).load(mAnime.get(position).getmThumbnail()).transition(DrawableTransitionOptions.withCrossFade()).into(holder.thumbnail);

            holder.crv_.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(mContext, anime_page.class);
                            i.putExtra("mNameEN", mAnime.get(position).getmNameEN());
                            i.putExtra("mThumbnail", mAnime.get(position).getmThumbnail());
                            mContext.startActivity(i);
                        }
                    }
            );
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mAnime.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mAnime.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView thumbnail;
        CardView crv_;

        public ViewHolder(@NonNull View itemView, boolean lastItem){
            super(itemView);
            if(!lastItem){
                title = itemView.findViewById(R.id.lbl_anime_title);
                thumbnail = itemView.findViewById(R.id.img_anime_cover);
                crv_ = itemView.findViewById(R.id.crv_anime);
            }
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
