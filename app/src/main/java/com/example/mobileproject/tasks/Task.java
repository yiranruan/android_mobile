package com.example.mobileproject.tasks;


import android.util.StatsLog;

import android.graphics.Bitmap;
import android.media.Image;

import java.util.Date;

public class Task extends Object{



    private String title;
    private int groupID;
    private String status;
    private String taskID;
    private boolean setCal = false;
    private String img;


    public Task(String taskID, int groupID, String title, String status){
        this.groupID = groupID;
        this.taskID = taskID;
        this.title = title;
        this.status = status;
    }




    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


    public void changeStatus(String status){
            this.status = status;

    }
    public String getStatus(){
        return this.status;
    }

    public int getGroupID() {
        return this.groupID;
    }


    public String getTitle() {
        return this.title;
    }




}