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

import java.util.List;

public class CategoriesSubRecycleAdapter extends RecyclerView.Adapter<CategoriesSubRecycleAdapter.ViewHolder> {

    List<anime_class> mAnime;
    Context mContext;
    LayoutInflater inflater;

    public CategoriesSubRecycleAdapter(Context context, List<anime_class> anime){
        this.mAnime = anime;
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.horizontal_grid_layout, parent, false);
        return new CategoriesSubRecycleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    @Override
    public int getItemCount() {
        return mAnime.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView thumbnail;
        CardView crv_;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.lbl_anime_title);
            thumbnail = itemView.findViewById(R.id.img_anime_cover);
            crv_ = itemView.findViewById(R.id.crv_anime);
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
