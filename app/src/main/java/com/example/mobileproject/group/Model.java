package com.example.mobileproject.group;

public class Model {

    private int groupID;
    private int userCount;
    private String groupName;
    private String subjectName;
    private String inviteCode;
    private String description;

    public Model(int groupID, int userCount, String groupName, String subjectName,String inviteCode, String description) {
        this.groupID = groupID;
        this.userCount = userCount;
        this.groupName = groupName;
        this.subjectName = subjectName;
        this.inviteCode = inviteCode;
        this.description = description;
    }


    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
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

    public String getInviteCode(){return inviteCode;}

    public void setInviteCode(String inviteCode){
        this.inviteCode = inviteCode;
    }

    public String getDescription(){return description;}

    public void setDescription(String description){
        this.description = description;
    }


}
