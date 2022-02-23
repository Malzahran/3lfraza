package com.alfraza.app.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfraza.app.R;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.models.UpFile;

import java.io.File;
import java.util.ArrayList;

public class UploadedFilesAdapter extends RecyclerView.Adapter<UploadedFilesAdapter.MyViewHolder> {

    private ArrayList<UpFile> upimages;
    private Context ctx;


    public UploadedFilesAdapter(Context context, ArrayList<UpFile> upimages) {
        this.ctx = context;
        this.upimages = upimages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.uploaded_file_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UpFile upfile = upimages.get(position);
        String image = upfile.getFile();
        /*Uri imageUri = Uri.parse(image);
        holder.imgicon.setImageURI(imageUri);*/
        File f = new File(image);
        String filename = f.getName();
        holder.tv_title.setText(filename);
        if (upfile.getType() == 1) {
            float SizeDp = 40f;
            int SizePx = ITUtilities.DpToPx(ctx, SizeDp);
            ITUtilities.loadImg(ctx)
                    .load(f)
                    .resize(SizePx, SizePx)
                    .centerCrop()
                    .into(holder.imgicon);
        } else {
            holder.imgicon.setImageResource(R.drawable.ic_home);
        }
    }

    @Override
    public int getItemCount() {
        return upimages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgicon;
        public TextView tv_title;


        MyViewHolder(View view) {
            super(view);
            imgicon = view.findViewById(R.id.imv_img);
            tv_title = view.findViewById(R.id.tv_title);
        }
    }
}