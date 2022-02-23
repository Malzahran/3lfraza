package com.alfraza.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alfraza.app.activities.users.ActivityLogin;
import com.alfraza.app.helpers.session.Session;
import com.alfraza.app.helpers.utilities.LanguageHelper;
import com.alfraza.app.models.IntentData;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class ActivityDelivery extends AppCompatActivity implements View.OnClickListener {

    private Session session;

    // override the base context of application to update default locale for this activity
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(this);
        setContentView(R.layout.activity_delivery);
        AppCompatButton btn_lang = findViewById(R.id.btn_lang);
        btn_lang.setOnClickListener(this);
        AppCompatButton btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        AppCompatButton btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (session.pref().IsUserlogged()) session.goHome();
        else session.checkArea();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_lang:
                session.changeLang();
                break;
            case R.id.btn_login:
                Intent login = new Intent(this, ActivityLogin.class);
                login.putExtra("type", "delivery");
                startActivity(login);
                break;
            case R.id.btn_register:
                IntentData registerIntent = new IntentData(6, 0, 0, 0, 0, "register_forms", "add_delivery_form", null, "add_delivery");
                session.NewActivity(registerIntent, 0);
        }
    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else finishAffinity();
    }
}
