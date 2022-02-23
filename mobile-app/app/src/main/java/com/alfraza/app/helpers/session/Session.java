package com.alfraza.app.helpers.session;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alfraza.app.ActivityLocation;
import com.alfraza.app.ActivityDelivery;
import com.alfraza.app.ActivityStore;
import com.alfraza.app.ActivityOrderHistory;
import com.alfraza.app.ActivityProductDetails;
import com.alfraza.app.ActivityShoppingCart;
import com.alfraza.app.ActivityContact;
import com.alfraza.app.ActivityForm;
import com.alfraza.app.ActivityMaps;
import com.alfraza.app.ActivityWorker;
import com.alfraza.app.R;
import com.alfraza.app.ActivityRecycler;
import com.alfraza.app.ActivityBackend;
import com.alfraza.app.ActivityView;
import com.alfraza.app.activities.misc.ActivityMessages;
import com.alfraza.app.activities.misc.ActivityNotifications;
import com.alfraza.app.activities.users.ActivityLogin;
import com.alfraza.app.activities.users.ActivityProfile;
import com.alfraza.app.activities.users.ActivityRegister;
import com.alfraza.app.ActivitySignIn;
import com.alfraza.app.api.ApiUtils;
import com.alfraza.app.api.RequestInterface;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.utilities.CallbackDialog;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.helpers.utilities.ITUtilities;
import com.alfraza.app.helpers.utilities.ImageHandler;
import com.alfraza.app.helpers.utilities.Tools;
import com.alfraza.app.models.ActionData;
import com.alfraza.app.models.FeatureItemSelect;
import com.alfraza.app.models.IntentData;
import com.alfraza.app.models.MiscData;
import com.alfraza.app.models.User;
import com.alfraza.app.services.GpsService;
import com.alfraza.app.services.PushService;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;

import static com.alfraza.app.helpers.utilities.Constants.EXTRA_FROM_NOTIF;
import static com.alfraza.app.helpers.utilities.Constants.EXTRA_OBJECT_ID;

public class Session {
    private AppSharedPreference pref;
    private RequestInterface api;
    private Picasso imgLoader;
    private Activity act;
    private Context ctx;
    private static final int REQUEST_GPS = 124;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private EditText et_email, et_phone;
    private TextInputLayout email_lyt, phone_lyt;
    private TextView tv_message;
    private ProgressBar progressDG, progress;
    private AlertDialog dialog;
    private AlertDialog notifyDialog;
    private AlertDialog loginDialog;
    private AlertDialog notfmsgdialog;
    private AlertDialog msgDialog;

    private AlertDialog rateDialog;
    private Float RatingVal;
    private Boolean is_rated = false;

    public Session(Activity act) {
        this.act = act;
        this.ctx = act;
    }

    public Session(Context ctx) {
        this.ctx = ctx;
    }

    public AppSharedPreference pref() {
        if (pref == null) {
            this.pref = AppSharedPreference.getInstance(ctx);
        }
        return pref;
    }

    public RequestInterface api() {
        if (api == null) {
            this.api = ApiUtils.getService();
        }
        return api;
    }

    public Picasso imgLoader() {
        if (imgLoader == null) {
            this.imgLoader = ImageHandler.getSharedInstance(ctx);
        }
        return imgLoader;
    }

