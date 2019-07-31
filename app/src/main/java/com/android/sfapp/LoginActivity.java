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
import android.widget.Button;
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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etDoc;
    private EditText etPassword;
    private Button btnLogin;

    private boolean isLogin = false;

    public static final String HOST = "https://cs-f.herokuapp.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etDoc = findViewById(R.id.et_doc_num);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
    }

    public void login(View view){
        sendUserInfo();
        btnLogin.setEnabled(false);
    }

    private void launchHome(){
        Intent homeActivity = new Intent(getBaseContext(), MainActivity.class);
        startActivity(homeActivity);
        finish();
    }

    private void sendUserInfo() {
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject login = new JSONObject();

        try {
            login.put("number_doc", etDoc.getText().toString());
            login.put("password", etPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, login.toString());

        Request request = new Request.Builder()
                .url(HOST + "/persons/auth")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Revisa tu conexion a internet y vuelve a intentarlo", Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                    }
                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Verifica tus datos y vuelve a intentarlo", Toast.LENGTH_SHORT).show();
                            btnLogin.setEnabled(true);
                        }
                    });
                }else {
                    String r = response.body().string();
                    Log.d("LOG", r);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            launchHome();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}