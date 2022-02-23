package com.alfraza.app;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.ContactData;

import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;

public class ActivityContact extends AppCompatActivity implements View.OnClickListener {
    private Session session;
    private Call<ServerResponse> response;
    private EditText et_name, et_phone, et_email, et_message;
    private AppCompatButton btn_send_msg;
    private ProgressBar progress;
    private CardView formLayout;

    @Override
    public void onDestroy() {
        super.onDestroy();
        session.destroySession();
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

    // override the base context of application to update default locale for this activity
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(this);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar aBar = getSupportActionBar();
        if (aBar != null) {
            aBar.setDisplayHomeAsUpEnabled(true);
            aBar.setTitle(getString(R.string.menu_contact));
        }
        initViews();
    }

    private void initViews() {
        TextView tv_face = findViewById(R.id.tv_face);
        formLayout = findViewById(R.id.formlayout);
        progress = findViewById(R.id.progress);
        et_name = findViewById(R.id.et_name);
        et_phone = findViewById(R.id.et_phone);
        et_email = findViewById(R.id.et_email);
        et_message = findViewById(R.id.et_message);
        btn_send_msg = findViewById(R.id.btn_send_msg);
        et_name.setText(session.pref().GetUsername());
        tv_face.setOnClickListener(this);
        btn_send_msg.setOnClickListener(this);
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
        MenuItem acProgress = menu.findItem(R.id.action_progress);
        homeItem.setVisible(false);
        searchItem.setVisible(false);
        acProgress.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_home) {
            session.goHome();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_face:
                try {
                    URL uri = null; // missing 'http://' will cause crashed
                    try {
                        uri = session.get_url("https://www.facebook.com/n/?kiddoclothesshop");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    if (uri != null) {
                        session.OpenLink(uri);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_send_msg:
                sendMessage();
                break;
        }
    }

    private void sendMessage() {
        String name = et_name.getText().toString();
        String phone = et_phone.getText().toString();
        String email = et_email.getText().toString();
        String msg = et_message.getText().toString();

        if (name.isEmpty()) {
            session.showToast(getString(R.string.msg_input_name), 1);
            return;
        }

        if (phone.isEmpty()) {
            session.showToast(getString(R.string.input_phone), 1);
            return;
        } else if (!session.validCellPhone(phone)) {
            session.showToast(getString(R.string.msg_input_valid_phone), 1);
            return;
        }

        if (email.isEmpty()) {
            session.showToast(getString(R.string.msg_input_email), 1);
            return;
        } else if (!session.isValidEmail(email.trim())) {
            session.showToast(getString(R.string.msg_input_valid_email), 1);
            return;
        }

        if (msg.isEmpty()) {
            session.showToast(getString(R.string.input_msg), 1);
            return;
        }
        sendMessageprocess(name, phone, email, msg);
    }

    private void sendMessageprocess(String name, String phone, String email, String msg) {
        try {
            session.hideKeyboard(getCurrentFocus());
            progress.setVisibility(View.VISIBLE);
            btn_send_msg.setOnClickListener(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ContactData contact = new ContactData();
        contact.setName(name);
        contact.setPhone(phone);
        contact.setEmail(email);
        contact.setMessage(msg);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CONTACT_OPERATION);
        request.setUser(session.getUserInfo());
        request.setContact(contact);
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
                                formLayout.setVisibility(View.GONE);
                                session.showToast(getResources().getString(R.string.success_message_sent), 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                btn_send_msg.setOnClickListener(ActivityContact.this);
                                progress.setVisibility(View.INVISIBLE);
                                String failmsg = getString(R.string.error_msg_notsent);
                                if (resp.getMessage() != null) {
                                    failmsg = resp.getMessage();
                                }
                                showSnackbar(failmsg, 1, 2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    try {
                        btn_send_msg.setOnClickListener(ActivityContact.this);
                        progress.setVisibility(View.GONE);
                        showSnackbar(getString(R.string.error_failed_later), 0, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    btn_send_msg.setOnClickListener(ActivityContact.this);
                    progress.setVisibility(View.GONE);
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
        CoordinatorLayout main_layout = findViewById(R.id.main_layout);
        Snackbar snackbar = Snackbar.make(main_layout, msg, SnLength);
        if (action != 0) {
            snackbar.setAction(getString(R.string.reloadbtn), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage();
                }
            });

            // Changing action button text color
            snackbar.setActionTextColor(Color.RED);
        }
        snackbar.show();
    }
}