package com.alfraza.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.models.FormInputData;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alfraza.app.adapter.AdapterShoppingCart;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.BuyerProfile;
import com.alfraza.app.models.Cart;
import com.alfraza.app.models.CartItems;
import com.alfraza.app.helpers.utilities.CallbackDialog;
import com.alfraza.app.helpers.utilities.DialogUtils;
import com.alfraza.app.helpers.utilities.Tools;
import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ActivityCheckout extends AppCompatActivity {

    private Session session;
    private View parent_view;
    private CardView notes_card, time_slot_card;
    private LinearLayout time_slot_lyt;
    private RecyclerView recyclerView;
    private TextView order_notes, order_time_label, order_time, total_order, price_delivery, price_discount, total_fees;
    private TextInputLayout buyer_name_lyt, email_lyt, phone_lyt, street_lyt, building_lyt, floor_lyt, apartment_lyt;
    private EditText buyer_name, email, phone, street, building, floor, apartment, additional, comment;

    private Call<ServerResponse> response;
    private List<CartItems> items;
    private Cart cart;
    private int time_slot;
    private BuyerProfile buyerProfile;
    // construct dialog progress
    ProgressDialog progressDialog = null;


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        setContentView(R.layout.activity_checkout);
        items = new ArrayList<>();
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            cart = bundle.getParcelable("cart_data");
            if (cart != null) items = cart.data;
        }
        initToolbar();
        iniComponent();
    }

    private void initToolbar() {
        ActionBar actionBar;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(R.string.title_activity_checkout);
            Tools.systemBarLolipop(this);
        }
    }

    private void iniComponent() {
        parent_view = findViewById(android.R.id.content);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MaterialRippleLayout lyt_add_cart = findViewById(R.id.lyt_add_cart);
        notes_card = findViewById(R.id.notes_card);
        order_notes = findViewById(R.id.order_notes);
        time_slot_card = findViewById(R.id.time_slot_card);
        time_slot_lyt = findViewById(R.id.time_slot_lyt);
        order_time_label = findViewById(R.id.order_time_label);
        order_time = findViewById(R.id.order_time);
        // cost view
        total_order = findViewById(R.id.total_order);
        price_delivery = findViewById(R.id.price_delivery);
        price_discount = findViewById(R.id.price_discount);
        total_fees = findViewById(R.id.total_fees);

        // form view
        buyer_name = findViewById(R.id.buyer_name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        street = findViewById(R.id.street);
        building = findViewById(R.id.building);
        floor = findViewById(R.id.floor);
        apartment = findViewById(R.id.apartment);
        additional = findViewById(R.id.additional);
        comment = findViewById(R.id.comment);

        buyer_name.addTextChangedListener(new CheckoutTextWatcher(buyer_name));
        email.addTextChangedListener(new CheckoutTextWatcher(email));
        phone.addTextChangedListener(new CheckoutTextWatcher(phone));
        street.addTextChangedListener(new CheckoutTextWatcher(street));
        building.addTextChangedListener(new CheckoutTextWatcher(building));
        floor.addTextChangedListener(new CheckoutTextWatcher(floor));
        apartment.addTextChangedListener(new CheckoutTextWatcher(apartment));

        buyer_name_lyt = findViewById(R.id.buyer_name_lyt);
        email_lyt = findViewById(R.id.email_lyt);
        phone_lyt = findViewById(R.id.phone_lyt);
        street_lyt = findViewById(R.id.street_lyt);
        building_lyt = findViewById(R.id.building_lyt);
        floor_lyt = findViewById(R.id.floor_lyt);
        apartment_lyt = findViewById(R.id.apartment_lyt);
        progressDialog = new ProgressDialog(ActivityCheckout.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(R.string.title_please_wait);
        progressDialog.setMessage(getString(R.string.content_submit_checkout));

        lyt_add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayData();
    }

    private void displayData() {
        AdapterShoppingCart adapter = new AdapterShoppingCart(this, false, items);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        if (cart.notes != null && !cart.notes.isEmpty()) {
            order_notes.setText(ITUtilities.fromHtml(cart.notes));
            notes_card.setVisibility(View.VISIBLE);
        } else notes_card.setVisibility(View.GONE);
        if (cart.time_label != null && !cart.time_label.isEmpty() && ((cart.time != null && !cart.time.isEmpty()) || cart.radio != null && cart.radio.size() > 0)) {
            order_time_label.setText(ITUtilities.fromHtml(cart.time_label));
            if (cart.time != null && !cart.time.isEmpty()) {
                order_time.setText(ITUtilities.fromHtml(cart.time));
                order_time.setVisibility(View.VISIBLE);
            } else order_time.setVisibility(View.GONE);
            if (cart.radio != null && cart.radio.size() > 0) {
                int i = 0;
                RadioGroup radioGroup = new RadioGroup(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                radioGroup.setLayoutParams(params);
                for (FormInputData radio_input : cart.radio) {
                    i++;
                    // Create RadioButton Dynamically
                    RadioButton radioButton = new RadioButton(this);
                    radioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    radioButton.setText(ITUtilities.fromHtml(radio_input.getText()));
                    radioButton.setId(radio_input.getId());
                    if (i == 1) {
                        radioButton.setChecked(true);
                        time_slot = radio_input.getId();
                    }
                    radioGroup.addView(radioButton);
                }
                time_slot_lyt.removeAllViews();
                time_slot_lyt.addView(radioGroup);

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        time_slot = checkedId;
                    }
                });

            } else time_slot_lyt.setVisibility(View.GONE);
            time_slot_card.setVisibility(View.VISIBLE);
        } else time_slot_card.setVisibility(View.GONE);
        setTotalPrice();
        buyer_name.setText(session.pref().GetUsername());
        email.setText(session.pref().GetUserEmail());
        phone.setText(session.pref().GetUserPhone());
        String str = session.pref().GetUserStreet();
        if (str != null && !str.isEmpty())
            street.setText(str);
        String build = session.pref().GetUserBuilding();
        if (build != null && !build.isEmpty())
            building.setText(build);
        String flr = session.pref().GetUserFloor();
        if (flr != null && !flr.isEmpty() && !flr.equals("0"))
            floor.setText(flr);
        String apr = session.pref().GetUserApartment();
        if (apr != null && !apr.isEmpty())
            apartment.setText(apr);
        String add = session.pref().GetUserAdditional();
        if (add != null && !add.isEmpty())
            additional.setText(add);
    }

    private void setTotalPrice() {
        // set to display
        total_order.setText(cart.subtotal);
        price_delivery.setText(cart.delivery);
        LinearLayout discount_layout = findViewById(R.id.discount_layout);
        if (cart.discount != null && !cart.discount.isEmpty()) {
            price_discount.setText(cart.discount);
            discount_layout.setVisibility(View.VISIBLE);
        } else discount_layout.setVisibility(View.GONE);
        total_fees.setText(cart.total);
    }


    private void submitForm() {
        if (!validateName()) {
            Snackbar.make(parent_view, R.string.invalid_name, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!validateEmail()) {
            Snackbar.make(parent_view, R.string.invalid_email, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!validatePhone()) {
            Snackbar.make(parent_view, R.string.invalid_phone, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!validateStreet()) {
            Snackbar.make(parent_view, R.string.invalid_street, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!validateBuilding()) {
            Snackbar.make(parent_view, R.string.invalid_building, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!validateFloor()) {
            Snackbar.make(parent_view, R.string.invalid_floor, Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!validateApartment()) {
            Snackbar.make(parent_view, R.string.invalid_apartment, Snackbar.LENGTH_SHORT).show();
            return;
        }

        buyerProfile = new BuyerProfile();
        buyerProfile.name = buyer_name.getText().toString();
        buyerProfile.email = email.getText().toString();
        buyerProfile.phone = phone.getText().toString();
        buyerProfile.street = street.getText().toString();
        buyerProfile.building = building.getText().toString();
        buyerProfile.floor = floor.getText().toString();
        buyerProfile.apartment = apartment.getText().toString();
        buyerProfile.additional = additional.getText().toString();
        buyerProfile.comment = comment.getText().toString().trim();
        buyerProfile.promo_code = cart.promo_code;
        buyerProfile.time_slot = time_slot;

        // hide keyboard
        hideKeyboard();

        // show dialog confirmation
        dialogConfirmCheckout();
    }

    private void submitOrderData() {
        // prepare checkout data
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.CART_TYPE);
        request.setSubType(Constants.CHECK_OUT_TYPE);
        request.setUser(session.getUserInfo());
        request.setBuyer(buyerProfile);
        // submit data to server
        response = session.api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null && resp.getResult().equals(Constants.SUCCESS)) {
                    dialogSuccess(resp.getMessage(), resp.getNotes());
                } else dialogFailedRetry();

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                if (!call.isCanceled()) dialogFailedRetry();
            }
        });
    }

    // give delay when submit data to give good UX
    private void delaySubmitOrderData() {
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                submitOrderData();
            }
        }, 2000);
    }

    public void dialogConfirmCheckout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmation);
        builder.setMessage(getString(R.string.confirm_checkout));
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delaySubmitOrderData();
            }
        });
        builder.setNegativeButton(R.string.NO, null);
        builder.show();
    }

    public void dialogFailedRetry() {
        progressDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.failed);
        builder.setMessage(getString(R.string.failed_checkout));
        builder.setPositiveButton(R.string.TRY_AGAIN, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                delaySubmitOrderData();
            }
        });
        builder.show();
    }

    public void dialogSuccess(String code, String sMsg) {
        progressDialog.dismiss();
        String msg = String.format(getString(R.string.msg_success_checkout), code);
        if (sMsg != null && !sMsg.isEmpty()) msg = sMsg;
        Dialog dialog = new DialogUtils(this).buildDialogInfo(
                getString(R.string.success_checkout),
                msg,
                getString(R.string.OK),
                R.drawable.img_checkout_success,
                new CallbackDialog() {
                    @Override
                    public void onPositiveClick(Dialog dialog) {
                        dialog.dismiss();
                        session.UpdateUserInfo();
                        startActivity(new Intent(ActivityCheckout.this, ActivityOrderHistory.class));
                        finish();
                    }

                    @Override
                    public void onNegativeClick(Dialog dialog) {
                    }
                });
        dialog.show();
    }

    // validation method
    private boolean validateEmail() {
        String str = email.getText().toString().trim();
        if (str.isEmpty() || !session.isValidEmail(str)) {
            email_lyt.setError(getString(R.string.invalid_email));
            requestFocus(email);
            return false;
        } else email_lyt.setErrorEnabled(false);
        return true;
    }

    private boolean validateName() {
        String str = buyer_name.getText().toString().trim();
        if (str.isEmpty()) {
            buyer_name_lyt.setError(getString(R.string.invalid_name));
            requestFocus(buyer_name);
            return false;
        } else {
            buyer_name_lyt.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePhone() {
        String str = phone.getText().toString().trim();
        if (str.isEmpty()) {
            phone_lyt.setError(getString(R.string.invalid_phone));
            requestFocus(phone);
            return false;
        } else phone_lyt.setErrorEnabled(false);
        return true;
    }

    private boolean validateStreet() {
        String str = street.getText().toString().trim();
        if (str.isEmpty()) {
            street_lyt.setError(getString(R.string.invalid_street));
            requestFocus(street);
            return false;
        } else {
            street_lyt.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateBuilding() {
        String str = building.getText().toString().trim();
        if (str.isEmpty()) {
            building_lyt.setError(getString(R.string.invalid_building));
            requestFocus(building);
            return false;
        } else {
            building_lyt.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateFloor() {
        String str = floor.getText().toString().trim();
        if (str.isEmpty()) {
            floor_lyt.setError(getString(R.string.invalid_floor));
            requestFocus(floor);
            return false;
        } else {
            floor_lyt.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateApartment() {
        String str = apartment.getText().toString().trim();
        if (str.isEmpty()) {
            apartment_lyt.setError(getString(R.string.invalid_apartment));
            requestFocus(apartment);
            return false;
        } else {
            apartment_lyt.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus())
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private class CheckoutTextWatcher implements TextWatcher {
        private View view;

        private CheckoutTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.email:
                    validateEmail();
                    break;
                case R.id.name:
                    validateName();
                    break;
                case R.id.phone:
                    validatePhone();
                    break;
                case R.id.street:
                    validateStreet();
                    break;
                case R.id.building:
                    validateBuilding();
                    break;
                case R.id.floor:
                    validateFloor();
                    break;
                case R.id.apartment:
                    validateApartment();
                    break;
            }
        }
    }
}
