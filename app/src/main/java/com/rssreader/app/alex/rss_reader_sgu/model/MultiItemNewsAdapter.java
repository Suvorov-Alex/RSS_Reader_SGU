package com.rssreader.app.alex.rss_reader_sgu.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rssreader.app.alex.rss_reader_sgu.R;

import java.util.Date;
import java.util.List;

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

    private String formatDateToDays(Date date) {
        long curTime = System.currentTimeMillis();
        Date curDate = new Date(curTime);
        Date tomorrowDate = new Date(curTime - 86400000);

        if (curDate.getMonth() == date.getMonth() && curDate.getDate() == date.getDate()) {
            return "Today";
        } else if (tomorrowDate.getMonth() == date.getMonth()
                && tomorrowDate.getDate() == date.getDate()) {
            return "Tomorrow";
        } else {
            if (curDate.getMonth() == date.getMonth()) {
                return (curDate.getDate() - date.getDate() + 1) + " Days ago";
            } else {
                int month = curDate.getMonth() + 1;
                if (month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                    return (curDate.getDate() - date.getDate() + 32) + " Days ago";
                } else if (month == 2) {
                    return (curDate.getDate() - date.getDate() + 29) + " Days ago";
                } else {
                    return (curDate.getDate() - date.getDate() + 31) + " Days ago";
                }
            }
        }
    }

    private String formatDate(Date date) {
        String[] months = {"Jan.", "Feb.", "Mar.", "Apr.", "May", "Ju.", "Jul.", "Aug.", "Sept.",
                "Oct.", "Nov.", "Dec."};

        String[] days = {"Mon.", "Tue.", "Wed.", "Thu.", "Fri.", "Sat.", "Sun."};

        return date.getDate() + ", "
                + months[date.getMonth() + 1]
                + " " + days[date.getDay()] + " "
                + (date.getYear() - 100 + 2000);
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
                TextView pubDate = (TextView) convertView.findViewById(R.id.pubDateTV);
                ImageView image = (ImageView) convertView.findViewById(R.id.holder_image);

                if (title == null) {
                    convertView = layoutInflater.inflate(R.layout.type0_item, parent, false);
                    title = (TextView) convertView.findViewById(R.id.newsTitle);
                    pubDate = (TextView) convertView.findViewById(R.id.pubDateTV);
                    image = (ImageView) convertView.findViewById(R.id.holder_image);
                }
                title.setText(article.title);
                pubDate.setText(formatDate(article.pubDate));
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
                postDate.setText(formatDateToDays(article.pubDate));
                break;
        }

        return convertView;
    }
}


