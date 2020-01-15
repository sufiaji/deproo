package com.deproo.android.deproo.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import com.deproo.android.deproo.R;
import com.deproo.android.deproo.utils.Constants;
import com.deproo.android.deproo.utils.Utils;

import java.io.File;

public class NewAssetImagePrevActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_asset_img_prev);
        getSupportActionBar().setTitle(R.string.title_preview_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView myImage = (ImageView) findViewById(R.id.id_imageview_preview);
        String path = getIntent().getStringExtra(NewAssetActivity.SELECTED_IMAGE_PATH);
        File imgFile = new File(path);
        //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        //myImage.setImageBitmap(myBitmap);
        myImage.setImageBitmap(Utils.decodeBitmapFromPathResize(
                path,
                Constants.DEFAULT_IMAGE_SIZE,
                Constants.DEFAULT_IMAGE_SIZE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                        "Hapus gambar?",
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
