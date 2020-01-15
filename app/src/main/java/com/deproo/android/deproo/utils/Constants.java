package com.deproo.android.deproo.utils;

public final class Constants {

    public final static String FIX_PASSWORD = "abc123";
    public final static int DEFAULT_IMAGE_SIZE = 800;
    public final static int DEFAULT_IMAGE_PROFILESIZE = 300;
    public final static int DEFAULT_IMAGE_THUMBSIZE = 300;
    public final static int MAX_UPLOAD_IMAGE = 8;
    public final static int MAX_UPLOAD_VIDEO = 5;
    public final static int JPG_COMPRESSION_QUALITY_MEDIUM = 70;

    public final static String IMAGEVIEW_UPLOAD_NAME_PIC = "id_imageview_mediapic_";
    public final static String IMAGEVIEW_UPLOAD_NAME_VID = "id_imageview_mediavid_";

    public final static String HORIZONTAL_ASSET_LIST_TEXTVIEW = "id_tasset_";
    public final static String HORIZONTAL_ASSET_LIST_IMAGEVIEW = "id_asset_";
    public final static String HORIZONTAL_ASSET_LIST_LAYOUT = "id_ly_p1_";

    public final static String HORIZONTAL_REVIEW_LIST_LAYOUT = "id_ly_r1_";
    public final static String HORIZONTAL_REVIEW_LIST_TEXTVIEW_NAME = "id_name_review";
    public final static String HORIZONTAL_REVIEW_LIST_TEXTVIEW_REVIEW = "id_review";
    public final static String HORIZONTAL_REVIEW_LIST_TEXTVIEW_RATING = "id_rating_review";

    public final static String SERVICECOMMAND = "SERVICECOMMAND";
    public static final String ASSET_OBJECT = "ASSETOBJECT";
    public static final String BROKER_OBJECT = "BROKEROBJECT";
    public static final String BITMAP = "BITMAP";
    public static final String URI = "URI";

    public static final String FILTER_CAT = "CATEGORY";
    public static final String FILTER_PSERVECAT = "PSERVECAT";

    public static final int HOME_SLIDE_NUMBER = 5;


    public final class SharedPreference {
        public static final String PREFNAME = "DEPROOSHAREDPREF";
        public static final String INITSTATE = "INITSTATE";
        public static final String PROFILEBITMAP = "PROFILEBITMAP";
        public static final String LISTBROKER = "LISTBROKER";
        public static final String LISTASSET = "LISTASSET";
    }


    public final class ParseTable {

        public final class TableUser {
            public final static String NAME = "_User";
            public final static String USERNAME = "username";
            public final static String EMAILVERIFIED = "emailVerified";
            public final static String CREATED_AT = "createdAt";
            public final static String LOGIN_WITH = "loginWith";
            public final static String RATING = "rating";
            public final static String PROFILE_THUMB = "profileThumb";
            public final static String PROFILE_BG = "profileBg";
            public final static String POINT = "point";
            public final static String STATUS = "status";
            public final static String RANKING = "rank";
            public final static String PHONE = "phone";
            public final static String BIO1 = "bio1";
            public final static String BIO2 = "bio2";
            public final static String ADDRESS = "address";
            public final static String GENDER = "gender";
            public final static String LONGNAME = "longname";
            public final static String NUMBER_OF_ASSET = "numAsset";
            public final static String NUMBER_OF_FOLLOWER = "numFollower";
            public final static String IS_BROKER = "isBroker";
            public final static String CITY = "city";
            public final static String PROVINCE = "province";
            public final static String NUMBER_OF_REVIEW = "numReview";
        }

