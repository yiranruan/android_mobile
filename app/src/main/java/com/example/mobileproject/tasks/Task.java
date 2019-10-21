package com.example.mobileproject.tasks;


import android.util.StatsLog;

import android.graphics.Bitmap;
import android.media.Image;

import java.util.Date;

public class Task extends Object{



    private String username;
    private String title;
    private String description;
    private String createDate;
    private String dueDate;
    private String usersID;
    private int groupID;
    private String path;
    private String location;
    private String status = "To-Do";


    public Task(String username, String title, String description, String usersID, int groupID,
                String createDate, String dueDate, String path, String location, String status) {
        this.username = username;
        this.title = title;
        this.description = description;
        this.usersID = usersID;
        this.groupID = groupID;
        this.path = path;
        this.location = location;
        this.createDate = createDate;
        this.dueDate = dueDate;

    };

    public String getCreateDate() {
        return createDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void changeStatus(int statusCode){
        if (statusCode == 2){
            this.status = "Done";
        }else if (statusCode == 1){
            this.status = "Doing";
        }else {
            this.status = "To-Do";
        }
    }
    public String getStatus(){
        return this.status;
    }

    public String getPath() {
        return this.path;
    }
    ;

    public String getUsername() {
        return this.username;
    }
    ;
    public String getUsersID() {
        return this.usersID;
    }
    ;
    public int getGroupID() {
        return this.groupID;
    }
    ;

    public String getTitle() {
        return this.title;
    }

    ;

    public String getDescription() {
        return this.description;
    }

    ;

    public String getlocation() {
        return this.location;
    }

    ;

}