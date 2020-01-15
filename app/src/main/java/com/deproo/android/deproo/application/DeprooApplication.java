package com.deproo.android.deproo.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.model.Asset;
import com.deproo.android.deproo.model.AssetImage;
import com.deproo.android.deproo.model.AssetVideo;
import com.deproo.android.deproo.model.Broker;
import com.deproo.android.deproo.model.UserReview;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;

public class DeprooApplication extends Application {

    public static Bitmap sharedProfilePic = null;
    public static boolean initState = false;
    public static ArrayList<Broker> sharedBrokerList;
    public static ArrayList<Asset> sharedAssetList;
    public static Broker thisBroker = null;

    private List<String> mCategoryList = new ArrayList<>();
    private List<String> mApartCategoryList = new ArrayList<>();
    private List<String> mPServeType = new ArrayList<>();
    private List<String> mPServeTime = new ArrayList<>();
    private List<String> mPServeNum = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Asset.class);
        ParseObject.registerSubclass(AssetImage.class);
        ParseObject.registerSubclass(AssetVideo.class);
        ParseObject.registerSubclass(UserReview.class);
        ParseUser.registerSubclass(Broker.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                            .applicationId(getString(R.string.back4app_app_id))
                            .clientKey(getString(R.string.back4app_client_key))
                            .server(getString(R.string.back4app_server_url))
                            .build());
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        ArrayList<String> channels = new ArrayList<>();
        channels.add("News");
        installation.put("channels", channels);
        installation.put("GCMSenderId", "69524391139");
        installation.saveInBackground();
        setCategoryList();
        setApartCategoryList();
        setPServeNumList();
        setPServeTypeList();
        setPServeTimeList();
        initBrokerList();
        initAssetList();
    }

    private void setToken(final String objectId) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()) {

                } else {
                    String token = task.getResult().getToken();
                    HashMap<String, Object> params = new HashMap<>(3);
                    params.put("installationId", objectId);
                    params.put("deviceToken", token);
                    params.put("pushType", "gcm");
                    ParseCloud.callFunctionInBackground("setDeviceToken", params, new FunctionCallback<Boolean>() {
                        @Override
                        public void done(java.lang.Boolean success, ParseException e) {
                            if (e == null) {
                                ParsePush.subscribeInBackground("News");
                            } else {

                            }
                        }
                    });
                }
            }
        });
    }

    private void initBrokerList() {
        sharedBrokerList = new ArrayList<>();
    }

    private void initAssetList() {
        sharedAssetList = new ArrayList<>();
    }

    private void setPServeNumList() {
        for(int i=1;i<=12;i++) {
            mPServeNum.add(Integer.toString(i));
        }
    }

    private void setPServeTimeList() {
        mPServeTime.add("Hari");
        mPServeTime.add("Bulan");
        mPServeTime.add("Tahun");
    }

    private void setPServeTypeList() {
        mPServeType.add("Jual");
        mPServeType.add("Sewa");
    }

    private void setApartCategoryList() {
        mApartCategoryList.add("Unfurnished");
        mApartCategoryList.add("Partly Furnished");
        mApartCategoryList.add("Fully Furnished");
    }

    private void setCategoryList() {
        mCategoryList.add("Rumah");
        mCategoryList.add("Apartemen");
        mCategoryList.add("Townhouse");
        mCategoryList.add("Kantor");
        mCategoryList.add("Tanah");
    }

    public List<String> getCategoryList() {
        return mCategoryList;
    }

    public List<String> getApartCategoryList() {
        return mApartCategoryList;
    }

    public List<String> getPServeTypeList() {
        return mPServeType;
    }

    public List<String> getPServeTimeList() {
        return mPServeTime;
    }

    public List<String> getPServeNumList() {
        return mPServeNum;
    }

}
