package com.example.mobileproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.widget.Toast;

import com.example.mobileproject.group.ShowGroupActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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


                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
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

        verifyCodeTextInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (loginBtn.isEnabled()){
                    verifyInputLayout.setError(null);
                }
                else{
                    verifyInputLayout.setError("Please verify your Email first!");
                }
                return false;
            }

        });

        /*

            Log in Button click listener

         */

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Log in failed, try it later", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//
                        String responseData =response.body().string();
                        Log.d("response", "login response: "+ responseData);
                        try {
                            final JSONObject jsonData = new JSONObject(responseData);
                            String userID = jsonData.getString("userID");
                            String token = jsonData.getString("token");
                            String result = jsonData.getString("result");

                            Intent intent = new Intent(MainActivity.this, ShowGroupActivity.class);
                            intent.putExtra("userID",userID);
                            intent.putExtra("token", token);

                            if (result.equals("success")){
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(MainActivity.this, "Failed, please verify your code", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });



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
