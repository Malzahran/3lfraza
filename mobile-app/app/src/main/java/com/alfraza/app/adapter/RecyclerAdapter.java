package com.alfraza.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.alfraza.app.R;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.transformations.CircleTransform;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.models.FormInputData;
import com.alfraza.app.models.IntentData;
import com.alfraza.app.models.ItemData;
import com.alfraza.app.models.LayoutData;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {
    private ArrayList<ItemData> items;
    private ArrayList<ItemData> mFilteredList;
    private LayoutData layoutData;
    private Session session;
    private Activity act;
    private Context ctx;

    public RecyclerAdapter(Activity activity, ArrayList<ItemData> items, LayoutData layoutData) {
        this.act = activity;
        this.ctx = activity.getApplicationContext();
        this.items = items;
        this.mFilteredList = items;
        this.layoutData = layoutData;
        this.session = new Session(act);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        if (layoutData.getLayoutType() == 1) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row_grid, viewGroup, false);
        } else if (layoutData.getLayoutType() == 2) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row_liner, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final int i = position;
        if (mFilteredList.get(i).getTitle() != null) {
            viewHolder.tv_title.setText(ITUtilities.fromHtml(mFilteredList.get(i).getTitle()));
        }
        viewHolder.img_frame.setVisibility(View.GONE);
        viewHolder.progressBar.setVisibility(View.GONE);
        if (mFilteredList.get(i).getImg() != null) {
            if (layoutData.getLayoutType() == 1 || layoutData.getLayoutType() == 2) {
                float SizeDp = 105f;
                if (layoutData.getLayoutType() == 2) {
                    SizeDp = 40f;
                }
                int SizePx = ITUtilities.DpToPx(ctx, SizeDp);
                viewHolder.img_frame.setVisibility(View.VISIBLE);
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                if (layoutData.isCircleimg() == 1) {
                    ITUtilities.loadImg(ctx)
                            .load(mFilteredList.get(i).getImg())
                            .error(R.drawable.ic_error)
                            .resize(SizePx, SizePx)
                            .centerCrop()
                            .transform(new CircleTransform())
                            .into(viewHolder.imv_img, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                }
                            });
                    if (layoutData.getLayoutType() == 1) {
                        viewHolder.card_view.setCardBackgroundColor(Color.TRANSPARENT);
                        viewHolder.card_view.setCardElevation(0);
                    }
                } else {
                    ITUtilities.loadImg(ctx)
                            .load(mFilteredList.get(i).getImg())
                            .error(R.drawable.ic_error)
                            .resize(SizePx, SizePx)
                            .centerCrop()
                            .into(viewHolder.imv_img, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                }
                            });
                    viewHolder.imv_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }
        }
        if (layoutData.getLayoutType() == 1) {

            if (mFilteredList.get(i).getTitle2() != null) {
                if (layoutData.isCircleimg() == 1) {
                    viewHolder.tv_title2.setTextColor(ContextCompat.getColor(act, R.color.colorPrimary));
                } else {
                    viewHolder.tv_title2.setBackgroundColor(ContextCompat.getColor(act, R.color.colorPrimary));
                    viewHolder.tv_title2.setTextColor(ContextCompat.getColor(act, R.color.White));
                }
                viewHolder.tv_title2.setVisibility(View.VISIBLE);
                viewHolder.tv_title2.setText(ITUtilities.fromHtml(mFilteredList.get(i).getTitle2()));
            } else {
                viewHolder.tv_title2.setVisibility(View.GONE);
            }

            if (mFilteredList.get(i).getCount() != null) {
                viewHolder.tv_count.setText(mFilteredList.get(i).getCount());
                viewHolder.tv_count.setVisibility(View.VISIBLE);
            }
        } else if (layoutData.getLayoutType() == 2) {
            viewHolder.tv_desc.setVisibility(View.GONE);
            viewHolder.tv_desc.setText(null);
            if (mFilteredList.get(i).getDesc() != null) {
                viewHolder.tv_desc.setText(ITUtilities.fromHtml(mFilteredList.get(i).getDesc()));
                viewHolder.tv_desc.setVisibility(View.VISIBLE);
            }

            viewHolder.tv_time.setVisibility(View.GONE);
            if (mFilteredList.get(i).getTime() != null) {
                viewHolder.tv_time.setText(ITUtilities.fromHtml(mFilteredList.get(i).getTime()));
                viewHolder.tv_time.setVisibility(View.VISIBLE);
            }

            viewHolder.tv_state.setVisibility(View.GONE);
            if (mFilteredList.get(i).getState() != null) {
                viewHolder.tv_state.setVisibility(View.VISIBLE);
                viewHolder.tv_state.setText(ITUtilities.fromHtml(mFilteredList.get(i).getState()));
            }
            viewHolder.layout_moreinfo.setVisibility(View.GONE);
            if (mFilteredList.get(i).getMinfo() != null) {
                viewHolder.layout_moreinfo.setVisibility(View.VISIBLE);
                viewHolder.tv_m_1.setText(ITUtilities.fromHtml(mFilteredList.get(i).getMinfo()));
            }
            if (mFilteredList.get(i).getMinfo2() != null) {
                viewHolder.layout_moreinfo.setVisibility(View.VISIBLE);
                viewHolder.tv_m_2.setText(ITUtilities.fromHtml(mFilteredList.get(i).getMinfo2()));
            }
            int clicktype;
            if (layoutData.HandleIndvd() != 0 && mFilteredList.get(i).getIntent() != null) {
                clicktype = mFilteredList.get(i).getClickType();
            } else {
                clicktype = layoutData.getClickType();
            }
            viewHolder.imv_view.setVisibility(View.GONE);
            viewHolder.imv_go.setVisibility(View.GONE);
            viewHolder.imv_msg.setVisibility(View.GONE);
            viewHolder.imv_phone.setVisibility(View.GONE);
            viewHolder.imv_sms.setVisibility(View.GONE);
            viewHolder.imv_web.setVisibility(View.GONE);
            viewHolder.imv_custom.setVisibility(View.GONE);
            viewHolder.layout_btn.setVisibility(View.GONE);
            viewHolder.layout_contact.setVisibility(View.GONE);
            viewHolder.tv_contact.setVisibility(View.GONE);
            viewHolder.ratingBar.setVisibility(View.GONE);
            viewHolder.tv_rateTxt.setVisibility(View.GONE);
            if (clicktype == 1) {
                viewHolder.layout_btn.setVisibility(View.VISIBLE);
                viewHolder.imv_view.setVisibility(View.VISIBLE);
                viewHolder.card_layout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        IntentData intentData = layoutData.getIntent();
                        int id = 0;
                        if (layoutData.HandleIndvd() != 0 && mFilteredList.get(i).getIntent() != null) {
                            intentData = mFilteredList.get(i).getIntent();
                        }
                        if (layoutData.Indvid() != 0 && mFilteredList.get(i).getId() != 0) {
                            id = mFilteredList.get(i).getId();
                        }
                        session.NewActivity(intentData, id);
                    }
                });
            } else if (clicktype == 2) {
                viewHolder.layout_btn.setVisibility(View.VISIBLE);
                viewHolder.imv_view.setVisibility(View.VISIBLE);
                viewHolder.imv_view.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        IntentData intentData = layoutData.getIntent();
                        int id = 0;
                        if (layoutData.HandleIndvd() != 0 && mFilteredList.get(i).getIntent() != null) {
                            intentData = mFilteredList.get(i).getIntent();
                        }
                        if (layoutData.Indvid() != 0 && mFilteredList.get(i).getId() != 0) {
                            id = mFilteredList.get(i).getId();
                        }
                        session.NewActivity(intentData, id);
                    }
                });

            } else if (clicktype == 3) {
                viewHolder.layout_btn.setVisibility(View.VISIBLE);
                viewHolder.imv_go.setVisibility(View.VISIBLE);
                viewHolder.imv_go.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        IntentData intentData = layoutData.getIntent();
                        int id = 0;
                        if (layoutData.HandleIndvd() != 0 && mFilteredList.get(i).getIntent() != null) {
                            intentData = mFilteredList.get(i).getIntent();
                        }
                        if (layoutData.Indvid() != 0 && mFilteredList.get(i).getId() != 0) {
                            id = mFilteredList.get(i).getId();
                        }
                        session.NewActivity(intentData, id);
                    }
                });
            } else if (clicktype == 4) {
                viewHolder.layout_btn.setVisibility(View.VISIBLE);
                viewHolder.imv_go.setVisibility(View.VISIBLE);
                viewHolder.card_layout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        IntentData intentData = layoutData.getIntent();
                        int id = 0;
                        if (layoutData.HandleIndvd() != 0 && mFilteredList.get(i).getIntent() != null) {
                            intentData = mFilteredList.get(i).getIntent();
                        }
                        if (layoutData.Indvid() != 0 && mFilteredList.get(i).getId() != 0) {
                            id = mFilteredList.get(i).getId();
                        }
                        session.NewActivity(intentData, id);
                    }
                });
            } else if (clicktype == 5) {
                viewHolder.layout_btn.setVisibility(View.VISIBLE);
                viewHolder.imv_custom.setVisibility(View.VISIBLE);
                String customicon = layoutData.getCustomicon();
                if (layoutData.HandleIndvd() != 0 && mFilteredList.get(i).getIntent() != null) {
                    customicon = mFilteredList.get(i).getCustomicon();
                }
                ITUtilities.loadImg(ctx)
                        .load(customicon)
                        .error(R.drawable.ic_error)
                        .fit()
                        .into(viewHolder.imv_custom);
                viewHolder.imv_custom.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        IntentData intentData = layoutData.getIntent();
                        int id = 0;
                        if (layoutData.HandleIndvd() != 0 && mFilteredList.get(i).getIntent() != null) {
                            intentData = mFilteredList.get(i).getIntent();
                        }
                        if (layoutData.Indvid() != 0 && mFilteredList.get(i).getId() != 0) {
                            id = mFilteredList.get(i).getId();
                        }
                        session.NewActivity(intentData, id);
                    }
                });
            } else if (clicktype == 6) {
                viewHolder.layout_btn.setVisibility(View.VISIBLE);
                viewHolder.imv_custom.setVisibility(View.VISIBLE);
                String customicon = layoutData.getCustomicon();
                if (layoutData.HandleIndvd() != 0 && mFilteredList.get(i).getIntent() != null) {
                    customicon = mFilteredList.get(i).getCustomicon();
                }
                ITUtilities.loadImg(ctx)
                        .load(customicon)
                        .error(R.drawable.ic_error)
                        .fit()
                        .into(viewHolder.imv_custom);
                viewHolder.card_layout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        IntentData intentData = layoutData.getIntent();
                        int id = 0;
                        if (layoutData.HandleIndvd() != 0 && mFilteredList.get(i).getIntent() != null) {
                            intentData = mFilteredList.get(i).getIntent();
                        }
                        if (layoutData.Indvid() != 0 && mFilteredList.get(i).getId() != 0) {
                            id = mFilteredList.get(i).getId();
                        }
                        session.NewActivity(intentData, id);
                    }
                });
            }

            if (mFilteredList.get(i).getRating() != 0f) {
                viewHolder.ratingBar.setRating(mFilteredList.get(i).getRating());
                viewHolder.ratingBar.setVisibility(View.VISIBLE);
            }
            if (mFilteredList.get(i).getRatingText() != null) {
                viewHolder.tv_rateTxt.setText(mFilteredList.get(i).getRatingText());
                viewHolder.tv_rateTxt.setVisibility(View.VISIBLE);
            }
            if (mFilteredList.get(i).getContacttitle() != null) {
                viewHolder.tv_contact.setVisibility(View.VISIBLE);
                viewHolder.tv_contact.setText(mFilteredList.get(i).getContacttitle());
            }

            if (mFilteredList.get(i).getPhone() != null) {
                if (viewHolder.layout_contact.getVisibility() == View.GONE) {
                    viewHolder.layout_contact.setVisibility(View.VISIBLE);
                }
                viewHolder.imv_phone.setVisibility(View.VISIBLE);
                viewHolder.imv_phone.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        session.CallNumber(mFilteredList.get(i).getPhone());
                    }
                });
            }

            if (mFilteredList.get(i).getSms() != null) {
                if (viewHolder.layout_contact.getVisibility() == View.GONE) {
                    viewHolder.layout_contact.setVisibility(View.VISIBLE);
                }
                viewHolder.imv_sms.setVisibility(View.VISIBLE);
                viewHolder.imv_sms.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        session.SendSms(mFilteredList.get(i).getSms());
                    }
                });
            }
            if (mFilteredList.get(i).getConvname() != null && mFilteredList.get(i).getConvid() != 0) {
                if (viewHolder.layout_contact.getVisibility() == View.GONE) {
                    viewHolder.layout_contact.setVisibility(View.VISIBLE);
                }
                viewHolder.imv_msg.setVisibility(View.VISIBLE);
                viewHolder.imv_msg.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Integer userid = mFilteredList.get(i).getConvid();
                        String username = mFilteredList.get(i).getConvname();
                        int type = mFilteredList.get(i).getType();
                        session.SendMsgDialog(username, userid, type);
                    }
                });
            }

            if (mFilteredList.get(i).getUrl() != null) {
                viewHolder.layout_btn.setVisibility(View.VISIBLE);
                viewHolder.imv_web.setVisibility(View.VISIBLE);
                viewHolder.imv_web.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        URL uri = null; // missing 'http://' will cause crashed
                        try {
                            uri = session.get_url(mFilteredList.get(i).getUrl());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        if (uri != null) {
                            session.OpenLink(uri);
                        }
                    }
                });
            }
            viewHolder.buttons_layout.setVisibility(View.GONE);
            viewHolder.buttons_layout.removeAllViews();
            if (mFilteredList.get(i).getButtons() != null && mFilteredList.get(i).getButtons().length > 0) {
                FormInputData[] buttons = mFilteredList.get(i).getButtons();
                int dp = ITUtilities.DpToPx(ctx, 5f);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.topMargin = dp;
                params.bottomMargin = dp;
                params.rightMargin = dp;
                params.leftMargin = dp;
                for (final FormInputData button : buttons) {
                    AppCompatButton btn = new AppCompatButton(act);
                    btn.setLayoutParams(params);
                    int resources = R.drawable.ripple_effect_dark;
                    if (button.getColor() != 0) {
                        switch (button.getColor()) {
                            case 1:
                                resources = R.drawable.button_red;
                                break;
                            case 2:
                                resources = R.drawable.button_orange;
                                break;
                            case 3:
                                resources = R.drawable.button_green;
                                break;
                            case 4:
                                resources = R.drawable.button_blue;
                                break;
                            case 5:
                                resources = R.drawable.button_purple;
                                break;
                            case 6:
                                resources = R.drawable.round_button_face;
                                break;
                            case 7:
                                resources = R.drawable.round_button_google;
                                break;
                            case 8:
                                resources = R.drawable.button_black;
                                break;
                            case 9:
                                resources = R.drawable.layout_border_oval;
                                break;
                        }
                    }
                    btn.setBackgroundResource(resources);
                    if (button.getId() != 0) {
                        btn.setId(button.getId());
                    }
                    if (button.getText() != null) {
                        btn.setText(button.getText());
                        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        btn.setTextColor(ContextCompat.getColor(act, R.color.White));
                        btn.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    }
                    if (button.getIntentData() != null) {
                        btn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                IntentData intentData = button.getIntentData();
                                session.NewActivity(intentData, 0);
                            }
                        });
                    }
                    viewHolder.buttons_layout.addView(btn);
                }
                viewHolder.buttons_layout.setVisibility(View.VISIBLE);
            }
            if (mFilteredList.get(i).getAction() != null && mFilteredList.get(i).getActionintent() != null) {
                viewHolder.btn_action.setVisibility(View.VISIBLE);
                viewHolder.btn_action.setText(mFilteredList.get(i).getAction());
                viewHolder.btn_action.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        session.NewActivity(mFilteredList.get(i).getActionintent(), 0);
                    }
                });
            }


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

                    mFilteredList = items;
                } else {

                    ArrayList<ItemData> filteredList = new ArrayList<>();

                    for (ItemData itemdata : items) {

                        if (itemdata.getTitle().toLowerCase().contains(charString)) {
                            filteredList.add(itemdata);
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
                mFilteredList = (ArrayList<ItemData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_desc, tv_title, tv_title2, tv_time, tv_count, tv_m_1, tv_m_2, tv_state, tv_contact, tv_rateTxt;
        private ImageView imv_img, imv_msg, imv_view, imv_go, imv_phone, imv_sms, imv_web, imv_custom;
        private LinearLayout card_layout, layout_moreinfo, layout_btn, layout_contact, buttons_layout;
        private AppCompatButton btn_action;
        private RatingBar ratingBar;
        private ProgressBar progressBar;
        private FrameLayout img_frame;
        private CardView card_view;

        public ViewHolder(View view) {
            super(view);
            tv_title = view.findViewById(R.id.tv_title);
            img_frame = view.findViewById(R.id.img_frame);
            imv_img = view.findViewById(R.id.imv_img);
            progressBar = view.findViewById(R.id.img_progress);
            if (layoutData.getLayoutType() == 1) {
                tv_title2 = view.findViewById(R.id.tv_title2);
                tv_time = view.findViewById(R.id.tv_time);
                tv_count = view.findViewById(R.id.tv_count);
                card_view = view.findViewById(R.id.card_view);
                view.setOnClickListener(this);
            } else if (layoutData.getLayoutType() == 2) {
                imv_msg = view.findViewById(R.id.imv_msg);
                imv_view = view.findViewById(R.id.imv_view);
                imv_go = view.findViewById(R.id.imv_go);
                imv_phone = view.findViewById(R.id.imv_phone);
                imv_sms = view.findViewById(R.id.imv_sms);
                imv_web = view.findViewById(R.id.imv_web);
                imv_custom = view.findViewById(R.id.imv_custom);
                card_layout = view.findViewById(R.id.card_layout);
                layout_moreinfo = view.findViewById(R.id.layout_moreinfo);
                layout_btn = view.findViewById(R.id.layout_btn);
                layout_contact = view.findViewById(R.id.layout_contact);
                buttons_layout = view.findViewById(R.id.buttons_layout);
                tv_desc = view.findViewById(R.id.tv_desc);
                tv_time = view.findViewById(R.id.tv_time);
                tv_rateTxt = view.findViewById(R.id.tv_rateTxt);
                tv_state = view.findViewById(R.id.tv_state);
                tv_contact = view.findViewById(R.id.tv_contact);
                tv_m_1 = view.findViewById(R.id.tv_m_1);
                tv_m_2 = view.findViewById(R.id.tv_m_2);
                ratingBar = view.findViewById(R.id.ratingBar);
                btn_action = view.findViewById(R.id.btn_action);
            }
        }

        @Override
        public void onClick(View view) {
            IntentData intentData = layoutData.getIntent();
            int id = 0;
            // get position
            int pos = getAdapterPosition();
            if (layoutData.HandleIndvd() != 0 && mFilteredList.get(pos).getIntent() != null) {
                intentData = mFilteredList.get(pos).getIntent();
            }
            if (layoutData.Indvid() != 0 && mFilteredList.get(pos).getId() != 0) {
                id = mFilteredList.get(pos).getId();
            }
            session.NewActivity(intentData, id);
        }

    }
}