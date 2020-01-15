package com.deproo.android.deproo.model;

import android.graphics.Bitmap;
import android.net.Uri;

import com.deproo.android.deproo.utils.Constants;
import com.deproo.android.deproo.utils.Utils;
import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * created by sufi.aji@gmail.com 13-May-18
 * only to God we adore...
 *
 * This is the Subclass of Asset Table
 */

@ParseClassName(Constants.ParseTable.TableAsset.NAME)
public class Asset extends ParseObject {

    public static final int TYPE_HOUSE = 0;
    public static final int TYPE_OFFICE = 1;
    public static final int TYPE_LAND = 2;
    public static final int TYPE_APART = 3;
    public static final int TYPE_TOWNHOUSE = 4;

    public static final int TYPE_APART_FULL_FURNISHED = 6;
    public static final int TYPE_APART_SEMI_FURNISHED = 7;
    public static final int TYPE_APART_UNFURNISHED = 8;

    private Bitmap mThumbnail;
    private Uri mUri;
    private String mOwnerLongname;

    public Asset() {
        super();
//        setDefaultValue();
    }

    public void setDefaultValue() {
        setNumDiscussion(0);
        setNumFloor(1);
        setNumViewer(0);
        setNumGarage(0);
        setNumBathroom(0);
        setNumBedroom(0);
        setNumPServe(0);
        setSizehouse(0);
        setSizeland(0);
        setNearCluster(false);
        setNearHealth(false);
        setNearHighway(false);
        setNearMarket(false);
        setNearTour(false);
        setNearWorship(false);
        setTypePServe("JUAL");
        setTimePServe("");
        setTypeApart("");
        setRating(0);
        setElectricity(0);
        setHakMilik("");
        setSwimPool(false);
        setNearToll(false);
        setOwner(ParseUser.getCurrentUser());
    }

    public boolean getNearToll() {
        return getBoolean(Constants.ParseTable.TableAsset.IS_NEAR_TOLL);
    }

    public void setNearToll(boolean nearToll) {
        put(Constants.ParseTable.TableAsset.IS_NEAR_TOLL, nearToll);
    }

    public void setOwnerLongname(String ownerLongname) {
        mOwnerLongname = ownerLongname;
    }

    public String getOwnerLongname() {
        return mOwnerLongname;
    }

    public void setSwimPool(boolean swimPool) {
        put(Constants.ParseTable.TableAsset.SWIMPOOL, swimPool);
    }

    public boolean getSwimPool(){
        return getBoolean(Constants.ParseTable.TableAsset.SWIMPOOL);
    }

    public boolean getNearSchool(){
        return getBoolean(Constants.ParseTable.TableAsset.IS_NEAR_SCHOOL);
    }

    public String getSwimPoolFormatted() {
        if(getBoolean(Constants.ParseTable.TableAsset.SWIMPOOL)) {
            return "1";
        } else {
            return "-";
        }
    }

    public void setHakMilik(String hakMilik) {
        put(Constants.ParseTable.TableAsset.HAKMILIK, hakMilik);
    }

    public String getHakMilik() {
        return getString(Constants.ParseTable.TableAsset.HAKMILIK);
    }

    public void setElectricity(int el) {
        put(Constants.ParseTable.TableAsset.ELECTRICITY, el);
    }

    public int getElectricity() {
        return getInt(Constants.ParseTable.TableAsset.ELECTRICITY);
    }

    public String getElectricityFormatted() {
        return (Integer.toString(getInt(Constants.ParseTable.TableAsset.ELECTRICITY))+"Watt");
    }

    public void setImageUri(Uri uri) {
        mUri = uri;
    }

    public Uri getImageUri() {
        return mUri;
    }

    public void setImage(Bitmap bitmap) {
        mThumbnail = bitmap;
    }

    public Bitmap getImage() {
        return mThumbnail;
    }

    public String getStringDate() {
        Date date = getCreatedAt();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(date);
    }

