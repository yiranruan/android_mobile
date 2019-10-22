package com.example.mobileproject.tasks;
import com.example.mobileproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kyle.calendarprovider.calendar.CalendarEvent;
import com.kyle.calendarprovider.calendar.CalendarProviderManager;


import androidx.appcompat.app.AppCompatActivity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//import com.kyle.calendarprovider.calendar.CalendarEvent;
//import com.kyle.calendarprovider.calendar.CalendarProviderManager;

public class ShowTaskActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    private EditText tv_location;
    private TextView tv_startDate;
    private TextView tv_endDate;
    private EditText tv_title;
    private EditText tv_member;



    private Button btn_cancel;
    private ImageView showimg;
    private Button btn_comfirm;

    private EditText et_note;



    private int day,month,year,hour,minute;
    private int day_x,month_x,year_x,hour_x,minute_x;
    private int day_y,month_y,year_y,hour_y,minute_y;
    private ImageView mImageView;
    private Uri image_uri;

    private FloatingActionButton mCaptureBtn;
    private Button btn_add;
    private Button btn_delete;

    /// ----

    private SwitchMaterial switch_calender;
    private Boolean send_calender = false;
    private Boolean initial_C;

    // -----

    private int position;
    private int page_code;

    private String location;
    private String startDate;
    private String dueDate;
    private String filepath;
    private int groupID;
    private String token;
    private String iamge;
    private String userID;
    private String taskID;
    private String title;
    private String description;
    private String username;
    private String status;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_task);



        tv_title = findViewById(R.id.et_Title);
        et_note = findViewById(R.id.et_note);
        tv_member = findViewById(R.id.member);
        // tv_location = findViewById(R.id.tv_Location);

        //设置edittext是否可编辑
        setEditTextEnable(tv_member,false);
        setEditTextEnable(tv_title,false);
        // setEditTextEnable(tv_location,false);



        //// --- 接收 数据 ---
        final Intent intent = getIntent();

        userID = intent.getStringExtra("userID");
        token = intent.getStringExtra("token");
        taskID = intent.getStringExtra("taskID");


        // --- 展示 TITLE -----
        tv_title = findViewById(R.id.et_Title);
        et_note = findViewById(R.id.et_note);
        tv_member = findViewById(R.id.member);
        tv_location = findViewById(R.id.tv_Location);
        tv_startDate = findViewById(R.id.tv_startdate);
        tv_endDate = findViewById(R.id.tv_enddate);
        mImageView = findViewById(R.id.image_view);

        //-------

        RequestBody requestBody = new FormBody.Builder()
                .add("userID", userID)
                .add("token", token)
                .add("taskID", taskID)
                .build();

        Request request = new Request.Builder()
                .url(getString(R.string.get_task))
                .post(requestBody)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(ShowTaskActivity.this, "Network issue", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("show task activity", "onResponse: " + responseData);
                try {
                    JSONObject jsonData = new JSONObject(responseData);
                    JSONObject task = new JSONObject(jsonData.getString("task"));
                    if (jsonData.getBoolean("result")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    tv_title.setText(task.getString("title"));
                                    tv_member.setText(task.getString("members"));
                                    et_note.setText(task.getString("description"));
                                    if(task.has("startDate")){
                                        tv_startDate.setText(task.getString("startDate"));
                                    }
                                    if(task.has("endDate")){
                                        tv_endDate.setText(task.getString("endDate"));
                                    }
                                    if(task.has("location")){
                                        tv_location.setText(task.getString("location"));
                                    }
                                    if(task.has("photo")){
                                        String image = task.getString("photo");
                                        Bitmap photo = stringToImage(image);
                                        mImageView.setImageBitmap(photo);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        /////----------- SET INITIAL TEXT -----------



//
//
//        // calender
//        switch_calender = findViewById(R.id.switchBtn);
//        if (initial_C) {switch_calender.setChecked(true);}
//

        // ---- 加入日志 ----
       /*btn_add = findViewById(R.id.btn_addCalender);
        OnClick onClick = new OnClick();
        btn_add.setOnClickListener(onClick);*/

        // -- 删除 按钮 ----
        btn_delete = findViewById(R.id.det_button);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (initial_C == true){
//                    delCalender();
//                }

                Intent intent_put = new Intent();

                intent_put.putExtra("page_code", page_code);
                intent_put.putExtra("position", position);
                intent_put.putExtra("delete", 1);

                // for task
                intent_put.putExtra("title", title);
                intent_put.putExtra("description", description);
                intent_put.putExtra("username", username);
                intent_put.putExtra("startDate", startDate);
                intent_put.putExtra("dueDate", dueDate);
                intent_put.putExtra("location", location);
                intent_put.putExtra("path", filepath);
                intent_put.putExtra("status", status);

                setResult(RESULT_OK, intent_put);

                finish();

            }
        });

        // --------确认修改--------
        btn_comfirm = findViewById(R.id.btn_ok);
        btn_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                send_calender = switch_calender.isChecked();

                if (initial_C == false && send_calender == true )
                {
                    addCalender();
                }




                description = et_note.getText().toString();
                Intent intent_put = new Intent();

                intent_put.putExtra("page_code", page_code);
                intent_put.putExtra("position", position);
                intent_put.putExtra("detele", 0);

                // for task
                intent_put.putExtra("title", title);
                intent_put.putExtra("description", description);
                intent_put.putExtra("username", username);
                intent_put.putExtra("startDate", startDate);
                intent_put.putExtra("dueDate", dueDate);
                intent_put.putExtra("location", location);
                intent_put.putExtra("path", filepath);
                intent_put.putExtra("status", status);

                setResult(RESULT_OK, intent_put);

                finish();
            }
        });


        // --------取消修改--------
        OnClick onClick = new OnClick();
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(onClick);




        /// ------ 设置 LOCATION BUTTION

