package com.rssreader.app.alex.rss_reader_sgu.db;

import android.provider.BaseColumns;

public class SguDbContract implements BaseColumns {
    public static final String TABLE_NAME = "articles";
    public static final String COLUMN_GUID = "guid";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_PUBDATE = "pub_date";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_FAVOURITE = "favourite";

    private SguDbContract() {
    }
}
