package com.animewatcher.animewatcher;

public class subtitle_class {
    private int mID;
    private int mEpisode;
    private String mLink;
    private String mLanguage;
    private String mShortLanguage;

    public subtitle_class(int mID, int mEpisode, String mLink, String mLanguage, String mShortLanguage) {
        this.mID = mID;
        this.mEpisode = mEpisode;
        this.mLink = mLink;
        this.mLanguage = mLanguage;
        this.mShortLanguage = mShortLanguage;
    }

    public subtitle_class() {
        this.mID = -1;
        this.mEpisode = -1;
        this.mLink = "";
        this.mLanguage = "";
        this.mShortLanguage = "";
    }

    public int getmID() {
        return mID;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }

    public int getmEpisode() {
        return mEpisode;
    }

    public void setmEpisode(int mEpisode) {
        this.mEpisode = mEpisode;
    }

    public String getmLink() {
        return mLink;
    }

    public void setmLink(String mLink) {
        this.mLink = mLink;
    }

    public String getmLanguage() {
        return mLanguage;
    }

    public void setmLanguage(String mLanguage) {
        this.mLanguage = mLanguage;
    }

    public String getmShortLanguage() {
        return mShortLanguage;
    }

    public void setmShortLanguage(String mShortLanguage) {
        this.mShortLanguage = mShortLanguage;
    }
}
