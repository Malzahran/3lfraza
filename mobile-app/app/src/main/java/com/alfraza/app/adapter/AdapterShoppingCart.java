package com.alfraza.app.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alfraza.app.R;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.models.CartItems;
import com.alfraza.app.models.FeatureItem;
import com.alfraza.app.helpers.utilities.Tools;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.List;
import java.util.Locale;


public class AdapterShoppingCart extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private List<CartItems> items;
    private Boolean is_cart;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, CartItems obj);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView amount;
        public TextView price;
        public ImageView image;
        public RelativeLayout lyt_image;
        public MaterialRippleLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            amount = v.findViewById(R.id.amount);
            price = v.findViewById(R.id.price);
            image = v.findViewById(R.id.image);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            lyt_image = v.findViewById(R.id.lyt_image);
        }
    }

    public AdapterShoppingCart(Context ctx, boolean is_cart, List<CartItems> items) {
        this.ctx = ctx;
        this.items = items;
        this.is_cart = is_cart;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        ViewGroup nullParent = null;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_cart, nullParent, false);
        vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;
            final CartItems c = items.get(position);
            StringBuilder item_name = new StringBuilder(c.product_name);
            if (c.features != null && c.features.size() > 0) {
                List<FeatureItem> featuresData = c.features;
                for (FeatureItem feature : featuresData) {
                    item_name.append("<br>").append(feature.name);
                }
            }
            vItem.title.setText(ITUtilities.fromHtml(item_name));
            vItem.price.setText(Tools.getFormattedPrice(c.price_item, c.currency));
            vItem.amount.setText(String.format(Locale.ENGLISH, "%d %s", c.amount, ctx.getString(R.string.items)));
            ITUtilities.loadImg(ctx)
                    .load(c.image)
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

            if (is_cart) {
                vItem.lyt_image.setVisibility(View.VISIBLE);
                vItem.lyt_parent.setEnabled(true);
            } else {
                vItem.lyt_image.setVisibility(View.GONE);
                vItem.lyt_parent.setEnabled(false);
            }
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<CartItems> getItems() {
        return items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<CartItems> items) {
        this.items = items;
        notifyDataSetChanged();
    }


}