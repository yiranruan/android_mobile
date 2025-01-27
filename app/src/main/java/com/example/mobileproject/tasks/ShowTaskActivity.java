package com.example.mobileproject.tasks;
import com.example.mobileproject.R;
import com.example.mobileproject.group.ShowGroupActivity;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kaopiz.kprogresshud.KProgressHUD;
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
import android.graphics.Canvas;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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


    //为了时间数据转化为Long类型
    private String startDateInitial;
    private String endDateInitial;
    private long  sDate;
    private long eDate;



    private Button btn_cancel;
    private Button btn_confirm;

    private EditText et_note;



    private int day,month,year,hour,minute;
    private int day_x,month_x,year_x,hour_x,minute_x;
    private int day_y,month_y,year_y,hour_y,minute_y;
    private ImageView mImageView;
    private Uri image_uri;

    private FloatingActionsMenu menuMultipleActions;
    private FloatingActionButton mCaptureBtn;
    private FloatingActionButton mHandWriteBtn;
    private Button btn_delete;
    private ImageView imageview;

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
    private String image;
    private String userID;
    private String taskID;
    private String title;
    private String description;
    private String username;
    private String status;
    private KProgressHUD hud;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_task);
        switch_calender = findViewById(R.id.calendar_status);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...");
        hud.show();


        tv_title = findViewById(R.id.et_Title);
        et_note = findViewById(R.id.et_note);
        tv_member = findViewById(R.id.member);
        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions_photo_task);
        // tv_location = findViewById(R.id.tv_Location);
        Log.d("show task test", "onCreate: ");

        //设置edittext是否可编辑
