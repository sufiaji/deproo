package com.deproo.android.deproo.model;

import android.graphics.Bitmap;

public class Shop {

    private Bitmap mBitmap;
    private int mId;
    private String mTitle;

    public Shop(int id, String title, Bitmap bitmap) {
        mBitmap = bitmap;
        mId = id;
        mTitle = title;
    }

    public Bitmap getImage() {
        return mBitmap;
    }

    public void setImage(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
