//package in.blackpaper.instasp.data.repositry;
//
//import android.app.Application;
//
//import java.util.List;
//
//import androidx.lifecycle.LiveData;
//import in.blackpaper.fig.room.dao.BookmarksDao;
//import in.blackpaper.fig.room.database.BookmarksRoomDatabase;
//import in.blackpaper.fig.room.tables.Bookmarks;
//
//public class BookmarksRepositry {
//    private BookmarksDao mBookmarksDao;
//    private LiveData<List<Bookmarks>> mListLiveData;
//    private LiveData<List<Bookmarks>> mFavoriteBookmarks;
//
//
//    private BookmarksRoomDatabase mDatabase;
//
//    public BookmarksRepositry(Application application) {
//        mDatabase = BookmarksRoomDatabase.getDatabase(application);
//        mBookmarksDao = mDatabase.bookmarksDao();
//        mListLiveData = mBookmarksDao.getAllBookmarks();
//
//    }
//
//    public void insertBookmark(Bookmarks bookmarks) {
//        mBookmarksDao.insert(bookmarks);
//    }
//
//    public void deleteBookmark(int id) {
//        mBookmarksDao.delete(id);
//    }
//
//    public LiveData<List<Bookmarks>> getmListLiveData() {
//        return mListLiveData;
//    }
//
//    public Bookmarks getSelectedItem(int id) {
//        return mBookmarksDao.getSelectedItem(id);
//    }
//
//    public LiveData<List<Bookmarks>> getFavoriteBookmarks() {
//        return mBookmarksDao.getFavoriteBookmarks(true);
//    }
//
//    public void setFavorite(int id, boolean isFavorite) {
//        mBookmarksDao.setFavorite(id, isFavorite);
//    }
//}