//        setEditTextEnable(tv_member,false);
        setEditTextEnable(tv_title,false);
        // setEditTextEnable(tv_location,false);



        //// --- 接收 数据 ---
        final Intent intent = getIntent();

        userID = intent.getStringExtra("userID");
        token = intent.getStringExtra("token");
        taskID = intent.getStringExtra("taskID");
        groupID = intent.getIntExtra("groupID",0);

        page_code = intent.getIntExtra("page_position", 999);
        position = intent.getIntExtra("position", 999);


        // --- 展示 TITLE -----
        tv_title = findViewById(R.id.et_Title);
        et_note = findViewById(R.id.et_note);
        tv_member = findViewById(R.id.member);
        tv_location = findViewById(R.id.tv_Location);
        tv_startDate = findViewById(R.id.tv_startdate);
        tv_endDate = findViewById(R.id.tv_enddate);
        mImageView = findViewById(R.id.image_view_task);

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
                hud.dismiss();
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
                                    title = task.getString("title");
                                    tv_title.setText(title);
                                    tv_member.setText(task.getString("members"));
                                    et_note.setText(task.getString("description"));
                                    if(task.has("startDate")){
                                        startDate = task.getString("startDate");
                                        tv_startDate.setText(startDate);
                                    }
                                    if(task.has("endDate")){
                                        dueDate = task.getString("endDate");
                                        tv_endDate.setText(dueDate);
                                    }
                                    if(task.has("location")){
                                        location = task.getString("location");
                                        tv_location.setText(location);
                                    }
                                    if(task.has("photo")){
                                        image = task.getString("photo");
                                        Bitmap photo = stringToImage(image);
                                        mImageView.setImageBitmap(photo);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    hud.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        /////----------- SET INITIAL TEXT -----------


        btn_confirm = findViewById(R.id.confirm_btn);
        btn_confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                hud.show();
                description = et_note.getText().toString();
                username = tv_member.getText().toString();

                Log.d("image", image);

                RequestBody requestBody = new FormBody.Builder()
                        .add("taskID", taskID)
                        .add("title", title)
                        .add("userID", userID)
                        .add("groupID", String.valueOf(groupID))
                        .add("token", token)
                        .add("location", location)
                        .add("members", username)
                        .add("startDate", startDate)
                        .add("endDate", dueDate)
                        .add("description", description)
                        .add("photo", image)
                        .build();

                Request request = new Request.Builder()
                        .url(getString(R.string.update_task))
                        .post(requestBody)
                        .build();
                Log.d("update", "onClick: " + request.body().toString());
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Toast.makeText(ShowTaskActivity.this, "Network issue, try it later", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();

                        Log.d("update task", "onResponse: " + responseData);
                        try {
                            JSONObject jsonData = new JSONObject(responseData);
                            boolean result = jsonData.getBoolean("result");
                            Intent intent = new Intent();
                            intent.putExtra("result", result);
                            intent.putExtra("delete", 0);
                            setResult(RESULT_OK, intent);
                            hud.dismiss();
                            if (switch_calender.isChecked() && result){
                                addCalender();
                            }
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });

            }
        });




        // -- 删除 按钮 ----
        btn_delete = findViewById(R.id.det_button);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hud.show();
                RequestBody requestBody = new FormBody.Builder()
                        .add("userID", userID)
                        .add("token", token)
                        .add("taskID", taskID)
                        .build();

                Request request = new Request.Builder()
                        .url(getString(R.string.delete_task))
                        .post(requestBody)
                        .build();
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Toast.makeText(ShowTaskActivity.this, "Network issue", Toast.LENGTH_SHORT).show();
                        hud.dismiss();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();
                        Log.d("show task activity", "onResponse: " + responseData);
                        try {
                            JSONObject jsonData = new JSONObject(responseData);
                            Boolean result = jsonData.getBoolean("result");
                            Intent intent_put = new Intent();
                            intent_put.putExtra("result",result);
                            intent_put.putExtra("page_code", page_code);
                            intent_put.putExtra("position", position);
                            intent_put.putExtra("delete", 1);
                            setResult(RESULT_OK, intent_put);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });



        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });




        /// ------ 设置 LOCATION BUTTION


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

                checkExpansion();
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
                                        startDateInitial = year_x+"-"+(month_x+1)+"-"+day_x+" "+hour_x+":"+minute_x;
                                        SimpleDateFormat dataformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                                        try {
                                            sDate = dataformat.parse(startDateInitial).getTime();
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
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
                checkExpansion();
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
                                        dueDate = day_y+"/"+(month_y+1)+"/"+year_y+" "+hour_y+":"+minute_y;
                                        endDateInitial = year_x+"-"+(month_x+1)+"-"+day_x+" "+hour_x+":"+minute_x;
                                        SimpleDateFormat dataformat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                        try {
                                            eDate = dataformat.parse(endDateInitial).getTime();
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
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
                checkExpansion();
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowTaskActivity.this);
                builder.setTitle("Photo");
                builder.setMessage("Do you want to remove this phote?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mImageView.setImageBitmap(null);
                        mImageView.setImageURI(null);
                        image = "";
                        bitmap = null;
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkExpansion();
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


        // 悬浮窗口
        mHandWriteBtn = findViewById(R.id.handwriting_task);
        mHandWriteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(ShowTaskActivity.this,drawLinesActivity.class);
                startActivityForResult(intent, 40);
                checkExpansion();
            }
        });
        /// ------ 打开摄像机的代码 --------
        mCaptureBtn = findViewById(R.id.photo_task);
        mCaptureBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //if system os is>= marshmallow, request runtime permission
                checkExpansion();
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

        /// ---- 手写 功能



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


    ///  接收数据的代码---
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //called when image was captured from camera
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_CAPTURE_CODE:
                if(resultCode == RESULT_OK){
                    //set the image captured to our Im
                    mImageView.setImageBitmap(null);
                    mImageView.setImageURI(null);
                    bitmap = null;


                    mImageView.setImageURI(image_uri);

                    //ImageView iv1 = (ImageView) findViewById(R.id.image_view);
                    bitmap = Bitmap.createBitmap(mImageView.getWidth(),
                            mImageView.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    mImageView.draw(canvas);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                    int options = 90;
                    while (baos.toByteArray().length / 1024 > 60) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                        baos.reset(); // 重置baos即清空baos
                        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                        options -= 10;// 每次都减少10
                        Log.d("size", String.valueOf(baos.toByteArray().length));
                    }

                    image = Base64.encodeToString(baos.toByteArray(), 0);
                }
                break;
            case 30:
                // ------ 接收 location 数据 --------
                if(resultCode == RESULT_OK){
                    Log.d("check:", "get location");
                    location = data.getStringExtra("location");
                    tv_location.setText(location);
                }
                break;
            case 40:
                if(resultCode == RESULT_OK) {
                    mImageView.setImageBitmap(null);
                    mImageView.setImageURI(null);
                    bitmap = null;

//                mImageView = (ImageView) findViewById(R.id.image_view);
                    byte[] bis = data.getByteArrayExtra("bitmap");
                    bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
                    mImageView.setImageBitmap(bitmap);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    image = Base64.encodeToString(baos.toByteArray(), 0);

                }
                break;
        }

    }




    public void addCalender(){
        try {
            CalendarEvent calendarEvent = new CalendarEvent(
                    title,
                    description,
                    location,
                    sDate,
                    eDate,
                    0, null
            );

            Log.d("msg:", "" + sDate);

            // 添加事件
            int result = CalendarProviderManager.addCalendarEvent(ShowTaskActivity.this, calendarEvent);

            Log.d("cal:", "" + result);
            if (result == 0) {
                //Toast.makeText(ShowTaskActivity.this, "successfully insert", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(ShowTaskActivity.this, "successfully insert", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            //Toast.makeText(ShowTaskActivity.this, "invalid insert", Toast.LENGTH_SHORT).show();
        }

    }

    private Bitmap stringToImage(String image){

        try{
            byte [] encodeByte=Base64.decode(image,Base64.DEFAULT);
            InputStream inputStream  = new ByteArrayInputStream(encodeByte);
            bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }

    }



    public void checkExpansion(){
        if (menuMultipleActions.isExpanded()) {
            menuMultipleActions.collapseImmediately();
        }
    }

}



