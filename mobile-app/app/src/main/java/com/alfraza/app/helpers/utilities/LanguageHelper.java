package com.alfraza.app.helpers.utilities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import com.alfraza.app.helpers.session.Session;

import java.util.Locale;

public class LanguageHelper {
    // returns Context having application default locale for all activities
    public static Context onAttach(Context context) {
        Session session = new Session(context);
        String lang = session.pref().GetLang();
        return updateResources(context, lang);
    }


    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            config.setLocale(locale);
        else config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
        return context;
    }
}
