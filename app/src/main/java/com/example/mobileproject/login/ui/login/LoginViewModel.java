package com.example.mobileproject.login.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.example.mobileproject.R;
import com.example.mobileproject.group.ShowGroupActivity;
import com.example.mobileproject.login.data.LoginRepository;
import com.example.mobileproject.login.data.Result;
import com.example.mobileproject.login.data.model.LoggedInUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private OkHttpClient client = new OkHttpClient();
    private String userID;
    private String token;
    private Boolean result;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(Request request, Context context) {
        // can be launched in a separate asynchronous job
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("login_r", "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//
                String responseData =response.body().string();
                Log.d("response", "login response: "+ responseData);
                try {
                    final JSONObject jsonData = new JSONObject(responseData);
                    userID = jsonData.getString("userID");
                    token = jsonData.getString("token");
                    result = jsonData.getBoolean("result");
                    Log.d("fake22", "call: "+result);
                    Log.d("fake22", "call: "+userID);
                    Log.d("fake22", "call: "+token);
                    Result<LoggedInUser> result = loginRepository.login(userID, token);

                    if (result instanceof Result.Success) {
                        Log.d("login_r2", "login2: true2");
                        LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
                        Log.d("login_r2", "login2: true2"+data.getUserId());
                        loginResult.setValue(new LoginResult(new LoggedInUserView(data.getUserId())));
                        Log.d("login_r2", "login2: true3");
                    } else {
                        Log.d("login_r", "login2: failed");
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                    }

                    Intent intent = new Intent(context, ShowGroupActivity.class);
                    intent.putExtra("userID",userID);
                    intent.putExtra("token", token);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }


    public void verifyCodeRequest(String email, Request request) {
        //check the Email address if is valid

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("fail to send", "onFailure: ");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "Send Email address failed", Toast.LENGTH_SHORT).show();
//                    }
//                });

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("response message", "onResponse: "+ responseData);
                try {
                    JSONObject jsonData = new JSONObject(responseData);
                    Boolean responseResult = jsonData.getBoolean("emailSent");
//                    if(responseResult){
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(MainActivity.this, "Please Check the verify code and Log in", Toast.LENGTH_SHORT).show();
//                                loginBtn.setEnabled(true);
////                                verifyCodeTextInput.setEnabled(true);
//
//                            }
//                        });
//                    }else{
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(MainActivity.this, "Invalid Email...", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }

    public void loginDataChanged(String email, String code) {
        if (!isUserNameValid(email)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(code)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder email validation check
    private boolean isUserNameValid(String email) {
        if (email == null) {
            return false;
        }
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return !email.trim().isEmpty();
        }
    }

    // A placeholder code validation check
    private boolean isPasswordValid(String code) {
        return code != null && code.trim().length() > 5;
    }
}
