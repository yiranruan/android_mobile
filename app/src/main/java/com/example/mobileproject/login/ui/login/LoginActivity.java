package com.example.mobileproject.login.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mobileproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private MaterialButton verifyBtn;
    private  MaterialButton loginBtn;
    private TextInputEditText emailTextInput;
    private TextInputLayout verifyInputLayout;
    private TextInputEditText verifyCodeTextInput;
//    private OkHttpClient client;
    private String email;
    private String code;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        // final EditText emailTextInput = findViewById(R.id.username);
        // final EditText verifyCodeTextInput = findViewById(R.id.password);
        // final Button loginBtn = findViewById(R.id.login);
        // final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        verifyBtn = findViewById(R.id.verify_btn);
        loginBtn = findViewById(R.id.login_btn);
        loginBtn.setEnabled(false);
        emailTextInput = findViewById(R.id.emailTextInput);
        verifyInputLayout = findViewById(R.id.verifyInputLayout);
        verifyCodeTextInput = findViewById(R.id.verifyCodeTextInput);
//        client = new OkHttpClient();




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

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                // loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
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
        verifyCodeTextInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    RequestBody requestBody = new FormBody.Builder()
                            .add("userEmail", email)
                            .add("userCode", code)
                            .build();

                    Request request = new Request.Builder()
                            .url(getString(R.string.log_in))
                            .post(requestBody)
                            .build();
                    loginViewModel.login(request, LoginActivity.this);
                }
                return false;
            }
        });

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
                loginViewModel.verifyCodeRequest(email, request);

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // loadingProgressBar.setVisibility(View.VISIBLE);
                code = verifyCodeTextInput.getText().toString();
                Log.d("login_r", "onEditorAction: "+email);
                Log.d("login_r", "onEditorAction: "+code);
                RequestBody requestBody = new FormBody.Builder()
                        .add("userEmail", email)
                        .add("userCode", code)
                        .build();

                Request request = new Request.Builder()
                        .url(getString(R.string.log_in))
                        .post(requestBody)
                        .build();
                loginViewModel.login(request, LoginActivity.this);
                Log.d("login_r", "hahaha");
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
