package com.animewatcher.animewatcher;

import java.util.ArrayList;
import java.util.List;

public class anime_class {
    private String mNameEN, mNameJP, mDescription, mThumbnail;
    private List<String> mCategories;
    private int mEpisodeCount;
    private boolean mOnGoing;

    public anime_class(String mNameEN, String mNameJP, String mDescription, String mThumbnail, int mEpisodeCount, boolean mOnGoing, List<String> Categories) {
        this.mNameEN = mNameEN;
        this.mNameJP = mNameJP;
        this.mDescription = mDescription;
        this.mThumbnail = mThumbnail;
        this.mEpisodeCount = mEpisodeCount;
        this.mOnGoing = mOnGoing;
        this.mCategories = Categories;
    }

    public anime_class() {
        this.mNameEN = "";
        this.mNameJP = "";
        this.mDescription = "";
        this.mThumbnail = "";
        this.mEpisodeCount = -1;
        this.mOnGoing = false;
        this.mCategories = new ArrayList<>();
    }

    public List<String> getmCategories() {
        return mCategories;
    }

    public void setmCategories(List<String> mCategories) {
        this.mCategories = mCategories;
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

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmThumbnail() {
        return mThumbnail;
    }

    public void setmThumbnail(String mThumbnail) {
        this.mThumbnail = mThumbnail;
    }

    public int getmEpisodeCount() {
        return mEpisodeCount;
    }

    public void setmEpisodeCount(int mEpisodeCount) {
        this.mEpisodeCount = mEpisodeCount;
    }

    public boolean ismOnGoing() {
        return mOnGoing;
    }

    public void setmOnGoing(boolean mOnGoing) {
        this.mOnGoing = mOnGoing;
    }
}
