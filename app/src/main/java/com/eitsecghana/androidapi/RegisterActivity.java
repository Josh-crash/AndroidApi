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

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private TextInputEditText fullname_field;
    private TextInputEditText email_field;
    private TextInputEditText password_field;

    private Button register;
    private TextView login;

    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private SQLiteHandler db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullname_field = (TextInputEditText) findViewById(R.id.fullname_textField);
        email_field = (TextInputEditText) findViewById(R.id.email_textField);
        password_field = (TextInputEditText) findViewById(R.id.password_textField);

        register = (Button) findViewById(R.id.register_button);
        login = findViewById(R.id.login_button);

        progressDialog =new ProgressDialog(this);
        sessionManager = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());


        //check if user is already login
        if (sessionManager.isLoggedIn()){
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


        //register
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = fullname_field.getText().toString().trim();
                String email = email_field.getText().toString().trim();
                String password = password_field.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    registerUser(name, email, password);
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter your details", Toast.LENGTH_LONG).show();

                }
            }
        });

       //login screen
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    private void registerUser(final String name, final String email, final String password) {

        //tag used to cancel request
        String tag_string_request = "req_register";
        progressDialog.setMessage("Registering");
        showDialog();

        StringRequest stringRequest = new StringRequest(Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register response" + response.toString());
                hideDialog();

                try {
                    JSONObject json = new JSONObject(response);
                    boolean error = json.getBoolean("error");
                    if (!error) {
                        //user successfull store to msql
                        //now store in sqlite
                        String uid = json.getString("uid");

                        JSONObject user = json.getJSONObject("user");
                        String name = user.getString("username");
                        String email = user.getString("email");
                        String created_at = user.getString("created_at");

                        //inserting to table
                        db.addUser(name, email, uid, created_at);
                        Toast.makeText(getApplicationContext(), "registration successfull", Toast.LENGTH_LONG).show();
                        //lauch mainactivity
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();


                    } else {
                        String errorMessage = json.getString("errorMessage");
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "registration error" + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }){
           @Override
           protected Map<String, String>getParams() {
               Map<String, String> params = new HashMap<String, String>();
               params.put("name",name);
               params.put("email",email);
               params.put("password",password);

               return params;
           }


        };


        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_request);
    }

    private void hideDialog() {
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    private void showDialog() {
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
    }
}