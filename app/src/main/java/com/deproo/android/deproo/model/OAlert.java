package com.deproo.android.deproo.model;

import android.app.AlertDialog;
import android.content.Context;

import com.deproo.android.deproo.R;

public class OAlert {

    private Context mContext;
    private String title, message;
    private Boolean cancelable = true;

    public OAlert(Context context) {
        mContext = context;
    }

    public OAlert setTitle(String title) {
        this.title = title;
        return this;
    }

    public OAlert setCancelable(Boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public OAlert setMessage(String message) {
        this.message = message;
        return this;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("OK", null);
        builder.setCancelable(cancelable);
        builder.create().show();
    }
}
