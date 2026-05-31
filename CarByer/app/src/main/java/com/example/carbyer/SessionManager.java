package com.example.carbyer;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "carbyer_session";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_JSON = "user_json";
    private SharedPreferences prefs;

    public SessionManager(Context ctx){
        prefs = ctx.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void save(String token, String userJson){
        prefs.edit().putString(KEY_TOKEN, token).putString(KEY_USER_JSON, userJson).apply();
    }

    public String getToken() { return prefs.getString(KEY_TOKEN, null); }

    public String getUserJson() { return prefs.getString(KEY_USER_JSON, null); }

    public boolean isLoggedIn() { return getToken() != null ;}

    public void clear() { prefs.edit().clear().apply(); }

}
