package com.blackpaper.InstaDownload.stories.profile.post.download.data.room.tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;



@Entity(tableName = "downloads_table")
public class Downloads {
    public final static String TABLE_NAME = "downloads_table";

    public void setId(Integer id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    private Integer id;


    @ColumnInfo(name = "username")
    private String username;
    @NonNull
    @ColumnInfo(name = "path")
    private String path;

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }


    public String getPath() {
        return path;
    }

    public void setPath(@NonNull String path) {
        this.path = path;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @ColumnInfo(name = "user_id")
    private String user_id;

    @NonNull
    public String getFilename() {
        return filename;
    }

    public void setFilename(@NonNull String filename) {
        this.filename = filename;
    }

    @NonNull
    @ColumnInfo(name = "filename")
    private String filename;


    @NonNull
    public Integer getType() {
        return type;
    }

    public void setType(@NonNull Integer type) {
        this.type = type;
    }

    @NonNull
    @ColumnInfo(name = "type")
    private Integer type;



    public Downloads() {
    }


}
