package com.example.todolist.tasks;

import android.media.Image;

public class Task {



    private int ID;
    private String title;
    private String description;
    private int usersID;
    private int groupID;
    private int iamge;
    private int location;
    private String status;

    public Task(int ID, String title, String description, int usersID, int groupID) {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.usersID = usersID;
        this.groupID = groupID;

    };

    public Task(int ID, String title, String description, int usersID, int groupID, int image) {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.usersID = usersID;
        this.groupID = groupID;
        this.iamge =iamge;

    };

    public Task(int ID, String title, String description, int usersID, int groupID, int image, int location) {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.usersID = usersID;
        this.groupID = groupID;
        this.iamge = iamge;
        this.location = location;
    };

    public void changeStatus(int statusCode){
        if (statusCode == 2){
            this.status = "Done";
        }else if (statusCode == 1){
            this.status = "Doing";
        }else {
            this.status = "ToDo";
        }
    }
    public String getStatus(){
        return this.status;
    }

    public int getImage() {
        return iamge;
    }
    ;

    public int getID() {
        return ID;
    }
    ;
    public int getUsersID() {
        return usersID;
    }
    ;
    public int getGroupID() {
        return groupID;
    }
    ;

    public String getTitle() {
        return title;
    }

    ;

    public String getText() {
        return description;
    }

    ;

    public int getlocation() {
        return location;
    }

    ;
    public String getImageUrl() {
        return "http";
    }

}
