package com.storyPost.PhotoVideoDownloader.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import com.storyPost.PhotoVideoDownloader.R;
import com.storyPost.PhotoVideoDownloader.models.HistoryModel;

/**
 * Created by tirgei on 10/29/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "zoomsta";
    private static final String DB_TABLE = "zoomsta_user", HISTORY_SEARCH = "history_search";
    private static final String USERNAME = "username";
    private static final String USER_ID = "userid";
    private static final String REALNAME = "realname";
    private static final String IMAGE_THUMBNAIL = "imagethumbnail";

    private static final String CREATE_TABLE = "CREATE TABLE " + DB_TABLE + "("
            + USERNAME + " TEXT," + IMAGE_THUMBNAIL + " BLOB);";

    private static final String CREATE_HISTORY_TABLE = "CREATE TABLE " + HISTORY_SEARCH + "("
            + USER_ID + " TEXT," + USERNAME + " TEXT," + REALNAME + " TEXT," + IMAGE_THUMBNAIL + " BLOB);";

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HISTORY_SEARCH);

        onCreate(sqLiteDatabase);
    }

    public void insertIntoDb(String username, Bitmap image){
        SQLiteDatabase db = this.getWritableDatabase();

        if(hasObject(username, DB_TABLE))
            deleteRow(username, DB_TABLE);

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(USERNAME, username);
            contentValues.put(IMAGE_THUMBNAIL, ZoomstaUtil.getBytes(image));
            db.insert(DB_TABLE, null, contentValues);
            db.close();

        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public void addHistory(String userId, String username, String realName, Bitmap image){
        SQLiteDatabase db = this.getWritableDatabase();

        if(hasObject(username, HISTORY_SEARCH))
            deleteRow(username, HISTORY_SEARCH);

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(USER_ID, userId);
            contentValues.put(USERNAME, username);
            contentValues.put(REALNAME, realName);
            contentValues.put(IMAGE_THUMBNAIL, ZoomstaUtil.getBytes(image));
            db.insert(HISTORY_SEARCH, null, contentValues);
            db.close();

        } catch (SQLiteException e){
            e.printStackTrace();
        }

        Log.d("dd_history", "adding " + username);

    }


    public List<HistoryModel> fetchHistory(Context context){
        List<HistoryModel> models = new ArrayList<>();

        String query = "SELECT * FROM " + HISTORY_SEARCH;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                HistoryModel model = new HistoryModel();
                model.setUserId(cursor.getString(0));
                model.setUserName(cursor.getString(1));
                model.setRealName(cursor.getString(2));
                model.setUserPic(cursor.getBlob(3));
                model.setFaved(ZoomstaUtil.containsUser(context, cursor.getString(0)));

                models.add(0, model);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        Log.d("AddHistory", "Added " + models.size() + " items");
        return models;
    }

    public Bitmap getUserIcon(Context context, String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Bitmap userIcon = null;

        if(hasObject(username, DB_TABLE)){
            String query = "SELECT * FROM " + DB_TABLE + " WHERE " + USERNAME + " =?";
            Cursor cursor = db.rawQuery(query, new String[] {username});

            if(cursor.moveToFirst()){

                    userIcon = ZoomstaUtil.getImage(cursor.getBlob(1));
            }

            cursor.close();
            db.close();

        } else {
            userIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_account);

        }

        return userIcon;

    }

    public void clearDb(Boolean all){
        SQLiteDatabase db = this.getWritableDatabase();
        if(all){
            db.delete(DB_TABLE, null, null);
            db.delete(HISTORY_SEARCH, null, null);
        } else {
            db.delete(HISTORY_SEARCH, null, null);
        }
        db.close();
    }

    public boolean hasObject(String username, String table) {
        SQLiteDatabase db = getWritableDatabase();
        String selectString = "SELECT * FROM " + table + " WHERE " + USERNAME + " =?";

        Cursor cursor = db.rawQuery(selectString, new String[] {username});

        boolean hasObject = false;
        if(cursor.moveToFirst()){
            hasObject = true;

        }

        cursor.close();
        return hasObject;
    }

    public void deleteRow(String username, String table)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table + " WHERE "+USERNAME+"='"+username+"'");
    }

}
