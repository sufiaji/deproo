package com.deproo.android.deproo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.deproo.android.deproo.model.Broker;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.deproo.android.deproo.R;
import com.deproo.android.deproo.model.Asset;
import com.deproo.android.deproo.model.AssetImage;
import com.deproo.android.deproo.model.AsyncGetAssetThumb;
import com.deproo.android.deproo.model.GetProfileThumbnail;
import com.deproo.android.deproo.utils.Constants;
import com.deproo.android.deproo.utils.Utils;
import com.elyeproj.loaderviewlibrary.LoaderImageView;
import com.elyeproj.loaderviewlibrary.LoaderTextView;
import com.libRG.CustomTextView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class AssetDetailActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private SliderLayout mImageSlider;
    private Context mContext;
    private Asset mAsset;
    private ArrayList<Asset> mOtherAssets;
    private LoaderTextView mTextViewNumAsset;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_detail);
        mContext = this;

        mOtherAssets = new ArrayList<>();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_asset));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_asset);
        collapsingToolbarLayout.setTitle("");

        mAsset = getIntent().getParcelableExtra(Constants.ASSET_OBJECT);

        // ****** check favourite
        checkFavourite();

         // ******* incerement number of viewer *******
        //>> get the updated data from Server first
        mAsset.fetchInBackground(new GetCallback<Asset>() {
            @Override
            public void done(Asset object, ParseException e) {
                if(e==null) {
                    int numViewer = object.getNumViewer();
                    numViewer = numViewer + 1;
                    LoaderTextView tvNumView = findViewById(R.id.id_num_viewer);
                    tvNumView.setText(Integer.toString(numViewer));
                    mAsset.setNumViewer(numViewer);
                    mAsset.saveEventually();
                }
            }
        });

        // ******* get Number of Asset *******
        mTextViewNumAsset = findViewById(R.id.id_brokernumasset);
        ParseQuery<Asset> query = ParseQuery.getQuery(Asset.class);
        query.whereEqualTo(Constants.ParseTable.TableAsset.USER, mAsset.getOwner());
        query.findInBackground(new FindCallback<Asset>() {
            @Override
            public void done(List<Asset> objects, ParseException e) {
                if(e==null) {
                    mTextViewNumAsset.setText(Integer.toString(objects.size()));
                }
            }
        });

        getImageAsset(mAsset);

        Uri uri = Uri.parse(getIntent().getStringExtra(Constants.URI));
        mImageSlider = findViewById(R.id.id_slider_asset);
        TextSliderView textSliderView = new TextSliderView(this);
        textSliderView
                .image(uri.toString())
                .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                    @Override
                    public void onSliderClick(BaseSliderView slider) {
                        Toast.makeText(mContext, "toast", Toast.LENGTH_LONG).show();
                    }
                });
        mImageSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "onClick", Toast.LENGTH_SHORT).show();
            }
        });
        mImageSlider.addSlider(textSliderView);
        mImageSlider.setPresetTransformer(SliderLayout.Transformer.Stack);
        mImageSlider.setCustomAnimation(new DescriptionAnimation());
        mImageSlider.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Toast.makeText(mContext, "onPageScrolled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageSelected(int position) {
//                Toast.makeText(mContext, "onPageSelected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Toast.makeText(mContext, "onPageScrollStateChanged", Toast.LENGTH_SHORT).show();
            }
        });

        // The Broker
        TextView tvBroker = findViewById(R.id.id_call_broker);
        tvBroker.setText(mAsset.getOwner().getString(Constants.ParseTable.TableUser.LONGNAME));

        // CARD 1
