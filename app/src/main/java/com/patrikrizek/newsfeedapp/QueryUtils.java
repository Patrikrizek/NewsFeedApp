package com.patrikrizek.newsfeedapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class QueryUtils {
    private static final  String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }


    public static List<NewsFeed> fetchNewsFeedData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponde = null;
        try {
            jsonResponde = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "There is a problem with making the HTTP request.", e);
        }
        List<NewsFeed> newsFeeds = extractFeaturesFromJson(jsonResponde);
        return newsFeeds;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL( stringUrl );
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "There is a problem of building the URL ", e);
        }
        return url;
    }

    private static  String makeHttpRequest (URL url) throws IOException {
        String jsonResponse = "";
        if(url == null) {
            return jsonResponse;
        }

        HttpsURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setReadTimeout( 1000 );
            urlConnection.setConnectTimeout( 15000 );
            urlConnection.setRequestMethod( "GET" );
            urlConnection.connect();

            if (urlConnection.getResponseCode() == urlConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the newsfeed JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return  jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader
                    ( inputStream, Charset.forName( "UTF-8" ) );
            BufferedReader reader = new BufferedReader( inputStreamReader );
            String line = reader.readLine();
            while (line != null) {
                output.append( line );
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<NewsFeed> extractFeaturesFromJson(String newsFeedJSON) {
        if (TextUtils.isEmpty( newsFeedJSON )) {
            return null;
        }

        List<NewsFeed> newsFeeds = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject( newsFeedJSON );
            JSONObject subBase = baseJsonResponse.getJSONObject( "response" );
            JSONArray newsFeedArray = subBase.getJSONArray( "results" );

            for (int i = 0; i < newsFeedArray.length(); i++) {
                JSONObject results = newsFeedArray.getJSONObject( i );

                String title = results.getString( "webTitle" );
                Log.i("QueryUtils", "webTitle uploaded");

                String section = results.getString( "sectionName" );
                Log.i("QueryUtils", "sectionName uploaded");

                String publicationDate = results.getString( "webPublicationDate" );
                Log.i("QueryUtils", "webPublicationDate uploaded");

                String url = results.getString( "webUrl" );
                Log.i("QueryUtils", "webUrl uploaded");

                JSONArray tagsArray = results.getJSONArray("tags");
                String author = null;
                if (tagsArray.length() == 1) {
                    JSONObject contributorTag = (JSONObject) tagsArray.get(0);
                    author = contributorTag.getString("webTitle");
                    Log.i("QueryUtils", "webTitle author uploaded");
                }

                NewsFeed newsFeed = new NewsFeed( title, section, publicationDate, url, author );
                newsFeeds.add(newsFeed);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the newsfeed JSON results", e);
        }

        return newsFeeds;
    }
}
