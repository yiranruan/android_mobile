package com.example.mobileproject.group;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.textclassifier.ConversationActions;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.mobileproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

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

public class GroupJoinActivity extends AppCompatActivity {

    private TextInputEditText code;
    private MaterialButton joinBtn;
    private String userID;
    private String token;
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_join);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        token = intent.getStringExtra("token");

        code = findViewById(R.id.join_code_tf);
        joinBtn = findViewById(R.id.join_group_btn);
        String groupCode = code.getText().toString();

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RequestBody requestBody = new FormBody.Builder()
                        .add("userID", userID)
                        .add("token", token)
                        .add("groupCode",groupCode)
                        .build();

                Request request = new Request.Builder()
                        .url(getString(R.string.join_group))
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Toast.makeText(GroupJoinActivity.this, "Network issue, Please check your network", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonData = new JSONObject(responseData);
                            String groupInfo = jsonData.getString("group");
                            if (jsonData.getBoolean("result")){
                                Intent intent = new Intent();
                                intent.putExtra("joinedGroup", groupInfo);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });



            }
        });
    }
}
