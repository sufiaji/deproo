package com.deproo.android.deproo.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import com.elyeproj.loaderviewlibrary.LoaderImageView;
import com.parse.ParseException;
import com.parse.ParseFile;
import java.lang.ref.WeakReference;

public class AsyncGetAssetThumb extends AsyncTask<AssetImage, Integer, Bitmap> {

    private final WeakReference<LoaderImageView> weakLoaderImageView;
    private final WeakReference<Asset> weakAsset;
    private Uri mImageUri;

    public AsyncGetAssetThumb(LoaderImageView loaderImageView, Asset asset) {
        weakLoaderImageView = new WeakReference<>(loaderImageView);
        weakAsset = new WeakReference<>(asset);
    }

    @Override
    protected Bitmap doInBackground(AssetImage... assetImages) {
        // get image file, uri and bitmap from AssetImage table
        Bitmap bitmap = null;
        AssetImage assetImage = assetImages[0];
        try {
            ParseFile parseFile = assetImage.getImageThumb();
            byte[] data = parseFile.getData();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            String imgAddress = parseFile.getUrl();
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
        if(weakLoaderImageView!=null && weakAsset!=null && bitmap!=null) {
            final LoaderImageView loaderImageView = weakLoaderImageView.get();
            final Asset asset = weakAsset.get();
            if(loaderImageView != null) {
                loaderImageView.setImageBitmap(bitmap);
            }
            if(asset!=null) {
                asset.setImageUri(mImageUri);
                asset.setImage(bitmap);
            }
        }

    }
}
