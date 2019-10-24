package com.example.mobileproject.group;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.mobileproject.R;
import com.example.mobileproject.UserSetting;
import com.example.mobileproject.tasks.HorizontalCoordinatorNtbActivity;
import com.example.mobileproject.tasks.ShowTaskActivity;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ShowGroupActivity extends AppCompatActivity {

    private static final long RIPPLE_DURATION = 250;
    ViewPager viewPager;
    Adapter adapter;
    List<Model> models;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private FloatingActionsMenu menuMultipleActions;

    private int res = R.drawable.content_music;
    private int page_position = 0;
    private String userID = "6";
    private String token = "umeuuuufae";
    private String taskID;
    private OkHttpClient client;
    private Context mContext;
    private KProgressHUD hud;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group);
        models = new ArrayList<>();
        mContext = getApplicationContext();
        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...");
        hud.show();

        /*

            get group information from server

         */

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        token = intent.getStringExtra("token");
        userName = intent.getStringExtra("userName");

        RequestBody requestBody = new FormBody.Builder()
                .add("userID", userID)
                .add("token", token)
                .build();

        Request request = new Request.Builder()
                .url(getString(R.string.get_group_list))
                .post(requestBody)
                .build();

        client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                hud.dismiss();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    JSONObject jsonData = new JSONObject(responseData);
//                    userName = jsonData.getString("userName");

                    JSONArray jsonArray = jsonData.getJSONArray("list");
                    Log.d("group list", "onResponse: " + jsonArray.length());
                    for (int i = 0 ; i < jsonArray.length(); i++) {
                        JSONObject element = jsonArray.getJSONObject(i);
                        Log.d("group", "group " + i + ": " + element.toString());
                        int groupID = element.getInt("groupID");
                        String groupName = element.getString("groupName");
                        String description = element.getString("description");
                        int memberCount = element.getInt("memberCount");
                        String inviteCode = element.getString("groupCode");
                        String subjectName = element.getString("subjectName");
                        Log.d("TEST", "onResponse: 1111");
                        models.add(new Model(groupID, memberCount, groupName, subjectName, inviteCode, description));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                    models.add(new Model(0,  1, "Personal", " ","", "This is a personal task"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            adapter.notifyDataSetChanged();
                            hud.dismiss();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        final View createG = findViewById(R.id.create);
        createG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowGroupActivity.this, GroupCreateActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("token", token);
                checkExpansion();
                startActivityForResult(intent, 2);
            }
        });

        final View joinG = findViewById(R.id.join);
        joinG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowGroupActivity.this, GroupJoinActivity.class);
                intent.putExtra("userID", userID);
                intent.putExtra("token", token);
                checkExpansion();
                startActivityForResult(intent, 1);
            }
        });


        final View userSettingBtn = findViewById(R.id.user_setting);
        userSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowGroupActivity.this, UserSetting.class);
                intent.putExtra("userID", userID);
                intent.putExtra("token", token);
                intent.putExtra("userName", userName);
                checkExpansion();
                startActivityForResult(intent, 3);
            }
        });





        colors = new Integer[]{
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4)
        };


        Log.d("TEST", "onResponse: 2222");
        startDrawable();

    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Log.d("item", "onContextItemSelected: "+item.getItemId());
        checkExpansion();
        switch (item.getItemId()) {//根据子菜单ID进行菜单选择判断
            case 1:
                Log.d("hhh", "onLongClick: True");
                Intent intent = new Intent(ShowGroupActivity.this, HorizontalCoordinatorNtbActivity.class);
                int groupID = models.get(page_position).getGroupID();
                intent.putExtra("groupName", models.get(page_position).getGroupName());
                intent.putExtra("groupID",groupID);
                intent.putExtra("userID", userID);
                intent.putExtra("token", token);

                startActivity(intent); // 获得position 得到特定页面
                break;
                // remove group item
            case 2:
                if (models.get(page_position).getGroupID() == 0){
                    Toast.makeText(ShowGroupActivity.this, "You cannot delete this group", Toast.LENGTH_SHORT).show();
                    break;
                }
                hud.show();
                Log.d("model_size1", "onContextItemSelected: "+models.size());
                RequestBody requestBody = new FormBody.Builder()
                        .add("userID", userID)
                        .add("token", token)
                        .add("groupID", String.valueOf(models.get(page_position).getGroupID()))
                        .build();
                Request request = new Request.Builder()
                        .url(getString(R.string.delete_group))
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        hud.dismiss();
                        Toast.makeText(ShowGroupActivity.this, "Network issue, try it later", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();
                        
                        try {
                            JSONObject jsonData = new JSONObject(responseData);
                            Boolean result = jsonData.getBoolean("result");
                            if (result){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        models.remove(page_position);
                                        Log.d("model_size2", "onContextItemSelected: "+models.size());
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }else{
                                Toast.makeText(ShowGroupActivity.this, "Your are not the creator, you cannot delete this group", Toast.LENGTH_SHORT).show();
                            }
                            hud.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            // Join a new group
            case 1:
                if (resultCode == RESULT_OK){
                    String groupInfo = data.getStringExtra("groupInfo");
                    try {
                        JSONObject jsonGroupInfo = new JSONObject(groupInfo);
                        models.add(new Model(
                                jsonGroupInfo.getInt("groupID"),
                                jsonGroupInfo.getInt("memberCount"),
                                jsonGroupInfo.getString("groupName"),
                                jsonGroupInfo.getString("subjectName"),
                                jsonGroupInfo.getString("groupCode"),
                                jsonGroupInfo.getString("description")
                                ));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(ShowGroupActivity.this, "You are in this group", Toast.LENGTH_SHORT).show();
                }
                break;

            // Create a new group
            case 2:
                Log.d("create group", "onActivityResult: 3");
                if(resultCode == RESULT_OK){
                    String group = data.getStringExtra("groupInfo");
                    Log.d("created group", "onActivityResult: " + group);
                    try {
                        JSONObject groupJson = new JSONObject(group);
                        models.add(new Model(
                                groupJson.getInt("groupID"),
                                groupJson.getInt("memberCount"),
                                groupJson.getString("groupName"),
                                groupJson.getString("subjectName"),
                                groupJson.getString("groupCode"),
                                groupJson.getString("description")
                        ));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            case 3:
                if(resultCode == RESULT_OK){
                    Boolean result = data.getBooleanExtra("result", false);
                    if (result){
                        Toast.makeText(ShowGroupActivity.this, "Change user name successfully", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(ShowGroupActivity.this, "Change user name failed", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(ShowGroupActivity.this, "Fail to create a new group, try it later", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    protected void startDrawable(){
        adapter = new Adapter(models, this);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);

        viewPager.setAdapter(adapter);
        viewPager.setPadding(100, 0,100,0);



        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                position = position % colors.length;
                if (position < (colors.length - 1)){
                    viewPager.setBackgroundColor(
                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                } else {
                    viewPager.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {
                page_position = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void checkExpansion(){
        if (menuMultipleActions.isExpanded()) {
            menuMultipleActions.collapseImmediately();
        }
    }



//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        drawerToggle.onConfigurationChanged(newConfig);
//    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return false;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (drawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                Log.d("setting","yes");
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

}
