package com.deproo.android.deproo.activity;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import android.widget.VideoView;

import com.deproo.android.deproo.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class VideoPlayerActivity extends AppCompatActivity {

    VideoView mVidView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplyr);
        mVidView = (VideoView)findViewById(R.id.id_video_player);


        ParseQuery<ParseObject> query = ParseQuery.getQuery("AssetVideo");
        query.getInBackground("DWsPEGp7mx", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null) {

                    ParseFile vidFile = (ParseFile) object.get("video");
                    String vidAddress = vidFile.getUrl();
                    Uri vidUri = Uri.parse(vidAddress);
                    mVidView.setVideoURI(vidUri);
                    mVidView.start();
                    Toast.makeText(getApplicationContext(),"Starting video...",Toast.LENGTH_SHORT).show();
                    /*vidFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            vidFile.getUrl()
                        }
                    })*/
                } else {
                    Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*String vidAddress = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";
        Uri vidUri = Uri.parse(vidAddress);

        mVidView.setVideoURI(vidUri);
        mVidView.start();*/
    }
}
