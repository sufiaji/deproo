package com.deproo.android.deproo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.application.DeprooApplication;
import com.deproo.android.deproo.model.Broker;
import com.deproo.android.deproo.model.GetDataService;
import com.deproo.android.deproo.utils.Constants;
import com.deproo.android.deproo.utils.Utils;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {

    private static final int FROM_SCHEDULED = 1;
    private int mIteration = 0;
    private int mPeriod = 7;
    private int mTimeOut = 3;
    private int mCorePool = 7;
    private ScheduledFuture<?> mSchedule;
    private Context mContext;
    private FetchData mFetchData;
    private Handler mMyHandler;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        mProgress = (ProgressBar) findViewById(R.id.id_progress_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        runCounter();
        mFetchData = new FetchData();
        mFetchData.execute();
    }

    private void getDataDummy() {
        Intent userIntent = new Intent(this, GetDataService.class);
        userIntent.putExtra(Constants.SERVICECOMMAND, Constants.ServiceCommand.GETUSERDATA);
        Intent dummyIntent = new Intent(this, GetDataService.class);
        dummyIntent.putExtra(Constants.SERVICECOMMAND, Constants.ServiceCommand.DUMMYCOMMAND);
        startService(dummyIntent);
        startService(userIntent);
        startService(userIntent);
        startService(userIntent);
        startService(userIntent);
        startService(userIntent);
    }

    private void startHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getUserData() {
        ParseUser user = ParseUser.getCurrentUser();
        if(user!=null) {
            try {
                user.fetch();
            } catch (ParseException e) {
                //Utils.CommonToastDisplayerLong(this,"Failed to load User");
                //Toast.makeText(mContext, "Gagal mengambil data user", Toast.LENGTH_LONG).show();
            }
        }
    }

//    private void getCategory() {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.ParseTable.TableMasterCategoryAsset.NAME);
//        try {
//            List<ParseObject> cats = query.find();
//            List<String> list = new ArrayList<>();
//            for (ParseObject cat : cats) {
//                list.add(cat.getString(Constants.ParseTable.TableMasterCategoryAsset.CATEGORY));
//            }
//            ((DeprooApplication) getApplicationContext()).setCategoryList(list);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }


    private void runCounter() {
        ScheduledThreadPoolExecutor _executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(mCorePool);
        mMyHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch(inputMessage.what) {
                    case FROM_SCHEDULED:
                        int iteration = inputMessage.getData().getInt("iteration");
                        mProgress.setVisibility(View.VISIBLE);
//                        Utils.CommonToastDisplayerShort(mContext, "Perlu beberapa saat lagi...");
                        if(iteration >= mTimeOut) {
                            mProgress.setVisibility(View.INVISIBLE);
                            Utils.CommonAlertOkDisplayer(mContext,
                                    "Deproo", "Ups, sepertinya tidak ada koneksi internet.");
                            mFetchData.cancel(true);
                            mSchedule.cancel(true);
                        }
                        break;
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        };

        Runnable _runScheduled = new Runnable() {
            @Override
            public void run() {
                Message msg = mMyHandler.obtainMessage();
                msg.what = FROM_SCHEDULED;
                Bundle b = new Bundle();
                b.putInt("iteration", mIteration++);
                msg.setData(b);
                mMyHandler.sendMessage(msg);
            }
        };
        mSchedule = _executor.scheduleAtFixedRate(_runScheduled, mPeriod, mPeriod, TimeUnit.SECONDS);
    }

    private class FetchData extends AsyncTask<Void, Void, Void> {

        private void checkSharedPref() {
            SharedPreferences sh = getSharedPreferences(Constants.SharedPreference.PREFNAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sh.edit();
            editor.putBoolean(Constants.SharedPreference.INITSTATE, true);
            editor.commit();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Long time operations that must be run & completely finish in this Splash Activity
            // must be run from here. Home fragment only be available after completing operation.
//            getDataDummy();
            //getCategory();
            getUserData();
            DeprooApplication.initState = true;
            DeprooApplication.thisBroker = (Broker) ParseUser.getCurrentUser();
//            getDataDummy();
//            checkSharedPref();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mSchedule.cancel(true);
            startHome();
        }
    }
}
