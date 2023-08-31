package com.example.sightzapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FirstActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        if(getSupportActionBar() !=null) {
            getSupportActionBar().setTitle("Sightz App");
        }


        //Open Login Activity
        Button buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent (FirstActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //Open Register Activity
        TextView textViewRegister = findViewById(R.id.textView_register_link);
        textViewRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent (FirstActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }
}