//        OnClick onClick1 = new OnClick();
//        tv_location.setOnClickListener(onClick1);

        tv_location = findViewById(R.id.tv_Location);
        tv_location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    Intent intent = new Intent(ShowTaskActivity.this, LocationActivity.class);
                    startActivityForResult(intent,30);
                }

            }
        });



        // --------日期--------



        // 修改日期按钮功能
        tv_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ShowTaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                year_x = i;
                                month_x = i1;
                                day_x = i2;
                                Calendar c = Calendar.getInstance();
                                hour = c.get(Calendar.HOUR);
                                minute = c.get(Calendar.MINUTE);
                                TimePickerDialog timePickerDialog = new TimePickerDialog(ShowTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                        hour_x = i;
                                        minute_x = i1;
                                        tv_startDate.setText(year_x + "/" + (month_x + 1) + "/" + day_x + " " + hour_x + ":" + minute_x);
                                        startDate = year_x + "/" + (month_x + 1) + "/" + day_x + " " + hour_x + ":" + minute_x;
                                    }
                                }, hour, minute, true);
                                timePickerDialog.show();
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        tv_endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ShowTaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                year_y = i;
                                month_y = i1;
                                day_y = i2;
                                Calendar c = Calendar.getInstance();
                                hour = c.get(Calendar.HOUR);
                                minute = c.get(Calendar.MINUTE);
                                TimePickerDialog timePickerDialog = new TimePickerDialog(ShowTaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                        hour_y = i;
                                        minute_y = i1;
                                        tv_endDate.setText(year_y + "/" + (month_y + 1) + "/" + day_y + " " + hour_y + ":" + minute_y);
                                        dueDate = year_y + "/" + (month_y + 1) + "/" + day_y + " " + hour_y + ":" + minute_y;
                                    }
                                }, hour, minute, true);
                                timePickerDialog.show();
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });



        // ---  点击图片弹窗 是否删除 ---
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowTaskActivity.this);
                builder.setTitle("Photo");
                builder.setMessage("Do you want to remove this phote?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mImageView.setImageBitmap(null);
                        mImageView.setImageURI(null);
                        filepath = null;
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


                AlertDialog alert =builder.create();
                alert.show();
                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(Color.parseColor("#000302"));
                nbutton.setBackgroundColor(Color.parseColor("#FFFFFF"));

                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.parseColor("#000302"));
                pbutton.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        });


        /// ---- 照相机 功能
        mCaptureBtn = findViewById(R.id.photo);
        mCaptureBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //if system os is>= marshmallow, request runtime permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.CAMERA)==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                                    PackageManager.PERMISSION_DENIED){
                        //permission not enabled, request it
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show popup to request permission
                        requestPermissions(permission,PERMISSION_CODE);
                    }
                    else {
                        //permission already granted
                        openCamera();
                    }
                }
                else {
                    //system os< marshallow
                    openCamera();
                }

            }
        });


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.READ_CALENDAR}, 1);
        }

    }

    private void setEditTextEnable(EditText editText, boolean mode){
        editText.setFocusable(mode);
        editText.setFocusableInTouchMode(mode);
        editText.setLongClickable(mode);
        editText.setInputType(mode ? InputType.TYPE_CLASS_TEXT:InputType.TYPE_NULL);
    }


    private void openCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);
    }

    //handling permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method is called, when user presses Allow or Deny fromm permission request Popup
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length>0 && grantResults[0]==
                        PackageManager.PERMISSION_GRANTED){
                    openCamera();
                }
                else {
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /// 接听地图数数据；
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //called when image was captured from camera
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            //set the image captured to our Im
            if(image_uri != null){
                mImageView.setImageURI(image_uri);
                //把image的string获得
                ImageView iv1 = (ImageView) findViewById(R.id.image_view);
                BitmapDrawable drawable = (BitmapDrawable) iv1.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                byte[] bb = bos.toByteArray();
                iamge = Base64.encodeToString(bb,0);
            }else {
                tv_location.setText(data.getStringExtra("data_return"));
            }
        }
        // ------ 接收 location 数据 --------
        else if (requestCode == 30) {

            Log.d("check:", "get location");
            location = data.getStringExtra("location");
            tv_location.setText(location);
        }
    }



    /////-------- onclick
    class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = null;


            //设置跳转
            switch (view.getId()) {
                /*case R.id.btn_addCalender:
                    CalendarEvent calendarEvent = new CalendarEvent(
                            "马上吃饭",
                            "吃好吃的",
                            "南信院二食堂",
                            System.currentTimeMillis(),
                            System.currentTimeMillis() + 60000,
                            0, null
                    );

                    // 添加事件
                    int result = CalendarProviderManager.addCalendarEvent(ShowTaskActivity.this, calendarEvent);
                    if (result == 0) {
                        Toast.makeText(ShowTaskActivity.this, "successfully insert", Toast.LENGTH_SHORT).show();
                    } else if (result == -1) {
                        Toast.makeText(ShowTaskActivity.this, "unsuccessfully insert", Toast.LENGTH_SHORT).show();
                    } else if (result == -2) {
                        Toast.makeText(ShowTaskActivity.this, "Do not have the permission", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.btn_deleteCalendar:
                    long calID2 = CalendarProviderManager.obtainCalendarAccountID(ShowTaskActivity.this);
                    List<CalendarEvent> events2 = CalendarProviderManager.queryAccountEvent(ShowTaskActivity.this, calID2);
                    if (null != events2) {
                        if (events2.size() == 0) {
                            Toast.makeText(ShowTaskActivity.this, "没有事件可以删除", Toast.LENGTH_SHORT).show();
                        } else {
                            long eventID = events2.get(0).getId();
                            int result2 = CalendarProviderManager.deleteCalendarEvent(ShowTaskActivity.this, eventID);
                            if (result2 == -2) {
                                Toast.makeText(ShowTaskActivity.this, "没有权限", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ShowTaskActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(ShowTaskActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                    }
                    break;*/


                case R.id.btn_cancel:
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                    break;

            }
        }
    }


    public void addCalender(){
        CalendarEvent calendarEvent = new CalendarEvent(
                "马上吃饭",
                "吃好吃的",
                "南信院二食堂",
                System.currentTimeMillis()+10000,
                System.currentTimeMillis() + 60000,
                0, null
        );

        Log.d("time", Long.toString(System.currentTimeMillis()));

        // 添加事件
        int result = CalendarProviderManager.addCalendarEvent(ShowTaskActivity.this, calendarEvent);

        if (result == 0) {
            Toast.makeText(ShowTaskActivity.this, "successfully insert", Toast.LENGTH_SHORT).show();
        } else if (result == -1) {
            Toast.makeText(ShowTaskActivity.this, "unsuccessfully insert", Toast.LENGTH_SHORT).show();
        } else if (result == -2) {
            Toast.makeText(ShowTaskActivity.this, "Do not have the permission", Toast.LENGTH_SHORT).show();
        }
    }


    private Bitmap stringToImage(String image){

        try{
            byte [] encodeByte=Base64.decode(image,Base64.DEFAULT);
            InputStream inputStream  = new ByteArrayInputStream(encodeByte);
            Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }

    }

//    public void delCalender(){
//        long calID2 = CalendarProviderManager.obtainCalendarAccountID(ShowTaskActivity.this);
//        List<CalendarEvent> events2 = CalendarProviderManager.queryAccountEvent(ShowTaskActivity.this, calID2);
//        if (null != events2) {
//            if (events2.size() == 0) {
//                Toast.makeText(ShowTaskActivity.this, "没有事件可以删除", Toast.LENGTH_SHORT).show();
//            } else {
//                long eventID = events2.get(0).getId();
//                int result2 = CalendarProviderManager.deleteCalendarEvent(ShowTaskActivity.this, eventID);
//                if (result2 == -2) {
//                    Toast.makeText(ShowTaskActivity.this, "没有权限", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(ShowTaskActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
//                }
//            }
//        } else {
//            Toast.makeText(ShowTaskActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
//        }
//    }

}



