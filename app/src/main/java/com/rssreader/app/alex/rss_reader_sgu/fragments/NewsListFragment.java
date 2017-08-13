package com.rssreader.app.alex.rss_reader_sgu.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.rssreader.app.alex.rss_reader_sgu.R;
import com.rssreader.app.alex.rss_reader_sgu.db.SguRssLoader;
import com.rssreader.app.alex.rss_reader_sgu.model.Article;
import com.rssreader.app.alex.rss_reader_sgu.model.MultiItemNewsAdapter;
import com.rssreader.app.alex.rss_reader_sgu.model.NewsItemAdapter;
import com.rssreader.app.alex.rss_reader_sgu.service.RefreshService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Article>> {

    private static final String LOG_TAG = "NewsListActivity";

    private final RefreshBroadcastReceiver refreshBroadcastReceiver = new RefreshBroadcastReceiver();
    private final ArrayList<Article> data = new ArrayList<>();
    private BaseAdapter dataAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private final class RefreshBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isResumed()) {
                getActivity().getLoaderManager().restartLoader(0, null, NewsListFragment.this);
                Toast.makeText(getActivity(), "Data Refreshed", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public interface Listener {
        void OnArticleClicked(Article article);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dataAdapter = new NewsItemAdapter(getActivity(), data);
        //this.dataAdapter = new MultiItemNewsAdapter(getActivity(), data);
        getLoaderManager().initLoader(0, null, this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.news_list_fragment, container, false);

        ListView newsList = (ListView) v.findViewById(R.id.news_list);
        newsList.setAdapter(dataAdapter);
        /*dataAdapter.notifyDataSetInvalidated();*/

        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = (Article) parent.getItemAtPosition(position);
                if (isResumed()) {
                    Listener l = (Listener) getActivity();
                    l.OnArticleClicked(article);
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

                Intent serviceIntent = new Intent(getActivity(), RefreshService.class);
                getActivity().startService(serviceIntent);

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(RefreshService.REFRESH_ACTION);
        getActivity().registerReceiver(refreshBroadcastReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(refreshBroadcastReceiver);
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader");
        return new SguRssLoader(getActivity());
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
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> loaderData) {
        Log.d(LOG_TAG, "onLoadFinished " + loader.hashCode());
        data.clear();
        data.addAll(loaderData);
        List<String> days = new ArrayList<>();
        List<Pair<String, Integer>> daysIndexes = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (!days.contains(formatData(data.get(i).pubDate))) {
                days.add(formatData(data.get(i).pubDate));
            }
            daysIndexes.add(new Pair<>(formatData(data.get(i).pubDate), i));
        }

        int j = 0;
        for (int i = 0; i < daysIndexes.size(); ) {
            int firstIndex = daysIndexes.size();
            int lastIndex = -1;
            while (daysIndexes.get(i).first.equals(days.get(j)) && j < days.size()) {
                if (daysIndexes.get(i).second < firstIndex) {
                    firstIndex = daysIndexes.get(i).second;
                }
                if (daysIndexes.get(i).second > lastIndex) {
                    lastIndex = daysIndexes.get(i).second;
                }
                if (i + 1 < daysIndexes.size()) {
                    i++;
                } else {
                    break;
                }
            }
            j++;
            if (lastIndex == firstIndex) {
                data.get(firstIndex).type = 0;
            } else if (firstIndex + 1 == lastIndex) {
                data.get(firstIndex).type = 0;
                data.get(lastIndex).type = 1;
            } else {
                data.get(firstIndex).type = 0;
                data.get(lastIndex).type = 1;
                for (int k = firstIndex + 1; k < lastIndex; k++) {
                    data.get(k).type = 1;
                }
            }
            if (i + 1 == daysIndexes.size()) {
                break;
            }
        }
        if (data.isEmpty()) {
            Toast.makeText(getActivity(), "Nothing to show", Toast.LENGTH_SHORT).show();
        }
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        Log.d(LOG_TAG, "onLoaderReset " + loader.hashCode());
    }

}