    public void orientationManager(boolean lock) {
        int currentOrientation = act.getResources().getConfiguration().orientation;
        if (lock) {
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
        } else {
            act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    public void showToast(String msg, int length) {
        int TLength = Toast.LENGTH_SHORT;
        switch (length) {
            case 1:
                TLength = Toast.LENGTH_LONG;
                break;
        }
        Toast.makeText(ctx, msg, TLength).show();
    }

    public void UpdateUserInfo() {
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.USERINFO_OPERATION);
        request.setUser(getUserInfo());
        Call<ServerResponse> response = api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        if (resp.getResult().equals(Constants.SUCCESS)) {
                            if (resp.getUser().AllowTracking() != 0) {
                                pref().setcurrentlyTracking(true);

                                if (resp.getUser().AllowTracking() > 1)
                                    pref().setTrackinginterval(resp.getUser().AllowTracking());
                                StartTracking();
                            } else {
                                StopTracking();
                                pref().setcurrentlyTracking(false);
                            }


                            if (resp.getUser().AllowPush() != 0) {
                                pref().setPushServices(true);
                                if (resp.getUser().AllowPush() > 1)
                                    pref().setPushinterval(resp.getUser().AllowPush());
                                StartPushServices();
                            } else {
                                pref().setPushServices(false);
                                StopPushServices();
                            }

                            if (resp.getUser().MapsInterval() != 0)
                                pref().setMapsinterval(resp.getUser().MapsInterval());
                            if (resp.getUser().RequiredPhone() == 1) {
                                pref().setRequiredPhone(true);
                                try {
                                    showPhoneDialog();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                pref().setRequiredPhone(false);
                                try {
                                    if (dialog != null && dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            pref().SetUserpic(resp.getUser().getProfileimg());
                            pref().SetUsername(resp.getUser().getName());
                            pref().SetUseremail(resp.getUser().getEmail());
                            pref().SetUserStreet(resp.getUser().getStreet());
                            pref().SetUserBuilding(resp.getUser().getBuilding());
                            pref().SetUserFLoor(resp.getUser().getFloor());
                            pref().SetUserApartment(resp.getUser().getApartment());
                            pref().SetUserAdditional(resp.getUser().getAdditional());
                            if (resp.getUser().getUsertype() != null)
                                pref().SetUsertype(resp.getUser().getUsertype());
                            if (resp.getUser().getPhone() != null)
                                pref().SetUserphone(resp.getUser().getPhone());
                            pref().AllowCredit(resp.getUser().AllowCredit());
                            pref().AllowChat(resp.getUser().AllowChat());
                            if (resp.getUser().getBalance() != null)
                                pref().SetUserbalance(resp.getUser().getBalance());
                            try {
                                if (act instanceof ActivityStore) ((ActivityStore) act).updateUi();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (resp.getResult().equals(Constants.LOGOUT)) {
                            Logout();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
            }
        });
    }

    public void initSession() {
        if (pref().IsUserlogged()) {
            StartServices();
            if (pref().RequiredPhone()) {
                if (dialog == null) showPhoneDialog();
                else if (!dialog.isShowing()) showPhoneDialog();
            }
        }
    }

    public void changeLang() {
        String LANG = pref().GetLang().equals("ar") ? "en" : "ar";
        pref().SetLang(LANG);
        clearArea();
        act.recreate();
    }


    public void clearArea() {
        pref().setCityName("");
        pref().setCityGroup(0);
        pref().setLatitude(0);
        pref().setLongitude(0);
        pref().setCity(0);
    }

    public void checkArea() {
        if (pref().getCity() == 0 || pref().getCityGroup() == 0 || pref().getLatitude() == 0 || pref().getLongitude() == 0) {
            act.startActivity(new Intent(act, ActivityLocation.class));
            act.finish();
        }
    }

    public void showKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null)
                inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null)
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void StartServices() {
        if (pref().hasUserSubscribeToNotification()) SendFCMToken();
        if (pref().IsUserlogged()) UpdateUserInfo();
    }

    private boolean verifyLocationPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_LOCATION, REQUEST_GPS);
                return false;
            } else return true;
        } else return true;
    }


    public void StartTracking() {
        if (pref().IsUserlogged()) {
            if (pref().iscurrentlyTracking()) {
                if (verifyLocationPermissions(act)) {
                    Intent trackerIntent = new Intent(ctx, GpsService.class);
                    trackerIntent.setAction("start");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        act.startForegroundService(trackerIntent);
                    else act.startService(trackerIntent);
                }
            }
        }
    }

    public void StopTracking() {
        Intent gpsIntent = new Intent(ctx, GpsService.class);
        gpsIntent.setAction("stop");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            act.startForegroundService(gpsIntent);
        else act.startService(gpsIntent);
    }

    public void StartPushServices() {
        if (pref().IsUserlogged()) {
            if (pref().PushServices()) {
                Intent pushIntent = new Intent(ctx, PushService.class);
                pushIntent.setAction("start");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    act.startForegroundService(pushIntent);
                else act.startService(pushIntent);
            }
        }
    }

    public void StopPushServices() {
        Intent pushIntent = new Intent(ctx, PushService.class);
        pushIntent.setAction("stop");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            act.startForegroundService(pushIntent);
        else act.startService(pushIntent);
    }

    public void destroySession() {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
        if (notifyDialog != null && notifyDialog.isShowing()) notifyDialog.dismiss();
        if (loginDialog != null && loginDialog.isShowing()) loginDialog.dismiss();
        if (msgDialog != null && msgDialog.isShowing()) msgDialog.dismiss();
        if (notfmsgdialog != null && notfmsgdialog.isShowing()) notfmsgdialog.dismiss();
        if (rateDialog != null && rateDialog.isShowing()) rateDialog.dismiss();
    }

    private void showPhoneDialog() {
        if (dialog != null) dialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        LayoutInflater inflater = act.getLayoutInflater();
        final ViewGroup nullParent = null;
        View view = inflater.inflate(R.layout.dialog_input_phone, nullParent);
        et_phone = view.findViewById(R.id.et_phone);
        tv_message = view.findViewById(R.id.tv_message);
        progressDG = view.findViewById(R.id.progressdg);
        builder.setView(view);
        builder.setTitle(act.getString(R.string.msg_input_phone));
        builder.setPositiveButton(act.getString(R.string.okbtn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendPhone();
            }
        });
        // Convert to pixels
        int paddingPx = ITUtilities.DpToPx(ctx, 5f);
        Button pButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pButton.setBackgroundColor(ContextCompat.getColor(act, R.color.colorPrimary));
        pButton.setTextColor(ContextCompat.getColor(act, R.color.White));
        pButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
    }

    public void showNotifyDialog() {
        if (notifyDialog != null) notifyDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        LayoutInflater inflater = act.getLayoutInflater();
        final ViewGroup nullParent = null;
        View view = inflater.inflate(R.layout.dialog_notify_location, nullParent);
        et_email = view.findViewById(R.id.email);
        et_phone = view.findViewById(R.id.phone);
        String email = pref().GetUserEmail();
        String phone = pref().GetUserPhone();
        if (pref().IsUserlogged()) {
            if (email != null && !email.isEmpty()) et_email.setText(email);
            if (phone != null && !phone.isEmpty()) et_phone.setText(phone);
        }
        email_lyt = view.findViewById(R.id.email_lyt);
        phone_lyt = view.findViewById(R.id.phone_lyt);
        tv_message = view.findViewById(R.id.tv_message);
        progressDG = view.findViewById(R.id.progressdg);
        builder.setView(view);
        builder.setTitle(act.getString(R.string.app_name));
        et_email.addTextChangedListener(new CheckoutTextWatcher(et_email));
        et_phone.addTextChangedListener(new CheckoutTextWatcher(et_phone));
        AppCompatButton btn_notify = view.findViewById(R.id.btn_notify);
        btn_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendNotifyAlert();
            }
        });
        notifyDialog = builder.create();
        notifyDialog.show();
    }

    private boolean validateEmail() {
        String str = et_email.getText().toString().trim();
        if (str.isEmpty() || !isValidEmail(str)) {
            email_lyt.setError(act.getString(R.string.invalid_email));
            requestFocus(et_email);
            return false;
        } else email_lyt.setErrorEnabled(false);
        return true;
    }

    private boolean validatePhone() {
        String str = et_phone.getText().toString().trim();
        if (str.isEmpty()) {
            phone_lyt.setError(act.getString(R.string.invalid_phone));
            requestFocus(et_phone);
            return false;
        } else phone_lyt.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus())
            act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void SendNotifyAlert() {
        try {
            if (!validateEmail()) {
                showToast(act.getString(R.string.invalid_email), 0);
                return;
            }
            if (!validatePhone()) {
                showToast(act.getString(R.string.invalid_phone), 0);
                return;
            }
            String email = et_email.getText().toString();
            String phone = et_phone.getText().toString();
            progressDG.setVisibility(View.VISIBLE);
            SendNotifyAlertProcess(email, phone);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SendNotifyAlertProcess(String email, String phone) {
        try {
            tv_message.setVisibility(View.GONE);
            hideKeyboard(notifyDialog.getCurrentFocus());
        } catch (Exception e) {
            e.printStackTrace();
        }
        User user = getUserInfo();
        user.setPhone(phone);
        user.setEmail(email);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.LOCATION_NOTIFY_TYPE);
        request.setUser(user);
        Call<ServerResponse> response = api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        switch (resp.getResult()) {
                            case Constants.SUCCESS:
                                try {
                                    if (resp.getMessage() != null && !resp.getMessage().isEmpty())
                                        showToast(resp.getMessage(), 1);
                                    notifyDialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case Constants.LOGOUT:
                                try {
                                    notifyDialog.dismiss();
                                    Logout();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                try {
                                    String error = act.getString(R.string.error_failed_try_again);
                                    if (resp.getMessage() != null) error = resp.getMessage();
                                    progressDG.setVisibility(View.GONE);
                                    tv_message.setVisibility(View.VISIBLE);
                                    tv_message.setText(error);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                } else {
                    try {
                        progressDG.setVisibility(View.GONE);
                        tv_message.setVisibility(View.VISIBLE);
                        tv_message.setText(act.getString(R.string.error_failed_later));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    progressDG.setVisibility(View.GONE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(act.getString(R.string.internetcheck));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
                case R.id.phone:
                    validatePhone();
                    break;
            }
        }
    }

    private void SendPhone() {
        try {
            String phone = et_phone.getText().toString();
            if (!phone.isEmpty() && phone.length() > 10) {
                progressDG.setVisibility(View.VISIBLE);
                sendPhoneProcess(phone);
            } else {
                tv_message.setVisibility(View.VISIBLE);
                tv_message.setText(act.getString(R.string.msg_input_phone));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPhoneProcess(String phone) {
        try {
            tv_message.setVisibility(View.GONE);
            hideKeyboard(dialog.getCurrentFocus());
        } catch (Exception e) {
            e.printStackTrace();
        }

        User user = getUserInfo();
        user.setPhone(phone);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.UP_PHONE);
        request.setUser(user);
        Call<ServerResponse> response = api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        switch (resp.getResult()) {
                            case Constants.SUCCESS:
                                try {
                                    if (resp.getUser().getPhone() != null) {
                                        pref().SetUserphone(resp.getUser().getPhone());
                                        pref().setRequiredPhone(false);
                                    }
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case Constants.LOGOUT:
                                try {
                                    Logout();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                try {
                                    String error = act.getString(R.string.error_failed_try_again);
                                    if (resp.getMessage() != null) {
                                        error = resp.getMessage();
                                    }
                                    progressDG.setVisibility(View.GONE);
                                    tv_message.setVisibility(View.VISIBLE);
                                    tv_message.setText(error);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                } else {
                    try {
                        progressDG.setVisibility(View.GONE);
                        tv_message.setVisibility(View.VISIBLE);
                        tv_message.setText(act.getString(R.string.error_failed_later));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    progressDG.setVisibility(View.GONE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(act.getString(R.string.internetcheck));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showLoginDialog() {
        if (loginDialog != null) loginDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        LayoutInflater inflater = act.getLayoutInflater();
        final ViewGroup nullParent = null;
        View view = inflater.inflate(R.layout.dialog_login, nullParent);
        AppCompatButton btn_login = view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(act, ActivitySignIn.class);
                act.startActivity(intent);
                loginDialog.dismiss();
            }
        });
        builder.setView(view);
        loginDialog = builder.create();
        loginDialog.show();
    }

    public void SendMsgDialog(String username, final int userID, final int type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        LayoutInflater inflater = act.getLayoutInflater();
        final ViewGroup nullParent = null;
        View view = inflater.inflate(R.layout.dialog_send_msg, nullParent);
        final EditText et_msg = view.findViewById(R.id.et_msg);
        progress = view.findViewById(R.id.progress);
        builder.setView(view);
        builder.setTitle(act.getString(R.string.sendmsg_head) + " " + username);
        builder.setIcon(ContextCompat.getDrawable(act, R.drawable.ic_email_colored));
        builder.setPositiveButton(act.getString(R.string.msgbtn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(act.getString(R.string.cancelbtn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        msgDialog = builder.create();
        msgDialog.show();
        msgDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msgtext = et_msg.getText().toString();
                if (!msgtext.isEmpty()) {
                    SendMessage(msgtext, userID, type);
                    progress.setVisibility(View.VISIBLE);
                } else {
                    showToast(act.getString(R.string.input_msg), 0);
                }
            }
        });
        // Convert to pixels
        int paddingPx = ITUtilities.DpToPx(ctx, 5f);
        Button pButton = msgDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pButton.setBackgroundColor(ContextCompat.getColor(act, R.color.colorPrimary));
        pButton.setTextColor(ContextCompat.getColor(act, R.color.White));
        pButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
        Button nButton = msgDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nButton.setBackgroundColor(ContextCompat.getColor(act, R.color.colorAccent));
        nButton.setTextColor(ContextCompat.getColor(act, R.color.colorPrimary));
        nButton.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
    }

    private void SendMessage(String msgText, int userID, int type) {
        MiscData misc = new MiscData();
        misc.setUserid(userID);
        misc.setMessageText(msgText);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent("messages");
        request.setSubType("sendmsg");
        misc.setType(String.valueOf(type));
        request.setUser(getUserInfo());
        request.setMisc(misc);
        Call<ServerResponse> response = api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null && resp.getResult() != null) {
                    try {
                        switch (resp.getResult()) {
                            case Constants.SUCCESS:
                                progress.setVisibility(View.GONE);
                                msgDialog.hide();
                                Toast.makeText(act, act.getString(R.string.success_message_sent), Toast.LENGTH_SHORT).show();
                                break;
                            case Constants.LOGOUT:
                                Logout();
                                break;
                            default:
                                progress.setVisibility(View.GONE);
                                String error = act.getString(R.string.error_msg_not_sent);
                                if (resp.getMessage() != null) {
                                    error = resp.getMessage();
                                }
                                Toast.makeText(act, error, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(act, act.getString(R.string.error_msg_not_sent), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void notifyStock(long itemID, List<FeatureItemSelect> features) {
        MiscData misc = new MiscData();
        misc.product_id = itemID;
        misc.setFeatures(features);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.NOTIFY_TYPE);
        request.setUser(getUserInfo());
        request.setMisc(misc);
        Call<ServerResponse> response = api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        switch (resp.getResult()) {
                            case Constants.SUCCESS:
                                try {
                                    showToast(act.getString(R.string.success_notify), 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case Constants.LOGOUT:
                                try {
                                    Logout();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                try {
                                    String failmsg = act.getString(R.string.error_failed_try_again);
                                    if (resp.getMessage() != null) {
                                        failmsg = resp.getMessage();
                                    }
                                    showToast(failmsg, 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                } else {
                    try {
                        showToast(act.getString(R.string.error_failed_later), 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    showToast(act.getString(R.string.internetcheck), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addCart(long itemID, int qty, int ftID, List<FeatureItemSelect> features) {
        MiscData misc = new MiscData();
        misc.product_id = itemID;
        misc.setQty(qty);
        misc.setFeature(ftID);
        misc.setFeatures(features);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.CART_TYPE);
        request.setSubType(Constants.ADD_TYPE);
        request.setUser(getUserInfo());
        request.setMisc(misc);
        Call<ServerResponse> response = api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        switch (resp.getResult()) {
                            case Constants.SUCCESS:
                                try {
                                    showToast(act.getString(R.string.success_added_cart), 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case Constants.LOGOUT:
                                try {
                                    Logout();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                try {
                                    String failmsg = act.getString(R.string.error_failed_try_again);
                                    if (resp.getMessage() != null) {
                                        failmsg = resp.getMessage();
                                    }
                                    showToast(failmsg, 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                } else {
                    try {
                        showToast(act.getString(R.string.error_failed_later), 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    showToast(act.getString(R.string.internetcheck), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void editCart(long itemID, int qty, int ftID) {
        MiscData misc = new MiscData();
        misc.product_id = itemID;
        misc.setQty(qty);
        misc.setFeature(ftID);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.CART_TYPE);
        request.setSubType(Constants.EDIT_TYPE);
        request.setUser(getUserInfo());
        request.setMisc(misc);
        Call<ServerResponse> response = api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        switch (resp.getResult()) {
                            case Constants.SUCCESS:
                                try {
                                    showToast(act.getString(R.string.success_edit_cart), 1);
                                    if (act instanceof ActivityShoppingCart)
                                        ((ActivityShoppingCart) act).updateCart();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case Constants.LOGOUT:
                                try {
                                    Logout();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                try {
                                    String failmsg = act.getString(R.string.error_failed_try_again);
                                    if (resp.getMessage() != null) failmsg = resp.getMessage();
                                    showToast(failmsg, 1);
                                    if (act instanceof ActivityShoppingCart)
                                        ((ActivityShoppingCart) act).updateCart();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                } else {
                    try {
                        showToast(act.getString(R.string.error_failed_later), 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    showToast(act.getString(R.string.internetcheck), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void delCart(long itemID, int ftID) {
        MiscData misc = new MiscData();
        misc.product_id = itemID;
        misc.setFeature(ftID);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.CART_TYPE);
        request.setSubType(Constants.DEL_TYPE);
        request.setUser(getUserInfo());
        request.setMisc(misc);
        Call<ServerResponse> response = api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        switch (resp.getResult()) {
                            case Constants.SUCCESS:
                                try {
                                    showToast(act.getString(R.string.success_edit_cart), 1);
                                    if (act instanceof ActivityShoppingCart) {
                                        ((ActivityShoppingCart) act).updateCart();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case Constants.LOGOUT:
                                try {
                                    Logout();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                try {
                                    String error = act.getString(R.string.error_failed_try_again);
                                    if (resp.getMessage() != null) {
                                        error = resp.getMessage();
                                    }
                                    showToast(error, 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                } else {
                    try {
                        showToast(act.getString(R.string.error_failed_later), 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    showToast(act.getString(R.string.internetcheck), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void emptyCart() {
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.CART_TYPE);
        request.setSubType(Constants.CLEAR_TYPE);
        request.setUser(getUserInfo());
        Call<ServerResponse> response = api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        switch (resp.getResult()) {
                            case Constants.SUCCESS:
                                try {
                                    showToast(act.getString(R.string.delete_success), 1);
                                    if (act instanceof ActivityShoppingCart)
                                        ((ActivityShoppingCart) act).updateCart();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case Constants.LOGOUT:
                                try {
                                    Logout();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                try {
                                    String error = act.getString(R.string.error_failed_try_again);
                                    if (resp.getMessage() != null) error = resp.getMessage();
                                    showToast(error, 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                } else {
                    try {
                        showToast(act.getString(R.string.error_failed_later), 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    showToast(act.getString(R.string.internetcheck), 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void showRateDialog(final Long itemID, final int type, final AppCompatButton rate_btn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        LayoutInflater inflater = act.getLayoutInflater();
        final ViewGroup nullParent = null;
        View view = inflater.inflate(R.layout.dialog_rate, nullParent);
        Button btn_submit_rate = view.findViewById(R.id.btn_submit_rate);
        RatingBar rateBar = view.findViewById(R.id.rateBar);
        final TextView tv_rateVAL = view.findViewById(R.id.tv_rateval);
        rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                tv_rateVAL.setText(String.valueOf(rating));
                RatingVal = rating;
                is_rated = true;
            }
        });
        btn_submit_rate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (is_rated) {
                    setRate(itemID, RatingVal, type, rate_btn);
                } else {
                    showToast(act.getString(R.string.no_rate), 0);
                }

            }

        });
        builder.setView(view);
        builder.setTitle(act.getString(R.string.set_rate));
        rateDialog = builder.create();
        rateDialog.show();
    }

    private void setRate(long itemID, float rate, int type, final AppCompatButton rate_btn) {
        MiscData misc = new MiscData();
        misc.item_id = itemID;
        misc.setType("order_rate");
        misc.setRating(rate);
        misc.setSubid(type);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.DATA_OPERATION);
        request.setParent(Constants.ACTION_SET);
        request.setUser(getUserInfo());
        request.setMisc(misc);
        Call<ServerResponse> response = api().operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        if (resp.getResult().equals(Constants.SUCCESS)) {
                            try {
                                rateDialog.hide();
                                showToast(act.getString(R.string.success_rate), 0);
                                if (rate_btn != null) rate_btn.setVisibility(View.GONE);
                                if (act instanceof ActivityOrderHistory)
                                    act.recreate();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                String error = act.getString(R.string.error_failed_try_again);
                                if (resp.getMessage() != null) {
                                    error = resp.getMessage();
                                }
                                showToast(error, 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    try {
                        showToast(act.getString(R.string.error_failed_later), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    showToast(act.getString(R.string.internetcheck), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void saveAction(Integer itemID, final String type, int tp, final int uiID, boolean logged) {
        if (logged && !pref().IsUserlogged()) {
            showLoginDialog();
        } else {
            MiscData misc = new MiscData();
            misc.setItem_id(itemID);
            misc.setType(type);
            misc.setSubid(tp);
            ServerRequest request = new ServerRequest();
            request.setOperation(Constants.DATA_OPERATION);
            request.setParent(Constants.ACTION_SET);
            request.setUser(getUserInfo());
            request.setMisc(misc);
            Call<ServerResponse> response = api().operation(request);

            response.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {

                    final ServerResponse resp = response.body();
                    if (resp != null) {
                        if (resp.getResult() != null) {
                            if (resp.getResult().equals(Constants.SUCCESS)) {
                                try {
                                    if (!type.equals("phone") && !type.equals("openinfo")) {
                                        if (resp.getMessage() != null) {
                                            String msg = resp.getMessage();
                                            showToast(msg, 0);
                                        }
                                    }
                                    if (uiID != 0) {
                                        AppCompatButton btn_action = act.findViewById(uiID);
                                        ActionData action = resp.getAction();
                                        if (action != null && action.getType() != 0) {
                                            switch (action.getType()) {
                                                case 1:
                                                    if (btn_action != null)
                                                        btn_action.setVisibility(View.GONE);
                                                    break;
                                                case 2:
                                                    if (action.getText() != null) {
                                                        if (btn_action != null) {
                                                            btn_action.setText(resp.getAction().getText());
                                                            if (resp.getAction().getDisable() != 0)
                                                                btn_action.setOnClickListener(null);
                                                            final IntentData actionIntent = action.getIntent();
                                                            if (actionIntent != null) {
                                                                btn_action.setOnClickListener(null);
                                                                btn_action.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        NewActivity(actionIntent, 0);
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                    break;
                                                case 3:
                                                    act.recreate();
                                                    break;
                                                case 4:
                                                    if (resp.getIntent() != null)
                                                        NewActivity(resp.getIntent(), 0);
                                                    else act.recreate();
                                                    break;
                                                case 5:
                                                    if (resp.getIntent() != null) {
                                                        NewActivity(resp.getIntent(), 0);
                                                        act.finish();
                                                    } else act.finish();
                                                    break;
                                                case 6:
                                                    CallbackDialog callbackDialog = new CallbackDialog() {
                                                        @Override
                                                        public void onPositiveClick(Dialog dialog) {
                                                            dialog.dismiss();
                                                            if (resp.getIntent() != null) {
                                                                NewActivity(resp.getIntent(), 0);
                                                                act.finish();
                                                            } else act.finish();
                                                        }

                                                        @Override
                                                        public void onNegativeClick(Dialog dialog) {

                                                        }
                                                    };
                                                    Tools.showDialogCustom(act, resp.getNotes(), callbackDialog);
                                                    break;
                                                case 7:
                                                    CallbackDialog callbackDialogReload = new CallbackDialog() {
                                                        @Override
                                                        public void onPositiveClick(Dialog dialog) {
                                                            dialog.dismiss();
                                                            if (resp.getIntent() != null) {
                                                                NewActivity(resp.getIntent(), 0);
                                                                act.recreate();
                                                            } else act.recreate();
                                                        }

                                                        @Override
                                                        public void onNegativeClick(Dialog dialog) {

                                                        }
                                                    };
                                                    Tools.showDialogCustom(act, resp.getNotes(), callbackDialogReload);
                                                    break;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    if (!type.equals("phone") && !type.equals("openinfo")) {
                                        String error = act.getString(R.string.error_failed_try_again);
                                        if (resp.getMessage() != null) {
                                            error = resp.getMessage();
                                        }
                                        showToast(error, 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        try {
                            if (!type.equals("phone") && !type.equals("openinfo")) {
                                showToast(act.getString(R.string.error_failed_later), 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                    try {
                        if (!type.equals("phone") && !type.equals("openinfo")) {
                            showToast(act.getString(R.string.internetcheck), 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void Logout() {
        pref().clearNotfPref();
        pref().clearUserPref();
        pref().clearGpsPref();
        pref().clearSettingsPref();
        try {
            StopTracking();
            StopPushServices();
            showToast(ctx.getString(R.string.success_logout), 0);
            goHome();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SendFCMToken() {
        User user = new User();
        if (pref().IsUserlogged()) {
            user.setUId(pref().GetUserid());
            user.setUserName(pref().GetUseruname());
            user.setPassword(pref().GetUserpass());
        }
        user.setFcmtoken(pref().GetToken());
        if (pref().GetOldToken() != null) {
            user.setOldFcmtoken(pref().GetOldToken());
        }
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.FCM_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = api().operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ServerResponse> call, @NonNull retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp != null) {
                    if (resp.getResult() != null) {
                        if (resp.getResult().equals(Constants.SUCCESS)) {
                            pref().saveNotificationSubscription(false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
            }
        });
    }

    public void goHome() {
        Intent intent = new Intent(act, ActivityStore.class);
        boolean isLogged = pref().IsUserlogged();
        if (pref().GetUsertype() != null && (pref().GetUsertype().equals("seller") || pref().GetUsertype().equals("admin"))) {
            intent = new Intent(act, ActivityBackend.class);
        } else {
            String role = pref().getRole();
            if (role != null) {
                if (role.equals("worker")) {
                    if (isLogged) intent = new Intent(act, ActivityBackend.class);
                    else intent = new Intent(act, ActivityWorker.class);
                } else if (role.equals("delivery")) {
                    if (isLogged) intent = new Intent(act, ActivityBackend.class);
                    else intent = new Intent(act, ActivityDelivery.class);
                }
            }
        }
        act.finishAffinity();
        act.startActivity(intent);
    }

    public void NewActivity(IntentData intentData, int id) {
        Bundle intentBundle;
        Intent intent;
        int type = intentData.getIntent();
        int reqID = intentData.getReqid();
        if (id != 0) reqID = id;
        int reqMID = intentData.getReqMid();
        int reqSID = intentData.getReqSid();
        int reqAID = intentData.getReqAid();
        String reqType = intentData.getReqtype();
        String reqSType = intentData.getReqStype();
        String reqAType = intentData.getReqAtype();
        String reqPType = intentData.getReqPtype();
        switch (type) {
            case 1:
                intent = new Intent(act, ActivityRecycler.class);
                break;
            case 2:
                intent = new Intent(act, ActivityView.class);
                break;
            case 3:
                intent = new Intent(act, ActivityMaps.class);
                break;
            case 4:
                intent = new Intent(act, ActivityMessages.class);
                break;
            case 5:
                intent = new Intent(act, ActivityNotifications.class);
                break;
            case 6:
                intent = new Intent(act, ActivityForm.class);
                break;
            case 7:
                intent = new Intent(act, ActivityLogin.class);
                break;
            case 8:
                intent = new Intent(act, ActivityRegister.class);
                break;
            case 9:
                intent = new Intent(act, ActivityProfile.class);
                break;
            case 10:
                intent = new Intent(act, ActivityContact.class);
                break;
            case 11:
                intent = new Intent(act, ActivityStore.class);
                break;
            case 14:
                intent = new Intent(act, ActivityBackend.class);
                break;
            case 15:
                if (pref().IsUserlogged()) {
                    if (pref().GetUsertype().equals(Constants.USER)) {
                        act.startActivity(new Intent(act, ActivityOrderHistory.class));
                        return;
                    } else intent = new Intent(act, ActivityRecycler.class);
                } else intent = new Intent(act, ActivitySignIn.class);
                break;
            case 17:
                Intent i = new Intent(act, ActivityProductDetails.class);
                i.putExtra(EXTRA_OBJECT_ID, (long) reqID);
                i.putExtra(EXTRA_FROM_NOTIF, false);
                act.startActivity(i);
                return;
            case 19:
                intent = new Intent(act, ActivityShoppingCart.class);
                break;
            case 20:
                boolean Logged = (reqMID != 0);
                saveAction(reqID, reqType, reqSID, reqAID, Logged);
                return;
            case 21:
                ConversationActivity(reqID, reqType, reqAID);
                return;
            case 23:
                URL uri = null; // missing 'http://' will cause crashed
                try {
                    uri = get_url(reqType);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (uri != null) OpenLink(uri);
                return;
            case 24:
                CallNumber(reqType);
                return;
            case 25:
                SendSms(reqType);
                return;
            case 26:
                String[] addresses = {reqType};
                composeEmail(addresses, reqAType);
                return;
            case 27:
                AlertDialog.Builder dialog = new AlertDialog.Builder(act)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(reqAType)
                        .setMessage(reqType);
                dialog.setOnKeyListener(new AlertDialog.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dlg, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) dlg.dismiss();
                        return true;
                    }
                });
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss(); // dialog dismiss without button press
                    }
                });
                notfmsgdialog = dialog.create();
                notfmsgdialog.show();
                return;
            case 28:
                intent = new Intent(act, ActivitySignIn.class);
                break;
            case 29:
                showRateDialog((long) reqID, 4, null);
                return;
            case 51:
                act.recreate();
                return;
            case 52:
                goHome();
                return;
            case 53:
                act.finish();
                return;
            case 54:
                changeLang();
                return;
            case 55:
                Logout();
                return;
            default:
                intent = new Intent(act, ActivityStore.class);
        }
        intentBundle = new Bundle();
        IntentData newIntent = new IntentData(
                type,
                reqID,
                reqMID,
                reqSID,
                reqAID,
                reqType,
                reqSType,
                reqAType,
                reqPType);
        intentBundle.putParcelable("intentData", newIntent);
        intent.putExtras(intentBundle);
        act.startActivity(intent);
    }

    public User getUserInfo() {
        User user = new User();
        user.setUId(pref().GetUserid());
        user.setUserName(pref().GetUseruname());
        user.setEmail(pref().GetUserEmail());
        user.setPassword(pref().GetUserpass());
        user.setLangcode(pref().GetLang());
        user.setRole(pref().getRole());
        user.setCity(pref().getCity());
        user.setCityGroup(pref().getCityGroup());
        user.setLatitude(pref().getLatitude());
        user.setLongitude(pref().getLongitude());
        return user;
    }

    private void ConversationActivity(int cId, String cTitle, int type) {
        Intent intent = new Intent(act, ActivityMessages.class);
        Bundle bundle = new Bundle();
        bundle.putInt("convid", cId);
        bundle.putString("contit", cTitle);
        bundle.putInt("contype", type);
        intent.putExtras(bundle);
        act.startActivity(intent);
    }

    public boolean isValidEmail(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public boolean validCellPhone(String number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    public URL get_url(final String a_url) throws MalformedURLException {
        try {
            return new URL(a_url);
        } catch (final MalformedURLException e) {
            e.printStackTrace();
        }
        return new URL("http://" + a_url);
    }

    public void OpenLink(URL uri) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(uri)));
        act.startActivity(i);
    }

    private void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(act.getPackageManager()) != null) {
            act.startActivity(intent);
        }
    }

    public void CallNumber(String number) {
        Uri callIntentUri = Uri.parse("tel:" + number);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, callIntentUri);
        act.startActivity(callIntent);
    }

    public void SendSms(String phone) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", phone);
        act.startActivity(smsIntent);
    }
}