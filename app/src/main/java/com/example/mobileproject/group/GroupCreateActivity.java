package com.example.mobileproject.group;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.ETC1;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobileproject.R;
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

public class GroupCreateActivity extends AppCompatActivity {

    private String userID;
    private String token;

    private Button createGroup;
    private TextInputEditText groupName;
    private TextInputEditText subjectName;
    private TextInputEditText description;
    private KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        token = intent.getStringExtra("token");

        createGroup = (Button) findViewById(R.id.btnCreate);
        groupName = findViewById(R.id.create_group_name_tf);
        subjectName = findViewById(R.id.create_subj_nm_tf);
        description = findViewById(R.id.create_desc_tf);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...");

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hud.show();
                String createdGroupName = groupName.getText().toString();
                String createdSubjectName = subjectName.getText().toString();
                String createdDescription = description.getText().toString();


                Log.d("fk", "onClick: "+userID);
                RequestBody requestBody = new FormBody.Builder()
                        .add("groupName",createdGroupName)
                        .add("subjectName",createdSubjectName)
                        .add("description",createdDescription)
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
                        hud.dismiss();
                        Toast.makeText(GroupCreateActivity.this, "Network issue, try it later", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonData = new JSONObject(responseData);
                            String groupInfo = jsonData.getString("group");
                            intent.putExtra("groupInfo", groupInfo);
                            Log.d("groupInfo", "onResponse: "+groupInfo);
                        } catch (JSONException e) {
                            Log.d("groupInfo", "onResponse: fail");
                            e.printStackTrace();
                        }
                        setResult(RESULT_OK, intent);
                        hud.dismiss();
                        finish();
                    }
                });


            }
        });
    }
}
