package com.alfraza.app.adapter;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfraza.app.R;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.models.Category;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.List;


public class AdapterCategory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private List<Category> items;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Category obj);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        TextView brief;
        public ImageView image;
        RelativeLayout lyt_color;
        LinearLayout title_lyt;
        public MaterialRippleLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            brief = v.findViewById(R.id.brief);
            image = v.findViewById(R.id.image);
            lyt_color = v.findViewById(R.id.lyt_color);
            title_lyt = v.findViewById(R.id.title_lyt);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    public AdapterCategory(Context ctx, List<Category> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;
            final Category c = items.get(position);
            if (c.showTitle() != 0) {
                vItem.name.setText(c.getName());
                if (c.getBrief() != null && !c.getBrief().isEmpty()) {
                    vItem.brief.setText(c.getBrief());
                    vItem.brief.setVisibility(View.VISIBLE);
                } else vItem.brief.setVisibility(View.GONE);
                vItem.title_lyt.setVisibility(View.VISIBLE);
            } else vItem.title_lyt.setVisibility(View.GONE);
            vItem.lyt_color.setBackgroundColor(Color.parseColor((c.getColor() != null && !c.getColor().isEmpty() ? c.getColor() : "#4db849")));
            ITUtilities.loadImg(ctx)
                    .load(c.getImg())
                    .error(R.drawable.ic_error)
                    .into(vItem.image);
            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, c);
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Category> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}