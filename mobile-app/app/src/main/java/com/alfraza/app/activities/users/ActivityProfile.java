package com.alfraza.app.activities.users;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alfraza.app.R;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.transformations.CircleTransform;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.IntentData;
import com.alfraza.app.models.ItemData;
import com.alfraza.app.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.alfraza.app.helpers.utilities.Constants.CHARGE_TYPE;

public class ActivityProfile extends AppCompatActivity implements View.OnClickListener {
    private Session session;
    private Call<ServerResponse> response;
    private TextView tv_message;
    private EditText et_old_password, et_new_password;
    private LinearLayout loading_overlay, warning_overlay, buttons_layout;
    private AlertDialog dialog;
    private ProgressBar progress;
    private MenuItem acProgress;

    @Override
    public void onDestroy() {
        super.onDestroy();
        session.destroySession();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (response != null && response.isExecuted()) response.cancel();
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
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
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar aBar = getSupportActionBar();
        if (aBar != null) {
            aBar.setDisplayHomeAsUpEnabled(true);
            aBar.setTitle(getString(R.string.menu_profile));
        }
        loading_overlay = findViewById(R.id.loading_overlay);
        loading_overlay.setVisibility(View.VISIBLE);
        initViews();
    }

    private void initViews() {
        warning_overlay = findViewById(R.id.warning_overlay);
        buttons_layout = findViewById(R.id.buttons_layout);
        TextView tv_name = findViewById(R.id.tv_name);
        TextView tv_username = findViewById(R.id.tv_username);
        TextView tv_credit = findViewById(R.id.tv_credit);
        ImageView imv_profile = findViewById(R.id.imv_profile);
        AppCompatButton btn_charge = findViewById(R.id.btn_charge);
        AppCompatButton btn_change_password = findViewById(R.id.btn_chg_password);
        AppCompatButton btn_logout = findViewById(R.id.btn_logout);
        tv_name.setText(String.format(getString(R.string.hello_user), session.pref().GetUsername()));
        if (session.pref().GetAuthMethod() != null && session.pref().GetAuthMethod().equals(Constants.LOGIN_FORM)) {
            tv_username.setText(String.format("%s : %s", getString(R.string.user_name), session.pref().GetUseruname()));
            tv_username.setVisibility(View.VISIBLE);
            btn_change_password.setVisibility(View.VISIBLE);
            btn_change_password.setOnClickListener(this);
        }

        if (session.pref().CreditAllowed() != 0) {
            tv_credit.setText(String.format("%s %s", getString(R.string.credit_label), session.pref().GetUserBalance()));
            tv_credit.setVisibility(View.VISIBLE);
            btn_charge.setVisibility(View.VISIBLE);
            btn_charge.setOnClickListener(this);
        }

        if (session.pref().GetUserpic() != null) {
            float resizeDp = 100f;
            // Convert to pixels
            int resizePx = ITUtilities.DpToPx(this.getApplicationContext(), resizeDp);
            try {
                session.imgLoader()
                        .load(session.pref().GetUserpic())
                        .error(R.mipmap.ic_launcher)
                        .resize(resizePx, resizePx) // resize the image to these dimensions (in pixel)
                        .centerCrop()
                        .transform(new CircleTransform())
                        .into(imv_profile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        btn_logout.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_inside, menu);
        MenuItem homeItem = menu.findItem(R.id.menu_home);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        acProgress = menu.findItem(R.id.action_progress);
        searchItem.setVisible(false);
        homeItem.setVisible(false);
        acProgress.setVisible(false);
        loadButtons();
        return true;
    }


    private void loadButtons() {
        try {
            acProgress.setVisible(true);
            if (warning_overlay.getVisibility() == View.VISIBLE) {
                loading_overlay.setVisibility(View.VISIBLE);
                warning_overlay.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.PROFILE_TYPE);
        request.setUser(session.getUserInfo());
        response = session.api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        if (resp.getResult().equals(Constants.SUCCESS)) {
                            try {
                                acProgress.setVisible(false);
                                if (loading_overlay.getVisibility() == View.VISIBLE)
                                    loading_overlay.setVisibility(View.GONE);
                                List<ItemData> items = resp.getItemsListing();
                                if (items.size() > 0) {
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    float marginDp = 10f;
                                    params.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginDp, getResources().getDisplayMetrics());
                                    for (final ItemData item : items) {
                                        AppCompatButton appCompatButton = new AppCompatButton(ActivityProfile.this);
                                        appCompatButton.setLayoutParams(params);
                                        appCompatButton.setText(item.getTitle());
                                        int resources = R.drawable.ripple_effect_dark;
                                        if (item.getColor() != 0) {
                                            switch (item.getColor()) {
                                                case 1:
                                                    resources = R.drawable.button_red;
                                                    break;
                                                case 2:
                                                    resources = R.drawable.button_orange;
                                                    break;
                                            }
                                        }
                                        appCompatButton.setBackgroundResource(resources);
                                        appCompatButton.setTextColor(ContextCompat.getColor(ActivityProfile.this, R.color.White));
                                        appCompatButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (item.getActionintent() != null)
                                                    session.NewActivity(item.getActionintent(), 0);
                                            }
                                        });
                                        buttons_layout.addView(appCompatButton);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                acProgress.setVisible(false);
                                warning_overlay.setVisibility(View.VISIBLE);
                                String error = getString(R.string.error_failed_try_again);
                                if (resp.getMessage() != null) error = resp.getMessage();
                                showSnackBar(error, 1, 2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    try {
                        acProgress.setVisible(false);
                        warning_overlay.setVisibility(View.VISIBLE);
                        showSnackBar(getString(R.string.error_failed_later), 0, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    acProgress.setVisible(false);
                    warning_overlay.setVisibility(View.VISIBLE);
                    showSnackBar(getString(R.string.internetcheck), 1, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final ViewGroup nullParent = null;
        View view = inflater.inflate(R.layout.dialog_change_password, nullParent);
        et_old_password = view.findViewById(R.id.et_old_password);
        et_new_password = view.findViewById(R.id.et_new_password);
        tv_message = view.findViewById(R.id.tv_message);
        progress = view.findViewById(R.id.progress);
        builder.setView(view);
        builder.setTitle(getString(R.string.change_pass));
        builder.setPositiveButton(getString(R.string.btn_change), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(getString(R.string.cancelbtn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old_password = et_old_password.getText().toString();
                String new_password = et_new_password.getText().toString();
                if (!old_password.isEmpty() && !new_password.isEmpty()) {
                    session.hideKeyboard(dialog.getCurrentFocus());
                    progress.setVisibility(View.VISIBLE);
                    changePasswordProcess(session.pref().GetUseruname()
                            , session.pref().GetUserpass()
                            , old_password, new_password);
                } else {
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(getString(R.string.fieldsempty));
                }
            }
        });
        float paddingDp = 5f;
        // Convert to pixels
        int paddingPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDp, getResources().getDisplayMetrics());
        Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        pbutton.setTextColor(ContextCompat.getColor(this, R.color.White));
        pbutton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
        Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        nbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        nbutton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_chg_password:
                showDialog();
                break;
            case R.id.btn_logout:
                session.Logout();
                break;
            case R.id.btn_charge:
                IntentData intentData = new IntentData(6, 0, 0, 0, 0, Constants.CREDIT_TYPE, "get_form", null, CHARGE_TYPE);
                session.NewActivity(intentData, 0);
                break;
        }
    }

    private void changePasswordProcess(String username, String password, String old_password, String new_password) {
        User user = new User();
        user.setUserName(username);
        user.setPassword(password);
        user.setOld_password(old_password);
        user.setNew_password(new_password);
        user.setLangcode(session.pref().GetLang());
        user.setUId(session.pref().GetUserid());
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CHANGE_PASSWORD_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = session.api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        if (resp.getResult().equals(Constants.SUCCESS)) {
                            try {
                                progress.setVisibility(View.GONE);
                                tv_message.setText(null);
                                tv_message.setVisibility(View.VISIBLE);
                                tv_message.setText(resp.getMessage());
                                dialog.dismiss();
                                session.Logout();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                progress.setVisibility(View.GONE);
                                tv_message.setVisibility(View.VISIBLE);
                                tv_message.setText(resp.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    progress.setVisibility(View.GONE);
                    tv_message.setText(null);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(getString(R.string.error_failed_later));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    progress.setVisibility(View.GONE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(getString(R.string.internetcheck));
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
                    loadButtons();
                }
            });
            // Changing action button text color
            snackbar.setActionTextColor(Color.RED);
        }
        snackbar.show();
    }
}