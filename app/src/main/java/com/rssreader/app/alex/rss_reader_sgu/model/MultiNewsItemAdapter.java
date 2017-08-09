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

import com.rssreader.app.alex.rss_reader_sgu.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Alex on 09.08.2017.
 */

public class MultiNewsItemAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<Article> data;

    public MultiNewsItemAdapter(Context context, List<Article> data) {
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
        Log.d("DateTag", data);
        String day = new SimpleDateFormat("d").format(new Date());

        String[] splitPubDate = data.split(" |:|-");
        Log.d("DateTag", Arrays.toString(splitPubDate));
        String articleDate = splitPubDate[2];
        if (articleDate.charAt(0) == '0') {
            articleDate = String.valueOf(articleDate.charAt(1));
        }
        int d = Integer.parseInt(day);
        int ad = Integer.parseInt(articleDate);

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
        final Article article = (Article) getItem(position);

        View view;

        if (convertView == null) {
            View dataView = layoutInflater.inflate(R.layout.data_subitem, null);
            view = layoutInflater.inflate(R.layout.item, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.pubDateView = (TextView) dataView.findViewById(R.id.pubDateTV);
            holder.titleView = (TextView) dataView.findViewById(R.id.titleTV);
            view.setTag(holder);

            LinearLayout itemLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
            if (dataView.getParent() != null) {
                ((ViewGroup) dataView.getParent()).removeView(dataView);
            }
            itemLayout.addView(dataView);
        } else {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.pubDateView.setText(formatData(article.pubDate));
        //holder.pubDateView.setText(article.pubDate);
        holder.titleView.setText(article.title);



       /* for (int i = 0; i < new Random().nextInt(10); i++){
            View newsView = layoutInflater.inflate(R.layout.news_subitem, null);

            if (newsView.getParent() != null){
                ((ViewGroup) newsView.getParent()).removeView(newsView);
            }

            itemLayout.addView(newsView);
        }*/


        return view;
    }

    private static final class ViewHolder {
        private TextView titleView;
        private TextView descriptionView;
        private TextView pubDateView;
        private ImageView image;
    }
}
