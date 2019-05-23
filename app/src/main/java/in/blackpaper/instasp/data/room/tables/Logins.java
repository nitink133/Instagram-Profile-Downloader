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
    @NonNull
    @ColumnInfo(name = "profile_pic")
    private String profilePic;

    @NonNull
    public String getFullName() {
        return fullName;
    }

    public void setFullName(@NonNull String fullName) {
        this.fullName = fullName;
    }

    @NonNull
    public String getBio() {
        return bio;
    }

    public void setBio(@NonNull String bio) {
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

    @NonNull
    @ColumnInfo(name = "full_name")
    private String fullName;

    @NonNull
    @ColumnInfo(name = "bio")
    private String bio;

    @NonNull
    @ColumnInfo(name = "media")
    private int media;

    @NonNull
    @ColumnInfo(name = "followed_by")
    private int followedBy;

    @NonNull
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

    @NonNull
    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(@NonNull String profilePic) {
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
