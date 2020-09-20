package com.eitsecghana.androidapi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.eitsecghana.androidapi.helper.SQLiteHandler;
import com.eitsecghana.androidapi.helper.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity  {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private TextView loginTextLink;

    private TextView loginButton;
    private TextInputEditText emailField;
    private TextInputEditText passwordField;
    private ProgressDialog progressDialog;

    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginTextLink = (TextView) findViewById(R.id.login_text);
        emailField = (TextInputEditText)findViewById(R.id.email_textField);
        passwordField = (TextInputEditText) findViewById(R.id.password_textField);
        loginButton = findViewById(R.id.login_button);

        //progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //database
        db = new SQLiteHandler(getApplicationContext());

        //session manager
        session = new SessionManager(getApplicationContext());

        //check if user is already login
        if (session.isLoggedIn()){
            //if user is login then we start the main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // login button is click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                //check if email field and password field is empty
                if (!email.isEmpty() && !password.isEmpty()){
                    checkLogin(email, password);
                }else{
                    Toast.makeText(getApplicationContext(), "please email and password is required",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        //register text is click i use button because is clickable
        loginTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private void checkLogin(final String email, final String password) {
        String tag__steing_req = "req_login";
        progressDialog.setMessage("logging in...... ");
        showDialog();

        StringRequest stringRequest = new StringRequest(Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "login response" + response.toString());
                hideProgressDialog();

                try {
                    JSONObject json = new JSONObject();
                    boolean error = json.getBoolean("error");
                    if (!error) {
                        session.setLogin(true);

                        String uid = json.getString("uid");
                        JSONObject user = json.getJSONObject("user");
                        String name = json.getString("name");
                        String email = json.getString("email");
                        String created_at = json.getString("created_at");

                        db.addUser(name, email, uid, created_at);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        String errorMessage = json.getString("error");
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "json error" + e.getMessage(), Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login error" + error.getMessage());
                hideProgressDialog();
            }
        }){
            @Override
            protected Map<String, String>getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };

        //adding request to queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag__steing_req);

    }


    private void showDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }



}