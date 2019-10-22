package com.example.mobileproject.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mobileproject.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;

public class drawLinesActivity extends AppCompatActivity {
    private DrawableView drawableView;
    private DrawableViewConfig config = new DrawableViewConfig();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_lines);
        initUi();
    }

    private void initUi() {
        drawableView = (DrawableView) findViewById(R.id.paintView);
        Button strokeWidthMinusButton = (Button) findViewById(R.id.strokeWidthMinusButton);
        Button strokeWidthPlusButton = (Button) findViewById(R.id.strokeWidthPlusButton);
        Button changeColorButton = (Button) findViewById(R.id.changeColorButton);
        Button undoButton = (Button) findViewById(R.id.undoButton);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        AppCompatActivity activity = this;

        config.setStrokeColor(getResources().getColor(android.R.color.black));
        config.setShowCanvasBounds(true);
        config.setStrokeWidth(20.0f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        config.setCanvasHeight(1080);
        config.setCanvasWidth(1920);
        drawableView.setConfig(config);

        strokeWidthPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                config.setStrokeWidth(config.getStrokeWidth() + 10);
            }
        });
        strokeWidthMinusButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                config.setStrokeWidth(config.getStrokeWidth() - 10);
            }
        });
        changeColorButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                Random random = new Random();
                config.setStrokeColor(
                        Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            }
        });
        undoButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                drawableView.undo();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath(), "zhangphil.jpg");

                Bitmap bitmap = screenShot(activity);
//                String path = temFileImage(drawLinesActivity.this, bitmap, "image");
//                File file = new File(path);
//                Log.d("saveScreenShot", "onClick: "+file.getAbsolutePath());
                try {
//                    if (!file.exists())
//                        file.createNewFile();

//                    boolean ret = save(bitmap, file, Bitmap.CompressFormat.JPEG, true);
////                    Log.d("saveScreenShot", "onClick: "+ret);
////                    if (ret) {
////                        Log.d("saveScreenShot", "onClick: "+file.getAbsolutePath());
////                        Log.d("saveScreenShot", "onClick: "+file.getName());
////                        Toast.makeText(getApplicationContext(), "截图已保持至 " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
////
////                    }
                    Intent intent = new Intent();
                    Log.d("saveScreenShot", "onClick: 1111");
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    byte[] bitmapByte = os.toByteArray();

                    intent.putExtra("bitmap", bitmapByte);
                    Log.d("saveScreenShot", "onClick: 2222");
                    setResult(RESULT_OK, intent);
                    Log.d("saveScreenShot", "onClick: 3333");
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    public static String temFileImage(Context context, Bitmap bitmap, String name){
//        File outputDir = context.getCacheDir();
//        File imageFile = new File(outputDir, name + ".jpg");
//
//        OutputStream os;
//        try {
//            os = new FileOutputStream(imageFile);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
//            os.flush();
//            os.close();
//        } catch (Exception e) {
//            Log.e(context.getClass().getSimpleName(), "Error writing file", e);
//        }
//
//        return imageFile.getAbsolutePath();
//    }

//    /**
//     * 保存图片到文件File。
//     *
//     * @param src     源图片
//     * @param file    要保存到的文件
//     * @param format  格式
//     * @param recycle 是否回收
//     * @return true 成功 false 失败
//     */
//    public static boolean save(Bitmap src, File file, Bitmap.CompressFormat format, boolean recycle) {
//        if (isEmptyBitmap(src))
//            return false;
//
//        OutputStream os;
//        boolean ret = false;
//        try {
//            os = new BufferedOutputStream(new FileOutputStream(file));
//            ret = src.compress(format, 100, os);
//            if (recycle && !src.isRecycled())
//                src.recycle();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return ret;
//    }


    /**
     * 获取当前屏幕截图，不包含状态栏（Status Bar）。
     *
     * @param activity activity
     * @return Bitmap
     */
    public static Bitmap screenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
//        int statusBarHeight = getStatusBarHeight(activity);
        int barHeight = view.findViewById(R.id.hwlayout).getHeight()*2;
        int width = (int) getDeviceDisplaySize(activity)[0];
        int height = (int) getDeviceDisplaySize(activity)[1];

        Bitmap ret = Bitmap.createBitmap(bmp, 0, barHeight, width, height - barHeight);
        view.destroyDrawingCache();

        return ret;
    }

    public static float[] getDeviceDisplaySize(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        float[] size = new float[2];
        size[0] = width;
        size[1] = height;

        return size;
    }

//    public static int getStatusBarHeight(Context context) {
//        int height = 0;
//        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            height = context.getResources().getDimensionPixelSize(resourceId);
//        }
//
//        return height;
//    }

    /**
     * Bitmap对象是否为空。
     */
    public static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

}
