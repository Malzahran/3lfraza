package com.alfraza.app.activities.users;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.alfraza.app.R;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.User;

import retrofit2.Call;
import retrofit2.Callback;

public class ActivityRegister extends AppCompatActivity implements View.OnClickListener {
    private Session session;
    private Call<ServerResponse> response;
    private ProgressBar progress;
    private EditText et_password, et_email, et_name, et_phone;
    private AppCompatButton btn_register;

    // override the base context of application to update default locale for this activity
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(this);
        setContentView(R.layout.activity_register);
        initViews();
    }

    private void initViews() {
        btn_register = findViewById(R.id.btn_register);
        et_password = findViewById(R.id.et_password);
        et_email = findViewById(R.id.et_email);
        et_name = findViewById(R.id.et_name);
        et_phone = findViewById(R.id.et_phone);
        progress = findViewById(R.id.progress);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (response != null && response.isExecuted()) response.cancel();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void register() {
        String password = et_password.getText().toString();
        String email = et_email.getText().toString();
        String name = et_name.getText().toString();
        String phone = et_phone.getText().toString();

        if (password.isEmpty()) {
            session.showToast(getString(R.string.msg_input_password), 1);
            return;
        }

        if (email.isEmpty()) {
            session.showToast(getString(R.string.msg_input_email), 1);
            return;
        } else if (!session.isValidEmail(email.trim())) {
            session.showToast(getString(R.string.msg_input_valid_email), 1);
            return;
        }

        if (name.isEmpty()) {
            session.showToast(getString(R.string.msg_input_name), 1);
            return;
        }

        if (phone.isEmpty()) {
            session.showToast(getString(R.string.msg_input_phone), 1);
            return;
        } else if (!session.validCellPhone(phone.trim())) {
            session.showToast(getString(R.string.msg_input_valid_phone), 1);
            return;
        }
        registerProcess(password, email, name, phone);
    }

    private void registerProcess(String password, String email, String name, String phone) {
        try {
            DisableButtons(1);
            session.hideKeyboard(this.getCurrentFocus());
            progress.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        User user = new User();
        user.setPassword(password);
        user.setEmail(email);
        user.setName(name);
        user.setPhone(phone);
        user.setLangcode(session.pref().GetLang());
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.REGISTER_OPERATION);
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
                                progress.setVisibility(View.GONE);
                                session.showToast(getResources().getString(R.string.success_login), 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            session.pref().SetUserlogged(true);
                            session.pref().SetUserid(resp.getUser().getUId());
                            session.pref().SetUsername(resp.getUser().getName());
                            session.pref().SetUserSname(resp.getUser().getSName());
                            session.pref().SetUseremail(resp.getUser().getEmail());
                            session.pref().SetUserpic(resp.getUser().getProfileimg());
                            session.pref().SetUserphone(resp.getUser().getPhone());
                            if (resp.getUser().RequiredPhone() == 1)
                                session.pref().setRequiredPhone(true);
                            else session.pref().setRequiredPhone(false);

                            session.pref().SetUseruname(resp.getUser().getUsername());
                            session.pref().SetUserpass(resp.getUser().getPassword());
                            session.pref().SetUserbalance(resp.getUser().getBalance());
                            session.pref().AllowCredit(resp.getUser().AllowCredit());
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
                                progress.setVisibility(View.GONE);
                                String error = getString(R.string.error_register_failed);
                                if (resp.getMessage() != null) error = resp.getMessage();
                                showSnackBar(error, 1, 2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    try {
                        DisableButtons(0);
                        progress.setVisibility(View.GONE);
                        showSnackBar(getString(R.string.error_failed_later), 0, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    DisableButtons(0);
                    progress.setVisibility(View.GONE);
                    showSnackBar(getString(R.string.internetcheck), 1, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void DisableButtons(int type) {
        if (type == 1) btn_register.setOnClickListener(null);
        else btn_register.setOnClickListener(this);
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
                    switch (action) {
                        case 1:
                            register();
                            break;
                    }
                }
            });

            // Changing action button text color
            snackbar.setActionTextColor(Color.RED);
        }
        snackbar.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                register();
                break;
        }
    }
}