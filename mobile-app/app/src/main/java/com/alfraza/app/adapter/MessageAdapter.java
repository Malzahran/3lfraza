package com.alfraza.app.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alfraza.app.R;
import com.alfraza.app.activities.misc.ActivityMessages;
import com.alfraza.app.fragments.MessagesFragment;
import com.alfraza.app.helpers.transformations.CircleTransform;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.models.MessagesData;

import java.util.ArrayList;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> implements Filterable {

    private ArrayList<MessagesData> messages;
    private ArrayList<MessagesData> mFilteredList;
    private Activity act;
    private Context ctx;
    private int type;

    public MessageAdapter(Activity activity, ArrayList<MessagesData> messages, int type) {
        this.act = activity;
        this.ctx = activity.getApplicationContext();
        this.messages = messages;
        this.mFilteredList = messages;
        this.type = type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_liner, parent, false);
        if (type == 2) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_row, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        MessagesData msg = mFilteredList.get(position);

        ImageView imgholder = null;
        ProgressBar imgprog = null;
        ImageView doneimg = null;
        ImageView errorimg = null;
        TextView id = null;
        TextView title = null;
        TextView msgtext = null;
        TextView state = null;
        TextView time = null;
        if (type == 1) {
            imgholder = holder.imv_img;
            imgprog = holder.progressBar;
            id = holder.tv_id;
            title = holder.tv_title;
            msgtext = holder.tv_desc;
            time = holder.tv_time;
            holder.tv_desc.setVisibility(View.GONE);
            holder.imv_go.setVisibility(View.VISIBLE);
            holder.tv_time.setVisibility(View.VISIBLE);
            holder.tv_time.setText(null);
            holder.tv_desc.setText(null);
            holder.tv_title.setText(null);

        } else if (type == 2) {
            holder.msg_from.setVisibility(View.GONE);
            holder.msg_me.setVisibility(View.GONE);
            holder.tv_tit_m.setVisibility(View.GONE);
            holder.tv_tit_f.setVisibility(View.GONE);
            holder.imv_done_f.setVisibility(View.GONE);
            holder.imv_error_f.setVisibility(View.GONE);
            holder.imv_done_m.setVisibility(View.GONE);
            holder.imv_error_m.setVisibility(View.GONE);
            holder.tv_state_f.setText(null);
            holder.tv_state_m.setText(null);
            holder.tv_time_f.setText(null);
            holder.tv_time_m.setText(null);
            holder.tv_tit_f.setText(null);
            holder.tv_tit_m.setText(null);
            holder.tv_msg_f.setText(null);
            holder.tv_msg_m.setText(null);
            if (msg.getType() == 1) {
                holder.msg_from.setVisibility(View.VISIBLE);
                imgholder = holder.imv_img_f;
                msgtext = holder.tv_msg_f;
                title = holder.tv_tit_f;
                doneimg = holder.imv_done_f;
                errorimg = holder.imv_error_f;
                state = holder.tv_state_f;
                time = holder.tv_time_f;
            } else if (msg.getType() == 2) {
                holder.msg_me.setVisibility(View.VISIBLE);
                imgholder = holder.imv_img_m;
                msgtext = holder.tv_msg_m;
                title = holder.tv_tit_m;
                doneimg = holder.imv_done_m;
                errorimg = holder.imv_error_m;
                state = holder.tv_state_m;
                time = holder.tv_time_m;
            }
        }

        if (msg.getId() != 0 && id != null) {
            id.setText(String.format(Locale.ENGLISH, "%d", msg.getId()));
        }
        if (msg.getTitle() != null && title != null) {
            title.setText(msg.getTitle());
            title.setVisibility(View.VISIBLE);
        }
        if (msg.getText() != null && msgtext != null) {
            msgtext.setText(ITUtilities.fromHtml(msg.getText()));
            msgtext.setVisibility(View.VISIBLE);
        }
        if (msg.getTime() != null && time != null) {
            time.setText(msg.getTime());
        }
        if (msg.getState() != null && state != null) {
            state.setText(msg.getState());
            state.setVisibility(View.VISIBLE);
        }
        if (doneimg != null) {
            doneimg.setVisibility(View.GONE);
        }
        if (errorimg != null) {
            errorimg.setVisibility(View.GONE);
        }
        if (msg.getIcon() != 0) {
            if (msg.getIcon() == 1 && doneimg != null) {
                doneimg.setVisibility(View.VISIBLE);
            } else if (msg.getIcon() == 2 && errorimg != null) {
                errorimg.setVisibility(View.VISIBLE);
            }
        }
        if (msg.getImg() != null && imgholder != null) {
            imgholder.setVisibility(View.VISIBLE);
            if (type == 1 && imgprog != null) {
                holder.progressBar.setVisibility(View.GONE);
            }
            float SizeDp = 60f;
            int SizePx = ITUtilities.DpToPx(ctx, SizeDp);
            ITUtilities.loadImg(ctx)
                    .load(msg.getImg())
                    .resize(SizePx, SizePx)
                    .centerCrop()
                    .error(R.mipmap.ic_launcher)
                    .transform(new CircleTransform())
                    .into(imgholder);
        } else if (imgholder != null) {
            imgholder.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = messages;
                } else {

                    ArrayList<MessagesData> filteredList = new ArrayList<>();

                    for (MessagesData message : messages) {

                        if (message.getTitle().toLowerCase().contains(charString)) {
                            filteredList.add(message);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<MessagesData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imv_img_m, imv_img_f;
        private ImageView imv_done_m, imv_done_f;
        private ImageView imv_error_m, imv_error_f;
        private TextView tv_tit_m, tv_tit_f, tv_msg_m, tv_msg_f;
        private TextView tv_state_m, tv_state_f;
        private TextView tv_time_m, tv_time_f;
        private CardView msg_me, msg_from;
        private ImageView imv_img, imv_go;
        private TextView tv_title, tv_desc, tv_time, tv_id;
        private ProgressBar progressBar;


        MyViewHolder(View view) {
            super(view);
            if (type == 1) {
                imv_img = view.findViewById(R.id.imv_img);
                imv_go = view.findViewById(R.id.imv_go);
                tv_title = view.findViewById(R.id.tv_title);
                tv_desc = view.findViewById(R.id.tv_desc);
                tv_time = view.findViewById(R.id.tv_time);
                tv_id = view.findViewById(R.id.tv_id);
                progressBar = view.findViewById(R.id.img_progress);
                view.setOnClickListener(this);
            } else if (type == 2) {
                imv_img_m = view.findViewById(R.id.imv_img_m);
                imv_img_f = view.findViewById(R.id.imv_img_f);
                imv_done_m = view.findViewById(R.id.imv_done_m);
                imv_done_f = view.findViewById(R.id.imv_done_f);
                imv_error_m = view.findViewById(R.id.imv_error_m);
                imv_error_f = view.findViewById(R.id.imv_error_f);
                tv_tit_m = view.findViewById(R.id.tv_tit_m);
                tv_tit_f = view.findViewById(R.id.tv_tit_f);
                tv_msg_m = view.findViewById(R.id.tv_msg_m);
                tv_msg_f = view.findViewById(R.id.tv_msg_f);
                tv_state_m = view.findViewById(R.id.tv_state_m);
                tv_state_f = view.findViewById(R.id.tv_state_f);
                tv_time_m = view.findViewById(R.id.tv_time_m);
                tv_time_f = view.findViewById(R.id.tv_time_f);
                msg_me = view.findViewById(R.id.msg_me);
                msg_from = view.findViewById(R.id.msg_from);
            }
        }

        @Override
        public void onClick(View v) {
            // get position
            int pos = getAdapterPosition();
            MessagesData msg = messages.get(pos);
            Bundle bundle = new Bundle();
            bundle.putInt("convid", msg.getId());
            bundle.putInt("contype", msg.getCtype());
            bundle.putString("contit", msg.getTitle());
            //set Fragmentclass Arguments
            Fragment fragment = new MessagesFragment();
            fragment.setArguments(bundle);
            FragmentTransaction ft = act.getFragmentManager().beginTransaction();
            ft.add(R.id.fragment_frame, fragment, "messages");
            ft.commitAllowingStateLoss();
            ((ActivityMessages) act).AllowFragment(1);
        }
    }

}