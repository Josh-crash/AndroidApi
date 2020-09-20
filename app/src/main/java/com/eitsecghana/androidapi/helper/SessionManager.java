package com.eitsecghana.androidapi.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {
    //logcat
    private static String TAG = SessionManager.class.getSimpleName();

    //sharePreferance
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context mcontext;

    private static final String  PRE_NAME ="AndroidApi";
    private static final String  KEY_IS_LOGIN = "isLoggedIn";

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this.mcontext = context;
        sharedPreferences = mcontext.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setLogin(Boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGIN, isLoggedIn);
        editor.commit();
        Log.d(TAG, "User login modified");
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGIN, false);
    }
}
