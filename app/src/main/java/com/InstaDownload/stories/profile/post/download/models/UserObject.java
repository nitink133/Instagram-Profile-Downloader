package com.InstaDownload.stories.profile.post.download.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tirgei on 10/31/17.
 */

public class UserObject implements Parcelable {
    private String userName;
    private String userId;
    private String realName;
    private String image;
    private Bitmap bitmap;
    private Boolean isFaved=false;

    public UserObject(){}

    protected UserObject(Parcel in) {
        userName = in.readString();
        userId = in.readString();
        realName = in.readString();
        image = in.readString();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        byte tmpIsFaved = in.readByte();
        isFaved = tmpIsFaved == 0 ? null : tmpIsFaved == 1;
    }

    public static final Creator<UserObject> CREATOR = new Creator<UserObject>() {
        @Override
        public UserObject createFromParcel(Parcel in) {
            return new UserObject(in);
        }

        @Override
        public UserObject[] newArray(int size) {
            return new UserObject[size];
        }
    };

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(userId);
        dest.writeString(realName);
        dest.writeString(image);
        dest.writeParcelable(bitmap, flags);
        dest.writeByte((byte) (isFaved == null ? 0 : isFaved ? 1 : 2));
    }
}
