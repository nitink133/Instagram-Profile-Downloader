package in.blackpaper.instasp.data.room.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


import in.blackpaper.instasp.data.room.dao.LoginsDao;
import in.blackpaper.instasp.data.room.tables.Logins;

@Database(entities = {Logins.class}, version = 1)
public abstract class LoginsDatabase extends RoomDatabase {
    private static LoginsDatabase INSTANCE;
    private static final String DATABASE_NAME = "wallet_database";

    public abstract LoginsDao loginsDao();

    public static LoginsDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (LoginsDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), LoginsDatabase.class,
                            DATABASE_NAME).allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;

    }
}
