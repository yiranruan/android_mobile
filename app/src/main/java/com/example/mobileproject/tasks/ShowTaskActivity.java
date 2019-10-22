package com.example.mobileproject.tasks;
import com.example.mobileproject.R;


import androidx.appcompat.app.AppCompatActivity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.List;

//import com.kyle.calendarprovider.calendar.CalendarEvent;
//import com.kyle.calendarprovider.calendar.CalendarProviderManager;

public class ShowTaskActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    private TextView tv_location;
    private TextView tv_startDate;
    private TextView tv_endDate;
    private TextView tv_member;



    private Button btn_cancel;
    private ImageView showimg;
    private Button btn_comfirm;

    private EditText et_note;
    private TextView tv_title;


    private int day,month,year,hour,minute;
    private int day_x,month_x,year_x,hour_x,minute_x;
    private int day_y,month_y,year_y,hour_y,minute_y;
    private ImageView mImageView;
    private Uri image_uri;

    private Button mCaptureBtn;
    private Button btn_add;
    private Button btn_delete;

    /// ----


    private String location;
    private String startDate;
    private String dueDate;
    private String filepath;
    private String iamge;
    private String path;
    private String userID;
    private int groupID;
    private String title;
    private String description;
    private String username;
    private String status;


    class OnClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Intent intent = null;

            //设置跳转
            switch (view.getId()) {
                case R.id.btn_addCalender:
//                    CalendarEvent calendarEvent = new CalendarEvent(
//                            "马上吃饭",
//                            "吃好吃的",
//                            "南信院二食堂",
//                            System.currentTimeMillis(),
//                            System.currentTimeMillis() + 60000,
//                            0, null
//                    );
//
//                    // 添加事件
//                    int result = CalendarProviderManager.addCalendarEvent(ShowTaskActivity.this, calendarEvent);
//                    if (result == 0) {
//                        Toast.makeText(ShowTaskActivity.this, "successfully insert", Toast.LENGTH_SHORT).show();
//                    } else if (result == -1) {
//                        Toast.makeText(ShowTaskActivity.this, "unsuccessfully insert", Toast.LENGTH_SHORT).show();
//                    } else if (result == -2) {
//                        Toast.makeText(ShowTaskActivity.this, "Do not have the permission", Toast.LENGTH_SHORT).show();
//                    }
                    break;

                case R.id.btn_deleteCalendar:
//                    long calID2 = CalendarProviderManager.obtainCalendarAccountID(ShowTaskActivity.this);
//                    List<CalendarEvent> events2 = CalendarProviderManager.queryAccountEvent(ShowTaskActivity.this, calID2);
//                    if (null != events2) {
//                        if (events2.size() == 0) {
//                            Toast.makeText(ShowTaskActivity.this, "没有事件可以删除", Toast.LENGTH_SHORT).show();
//                        } else {
//                            long eventID = events2.get(0).getId();
//                            int result2 = CalendarProviderManager.deleteCalendarEvent(ShowTaskActivity.this, eventID);
//                            if (result2 == -2) {
//                                Toast.makeText(ShowTaskActivity.this, "没有权限", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(ShowTaskActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    } else {
//                        Toast.makeText(ShowTaskActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
//                    }
                    break;


                case R.id.btn_cancel:
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                    break;
//                case R.id.tv_Location:
//                    intent = new Intent(CreateNewTask.this, LocationActivity.class);
//                    startActivityForResult(intent,1);
//                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_task);

        tv_title = findViewById(R.id.et_Title);
        et_note = findViewById(R.id.et_note);
        tv_member = findViewById(R.id.member);
        showimg = findViewById(R.id.image_view);


        //// --- 接收 数据 ---
        final Intent intent = getIntent();

        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        username = intent.getStringExtra("username");
        startDate = intent.getStringExtra("strDate");
        dueDate = intent.getStringExtra("dueDate");
        status = intent.getStringExtra("status");
        userID= intent.getStringExtra("userID");
        groupID = Integer.parseInt(intent.getStringExtra("groupID"));
        location = intent.getStringExtra("location");
        //path = intent.getParcelableExtra("path");
        filepath = intent.getStringExtra("path");

        //// ----- 接收 数据 ----




        // --- 展示 TITLE -----
        tv_title = findViewById(R.id.et_Title);
        tv_title.setText(title);

        // --- 展示 TASK 描述 -----
        et_note = findViewById(R.id.et_note);
        et_note.setText(description);

        // --- 展示 TASK 成员 -----
        tv_member = findViewById(R.id.member);
        tv_member.setText(username);

        // --- 展示 图片 （有问题）------
        if(filepath != null){
            File file = new File(filepath);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ImageView imageView = (ImageView) findViewById(R.id.image_view);
            imageView.setImageBitmap(bitmap);
        }

//        showimg = findViewById(R.id.image_view);
//        showimg.setImageBitmap(bitmap);




        // ------ 展示 LOCATION --------
        tv_location = findViewById(R.id.tv_Location);
        tv_location.setText(location);





        // 没有 用到
        mImageView = findViewById(R.id.image_view);
        mCaptureBtn = findViewById(R.id.capture_image_btn);

        // ---- 加入日志 ----
        btn_add = findViewById(R.id.btn_addCalender);
        OnClick onClick = new OnClick();
        btn_add.setOnClickListener(onClick);

        // ---- 删除日志 ---- 还没用到
        btn_delete = findViewById(R.id.btn_deleteCalendar);
        //


        // --------确认修改--------
        btn_comfirm = findViewById(R.id.btn_ok);
        btn_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                description = et_note.getText().toString();

                Intent intent_put = new Intent();
                intent_put.putExtra("title", title);
                intent_put.putExtra("description", description);
                intent_put.putExtra("username", username);
                intent_put.putExtra("startDate", startDate);
                intent_put.putExtra("dueDate", dueDate);
                intent_put.putExtra("location", location);
                //要往数据库里传输的？？？
                intent_put.putExtra("path", filepath);
                intent_put.putExtra("status", status);
                setResult(RESULT_OK, intent_put);

                finish();
            }
        });

        // --------取消修改--------
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(onClick);






        // --------日期--------


        // 展示日期
        tv_startDate = findViewById(R.id.tv_startdate);
        tv_startDate.setText(startDate);
        tv_endDate = findViewById(R.id.tv_enddate);
        tv_endDate.setText(dueDate);

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


