package com.example.mobileproject.tasks;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileproject.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.kyle.calendarprovider.calendar.CalendarEvent;
import com.kyle.calendarprovider.calendar.CalendarProviderManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CreateButton extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    // 为数据服务

    private String location = "no location";
    private String startDate = "no start date";
    private String dueDate = "no due date";
    private String filePath;
    private String image = "";
    Bitmap bitmap = null;
    private String title = "no title ";
    private String description = "no note";
    private String username = "ZI JI";


    // 以下为 XML 服务
    private Button btn_add;
    private Button btn_cancel;
    private FloatingActionsMenu menuMultipleActions;
    private FloatingActionButton mHandWriteBtn;
    private FloatingActionButton mCaptureBtn;

    private SwitchMaterial switch_calender;
    private Boolean send_calender = false;


//    private TextInputLayout location_layout;
//    private TextInputLayout start_date;

    private TextInputEditText tv_location;
    private TextInputEditText tv_startDate;
    private TextInputEditText tv_endDate;

    private TextInputEditText input_title;
    private TextInputEditText input_note;
    private TextInputEditText input_member;


    private int day,month,year,hour,minute;
    private int day_x,month_x,year_x,hour_x,minute_x;
    private int day_y,month_y,year_y,hour_y,minute_y;

    //为了时间数据转化为Long类型
    private String startDateInitial;
    private String endDateInitial;
    private long  sDate;
    private long eDate;


    private ImageView mImageView;
    private Uri image_uri;
    private KProgressHUD hud;

    private String groupID;
    private String userID;
    private String token;



    private OkHttpClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_button);
        /// 添加任务 相关的任务

        // 接收小组ID 和 USERID 的数据
        Intent intent_reci = getIntent();
        userID = intent_reci.getStringExtra("userID");
        token = intent_reci.getStringExtra("token");
        groupID = intent_reci.getStringExtra("groupID");

        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading...");

        // switch bar;
        switch_calender = findViewById(R.id.switchBtn);

        /// 这三个是 EDITTEXT 绑定ID即可
        input_title = findViewById(R.id.input_title);
        input_note = findViewById(R.id.notes_input);
        input_member = findViewById(R.id.members_input);
        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions_photo);

        Log.d("msg","check 1");

//        location_layout = findViewById(R.id.location);
//        start_date = findViewById(R.id.start_date);


        // ---  点击图片弹窗 是否删除 ---
        mImageView = findViewById(R.id.image_view);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateButton.this);
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

        Log.d("msg","check 2");


        /// ------ 设置 LOCATION BUTTION

