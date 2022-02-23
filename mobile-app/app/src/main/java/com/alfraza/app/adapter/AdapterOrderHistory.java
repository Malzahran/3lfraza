package com.alfraza.app.adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alfraza.app.R;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.models.Order;
import com.alfraza.app.helpers.utilities.Tools;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.List;


public class AdapterOrderHistory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Order> items;
    private Session session;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Order obj);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView code;
        public TextView date;
        public TextView price;
        public TextView status;
        public TextView refuse;
        public RatingBar ratingBar;
        public AppCompatButton btn_rate;
        public MaterialRippleLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            code = v.findViewById(R.id.code);
            date = v.findViewById(R.id.date);
            price = v.findViewById(R.id.price);
            status = v.findViewById(R.id.status);
            refuse = v.findViewById(R.id.refuse);
            ratingBar = v.findViewById(R.id.ratingBar);
            btn_rate = v.findViewById(R.id.btn_rate);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    public AdapterOrderHistory(AppCompatActivity activity, List<Order> items) {
        this.session = new Session(activity);
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            final ViewHolder vItem = (ViewHolder) holder;
            final Order c = items.get(position);
            vItem.code.setText(c.code);
            vItem.price.setText(c.total_fees);
            vItem.status.setText(c.status);
            vItem.date.setText(c.time);
            if (c.refuse != null && !c.refuse.isEmpty()) {
                vItem.refuse.setText(c.refuse);
                vItem.refuse.setVisibility(View.VISIBLE);
            } else vItem.refuse.setVisibility(View.GONE);
            if (c.rating != 0) {
                vItem.ratingBar.setRating(c.rating);
                vItem.ratingBar.setVisibility(View.VISIBLE);
            } else vItem.ratingBar.setVisibility(View.GONE);
            if (c.a_rating != 0) {
                vItem.btn_rate.setVisibility(View.VISIBLE);
                vItem.btn_rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        session.showRateDialog(c.id, 1, vItem.btn_rate);
                    }
                });
            } else vItem.btn_rate.setVisibility(View.GONE);
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

    public List<Order> getItem() {
        return items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Order> items) {
        this.items = items;
        notifyDataSetChanged();
    }


}