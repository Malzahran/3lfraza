package com.alfraza.app.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alfraza.app.ActivityForm;
import com.alfraza.app.R;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.models.ExpandItemsData;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ExpandItemsAdapter extends RecyclerView.Adapter<ExpandItemsAdapter.MyViewHolder> {

    private static int currentPosition = 7120;
    private ArrayList<ExpandItemsData> expandItems;
    private Context ctx;
    private Session session;
    private int sRadio = 0;


    public ExpandItemsAdapter(Activity activity, ArrayList<ExpandItemsData> expandItems) {
        this.ctx = activity.getApplicationContext();
        this.session = new Session(activity);
        this.expandItems = expandItems;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_expand, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ExpandItemsData expandItem = expandItems.get(position);
        final int cPosition = position;
        holder.rd_check.setVisibility(View.GONE);
        holder.expand_btn.setVisibility(View.GONE);
        holder.btn_action.setVisibility(View.GONE);
        holder.tv_title.setText(expandItem.getTitle());
        holder.imv_img.setVisibility(View.GONE);
        if (expandItem.getDesc() != null) {
            holder.tv_desc.setText(ITUtilities.fromHtml(expandItem.getDesc()));
        }

        if (expandItem.getImage() != null) {
            holder.imv_img.setVisibility(View.VISIBLE);
            float resizeDp = 200f;
            // Convert to pixels
            int resizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, resizeDp, ctx.getResources().getDisplayMetrics());
            ITUtilities.loadImg(ctx)
                    .load(expandItem.getImage())
                    .centerCrop()
                    .resize(resizePx, resizePx)
                    .error(R.drawable.ic_error)
                    .into(holder.imv_img);
        }
        if (expandItem.getAction() != null) {
            holder.btn_action.setVisibility(View.VISIBLE);
            holder.btn_action.setText(expandItem.getAction());
            if (expandItem.getIntent() != null || expandItem.getUrl() != null) {
                holder.btn_action.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (expandItem.getIntent() != null) {
                            session.NewActivity(expandItem.getIntent(), 0);
                        } else if (expandItem.getUrl() != null) {
                            URL uri = null; // missing 'http://' will cause crashed
                            try {
                                uri = session.get_url(expandItem.getUrl());
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            if (uri != null) {
                                session.OpenLink(uri);
                            }
                        }
                    }
                });
            }
        }
        if (expandItem.AllowExpand() != 0) {
            holder.expand_btn.setVisibility(View.VISIBLE);
            //if the position is equals to the item position which is to be expanded
            if (currentPosition == position) {
                boolean expand = true;
                if (holder.linearLayout.getVisibility() == View.VISIBLE) {
                    expand = false;
                }
                if (expand) {
                    holder.expand_btn.setImageResource(R.drawable.ic_collapse);
                    //creating an animation
                    Animation slideDown = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);

                    //toggling visibility
                    holder.linearLayout.setVisibility(View.VISIBLE);

                    //adding sliding effect
                    holder.linearLayout.startAnimation(slideDown);
                }

            } else {
                holder.expand_btn.setImageResource(R.drawable.ic_expand);
                holder.linearLayout.setVisibility(View.GONE);
            }

            holder.expand_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (currentPosition == cPosition) {
                        currentPosition = 7120;
                    } else {
                        //getting the position of the item to expand it
                        currentPosition = cPosition;
                    }

                    //reloading the list
                    notifyDataSetChanged();
                }
            });

            holder.tv_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (currentPosition == cPosition) {
                        currentPosition = 7120;
                    } else {
                        //getting the position of the item to expand it
                        currentPosition = cPosition;
                    }

                    //reloading the list
                    notifyDataSetChanged();
                }
            });
        }
        if (expandItem.EnableRadio() != 0) {
            holder.rd_check.setVisibility(View.VISIBLE);
            if (expandItem.DisableRadioCheck() != 0) {
                holder.rd_check.setEnabled(false);
            } else {
                holder.rd_check.setEnabled(true);
                if (sRadio == expandItem.getId()) {
                    holder.rd_check.setChecked(true);
                } else {
                    holder.rd_check.setChecked(false);
                }
                final int itemID = expandItem.getId();
                boolean expand = false;
                if (expandItem.AllowExpand() != 0) {
                    expand = true;
                }
                final boolean allowExpand = expand;
                holder.rd_check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (sRadio != itemID) {
                            if (allowExpand && currentPosition != 7120) {
                                if (currentPosition != cPosition) {
                                    currentPosition = cPosition;
                                }
                            }
                            sRadio = itemID;
                            if (expandItem.AllowGps() != 0) {
                                if (ctx instanceof ActivityForm) {
                                    ((ActivityForm) ctx).gpsState(true);
                                }
                            }
                            //reloading the list
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        }
    }

    public int getSelectedRadio() {
        return sRadio;
    }

    @Override
    public int getItemCount() {
        return expandItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_desc;
        ImageView imv_img, expand_btn;
        RadioButton rd_check;
        AppCompatButton btn_action;
        LinearLayout linearLayout;


        MyViewHolder(View view) {
            super(view);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_desc = itemView.findViewById(R.id.tv_desc);
            imv_img = itemView.findViewById(R.id.imv_img);
            rd_check = itemView.findViewById(R.id.rd_check);
            expand_btn = itemView.findViewById(R.id.expand_btn);
            btn_action = itemView.findViewById(R.id.btn_action);
            linearLayout = view.findViewById(R.id.linearLayout);
        }
    }

}