//        OnClick onClick1 = new OnClick();
//        tv_location.setOnClickListener(onClick1);

        tv_location = findViewById(R.id.input_location);
        tv_location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    Intent intent = new Intent(CreateButton.this, LocationActivity.class);
                    startActivityForResult(intent,30);
                }

            }
        });


        Log.d("msg","check 3");

        // ADD BUTTON 的代码 -- 确认信息
        btn_add = findViewById(R.id.btn_ok);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                title = input_title.getText().toString();
                description = input_note.getText().toString();

                username = input_member.getText().toString();
                Log.d("create task data", "onClick: " + description );
                Log.d("create task data", "onClick: " + image );
                Log.d("create task data", "onClick: " + startDate );

                if (title.length() > 0) {

                    hud.show();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("userID", userID)
                            .add("token", token)
                            .add("groupID", groupID)
                            .add("title", title)
                            .add("location", location)
                            .add("members", username)
                            .add("startDate", startDate)
                            .add("endDate", dueDate)
                            .add("description", description)
                            .add("photo", image)
                            .build();

                    Request request = new Request.Builder()
                            .url(getString(R.string.new_task))
                            .post(requestBody)
                            .build();

                    OkHttpClient client = new OkHttpClient();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            hud.dismiss();
                            Toast.makeText(CreateButton.this, "Network issue, try it later", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            String responseData = response.body().string();
                            Boolean result = false;
                            try {
                                JSONObject jsonData = new JSONObject(responseData);
                                result = jsonData.getBoolean("result");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("create task", "onResponse: " + responseData);
                            Intent intent = new Intent();
                            intent.putExtra("responseData", responseData);
                            setResult(RESULT_OK, intent);

                            hud.dismiss();

                            if (switch_calender.isChecked() && result){
                                addCalender();
                            }
                            finish();
                        }
                    });
                }else {
                    Toast.makeText(CreateButton.this, "must have the title", Toast.LENGTH_SHORT).show();
                }

            }
        });

        /// ------ 设置 CANCEL BUTTON
        OnClick onClick = new OnClick();
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(onClick);


        tv_startDate = findViewById(R.id.start_date_input);
        tv_endDate = findViewById(R.id.due_date_input);

        tv_startDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                DatePickerDialog datePickerDialog;
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(CreateButton.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                year_x = i;
                                month_x =i1;
                                day_x = i2;
                                Calendar c = Calendar.getInstance();
                                hour = c.get(Calendar.HOUR);
                                minute = c.get(Calendar.MINUTE);
                                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateButton.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                        hour_x =i;
                                        minute_x =i1;
                                        tv_startDate.setText(day_x+"/"+(month_x+1)+"/"+year_x+" "+hour_x+":"+minute_x);
                                        startDate = day_x+"/"+(month_x+1)+"/"+year_x+" "+hour_x+":"+minute_x;
                                        //startDate = day_x+"/"+(month_x+1)+"/"+year_x+" "+hour_x+":"+minute_x;
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
                        },year,month,day);
                if(hasFocus){
                    datePickerDialog.show();
                }
                else{
                    datePickerDialog.cancel();
                }
            }

        });


        tv_endDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateButton.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                year_y = i;
                                month_y =i1;
                                day_y = i2;
                                Calendar c = Calendar.getInstance();
                                hour = c.get(Calendar.HOUR);
                                minute = c.get(Calendar.MINUTE);
                                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateButton.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                        hour_y =i;
                                        minute_y =i1;
                                        tv_endDate.setText(day_y+"/"+(month_y+1)+"/"+year_y+" "+hour_y+":"+minute_y);
                                       // dueDate = day_y+"/"+(month_y+1)+"/"+year_y+" "+hour_y+":"+minute_y;
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
                        },year,month,day);

                if(hasFocus){
                    datePickerDialog.show();
                }
                else{
                    datePickerDialog.cancel();
                }

            }
        });


        ///———————— 之前都是关于添加时间的代码






        // 悬浮窗口


        mHandWriteBtn = findViewById(R.id.handwriting);
        mHandWriteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(CreateButton.this,drawLinesActivity.class);
                startActivityForResult(intent, 40);
                checkExpansion();
            }
        });
        /// ------ 打开摄像机的代码 --------
        mCaptureBtn = findViewById(R.id.photo);
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
    // ------- 以上都为 打开摄像机的代码；


    //-------handling permission request
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
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_CAPTURE_CODE:
                if (resultCode == RESULT_OK) {
                    //set the image captured to our Im

                    mImageView.setImageBitmap(null);
                    mImageView.setImageURI(null);
                    bitmap = null;
                    mImageView.setImageURI(image_uri);

                    ImageView iv1 = (ImageView) findViewById(R.id.image_view);
                    bitmap = Bitmap.createBitmap(iv1.getWidth(),
                            iv1.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    iv1.draw(canvas);
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
                if (resultCode == RESULT_OK) {
                    Log.d("check:", "get location");
                    location = data.getStringExtra("location");
                    tv_location.setText(location);
                }
                break;
            case 40:
                if (resultCode == RESULT_OK) {

                    mImageView.setImageBitmap(null);
                    mImageView.setImageURI(null);

                    bitmap = null;


                    Log.d("saveScreenShot", "onActivityResult: true");
                    mImageView = (ImageView) findViewById(R.id.image_view);
                    byte [] bis = data.getByteArrayExtra("bitmap");
                    bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
                    mImageView.setImageBitmap(bitmap);

//                        bitmap = intent.getParcelableExtra("bitmap");

                    //ImageView iv2 = (ImageView) findViewById(R.id.image_view);
                    BitmapDrawable drawable1 = (BitmapDrawable) mImageView.getDrawable();
                    bitmap = drawable1.getBitmap();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                    int options = 90;
                    while (baos.toByteArray().length / 1024 > 60) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                        baos.reset(); // 重置baos即清空baos
                        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                        options -= 10;// 每次都减少10
                    }
                    Log.d("bao", String.valueOf(baos.toByteArray().length));
                    image = Base64.encodeToString(baos.toByteArray(),0);



                }
                break;
        }
    }

    //creates a temporary file and return the absolute file path
    public static String temFileImage(Context context, Bitmap bitmap, String name){
        File outputDir = context.getCacheDir();
        File imageFile = new File(outputDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 1, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Error writing file", e);
        }

        return imageFile.getAbsolutePath();
    }


    /// ---- 为 CANCEL BUTTON 和 LOCATION BUTTON 写的点击代码 -----
    class OnClick implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent intent = null;
            //设置跳转
            switch (view.getId()){
                case R.id.btn_cancel:
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                    break;

            }
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
            int result = CalendarProviderManager.addCalendarEvent(CreateButton.this, calendarEvent);

            Log.d("cal:", "" + result);
            if (result == 0) {
                //Toast.makeText(CreateButton.this, "successfully insert", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(CreateButton.this, "successfully insert", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            //Toast.makeText(CreateButton.this, "invalid insert", Toast.LENGTH_SHORT).show();
        }

    }

    public void checkExpansion(){
        if (menuMultipleActions.isExpanded()) {
            menuMultipleActions.collapseImmediately();
        }
    }
}
