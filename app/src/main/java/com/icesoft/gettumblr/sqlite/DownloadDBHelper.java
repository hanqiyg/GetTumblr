package com.icesoft.gettumblr.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018/5/2.
 */

public class DownloadDBHelper extends SQLiteOpenHelper
{
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "com.icesoft.gettumblr.downloads.db";
    public static final String TABLE_NAME = "downloads";

    public DownloadDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static final String ID_COLUMN_NAME = "id";
    public static final String TYPE_COLUMN_NAME= "type";
    public static final String SOURCE_URL_COLUMN_NAME = "source_url";
    public static final String DOWNLOAD_ID_COLUMN_NAME = "download_id";
    public static final String JSON_COLUMN_NAME = "infojson";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "  ( " +
                ID_COLUMN_NAME + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SOURCE_URL_COLUMN_NAME + " TEXT," +
                TYPE_COLUMN_NAME + " INTEGER," +
                DOWNLOAD_ID_COLUMN_NAME + " INTEGER," +
                JSON_COLUMN_NAME + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }
}
