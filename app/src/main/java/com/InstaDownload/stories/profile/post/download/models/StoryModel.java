package com.InstaDownload.stories.profile.post.download.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tirgei on 10/29/17.
 */

public class StoryModel implements Parcelable {
    private String filePath;
    private String fileName;
    private int type;
    private Boolean isSaved;
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StoryModel(){}

    public StoryModel(String filePath, String fileName, int type, Boolean isSaved){
        this.filePath = filePath;
        this.fileName = fileName;
        this.type = type;
        this.isSaved = isSaved;
    }

    protected StoryModel(Parcel in) {
        filePath = in.readString();
        fileName = in.readString();
        type = in.readInt();
        isSaved = in.readByte() != 0;
    }

    public static final Creator<StoryModel> CREATOR = new Creator<StoryModel>() {
        @Override
        public StoryModel createFromParcel(Parcel in) {
            return new StoryModel(in);
        }

        @Override
        public StoryModel[] newArray(int size) {
            return new StoryModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(filePath);
        parcel.writeString(fileName);
        parcel.writeInt(type);
        parcel.writeByte((byte)(isSaved ? 1 : 0));
    }


    public Boolean getSaved() {
        return isSaved;
    }

    public void setSaved(Boolean saved) {
        isSaved = saved;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
