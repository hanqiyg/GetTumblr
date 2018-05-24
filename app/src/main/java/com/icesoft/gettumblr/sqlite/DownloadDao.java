package com.icesoft.gettumblr.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.icesoft.gettumblr.models.TypedJson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by Administrator on 2018/5/2.
 */

public class DownloadDao
{
    private static final String NULL_COLUMN_HACK = "NullColumn";
    private DownloadDBHelper downloadDBHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private static final String SELECT_KEY = " = ?";
    private static final String ORDER = " DESC";
    private static final String ORDERBY_AUTOINCREMENT_ID = DownloadDBHelper.ID_COLUMN_NAME + ORDER;
    private static final String SELECTIONBY_TYPE = DownloadDBHelper.TYPE_COLUMN_NAME + SELECT_KEY;
    private static final String SELECTIONBY_SOURCE = DownloadDBHelper.SOURCE_URL_COLUMN_NAME + SELECT_KEY;
    public DownloadDao(Context context)
    {
        downloadDBHelper = new DownloadDBHelper(context);
    }

    public void addDownload(int type, String sourceUrl,long downloadId,String json)
    {
        db = downloadDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DownloadDBHelper.TYPE_COLUMN_NAME,                    type);
        contentValues.put(DownloadDBHelper.SOURCE_URL_COLUMN_NAME,        sourceUrl);
        contentValues.put(DownloadDBHelper.DOWNLOAD_ID_COLUMN_NAME,   downloadId);
        contentValues.put(DownloadDBHelper.JSON_COLUMN_NAME,                    json);
        db.insertWithOnConflict(DownloadDBHelper.TABLE_NAME,NULL_COLUMN_HACK,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }
    public void delDownload(long downloadId)
    {
        db = downloadDBHelper.getWritableDatabase();
        db.delete(DownloadDBHelper.TABLE_NAME,DownloadDBHelper.DOWNLOAD_ID_COLUMN_NAME + SELECT_KEY ,new String[]{String.valueOf(downloadId)});
        db.close();
    }
    public void delDownloadBySourceUrl(String sourceUrl)
    {
        db = downloadDBHelper.getWritableDatabase();
        db.delete(DownloadDBHelper.TABLE_NAME,DownloadDBHelper.SOURCE_URL_COLUMN_NAME + SELECT_KEY ,new String[]{String.valueOf(sourceUrl)});
        db.close();
    }
    public int getCountByType(int type) {
        int count = 0;
        db = downloadDBHelper.getReadableDatabase();
        cursor = db.query(DownloadDBHelper.TABLE_NAME,
                new String[]{"COUNT(" + DownloadDBHelper.ID_COLUMN_NAME + ")"},
                SELECTIONBY_TYPE, new String[]{String.valueOf(type)},
                null,null,null,
                "0,1");
        if(cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        return count;
    }
    public int getCount() {
        int count = 0;
        db = downloadDBHelper.getReadableDatabase();
        cursor = db.query(DownloadDBHelper.TABLE_NAME,
                new String[]{"COUNT(" + DownloadDBHelper.ID_COLUMN_NAME + ")"},
                null,null,null,null,null,
                "0,1");
        if(cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        return count;
    }
    public String getDownloadByType(int type, int position)
    {
        db = downloadDBHelper.getReadableDatabase();
        cursor = db.query(
                DownloadDBHelper.TABLE_NAME,
                new String[]{DownloadDBHelper.JSON_COLUMN_NAME},
                SELECTIONBY_TYPE, new String[]{String.valueOf(type)},
                null,null,
                ORDERBY_AUTOINCREMENT_ID,
                position + " , " + 1);
        String json = null;
        if(cursor.moveToFirst())
        {
            json = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return json;
    }

    public TypedJson getDownloadBySource(String link)
    {
        db = downloadDBHelper.getReadableDatabase();
        cursor = db.query(
                DownloadDBHelper.TABLE_NAME,
                new String[]{DownloadDBHelper.TYPE_COLUMN_NAME,DownloadDBHelper.JSON_COLUMN_NAME},
                SELECTIONBY_SOURCE, new String[]{link},
                null,null,
                ORDERBY_AUTOINCREMENT_ID,
                null);
        TypedJson typedJson = null;
        int type = -1;
        String json = null;
        if(cursor.moveToFirst())
        {
            type = cursor.getInt(0);
            json = cursor.getString(1);
            typedJson = new TypedJson(type,json);
        }
        cursor.close();
        db.close();
        return typedJson;
    }
}
