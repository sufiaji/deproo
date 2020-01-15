package com.deproo.android.deproo.model;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.deproo.android.deproo.utils.Constants;
import com.deproo.android.deproo.utils.Utils;
import com.parse.ParseException;
import com.parse.ParseUser;

public class GetDataService extends Service {

    public static final String ACTION = "com.deproo.android.deproo.GetDataService";
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private IBinder mBinder;
    private boolean mAllowRebind;
    private Intent mIntent;
    private Context mContext;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        mIntent = intent;
        mContext = this;
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // do something,
            String command = mIntent.getStringExtra(Constants.SERVICECOMMAND);
            if (command.equals(Constants.ServiceCommand.GETUSERDATA)) {
                ParseUser user = ParseUser.getCurrentUser();
                if(user!=null) {
                    try {
                        user.fetch();
                    } catch (ParseException e) {
                        Utils.CommonToastDisplayerLong(getApplicationContext(),
                                "Failed to load User");
                    }
                }
                // then finally stop service
//                Utils.CommonToastDisplayerLong(getApplicationContext(),
//                        "Command get user data");
            } else if (command.equals(Constants.ServiceCommand.DUMMYCOMMAND)) {
                Intent broadcastIntent = new Intent(ACTION);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(broadcastIntent);
//                Utils.CommonToastDisplayerLong(getApplicationContext(),
//                        "Dummy Command");
            }
            stopSelf(msg.arg1);
        }
    }
}
