package com.InstaDownload.stories.profile.post.download.models;

/**
 * Created by tirgei on 10/29/17.
 */

public class HistoryModel {
    private String userName;
    private byte[] userPic;
    private String userId;
    private String realName;
    private Boolean isFaved;

    public HistoryModel(){

    }

    public HistoryModel(String userName, byte[] userPic, String userId, String realName){
        this.userName = userName;
        this.realName = realName;
        this.userPic = userPic;
        this.userId = userId;
    }

    public Boolean getFaved() {
        return isFaved;
    }

    public void setFaved(Boolean faved) {
        isFaved = faved;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getUserPic() {
        return userPic;
    }

    public void setUserPic(byte[] userPic) {
        this.userPic = userPic;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
