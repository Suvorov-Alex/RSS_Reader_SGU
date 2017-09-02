package com.rssreader.app.alex.rss_reader_sgu.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rssreader.app.alex.rss_reader_sgu.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Alex on 09.08.2017.
 */

public class MultiItemNewsAdapter extends BaseAdapter {

    private static final int TYPE_NEWS = 0;
    private static final int TYPE_HEADER = 1;

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Article> data;

    public MultiItemNewsAdapter(Context context, List<Article> data) {
        this.context = context;
        this.data = data;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private String formatData(String data) {
        String day = new SimpleDateFormat("d").format(new Date());
        String[] splitPubDate = data.split(" |:|-");
        String articleDate = splitPubDate[2];
        if (articleDate.charAt(0) == '0') {
            articleDate = String.valueOf(articleDate.charAt(1));
        }
        int d = Integer.parseInt(day);
        int ad = Integer.parseInt(articleDate);
        d++;
        Log.d("DateTag", String.valueOf(d) + " " + ad);
        int difference = d - ad;
        if (difference < 0) {
            if (splitPubDate[1].equals("01") ||
                    splitPubDate[1].equals("03") ||
                    splitPubDate[1].equals("05") ||
                    splitPubDate[1].equals("07") ||
                    splitPubDate[1].equals("08") ||
                    splitPubDate[1].equals("10") ||
                    splitPubDate[1].equals("12")) {
                difference += 31;
            } else if (splitPubDate[1].equals("02")) {
                difference += 28;
            } else {
                difference += 30;
            }
        }
        Log.d("DateTag", String.valueOf(difference));
        String result;
        switch (difference) {
            case 0:
                result = "Today";
                break;
            case 1:
                result = "Yesterday";
                break;
            default:
                result = difference + " Days ago";
                break;
        }

        return result;
    }

    @Override
    public int getItemViewType(int position) {
        Article article = (Article) getItem(position);
        if (article.type == 0) {
            return TYPE_NEWS;
        }

        return TYPE_HEADER;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = (Article) getItem(position);
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_NEWS:
                    convertView = layoutInflater.inflate(R.layout.type0_item, parent, false);
                    break;
                case TYPE_HEADER:
                    convertView = layoutInflater.inflate(R.layout.type1_item, parent, false);
                    break;
            }
        }

        switch (type) {
            case TYPE_NEWS:
                TextView title = (TextView) convertView.findViewById(R.id.newsTitle);
                ImageView image = (ImageView) convertView.findViewById(R.id.holder_image);
                if (title == null) {
                    convertView = layoutInflater.inflate(R.layout.type0_item, parent, false);
                    title = (TextView) convertView.findViewById(R.id.newsTitle);
                    image = (ImageView) convertView.findViewById(R.id.holder_image);
                }
                title.setText(article.title);
                Glide.with(context)
                        .load(article.imageUrl)
                        //.error(R.drawable.sgulogo)
                        .crossFade()
                        .fitCenter()
                        .into(image);
                break;
            case TYPE_HEADER:
                TextView postDate = (TextView) convertView.findViewById(R.id.postDate);
                if (postDate == null) {
                    convertView = layoutInflater.inflate(R.layout.type1_item, parent, false);
                    postDate = (TextView) convertView.findViewById(R.id.postDate);
                }
                postDate.setText(article.pubDate.toString());
                break;
        }

        return convertView;
    }


    private static final class ViewHolder {
        private TextView titleView;
        private TextView pubDateView;
    }
}