//
//        mCaptureBtn.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                //if system os is>= marshmallow, request runtime permission
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                    if(checkSelfPermission(Manifest.permission.CAMERA)==
//                            PackageManager.PERMISSION_DENIED ||
//                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==
//                                    PackageManager.PERMISSION_DENIED){
//                        //permission not enabled, request it
//                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                        //show popup to request permission
//                        requestPermissions(permission,PERMISSION_CODE);
//                    }
//                    else {
//                        //permission already granted
//                        openCamera();
//                    }
//                }
//                else {
//                    //system os< marshallow
//                    openCamera();
//                }
//
//            }
//        });
//
//
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_CALENDAR,
//                            Manifest.permission.READ_CALENDAR}, 1);
//        }
//
//    }


//
//    private void openCamera(){
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE,"New Picture");
//        values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
//        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
//        //Camera intent
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
//        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);
//    }


//    //handling permission request
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        //this method is called, when user presses Allow or Deny fromm permission request Popup
//        switch (requestCode){
//            case PERMISSION_CODE:{
//                if(grantResults.length>0 && grantResults[0]==
//                        PackageManager.PERMISSION_GRANTED){
//                    openCamera();
//                }
//                else {
//                    //permission from popup was denied
//                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }


//    /// 接听地图数数据；
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        //called when image was captured from camera
//
//        if(resultCode == RESULT_OK){
//            //set the image captured to our Im
//            if(image_uri != null){
//                mImageView.setImageURI(image_uri);
//                //把image的string获得
//                ImageView iv1 = (ImageView) findViewById(R.id.image_view);
//                BitmapDrawable drawable = (BitmapDrawable) iv1.getDrawable();
//                Bitmap bitmap = drawable.getBitmap();
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
//                byte[] bb = bos.toByteArray();
//                iamge = Base64.encodeToString(bb,0);
//            }else {
//                tv_location.setText(data.getStringExtra("data_return"));
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//
//    }



    }


}