    public int getPropertyType() {
        if(getCategory().equalsIgnoreCase("tanah")) {
            return TYPE_LAND;
        } else if(getCategory().equalsIgnoreCase("kantor")) {
            return TYPE_OFFICE;
        } else if(getCategory().equalsIgnoreCase("gudang")) {
                return TYPE_OFFICE;
        } else if(getCategory().equalsIgnoreCase("rumah")) {
            return TYPE_HOUSE;
        } else if(getCategory().equalsIgnoreCase("apartemen")) {
            return TYPE_APART;
        } else if(getCategory().equalsIgnoreCase("townhouse")) {
            return TYPE_TOWNHOUSE;
        } else if(getCategory().equalsIgnoreCase("villa")) {
            return TYPE_TOWNHOUSE;
        } else {
            return TYPE_HOUSE;
        }
    }

    public void setOwner(ParseUser User) {
        put(Constants.ParseTable.TableAsset.USER, User);
    }

    public Broker getOwner() {
        return (Broker) getParseUser(Constants.ParseTable.TableAsset.USER);
    }

    public void setTitle(String title) {
        put(Constants.ParseTable.TableAsset.TITLE,title);
    }

    public String getTitle() {
        return getString(Constants.ParseTable.TableAsset.TITLE);
    }

    public void setDescription(String description) {
        put(Constants.ParseTable.TableAsset.DESCRIPTION, description);
    }

    public String getDescription() {
        return getString(Constants.ParseTable.TableAsset.DESCRIPTION);
    }

    public void setCategory(String category) {
        put(Constants.ParseTable.TableAsset.CATEGORY, category);
    }

    public String getCategory() {
        return getString(Constants.ParseTable.TableAsset.CATEGORY);
    }

    public String getGlobalCategory() {
        if(getCategory().equalsIgnoreCase("rumah")||
        getCategory().equalsIgnoreCase("apartemen")||
        getCategory().equalsIgnoreCase("townhouse")) {
            return "PROPERTY";
        } else if(getCategory().equalsIgnoreCase("tanah")) {
            return "TANAH";
        } else if(getCategory().equalsIgnoreCase("kantor")) {
            return "KANTOR";
        }
        return null;
    }

    public void setPrice(double price) {
        put(Constants.ParseTable.TableAsset.PRICE,price);
    }

    public double getPrice() {
        return getDouble(Constants.ParseTable.TableAsset.PRICE);
    }

    public String getPriceFormatted() {
        return Utils.formatIDR(getPrice());
    }

    public void setSizeland(double sizeland) {
        put(Constants.ParseTable.TableAsset.SIZE_OF_LAND, sizeland);
    }

    public double getSizeland() {
        return getDouble(Constants.ParseTable.TableAsset.SIZE_OF_LAND);
    }

    public String getSizelandFormatted() {
        if(getDouble(Constants.ParseTable.TableAsset.SIZE_OF_LAND)==0) {
            return "  -";
        } else {
            return ("  " + Utils.formatDouble(getSizeland()));
        }
    }

    public void setSizehouse(double sizehouze) {
        put(Constants.ParseTable.TableAsset.SIZE_OF_HOUSE, sizehouze);
    }

    public double getSizehouse() {
        return getDouble(Constants.ParseTable.TableAsset.SIZE_OF_HOUSE);
    }

    public String getSizehouseFormatted() {
        if(getDouble(Constants.ParseTable.TableAsset.SIZE_OF_HOUSE)==0) {
            return "  -";
        } else {
            return ("  " + Utils.formatDouble(getSizehouse()));
        }
    }

    public void setNumBedroom(int numBedroom) {
        put(Constants.ParseTable.TableAsset.NUMBER_OF_BEDROOM, numBedroom);
    }

    public int getNumBedroom() {
        return getInt(Constants.ParseTable.TableAsset.NUMBER_OF_BEDROOM);
    }

