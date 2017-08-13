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

import java.util.List;

public class NewsItemAdapter extends BaseAdapter {

    private final List<Article> data;
    private final LayoutInflater inflater;
    private Context context;

    public NewsItemAdapter(Context context, List<Article> data) {
        this.data = data;
        this.context = context;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Article art = (Article) getItem(position);
        View v;


        if (convertView == null) {
            v = inflater.inflate(R.layout.new_news_list_item, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.titleView = (TextView) v.findViewById(R.id.title);
            //holder.descriptionView = (TextView) v.findViewById(R.id.description);
            holder.pubDateView = (TextView) v.findViewById(R.id.pub_date);
            holder.image = (ImageView) v.findViewById(R.id.holder_image);
            v.setTag(holder);
        } else {
            v = convertView;
        }

        ViewHolder holder = (ViewHolder) v.getTag();
        holder.titleView.setText(art.title);
        //holder.descriptionView.setText(art.description);
        holder.pubDateView.setText(art.pubDate);
        Glide.with(context)
                .load(art.imageUrl)
                .into(holder.image);

        return v;
    }

    private static final class ViewHolder {
        private TextView titleView;
        private TextView descriptionView;
        private TextView pubDateView;
        private ImageView image;
    }
}
