package com.snail.education.app;

import android.content.Context;

/**
 * Created by tianxiaopeng on 15-1-6.
 */
public class SEConfig {


    private static SEConfig s_instance;

    private String _apiBaseURL;
    private Context _context;
    private String _versionText;

    private SEConfig() {
    }

    public static SEConfig getInstance() {
        if (s_instance == null) {
            s_instance = new SEConfig();
        }
        return s_instance;
    }

    public void init(String apiBaseURL, String versionText, Context context) {
        _apiBaseURL = apiBaseURL;
        _versionText = versionText;
        _context = context;
    }

    public String getVersionText() {
        return _versionText;
    }

    public String getAPIBaseURL() {
        return _apiBaseURL;
    }

    public Context getContext() {
        return _context;
    }
}

