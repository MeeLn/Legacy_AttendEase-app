package com.example.loginscreen;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "LoginSession";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_IS_RECOGNIZED = "isRecognized";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String email, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ROLE, role);
        editor.putBoolean(KEY_IS_RECOGNIZED, false);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, null);
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, null);
    }

    public boolean isRecognized() {
        return pref.getBoolean(KEY_IS_RECOGNIZED, false);
    }

    public void setRecognized(boolean isRecognized) {
        editor.putBoolean(KEY_IS_RECOGNIZED, isRecognized);
        editor.commit();
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }
}