    public String getNumBedroomFormatted() {
        if(getNumBedroom()==0) {
            return "  -";
        } else {
            return ("  " + Integer.toString(getNumBedroom()));
        }
    }

    public void setNumBathroom(int numBathroom) {
        put(Constants.ParseTable.TableAsset.NUMBER_OF_BATHROOM, numBathroom);
    }

    public int getNumBathroom() {
        return getInt(Constants.ParseTable.TableAsset.NUMBER_OF_BATHROOM);
    }

    public String getNumBathroomFormatted() {
        if(getNumBathroom()==0) {
            return "  -";
        } else {
            return ("  " + Integer.toString(getNumBathroom()));
        }
    }

    public void setNearWorship(boolean isNear) {
        put(Constants.ParseTable.TableAsset.IS_NEAR_WORSHIP_PLACE, isNear);
    }

    public boolean getNearWorship() {
        return getBoolean(Constants.ParseTable.TableAsset.IS_NEAR_WORSHIP_PLACE);
    }

    public void setNearHealth(boolean isNear) {
        put(Constants.ParseTable.TableAsset.IS_NEAR_HEALTH_FACILITY, isNear);
    }

    public boolean getNearHealth() {
        return getBoolean(Constants.ParseTable.TableAsset.IS_NEAR_HEALTH_FACILITY);
    }

    public void setNearMarket(boolean isNear) {
        put(Constants.ParseTable.TableAsset.IS_NEAR_MARKET, isNear);
    }

    public boolean getNearMarket() {
        return getBoolean(Constants.ParseTable.TableAsset.IS_NEAR_MARKET);
    }

    public void setNearCluster(boolean isNear) {
        put(Constants.ParseTable.TableAsset.IS_NEAR_PUBLIC_CLUSTER, isNear);
    }

    public boolean getNearCluster() {
        return getBoolean(Constants.ParseTable.TableAsset.IS_NEAR_PUBLIC_CLUSTER);
    }

    public void setNearHighway(boolean isNear) {
        put(Constants.ParseTable.TableAsset.IS_NEAR_HIGHWAY, isNear);
    }

    public boolean getNearHighway() {
        return getBoolean(Constants.ParseTable.TableAsset.IS_NEAR_HIGHWAY);
    }

    public void setNearTour(boolean isNear) {
        put(Constants.ParseTable.TableAsset.IS_NEAR_TOUR, isNear);
    }

    public boolean getNearTour() {
        return getBoolean(Constants.ParseTable.TableAsset.IS_NEAR_TOUR);
    }

    public void setLocation(ParseGeoPoint location) {
        if(location != null)
            put(Constants.ParseTable.TableAsset.LOCATIONS, location);
    }

    public ParseGeoPoint getLocation() {
        return (ParseGeoPoint) getParseGeoPoint(Constants.ParseTable.TableAsset.LOCATIONS);
    }

    public String getConcatAddress() {
        return ( getString(Constants.ParseTable.TableAsset.CITY) + ", " +
                getString(Constants.ParseTable.TableAsset.PROVINCE));
    }

    public void setAddress(String address) {
        put(Constants.ParseTable.TableAsset.ADDRESS, address);
    }

    public String getAddress() {
        return getString(Constants.ParseTable.TableAsset.ADDRESS);
    }

    public void setCity(String city) {
        put(Constants.ParseTable.TableAsset.CITY, city);
    }

    public String getCity() {
        return getString(Constants.ParseTable.TableAsset.CITY);
    }

    public void setProvince(String province) {
        put(Constants.ParseTable.TableAsset.PROVINCE, province);
    }

    public String getProvince() {
        return getString(Constants.ParseTable.TableAsset.PROVINCE);
    }

    public void setRating(int rating) {
        put(Constants.ParseTable.TableAsset.RATING, rating);
    }

    public int getRating() {
        return getInt(Constants.ParseTable.TableAsset.RATING);
    }

