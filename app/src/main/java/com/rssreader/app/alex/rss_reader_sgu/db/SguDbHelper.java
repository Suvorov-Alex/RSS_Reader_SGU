package com.rssreader.app.alex.rss_reader_sgu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SguDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "sgudb.sqlite";
    private static final int DB_VERSION = 3;

    public SguDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + SguDbContract.TABLE_NAME + "("
                + SguDbContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SguDbContract.COLUMN_GUID + " INTEGER NOT NULL UNIQUE, "
                + SguDbContract.COLUMN_TITLE + " TEXT NOT NULL, "
                + SguDbContract.COLUMN_DESCRIPTION + " TEXT, "
                + SguDbContract.COLUMN_LINK + " TEXT NOT NULL, "
                + SguDbContract.COLUMN_IMAGE_URL + " TEXT, "
                + SguDbContract.COLUMN_FAVOURITE + " INTEGER, "
                + SguDbContract.COLUMN_PUBDATE + " DATETIME"
                + ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + SguDbContract.TABLE_NAME);
        onCreate(db);
    }
}
