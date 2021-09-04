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

import com.android.volley.Response;
import com.bumptech.glide.Glide;

import java.util.List;

public class SearchRecycleAdapter extends RecyclerView.Adapter<SearchRecycleAdapter.ViewHolder> {

    Context mContext;
    List<anime_class> mAnime;
    LayoutInflater inflater;

    public SearchRecycleAdapter(Context context, List<anime_class> anime){
        this.mAnime = anime;
        this.inflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @NonNull
    @Override
    public SearchRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_recycle_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchRecycleAdapter.ViewHolder holder, int pos) {
            holder.titleEN.setText(capitalizeLetters(mAnime.get(pos).getmNameEN()));

            if(!mAnime.get(pos).getmNameJP().equals("null"))
                holder.titleJP.setText(capitalizeLetters(mAnime.get(pos).getmNameJP()));
            else
                holder.titleJP.setText("");

            if(mAnime.get(pos).getmDescription().length() < 93)
                holder.desc.setText(mAnime.get(pos).getmDescription());
            else
                holder.desc.setText(mAnime.get(pos).getmDescription().substring(0, 93) + "…");

            Glide.with(mContext).load(mAnime.get(pos).getmThumbnail()).into(holder.thumbnail);

            holder.crv_.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(mContext, anime_page.class);
                            i.putExtra("mNameEN", mAnime.get(pos).getmNameEN());
                            i.putExtra("mThumbnail", mAnime.get(pos).getmThumbnail());
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        TextView titleEN, titleJP, desc;
        CardView crv_;
        ImageView thumbnail;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            titleEN = itemView.findViewById(R.id.search_lbl_anime_titleEN);
            titleJP = itemView.findViewById(R.id.search_lbl_anime_titleJP);
            desc = itemView.findViewById(R.id.search_lbl_description);
            thumbnail = itemView.findViewById(R.id.search_img_anime_cover);
            crv_ = itemView.findViewById(R.id.search_parent_card);
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
