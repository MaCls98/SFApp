package com.android.sfapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.sfapp.R;

public class HomeActivity extends AppCompatActivity {

    private Spinner spObras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String[] arraySpinner = new String[] {
                "1", "2", "3", "4", "5", "6", "7"
        };
        spObras = findViewById(R.id.sp_obras);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spObras.setAdapter(adapter);
    }
}
