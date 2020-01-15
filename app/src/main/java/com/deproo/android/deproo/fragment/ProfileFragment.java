package com.deproo.android.deproo.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.activity.MainActivity;
import com.deproo.android.deproo.utils.Constants;
import com.deproo.android.deproo.utils.Utils;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import de.hdodenhof.circleimageview.CircleImageView;
//import com.pkmmte.view.CircularImageView;
//import com.pkmmte.view.CircularImageView;

public class ProfileFragment extends Fragment {

//    private CircularImageView mProfileImage;
    private ImageView mBgImage;
    private CircleImageView mProfileImage;
    private ProgressBar mProgressBar;
    private TextView mTextUsername;
    private RatingBar mRatingBar;

    public ProfileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mTextUsername = v.findViewById(R.id.id_textview_username);
        mRatingBar = v.findViewById(R.id.id_ratingbar_profile);
        mProfileImage = v.findViewById(R.id.id_imageview_profile_pic);
        mBgImage = v.findViewById(R.id.id_imageview_profile_bg);
        mProgressBar = v.findViewById(R.id.id_progressbar_profile);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // update necessary views
        mTextUsername.setText(ParseUser.getCurrentUser().getString(Constants.ParseTable.TableUser.LONGNAME));
        double rating = ParseUser.getCurrentUser().getDouble(Constants.ParseTable.TableUser.RATING);
        mRatingBar.setRating((float)rating);

        ParseUser.getCurrentUser().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                mProgressBar.setVisibility(View.GONE);
                if(e==null) {
                    displayImageProfile();
                } else {
                    Utils.CommonToastDisplayerLong(getActivity(), e.getMessage().toString());
                }
            }
        });
    }

    private void displayImageProfile() {
        ParseUser user = ParseUser.getCurrentUser();
        try {
            ParseFile parseFile = ParseUser.getCurrentUser().getParseFile(Constants.ParseTable.TableUser.PROFILE_THUMB);
            byte[] data = parseFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            mProfileImage.setImageBitmap(bitmap);
//            parseFile = ParseUser.getCurrentUser().getParseFile(Constants.ParseTable.TableUser.PROFILE_BG);
//            data = parseFile.getData();
//            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            mBgImage.setImageBitmap(bitmap);
        } catch(Exception e) {
            //Utils.CommonToastDisplayerLong(getActivity(), e.getMessage().toString());
        }
    }


}
