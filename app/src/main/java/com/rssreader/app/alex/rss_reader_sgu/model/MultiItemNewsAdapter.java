package com.rssreader.app.alex.rss_reader_sgu.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rssreader.app.alex.rss_reader_sgu.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Alex on 09.08.2017.
 */

public class MultiItemNewsAdapter extends BaseAdapter {

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
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = (Article) getItem(position);
        View view;
        if (convertView == null) {
            ViewHolder viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.item, parent, false);
            LinearLayout itemLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
            View dataView;
            if (article.type == 0) {
                dataView = layoutInflater.inflate(R.layout.type0_item, null);
                viewHolder.titleView = (TextView) dataView.findViewById(R.id.title0);
                viewHolder.pubDateView = (TextView) dataView.findViewById(R.id.postDate);
            } else {
                dataView = layoutInflater.inflate(R.layout.type1_item, null);
                viewHolder.titleView = (TextView) dataView.findViewById(R.id.title1);
            }
            itemLayout.addView(dataView);
            view.setTag(viewHolder);
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            if (viewHolder.pubDateView != null && article.type == 0 ||
                    viewHolder.pubDateView == null && article.type == 1) {
                view = convertView;
            } else if (viewHolder.pubDateView != null && article.type == 1) {
                view = layoutInflater.inflate(R.layout.item, parent, false);
                View dataView = layoutInflater.inflate(R.layout.type1_item, null);
                viewHolder.titleView = (TextView) dataView.findViewById(R.id.title1);
                LinearLayout itemLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
                itemLayout.addView(dataView);
                view.setTag(viewHolder);
            } else {
                view = layoutInflater.inflate(R.layout.item, parent, false);
                View dataView = layoutInflater.inflate(R.layout.type0_item, null);
                viewHolder.titleView = (TextView) dataView.findViewById(R.id.title0);
                viewHolder.pubDateView = (TextView) dataView.findViewById(R.id.postDate);
                LinearLayout itemLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
                itemLayout.addView(dataView);
                view.setTag(viewHolder);
            }
        }
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        if (article.type == 0) {
            viewHolder.pubDateView.setText(formatData(article.pubDate));
            viewHolder.titleView.setText(article.title);
        } else {
            viewHolder.titleView.setText(article.title);
        }

        return view;
    }


    private static final class ViewHolder {
        private TextView titleView;
        private TextView pubDateView;
    }
}


