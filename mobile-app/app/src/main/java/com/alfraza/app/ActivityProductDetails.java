package com.alfraza.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alfraza.app.adapter.AdapterProductImage;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.data.DatabaseHandler;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.Wishlist;
import com.alfraza.app.models.FeatureItem;
import com.alfraza.app.models.FeatureItemSelect;
import com.alfraza.app.models.Features;
import com.alfraza.app.models.MiscData;
import com.alfraza.app.models.Product;
import com.alfraza.app.models.ProductImage;
import com.alfraza.app.helpers.utilities.NetworkCheck;
import com.alfraza.app.helpers.utilities.Tools;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

import static com.alfraza.app.helpers.utilities.Constants.EXTRA_FROM_NOTIF;
import static com.alfraza.app.helpers.utilities.Constants.EXTRA_OBJECT_ID;

public class ActivityProductDetails extends AppCompatActivity {

    private Session session;

    private Long product_id;
    private Boolean from_notif;

    // extra obj
    private Product product;

    private MenuItem wishlist_menu;
    private boolean flag_wishlist = false;
    private DatabaseHandler db;

    private Call<ServerResponse> response;
    private SwipeRefreshLayout swipe_refresh;
    private TextView tv_price, tv_dsc_price;
    private double price, dsc_price;
    private int qty = 1;
    private WebView webview = null;
    private LinearLayout qty_lyt;
    private MaterialRippleLayout lyt_add_cart, lyt_notify;
    private ArrayList<FeatureItemSelect> sFeatures = new ArrayList<>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.destroySession();
        if (response != null && response.isExecuted()) response.cancel();
    }

    // override the base context of application to update default locale for this activity
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(this);

        setContentView(R.layout.activity_product_details);

        product_id = getIntent().getLongExtra(EXTRA_OBJECT_ID, -1L);
        from_notif = getIntent().getBooleanExtra(EXTRA_FROM_NOTIF, false);

        db = new DatabaseHandler(this);

        initToolbar();
        initComponent();
        requestAction();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("");
        }
    }

    private void initComponent() {
        swipe_refresh = findViewById(R.id.swipe_refresh_layout);
        tv_price = findViewById(R.id.price);
        tv_dsc_price = findViewById(R.id.price_strike);
        qty_lyt = findViewById(R.id.qty_lyt);
        lyt_add_cart = findViewById(R.id.lyt_add_cart);
        lyt_notify = findViewById(R.id.lyt_notify);
        final TextView qty_tv = findViewById(R.id.qty);
        ImageView qty_minus = findViewById(R.id.qty_minus);
        ImageView qty_plus = findViewById(R.id.qty_plus);
        qty_tv.setText(String.format(Locale.ENGLISH, "%d", qty));
        qty_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qty > 1) {
                    qty--;
                    qty_tv.setText(String.format(Locale.ENGLISH, "%d", qty));
                }
            }
        });
        qty_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product != null && product.stock != 0 && qty < product.stock) {
                    qty++;
                    qty_tv.setText(String.format(Locale.ENGLISH, "%d", qty));
                }
            }
        });
        // on swipe
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestAction();
            }
        });

        lyt_add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product == null || (product.name != null && product.name.equals(""))) {
                    Toast.makeText(getApplicationContext(), R.string.please_wait_text, Toast.LENGTH_SHORT).show();
                } else {
                    if (session.pref().IsUserlogged()) {
                        session.addCart(product.id, qty, 0, sFeatures);
                    } else session.showLoginDialog();
                }
            }
        });
        lyt_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product == null || (product.name != null && product.name.equals(""))) {
                    Toast.makeText(getApplicationContext(), R.string.please_wait_text, Toast.LENGTH_SHORT).show();
                } else {
                    if (session.pref().IsUserlogged()) {
                        session.notifyStock(product.id, sFeatures);
                    } else session.showLoginDialog();
                }
            }
        });
    }

    private void requestAction() {
        showFailedView(false, "");
        swipeProgress(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestNewsInfoDetailsApi();
            }
        }, 1000);
    }

    private void onFailRequest() {
        swipeProgress(false);
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.no_internet_text));
        }
    }

    private void requestNewsInfoDetailsApi() {
        MiscData misc = new MiscData();
        misc.product_id = product_id;
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.STORE_TYPE);
        request.setSubType(Constants.SINGLE_TYPE);
        request.setUser(session.getUserInfo());
        request.setMisc(misc);
        response = session.api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null && resp.getResult().equals(Constants.SUCCESS)) {
                    product = resp.product;
                    displayPostData();
                    swipeProgress(false);
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest();
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void displayPostData() {
        CardView featuresLayout = findViewById(R.id.featuresLayout);

        ((TextView) findViewById(R.id.title)).setText(Html.fromHtml(product.name));

        webview = findViewById(R.id.content);
        String html_data = "<style>img{max-width:100%;height:auto;} iframe{width:100%;}</style> ";
        html_data += product.description;
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadData(html_data, "text/html; charset=UTF-8", null);
        // disable scroll on touch
        webview.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        price = product.price;
        dsc_price = product.price_discount;
        updateTotal();
        TextView status = findViewById(R.id.status);
        if (product.status != 0) {
            status.setVisibility(View.VISIBLE);
            if (product.status == 1) {
                status.setText(getString(R.string.ready_stock));
            } else if (product.status == 2) {
                status.setText(getString(R.string.out_of_stock));
            } else if (product.status == 3) {
                status.setText(getString(R.string.suspend));
            }
        } else status.setVisibility(View.GONE);
        // display Image slider
        displayImageSlider();
        if (product.a_cart != 0) {
            qty_lyt.setVisibility(View.VISIBLE);
            lyt_add_cart.setVisibility(View.VISIBLE);
        } else {
            qty_lyt.setVisibility(View.GONE);
            lyt_add_cart.setVisibility(View.GONE);
        }
        if (product.a_notify != 0) lyt_notify.setVisibility(View.VISIBLE);
        else lyt_notify.setVisibility(View.GONE);
        if (product.features != null && product.features.size() > 0) {
            featuresLayout.setVisibility(View.VISIBLE);
            prepareFeatures(product.features);
        } else featuresLayout.setVisibility(View.GONE);

        // analytics track
        ThisApplication.getInstance().saveLogEvent(product.id, product.name, "PRODUCT_DETAILS");
    }

    private void displayImageSlider() {
        final LinearLayout layout_dots = findViewById(R.id.layout_dots);
        ViewPager viewPager = findViewById(R.id.pager);
        final AdapterProductImage adapterSlider = new AdapterProductImage(this, new ArrayList<ProductImage>());

        final List<ProductImage> productImages = new ArrayList<>();
        ProductImage p = new ProductImage();
        p.product_id = product.id;
        p.name = product.image;
        p.full = product.full_image;
        productImages.add(p);
        if (product.product_images != null && product.product_images.size() > 0)
            productImages.addAll(product.product_images);
        adapterSlider.setItems(productImages);
        viewPager.setAdapter(adapterSlider);

        // displaying selected image first
        viewPager.setCurrentItem(0);
        addBottomDots(layout_dots, adapterSlider.getCount(), 0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                addBottomDots(layout_dots, adapterSlider.getCount(), pos);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        final ArrayList<String> images_list = new ArrayList<>();
        for (ProductImage img : productImages) {
            images_list.add(img.full);
        }

        adapterSlider.setOnItemClickListener(new AdapterProductImage.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ProductImage obj, int pos) {
                Intent i = new Intent(ActivityProductDetails.this, ActivityFullScreenImage.class);
                i.putExtra(ActivityFullScreenImage.EXTRA_POS, pos);
                i.putStringArrayListExtra(ActivityFullScreenImage.EXTRA_IMGS, images_list);
                startActivity(i);
            }
        });
    }

    private void prepareFeatures(List<Features> features) {
        LinearLayout cardLayout = findViewById(R.id.card_layout);
        cardLayout.removeAllViews();
        for (Features feature : features) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setId(feature.id);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int Margin = ITUtilities.DpToPx(this, 3f);
            params.setMargins(Margin, Margin, Margin, Margin);
            linearLayout.setLayoutParams(params);
            TextView tvTitle = new TextView(this);
            String tid = "22" + feature.id;
            tvTitle.setId(Integer.parseInt(tid));
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tvParams.gravity = Gravity.START | Gravity.CENTER;
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
            tvTitle.setText(feature.name);
            tvTitle.setLayoutParams(tvParams);
            linearLayout.addView(tvTitle);
            final Spinner spinner = new Spinner(this);
            final String sid = "11" + feature.id;
            spinner.setId(Integer.parseInt(sid));
            LinearLayout.LayoutParams spParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            spinner.setGravity(Gravity.CENTER);
            spinner.setLayoutParams(spParams);
            //fill data in spinner
            ArrayAdapter<FeatureItem> adapter;
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, feature.features);
            spinner.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    FeatureItem ftData = (FeatureItem) parent.getSelectedItem();
                    setsFeatures(Integer.parseInt(sid), ftData.id, ftData.price, ftData.dsc_price);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            linearLayout.addView(spinner);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spinner.performClick();
                }
            });
            cardLayout.addView(linearLayout);
        }
    }


    private void setsFeatures(int sid, int iid, double price, double dsc_price) {
        if (sFeatures.size() > 0) {
            for (int i = 0; i < sFeatures.size(); i++) {
                if (sFeatures.get(i).sid == sid) unsetFeature(i);
            }
            sFeatures.add(new FeatureItemSelect(sid, iid, price, dsc_price));
        } else sFeatures.add(new FeatureItemSelect(sid, iid, price, dsc_price));
        updateTotal();
    }

    private void unsetFeature(int position) {
        sFeatures.remove(position);
    }

    public void updateTotal() {
        if (product != null) {
            if (sFeatures.size() > 0) {
                price = 0;
                dsc_price = 0;
                for (int i = 0; i < sFeatures.size(); i++) {
                    price += sFeatures.get(i).price;
                    dsc_price += sFeatures.get(i).dsc_price;
                }
            }
            // handle discount view
            if (dsc_price > 0) {
                tv_dsc_price.setText(Tools.getFormattedPrice(dsc_price, product.currency));
                tv_dsc_price.setPaintFlags(tv_dsc_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tv_dsc_price.setVisibility(View.VISIBLE);
            } else tv_dsc_price.setVisibility(View.GONE);
            tv_price.setText(Tools.getFormattedPrice(price, product.currency));
        }
    }

    private void addBottomDots(LinearLayout layout_dots, int size, int current) {
        ImageView[] dots = new ImageView[size];

        layout_dots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 15;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle);
            dots[i].setColorFilter(ContextCompat.getColor(this, R.color.darkOverlaySoft));
            layout_dots.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[current].setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryLight));
        if (size > 1) layout_dots.setVisibility(View.VISIBLE);
        else layout_dots.setVisibility(View.GONE);
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = findViewById(R.id.lyt_failed);
        View lyt_main_content = findViewById(R.id.lyt_main_content);

        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            lyt_main_content.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            lyt_main_content.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        findViewById(R.id.failed_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction();
            }
        });
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipe_refresh.setRefreshing(false);
            return;
        }
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh.setRefreshing(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_product_details, menu);
        wishlist_menu = menu.findItem(R.id.action_wish);
        refreshWishlistMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            onBackAction();
        } else if (item_id == R.id.action_wish) {
            if (product.name == null || product.name.equals("")) {
                Toast.makeText(this, R.string.cannot_add_wishlist, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (flag_wishlist) {
                db.deleteWishlist(product_id);
                Toast.makeText(this, R.string.remove_wishlist, Toast.LENGTH_SHORT).show();
            } else {
                Wishlist w = new Wishlist(product.id, product.name, product.image, System.currentTimeMillis());
                db.saveWishlist(w);
                Toast.makeText(this, R.string.add_wishlist, Toast.LENGTH_SHORT).show();
            }
            refreshWishlistMenu();
        } else if (item_id == R.id.action_cart) {
            Intent i = new Intent(this, ActivityShoppingCart.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        onBackAction();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webview != null) webview.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webview != null) webview.onPause();
    }

    private void onBackAction() {
        if (from_notif) {
            if (ActivityStore.active) {
                finish();
            } else {
                Intent intent = new Intent(getApplicationContext(), ActivitySplash.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void refreshWishlistMenu() {
        Wishlist w = db.getWishlist(product_id);
        flag_wishlist = (w != null);
        if (flag_wishlist) {
            wishlist_menu.setIcon(R.drawable.ic_fav_colored);
        } else {
            wishlist_menu.setIcon(R.drawable.ic_fav);
        }
    }
}
