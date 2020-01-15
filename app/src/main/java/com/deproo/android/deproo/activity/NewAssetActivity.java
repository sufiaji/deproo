package com.deproo.android.deproo.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.deproo.android.deproo.application.DeprooApplication;
import com.deproo.android.deproo.R;
import com.deproo.android.deproo.model.Asset;
import com.deproo.android.deproo.utils.Constants;
import com.deproo.android.deproo.utils.Utils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class NewAssetActivity extends AppCompatActivity {

    public static final String MAPSCREENSHOT = "MAPSCREENSHOT";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String EXISTING_DESC = "EXISTING_DESC";
    public static final String SELECTED_IMAGE_PATH = "SELECTED_IMAGE_PATH";
    public static final String SELECTED_VIDEO_PATH = "SELECTED_VIDEO_PATH";
    private static final String IMAGE_MODE = "IMAGE_MODE";
    private static final String VIDEO_MODE = "VIDEO_MODE";
    public static final String MAPCALLEDFROM = "MAPCALLEDFROM";

    private static final int REQUEST_PICK_IMAGE = 1;
    private static final int REQUEST_PICK_VIDEO = 2;
    private static final int REQUEST_MAP = 3;
    private static final int REQUEST_DESC = 4;
    private static final int REQUEST_PREVIEW_IMAGE = 5;
    private static final int REQUEST_PREVIEW_VIDEO = 6;
    private static final int REQUEST_PERMISSION_STORAGE_CAM_AUDIO_WAKELOCK = 7;

    private ParseGeoPoint mGeopoints;
    private Spinner mSpinnerCategory;
    private Spinner mSpinnerPServeNum;
    private Spinner mSpinnerPServeTime;
    private Spinner mSpinnerPServeType;
    private Spinner mSpinnerApartCategory;
    private EditText mEditDesc;
    private EditText mEditProv;
    private EditText mEditCity;

    private ProgressDialog mProgressDialog;
    private String mDescription = "";

    //private ArrayList<String> mPicListPath;
    private ArrayList<Bitmap> mPicListBitmap;
    private ArrayList<Bitmap> mPicListThumb;
    private ArrayList<String> mPicListPath;

    private ArrayList<FileInputStream> mVidListFis;
    private ArrayList<Bitmap> mVidListThumb;
    private ArrayList<String> mVidListPath;
    private int mImageViewID;
    private boolean mRequestImage;
    private int mSelectedImageIndex;
    private int mSelectedVideoIndex;
    private Bitmap mMapImage;

   //private Asset mAsset;

    //private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mContext = this;
        setContentView(R.layout.activity_new_asset);
        getSupportActionBar().setTitle(R.string.title_new_asset);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mEditDesc = (EditText) findViewById(R.id.id_edittext_newasset_desc);
        mEditDesc.setInputType(InputType.TYPE_NULL);
        ImageView mMap = (ImageView) findViewById(R.id.id_imageview_newasset_map);
        registerForContextMenu(mMap);

        DeprooApplication app = (DeprooApplication) getApplicationContext();

        // rumah, tanah, apartemen, kantor
        mSpinnerCategory = (Spinner) findViewById(R.id.id_spinner_category);
        List<String> categoryList = app.getCategoryList();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categoryList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCategory.setAdapter(dataAdapter);
        mSpinnerCategory.setSelection(0);

        // unfurnished, fully furnished, partly furnished
        mSpinnerApartCategory = (Spinner) findViewById(R.id.id_spinner_category_apart);
        List<String> apartcategoryList = app.getApartCategoryList();
        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, apartcategoryList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerApartCategory.setAdapter(dataAdapter);
        mSpinnerApartCategory.setSelection(0);

        // sewa, jual
        mSpinnerPServeType = (Spinner) findViewById(R.id.id_spinner_pservetype);
        List<String> pServeTypeList = app.getPServeTypeList();
        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, pServeTypeList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerPServeType.setAdapter(dataAdapter);
        mSpinnerPServeType.setSelection(0);

        // bulan, hari, tahun
        mSpinnerPServeTime = (Spinner) findViewById(R.id.id_spinner_pservetime);
        List<String> pServeTimeList = app.getPServeTimeList();
        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, pServeTimeList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerPServeTime.setAdapter(dataAdapter);
        mSpinnerPServeTime.setSelection(0);

        // 1,2,3,4 .... 12
        mSpinnerPServeNum = (Spinner) findViewById(R.id.id_spinner_pservenum);
        List<String> pServeNumList = app.getPServeNumList();
        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, pServeNumList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerPServeNum.setAdapter(dataAdapter);
        mSpinnerPServeNum.setSelection(0);

        mPicListPath = new ArrayList<>();
        mPicListBitmap = new ArrayList<>();
        mPicListThumb = new ArrayList<>();

        mVidListPath = new ArrayList<>();
        mVidListFis = new ArrayList<>();
        mVidListThumb = new ArrayList<>();

        // selection on Sewa, Jual
        mSpinnerPServeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            LinearLayout lin1 = (LinearLayout) findViewById(R.id.id_ly_numrent);
            LinearLayout lin2 = (LinearLayout) findViewById(R.id.id_ly_timerent);

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0) {
                    // JUal, then hide timeserve and timenum
                    lin1.setVisibility(View.GONE);
                    lin2.setVisibility(View.GONE);
                } else {
                    lin1.setVisibility(View.VISIBLE);
                    lin2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                lin1.setVisibility(View.VISIBLE);
                lin2.setVisibility(View.VISIBLE);
            }
        });

        // selection on Rumah, Tanah, Apart, Kantor, townhouse
        mSpinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0) {
                    // rumah
                    setLayoutForHouse();
                } else if(i==1) {
                    // apartemen
                    setLayoutForApart();
                } else if(i==2) {
                    // townhouse
                    setLayoutForTownhouse();
                } else if(i==3){
                    // kantor
                    setLayoutForOffice();
                } else if(i==4) {
                    // tanah
                    setLayoutForLand();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                setLayoutForHouse();
            }
        });

    }

    private void setLayoutForTownhouse() {
        // hide apart
        LinearLayout lin1 = (LinearLayout) findViewById(R.id.id_ly_apart_category);
        lin1.setVisibility(View.GONE);
        // show rest
        LinearLayout ly1 = (LinearLayout) findViewById(R.id.id_ly_luas_bangunan);
        ly1.setVisibility(View.VISIBLE);
        LinearLayout ly2 = (LinearLayout) findViewById(R.id.id_ly_kamar);
        ly2.setVisibility(View.VISIBLE);
        LinearLayout ly3 = (LinearLayout) findViewById(R.id.id_ly_garage_floor);
        ly3.setVisibility(View.VISIBLE);
        LinearLayout ly4 = (LinearLayout) findViewById(R.id.id_ly_luas_tanah);
        ly4.setVisibility(View.VISIBLE);
        // kolam renang, wattage, shm
        LinearLayout ly5 = findViewById(R.id.id_ly_newasset_shm);
        ly5.setVisibility(View.VISIBLE);
        LinearLayout ly6 = findViewById(R.id.id_ly_newasset_watt);
        ly6.setVisibility(View.VISIBLE);
        CheckBox ck1 = findViewById(R.id.id_checkbox_swimpool);
        ck1.setVisibility(View.VISIBLE);
    }

    private void setLayoutForHouse() {
        // hide apart
        LinearLayout lin1 = (LinearLayout) findViewById(R.id.id_ly_apart_category);
        lin1.setVisibility(View.GONE);
        // show rest
        LinearLayout ly1 = (LinearLayout) findViewById(R.id.id_ly_luas_bangunan);
        ly1.setVisibility(View.VISIBLE);
        LinearLayout ly2 = (LinearLayout) findViewById(R.id.id_ly_kamar);
        ly2.setVisibility(View.VISIBLE);
        LinearLayout ly3 = (LinearLayout) findViewById(R.id.id_ly_garage_floor);
        ly3.setVisibility(View.VISIBLE);
        LinearLayout ly4 = (LinearLayout) findViewById(R.id.id_ly_luas_tanah);
        ly4.setVisibility(View.VISIBLE);
        // kolam renang, wattage, shm
        LinearLayout ly5 = findViewById(R.id.id_ly_newasset_shm);
        ly5.setVisibility(View.VISIBLE);
        LinearLayout ly6 = findViewById(R.id.id_ly_newasset_watt);
        ly6.setVisibility(View.VISIBLE);
        CheckBox ck1 = findViewById(R.id.id_checkbox_swimpool);
        ck1.setVisibility(View.VISIBLE);
    }

    private void setLayoutForApart() {
        // show apart
        LinearLayout lin1 = (LinearLayout) findViewById(R.id.id_ly_apart_category);
        lin1.setVisibility(View.VISIBLE);
        // show rest
        LinearLayout ly1 = (LinearLayout) findViewById(R.id.id_ly_luas_bangunan);
        ly1.setVisibility(View.VISIBLE);
        LinearLayout ly2 = (LinearLayout) findViewById(R.id.id_ly_kamar);
        ly2.setVisibility(View.VISIBLE);
        LinearLayout ly3 = (LinearLayout) findViewById(R.id.id_ly_garage_floor);
        ly3.setVisibility(View.VISIBLE);
        LinearLayout ly4 = (LinearLayout) findViewById(R.id.id_ly_luas_tanah);
        ly4.setVisibility(View.GONE);
        ((EditText) findViewById(R.id.id_edittext_newasset_luas_tanah)).setText("");
        // kolam renang, wattage, shm
        LinearLayout ly5 = findViewById(R.id.id_ly_newasset_shm);
        ly5.setVisibility(View.VISIBLE);
        LinearLayout ly6 = findViewById(R.id.id_ly_newasset_watt);
        ly6.setVisibility(View.VISIBLE);
        CheckBox ck1 = findViewById(R.id.id_checkbox_swimpool);
        ck1.setVisibility(View.VISIBLE);
    }

    private void setLayoutForLand() {
        // hide apart
        LinearLayout lin1 = (LinearLayout) findViewById(R.id.id_ly_apart_category);
        lin1.setVisibility(View.GONE);
        // hide rest
        LinearLayout ly1 = (LinearLayout) findViewById(R.id.id_ly_luas_bangunan);
        ly1.setVisibility(View.GONE);
        LinearLayout ly2 = (LinearLayout) findViewById(R.id.id_ly_kamar);
        ly2.setVisibility(View.GONE);
        LinearLayout ly3 = (LinearLayout) findViewById(R.id.id_ly_garage_floor);
        ly3.setVisibility(View.GONE);
        LinearLayout ly4 = (LinearLayout) findViewById(R.id.id_ly_luas_tanah);
        ly4.setVisibility(View.VISIBLE);
        ((EditText) findViewById(R.id.id_edittext_newasset_luas_bangunan)).setText("");
        ((EditText) findViewById(R.id.id_edittext_newasset_bathnum)).setText("");
        ((EditText) findViewById(R.id.id_edittext_newasset_bednum)).setText("");
        ((EditText) findViewById(R.id.id_edittext_newasset_garagenum)).setText("");
        ((EditText) findViewById(R.id.id_edittext_newasset_floornum)).setText("");
        // kolam renang, wattage, shm
        LinearLayout ly5 = findViewById(R.id.id_ly_newasset_shm);
        ly5.setVisibility(View.VISIBLE);
        LinearLayout ly6 = findViewById(R.id.id_ly_newasset_watt);
        ly6.setVisibility(View.GONE);
        CheckBox ck1 = findViewById(R.id.id_checkbox_swimpool);
        ck1.setVisibility(View.GONE);
    }

    private void setLayoutForOffice() {
        // hide apart
        LinearLayout lin1 = (LinearLayout) findViewById(R.id.id_ly_apart_category);
        lin1.setVisibility(View.GONE);
        // hide kamar & bedroom & bathroom
        LinearLayout ly2 = (LinearLayout) findViewById(R.id.id_ly_kamar);
        ly2.setVisibility(View.GONE);
        LinearLayout ly3 = (LinearLayout) findViewById(R.id.id_ly_luas_bangunan);
        ly3.setVisibility(View.VISIBLE);
        LinearLayout ly4 = (LinearLayout) findViewById(R.id.id_ly_luas_tanah);
        ly4.setVisibility(View.GONE);
        ((EditText) findViewById(R.id.id_edittext_newasset_bathnum)).setText("");
        ((EditText) findViewById(R.id.id_edittext_newasset_bednum)).setText("");
        // hide luas tanah
        ((EditText) findViewById(R.id.id_edittext_newasset_luas_tanah)).setText("");
        // kolam renang, wattage, shm
        LinearLayout ly5 = findViewById(R.id.id_ly_newasset_shm);
        ly5.setVisibility(View.GONE);
        LinearLayout ly6 = findViewById(R.id.id_ly_newasset_watt);
        ly6.setVisibility(View.VISIBLE);
        CheckBox ck1 = findViewById(R.id.id_checkbox_swimpool);
        ck1.setVisibility(View.GONE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.id_imageview_newasset_map) {
            getMenuInflater().inflate(R.menu.menu_img_loc, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_menu_delete_loc:
                ImageView imgMap = (ImageView) findViewById(R.id.id_imageview_newasset_map);
                imgMap.setImageBitmap(null);
                imgMap.setImageResource(R.drawable.maps);
                mGeopoints = null;
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                // todo: goto back activity from here
               onBackPressed();
			return true; }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * When user click on Add Pic thumbnail
     * @param v
     */
    public void onPicClick(View v) {
        mImageViewID = v.getId();
        mRequestImage = true;
        if (Build.VERSION.SDK_INT < 23) {
            handleImagePickerRequest();
        } else {
            if(checkPermission())
                handleImagePickerRequest();
        }
    }

    /**
     * When user click on Add Vid thumbnail
     * @param v
     */
    public void onVidClick(View v) {
        mImageViewID = v.getId();
        mRequestImage = false;
        if (Build.VERSION.SDK_INT < 23) {
            handleVideoPickerRequest();
        } else {
            if(checkPermission())
                handleVideoPickerRequest();
        }
    }

    private boolean checkPermission() {
        int permissionReadStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int permissionWakelock = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK);
        List<String> listPermissionNeeded = new ArrayList<>();

        if(permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.CAMERA);
        }
        if(permissionAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if(permissionWakelock != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.WAKE_LOCK);
        }
        if (listPermissionNeeded.size()>0) {
            ActivityCompat.requestPermissions(
                    this,
                    listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]),
                    REQUEST_PERMISSION_STORAGE_CAM_AUDIO_WAKELOCK);
            return false;
        }
        return true;
    }

    public void onDescriptionClick(View v) {
        Intent i = new Intent(NewAssetActivity.this, NewAssetDescActivity.class);
        i.putExtra(EXISTING_DESC, mDescription);
        startActivityForResult(i, REQUEST_DESC);
    }

    public void onSaveAssetClick(View v) {
        alertConfirmSave("Deproo", "Apakah Anda yakin semua data sudah benar?");
        // test
//        saveDataToParse2();
//        String storageDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "Depronesia" + File.separator;
//        File dir = new File(storageDir);
//        if(!dir.exists()) {
//            dir.mkdirs();
//        }
//        new AsyncCompressVideo(getApplicationContext(), storageDir).execute(mVidListPath);
    }

    public void onMapClick(View v) {
        Intent i = new Intent(NewAssetActivity.this, AssetLocActivity.class);
        i.putExtra(MAPCALLEDFROM, NewAssetActivity.class.getSimpleName());
        if(mGeopoints != null) {
            i.putExtra(LATITUDE, mGeopoints.getLatitude());
            i.putExtra(LONGITUDE, mGeopoints.getLongitude());
        }
        startActivityForResult(i, REQUEST_MAP);
    }

    private void openMediaPicker(String mode) {
        if(mode.equals(IMAGE_MODE)) {
            /*intent.putExtra("title", "Pilih Gambar");
            intent.putExtra("maxSelection", Constants.MAX_UPLOAD_IMAGE - mPicListThumb.size());
            startActivityForResult(intent, REQUEST_PICK_IMAGE);*/
            Matisse.from(NewAssetActivity.this)
                    .choose(MimeType.ofImage())
                    .showSingleMediaType(true)
                    .countable(true)
                    .maxSelectable(Constants.MAX_UPLOAD_IMAGE - mPicListThumb.size())
                    .capture(true)
                    .captureStrategy(
                            new CaptureStrategy(
                                    true,
                                    "com.zhihu.matisse.sample.fileprovider"))
                    .forResult(REQUEST_PICK_IMAGE);
        } else if(mode.equals(VIDEO_MODE)) {
            /*intent.putExtra("title", "Pilih Video");
            intent.putExtra("maxSelection", Constants.MAX_UPLOAD_VIDEO - mVidListThumb.size());
            startActivityForResult(intent, REQUEST_PICK_VIDEO);*/
            Matisse.from(NewAssetActivity.this)
                    .choose(MimeType.ofVideo())
                    .showSingleMediaType(true)
                    .countable(true)
                    .maxSelectable(Constants.MAX_UPLOAD_VIDEO - mVidListThumb.size())
//                    .capture(true)
//                    .captureStrategy(
//                            new CaptureStrategy(
//                                    true,
//                                    "com.zhihu.matisse.sample.fileprovider"))
                    .forResult(REQUEST_PICK_VIDEO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MAP && resultCode == RESULT_OK && null != data) {
            // From Get Location
            double lat = data.getExtras().getDouble(LATITUDE);
            double lon = data.getExtras().getDouble(LONGITUDE);
            mGeopoints = new ParseGeoPoint(lat, lon);
            byte[] mapShoot = data.getExtras().getByteArray(MAPSCREENSHOT);
            if(mapShoot != null){
                try{
                    Bitmap b = BitmapFactory.decodeByteArray(mapShoot, 0, mapShoot.length);
                    ImageView showMap = (ImageView) findViewById(R.id.id_imageview_newasset_map);
                    showMap.setImageBitmap(b);
                    mMapImage = b;
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == REQUEST_DESC && resultCode == RESULT_OK){
            // From Write Description
            mEditDesc.setText(data.getExtras().getString(DESCRIPTION));
            mDescription = data.getExtras().getString(DESCRIPTION);
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            // From Image Picker New Image
            handleNewImagePickerResult(Matisse.obtainPathResult(data));
        } else if (requestCode == REQUEST_PICK_VIDEO && resultCode == RESULT_OK && data != null) {
            // From Video Picker New Video
            handleNewVideoPickerResult(Matisse.obtainPathResult(data));
        } else if(requestCode == REQUEST_PREVIEW_IMAGE && resultCode == RESULT_OK) {
            // From Image Picker Existing Image
            handleExistingImagePickerResult(mSelectedImageIndex);
        } else if(requestCode == REQUEST_PREVIEW_VIDEO && resultCode == RESULT_OK) {
            // From Video Picker Existing Video
            handleExistingVideoPickerResult(mSelectedVideoIndex);
        }
    }

    private void handleExistingImagePickerResult(int index) {
        mPicListPath.remove(index);
        mPicListThumb.remove(index);
        mPicListBitmap.remove(index);
        for(int i=0;i<mPicListThumb.size();i++) {
            String name = Constants.IMAGEVIEW_UPLOAD_NAME_PIC + Integer.toString(i + 1);
            int id = getResources().getIdentifier(name, "id", this.getPackageName());
            ImageView imgv = (ImageView) findViewById(id);
            imgv.setImageBitmap(mPicListThumb.get(i));
            imgv.setVisibility(View.VISIBLE);
        }
        String name = Constants.IMAGEVIEW_UPLOAD_NAME_PIC + Integer.toString(mPicListBitmap.size() + 1);
        int id = getResources().getIdentifier(name, "id", this.getPackageName());
        ImageView imgv = (ImageView) findViewById(id);
        imgv.setImageResource(R.drawable.add_image);
        imgv.setVisibility(View.VISIBLE);
        if(mPicListThumb.size()<=Constants.MAX_UPLOAD_IMAGE-2) {
            for(int i=mPicListThumb.size()+2;i<=Constants.MAX_UPLOAD_IMAGE;i++) {
                String name2 = Constants.IMAGEVIEW_UPLOAD_NAME_PIC + Integer.toString(i);
                int id2 = getResources().getIdentifier(name2, "id", this.getPackageName());
                ImageView imgv2 = (ImageView) findViewById(id2);
                imgv2.setVisibility(View.GONE);
            }
        }
    }

    private void handleExistingVideoPickerResult(int index) {
        mVidListPath.remove(index);
        mVidListThumb.remove(index);
        mVidListFis.remove(index);
        for(int i=0;i<mVidListThumb.size();i++) {
            String name = Constants.IMAGEVIEW_UPLOAD_NAME_VID + Integer.toString(i + 1);
            int id = getResources().getIdentifier(name, "id", this.getPackageName());
            ImageView imgv = (ImageView) findViewById(id);
            imgv.setImageBitmap(mVidListThumb.get(i));
            imgv.setVisibility(View.VISIBLE);
        }
        String name = Constants.IMAGEVIEW_UPLOAD_NAME_VID + Integer.toString(mVidListThumb.size() + 1);
        int id = getResources().getIdentifier(name, "id", this.getPackageName());
        ImageView imgv = (ImageView) findViewById(id);
        imgv.setImageResource(R.drawable.add_video);
        imgv.setVisibility(View.VISIBLE);
        if(mVidListThumb.size()<=Constants.MAX_UPLOAD_VIDEO-2) {
            for(int i=mVidListThumb.size()+2;i<=Constants.MAX_UPLOAD_VIDEO;i++) {
                String name2 = Constants.IMAGEVIEW_UPLOAD_NAME_VID + Integer.toString(i);
                int id2 = getResources().getIdentifier(name2, "id", this.getPackageName());
                ImageView imgv2 = (ImageView) findViewById(id2);
                imgv2.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_STORAGE_CAM_AUDIO_WAKELOCK: {
                if (grantResults.length > 0) {
                    boolean allowed = true;
                    for(int i=0;i<grantResults.length;i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                            allowed = false;
                    }
                    if(allowed) {
                        if(mRequestImage)
                            handleImagePickerRequest();
                        else
                            handleVideoPickerRequest();
                    } else {
                        String msg;
                        if(mRequestImage)
                            msg = "Tidak dapat mengambil gambar karena tidak diperbolehkan.";
                        else
                            msg = "Tidak dapat mengambil video karena tidak diperbolehkan.";
                        Utils.CommonToastDisplayerLong(this,msg);
                    }
                }
                return;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void handleImagePickerRequest() {
        String mode = IMAGE_MODE;
        // Multimedia File Picker Library by https://github.com/erikagtierrez/multiple-media-picker
        if (mPicListThumb.size() == 0) {
            // no selected pics yet, open mediapicker
            //Utils.CommonToastDisplayerShort(this, Utils.image_upload_max);
            openMediaPicker(mode);
        } else {
            // if in empty imageview, open mediapicker
            // if in imageview with image, open preview and give option to delete
            String idImgView = getResources().getResourceName(mImageViewID);
            String cindex = idImgView.substring(idImgView.lastIndexOf("_")+1);
            int index = Integer.valueOf(cindex);
            mSelectedImageIndex = index - 1;
            if (mPicListThumb.size() < index) {
                // user click on empty imageview
                //Utils.CommonToastDisplayerShort(this, Utils.image_upload_max);
                openMediaPicker(mode);
            } else {
                // user click on existing bitmap, so open preview and give delete option
                Intent i = new Intent(NewAssetActivity.this, NewAssetImagePrevActivity.class);
                i.putExtra(SELECTED_IMAGE_PATH, mPicListPath.get(mSelectedImageIndex));
                startActivityForResult(i,REQUEST_PREVIEW_IMAGE);
            }
        }
    }

    private void handleVideoPickerRequest() {
        String mode = VIDEO_MODE;
        // Multimedia File Picker Library by https://github.com/erikagtierrez/multiple-media-picker
        if (mVidListThumb.size() == 0) {
            // no selected pics yet, open mediapicker
            openMediaPicker(mode);
        } else {
            // if in empty imageview, open mediapicker
            // if in imageview with image, open preview and give option to delete
            String idImgView = getResources().getResourceName(mImageViewID);
            String cindex = idImgView.substring(idImgView.lastIndexOf("_")+1);
            int index = Integer.valueOf(cindex);
            mSelectedVideoIndex = index - 1;
            if (mVidListThumb.size() < index) {
                // user click on empty imageview
                openMediaPicker(mode);
            } else {
                // user click on existing bitmap, so open preview and give delete option
                Intent i = new Intent(NewAssetActivity.this, NewAssetVideoPrevActivity.class);
                i.putExtra(SELECTED_VIDEO_PATH, mVidListPath.get(mSelectedVideoIndex));
                startActivityForResult(i, REQUEST_PREVIEW_VIDEO);
            }
        }
    }

    private void handleNewImagePickerResult(List<String> imagePaths) {
        for (String path : imagePaths) {
            mPicListPath.add(path);
            mPicListBitmap.add(Utils.decodeBitmapFromPathResize(path,Constants.DEFAULT_IMAGE_SIZE, Constants.DEFAULT_IMAGE_SIZE));
            Bitmap bitmapThumb = Utils.decodeBitmapFromPathResize(path,Constants.DEFAULT_IMAGE_THUMBSIZE, Constants.DEFAULT_IMAGE_THUMBSIZE);
            mPicListThumb.add(bitmapThumb);
            String name = Constants.IMAGEVIEW_UPLOAD_NAME_PIC + Integer.toString(mPicListBitmap.size());
            int id = getResources().getIdentifier(name, "id", this.getPackageName());
            ImageView imgv = (ImageView) findViewById(id);
            imgv.setImageBitmap(bitmapThumb);
            imgv.setVisibility(View.VISIBLE);
        }
        if(mPicListBitmap.size()<Constants.MAX_UPLOAD_IMAGE) {
            String name = Constants.IMAGEVIEW_UPLOAD_NAME_PIC + Integer.toString(mPicListBitmap.size() + 1);
            int id = getResources().getIdentifier(name, "id", this.getPackageName());
            ImageView imgv = (ImageView) findViewById(id);
            imgv.setImageResource(R.drawable.add_image);
            imgv.setVisibility(View.VISIBLE);
        }
    }

    private void handleNewVideoPickerResult(List<String> videoPaths) {
        for (String path : videoPaths) {

            try {
                mVidListPath.add(path);
                mVidListFis.add(new FileInputStream(path));
                Bitmap thumbVid = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MICRO_KIND);
                mVidListThumb.add(thumbVid);
                String name = Constants.IMAGEVIEW_UPLOAD_NAME_VID + Integer.toString(mVidListFis.size());
                int id = getResources().getIdentifier(name, "id", this.getPackageName());
                ImageView imgv = findViewById(id);
                imgv.setImageBitmap(thumbVid);
                imgv.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Utils.CommonToastDisplayerLong(this, e.getMessage().toString());
            }
        }
        if(mVidListFis.size()<Constants.MAX_UPLOAD_VIDEO) {
            String name = Constants.IMAGEVIEW_UPLOAD_NAME_VID + Integer.toString(mVidListFis.size() + 1);
            int id = getResources().getIdentifier(name, "id", this.getPackageName());
            ImageView imgv = findViewById(id);
            imgv.setImageResource(R.drawable.add_video);
            imgv.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkInput() {

        String title = ((EditText) findViewById(R.id.id_edittext_newasset_title)).getText().toString().trim();
        if(title.isEmpty()) {
            Utils.CommonToastDisplayerLong(this,"Anda belum mengisi Judul Properti.");
            return false;
        }
        //String descr = ((EditText) findViewById(R.id.id_edittext_newasset_desc)).getText().toString().trim();
        if(mDescription == null || mDescription.isEmpty()) {
            Utils.CommonToastDisplayerLong(this, "Anda harus memberikan deskripsi properti.");
            return false;
        }
        String sprice = ((EditText) findViewById(R.id.id_edittext_newasset_price)).getText().toString().trim();
        if(sprice.isEmpty()) {
            Utils.CommonToastDisplayerLong(this,"Anda belum mengisi harga properti.");
            return false;
        }

        return true;
    }

    private void saveDataToParse2() {
        // compress video

        Intent intent = new Intent();
        intent.putExtra(MainActivity.IMAGE_UPLOAD_PATH, mPicListPath);
        intent.putExtra(MainActivity.VIDEO_UPLOAD_PATH, mVidListPath);
//        intent.putExtra(Constants.ASSET_OBJECT, asset);
        intent.putExtra(MainActivity.MAP_BITMAP, mMapImage);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * ATTENTION: THIS CLASS POTENTIALLY CAN CAUSE MEMORY LEAK
     * Need to refactor this class in the future since no WeakReference used for now
     */
    private class AsyncCompressVideo extends AsyncTask<List<String>, Integer, ArrayList<String>> {

        Context context;
        String destPath;

        public AsyncCompressVideo(Context context, String destPath) {
            this.context = context;
            this.destPath = destPath;
        }

        @Override
        protected ArrayList<String> doInBackground(List<String>... lists) {
            List<String> sourcePaths = lists[0];
            ArrayList<String> destPaths = new ArrayList<>();
            try {
                int n = 0;
                for (String sourcePath : sourcePaths) {
                    String filepath = SiliCompressor.with(context).compressVideo(sourcePath, destPath);
                    destPaths.add(filepath);
                    n = n+1;
                    publishProgress(n);
                }
            } catch(URISyntaxException e) {

            }
            return destPaths;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Toasty.info(context, "Video " + values[0] + " selesai diproses" );
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            mVidListPath = strings;
            stopProgressDialog();
            saveDataToParse();
        }
    }

    private void compressVideos() {
        startProgressDialog("Mohon tunggu", "Sedang memproses video...");
        String storageDir = Environment.getExternalStorageDirectory().getPath() + File.separator + "Depronesia" + File.separator;
        File dir = new File(storageDir);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        new AsyncCompressVideo(getApplicationContext(), storageDir).execute(mVidListPath);
    }

    private void saveDataToParse() {
        // onSave
        // oke, now we prepare to upload
        // upload is done using Service, as best practice in Android
        // But
        // even the upload is in background, User on this activity should not be killed during upload
        // so we should put correct handle onPause and onResume

        // first, we need to create the Asset so that if uploading Video and Image are failed,
        // user doesn't need to re-write everything again from the scratch
        // later user can also edit the Asset created
        startProgressDialog("Mohon tunggu", "Sedang menayangkan iklan Anda...");
        final NewAssetActivity newAssetActivity = this;
        final Asset asset = createAsset();
        asset.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null) {
                    // prepare the Intents to be grabbed by AssetUploadService,
                    // and finally call the MainActivity by finish this Activity
                    // MainActivity will receive the event of finsihing this Activity,
                    // then MainActivity will launch the Service
                    stopProgressDialog();
                    // update number of asset
                    int numAsset = ParseUser.getCurrentUser().getInt(Constants.ParseTable.TableUser.NUMBER_OF_ASSET);
                    ParseUser.getCurrentUser().put(Constants.ParseTable.TableUser.NUMBER_OF_ASSET, numAsset + 1);
                    ParseUser.getCurrentUser().saveInBackground();
                    Intent intent = new Intent();
                    intent.putExtra(MainActivity.IMAGE_UPLOAD_PATH, mPicListPath);
                    intent.putExtra(MainActivity.VIDEO_UPLOAD_PATH, mVidListPath);
                    intent.putExtra(Constants.ASSET_OBJECT, asset);
                    intent.putExtra(MainActivity.MAP_BITMAP, mMapImage);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    // ups... think internet is slow, or we have internal error
                    // no need to start service for uploading. Just ask the user to click save again
                    Log.d("MYNEWASSET", e.getMessage().toString());
                    stopProgressDialog();
                    Utils.CommonAlertOkDisplayer(newAssetActivity,
                            "Deproo",
                            "Terjadi kesalahan atau internet lambat, mohon coba kembali");
                }
            }
        });

    }

    private void startProgressDialog(String title, String msg) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    private void stopProgressDialog() {
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    public Asset createAsset() {
        // onSave
        Asset asset = new Asset();

        String title = ((EditText) findViewById(R.id.id_edittext_newasset_title)).getText().toString().trim();

        String descr = mDescription;
        if(descr.isEmpty() || descr==null) descr = "";

        String address = ((EditText) findViewById(R.id.id_edittext_newasset_address)).getText().toString().trim();
        if(address.isEmpty() || address==null) address="";

        String city = ((EditText) findViewById(R.id.id_edittext_newasset_city)).getText().toString().trim();
        if(city.isEmpty() || city==null) city="";

        String province = ((EditText) findViewById(R.id.id_edittext_newasset_province)).getText().toString().trim();
        if(province.isEmpty() || province==null) province="";

        // rumah, kantor, apart, tanah
        String category = ((Spinner) findViewById(R.id.id_spinner_category)).getSelectedItem().toString();

        // apart category
        String apartCategory = "";
        if(category.equalsIgnoreCase("apartemen")) {
            apartCategory = ((Spinner) findViewById(R.id.id_spinner_category_apart)).getSelectedItem().toString();
        }

        // sewa/jual
        String pServeType = ((Spinner) findViewById(R.id.id_spinner_pservetype)).getSelectedItem().toString();

        // hari, bulan, tahun
        String pServeTime = ((Spinner) findViewById(R.id.id_spinner_pservetime)).getSelectedItem().toString();

        // 1,2,3,4 ...12
        String pServeNumString = ((Spinner) findViewById(R.id.id_spinner_pservenum)).getSelectedItem().toString();
        int pServeNum = Integer.parseInt(pServeNumString);

        String priceString = ((EditText) findViewById(R.id.id_edittext_newasset_price)).getText().toString().trim();
        long price = 0;
        if(!priceString.isEmpty())
            price = Long.parseLong(priceString);

        String sizeLandString = ((EditText) findViewById(R.id.id_edittext_newasset_luas_tanah)).getText().toString().trim();
        double sizeLand = 0;
        if(!sizeLandString.isEmpty())
            sizeLand = Double.parseDouble(sizeLandString);

        String sizeHouseString = ((EditText) findViewById(R.id.id_edittext_newasset_luas_bangunan)).getText().toString().trim();
        double sizeHouse = 0;
        if(!sizeHouseString.isEmpty())
            sizeHouse = Double.parseDouble(sizeHouseString);

        String numBedString = ((EditText) findViewById(R.id.id_edittext_newasset_bednum)).getText().toString().trim();
        int numBed = 0;
        if(!numBedString.isEmpty())
            numBed = Integer.parseInt(numBedString);

        String numBathString = ((EditText) findViewById(R.id.id_edittext_newasset_bathnum)).getText().toString().trim();
        int numBath = 0;
        if(!numBathString.isEmpty())
            numBath = Integer.parseInt(numBathString);

        String numGarageString = ((EditText) findViewById(R.id.id_edittext_newasset_garagenum)).getText().toString().trim();
        int numGarage = 0;
        if(!numGarageString.isEmpty())
            numGarage = Integer.parseInt(numGarageString);

        String numFloorString = ((EditText) findViewById(R.id.id_edittext_newasset_floornum)).getText().toString().trim();
        int numFloor = 1;
        if(!numFloorString.isEmpty())
            numFloor = Integer.parseInt(numFloorString);

        String sWatt = ((EditText) findViewById(R.id.id_edittext_newasset_watt)).getText().toString().trim();
        int watt = 0;
        if(!sWatt.isEmpty())
            watt = Integer.parseInt(numFloorString);

        String stSHM = "";
        String shm = ((EditText) findViewById(R.id.id_edittext_newasset_shm)).getText().toString().trim();
        if(!shm.isEmpty())
            stSHM = shm;


        ParseGeoPoint geoPoints = mGeopoints;
        boolean isNearWorshipPlace = ((CheckBox) findViewById(R.id.id_checkbox_wshp)).isChecked();
        boolean isNearHealth = ((CheckBox) findViewById(R.id.id_checkbox_health)).isChecked();
        boolean isNearHway = ((CheckBox) findViewById(R.id.id_checkbox_highway)).isChecked();
        boolean isNearMarket = ((CheckBox) findViewById(R.id.id_checkbox_market)).isChecked();
        boolean isNearPublicCluster = ((CheckBox) findViewById(R.id.id_checkbox_public)).isChecked();
        boolean isNearTourist = ((CheckBox) findViewById(R.id.id_checkbox_tourist)).isChecked();
        boolean isNearToll = ((CheckBox) findViewById(R.id.id_checkbox_toll)).isChecked();
        boolean isSwimpool = ((CheckBox) findViewById(R.id.id_checkbox_swimpool)).isChecked();

        asset.setDefaultValue();
        asset.setTitle(title);
        asset.setDescription(descr);
        asset.setCategory(category);
        asset.setPrice(price);
        asset.setSizeland(sizeLand);
        asset.setSizehouse(sizeHouse);
        asset.setNumBathroom(numBath);
        asset.setNumBedroom(numBed);
        asset.setLocation(geoPoints);
        asset.setNearWorship(isNearWorshipPlace);
        asset.setNearCluster(isNearPublicCluster);
        asset.setNearHealth(isNearHealth);
        asset.setNearHighway(isNearHway);
        asset.setNearMarket(isNearMarket);
        asset.setNearTour(isNearTourist);
        asset.setAddress(address);
        asset.setCity(city);
        asset.setProvince(province);
        asset.setOwner(ParseUser.getCurrentUser());
        asset.setNumGarage(numFloor);
        asset.setNumFloor(numFloor);
        asset.setNumGarage(numGarage);
        asset.setNearToll(isNearToll);
        asset.setElectricity(watt);
        asset.setHakMilik(stSHM);
        asset.setSwimPool(isSwimpool);
        asset.setTypeApart(apartCategory);

        asset.setTypePServe(pServeType);
        if(pServeType.equalsIgnoreCase("SEWA")) {
            asset.setTimePServe(pServeTime);
            asset.setNumPServe(pServeNum);
        }

        return asset;
    }

    private void alertConfirmSave(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(NewAssetActivity.this)
//                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Cek Kembali", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if(checkInput()) {
                            if(mVidListPath.size()>0)
                                compressVideos();
                            else
                                saveDataToParse();
                        }
                    }
                });

        AlertDialog ok = builder.create();
        ok.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewAssetActivity.this)
//                .setTitle("Deproo")
                .setMessage("Batal memasang iklan Asset Anda?")
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Ya, batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        onBackPressedCustom();
                    }
                });

        AlertDialog ok = builder.create();
        ok.show();
    }

    private void onBackPressedCustom() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
