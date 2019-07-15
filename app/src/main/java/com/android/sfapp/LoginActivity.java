package com.android.sfapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText etDoc;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etDoc = findViewById(R.id.et_doc_num);
        etPassword = findViewById(R.id.et_password);
    }

    public void login(View view){
        Intent homeActivity = new Intent(getBaseContext(), HomeActivity.class);
        startActivity(homeActivity);
        finish();
    }
}