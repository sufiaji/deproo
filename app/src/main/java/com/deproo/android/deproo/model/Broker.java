package com.deproo.android.deproo.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.utils.Constants;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

@ParseClassName(Constants.ParseTable.TableUser.NAME)
public class Broker extends ParseUser {

    private Bitmap mProfilePicThumb;

    public int getNumAsset() {
        return getInt(Constants.ParseTable.TableUser.NUMBER_OF_ASSET);
    }

    public Broker() {
        super();
    }

    public String getLongname() {
        return getString(Constants.ParseTable.TableUser.LONGNAME);
    }

    public String getTitle() {
        return getString(Constants.ParseTable.TableUser.BIO1);
    }

    public int getNumReview() {
        return getInt(Constants.ParseTable.TableUser.NUMBER_OF_REVIEW);
    }

    public int getNumFollower() {
        return getInt(Constants.ParseTable.TableUser.NUMBER_OF_FOLLOWER);
    }

    public int getDiamondStatus() {
        int resId = R.drawable.diamond1;
        if(getString(Constants.ParseTable.TableUser.STATUS).equalsIgnoreCase(Constants.BroStatus.STATUS1)) {
            resId = R.drawable.diamond1;
        } else if(getString(Constants.ParseTable.TableUser.STATUS).equalsIgnoreCase(Constants.BroStatus.STATUS2)) {
            resId = R.drawable.diamond2;
        } else if(getString(Constants.ParseTable.TableUser.STATUS).equalsIgnoreCase(Constants.BroStatus.STATUS3)) {
            resId = R.drawable.diamond3;
        }

        return resId;
    }

    public void setProfilePicThumb(Bitmap bitmap) {
        mProfilePicThumb = bitmap;
    }

    public Bitmap getProfilePicThumb() {
        return mProfilePicThumb;
    }

    public Bitmap getProfilePicThumbFromServer() {
        ParseFile parseFile = getParseFile(Constants.ParseTable.TableUser.PROFILE_THUMB);
        Bitmap bitmap = null;
        try {
            byte[] data = parseFile.getData();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return bitmap;
    }

    public double getRating() {
        return getDouble(Constants.ParseTable.TableUser.RATING);
    }

    public String getCityProvince() {
        return (getString(Constants.ParseTable.TableUser.CITY) + ", " +
                getString(Constants.ParseTable.TableUser.PROVINCE));
    }

}
