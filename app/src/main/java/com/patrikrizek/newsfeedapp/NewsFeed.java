package com.patrikrizek.newsfeedapp;

public class NewsFeed {
    private String mTitle;
    private String mAuthor;
    private String mSection;
    private String mTimeToTransfer;
    private String mUrl;

    public NewsFeed(String title, String section, String timeToTransfer, String url, String author ) {
        mTitle = title;
        mSection = section;
        mTimeToTransfer = timeToTransfer;
        mUrl = url;
        mAuthor = author;
    }

    public String getTitle() { return mTitle; }
    public String getSection() { return mSection; }
    public String getTimeToTransfer() { return mTimeToTransfer; }
    public String getUrl() { return mUrl; }
    public String getAuthor() { return mAuthor; }
}
