package com.example.mobileproject.group;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.example.mobileproject.R;
import com.google.android.material.textfield.TextInputEditText;

public class GroupJoinActivity extends AppCompatActivity {
    private Button createGroup;
    private TextInputEditText eT_groupName;
    private EditText eT_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_join);

        createGroup = (Button) findViewById(R.id.btnJoin);
        eT_groupName = findViewById(R.id.join_name_edit_text);
        eT_pw = (EditText) findViewById(R.id.join_pw_edit_text);

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                String name = eT_groupName.getText().toString();
                String pw = eT_pw.getText().toString();
                intent.putExtra("group_name", name);
                intent.putExtra("password", pw);
                setResult(RESULT_OK, intent);
//                Toast.makeText(GroupCreateActivity.this, "Create Group Success!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
