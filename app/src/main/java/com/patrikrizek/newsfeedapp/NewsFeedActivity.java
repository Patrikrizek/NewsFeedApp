package com.patrikrizek.newsfeedapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsFeed>> {


    private static final String USGS_REQUEST_URL = "https://content.guardianapis.com/search?show-tags=contributor&q=czech%20republic&api-key=0d3debfb-b251-4852-a537-60d7e1d24911";

    private static final int NEWSFEED_LOADER_ID = 1;
    private NewsFeedAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate ( Bundle saveInstanceState ) {
        super.onCreate( saveInstanceState );
        setContentView( R.layout.news_feed_activity );

        ListView newsFeedListView = (ListView) findViewById( R.id.list );
        mEmptyStateTextView = (TextView) findViewById( R.id.empty_view);
        newsFeedListView.setEmptyView( mEmptyStateTextView );

        mAdapter = new NewsFeedAdapter( this, new ArrayList<NewsFeed>() );
        newsFeedListView.setAdapter( mAdapter );

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);

        /*
         * Information on how to do this was found here:
         * https://developer.android.com/training/swipe/
         */
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLoaderManager().restartLoader
                        (NEWSFEED_LOADER_ID,null, NewsFeedActivity.this);
                mSwipeRefreshLayout.setRefreshing(false); // Disables the refresh icon
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if( networkInfo != null && networkInfo.isConnectedOrConnecting() ) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader( NEWSFEED_LOADER_ID, null, this );
        } else {
            View loadingIndicator = findViewById( R.id.loading_indicator );
            loadingIndicator.setVisibility( View.GONE );
            mEmptyStateTextView.setText(getString( R.string.network_info ));
        }

        newsFeedListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                NewsFeed currentNewsFeed = mAdapter.getItem( position );
                Uri newsfeedUri = Uri.parse( currentNewsFeed.getUrl() );
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsfeedUri);
                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public Loader<List<NewsFeed>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String maxShownAmount = sharedPrefs.getString(getString(R.string.settings_max_shown_amount_key),
                getString(R.string.settings_max_shown_amount_default));

        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));


        Uri baseUri = Uri.parse(USGS_REQUEST_URL);

        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("page-size", maxShownAmount);
        uriBuilder.appendQueryParameter("order-by", orderBy);

        return new NewsFeedLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsFeed>> loader, List<NewsFeed> newsFeeds ) {
        View loadingIndicator = findViewById( R.id.loading_indicator );
        loadingIndicator.setVisibility( View.GONE );
        mEmptyStateTextView.setText( getString( R.string.on_load_finished ) );
        mAdapter.clear();
        if( newsFeeds != null && !newsFeeds.isEmpty()) {
            mAdapter.addAll(newsFeeds);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsFeed>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent (this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected( item );
    }
}