package com.deproo.android.deproo.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.model.Asset;
import com.deproo.android.deproo.utils.Constants;

public class NewDiscussionActivity extends AppCompatActivity {

    private Asset mAsset;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_discussion);
        mAsset = getIntent().getParcelableExtra(Constants.ASSET_OBJECT);
    }

    public void onNewDiscussionClick(View view) {

    }

    public void onNewPrivateMessageClick(View view) {

    }
}
