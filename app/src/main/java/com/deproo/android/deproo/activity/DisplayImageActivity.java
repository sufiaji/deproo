package com.deproo.android.deproo.activity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.utils.Constants;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DisplayImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        String uriImage = getIntent().getStringExtra(Constants.URI);
        if(uriImage!=null && !uriImage.isEmpty()) {
            Uri uri = Uri.parse(uriImage);
            ImageView iv = findViewById(R.id.id_image_display);
            iv.setImageURI(uri);
        }
    }
}
