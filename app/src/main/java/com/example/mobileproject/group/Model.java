package com.example.mobileproject.group;

public class Model {

    private int groupID;
    private String deadline;
    private int userCount;
    private String groupName;
    private String subjectName;
    private String description;

    public Model(int groupID, String deadline, int userCount, String groupName, String subjectName, String description) {
        this.groupID = groupID;
        this.deadline = deadline;
        this.userCount = userCount;
        this.groupName = groupName;
        this.subjectName = subjectName;
        this.description = description;
    }


    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getDescription(){return description;}

    public void setDescription(String description){
        this.description = description;
    }


}