    public void setNumGarage(int numGarage) {
        put(Constants.ParseTable.TableAsset.NUMBER_OF_GARAGE, numGarage);
    }

    public int getNumGarage() {
        return getInt(Constants.ParseTable.TableAsset.NUMBER_OF_GARAGE);
    }

    public String getNumGarageFormatted() {
        if(getNumGarage()==0) {
            return "  -";
        } else {
            return ("  " + Integer.toString(getNumGarage()));
        }
    }

    public void setNumViewer(int numViewer) {
        put(Constants.ParseTable.TableAsset.NUMBER_OF_VIEWER, numViewer);
    }

    public int getNumViewer() {
        return getInt(Constants.ParseTable.TableAsset.NUMBER_OF_VIEWER);
    }

    public void setNumFloor(int numFloor) {
        put(Constants.ParseTable.TableAsset.NUMBER_OF_FLOOR, numFloor);
    }

    public int getNumFloor() {
        return getInt(Constants.ParseTable.TableAsset.NUMBER_OF_FLOOR);
    }

    public String getNumFloorFormatted() {
        return ("  " + Integer.toString(getNumFloor()));
    }

    public void setNumDiscussion(int numDiscussion) {
        put(Constants.ParseTable.TableAsset.NUMBER_OF_DISCUSSION, numDiscussion);
    }

    public int getNumDiscussion() {
        return getInt(Constants.ParseTable.TableAsset.NUMBER_OF_DISCUSSION);
    }

    public void setTypeApart(String apartType) {
        if(apartType.equalsIgnoreCase("Fully Furnished")) {
            put(Constants.ParseTable.TableAsset.CATEGORY_OF_APARTEMENT,
                    Constants.CategoryApartment.FULLYFURNISHED);
        } else if(apartType.equalsIgnoreCase("Unfurnished")) {
            put(Constants.ParseTable.TableAsset.CATEGORY_OF_APARTEMENT,
                    Constants.CategoryApartment.UNFURNISHED);
        } else if(apartType.equalsIgnoreCase("Partly Furnished")) {
            put(Constants.ParseTable.TableAsset.CATEGORY_OF_APARTEMENT,
                    Constants.CategoryApartment.PARTLYFURNISHED);
        }
    }

    public String getTypeApart() {
        String codeType = getString(Constants.ParseTable.TableAsset.CATEGORY_OF_APARTEMENT);
        if(codeType.equalsIgnoreCase(Constants.CategoryApartment.UNFURNISHED)) {
            return "Unfurnished";
        } else if(codeType.equalsIgnoreCase(Constants.CategoryApartment.FULLYFURNISHED)) {
            return "Fully furnished";
        } else if(codeType.equalsIgnoreCase(Constants.CategoryApartment.PARTLYFURNISHED)) {
            return "Partly furnished";
        } else {
            return "Unfurnished";
        }
    }

    public void setTypePServe(String typePServe) {
        // sewa, jual
        put(Constants.ParseTable.TableAsset.CATEGORY_OF_PSERVE, typePServe);
    }

    public String getTypePServe() {
        // sewa, jual
        return getString(Constants.ParseTable.TableAsset.CATEGORY_OF_PSERVE);
    }

    public void setTimePServe(String timeString) {
        // bulan, tahun, hari
        put(Constants.ParseTable.TableAsset.TIME_OF_PSERVE, timeString);
    }

    public String getTimePServe() {
        // bulan, tahun, hari
        return getString(Constants.ParseTable.TableAsset.TIME_OF_PSERVE);
    }

    public void setNumPServe(int numServe) {
        // 1, 2, 3, 4 .... 12
        put(Constants.ParseTable.TableAsset.NUM_OF_PSERVE, numServe);
    }

    public int getNumPServe() {
        // 1, 2, 3, 4 .... 12
        return getInt(Constants.ParseTable.TableAsset.NUM_OF_PSERVE);
    }

}
