package com.eitsecghana.androidapi;

import android.app.Application;
import android.nfc.Tag;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {
    private static final String TAG = AppController.class.getSimpleName(); //to check for error in logcat
    private static AppController appControllerInstance; // creating instance  of class to avoid instantiate
    private RequestQueue requestQueue;


    @Override
    public void onCreate() {
        super.onCreate();
        appControllerInstance =this;
    }

    public static synchronized AppController getInstance() {
        return appControllerInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag){
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(request);

    }

    public <T> void addToRequestQueue(Request<T> request){
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public  void cancelPendingRequests(Object tag) {
        if(requestQueue !=null){
            requestQueue.cancelAll(tag);

        }
    }
}
