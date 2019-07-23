package com.android.sfapp;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

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
        Intent homeActivity = new Intent(getBaseContext(), MainActivity.class);
        startActivity(homeActivity);
        finish();
    }
}