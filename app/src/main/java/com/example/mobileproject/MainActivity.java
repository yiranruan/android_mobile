package com.example.mobileproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.widget.Toast;

import com.example.mobileproject.group.ShowGroupActivity;
import com.example.mobileproject.ui.login.LoginFormState;
import com.example.mobileproject.ui.login.LoginViewModel;
import com.example.mobileproject.ui.login.LoginViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private MaterialButton verifyBtn;
    private  MaterialButton loginBtn;
    private TextInputEditText emailTextInput;
    private TextInputLayout verifyInputLayout;
    private TextInputEditText verifyCodeTextInput;
    private OkHttpClient client;
    private String email;
    private KProgressHUD hud;
    private LoginViewModel loginViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        verifyBtn = findViewById(R.id.verify_btn);
        loginBtn = findViewById(R.id.login_btn);
        loginBtn.setEnabled(false);
        emailTextInput = findViewById(R.id.emailTextInput);
        verifyInputLayout = findViewById(R.id.verifyInputLayout);
        verifyCodeTextInput = findViewById(R.id.verifyCodeTextInput);
        client = new OkHttpClient();
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...");

        /*

            Verify button click listener

         */

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailTextInput.getText().toString();
                RequestBody requestBody = new FormBody.Builder()
                        .add("userEmail", email)
                        .build();
                Log.d("useremail", "onClick: "+ email);

                Request request = new Request.Builder()
                        .url(getString(R.string.request_verify_code))
                        .post(requestBody)
                        .build();


                //check the Email address if is valid



                if (isEmail(email)){

                    hud.show();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            hud.dismiss();
                            Log.d("fail to send", "onFailure: ");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Send Email address failed", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            hud.dismiss();
                            String responseData = response.body().string();
                            Log.d("response message", "onResponse: "+ responseData);
                            try {
                                JSONObject jsonData = new JSONObject(responseData);
                                Boolean responseResult = jsonData.getBoolean("emailSent");
                                if(responseResult){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "Please Check the verify code and Log in", Toast.LENGTH_SHORT).show();
                                            loginBtn.setEnabled(true);
//                                verifyCodeTextInput.setEnabled(true);

                                        }
                                    });
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "Invalid Email...", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this,"Please enter vaild Email", Toast.LENGTH_SHORT).show();
                }


            }
        });

        /*

            Log in Button click listener

         */

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hud.show();
                String code = verifyCodeTextInput.getText().toString();
                Log.d("login", "onClick: " + code);
                RequestBody requestBody = new FormBody.Builder()
                        .add("userEmail", email)
                        .add("userCode", code)
                        .build();

                Request request = new Request.Builder()
                        .url(getString(R.string.log_in))
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        hud.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(MainActivity.this, "Log in failed, try it later", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        hud.dismiss();
                        String responseData =response.body().string();
                        Log.d("response", "login response: "+ responseData);
                        try {
                            final JSONObject jsonData = new JSONObject(responseData);
                            String userID = jsonData.getString("userID");
                            String token = jsonData.getString("token");
                            Boolean result = jsonData.getBoolean("result");

                            Intent intent = new Intent(MainActivity.this, ShowGroupActivity.class);
                            intent.putExtra("userID",userID);
                            intent.putExtra("token", token);
                            intent.putExtra("userName", jsonData.getString("userName"));

                            if (result){
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Failed, please verify your code", Toast.LENGTH_SHORT).show();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Toast.makeText(MainActivity.this, "Failed, please verify your code", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginBtn.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    emailTextInput.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    verifyCodeTextInput.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(emailTextInput.getText().toString(),
                        verifyCodeTextInput.getText().toString());
            }
        };

        emailTextInput.addTextChangedListener(afterTextChangedListener);
        verifyCodeTextInput.addTextChangedListener(afterTextChangedListener);



    }


    private boolean isEmail(String string) {
        if (string == null)
            return false;
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(string);
        if (m.matches())
            return true;
        else
            return false;
    }

}
