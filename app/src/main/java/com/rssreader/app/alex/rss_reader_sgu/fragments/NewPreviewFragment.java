package com.rssreader.app.alex.rss_reader_sgu.fragments;

import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rssreader.app.alex.rss_reader_sgu.R;
import com.rssreader.app.alex.rss_reader_sgu.db.SguDbContract;
import com.rssreader.app.alex.rss_reader_sgu.db.SguDbHelper;

import java.text.ParseException;

public class NewPreviewFragment extends Fragment {

    private SQLiteDatabase db;

    private ImageView imageView;
    private TextView titleTextView;
    private TextView pubDateTextView;
    private TextView descriptionTextView;

    public NewPreviewFragment() {
        setArguments(new Bundle());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_preview_fragment, container, false);

        db = new SguDbHelper(getActivity()).getReadableDatabase();

        imageView = (ImageView) v.findViewById(R.id.imageView);
        titleTextView = (TextView) v.findViewById(R.id.newTitle);
        pubDateTextView = (TextView) v.findViewById(R.id.pubdate);
        descriptionTextView = (TextView) v.findViewById(R.id.descriptionTextView);

        loadData();

        if (getArguments().getBoolean("delMenu")) {
            setHasOptionsMenu(false);
            getArguments().putBoolean("delMenu", false);
        } else {
            setHasOptionsMenu(true);
        }

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favourite) {
            setFavourite();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setFavourite() {
        Cursor cursor = db.query(SguDbContract.TABLE_NAME, null, null, null, null, null, null);
        int isFavourite = 0;
        while (cursor.moveToNext()) {
            int imageUrlColumnIndex = cursor.getColumnIndex(SguDbContract.COLUMN_IMAGE_URL);
            if (cursor.getString(imageUrlColumnIndex).equals(getArguments().getString("url"))) {
                isFavourite = cursor.getInt(cursor.getColumnIndex(SguDbContract.COLUMN_FAVOURITE));
            }
        }
        ContentValues contentValues = new ContentValues();
        if (isFavourite == 0) {
            contentValues.put(SguDbContract.COLUMN_FAVOURITE, 1);
            db.update(SguDbContract.TABLE_NAME, contentValues,
                    SguDbContract.COLUMN_IMAGE_URL + " = ?", new String[]{getArguments().getString("url")});
            Toast.makeText(getActivity(), "News added to favourites", Toast.LENGTH_SHORT).show();
        } else {
            contentValues.put(SguDbContract.COLUMN_FAVOURITE, 0);
            db.update(SguDbContract.TABLE_NAME, contentValues,
                    SguDbContract.COLUMN_IMAGE_URL + " = ?", new String[]{getArguments().getString("url")});
            Toast.makeText(getActivity(), "News removed from favourites", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.news_list_fragment_menu, menu);
    }


    public void reload() {
        String url = getArguments().getString("url");
        Glide.with(getActivity())
                .load(url)
                .into(imageView);
    }

    private void loadData() {
        Cursor cursor = db.query(SguDbContract.TABLE_NAME, new String[]{
                SguDbContract.COLUMN_TITLE,
                SguDbContract.COLUMN_DESCRIPTION,
                SguDbContract.COLUMN_PUBDATE,
                SguDbContract.COLUMN_LINK,
                SguDbContract.COLUMN_FAVOURITE,
                SguDbContract.COLUMN_IMAGE_URL
        }, null, null, null, null, SguDbContract.COLUMN_PUBDATE + " DESC");
        while (cursor.moveToNext()) {
            int imageUrlColumnIndex = cursor.getColumnIndex(SguDbContract.COLUMN_IMAGE_URL);
            if (cursor.getString(imageUrlColumnIndex).equals(getArguments().getString("url"))) {
                titleTextView.setText(cursor.getString(
                        cursor.getColumnIndex(SguDbContract.COLUMN_TITLE)));
                descriptionTextView.setText(cursor.getString(
                        cursor.getColumnIndex(SguDbContract.COLUMN_DESCRIPTION)));
                //descriptionTextView.setMovementMethod(new ScrollingMovementMethod());
                //String[] formatMonth = formatMonth(cursor);
                try {
                    pubDateTextView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(
                            cursor.getColumnIndex(SguDbContract.COLUMN_PUBDATE))).toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        reload();
        cursor.close();
    }

    private String[] formatMonth(Cursor cursor) {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        String pubDate = cursor.getString(
                cursor.getColumnIndex(SguDbContract.COLUMN_PUBDATE));
        String[] splitPubDate = pubDate.split(" |:|-");
        String month = splitPubDate[1];
        if (month.charAt(0) == '0') {
            month = String.valueOf((month.charAt(1)));
        }
        int monthInt = Integer.parseInt(month);
        return new String[]{months[monthInt - 1], splitPubDate[2], splitPubDate[0]};
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}
