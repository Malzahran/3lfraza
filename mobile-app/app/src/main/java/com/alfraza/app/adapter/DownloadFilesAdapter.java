package com.alfraza.app.adapter;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfraza.app.R;
import com.alfraza.app.ActivityView;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.models.DownFileData;

import java.util.ArrayList;

public class DownloadFilesAdapter extends RecyclerView.Adapter<DownloadFilesAdapter.MyViewHolder> {

    private ArrayList<DownFileData> downloadfiles;
    private Context mContext;
    private Activity mActivity;


    public DownloadFilesAdapter(Activity activity, ArrayList<DownFileData> files) {
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
        this.downloadfiles = files;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.download_file_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        DownFileData dwfile = downloadfiles.get(position);
        final String filename = dwfile.getFileName();
        final String fileurl = dwfile.getFileUrl();
        final String ext = dwfile.getFileExt();
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivity instanceof ActivityView) {
                    ((ActivityView) mActivity).DownloadFile(fileurl, filename, ext);
                }
            }
        });
        holder.tv_title.setText(filename);
        if (dwfile.getType() == 1) {
            ITUtilities.loadImg(mContext)
                    .load(fileurl)
                    .fit()
                    .centerCrop()
                    .into(holder.imgicon);
        } else holder.imgicon.setImageResource(R.drawable.ic_files);
    }

    @Override
    public int getItemCount() {
        return downloadfiles.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgicon;
        private TextView tv_title;
        private AppCompatButton download;

        private MyViewHolder(View view) {
            super(view);
            imgicon = view.findViewById(R.id.imv_img);
            tv_title = view.findViewById(R.id.tv_title);
            download = view.findViewById(R.id.download);
        }
    }
}