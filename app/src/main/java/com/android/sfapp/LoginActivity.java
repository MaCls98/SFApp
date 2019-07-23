package com.android.sfapp;

import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sfapp.model.Nomina;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etDoc;
    private EditText etPassword;

    private boolean isLogin = false;

    public static final String HOST = "https://cs-f.herokuapp.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etDoc = findViewById(R.id.et_doc_num);
        etPassword = findViewById(R.id.et_password);
    }

    public void login(View view){
        Runnable r = new Runnable() {
            @Override
            public void run() {
                sendUserInfo();
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 2000);
        if (isLogin){
            Intent homeActivity = new Intent(getBaseContext(), MainActivity.class);
            startActivity(homeActivity);
            finish();
        }else {
            Toast.makeText(this, "Verifica el numero de documento y la contraseña", Toast.LENGTH_SHORT).show();
        }
        
    }

    private void sendUserInfo() {
        OkHttpClient client = new OkHttpClient();

        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("number_doc", etDoc.getText().toString())
                .add("password", etPassword.getText().toString());

        RequestBody requestBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(HOST + "/persons/auth")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("ERROR", e.getMessage());
                isLogin = false;
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("MATERIALES", response.body().string());
                isLogin = true;
            }
        });
    }
}