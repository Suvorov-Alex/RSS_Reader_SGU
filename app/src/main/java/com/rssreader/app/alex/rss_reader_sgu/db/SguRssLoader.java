package com.rssreader.app.alex.rss_reader_sgu.db;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rssreader.app.alex.rss_reader_sgu.model.Article;

import java.util.ArrayList;
import java.util.List;

final public class SguRssLoader extends AsyncTaskLoader<List<Article>> {

    private static final String URL = "http://www.sgu.ru/news.xml";
    private static final String LOG_TAG = "SguRssLoader";

    private List<Article> data;

    public SguRssLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (data != null) {
            deliverResult(data);
        } else {
            forceLoad();
        }
    }

    @Override
    public List<Article> loadInBackground() {
        List<Article> res = null;
        SQLiteDatabase db = new SguDbHelper(getContext()).getReadableDatabase();
        Cursor cursor = db.query(SguDbContract.TABLE_NAME, new String[]{
                SguDbContract.COLUMN_TITLE,
                SguDbContract.COLUMN_DESCRIPTION,
                SguDbContract.COLUMN_PUBDATE,
                SguDbContract.COLUMN_LINK,
                SguDbContract.COLUMN_FAVOURITE,
                SguDbContract.COLUMN_IMAGE_URL,
                SguDbContract.COLUMN_GUID
        }, null, null, null, null, SguDbContract.COLUMN_PUBDATE + " DESC");
        try {
            res = new ArrayList<>();
            while (cursor.moveToNext()) {
                Article article = new Article();
                article.title = cursor.getString(0);
                article.description = cursor.getString(1);
                article.pubDate = cursor.getString(2);
                article.link = cursor.getString(3);
                article.favourite = cursor.getInt(4);
                article.imageUrl = cursor.getString(5);
                article.guid = cursor.getLong(6);

                res.add(article);
            }
        } finally {
            cursor.close();
            db.close();
        }
        Log.d(LOG_TAG, "load finished");
        return res;
    }

    @Override
    protected void onReset() {
        this.data = null;
    }
}
