package com.rssreader.app.alex.rss_reader_sgu.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.rssreader.app.alex.rss_reader_sgu.R;
import com.rssreader.app.alex.rss_reader_sgu.db.SguDbContract;
import com.rssreader.app.alex.rss_reader_sgu.db.SguDbHelper;
import com.rssreader.app.alex.rss_reader_sgu.ui.fragment.NewsListFragmentContainer;
import com.rssreader.app.alex.rss_reader_sgu.model.Article;
import com.rssreader.app.alex.rss_reader_sgu.utils.NetUtils;
import com.rssreader.app.alex.rss_reader_sgu.utils.RssUtils;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class RefreshService extends Service {

    public static final String REFRESH_ACTION = "ru.sgu.csiit.sgu17.service" +
            ".RefreshService.ACTION_REFRESH";

    private static final String LOG_TAG = "RefreshService";
    private static final String URL = "http://www.sgu.ru/news.xml";

    private Thread refreshThread;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            SharedPreferences sharedPreferences = getSharedPreferences("MainActivity", MODE_PRIVATE);
            while (!Thread.interrupted()) {
                try {
                    boolean loadAllowed = isUseMobildeData() || isWifiConnected();
                    if (loadAllowed) {
                        loadData();

                    }
                    if (sharedPreferences.getLong("frequency", 900_000) == -1) {
                        break;
                    }
                    Log.d("RefreshServiceTime", "FREQUENCY: " + sharedPreferences.getLong("frequency", 900_000));
                    Log.d("RefreshServiceTime", "StartTimer " + Calendar.getInstance().getTime());
                    Thread.sleep(sharedPreferences.getLong("frequency", 900_000));
                    Log.d("RefreshServiceTime", "EndTimer " + Calendar.getInstance().getTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand()");
        if (intent != null && refreshThread == null) {
            refreshThread = new Thread(refreshRunnable);
            refreshThread.start();
        } else if (intent != null && refreshRunnable != null) {
            refreshThread.interrupt();
            refreshThread = new Thread(refreshRunnable);
            refreshThread.start();
        }
        return Service.START_STICKY;
    }

    private void onPostRefresh() {
        Log.d("RefreshServiceTime", "data refreshed");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(REFRESH_ACTION);
        sendBroadcast(broadcastIntent);
        sendNewNewsCountNotification();
    }

    private void loadData() {
        List<Article> netData = null;
        try {
            String httpResponse = NetUtils.httpGet(URL);
            netData = RssUtils.parseRss(httpResponse);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to get HTTP response: " + e.getMessage(), e);
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, "Failed to parse RSS: " + e.getMessage(), e);
        }
        SQLiteDatabase db = new SguDbHelper(RefreshService.this).getWritableDatabase();
        db.beginTransaction();
        try {
            if (netData != null) {
                int insertedCount = 0;
                for (Article a : netData) {
                    ContentValues cv = new ContentValues();
                    cv.put(SguDbContract.COLUMN_GUID, a.guid);
                    cv.put(SguDbContract.COLUMN_TITLE, a.title);
                    cv.put(SguDbContract.COLUMN_DESCRIPTION, a.description);
                    cv.put(SguDbContract.COLUMN_LINK, a.link);
                    cv.put(SguDbContract.COLUMN_PUBDATE, a.pubDate.getTime());
                    cv.put(SguDbContract.COLUMN_FAVOURITE, a.favourite);
                    cv.put(SguDbContract.COLUMN_IMAGE_URL, a.imageUrl);
                    long insertedId = db.insertWithOnConflict(SguDbContract.TABLE_NAME,
                            null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                    if (insertedId == -1L) {
                        Log.i(LOG_TAG, "skipped article guid=" + a.guid);
                    } else {
                        insertedCount++;
                    }
                }
                getSharedPreferences("MainActivity", MODE_PRIVATE).edit()
                        .putInt("newNewsCount", insertedCount)
                        .apply();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                hideInProgressNotification();
                onPostRefresh();
            }
        });
    }

    private void sendNewNewsCountNotification() {
        if (isNotificationsEnabled()) {
            Intent startIntent = new Intent(this, NewsListFragmentContainer.class);
            PendingIntent notificationIntent = PendingIntent.getActivity(
                    this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(this)
                    .setContentTitle("SGU RSS data refreshed")
                    .setContentText(getSharedPreferences("MainActivity", MODE_PRIVATE)
                            .getInt("newNewsCount", 0) + " New news")
                    .setContentIntent(notificationIntent)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.sgu_main)
                    .build();
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(1, notification);
        }
    }

    private void hideInProgressNotification() {
        stopForeground(false);
    }

    private boolean isNotificationsEnabled() {
        SharedPreferences prefs = getSharedPreferences("MainActivity"
                /*NewsListFragmentContainer.class.getSimpleName()*/, MODE_PRIVATE);
        return prefs.getBoolean("notifications", true);
    }

    private boolean isPeriodicUpdatesEnabled() {
        SharedPreferences prefs = getSharedPreferences("MainActivity"
                /*NewsListFragmentContainer.class.getSimpleName()*/, MODE_PRIVATE);
        return prefs.getBoolean("periodicUpdates", true);
    }

    private boolean isUseMobildeData() {
        SharedPreferences prefs = getSharedPreferences("MainActivity"
                /*NewsListFragmentContainer.class.getSimpleName()*/, MODE_PRIVATE);
        return prefs.getBoolean("useMobileData", false);
    }

    private boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        return netInfo != null
                && netInfo.isConnected()
                && netInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }
}
