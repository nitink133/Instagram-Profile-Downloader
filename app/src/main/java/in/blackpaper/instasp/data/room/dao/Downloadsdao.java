package in.blackpaper.instasp.data.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import in.blackpaper.instasp.data.room.tables.Downloads;
import in.blackpaper.instasp.data.room.tables.Logins;
@Dao
public interface Downloadsdao {

    @Query("Select * From downloads_table")
    LiveData<List<Downloads>> getAllDownloads();

    @Query("Select * From downloads_table Where id = :id")
    Downloads getSelectedDownload(int id);

    @Insert
    long insert(Downloads downloads);

    @Query("Delete From downloads_table Where id = :id")
    int delete(int id);


}
