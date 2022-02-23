package com.alfraza.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.alfraza.app.models.MiscData;
import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alfraza.app.adapter.AdapterShoppingCart;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.Cart;
import com.alfraza.app.models.CartItems;
import com.alfraza.app.models.IntentData;
import com.alfraza.app.helpers.utilities.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ActivityShoppingCart extends AppCompatActivity {

    private View parent_view;
    private Session session;
    private Call<ServerResponse> response;
    private AdapterShoppingCart adapter;
    private Cart cart;
    private TextView price_total;
    private LinearLayout price_layout;
    private AppCompatEditText promo_code;

    private boolean firstStart = true;

    // override the base context of application to update default locale for this activity
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(this);
        setContentView(R.layout.activity_shopping_cart);
        initToolbar();
        processIntent(getIntent());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (response != null && response.isExecuted()) response.cancel();
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
        List<CartItems> items = new ArrayList<>();
        parent_view = findViewById(android.R.id.content);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        price_layout = findViewById(R.id.price_layout);
        MaterialRippleLayout lyt_chk_out = findViewById(R.id.lyt_chk_out);
        price_total = findViewById(R.id.price_total);
        promo_code = findViewById(R.id.promo_code);
        promo_code.setText(session.pref().GetPromo());
        AppCompatButton apply_promo_btn = findViewById(R.id.apply_promo_btn);
        apply_promo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.hideKeyboard(getCurrentFocus());
                if (promo_code != null && promo_code.getText() != null)
                    session.pref().SetPromo(promo_code.getText().toString());
                updateCart();
            }
        });
        lyt_chk_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getItemCount() > 0) {
                    Intent intent = new Intent(ActivityShoppingCart.this, ActivityCheckout.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("cart_data", cart);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else
                    Snackbar.make(parent_view, R.string.msg_cart_empty, Snackbar.LENGTH_SHORT).show();
            }
        });
        adapter = new AdapterShoppingCart(ActivityShoppingCart.this, true, items);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        adapter.setOnItemClickListener(new AdapterShoppingCart.OnItemClickListener() {
            @Override
            public void onItemClick(View view, CartItems obj) {
                dialogCartAction(obj);
            }
        });
        updateCart();
    }

    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.title_activity_cart);
            Tools.systemBarLolipop(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_shopping_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed();
        } else if (item_id == R.id.action_delete) {
            if (adapter.getItemCount() == 0) {
                Snackbar.make(parent_view, R.string.msg_cart_empty, Snackbar.LENGTH_SHORT).show();
                return true;
            }
            dialogDeleteConfirmation();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!session.pref().IsUserlogged()) finish();
        if (!firstStart) updateCart();
    }

    public void updateCart() {
        String promo = promo_code != null && promo_code.getText() != null ? promo_code.getText().toString() : "";
        ServerRequest request = new ServerRequest();
        MiscData misc = new MiscData();
        misc.setPromoCode(promo);
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.CART_TYPE);
        request.setSubType(Constants.GET_TYPE);
        request.setUser(session.getUserInfo());
        request.setMisc(misc);
        response = session.api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null && resp.getResult().equals(Constants.SUCCESS)) {
                    if (firstStart) firstStart = false;
                    cart = resp.cart;
                    List<CartItems> items = cart.data != null && cart.data.size() > 0 ? cart.data : new ArrayList<CartItems>();
                    adapter.setItems(items);
                    View lyt_no_item = findViewById(R.id.lyt_no_item);
                    if (adapter.getItemCount() == 0) {
                        price_layout.setVisibility(View.GONE);
                        lyt_no_item.setVisibility(View.VISIBLE);
                    } else {
                        price_layout.setVisibility(View.VISIBLE);
                        lyt_no_item.setVisibility(View.GONE);
                    }
                    setTotalPrice(resp.cart);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    updateCart();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void setTotalPrice(Cart item) {
        TextView discount = findViewById(R.id.discount_value);
        TextView promo_error = findViewById(R.id.promo_error);
        TextView delivery = findViewById(R.id.price_delivery);
        TextView sub_total = findViewById(R.id.price_sub_total);
        delivery.setText(item.delivery);
        sub_total.setText(item.subtotal);
        price_total.setText(item.total);
        if (item.discount != null && !item.discount.isEmpty() && adapter.getItemCount() != 0) {
            discount.setText(String.format("%s %s %s", getString(R.string.discount), item.discount, item.currency));
            discount.setVisibility(View.VISIBLE);
        } else discount.setVisibility(View.GONE);
        if (item.promo_error != null && !item.promo_error.isEmpty()) {
            session.pref().SetPromo("");
            promo_error.setText(item.promo_error);
            promo_error.setVisibility(View.VISIBLE);
        } else promo_error.setVisibility(View.GONE);
    }

    private void dialogCartAction(final CartItems model) {
        final Dialog dialog = new Dialog(ActivityShoppingCart.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_cart_option);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        if (dialog.getWindow() != null) {
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        ((TextView) dialog.findViewById(R.id.title)).setText(model.product_name);
        if (model.a_stock != 0)
            ((TextView) dialog.findViewById(R.id.stock)).setText(getString(R.string.stock) + model.stock);
        final TextView qty = dialog.findViewById(R.id.quantity);
        qty.setText(model.amount + "");

        dialog.findViewById(R.id.img_decrease).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.amount > 1) {
                    model.amount = model.amount - 1;
                    qty.setText(model.amount + "");
                }
            }
        });
        dialog.findViewById(R.id.img_increase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.a_stock == 0 || model.amount < model.stock) {
                    model.amount = model.amount + 1;
                    qty.setText(model.amount + "");
                }
            }
        });
        dialog.findViewById(R.id.bt_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.editCart(model.product_id, model.amount, model.ftid);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.bt_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.delCart(model.product_id, model.ftid);
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void dialogDeleteConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_delete_confirm);
        builder.setMessage(getString(R.string.content_delete_confirm) + getString(R.string.title_activity_cart));
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int i) {
                di.dismiss();
                session.emptyCart();
            }
        });
        builder.setNegativeButton(R.string.CANCEL, null);
        builder.show();
    }

}
