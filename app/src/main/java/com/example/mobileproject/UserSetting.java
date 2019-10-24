package com.example.mobileproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.kaopiz.kprogresshud.KProgressHUD;

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

public class UserSetting extends AppCompatActivity {

    private String userID;
    private String token;
    private String userName;
    private KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        MaterialButton saveBtn = findViewById(R.id.save_user_setting);
        TextInputEditText newUserName = findViewById(R.id.user_name_input);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        token = intent.getStringExtra("token");
        userName = intent.getStringExtra("userName");
//        newUserName.setText(userName);

        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...");

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hud.show();
                String userName = newUserName.getText().toString();
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("userID",userID)
                        .add("token", token)
                        .add("userName", userName)
                        .build();

                Request request = new Request.Builder()
                        .url(getString(R.string.user_update))
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Toast.makeText(UserSetting.this, "Network issue", Toast.LENGTH_SHORT).show();
                        hud.dismiss();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseDate = response.body().string();
                        hud.dismiss();
                        try {
                            JSONObject jsonData = new JSONObject(responseDate);
                            Boolean result = jsonData.getBoolean("result");
                            Intent intent_result = new Intent();
                            intent_result.putExtra("result", result);
                            setResult(RESULT_OK, intent_result);

                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });



    }
}
