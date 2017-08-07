package com.rssreader.app.alex.rss_reader_sgu;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.rssreader.app.alex.rss_reader_sgu.fragments.FavouriteNewsListFragment;
import com.rssreader.app.alex.rss_reader_sgu.fragments.FavouriteNewsListFragmentContainer;
import com.rssreader.app.alex.rss_reader_sgu.fragments.NewPreviewFragment;
import com.rssreader.app.alex.rss_reader_sgu.fragments.NewsListFragment;
import com.rssreader.app.alex.rss_reader_sgu.fragments.NewsListFragmentContainer;
import com.rssreader.app.alex.rss_reader_sgu.fragments.PrefsFragment;
import com.rssreader.app.alex.rss_reader_sgu.fragments.UpdateFrequencyFragment;
import com.rssreader.app.alex.rss_reader_sgu.model.Article;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        NewsListFragment.Listener,
        FavouriteNewsListFragment.Listener,
        PrefsFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_container, new NewsListFragmentContainer())
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //startService(new Intent(this, LocationService.class));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Log.d("TAG", "onBackPressed: " + getFragmentManager().getBackStackEntryCount());
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_news) {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStackImmediate();
            }
            Fragment newsListFragmentContainer;
            if (getFragmentManager().findFragmentById(R.id.container) == null) {
                newsListFragmentContainer = new NewsListFragmentContainer();
            } else {
                newsListFragmentContainer = getFragmentManager().findFragmentById(R.id.container);
            }
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_container, newsListFragmentContainer)
                    .commit();
        } else if (id == R.id.nav_favourite) {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStackImmediate();
            }
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_container, new FavouriteNewsListFragmentContainer())
                    .commit();
        } else if (id == R.id.nav_settings) {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStackImmediate();
            }
            Fragment prefsFragment;
            if (getFragmentManager().findFragmentById(R.id.prefsFragment) == null) {
                prefsFragment = new PrefsFragment();
            } else {
                prefsFragment = getFragmentManager().findFragmentById(R.id.prefsFragment);
            }
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_container, prefsFragment)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void OnArticleClicked(Article article) {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
        //if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            NewPreviewFragment fragment = new NewPreviewFragment();
            fragment.getArguments().putString("url", article.imageUrl);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        /*} else {
            NewPreviewFragment fragment = (NewPreviewFragment) getFragmentManager()
                    .findFragmentById(R.id.new_preview_fragment);
            fragment.getArguments().putString("url", article.imageUrl);
            fragment.reload();
        }*/
    }

    @Override
    public void OnFavouriteArticleClicked(Article article) {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            NewPreviewFragment fragment = new NewPreviewFragment();
            fragment.getArguments().putString("url", article.imageUrl);
            getFragmentManager().beginTransaction()
                    .replace(R.id.favourite_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            NewPreviewFragment fragment = (NewPreviewFragment) getFragmentManager()
                    .findFragmentById(R.id.new_preview_fragment);
            fragment.getArguments().putString("url", article.imageUrl);
            fragment.reload();
        }
    }

    @Override
    public void onUpdateFrequencyClicked() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
        UpdateFrequencyFragment frequencyFragment = new UpdateFrequencyFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.main_container, frequencyFragment)
                .addToBackStack(null)
                .commit();
    }
}