        public final class TableAsset {
            public final static String NAME = "Asset";
            public final static String USER = "USER";
            public final static String TITLE = "title";
            public final static String DESCRIPTION = "description";
            public final static String CATEGORY = "category";
            public final static String PRICE = "price";
            public final static String SIZE_OF_LAND = "sizeLand";
            public final static String SIZE_OF_HOUSE = "sizeHouse";
            public final static String NUMBER_OF_BEDROOM = "numBedroom";
            public final static String NUMBER_OF_BATHROOM = "numBathroom";
            public final static String LOCATIONS = "locations";
            public final static String IS_NEAR_WORSHIP_PLACE = "isNearWshp";
            public final static String IS_NEAR_HEALTH_FACILITY = "isNearHlth";
            public final static String IS_NEAR_MARKET = "isNearMrkt";
            public final static String IS_NEAR_PUBLIC_CLUSTER = "isNearClstr";
            public final static String IS_NEAR_HIGHWAY = "isNearHghw";
            public final static String IS_NEAR_TOUR = "isNearTour";
            public final static String IS_NEAR_SCHOOL = "isNearSchool";
            public final static String CREATED_AT = "createdAt";
            public final static String NUMBER_OF_VIEWER = "numViewer";
            public final static String RATING = "rating";
            public final static String ADDRESS = "address";
            public final static String CITY = "city";
            public final static String PROVINCE = "province";
            public final static String NUMBER_OF_GARAGE = "numGarage";
            public final static String NUMBER_OF_DISCUSSION = "numDiscussion";
            public final static String NUMBER_OF_FLOOR = "numFloor";
            public final static String CATEGORY_OF_APARTEMENT = "apartCategory"; //semi, fully/unfurnished
            public final static String CATEGORY_OF_PSERVE = "pServeCategory";
            public final static String TIME_OF_PSERVE = "pServeTime";
            public final static String NUM_OF_PSERVE = "pServeNum";
            public final static String ELECTRICITY = "electricity";
            public final static String HAKMILIK = "hakMilik";
            public final static String SWIMPOOL = "swimpool";
            public final static String IS_NEAR_TOLL = "isNearToll";
        }

        public final class TableAssetImage {
            public final static String NAME = "AssetImage";
            public final static String ASSET = "ASSET";
            public final static String IMAGE_FILE = "imageFile";
            public final static String IMAGE_FILENAME = "imageFilename";
            public final static String IMAGE_ALIASNAME = "imageAliasname";
            public final static String IMAGE_THUMB = "imageThumb";
        }

        public final class TableAssetVideo {
            public final static String NAME = "AssetVideo";
            public final static String ASSET = "ASSET";
            public final static String VIDEO_FILE = "videoFile";
            public final static String VIDEO_FILENAME = "videoFilename";
            public final static String VIDEO_ALIASNAME = "videoAliasname";
            public final static String VIDEO_THUMB = "videoThumb";
        }

        public final class TableUserReview {
            public final static String NAME = "UserReview";
            public final static String USER = "USER"; //owner
            public final static String RATING_FROM = "rUser"; // user who give the review
            public final static String RATING = "rRating"; // rating given
            public final static String REVIEW = "rReview"; // review given
        }

        public final class TableAssetDiscussion {
            public static final String NAME = "AssetDiscussion";
            public static final String USER = "user";
            public static final String ASSET = "asset";
            public static final String DISCUSSION = "discussion";
        }

        public final class TableMasterCategoryAsset {
            public final static String NAME = "MasterCategoryAsset";
            public final static String CATEGORY = "category";
        }
    }

    public final class BroStatus {
        public final static String STATUS1 = "Bro";
        public final static String STATUS2 = "De-Bro";
        public final static String STATUS3 = "Big-Bro";
    }

    public final class ServiceCommand {
        public final static String DUMMYCOMMAND = "DUMMYCOMMAND";
        public final static String GETUSERDATA = "GETUSERDATA";
    }

    public final class CategoryApartment {
        public final static String PARTLYFURNISHED = "PF";
        public final static String FULLYFURNISHED = "FF";
        public final static String UNFURNISHED = "UF";
    }

    public final class CategoryPropertyServe {
        public final static String SELL = "JUAL";
        public final static String RENT = "SEWA";
    }

}
