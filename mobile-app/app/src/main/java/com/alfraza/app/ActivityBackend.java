package com.alfraza.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alfraza.app.activities.misc.ActivityMessages;
import com.alfraza.app.activities.misc.ActivityNotifications;
import com.alfraza.app.adapter.RecyclerAdapter;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.transformations.CircleTransform;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.ItemData;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class ActivityBackend extends AppCompatActivity {
    private Session session;
    private TextView ntfCount;
    private Call<ServerResponse> response;
    private ArrayList<ItemData> data;
    private RecyclerAdapter adapter;
    private RecyclerView recycler;
    private LinearLayoutManager layoutManager;
    private LinearLayout loading_overlay, warning_overlay;
    private AlertDialog progressDialog;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            UpdateNtfCount();
            handler.postDelayed(this, 2500); // for interval...
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        session.destroySession();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        if (response != null && response.isExecuted()) response.cancel();
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!session.pref().IsUserlogged()) session.goHome();
        else {
            session.checkArea();
            handler.postDelayed(runnable, 5000);
        }
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
        setContentView(R.layout.activity_backend);
        loading_overlay = findViewById(R.id.loading_overlay);
        loading_overlay.setVisibility(View.VISIBLE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar aBar = getSupportActionBar();
        if (aBar != null) aBar.setTitle(getString(R.string.backend_title));
        initViews();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onBackPressed() {
        doExitApp();
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else finishAffinity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        getMenuInflater().inflate(R.menu.genral, menu);
        MenuItem ntfItem = menu.findItem(R.id.menu_notification);
        FrameLayout ntfLayout = (FrameLayout) ntfItem.getActionView();
        ntfLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartNotificationsActivity();
            }
        });
        MenuItem msgItem = menu.findItem(R.id.menu_messages);
        if (session.pref().ChatAllowed() != 0) {
            FrameLayout msgLayout = (FrameLayout) msgItem.getActionView();
            msgLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    StartMessageActivity();
                }
            });
        } else msgItem.setVisible(false);
        ntfCount = ntfLayout.findViewById(R.id.notf_count);
        ntfCount.setVisibility(View.GONE);
        UpdateUi();
        return true;
    }

    private void initViews() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullParent = null;
        View view = inflater.inflate(R.layout.dialog_progress, nullParent);
        builder.setView(view);
        builder.setCancelable(false);
        progressDialog = builder.create();
        warning_overlay = findViewById(R.id.warning_overlay);
        recycler = findViewById(R.id.card_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        data = new ArrayList<>();
        adapter = new RecyclerAdapter(ActivityBackend.this, data, null);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        loadData();
    }

    public void UpdateUi() {
        try {
            TextView tv_name = findViewById(R.id.tv_name);
            if (session.pref().IsUserlogged()) {
                String name = session.pref().GetUsername();
                if (session.pref().GetUserSname() != null) name = session.pref().GetUserSname();
                tv_name.setText(String.format(getString(R.string.hello_user), name));
                if (session.pref().GetUserpic() != null) {
                    ImageView imv_profile = findViewById(R.id.imv_profile);
                    float resizeDp = 60f;
                    // Convert to pixels
                    int resizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, resizeDp, getResources().getDisplayMetrics());
                    session.imgLoader()
                            .load(session.pref().GetUserpic())
                            .error(R.drawable.ic_error)
                            .resize(resizePx, resizePx) // resize the image to these dimensions (in pixel)
                            .centerCrop()
                            .transform(new CircleTransform())
                            .into(imv_profile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UpdateNtfCount() {
        if (session.pref().IsUserlogged()) {
            if (ntfCount != null) {
                int ntfCount = session.pref().GetNotfCount();
                if (ntfCount == 0) this.ntfCount.setVisibility(View.GONE);
                else {
                    this.ntfCount.setText(String.format(Locale.ENGLISH, "%d", ntfCount));
                    this.ntfCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void StartNotificationsActivity() {
        startActivity(new Intent(this, ActivityNotifications.class));
    }

    private void StartMessageActivity() {
        startActivity(new Intent(this, ActivityMessages.class));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about_app) {
            Context context = this.getApplicationContext();
            String VersionCode = "1.0.0";
            try {
                VersionCode = Integer.toString(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            String about = String.format(getString(R.string.about_html), getString(R.string.app_name), VersionCode);
            String msg;
            msg = String.valueOf(ITUtilities.fromHtml(about));
            new AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getString(R.string.about_app))
                    .setMessage(msg)
                    .setPositiveButton(getString(R.string.cancelbtn), null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        try {
            if (loading_overlay.getVisibility() == View.GONE)
                loading_overlay.setVisibility(View.VISIBLE);
            if (warning_overlay.getVisibility() == View.VISIBLE)
                warning_overlay.setVisibility(View.GONE);
            if (progressDialog != null) progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent("home");
        request.setUser(session.getUserInfo());
        response = session.api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        switch (resp.getResult()) {
                            case Constants.SUCCESS:
                                try {
                                    loading_overlay.setVisibility(View.GONE);
                                    progressDialog.dismiss();
                                    data.clear();
                                    data.addAll(resp.getItemsListing());
                                    adapter = new RecyclerAdapter(ActivityBackend.this, data, resp.getLayoutInfo());
                                    if (resp.getLayoutInfo().getLayoutType() == 1) {
                                        int columns = resp.getLayoutInfo().getGridColoumn();
                                        recycler.setLayoutManager(new GridLayoutManager(ActivityBackend.this, columns));
                                    } else if (resp.getLayoutInfo().getLayoutType() == 2)
                                        recycler.setLayoutManager(layoutManager);
                                    recycler.setAdapter(adapter);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case Constants.LOGOUT:
                                try {
                                    session.Logout();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                try {
                                    progressDialog.dismiss();
                                    warning_overlay.setVisibility(View.VISIBLE);
                                    loading_overlay.setVisibility(View.GONE);
                                    String error = getString(R.string.error_failed_try_again);
                                    if (resp.getMessage() != null) error = resp.getMessage();
                                    showSnackBar(error, 1, 2);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                } else {
                    try {
                        progressDialog.dismiss();
                        warning_overlay.setVisibility(View.VISIBLE);
                        loading_overlay.setVisibility(View.GONE);
                        showSnackBar(getString(R.string.error_failed_later), 0, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    progressDialog.dismiss();
                    warning_overlay.setVisibility(View.VISIBLE);
                    loading_overlay.setVisibility(View.GONE);
                    showSnackBar(getString(R.string.internetcheck), 1, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showSnackBar(String msg, final int action, int length) {
        int SnLength = Snackbar.LENGTH_SHORT;
        switch (length) {
            case 1:
                SnLength = Snackbar.LENGTH_LONG;
                break;
            case 2:
                SnLength = Snackbar.LENGTH_INDEFINITE;
                break;
        }
        CoordinatorLayout view = findViewById(R.id.main_layout);
        Snackbar snackbar = Snackbar.make(view, msg, SnLength);
        if (action != 0) {
            snackbar.setAction(getString(R.string.reloadbtn), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadData();
                }
            });
            // Changing action button text color
            snackbar.setActionTextColor(Color.RED);
        }
        snackbar.show();
    }
}