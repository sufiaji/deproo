package com.deproo.android.deproo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.application.DeprooApplication;
import com.deproo.android.deproo.model.Asset;
import com.deproo.android.deproo.model.AssetImage;
import com.deproo.android.deproo.model.AsyncGetAssetThumb;
import com.deproo.android.deproo.model.Broker;
import com.deproo.android.deproo.model.UserReview;
import com.deproo.android.deproo.utils.Constants;
import com.deproo.android.deproo.utils.Utils;
import com.elyeproj.loaderviewlibrary.LoaderImageView;
import com.elyeproj.loaderviewlibrary.LoaderTextView;
import com.jcminarro.roundkornerlayout.RoundKornerLinearLayout;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BrokerProfileActivity extends AppCompatActivity {

    private Broker mBroker;
    private LoaderTextView mTextViewNumAsset;
    private ArrayList<Asset> mOtherAssets;
    private ArrayList<UserReview> mUserReview;
    private Context mContext;
    private LinearLayout mLayoutAsset;
    private LinearLayout mLayoutReview;
    private RelativeLayout mLayoutNoReview;
    private RelativeLayout mLayoutNoAsset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broker_public);

        mLayoutAsset = findViewById(R.id.id_layout_include_asset);
        mLayoutReview = findViewById(R.id.id_layout_include_review);
        mLayoutNoReview = findViewById(R.id.id_layout_no_review);
        mLayoutNoAsset = findViewById(R.id.id_layout_no_asset);
        mLayoutNoReview.setVisibility(View.GONE);
        mLayoutNoAsset.setVisibility(View.GONE);

        mOtherAssets = new ArrayList<>();
        mUserReview = new ArrayList<>();
        mContext = this;
        // get selected card broker
        mBroker = getIntent().getParcelableExtra(Constants.BROKER_OBJECT);
        if(DeprooApplication.sharedProfilePic !=null) {
            mBroker.setProfilePicThumb(DeprooApplication.sharedProfilePic);
            DeprooApplication.sharedProfilePic =null;
        }

        checkFavourite();

        initHeader();

        initReview();

        initListReview();

        initListAsset();

    }

    private void checkFavourite() {
        ImageView iv = findViewById(R.id.id_fav);
        List<String> subscribedChannels = ParseInstallation.getCurrentInstallation().getList("channels");
        if(subscribedChannels!=null && subscribedChannels.size()>0) {
            if (subscribedChannels.contains("USER-" + mBroker.getObjectId())) {
                iv.setImageResource(R.drawable.ic_deproo_fav_on);
            } else {
                iv.setImageResource(R.drawable.ic_deproo_fav_off);
            }
        } else {
            iv.setImageResource(R.drawable.ic_deproo_fav_off);
        }
    }

    private void initHeader() {
        /*
        Update Header Part
        1. Diamond
        2. Profile pic
        3. Rating total
        4. Profile Name
        5. City Province
        6. Bio1
         */
        ImageView diamond = findViewById(R.id.id_broker_diamond);
        diamond.setImageResource(mBroker.getDiamondStatus());
        CircleImageView profilePic = findViewById(R.id.id_broker_picture);
        if(mBroker.getProfilePicThumb()!=null)
            profilePic.setImageBitmap(mBroker.getProfilePicThumb());
        else
            profilePic.setImageResource(R.drawable.broker_fake);
        TextView name = findViewById(R.id.id_broker_name);
        name.setText(mBroker.getLongname());
        RatingBar rating = findViewById(R.id.id_broker_rating);
        double ratings = mBroker.getRating();
        rating.setRating((float) ratings);
        TextView addr = findViewById(R.id.id_broker_location);
        addr.setText(mBroker.getCityProvince());
        TextView title = findViewById(R.id.id_broker_title);
        title.setText(mBroker.getTitle());
    }

    private void initReview() {
        /*
        Update Review Summary Part
        1. Review
        2. Asset
        3. Follower
         */

        mTextViewNumAsset = findViewById(R.id.id_broker_numasset);
        ParseQuery<Asset> query = ParseQuery.getQuery(Asset.class);
        query.whereEqualTo(Constants.ParseTable.TableAsset.USER, mBroker);
        query.findInBackground(new FindCallback<Asset>() {
            @Override
            public void done(List<Asset> objects, ParseException e) {
                if(e==null) {
                    mTextViewNumAsset.setText(Integer.toString(objects.size()));
                }
            }
        });
        TextView numFollower = findViewById(R.id.id_broker_numfollower);
        numFollower.setText(Integer.toString(mBroker.getNumFollower()));
        TextView numReview = findViewById(R.id.id_broker_numreview);
        numReview.setText(Integer.toString(mBroker.getNumReview()));
    }

    private void initListReview() {
        /*
         Update Review List
         Card review list, See All ...
         */
        displayReview();
    }

    private void initListAsset() {
        /*
         Update Asset
         Asset list, see all...
         */
        displayOtherAsset();
    }

    private void displayReview() {
        ParseQuery<UserReview> query = ParseQuery.getQuery(UserReview.class);
        query.whereEqualTo(Constants.ParseTable.TableUserReview.USER, mBroker);
        query.setLimit(5);
        query.include(Constants.ParseTable.TableUserReview.RATING_FROM);
        query.findInBackground(new FindCallback<UserReview>() {
            @Override
            public void done(List<UserReview> userReviews, ParseException e) {
                if(e==null && userReviews.size()>0) {
                    mUserReview.clear();
                    mUserReview.addAll(userReviews);
                    int size = mUserReview.size();
                    for(int i=0;i<5;i++) {
                        if(i>size-1) {
                            // hide the layout since no more review
                            String nameLy = Constants.HORIZONTAL_REVIEW_LIST_LAYOUT + Integer.toString(i+1);
                            int idLy = getResources().getIdentifier(nameLy,"id",mContext.getPackageName());
                            CardView ly = findViewById(idLy);
                            ly.setVisibility(View.GONE);
                        } else {
                            String name = Constants.HORIZONTAL_REVIEW_LIST_TEXTVIEW_NAME + Integer.toString(i + 1);
                            int idName = getResources().getIdentifier(name, "id", mContext.getPackageName());
                            LoaderTextView tvName = findViewById(idName);
                            tvName.setText(mUserReview.get(i).getBrokerWhoGiveRating().getLongname());

                            String review = Constants.HORIZONTAL_REVIEW_LIST_TEXTVIEW_REVIEW + Integer.toString(i + 1);
                            int idReview = getResources().getIdentifier(review, "id", mContext.getPackageName());
                            LoaderTextView tvReview = findViewById(idReview);
                            tvReview.setText(mUserReview.get(i).getReview());

                            String rating = Constants.HORIZONTAL_REVIEW_LIST_TEXTVIEW_RATING + Integer.toString(i+1);
                            int idRating = getResources().getIdentifier(rating, "id", mContext.getPackageName());
                            RatingBar ratingBar = findViewById(idRating);
                            ratingBar.setRating((float) mUserReview.get(i).getRating());
                        }
                    }
                } else {
                    // hide the layout totally
                    mLayoutReview.setVisibility(View.GONE);
                    mLayoutNoReview.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void displayOtherAsset() {
        ParseQuery<Asset> query = ParseQuery.getQuery(Asset.class);
        query.whereEqualTo(Constants.ParseTable.TableAsset.USER, mBroker);
        query.setLimit(5);
        query.include(Constants.ParseTable.TableAsset.USER);
        query.findInBackground(new FindCallback<Asset>() {
            @Override
            public void done(List<Asset> assets, ParseException e) {
                if(e==null && assets.size()>0) {

                    mOtherAssets.clear();
                    mOtherAssets.addAll(assets);
                    int size = assets.size();

                    for(int i=0;i<5;i++) {

                        if(i>size-1) {
                            // hide the layout since no more asset
                            String nameLy = Constants.HORIZONTAL_ASSET_LIST_LAYOUT + Integer.toString(i+1);
                            int idLy = getResources().getIdentifier(nameLy,"id",mContext.getPackageName());
                            LinearLayout ly = findViewById(idLy);
                            ly.setVisibility(View.GONE);
                        } else {
                            // assign value of textview first
                            String name1 = Constants.HORIZONTAL_ASSET_LIST_TEXTVIEW + Integer.toString(i + 1) + "_1";
                            int id1 = getResources().getIdentifier(name1, "id", mContext.getPackageName());
                            LoaderTextView tv1 = findViewById(id1);

                            String name2 = Constants.HORIZONTAL_ASSET_LIST_TEXTVIEW + Integer.toString(i + 1) + "_2";
                            int id2 = getResources().getIdentifier(name2, "id", mContext.getPackageName());
                            LoaderTextView tv2 = findViewById(id2);

                            tv1.setText(Utils.formatIDR(assets.get(i).getPrice()));
                            tv2.setText(assets.get(i).getTitle());
                            // get image asset
                            ParseQuery<AssetImage> imageQuery1 = ParseQuery.getQuery(AssetImage.class);
                            imageQuery1.whereEqualTo(Constants.ParseTable.TableAssetImage.ASSET, assets.get(i));
                            final int index = i;
                            imageQuery1.getFirstInBackground(new GetCallback<AssetImage>() {
                                @Override
                                public void done(AssetImage assetImage, ParseException e) {
                                    String name3 = Constants.HORIZONTAL_ASSET_LIST_IMAGEVIEW + Integer.toString(index + 1);
                                    int id3 = getResources().getIdentifier(name3, "id", mContext.getPackageName());
                                    LoaderImageView loaderImageView = findViewById(id3);
                                    // download the image
                                    AsyncGetAssetThumb task = new AsyncGetAssetThumb(loaderImageView,mOtherAssets.get(index));
                                    task.execute(assetImage);
                                }
                            });
                            String nameLy = Constants.HORIZONTAL_ASSET_LIST_LAYOUT + Integer.toString(i+1);
                            int idLy = getResources().getIdentifier(nameLy,"id",mContext.getPackageName());
                            LinearLayout ly = findViewById(idLy);
                            final int indexx = i;
                            ly.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openAssetDetail(mOtherAssets.get(indexx));
                                }
                            });
                        }
                    }
                } else {
                    // no asset found at all, just hide the cardview of asset
                    mLayoutAsset.setVisibility(View.GONE);
                    mLayoutNoAsset.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void openAssetDetail(Asset asset) {
        Intent intent = new Intent(BrokerProfileActivity.this, AssetDetailActivity.class);
        intent.putExtra(Constants.ASSET_OBJECT, asset);
        intent.putExtra(Constants.URI, asset.getImageUri().toString());
        startActivity(intent);
    }

    public void onSeeAllAssetClick(View view) {
        Intent intent = new Intent(BrokerProfileActivity.this, AssetListActivity.class);
        intent.putExtra(Constants.BROKER_OBJECT, mBroker);
        startActivity(intent);
    }

    public void onSeeAllReviewClick(View view) {

    }

    public void onFavClick(View view) {
        ImageView iv = findViewById(R.id.id_fav);
        if(iv.getDrawable().getConstantState()==getResources().getDrawable(R.drawable.ic_deproo_fav_off).getConstantState()) {
            iv.setImageResource(R.drawable.ic_deproo_fav_on);
            ParsePush.subscribeInBackground("USER-" + mBroker.getObjectId());
        } else {
            iv.setImageResource(R.drawable.ic_deproo_fav_off);
            ParsePush.unsubscribeInBackground("USER-" + mBroker.getObjectId());
        }
    }

    public void onShareClick(View view) {

    }

    public void onChatClick(View view) {

    }

    public void onInboxClick(View view) {

    }

    public void onWhatsappClick(View view) {

    }

    public void onCallClick(View view) {

    }

    public void onGiveReviewClick(View view) {
        Intent intent = new Intent(BrokerProfileActivity.this, AddReviewActivity.class);
        intent.putExtra(Constants.BROKER_OBJECT, mBroker);
        DeprooApplication.sharedProfilePic = mBroker.getProfilePicThumb();
        startActivity(intent);
    }

    public void onCustomBackClick(View view) {
        onBackPressed();
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home: {
//                // todo: goto back activity from here
//                onBackPressed();
//                return true; }
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}
