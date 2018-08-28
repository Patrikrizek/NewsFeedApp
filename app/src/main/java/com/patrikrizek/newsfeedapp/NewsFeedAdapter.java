package com.patrikrizek.newsfeedapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NewsFeedAdapter extends ArrayAdapter<NewsFeed> {

   private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public NewsFeedAdapter(Context context, List<NewsFeed> newsFeeds) {
        super(context,0, newsFeeds);
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        View listItemView = convertView;
        if (listItemView == null ) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_feed_list_item, parent, false);

        }
        NewsFeed currentNewsFeed = getItem( position );

        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        String title = currentNewsFeed.getTitle();
        titleView.setText(title);

        TextView authorView = (TextView) listItemView.findViewById(R.id.author);
        String author = currentNewsFeed.getAuthor();
        authorView.setText(author);
        if (authorView.length() == 0) {
            authorView.setVisibility( View.GONE );
        }

        TextView sectionView = (TextView) listItemView.findViewById(R.id.section);
        String section = currentNewsFeed.getSection();
        sectionView.setText(section);

        TextView publicationDateView = (TextView) listItemView.findViewById(R.id.publication_date);
        String publicationDate = currentNewsFeed.getTimeToTransfer();
        publicationDateView.setText(publicationDate);

        // Display the date in correct format.
        // Snippet of this code found on here https://discussions.udacity.com/t/display-the-date-in-the-news-app/227647
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat
                ("yyyy-MM-dd'T'kk:mm:ss'Z'");
        Date date = null;
        try {
            date = simpleDateFormat.parse(currentNewsFeed.getTimeToTransfer());
        } catch (ParseException e) {
            Log.e(LOG_TAG, "getView: There is a problem parsing the date", e);
        }

        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd MMM, yyyy");
        String finalDate = newDateFormat.format(date);
        if (date != null) {
            publicationDateView.setText(finalDate);
        }
        return listItemView;
    }
}
