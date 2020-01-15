package com.deproo.android.deproo.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.model.Asset;
import com.deproo.android.deproo.model.AssetAdapter;
import com.deproo.android.deproo.model.AssetImage;
import com.deproo.android.deproo.model.Broker;
import com.deproo.android.deproo.model.EndlessRecyclerViewScrollListener;
import com.deproo.android.deproo.utils.Constants;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class AssetListActivity extends AppCompatActivity {

    private Context mContext;
    private ProgressBar mPb;
    private ArrayList<Asset> mData;
    private AssetAdapter mAdapter;
    private int mSkip = 0;
    private Broker mOwner;
    private String mPServeCat;
    private String mCat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_asset_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_asset_list);
        mOwner = getIntent().getParcelableExtra(Constants.BROKER_OBJECT);
        mPServeCat = getIntent().getStringExtra(Constants.FILTER_PSERVECAT);
        mCat = getIntent().getStringExtra(Constants.FILTER_CAT);
        if(mOwner!=null)
            getSupportActionBar().setTitle("Asset: " + mOwner.getLongname());
        else if(mCat!=null && !mCat.isEmpty() && mPServeCat!=null && !mPServeCat.isEmpty()) {
            if(mPServeCat.equalsIgnoreCase("jual"))
                getSupportActionBar().setTitle(mCat + " Dijual");
            else
                getSupportActionBar().setTitle(mCat + " Disewakan");
        } else if(mCat!=null && !mCat.isEmpty())
            getSupportActionBar().setTitle(mCat);

        mPb = findViewById(R.id.id_progress_asset_list);
        mData = new ArrayList<>();
        mAdapter = new AssetAdapter(mContext, mData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext,
                RecyclerView.VERTICAL, false);
        RecyclerView recyclerView = findViewById(R.id.id_recycler_asset);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(mOwner !=null) {
                    getAsset(mOwner);
                } else if(mCat!=null && !mCat.isEmpty() && mPServeCat!=null && !mPServeCat.isEmpty()) {
                    getAsset(mCat, mPServeCat);
                } else if(mCat!=null && !mCat.isEmpty()) {
                    getAsset(mCat);
                } else {
                    getAsset();
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        if(mOwner !=null) {
            getAsset(mOwner);
        } else if(mCat!=null && !mCat.isEmpty() && mPServeCat!=null && !mPServeCat.isEmpty()) {
            getAsset(mCat, mPServeCat);
        } else if(mCat!=null && !mCat.isEmpty()) {
            getAsset(mCat);
        } else {
            getAsset();
        }
    }

    private void getAsset(ParseUser user) {
        ParseQuery<Asset> query = ParseQuery.getQuery(Asset.class);
        query.addDescendingOrder(Constants.ParseTable.TableAsset.CREATED_AT);
        query.whereEqualTo(Constants.ParseTable.TableAsset.USER, user);
        query.include(Constants.ParseTable.TableAsset.USER);
        query.setLimit(5);
        query.setSkip(mSkip);
        query.findInBackground(new FindCallback<Asset>() {
            @Override
            public void done(List<Asset> assets, ParseException e) {
                mPb.setVisibility(View.GONE);
                if(e==null && assets.size()>0) {
                    int curSize = mAdapter.getItemCount();
                    mData.addAll(assets);
                    mAdapter.notifyItemRangeInserted(curSize, assets.size());
                    mSkip = mSkip + 5;
                    getAssetImages(assets);
                }
            }
        });
    }

    private void getAsset() {
        ParseQuery<Asset> query = ParseQuery.getQuery(Asset.class);
        query.addDescendingOrder(Constants.ParseTable.TableAsset.CREATED_AT);
        query.include(Constants.ParseTable.TableAsset.USER);
        query.setLimit(5);
        query.setSkip(mSkip);
        query.findInBackground(new FindCallback<Asset>() {
            @Override
            public void done(List<Asset> assets, ParseException e) {
                mPb.setVisibility(View.GONE);
                if(e==null && assets.size()>0) {
                    int curSize = mAdapter.getItemCount();
                    mData.addAll(assets);
                    mAdapter.notifyItemRangeInserted(curSize, assets.size());
                    mSkip = mSkip + 5;
                    getAssetImages(assets);
                }
            }
        });
    }

    private void getAsset(String cat, String pserve) {
        ParseQuery<Asset> query = ParseQuery.getQuery(Asset.class);
        query.addDescendingOrder(Constants.ParseTable.TableAsset.CREATED_AT);
        query.whereEqualTo(Constants.ParseTable.TableAsset.CATEGORY, cat);
        query.whereEqualTo(Constants.ParseTable.TableAsset.CATEGORY_OF_PSERVE, pserve);
        query.include(Constants.ParseTable.TableAsset.USER);
        query.setLimit(5);
        query.setSkip(mSkip);
        query.findInBackground(new FindCallback<Asset>() {
            @Override
            public void done(List<Asset> assets, ParseException e) {
                mPb.setVisibility(View.GONE);
                if(e==null && assets.size()>0) {
                    int curSize = mAdapter.getItemCount();
                    mData.addAll(assets);
                    mAdapter.notifyItemRangeInserted(curSize, assets.size());
                    mSkip = mSkip + 5;
                    getAssetImages(assets);
                } else {
                    Toasty.info(getApplicationContext(),"Asset tidak ditemukan");
                }
            }
        });
    }

    private void getAsset(String cat) {
        ParseQuery<Asset> query = ParseQuery.getQuery(Asset.class);
        query.addDescendingOrder(Constants.ParseTable.TableAsset.CREATED_AT);
        query.whereEqualTo(Constants.ParseTable.TableAsset.CATEGORY, cat);
        query.include(Constants.ParseTable.TableAsset.USER);
        query.setLimit(5);
        query.setSkip(mSkip);
        query.findInBackground(new FindCallback<Asset>() {
            @Override
            public void done(List<Asset> assets, ParseException e) {
                mPb.setVisibility(View.GONE);
                if(e==null && assets.size()>0) {
                    int curSize = mAdapter.getItemCount();
                    mData.addAll(assets);
                    mAdapter.notifyItemRangeInserted(curSize, assets.size());
                    mSkip = mSkip + 5;
                    getAssetImages(assets);
                } else {
                    Toasty.info(getApplicationContext(),"Asset tidak ditemukan");
                }
            }
        });
    }

    private void getAssetImages(List<Asset> assets) {
        for(int i=0;i<assets.size();i++) {
            Asset asset = assets.get(i);
            final String objectID = asset.getObjectId();
            ParseQuery<AssetImage> imageQuery = ParseQuery.getQuery(AssetImage.class);
            imageQuery.whereEqualTo(Constants.ParseTable.TableAssetImage.ASSET, asset);
            imageQuery.getFirstInBackground(new GetCallback<AssetImage>() {
                @Override
                public void done(AssetImage assetImage, ParseException e) {
                    if(e==null && assetImage != null) {
                        AsyncAssetImage task = new AsyncAssetImage(objectID);
                        task.execute(assetImage);
                    }
                }
            });
        }
    }

    private class AsyncAssetImage extends AsyncTask<AssetImage, Integer, Bitmap> {

        private Uri mImageUri;
        private String mObjectID;

        public AsyncAssetImage(String objectId) {
            mObjectID = objectId;
        }

        @Override
        protected Bitmap doInBackground(AssetImage... parseObjects) {
            ParseObject object = parseObjects[0];
            ParseFile parseFile = object.getParseFile(Constants.ParseTable.TableAssetImage.IMAGE_THUMB);
            Bitmap bitmap = null;
            try {
                byte[] data = parseFile.getData();
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                ParseFile imgFile = (ParseFile) object.get(Constants.ParseTable.TableAssetImage.IMAGE_THUMB);
                String imgAddress = imgFile.getUrl();
                mImageUri = Uri.parse(imgAddress);

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null) {
                for(int i=0;i<mAdapter.getItemCount();i++) {
                    if(mData.get(i).getObjectId().equals(mObjectID)) {
                        mData.get(i).setImage(bitmap);
                        mData.get(i).setImageUri(mImageUri);
                        mAdapter.notifyItemChanged(i);
                        break;
                    }
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
}
