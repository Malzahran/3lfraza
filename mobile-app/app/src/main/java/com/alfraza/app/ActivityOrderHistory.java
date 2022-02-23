package com.alfraza.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alfraza.app.adapter.AdapterOrderHistory;
import com.alfraza.app.adapter.AdapterShoppingCart;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.IntentData;
import com.alfraza.app.models.Order;
import com.alfraza.app.helpers.utilities.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class ActivityOrderHistory extends AppCompatActivity {

    private View parent_view;
    private AdapterOrderHistory adapter;
    private List<Order> orders;
    private Session session;
    private Call<ServerResponse> response;
    private boolean firstStart = true;

    @Override
    public void onDestroy() {
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
        setContentView(R.layout.activity_order_history);
        initToolbar();
        processIntent(getIntent());
    }

    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.title_activity_history);
            Tools.systemBarLolipop(this);
        }
    }

    private void processIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            if (session.pref().IsUserlogged()) iniComponent();
            else {
                IntentData loginIntent = new IntentData(28, 0, 0, 0, 0, null, null, null, null);
                session.NewActivity(loginIntent, 0);
            }
        } else {
            if (session.pref().IsUserlogged()) iniComponent();
            else {
                IntentData nIntent = new IntentData(28, 0, 0, 0, 0, null, null, null, null);
                session.NewActivity(nIntent, 0);
            }
        }
    }

    private void iniComponent() {
        orders = new ArrayList<>();
        parent_view = findViewById(android.R.id.content);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterOrderHistory(this, orders);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        adapter.setOnItemClickListener(new AdapterOrderHistory.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Order obj) {
                dialogOrderHistoryDetails(obj);
            }
        });
        displayData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!session.pref().IsUserlogged()) finish();
        if (!firstStart) displayData();
    }

    private void displayData() {
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.ORDERS_TYPE);
        request.setSubType(Constants.GET_TYPE);
        request.setUser(session.getUserInfo());
        response = session.api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null && resp.getResult().equals(Constants.SUCCESS)) {
                    if (firstStart) firstStart = false;
                    orders = resp.orders;
                    adapter.setItems(orders);
                    View lyt_no_item = findViewById(R.id.lyt_no_item);
                    if (adapter.getItemCount() == 0) {
                        lyt_no_item.setVisibility(View.VISIBLE);
                    } else {
                        lyt_no_item.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    displayData();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void dialogOrderHistoryDetails(final Order order) {
        final Dialog dialog = new Dialog(ActivityOrderHistory.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_order_history_details);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AdapterShoppingCart _adapter = new AdapterShoppingCart(this, false, order.cart_list);
        recyclerView.setAdapter(_adapter);
        recyclerView.setNestedScrollingEnabled(false);
        dialog.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.code)).setText(order.code);
        dialog.findViewById(R.id.copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tools.copyToClipboard(getApplicationContext(), order.code);
            }
        });
        TextView notes = dialog.findViewById(R.id.notes);
        if (order.notes != null && !order.notes.isEmpty()) {
            notes.setVisibility(View.VISIBLE);
            notes.setText("* " + order.notes);
        } else notes.setVisibility(View.GONE);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}
