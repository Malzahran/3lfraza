package com.alfraza.app.adapter;

import android.content.Context;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alfraza.app.R;
import com.alfraza.app.data.Constant;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.models.ItemData;
import com.alfraza.app.helpers.utilities.Tools;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

public class AdapterProduct extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;

    private List<ItemData> items;

    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, ItemData obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterProduct(Context context, RecyclerView view, List<ItemData> items) {
        this.items = items;
        ctx = context;
        lastItemViewDetector(view);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView status;
        public TextView description;
        public TextView price;
        public TextView price_strike;
        public ImageView image;
        public MaterialRippleLayout lyt_parent;

        OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            status = v.findViewById(R.id.status);
            description = v.findViewById(R.id.description);
            price = v.findViewById(R.id.price);
            price_strike = v.findViewById(R.id.price_strike);
            image = v.findViewById(R.id.image);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progress_loading);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final ItemData p = items.get(position);
            OriginalViewHolder vItem = (OriginalViewHolder) holder;
            vItem.name.setText(p.getTitle());
            if (p.getDesc() != null && !p.getDesc().isEmpty())
                vItem.description.setText(p.getDesc());

            if (p.getStatus() != 0 && p.getStatus() != 1) {
                vItem.status.setVisibility(View.VISIBLE);
                if (p.getStatus() == 2) {
                    vItem.status.setText(ctx.getString(R.string.out_of_stock));
                } else if (p.getStatus() == 3) {
                    vItem.status.setText(ctx.getString(R.string.suspend));
                }
            } else vItem.status.setVisibility(View.GONE);

            // handle discount view
            if (p.getDscPrc() > 0) {
                vItem.price.setText(Tools.getFormattedPrice((double) p.getPrc(), p.getMinfo2()));
                vItem.price_strike.setText(Tools.getFormattedPrice((double) p.getDscPrc(), p.getMinfo2()));
                vItem.price_strike.setPaintFlags(vItem.price_strike.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                vItem.price_strike.setVisibility(View.VISIBLE);
            } else {
                vItem.price.setText(Tools.getFormattedPrice((double) p.getPrc(), p.getMinfo2()));
                vItem.price_strike.setVisibility(View.GONE);
            }
            ITUtilities.loadImg(ctx)
                    .load(p.getImg())
                    .error(R.drawable.ic_error)
                    .into(vItem.image);
            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, p, holder.getAdapterPosition());
                    }
                }
            });
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        return this.items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void insertData(List<ItemData> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.items.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        int current_page = getItemCount() / Constant.PRODUCT_PER_REQUEST;
                        onLoadMoreListener.onLoadMore(current_page);
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

}