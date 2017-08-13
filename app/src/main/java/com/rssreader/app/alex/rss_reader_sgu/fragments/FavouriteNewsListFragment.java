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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.rssreader.app.alex.rss_reader_sgu.R;
import com.rssreader.app.alex.rss_reader_sgu.db.FavouriteNewsLoader;
import com.rssreader.app.alex.rss_reader_sgu.model.Article;
import com.rssreader.app.alex.rss_reader_sgu.model.NewsItemAdapter;
import com.rssreader.app.alex.rss_reader_sgu.service.RefreshService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 21.07.2017.
 */

public class FavouriteNewsListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Article>> {
    private static final String LOG_TAG = "NewsListActivity";

    private final FavouriteNewsListFragment.RefreshBroadcastReceiver refreshBroadcastReceiver =
            new FavouriteNewsListFragment.RefreshBroadcastReceiver();
    private final ArrayList<Article> data = new ArrayList<>();
    private NewsItemAdapter dataAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private final class RefreshBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isResumed()) {
                getActivity().getLoaderManager().restartLoader(0, null, FavouriteNewsListFragment.this);
                Toast.makeText(getActivity(), "Favourite News Refreshed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface Listener {
        void OnFavouriteArticleClicked(Article article);
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
        getLoaderManager().initLoader(0, null, this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.news_list_fragment, container, false);

        ListView newsList = (ListView) v.findViewById(R.id.news_list);
        newsList.setAdapter(dataAdapter);
        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = (Article) parent.getItemAtPosition(position);
                if (isResumed()) {
                    FavouriteNewsListFragment.Listener l = (FavouriteNewsListFragment.Listener) getActivity();
                    l.OnFavouriteArticleClicked(article);
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
                getActivity().stopService(serviceIntent);
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
        return new FavouriteNewsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> loaderData) {
        Log.d(LOG_TAG, "onLoadFinished " + loader.hashCode());
        data.clear();
        data.addAll(loaderData);
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
