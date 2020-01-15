package com.deproo.android.deproo.model;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.deproo.android.deproo.utils.Utils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

public class AssetUploadService extends IntentService {

    public static final String ACTION = "com.deproo.android.deproo.AssetUploadService";
    public static final String SEQUENCE = "SEQUENCE";
    public static final String PATH = "PATH";
    public static final String TYPE = "TYPE"; //image or video
    public static final String ASSET_OBJECT = "ASSET_OBJECT";
    public static final String USER_OBJECT = "USER_OBJECT";

    public static final String TYPEIMAGE = "IMAGE";
    public static final String TYPEVIDEO = "VIDEO";
    public static final String TYPEMAPIMAGE = "MAPIMAGE";

    public static final String RESULTCODE = "RESULTCODE";
    public static final String RESULTVALUE = "RESULTVALUE";
    public static final String RESULTMSG = "RESULTMSG";

    public static int OK_CODE = 0;
    public static int ER_CODE = 9;

    private int mNotifIdDefaultImage = 100;
    private int mNotifIdDefaultVideo = 200;

    private int mNotifId;
    private int mSequence;
    private String mPath;
    private String mFilename;
    private String mAliasName;
    private ParseFile mParseFile;
    private ParseFile mParseThumb;
    private String mType;
    private boolean mError;
    private byte[] mBytesFile;
    private byte[] mBytesThumb;
    private Asset mAsset;
    private ParseUser mUser;
    private Context mContext;
    private static final String CHANNEL_ID = "UPLOAD_ASSET_CHANNEL_ID";

