package com.alfraza.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.alfraza.app.activities.misc.ActivityNotifications;
import com.alfraza.app.helpers.listener.ShakeEventListener;
import com.alfraza.app.helpers.transformations.CircleTransform;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.fragments.FragmentCategory;
import com.alfraza.app.fragments.FragmentFeaturedNews;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.IntentData;
import com.alfraza.app.helpers.utilities.CallbackDialog;
import com.alfraza.app.helpers.utilities.DialogUtils;

import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;

import static com.alfraza.app.helpers.utilities.Constants.EXTRA_FROM_NOTIF;
import static com.alfraza.app.helpers.utilities.Constants.EXTRA_OBJECT_ID;
import static com.alfraza.app.helpers.utilities.Tools.showDialogAbout;

public class ActivityStore extends AppCompatActivity {

    private Session session;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private CardView search_bar;
    private SwipeRefreshLayout swipe_refresh;
    private NavigationView nav_view;
    private View header;
    private AlertDialog notfmsgdialog;
    private boolean firstStart = true;
    private boolean notification = false;
    private Dialog dialog_failed = null;
    public boolean category_load = false, news_load = false;
    private Call<ServerResponse> response;

    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;

    @SuppressLint("StaticFieldLeak")
    static ActivityStore activityStore;

    public static ActivityStore getInstance() {
        return activityStore;
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
        session.initSession();
        setContentView(R.layout.activity_store);
        activityStore = this;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {
                lunchInstruction();
            }
        });
        initToolbar();
        initDrawerMenu();
        processIntent(getIntent());
        initFragment();
        swipeProgress(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            initComponent();
        } else if (extras.getBoolean("NotiClick")) {
            notification = true;
            initComponent();
            String ntype = intent.getStringExtra("ntype");
            String naction = intent.getStringExtra("naction");
            String msg = intent.getStringExtra("nmessage");
            switch (ntype) {
                case "home":
                    if (!msg.isEmpty()) {
                        msg = String.valueOf(ITUtilities.fromHtml(msg));
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                                .setIcon(R.mipmap.ic_launcher)
                                .setTitle(getString(R.string.new_notification_alert))
                                .setMessage(msg);
                        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dlg, int keyCode, KeyEvent event) {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    dlg.dismiss();
                                }
                                return true;
                            }
                        });
                        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                dialog.dismiss();
                                // dialog dismiss without button press
                            }
                        });
                        notfmsgdialog = dialog.create();
                        notfmsgdialog.show();
                    } else {
                        finish();
                    }
                    break;
                case "item":
                    if (!naction.isEmpty()) {
                        int item_id = Integer.valueOf(naction);
                        Intent i = new Intent(ActivityStore.this, ActivityProductDetails.class);
                        i.putExtra(EXTRA_OBJECT_ID, (long) item_id);
                        i.putExtra(EXTRA_FROM_NOTIF, true);
                        startActivity(i);
                    }
                    break;
                case "logout":
                    session.Logout();
                    break;
                case "url":
                    URL uri = null; // missing 'http://' will cause crashed
                    try {
                        uri = session.get_url(naction);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    if (uri != null) {
                        session.OpenLink(uri);
                    }
                    break;
            }
        } else initComponent();
    }

    private void checkLocation() {
        if (!notification) {
            session.checkArea();
        } else notification = false;

    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.app_name);
        }
    }

    private void initDrawerMenu() {
        nav_view = findViewById(R.id.nav_view);
        header = nav_view.getHeaderView(0);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
                onItemSelected(item.getItemId());
                //drawer.closeDrawers();
                return true;
            }
        });
        nav_view.setItemIconTintList(getResources().getColorStateList(R.color.nav_state_list));
        if (firstStart) firstStart = false;
        updateUi();
        setArea();
    }

    private void initFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // init fragment slider new product
        FragmentFeaturedNews fragmentFeaturedNews = new FragmentFeaturedNews();
        fragmentTransaction.replace(R.id.frame_content_new_product, fragmentFeaturedNews);
        // init fragment category
        FragmentCategory fragmentCategory = new FragmentCategory();
        fragmentTransaction.replace(R.id.frame_content_category, fragmentCategory);

        fragmentTransaction.commit();
    }

    private void lunchInstruction() {
        Intent intent = new Intent(this, ActivityInstruction.class);
        startActivity(intent);
    }

    private void changeArea() {
        Intent intent = new Intent(this, ActivityLocation.class);
        startActivity(intent);
        finish();
    }

    private void initComponent() {
        // launch instruction when first launch
        if (session.pref().isFirstLaunch() && !notification) {
            lunchInstruction();
            session.pref().setFirstLaunch(false);
        }
        search_bar = findViewById(R.id.search_bar);
        swipe_refresh = findViewById(R.id.swipe_refresh_layout);
        fab = findViewById(R.id.fab);
        NestedScrollView nested_content = findViewById(R.id.nested_content);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateFab(false);
                    animateSearchBar(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateFab(true);
                    animateSearchBar(true);
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ActivityShoppingCart.class);
                startActivity(i);
            }
        });

        // on swipe list
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFragment();
            }
        });

        findViewById(R.id.action_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivitySearch.navigate(ActivityStore.this);
            }
        });
    }

    private void refreshFragment() {
        category_load = false;
        news_load = false;
        swipeProgress(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initFragment();
            }
        }, 500);
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

    public void onItemSelected(int id) {
        switch (id) {
            //sub menu
            case R.id.btn_lang:
                session.changeLang();
                break;
            case R.id.nav_news:
                startActivity(new Intent(this, ActivityNewsInfo.class));
                return;
            case R.id.nav_about:
                showDialogAbout(this);
                break;
            case R.id.nav_cart:
                IntentData cartIntent = new IntentData(19, 0, 0, 0, 0, null, null, null, null);
                session.NewActivity(cartIntent, 0);
                break;
            case R.id.nav_wish:
                startActivity(new Intent(this, ActivityWishlist.class));
                return;
            case R.id.nav_orders:
                IntentData ordersIntent = new IntentData(15, 0, 0, 0, 0, "orders", "getorders", null, null);
                session.NewActivity(ordersIntent, 0);
                break;
            case R.id.nav_contact:
                IntentData contactIntent = new IntentData(10, 0, 0, 0, 0, null, null, null, null);
                session.NewActivity(contactIntent, 0);
                break;
            case R.id.nav_share:
                ITUtilities.shareIntent(this);
                break;
            case R.id.nav_login:
                IntentData LoginIntent = new IntentData(28, 0, 0, 0, 0, null, null, null, null);
                session.NewActivity(LoginIntent, 0);
                break;
            case R.id.nav_account:
                IntentData profileIntent = new IntentData(9, 0, 0, 0, 0, null, null, null, null);
                session.NewActivity(profileIntent, 0);
                break;
            case R.id.nav_notifications:
                startActivity(new Intent(this, ActivityNotifications.class));
                return;
            case R.id.nav_logout:
                session.Logout();
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawers();
    }

    boolean isFabHide = false;

    private void animateFab(final boolean hide) {
        if (isFabHide && hide || !isFabHide && !hide) return;
        isFabHide = hide;
        int moveY = hide ? (2 * fab.getHeight()) : 0;
        fab.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    boolean isSearchBarHide = false;

    private void animateSearchBar(final boolean hide) {
        if (isSearchBarHide && hide || !isSearchBarHide && !hide) return;
        isSearchBarHide = hide;
        int moveY = hide ? -(2 * search_bar.getHeight()) : 0;
        search_bar.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean cont = true;
        if (session.pref().IsUserlogged()) {
            String userType = session.pref().GetUsertype();
            if (userType != null && !userType.isEmpty() && (userType.equals("admin") || userType.equals("seller")))
                cont = false;
        }
        if (cont) {
            if (!firstStart) updateUi();
            checkLocation();
            mSensorManager.registerListener(mSensorListener,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_UI);
        } else session.goHome();
    }

    static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onPause() {
        if (notfmsgdialog != null && notfmsgdialog.isShowing()) notfmsgdialog.dismiss();
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.destroySession();
        if (response != null && response.isExecuted()) response.cancel();
        active = false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (count != 0)
                getSupportFragmentManager().popBackStack();
            else
                doExitApp();
        }
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else finishAffinity();
    }

    public void showDataLoaded() {
        if (category_load && news_load) {
            swipeProgress(false);
        }
    }

    public void showDialogFailed(@StringRes int msg) {
        if (dialog_failed != null && dialog_failed.isShowing()) return;
        swipeProgress(false);
        dialog_failed = new DialogUtils(this).buildDialogWarning(-1, msg, R.string.TRY_AGAIN, R.drawable.img_no_connect, new CallbackDialog() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();
                refreshFragment();
            }

            @Override
            public void onNegativeClick(Dialog dialog) {
            }
        });
        dialog_failed.show();
    }

    private void setArea() {
        if (nav_view != null) {
            Menu menu = nav_view.getMenu();
            try {
                MenuItem areaMenuItem = menu.findItem(R.id.nav_area);
                RelativeLayout rootView = (RelativeLayout) areaMenuItem.getActionView();
                TextView your_area = rootView.findViewById(R.id.your_area);
                String cityName = session.pref().getCityName();
                if (cityName != null && !cityName.isEmpty())
                    your_area.setText(cityName);
                AppCompatButton btn_change_area = rootView.findViewById(R.id.btn_change_area);
                btn_change_area.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeArea();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateUi() {
        if (nav_view != null) {
            Menu menu = nav_view.getMenu();
            try {
                TextView navTitle = header.findViewById(R.id.title);
                ImageView profile = header.findViewById(R.id.imv_profile);
                MenuItem orders = menu.findItem(R.id.nav_orders);
                MenuItem login = menu.findItem(R.id.nav_login);
                MenuItem logout = menu.findItem(R.id.nav_logout);
                MenuItem account = menu.findItem(R.id.nav_account);
                MenuItem notifications = menu.findItem(R.id.nav_notifications);
                if (session.pref().IsUserlogged()) {
                    if (session.pref().GetUserSname() != null)
                        navTitle.setText(session.pref().GetUserSname());
                    else navTitle.setText(session.pref().GetUsername());
                    if (login.isVisible()) login.setVisible(false);
                    if (!orders.isVisible()) orders.setVisible(true);
                    if (!logout.isVisible()) logout.setVisible(true);
                    if (!account.isVisible()) account.setVisible(true);
                    if (!notifications.isVisible()) notifications.setVisible(true);
                    profile.setVisibility(View.VISIBLE);
                    ITUtilities.loadImg(this)
                            .load(session.pref().GetUserpic())
                            .transform(new CircleTransform())
                            .error(R.drawable.ic_error)
                            .into(profile);
                    getCartCount();
                } else {
                    profile.setVisibility(View.GONE);
                    navTitle.setText(getString(R.string.app_name));
                    if (orders.isVisible()) orders.setVisible(false);
                    if (logout.isVisible()) logout.setVisible(false);
                    if (account.isVisible()) account.setVisible(false);
                    if (notifications.isVisible()) notifications.setVisible(false);
                    if (!login.isVisible()) login.setVisible(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getCartCount() {
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.STORE_TYPE);
        request.setSubType(Constants.CART_TYPE);
        request.setUser(session.getUserInfo());
        response = session.api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null && resp.getResult().equals(Constants.SUCCESS)) {
                    try {
                        if (resp.cart != null) {
                            if (nav_view != null) {
                                Menu menu = nav_view.getMenu();
                                // update cart counter
                                int cart_count = resp.cart.count;
                                ((TextView) menu.findItem(R.id.nav_cart).getActionView().findViewById(R.id.counter)).setText(String.valueOf(cart_count));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (resp != null && resp.getResult().equals(Constants.LOGOUT)) {
                    try {
                        session.Logout();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                getCartCount();
            }
        });
    }
}
