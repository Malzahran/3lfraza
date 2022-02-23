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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alfraza.app.R;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.User;

import retrofit2.Call;
import retrofit2.Callback;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {
    private Session session;
    private Call<ServerResponse> response;
    private AppCompatButton btn_login;
    private EditText et_username, et_password, et_email;
    private AlertDialog dialog;
    private String user_type = "user";
    private TextView tv_reset_password, tv_message;
    private ProgressBar progress, progressDG;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (response != null && response.isExecuted()) response.cancel();
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
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
        setContentView(R.layout.activity_login);
        if (getIntent().getExtras() != null) {
            user_type = getIntent().getExtras().getString("type", "user");
        }
        initViews();
    }

    private void signIn() {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        if (username.isEmpty()) {
            session.showToast(getString(R.string.msg_input_username), 1);
            return;
        }

        if (password.isEmpty()) {
            session.showToast(getString(R.string.msg_input_password), 1);
            return;
        }
        loginProcess(username, password);
    }

    private void initViews() {
        btn_login = findViewById(R.id.btn_login);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        progress = findViewById(R.id.progress);
        tv_reset_password = findViewById(R.id.tv_reset_password);
        tv_reset_password.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                signIn();
                break;
            case R.id.tv_reset_password:
                forgetPassDialog();
                break;
        }
    }

    private void resetPassProcess(String email) {
        try {
            tv_message.setVisibility(View.GONE);
            tv_message.setText(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        User user = new User();
        user.setEmail(email);
        user.setLangcode(session.pref().GetLang());
        user.setUsertype(user_type);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.RESET_PASSWORD);
        request.setUser(user);
        response = session.api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        if (resp.getResult().equals(Constants.SUCCESS)) {
                            try {
                                progressDG.setVisibility(View.GONE);
                                dialog.dismiss();
                                String successmsg = getString(R.string.success_done);
                                if (resp.getMessage() != null) successmsg = resp.getMessage();
                                session.showToast(successmsg, 1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                progressDG.setVisibility(View.GONE);
                                String failmsg = getString(R.string.error_reset_pass_failed);
                                if (resp.getMessage() != null) failmsg = resp.getMessage();
                                tv_message.setText(failmsg);
                                tv_message.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    try {
                        progressDG.setVisibility(View.GONE);
                        tv_message.setText(getString(R.string.error_failed_later));
                        tv_message.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    progressDG.setVisibility(View.GONE);
                    tv_message.setText(getString(R.string.error_failed_later));
                    tv_message.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loginProcess(String username, String password) {
        try {
            DisableButtons(1);
            session.hideKeyboard(this.getCurrentFocus());
            progress.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        User user = new User();
        user.setUserName(username);
        user.setPassword(password);
        user.setLangcode(session.pref().GetLang());
        user.setUsertype(user_type);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.LOGIN_OPERATION);
        request.setParent(Constants.LOGIN_FORM);
        request.setUser(user);
        response = session.api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        if (resp.getResult().equals(Constants.SUCCESS)) {
                            try {
                                progress.setVisibility(View.INVISIBLE);
                                session.showToast(getResources().getString(R.string.success_login), 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            session.pref().SetUserlogged(true);
                            session.pref().SetUserid(resp.getUser().getUId());
                            session.pref().SetUsername(resp.getUser().getName());
                            session.pref().SetUserSname(resp.getUser().getSName());
                            session.pref().SetUseremail(resp.getUser().getEmail());
                            session.pref().SetUserStreet(resp.getUser().getStreet());
                            session.pref().SetUserBuilding(resp.getUser().getBuilding());
                            session.pref().SetUserFLoor(resp.getUser().getFloor());
                            session.pref().SetUserApartment(resp.getUser().getApartment());
                            session.pref().SetUserAdditional(resp.getUser().getAdditional());
                            session.pref().SetUserpic(resp.getUser().getProfileimg());
                            session.pref().SetUserphone(resp.getUser().getPhone());
                            if (resp.getUser().RequiredPhone() == 1)
                                session.pref().setRequiredPhone(true);
                            else session.pref().setRequiredPhone(false);
                            session.pref().SetUseruname(resp.getUser().getUsername());
                            session.pref().SetUserpass(resp.getUser().getPassword());
                            session.pref().SetUsertype(resp.getUser().getUsertype());
                            session.pref().SetUserbalance(resp.getUser().getBalance());
                            session.pref().AllowCredit(resp.getUser().AllowCredit());
                            session.pref().AllowChat(resp.getUser().AllowChat());
                            session.pref().SetAuthMethod(Constants.LOGIN_FORM);
                            session.SendFCMToken();
                            if (resp.getUser().AllowTracking() != 0) {
                                session.pref().setcurrentlyTracking(true);
                                if (resp.getUser().AllowTracking() > 1)
                                    session.pref().setTrackinginterval(resp.getUser().AllowTracking());
                                session.StartTracking();
                            } else {
                                session.pref().setcurrentlyTracking(false);
                                session.StopTracking();
                            }
                            if (resp.getUser().MapsInterval() != 0)
                                session.pref().setMapsinterval(resp.getUser().MapsInterval());
                            if (resp.getUser().AllowPush() != 0) {
                                session.pref().setPushServices(true);
                                if (resp.getUser().AllowPush() > 1)
                                    session.pref().setPushinterval(resp.getUser().AllowPush());
                                session.StartPushServices();
                            } else {
                                session.pref().setPushServices(false);
                                session.StopPushServices();
                            }
                            finish();
                        } else {
                            try {
                                DisableButtons(0);
                                progress.setVisibility(View.INVISIBLE);
                                String failmsg = getString(R.string.error_login_failed);
                                if (resp.getMessage() != null) failmsg = resp.getMessage();
                                showSnackbar(failmsg, 1, 2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    try {
                        DisableButtons(0);
                        progress.setVisibility(View.INVISIBLE);
                        showSnackbar(getString(R.string.error_failed_later), 0, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    DisableButtons(0);
                    progress.setVisibility(View.INVISIBLE);
                    showSnackbar(getString(R.string.internetcheck), 1, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showSnackbar(String msg, final int action, int length) {
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
                    switch (action) {
                        case 1:
                            signIn();
                            break;
                    }
                }
            });
            // Changing action button text color
            snackbar.setActionTextColor(Color.RED);
        }
        snackbar.show();
    }

    private void DisableButtons(int type) {
        if (type == 1) {
            btn_login.setOnClickListener(null);
            tv_reset_password.setOnClickListener(null);
        } else {
            btn_login.setOnClickListener(this);
            tv_reset_password.setOnClickListener(this);
        }
    }

    private void forgetPassDialog() {
        if (dialog != null) dialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullParent = null;
        View view = inflater.inflate(R.layout.dialog_reset_password, nullParent);
        et_email = view.findViewById(R.id.et_email);
        tv_message = view.findViewById(R.id.tv_message);
        progressDG = view.findViewById(R.id.progressdg);
        builder.setView(view);
        builder.setIcon(R.drawable.ic_password);
        builder.setTitle(getString(R.string.forget_password_head));
        builder.setPositiveButton(getString(R.string.forget_password_btn), new DialogInterface.OnClickListener() {
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
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogg) {
                session.hideKeyboard(dialog.getCurrentFocus());
            }
        });

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPass();
            }
        });
        float paddingDp = 5f;
        // Convert to pixels
        int paddingPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingDp, this.getResources().getDisplayMetrics());
        Button pbutton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        pbutton.setTextColor(ContextCompat.getColor(this, R.color.White));
        pbutton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
        Button nbutton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
        nbutton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        nbutton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
    }

    private void forgetPass() {
        if (dialog != null)
            try {
                String email = et_email.getText().toString();
                if (!email.isEmpty() && session.isValidEmail(email.trim())) {
                    session.hideKeyboard(dialog.getCurrentFocus());
                    progressDG.setVisibility(View.VISIBLE);
                    resetPassProcess(email);
                } else if (!email.isEmpty() && !session.isValidEmail(email.trim())) {
                    session.showToast(getString(R.string.msg_input_valid_email), 1);
                } else {
                    session.showToast(getString(R.string.msg_input_email), 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}