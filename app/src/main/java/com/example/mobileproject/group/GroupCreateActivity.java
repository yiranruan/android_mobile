package com.example.mobileproject.group;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.ETC1;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobileproject.R;
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

public class GroupCreateActivity extends AppCompatActivity {

    private String userID;
    private String token;

    private Button createGroup;
    private TextInputEditText groupName;
    private TextInputEditText subjectName;
    private TextInputEditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        createGroup = (Button) findViewById(R.id.btnCreate);
        groupName = findViewById(R.id.create_group_name_tf);
        subjectName = findViewById(R.id.create_subj_nm_tf);
        description = findViewById(R.id.create_desc_tf);

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                String createdGroupName = groupName.getText().toString();
                String createdSubjectName = subjectName.getText().toString();
                String createdDescription = description.getText().toString();

                JSONObject data = new JSONObject();
                try {
                    data.put("groupName", groupName);
                    data.put("subjectName", subjectName);
                    data.put("description", description);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String sentData = data.toString();
                RequestBody requestBody = new FormBody.Builder()
                        .add("groupInfo", sentData)
                        .add("userID", userID)
                        .add("token", token)
                        .build();

                Request request = new Request.Builder()
                        .url(getString(R.string.create_group))
                        .post(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Toast.makeText(GroupCreateActivity.this, "Network issue, try it later", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonData = new JSONObject(responseData);
                            String groupInfo = jsonData.getString("groupInfo");
                            intent.putExtra("groupInfo", groupInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });


            }
        });
    }
}
