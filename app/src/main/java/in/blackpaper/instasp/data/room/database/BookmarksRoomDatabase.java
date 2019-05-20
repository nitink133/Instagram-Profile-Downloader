package in.blackpaper.instasp.data.room.database;

import androidx.room.RoomDatabase;

//@Database(entities = {Bookmarks.class, PinnedText.class}, version = 1)
public abstract class BookmarksRoomDatabase extends RoomDatabase {
//    private static BookmarksRoomDatabase INSTANCE;
//    private static final String DATABASE_NAME = "wallet_database";
//
//    public abstract BookmarksDao bookmarksDao();
//    public abstract PinnedTextDao pinnedTextDao();
//
//    public static BookmarksRoomDatabase getDatabase(Context context) {
//        if (INSTANCE == null) {
//            synchronized (BookmarksRoomDatabase.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), BookmarksRoomDatabase.class,
//                            DATABASE_NAME).allowMainThreadQueries().build();
//                }
//            }
//        }
//        return INSTANCE;
//
//    }
}
