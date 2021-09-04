package com.animewatcher.animewatcher;

import java.sql.Time;
import java.util.Date;

public class episode_class {
    private int mID;
    private int mEpisodeNumber;
    private String mNameEN;
    private String mNameJP;
    private String mAnime;
    private int mLengthSecs;
    private int mViews;
    private Date mReleaseDate;
    private String mThumbnail;
    private String mVideoFileLink;
    private boolean misVCDN;

    public episode_class(int mID, int mEpisodeNumber, String mNameEN, String mNameJP, String mAnime, int mLengthSecs, int mViews, Date mReleaseDate, String mThumbnail, String mVideoFileLink, boolean misVCDN) {
        this.mID = mID;
        this.mEpisodeNumber = mEpisodeNumber;
        this.mNameEN = mNameEN;
        this.mNameJP = mNameJP;
        this.mAnime = mAnime;
        this.mLengthSecs = mLengthSecs;
        this.mViews = mViews;
        this.mReleaseDate = mReleaseDate;
        this.mThumbnail = mThumbnail;
        this.mVideoFileLink = mVideoFileLink;
        this.misVCDN = misVCDN;
    }

    public episode_class() {
        this.mID = -1;
        this.mEpisodeNumber = -1;
        this.mNameEN = "";
        this.mNameJP = "";
        this.mAnime = "";
        this.mLengthSecs = -1;
        this.mViews = -1;
        this.mReleaseDate = null;
        this.mThumbnail = "";
        this.mVideoFileLink = "";
        this.misVCDN = false;
    }

    public int getmID() {
        return mID;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }

    public int getmEpisodeNumber() {
        return mEpisodeNumber;
    }

    public void setmEpisodeNumber(int mEpisodeNumber) {
        this.mEpisodeNumber = mEpisodeNumber;
    }

    public String getmNameEN() {
        return mNameEN;
    }

    public void setmNameEN(String mNameEN) {
        this.mNameEN = mNameEN;
    }

    public String getmNameJP() {
        return mNameJP;
    }

    public void setmNameJP(String mNameJP) {
        this.mNameJP = mNameJP;
    }

    public String getmAnime() {
        return mAnime;
    }

    public void setmAnime(String mAnime) {
        this.mAnime = mAnime;
    }

    public int getmLengthSecs() {
        return mLengthSecs;
    }

    public void setmLengthSecs(int mLengthSecs) {
        this.mLengthSecs = mLengthSecs;
    }

    public int getmViews() {
        return mViews;
    }

    public void setmViews(int mViews) {
        this.mViews = mViews;
    }

    public Date getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(Date mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String getmThumbnail() {
        return mThumbnail;
    }

    public void setmThumbnail(String mThumbnail) {
        this.mThumbnail = mThumbnail;
    }

    public String getmVideoFileLink() {
        return mVideoFileLink;
    }

    public void setmVideoFileLink(String mVideoFileLink) {
        this.mVideoFileLink = mVideoFileLink;
    }

    public boolean isMisVCDN() {
        return misVCDN;
    }

    public void setMisVCDN(boolean misVCDN) {
        this.misVCDN = misVCDN;
    }
}
