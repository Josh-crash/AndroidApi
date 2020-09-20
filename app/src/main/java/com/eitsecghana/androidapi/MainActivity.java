package com.eitsecghana.androidapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.eitsecghana.androidapi.helper.SQLiteHandler;
import com.eitsecghana.androidapi.helper.SessionManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private TextView text_name;
    private TextView text_email;
    private Button logout;

    private SessionManager sessionManager;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_name = (TextView) findViewById(R.id.name_text);
        text_email = (TextView) findViewById(R.id.email_text);
        logout = (Button) findViewById(R.id.logout_button);

        sessionManager = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        if (sessionManager.isLoggedIn()) {
            logoutUser();
        }
        //feteching user data from sqlite
        HashMap<String, String> user = db.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");

        //display it on textview
        text_name.setText(name);
        text_email.setText(email);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

    }

    private void logoutUser() {
        sessionManager.setLogin(false);
        db.deleteUser();


        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}