//        TextView tvNumView = findViewById(R.id.id_num_viewer);
//        tvNumView.setText(Integer.toString(mAsset.getNumViewer()));
        TextView tvPrice = (TextView) findViewById(R.id.id_card1_price);
        tvPrice.setText(mAsset.getPriceFormatted());
        TextView tvTitle = (TextView) findViewById(R.id.id_card1_title);
        tvTitle.setText(mAsset.getTitle());
        TextView tvCityProv = (TextView) findViewById(R.id.id_card1_cityprovince);
        tvCityProv.setText(mAsset.getConcatAddress());
        TextView tvAddress = (TextView) findViewById(R.id.id_card1_address);
        tvAddress.setText(mAsset.getAddress());
        TextView tvCategory = (TextView) findViewById(R.id.id_card1_category);
        tvCategory.setText(mAsset.getCategory().toUpperCase());
        TextView tvPserve = (TextView) findViewById(R.id.id_card1_pserve);
        tvPserve.setText(mAsset.getTypePServe().toUpperCase());
        TextView tvFurnish = (TextView) findViewById(R.id.id_card1_furnish);
        if(mAsset.getCategory().equalsIgnoreCase("apartemen")) {
            tvFurnish.setVisibility(View.VISIBLE);
            tvFurnish.setText(mAsset.getTypeApart().toUpperCase());
        } else {
            tvFurnish.setVisibility(View.GONE);
        }
        TextView tvMonthYear = (TextView) findViewById(R.id.id_card1_monthyear);
        if(mAsset.getTypePServe().equalsIgnoreCase("sewa")) {
            tvMonthYear.setVisibility(View.VISIBLE);
            tvMonthYear.setText("/" + Integer.toString(mAsset.getNumPServe())
                    + " " + mAsset.getTimePServe());
        } else {
            tvMonthYear.setVisibility(View.GONE);
        }
        TextView tvUpdate = findViewById(R.id.id_card1_last_update);
        tvUpdate.setText("Terakhir diupdate: " + Utils.getDateFormattedShort(mAsset.getUpdatedAt()));

        // CARD 2
        CardView cvProperty = (CardView) findViewById(R.id.id_asset_detail_card2_p);
        CardView cvLand = (CardView) findViewById(R.id.id_asset_detail_card2_l);
        CardView cvOffice = (CardView) findViewById(R.id.id_asset_detail_card2_o);
        TextView tvNumbed = (TextView) findViewById(R.id.id_card2_numbed);
        TextView tvNumbath = (TextView) findViewById(R.id.id_card2_numbath);
        TextView tvNumgarage = (TextView) findViewById(R.id.id_card2_numgarage);
        TextView tvFloor = (TextView) findViewById(R.id.id_card2_numfloor);
        TextView tvHarea = (TextView) findViewById(R.id.id_card2_harea);
        TextView tvLarea = (TextView) findViewById(R.id.id_card2_larea);
        if(mAsset.getGlobalCategory().equalsIgnoreCase("property")) {
            cvProperty.setVisibility(View.VISIBLE);
            cvOffice.setVisibility(View.GONE);
            cvLand.setVisibility(View.GONE);
            tvNumbed.setText(mAsset.getNumBedroomFormatted());
            tvNumbath.setText(mAsset.getNumBathroomFormatted());
            tvNumgarage.setText(mAsset.getNumGarageFormatted());
            tvFloor.setText(mAsset.getNumFloorFormatted());
            tvLarea.setText(Html.fromHtml(String.format(getResources().getString(R.string.m2), mAsset.getSizelandFormatted())));
            if (mAsset.getCategory().equalsIgnoreCase("apartemen")) {
                tvLarea.setVisibility(View.GONE);
            } else {
                tvLarea.setVisibility(View.VISIBLE);
            }
            tvHarea.setText(Html.fromHtml(String.format(getResources().getString(R.string.m2), mAsset.getSizehouseFormatted())));
        } else if(mAsset.getGlobalCategory().equalsIgnoreCase("tanah")) {
            cvProperty.setVisibility(View.GONE);
            cvOffice.setVisibility(View.GONE);
            cvLand.setVisibility(View.VISIBLE);
            tvLarea.setText(Html.fromHtml(String.format(getResources().getString(R.string.m2), mAsset.getSizelandFormatted())));
        } else if(mAsset.getGlobalCategory().equalsIgnoreCase("kantor")) {
            cvProperty.setVisibility(View.GONE);
            cvOffice.setVisibility(View.VISIBLE);
            cvLand.setVisibility(View.GONE);
            tvNumgarage.setText(mAsset.getNumGarageFormatted());
            tvFloor.setText(mAsset.getNumFloorFormatted());
            tvHarea.setText(Html.fromHtml(String.format(getResources().getString(R.string.m2), mAsset.getSizehouseFormatted())));
        }

        //card description
        TextView tvDescription = findViewById(R.id.id_description);
        tvDescription.setText(mAsset.getDescription());

        // card map
        LoaderImageView loaderMap = findViewById(R.id.id_map);
        AsyncMapStatic asyncMapStatic = new AsyncMapStatic(loaderMap);
        asyncMapStatic.execute(mAsset.getLocation());

        // card discussion
