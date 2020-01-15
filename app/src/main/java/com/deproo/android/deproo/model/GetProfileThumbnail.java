package com.deproo.android.deproo.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import com.deproo.android.deproo.utils.Constants;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.lang.ref.WeakReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class GetProfileThumbnail extends AsyncTask<ParseUser, Integer, Bitmap> {

    private WeakReference<CircleImageView> weakCircleImageView;

    public GetProfileThumbnail(CircleImageView circleImageView) {
        weakCircleImageView = new WeakReference<CircleImageView>(circleImageView);
    }

    @Override
    protected Bitmap doInBackground(ParseUser... parseUsers) {
        Bitmap bitmap = null;
        ParseUser user = parseUsers[0];

        try {
            ParseFile parseFile = user.getParseFile(Constants.ParseTable.TableUser.PROFILE_THUMB);
            byte[] data = parseFile.getData();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(weakCircleImageView!=null && bitmap!=null) {
            CircleImageView circleImageView = weakCircleImageView.get();
            if(circleImageView!=null) {
                circleImageView.setImageBitmap(bitmap);
            }
        }
    }
}
