package com.alfraza.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfraza.app.R;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.models.Video;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    private ArrayList<Video> videos;
    private Context ctx;


    public VideoAdapter(Context context, ArrayList<Video> videos) {
        this.ctx = context;
        this.videos = videos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Video video = videos.get(position);
        String image = video.getThumb();
        String title = video.getName();
        holder.tv_title.setText(title);
        if (image != null) {
            ITUtilities.loadImg(ctx)
                    .load(image)
                    .fit()
                    .centerCrop()
                    .into(holder.imgicon);
        }
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    private void IntentYoutube(String videourl) {
        Intent yintent = new Intent(Intent.ACTION_VIEW, Uri.parse(videourl));
        ctx.startActivity(yintent);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgicon;
        private TextView tv_title;


        MyViewHolder(View view) {
            super(view);
            imgicon = view.findViewById(R.id.imv_img);
            tv_title = view.findViewById(R.id.tv_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (videos.get(position).getVideourl() != null)
                IntentYoutube(videos.get(position).getVideourl());

        }
    }


}