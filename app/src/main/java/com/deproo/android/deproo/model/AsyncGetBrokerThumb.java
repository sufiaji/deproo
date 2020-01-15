package com.deproo.android.deproo.model;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.deproo.android.deproo.application.DeprooApplication;
import com.elyeproj.loaderviewlibrary.LoaderImageView;

import java.lang.ref.WeakReference;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class AsyncGetBrokerThumb extends AsyncTask<Broker, Integer, Broker> {

    private final WeakReference<LoaderImageView> mLoaderImageReference;
    private final WeakReference<CircleImageView> mCircleView;
    private int nMode;

    public AsyncGetBrokerThumb(LoaderImageView loaderView) {
        mLoaderImageReference = new WeakReference<>(loaderView);
        mCircleView = null;
        nMode = 1;
    }

    public AsyncGetBrokerThumb(CircleImageView circleView) {
        mLoaderImageReference = null;
        mCircleView = new WeakReference<>(circleView);
        nMode = 2;
    }

    @Override
    protected Broker doInBackground(Broker... brokers) {
        Broker broker = brokers[0];
        Bitmap bitmap = broker.getProfilePicThumbFromServer();
        if(bitmap!=null) {
            broker.setProfilePicThumb(bitmap);
            if(nMode==1) {
                for (int i = 0; i < DeprooApplication.sharedBrokerList.size(); i++) {
                    if (broker.getObjectId().equals(DeprooApplication.sharedBrokerList.get(i))) {
                        DeprooApplication.sharedBrokerList.get(i).setProfilePicThumb(bitmap);
                    }
                }
            }
//            } else {
//                DeprooApplication.thisBroker = broker;
//            }
        }
        return broker;
    }

    @Override
    protected void onPostExecute(Broker broker) {
        if(nMode==1) {
            if (mLoaderImageReference != null && broker != null) {
                final LoaderImageView loaderView = mLoaderImageReference.get();
                if (loaderView != null) {
                    Bitmap bitmap = broker.getProfilePicThumb();
                    if (bitmap != null)
                        loaderView.setImageBitmap(bitmap);
                }
            }
        } else if(nMode==2) {
            if(mCircleView != null && broker != null) {
                final CircleImageView circleImageView = mCircleView.get();
                Bitmap bitmap = broker.getProfilePicThumb();
                if(bitmap!=null)
                    circleImageView.setImageBitmap(broker.getProfilePicThumb());
            }
        }
    }

}
