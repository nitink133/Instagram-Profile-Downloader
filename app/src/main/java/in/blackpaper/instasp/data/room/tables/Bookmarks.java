package in.blackpaper.instasp.data.room.tables;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookmarks_table")
public class Bookmarks {
    public final static String TABLE_NAME = "bookmarks_table";

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @NonNull
    @ColumnInfo(name = "title")
    private String mTitle;
    @NonNull
    @ColumnInfo(name = "url")
    private String mUrl;
    @NonNull
    @ColumnInfo(name = "date")
    private String mDate;
    @ColumnInfo(name = "tags")
    private String mTags;


    @ColumnInfo(name = "image")
    private String mImage;

    @ColumnInfo(name = "isFavorite")
    private boolean Favorite = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getTags() {
        return mTags;
    }

    public void setTags(String mTags) {
        this.mTags = mTags;
    }

    public boolean getFavorite() {
        return Favorite;
    }

    public void setFavorite(boolean mIsFavorite) {
        this.Favorite = mIsFavorite;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public Bookmarks() {
    }

    @Ignore
    public Bookmarks(String title, String url) {
        this.mTitle = title;
        this.mUrl = url;

    }
}