    public AssetUploadService() {
        super("AssetUploadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        setup(intent);
        uploadFile();
    }

    private void setup(Intent intent) {

        // extract infromation from intents
        mContext = this;
        mError = false;
        mSequence = intent.getIntExtra(SEQUENCE,0);
        mPath = intent.getStringExtra(PATH);
        mType = intent.getStringExtra(TYPE);
        mAsset = intent.getParcelableExtra(ASSET_OBJECT);
        mFilename = mPath.substring(mPath.lastIndexOf(".")+1);

        mBytesFile = new byte[0];
        mBytesThumb = new byte[0];

        if(mType.equals(TYPEVIDEO)) {
            mBytesFile = Utils.videoPathToParseByteArray(mPath);
            mBytesThumb = Utils.videoPathToThumbnailByteArray(mPath);
            mAliasName = "Video " + Integer.toString(mSequence + 1);
            mNotifId = mNotifIdDefaultVideo + mSequence;
            mFilename = mAliasName + "." + mFilename;
            mFilename = mFilename.replace(" ","-");
        } else if(mType.equals(TYPEIMAGE)) {
            mBytesFile = Utils.bitmapPathToParseByteArray(mPath);
            mBytesThumb = Utils.imagePathToThumbnailByteArray(mPath);
            mAliasName = "Foto " + Integer.toString(mSequence + 1);
            mNotifId = mNotifIdDefaultImage + mSequence;
            mFilename = mAliasName + "." + mFilename;
            mFilename = mFilename.replace(" ","-");
        }
        if(mBytesFile ==null) {
            mError = true;
        }

    }

    private void uploadFile() {
        // upload thumbnail to Back4App Content Repository
        if(!mError) {
            sendNotificationOk(0, mAliasName);
            mParseThumb =  new ParseFile("thumb "+ mFilename, mBytesThumb);
            mParseThumb.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null) {
                        Log.d("MYSERVICE", "Upload thumbnail "+ mAliasName +" uploaded");
                        uploadMainFile();
                    } else {
                        String er = AssetUploadService.class.getSimpleName() + ":" +
                                "uploadFile"+":"+ e.getMessage().toString();
                        Log.d("MYSERVICE", "Upload thumbnail "+ mAliasName +" error");
                        mError = true;
                        broadcastResultError(er);
                    }
                }
            });
        }
    }

    private void uploadMainFile() {
        // upload AssetVideo/AssetImage to Back4App Content Repository
        if(!mError) {
            mParseFile = new ParseFile(mFilename, mBytesFile);
            mParseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null) {
                        Log.d("MYSERVICE", "Upload "+ mAliasName +" uploaded");
                        saveFile();
                    } else {
                        String er = AssetUploadService.class.getSimpleName() + ":" +
                                "uploadMainFile"+":"+ e.getMessage().toString();
                        Log.d("MYSERVICE", "Upload "+ mAliasName +" error");
                        mError = true;
                        broadcastResultError(er);
                    }
                }
            }, new ProgressCallback() {
                @Override
                public void done(Integer percentDone) {
                    Log.d("MYSERVICE", Integer.toString(percentDone));
                    if(percentDone < 100)
                        sendNotificationOk(percentDone, mAliasName);
                }
            });
        }
    }

    private void saveFile() {
        // save to Asset
        if(!mError) {
            if(mType.equals(TYPEVIDEO)) {
                AssetVideo assetVideo = new AssetVideo();
                assetVideo.setFilename(mFilename);
                assetVideo.setAliasName(mAliasName);
                assetVideo.setVideoFile(mParseFile);
                assetVideo.setThumbFile(mParseThumb);
                assetVideo.setOwner(mAsset);
                assetVideo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null) {
                            broadcastResultOk();
                        } else {
                            String er = AssetUploadService.class.getSimpleName() + ":" +
                                    "saveFile" + ":" + e.getMessage().toString();
                            Log.d("MYSERVICE", e.getMessage().toString());
                            broadcastResultError(er);
                        }
                    }
                });
            } else if(mType.equals(TYPEIMAGE)) {
                AssetImage assetImage = new AssetImage();
                assetImage.setFilename(mFilename);
                assetImage.setAliasName(mAliasName);
                assetImage.setImageFile(mParseFile);
                assetImage.setThumbFile(mParseThumb);
                assetImage.setOwner(mAsset);
                assetImage.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null) {
                            broadcastResultOk();
                        } else {
                            String er = AssetUploadService.class.getSimpleName() + ":" +
                                    "saveFile"+":"+ e.getMessage().toString();
                            Log.d("MYSERVICE", e.getMessage().toString());
                            broadcastResultError(er);
                        }
                    }
                });
            }

        }
    }

    private void broadcastResultOk() {
        Log.d("MYSERVICE", "Done Save");
        Intent broadcastIntent = new Intent(ACTION);
        broadcastIntent.putExtra(RESULTCODE, OK_CODE);
        broadcastIntent.putExtra(RESULTVALUE, mAliasName);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(broadcastIntent);
        sendNotificationOk(100, mAliasName);
    }

    private void broadcastResultError(String message) {
        Log.d("MYSERVICE", "Error Save");
        Utils.CommonToastDisplayerLong(mContext, message);
        Intent broadcastIntent = new Intent(ACTION);
        broadcastIntent.putExtra(RESULTCODE, ER_CODE);
        broadcastIntent.putExtra(RESULTVALUE, mAliasName);
        broadcastIntent.putExtra(RESULTMSG, message);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(broadcastIntent);
        sendNotificationError(mAliasName);
    }

    private void sendNotificationOk(int percentDone, String filename) {
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle("Menunggah");
        builder.setSmallIcon(android.R.drawable.ic_menu_upload);
//        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if(percentDone==100) {
            builder.setContentText("Selesai mengunggah " + filename);
            builder.setProgress(0, 0, false);
        } else {
            builder.setContentText("Sedang mengunggah " + filename + ": " + Integer.toString(percentDone) + "%");
            builder.setProgress(100, percentDone, false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create Notification Channel first for Android O or above

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "name",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("description");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            NotificationManagerCompat notificationManagerCompat =
                    NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(mNotifId, builder.build());
        } else {
            builder.setPriority(Notification.PRIORITY_LOW); // no sound
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(mNotifId, builder.build());
        }
    }

    private void sendNotificationError(String filename) {
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle("Terjadi kesalahan.");

        builder.setSmallIcon(androidx.core.R.drawable.notification_icon_background);
//        builder.setContentIntent(pendingIntent);
        builder.setContentText("Terjadi kesalahan ketika mengunggah " + filename);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create Notification Channel first for Android O or above

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "name",
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("description");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            NotificationManagerCompat notificationManagerCompat =
                    NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(mNotifId, builder.build());
        } else {
            builder.setPriority(Notification.PRIORITY_LOW); // no sound
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(mNotifId, builder.build());
        }
    }
}
