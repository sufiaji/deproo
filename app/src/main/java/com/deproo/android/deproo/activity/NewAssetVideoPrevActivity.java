package com.deproo.android.deproo.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.utils.Utils;

public class NewAssetVideoPrevActivity extends AppCompatActivity {

    private VideoView mVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_asset_vid_prev);
        getSupportActionBar().setTitle(R.string.title_preview_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String path = getIntent().getStringExtra(NewAssetActivity.SELECTED_VIDEO_PATH);

        mVideoView = (VideoView) findViewById(R.id.id_video_prev);
        mVideoView.setVideoPath(path);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
        mVideoView.setKeepScreenOn(true);

        mVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mVideoView.pause();
        switch (item.getItemId()) {
            case android.R.id.home: {
                // todo: goto back activity from here
                onBackPressed();
                return true;
            }
            case R.id.action_delete: {
                Intent intent = new Intent();
                Utils.CommonAlertOkCancelDisplayerResult(
                        this,
                        "Deproo",
                        "Hapus video?",
                        "Ya",
                        "Tidak",
                        intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
