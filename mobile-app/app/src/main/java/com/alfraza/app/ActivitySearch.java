package com.alfraza.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alfraza.app.adapter.AdapterProduct;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.Category;
import com.alfraza.app.models.ItemData;
import com.alfraza.app.models.MiscData;
import com.alfraza.app.helpers.utilities.NetworkCheck;
import com.alfraza.app.helpers.utilities.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.alfraza.app.helpers.utilities.Constants.EXTRA_FROM_NOTIF;
import static com.alfraza.app.helpers.utilities.Constants.EXTRA_OBJECT_ID;

public class ActivitySearch extends AppCompatActivity {

    private static final String EXTRA_CATEGORY_ID = "key.EXTRA_CATEGORY_ID";
    private static final String EXTRA_CATEGORY_NAME = "key.EXTRA_CATEGORY_NAME";
    private Call<ServerResponse> response;
    private Session session;

    // activity transition
    public static void navigate(Activity activity, Category category) {
        Intent i = new Intent(activity, ActivitySearch.class);
        i.putExtra(EXTRA_CATEGORY_ID, category.getId());
        i.putExtra(EXTRA_CATEGORY_NAME, category.getName() + " (" + category.getBrief() + ")");
        activity.startActivity(i);
    }

    // activity transition
    public static void navigate(Activity activity) {
        Intent i = new Intent(activity, ActivitySearch.class);
        i.putExtra(EXTRA_CATEGORY_NAME, activity.getString(R.string.ALL));
        activity.startActivity(i);
    }

    private EditText et_search;
    private RecyclerView recyclerView;
    private AdapterProduct adapterProduct;
    private ImageButton bt_clear;
    private SwipeRefreshLayout swipe_refresh;

    private int post_total = 0;
    private int failed_page = 0;
    private long category_id = -1L;
    private String category_name;
    private String query = "";


    @Override
    public void onDestroy() {
        super.onDestroy();
        swipeProgress(false);
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
        setContentView(R.layout.activity_search);
        category_name = getString(R.string.ALL);
        category_id = getIntent().getLongExtra(EXTRA_CATEGORY_ID, 8000L);
        category_name = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);
        initComponent();
        setupToolbar();
    }

    private void initComponent() {
        swipe_refresh = findViewById(R.id.swipe_refresh_layout);
        et_search = findViewById(R.id.et_search);
        et_search.addTextChangedListener(textWatcher);

        bt_clear = findViewById(R.id.bt_clear);
        ((TextView) findViewById(R.id.category)).setText(String.format("%s%s", getString(R.string.Category), category_name));
        bt_clear.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, Tools.getGridSpanCount(this)));
        recyclerView.setHasFixedSize(true);
        //set data and list adapter
        adapterProduct = new AdapterProduct(this, recyclerView, new ArrayList<ItemData>());
        recyclerView.setAdapter(adapterProduct);
        adapterProduct.setOnItemClickListener(new AdapterProduct.OnItemClickListener() {
            @Override
            public void onItemClick(View v, ItemData obj, int pos) {
                Intent i = new Intent(ActivitySearch.this, ActivityProductDetails.class);
                i.putExtra(EXTRA_OBJECT_ID, (long) obj.getId());
                i.putExtra(EXTRA_FROM_NOTIF, false);
                startActivity(i);
            }
        });

        // detect when scroll reach bottom
        adapterProduct.setOnLoadMoreListener(new AdapterProduct.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int current_page) {
                if (post_total > adapterProduct.getItemCount() && current_page != 0) {
                    int next_page = current_page + 1;
                    requestAction(next_page);
                } else {
                    adapterProduct.setLoaded();
                }
            }
        });

        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
                adapterProduct.resetListData();
                showNoItemView(true);
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    searchAction();
                    return true;
                }
                return false;
            }
        });

        // on swipe list
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (response != null && response.isExecuted()) response.cancel();
                adapterProduct.resetListData();
                requestAction(1);
            }
        });
        showNoItemView(true);
    }

    private void searchAction() {
        query = et_search.getText().toString().trim();
        if (!query.equals("")) {
            adapterProduct.resetListData();
            // request action will be here
            requestAction(1);
        } else {
            Toast.makeText(this, R.string.please_fill, Toast.LENGTH_SHORT).show();
        }
    }

    private void requestAction(final int page_no) {
        showFailedView(false, "");
        showNoItemView(false);
        if (page_no == 1) {
            swipeProgress(true);
        } else {
            adapterProduct.setLoading();
        }

        // analytics track
        ThisApplication.getInstance().saveCustomLogEvent("SEARCH_PRODUCT", "keyword", query);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestListProduct(page_no);
            }
        }, 1000);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }


    private void requestListProduct(final int page_no) {
        MiscData misc = new MiscData();
        misc.setCatid((int) category_id);
        misc.setSearchq(query);
        misc.setPage(page_no);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.STORE_TYPE);
        request.setSubType(Constants.STORE_ITEMS_TYPE);
        request.setUser(session.getUserInfo());
        request.setMisc(misc);
        response = session.api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null && resp.getResult().equals(Constants.SUCCESS)) {
                    post_total = resp.total;
                    displayApiResult(resp.getStore().getItems());
                } else {
                    onFailRequest(page_no);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest(page_no);
            }

        });
    }

    private void displayApiResult(final List<ItemData> items) {
        adapterProduct.insertData(items);
        swipeProgress(false);
        if (items.size() == 0) showNoItemView(true);
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        adapterProduct.setLoaded();
        swipeProgress(false);
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.no_internet_text));
        }
    }


    @Override
    protected void onResume() {
        adapterProduct.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence c, int i, int i1, int i2) {
            if (c.toString().trim().length() == 0) {
                bt_clear.setVisibility(View.GONE);
            } else {
                bt_clear.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence c, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = findViewById(R.id.lyt_failed);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        findViewById(R.id.failed_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction(failed_page);
            }
        });
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = findViewById(R.id.lyt_no_item);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
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
}