//        if(mAsset.getNumDiscussion()>0) {
//            TextView tvDiscuss = findViewById(R.id.id_header_diskusi);
//            tvDiscuss.setText("Diskusi (" + Integer.toString(mAsset.getNumDiscussion()) + ")");
//        } else {
//            CardView cDiscuss = findViewById(R.id.id_asset_detail_discussion);
//            cDiscuss.setVisibility(View.GONE);
//        }

        // card owner
        CircleImageView profileImage = findViewById(R.id.id_brokerpic);
        GetProfileThumbnail taskProfile = new GetProfileThumbnail(profileImage);
        taskProfile.execute(mAsset.getOwner());
        TextView tvBrokerName = findViewById(R.id.id_brokername);
        tvBrokerName.setText(mAsset.getOwner().getString(Constants.ParseTable.TableUser.LONGNAME));
        RatingBar brokerRating = findViewById(R.id.id_brokerrating);
        brokerRating.setRating((float)mAsset.getOwner().getDouble(Constants.ParseTable.TableUser.RATING));
//        TextView mTextViewNumAsset = findViewById(R.id.id_brokernumasset);
//        mTextViewNumAsset.setText(Integer.toString(mAsset
//                .getOwner().getInt(Constants.ParseTable.TableUser.NUMBER_OF_ASSET))+" Asset");
        TextView tvJoinSince = findViewById(R.id.id_brokerjoinsince);
        tvJoinSince.setText("Bergabung sejak "+Utils.getDateFormattedShort(mAsset.getOwner().getCreatedAt()));
        TextView tvAssetWord = findViewById(R.id.id_title_asset);
        tvAssetWord.setVisibility(View.INVISIBLE);

        // card facilities
        CustomTextView tvPool = findViewById(R.id.id_dekat_swimpool);
        CustomTextView tvPray = findViewById(R.id.id_dekat_ibadah);
        CustomTextView tvToll = findViewById(R.id.id_dekat_toll);
        CustomTextView tvHway = findViewById(R.id.id_dekat_jalanraya);
        CustomTextView tvPerum = findViewById(R.id.id_dekat_perumahan);
        CustomTextView tvTour = findViewById(R.id.id_dekat_wisata);
        CustomTextView tvPasar = findViewById(R.id.id_dekat_pasar);
        CustomTextView tvHealth = findViewById(R.id.id_dekat_kesehatan);
        CustomTextView tvSchool = findViewById(R.id.id_dekat_sekolah);
        if(!mAsset.getNearCluster()) {
            tvPerum.setVisibility(View.GONE);
        }
        if(!mAsset.getNearHealth()) {
            tvHealth.setVisibility(View.GONE);
        }
        if(!mAsset.getNearHighway()) {
            tvHway.setVisibility(View.GONE);
        }
        if(!mAsset.getNearMarket()) {
            tvPasar.setVisibility(View.GONE);
        }
        if(!mAsset.getNearToll()) {
            tvToll.setVisibility(View.GONE);
        }
        if(!mAsset.getNearTour()) {
            tvTour.setVisibility(View.GONE);
        }
        if(!mAsset.getNearWorship()) {
            tvPray.setVisibility(View.GONE);
        }
        if(!mAsset.getSwimPool()) {
            tvPool.setVisibility(View.GONE);
        }
        if(!mAsset.getNearSchool()) {
            tvSchool.setVisibility(View.GONE);
        }

        //card list of other asset
        displayOtherAsset(mAsset.getOwner());

    }

    public void onTextCallClick(View v) {
        Toast.makeText(mContext, "onTextViewClick", Toast.LENGTH_SHORT).show();
    }

    private void checkFavourite() {
        ImageView iv = findViewById(R.id.id_fav);
        List<String> subscribedChannels = ParseInstallation.getCurrentInstallation().getList("channels");
        if(subscribedChannels.contains("ASSET-" + mAsset.getObjectId())) {
            iv.setImageResource(R.drawable.ic_deproo_fav_on);
        } else {
            iv.setImageResource(R.drawable.ic_deproo_fav_off);
        }
    }

    public void onChatClick(View view) {

    }

    public void onFollowUserClick(View view) {
        ParseUser broker = mAsset.getOwner();
        broker.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null) {

                }
            }
        });
    }

    public void onFavClick(View view) {
        ImageView iv = findViewById(R.id.id_fav);
        if(iv.getDrawable().getConstantState()==getResources().getDrawable(R.drawable.ic_deproo_fav_off).getConstantState()) {
            iv.setImageResource(R.drawable.ic_deproo_fav_on);
            ParsePush.subscribeInBackground("ASSET-" + mAsset.getObjectId());
        } else {
            iv.setImageResource(R.drawable.ic_deproo_fav_off);
            ParsePush.unsubscribeInBackground("ASSET-" + mAsset.getObjectId());
        }
    }

    public void onShareClick(View view) {
        Toast.makeText(mContext,"onShareClick", Toast.LENGTH_SHORT).show();
    }

    public void onDiscussionClick(View view) {
        Intent intent = new Intent(AssetDetailActivity.this, NewDiscussionActivity.class);
        intent.putExtra(Constants.ASSET_OBJECT, mAsset);
        startActivity(intent);
    }

    public void onBrokerClick(View view) {
        if(!mAsset.getOwner().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
            Intent intent = new Intent(AssetDetailActivity.this, BrokerProfileActivity.class);
            intent.putExtra(Constants.BROKER_OBJECT, mAsset.getOwner());
            startActivity(intent);
        }
    }

    public void onSeeAllAssetClick(View view) {
        Intent intent = new Intent(AssetDetailActivity.this, AssetListActivity.class);
        intent.putExtra(Constants.BROKER_OBJECT, mAsset.getOwner());
        startActivity(intent);
    }

    private void displayOtherAsset(ParseUser broker) {
        ParseQuery<Asset> query = ParseQuery.getQuery(Asset.class);
        query.whereEqualTo(Constants.ParseTable.TableAsset.USER, broker);
        query.whereNotEqualTo("objectId", mAsset.getObjectId());
        query.include(Constants.ParseTable.TableAsset.USER);
        query.findInBackground(new FindCallback<Asset>() {
            @Override
            public void done(List<Asset> assets, ParseException e) {
                if(e==null) {
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
                    // no asset found at all, just hide the cardview of other asset
                    LinearLayout ly = findViewById(R.id.id_other_asset_list);
                    ly.setVisibility(View.GONE);
                }
            }
        });
    }

    private void openAssetDetail(Asset asset) {
        Intent intent = new Intent(AssetDetailActivity.this, AssetDetailActivity.class);
        intent.putExtra(Constants.ASSET_OBJECT, asset);
        intent.putExtra(Constants.URI, asset.getImageUri().toString());
        startActivity(intent);
    }

    private void getImageAsset(Asset asset) {
        ParseQuery<AssetImage> query =  ParseQuery.getQuery(AssetImage.class);
        query.whereEqualTo(Constants.ParseTable.TableAssetImage.ASSET, asset);
        query.findInBackground(new FindCallback<AssetImage>() {
            @Override
            public void done(List<AssetImage> objects, ParseException e) {
                if(e==null) {
                    AsyncAssetImage asyncTask = new AsyncAssetImage(mImageSlider);
                    asyncTask.execute(objects);
                }
            }
        });
    }

    public void onMapClick(View view) {
        Intent intent = new Intent(AssetDetailActivity.this, AssetLocActivity.class);
        intent.putExtra(NewAssetActivity.MAPCALLEDFROM, AssetDetailActivity.class.getSimpleName());
        intent.putExtra(NewAssetActivity.LATITUDE, mAsset.getLocation().getLatitude());
        intent.putExtra(NewAssetActivity.LONGITUDE, mAsset.getLocation().getLongitude());
        startActivity(intent);
    }

    private class AsyncAssetImage extends AsyncTask<List<AssetImage>, Integer, List<String>> {

        private final WeakReference<SliderLayout> mWeakSliderLayout;

        public AsyncAssetImage(SliderLayout sliderLayout) {
            mWeakSliderLayout = new WeakReference<SliderLayout>(sliderLayout);
        }

        @Override
        protected List<String> doInBackground(List<AssetImage>... assetImages) {
            ArrayList<String> uriString = new ArrayList<>();
            for(int i=0;i<assetImages[0].size();i++) {
                ParseFile imgFile = (ParseFile) assetImages[0].get(i)
                        .get(Constants.ParseTable.TableAssetImage.IMAGE_FILE);
                String imgAddress = imgFile.getUrl();
                uriString.add(imgAddress);
            }
            return uriString;
        }

        @Override
        protected void onPostExecute(final List<String> uriString) {
            if(uriString!=null && uriString.size()>0) {
                final SliderLayout sliderLayout = mWeakSliderLayout.get();
                if(sliderLayout != null) {
                    sliderLayout.removeAllSliders();
                    for(int i=0;i<uriString.size();i++) {
                        final int j = i;
                        TextSliderView textSliderView = new TextSliderView(mContext);
                        textSliderView
                                .image(uriString.get(i))
                                .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
//                                        Toast.makeText(getApplicationContext(), "toastasync", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AssetDetailActivity.this, DisplayImageActivity.class);
                                        intent.putExtra(Constants.URI, uriString.get(j));
                                        getApplicationContext().startActivity(intent);
                                    }
                                });
                        sliderLayout.addSlider(textSliderView);
                    }
//                    sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
//                    mImageSlider.setPresetTransformer(SliderLayout.Transformer.Stack);
//                    mImageSlider.setCustomAnimation(new DescriptionAnimation());
//                    sliderLayout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
//                        @Override
//                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                        }
//
//                        @Override
//                        public void onPageSelected(int position) {
//
//                        }
//
//                        @Override
//                        public void onPageScrollStateChanged(int state) {
//
//                        }
//                    });
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                // todo: goto back activity from here
                onBackPressed();
                return true; }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class AsyncMapStatic extends AsyncTask<ParseGeoPoint, Integer, Bitmap> {

        private final WeakReference<LoaderImageView> loaderImageReference;


        public AsyncMapStatic(LoaderImageView loaderView) {
            loaderImageReference = new WeakReference<LoaderImageView>(loaderView);
        }

        @Override
        protected Bitmap doInBackground(ParseGeoPoint... parseGeoPoints) {
            Bitmap bmp = null;
            ParseGeoPoint parseGeoPoint = parseGeoPoints[0];
            if(parseGeoPoint!=null) {
                try {
                    Double lat = parseGeoPoint.getLatitude();
                    Double lon = parseGeoPoint.getLongitude();
                    String url = "https://dev.virtualearth.net/REST/V1/Imagery/Map/Road/"+ lat +
                            "%2C" + lon + "/16?mapSize=600,300&format=jpeg&pushpin=" + lat + "," +
                            lon + ";66;A&key=" + getResources().getString(R.string.bing_token);
                    InputStream in = (InputStream) new URL(url).getContent();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(loaderImageReference != null && bitmap != null) {
                final LoaderImageView loaderView = loaderImageReference.get();
                if(loaderView != null) {
                    loaderView.setImageBitmap(bitmap);
                }
            } else if(bitmap==null) {
                final LoaderImageView loaderView = loaderImageReference.get();
                if(loaderView != null) {
                    loaderView.setImageResource(R.drawable.nomap);
                }
            }
        }
    }

}
