package in.blackpaper.instasp.data.room.tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "login_table")
public class Logins {
    public final static String TABLE_NAME = "login_table";

    public void setId(Integer id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;
    @NonNull
    @ColumnInfo(name = "user_name")
    private String userName;
    @ColumnInfo(name = "profile_pic")
    private String profilePic;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getMedia() {
        return media;
    }

    public void setMedia(int media) {
        this.media = media;
    }

    public int getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(int followedBy) {
        this.followedBy = followedBy;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    @ColumnInfo(name = "full_name")
    private String fullName;


    @ColumnInfo(name = "bio")
    private String bio;


    @ColumnInfo(name = "media")
    private int media;

    @ColumnInfo(name = "cookies")
    private String cookies;


    @ColumnInfo(name = "csrf")
    private String csrf;


    public String getCookies() {
        return cookies;
    }

    public void setCookies( String cookies) {
        this.cookies = cookies;
    }

    public String getCsrf() {
        return csrf;
    }

    public void setCsrf(String csrf) {
        this.csrf = csrf;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    @ColumnInfo(name = "session_id")
    private String session_id;


    @ColumnInfo(name = "followed_by")
    private int followedBy;


    @ColumnInfo(name = "follows")
    private int follows;

    public Integer getId() {
        return id;
    }


    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getUserName() {
        return userName;
    }

    public void setUserName(@NonNull String userName) {
        this.userName = userName;
    }


    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @ColumnInfo(name = "token")
    private String token;


    public Logins() {
    }


}
