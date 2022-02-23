package com.alfraza.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.transformations.Animate;
import com.alfraza.app.helpers.utilities.CallbackDialog;
import com.alfraza.app.helpers.utilities.DialogUtils;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.helpers.utilities.NetworkCheck;

public class ActivitySplash extends AppCompatActivity {

    private Session session;
    private RelativeLayout first_start, splash;

    // override the base context of application to update default locale for this activity
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(this);
        setContentView(R.layout.activity_splash);
        first_start = findViewById(R.id.first_start_layout);
        splash = findViewById(R.id.splash_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (first_start.getVisibility() == View.VISIBLE) first_start.setVisibility(View.GONE);
        if (splash.getVisibility() == View.GONE) splash.setVisibility(View.VISIBLE);
        startProcess();
    }

    private void startProcess() {
        if (!NetworkCheck.isConnect(this)) {
            dialogNoInternet();
        } else {
            startActivityMainDelay();
        }
    }

    private void startActivityMainDelay() {
        // Show splash screen for 2.5 seconds
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                load();
            }
        }, 2500);
    }

    private void load() {
        // check if user role is set
        if (session.pref().getRole() == null) {
            AppCompatButton lang = findViewById(R.id.btn_lang);
            lang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    session.changeLang();
                }
            });
            AppCompatButton client = findViewById(R.id.btn_get_it);
            client.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    session.pref().setRole("client");
                    Intent intent = new Intent(ActivitySplash.this, ActivityLocation.class);
                    startActivity(intent);
                    finish(); // kill current activity
                }
            });
            AppCompatButton worker = findViewById(R.id.btn_make_it);
            worker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    session.pref().setRole("worker");
                    Intent intent = new Intent(ActivitySplash.this, ActivityLocation.class);
                    startActivity(intent);
                    finish(); // kill current activity
                }
            });
            AppCompatButton delivery = findViewById(R.id.btn_deliver_it);
            delivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    session.pref().setRole("delivery");
                    Intent intent = new Intent(ActivitySplash.this, ActivityLocation.class);
                    startActivity(intent);
                    finish(); // kill current activity
                }
            });
            Animate.slideToBottom(splash, null);
            Animate.slideToTop(first_start, null);
        } else {
            Intent intent = new Intent(this, ActivityLocation.class);
            startActivity(intent);
            finish(); // kill current activity
        }
    }

    public void dialogNoInternet() {
        Dialog dialog = new DialogUtils(this).buildDialogWarning(R.string.title_no_internet, R.string.msg_no_internet, R.string.TRY_AGAIN, R.string.CLOSE, R.drawable.img_no_internet, new CallbackDialog() {
            @Override
            public void onPositiveClick(Dialog dialog) {
                dialog.dismiss();
                retryOpenApplication();
            }

            @Override
            public void onNegativeClick(Dialog dialog) {
                finish();
            }
        });
        dialog.show();
    }

    // make a delay to start next activity
    private void retryOpenApplication() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startProcess();
            }
        }, 2000);
    }
}