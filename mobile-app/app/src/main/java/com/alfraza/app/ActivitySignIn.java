package com.alfraza.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.view.View;

import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.alfraza.app.activities.users.ActivityLogin;
import com.alfraza.app.activities.users.ActivityRegister;
import com.alfraza.app.api.ServerRequest;
import com.alfraza.app.api.ServerResponse;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.Constants;
import com.alfraza.app.models.User;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;

public class ActivitySignIn extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    private Session session;
    private AppCompatButton btn_login, btn_face, btn_google, btn_register, btn_home;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private ProfileTracker profileTracker;
    private String fbImg, Name;
    private ProgressDialog progressDialog;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (session.pref().IsUserlogged()) finish();
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
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        final String Userid = loginResult.getAccessToken().getUserId();
                        if (Profile.getCurrentProfile() == null) {
                            profileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                    // profile2 is the new profile
                                    fbImg = "https://graph.facebook.com/" + Userid + "/picture?width=250&height=250";
                                    Name = profile2.getName();
                                    profileTracker.stopTracking();
                                    FaceLoginProcess(Userid, Name, fbImg);
                                }
                            };
                            // no need to call startTracking() on mProfileTracker
                            // because it is called by its constructor, internally.
                        } else {
                            Profile profile;
                            profile = Profile.getCurrentProfile();
                            fbImg = "https://graph.facebook.com/" + Userid + "/picture?width=250&height=250";
                            Name = profile.getName();
                            FaceLoginProcess(Userid, Name, fbImg);
                        }
                    }

                    @Override
                    public void onCancel() {
                        showAlert();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        exception.printStackTrace();
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                                signInWithFacebook();
                            }
                        } else showAlert();
                    }
                });
        // Configure sign-in to request the user's ID, email address, and basic profile.
        // ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        setContentView(R.layout.activity_sign_in);
        initViews();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String userID;
            if (acct != null) {
                userID = acct.getId();
                String personName = acct.getDisplayName();
                String email = acct.getEmail();
                GoogleLoginProcess(userID, personName, email);
            }
        } else showAlert();
    }

    private void signInWithFacebook() {
        disconnectFromFacebook();
        LoginManager.getInstance().logInWithReadPermissions(ActivitySignIn.this,
                Arrays.asList("public_profile", "email"));
    }

    private void signInWithGoogle() {
        disconnectFromGoogle();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void disconnectFromFacebook() {
        try {
            if (AccessToken.getCurrentAccessToken() != null) LoginManager.getInstance().logOut();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disconnectFromGoogle() {
        if (mGoogleApiClient != null) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void showAlert() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.error_failed)
                .setMessage(R.string.permission_not_granted)
                .setPositiveButton(R.string.okbtn, null)
                .show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void initViews() {
        btn_home = findViewById(R.id.btn_home);
        btn_register = findViewById(R.id.btn_register);
        btn_login = findViewById(R.id.btn_login);
        btn_face = findViewById(R.id.btn_face);
        btn_google = findViewById(R.id.btn_google);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        btn_home.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_google.setOnClickListener(this);
        btn_face.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
                session.goHome();
                break;
            case R.id.btn_login:
                Intent intent = new Intent(this, ActivityLogin.class);
                startActivity(intent);
                break;
            case R.id.btn_register:
                Intent rIntent = new Intent(this, ActivityRegister.class);
                startActivity(rIntent);
                break;
            case R.id.btn_face:
                signInWithFacebook();
                break;
            case R.id.btn_google:
                signInWithGoogle();
                break;
        }
    }

    private void FaceLoginProcess(String username, final String name, String profileimg) {
        try {
            DisableButtons(1);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        User user = new User();
        user.setUserName(username);
        user.setName(name);
        user.setProfileimg(profileimg);
        user.setLangcode(session.pref().GetLang());
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.LOGIN_OPERATION);
        request.setParent(Constants.LOGIN_FACE);
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
                                progressDialog.dismiss();
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
                            if (resp.getUser().RequiredPhone() == 1)
                                session.pref().setRequiredPhone(true);
                            else session.pref().setRequiredPhone(false);
                            session.pref().SetUseruname(resp.getUser().getUsername());
                            session.pref().SetUserpass(resp.getUser().getPassword());
                            session.pref().SetUsertype(resp.getUser().getUsertype());
                            session.pref().SetUserbalance(resp.getUser().getBalance());
                            session.pref().AllowCredit(resp.getUser().AllowCredit());
                            session.pref().AllowChat(resp.getUser().AllowChat());
                            session.pref().SetUserpic(resp.getUser().getProfileimg());
                            session.pref().SetAuthMethod(Constants.LOGIN_FACE);
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
                                progressDialog.dismiss();
                                String error = getString(R.string.error_login_failed);
                                if (resp.getMessage() != null) error = resp.getMessage();
                                showSnackBar(error, 2, 2);
                                disconnectFromFacebook();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    try {
                        DisableButtons(0);
                        progressDialog.dismiss();
                        showSnackBar(getString(R.string.error_failed_later), 0, 1);
                        disconnectFromFacebook();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    DisableButtons(0);
                    progressDialog.dismiss();
                    showSnackBar(getString(R.string.internetcheck), 2, 2);
                    disconnectFromFacebook();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void GoogleLoginProcess(String username, String name, String email) {
        try {
            DisableButtons(1);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        User user = new User();
        user.setUserName(username);
        user.setName(name);
        user.setEmail(email);
        user.setLangcode(session.pref().GetLang());
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.LOGIN_OPERATION);
        request.setParent(Constants.LOGIN_GOOGLE);
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
                                progressDialog.dismiss();
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
                            session.pref().SetUserpic(resp.getUser().getProfileimg());
                            session.pref().SetAuthMethod(Constants.LOGIN_GOOGLE);
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
                                progressDialog.dismiss();
                                String failmsg = getString(R.string.error_login_failed);
                                if (resp.getMessage() != null) failmsg = resp.getMessage();
                                showSnackBar(failmsg, 3, 2);
                                disconnectFromGoogle();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    try {
                        progressDialog.dismiss();
                        DisableButtons(0);
                        showSnackBar(getString(R.string.error_failed_later), 0, 1);
                        disconnectFromGoogle();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<ServerResponse> call, @NonNull Throwable t) {
                try {
                    DisableButtons(0);
                    progressDialog.dismiss();
                    showSnackBar(getString(R.string.internetcheck), 3, 2);
                    disconnectFromGoogle();
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
                    switch (action) {
                        case 2:
                            signInWithFacebook();
                            break;
                        case 3:
                            signInWithGoogle();
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
            session.orientationManager(true);
            btn_home.setOnClickListener(null);
            btn_register.setOnClickListener(null);
            btn_login.setOnClickListener(null);
            btn_face.setOnClickListener(null);
            btn_google.setOnClickListener(null);
        } else {
            session.orientationManager(false);
            btn_home.setOnClickListener(this);
            btn_register.setOnClickListener(this);
            btn_login.setOnClickListener(this);
            btn_face.setOnClickListener(this);
            btn_google.setOnClickListener(this);
        }
    }
}