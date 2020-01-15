package com.deproo.android.deproo.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.utils.Constants;
import com.deproo.android.deproo.utils.Utils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import io.github.pierry.progress.Progress;

public class EditProfileActivity extends AppCompatActivity {

    private Spinner mSpinnerGender;
    private static final int REQUEST_PERMISSION_CAM_STORAGE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private EditText mEditNama;
    private EditText mEditEmail;
    private EditText mEditPhone;
    private EditText mEditAddress;
    private EditText mEditBio1;
    private EditText mEditBio2;
    private EditText mEditCity;
    private EditText mEditProvince;
    private String mEmail;
    private boolean mPicProfileAssigned;
    private boolean mPicBgAssigned;
    private ParseUser mCurrentUser;
    private ProgressDialog mProgress;
    private int mRotation;
//    Progress mProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle(R.string.title_edit_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mSpinnerGender = (Spinner) findViewById(R.id.id_spinner_gender);
        mEditNama = (EditText) findViewById(R.id.id_edit_profile_name);
        mEditEmail = (EditText) findViewById(R.id.id_edit_profile_email);
        mEditPhone = (EditText) findViewById(R.id.id_edit_profile_phone);
        mEditAddress = (EditText) findViewById(R.id.id_edit_profile_address);
        mEditBio1 = (EditText) findViewById(R.id.id_edit_profile_bio1);
        mEditBio2 = (EditText) findViewById(R.id.id_edit_profile_bio2);
        mEditCity = (EditText) findViewById(R.id.id_edit_profile_city);
        mEditProvince = (EditText) findViewById(R.id.id_edit_profile_province);


        String nama = ParseUser.getCurrentUser().getString(Constants.ParseTable.TableUser.LONGNAME);
        mEditNama.setText(nama);
        mEmail = ParseUser.getCurrentUser().getEmail().toLowerCase();
        mEditEmail.setText(mEmail);
        String phone = ParseUser.getCurrentUser().getString(Constants.ParseTable.TableUser.PHONE);
        mEditPhone.setText(phone);
        String address = ParseUser.getCurrentUser().getString(Constants.ParseTable.TableUser.ADDRESS);
        mEditAddress.setText(address);
        String bio1 = ParseUser.getCurrentUser().getString(Constants.ParseTable.TableUser.BIO1);
        mEditBio1.setText(bio1);
        String bio2 = ParseUser.getCurrentUser().getString(Constants.ParseTable.TableUser.BIO2);
        mEditBio2.setText(bio2);

        String city = ParseUser.getCurrentUser().getString(Constants.ParseTable.TableUser.CITY);
        mEditCity.setText(city);
        String province = ParseUser.getCurrentUser().getString(Constants.ParseTable.TableUser.PROVINCE);
        mEditProvince.setText(province);

        String gender = ParseUser.getCurrentUser().getString(Constants.ParseTable.TableUser.GENDER);
        List<String> genderList = new ArrayList<>();
        genderList.add("-");
        genderList.add("Pria");
        genderList.add("Wanita");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, genderList);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerGender.setAdapter(dataAdapter);
        if(TextUtils.isEmpty(gender))
            mSpinnerGender.setSelection(0);
        else if(gender.equals("Pria"))
            mSpinnerGender.setSelection(1);
        else mSpinnerGender.setSelection(2);

        mPicProfileAssigned = false;
        mPicBgAssigned = false;

        displayImageProfile();

    }

    public void onBgClick(View v) {
        // not-used for now...
        if (Build.VERSION.SDK_INT < 23) {
            openMediaPicker();
        } else {
            if(checkPermission())
                openMediaPicker();
        }
    }

    public void onPicClick(View v) {
        if (Build.VERSION.SDK_INT < 23) {
            openMediaPicker();
        } else {
            if(checkPermission())
                openMediaPicker();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void displayImageProfile() {
        try {
            ParseFile parseFile = ParseUser.getCurrentUser().getParseFile(Constants.ParseTable.TableUser.PROFILE_THUMB);
            byte[] data = parseFile.getData();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            ImageView v1 = (ImageView) findViewById(R.id.id_img_profile);
            v1.setImageBitmap(bitmap);
//            parseFile = ParseUser.getCurrentUser().getParseFile(Constants.ParseTable.TableUser.PROFILE_BG);
//            data = parseFile.getData();
//            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            mBgImage.setImageBitmap(bitmap);
        } catch(Exception e) {
            //Utils.CommonToastDisplayerLong(getActivity(), e.getMessage().toString());
        }
    }

    private boolean checkPermission() {
        int permissionReadStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

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

        if (listPermissionNeeded.size()>0) {
            ActivityCompat.requestPermissions(
                    this,
                    listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]),
                    REQUEST_PERMISSION_CAM_STORAGE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CAM_STORAGE: {
                if (grantResults.length > 0) {
                    boolean allowed = true;
                    for(int i=0;i<grantResults.length;i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                            allowed = false;
                    }
                    if(allowed) {
                        openMediaPicker();
                    } else {
                        String msg;
                        msg = "Tidak dapat mengambil gambar karena tidak diperbolehkan.";
                        Utils.CommonToastDisplayerLong(this,msg);
                    }
                }
                return;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void openMediaPicker() {
            /*intent.putExtra("title", "Pilih Gambar");
            intent.putExtra("maxSelection", Constants.MAX_UPLOAD_IMAGE - mPicListThumb.size());
            startActivityForResult(intent, REQUEST_PICK_IMAGE);*/
            Matisse.from(EditProfileActivity.this)
                .choose(MimeType.ofImage())
                .showSingleMediaType(true)
                .capture(true)
                .captureStrategy(new CaptureStrategy(true,"com.zhihu.matisse.sample.fileprovider"))
                .forResult(REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            // From Image Picker New Image
            handleNewImagePickerResult(Matisse.obtainPathResult(data));
        }
    }

    private void handleNewImagePickerResult(List<String> imagePaths) {
        for (String path : imagePaths) {
            ExifInterface exif;
            int orientation = 0;
            try {
                exif = new ExifInterface(path);
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int rotation = 0;
            if(orientation==ExifInterface.ORIENTATION_ROTATE_90) rotation = 90;
            else if(orientation==ExifInterface.ORIENTATION_ROTATE_180) rotation = 180;
            else if(orientation==ExifInterface.ORIENTATION_ROTATE_270) rotation = 270;
            mRotation = rotation;

            Bitmap b = Utils.decodeBitmapFromPathResize(path,Constants.DEFAULT_IMAGE_PROFILESIZE,
                    Constants.DEFAULT_IMAGE_PROFILESIZE);
            ImageView imageView = findViewById(R.id.id_img_profile);
            imageView.setImageBitmap(b);
            imageView.setRotation(rotation);
            break;
        }
        mPicProfileAssigned = true;
    }

    public void onSaveProfileClick(View v) {
        String nama = mEditNama.getText().toString().trim();
        String email = mEditEmail.getText().toString().trim().toLowerCase();
        String telp = mEditPhone.getText().toString().trim();
        String address = mEditAddress.getText().toString().trim();
        String bio1 = mEditBio1.getText().toString().trim();
        String bio2 = mEditBio2.getText().toString().trim();
        String city = mEditCity.getText().toString().trim();
        String province = mEditProvince.getText().toString().trim();

        String gender = mSpinnerGender.getSelectedItem().toString();

        // validate email first
        if(!Utils.ValidateEmail(email)) {
            Toast.makeText(this,"Email tidak valid", Toast.LENGTH_LONG).show();
            return;
        }

        // show progress dialog
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Menyimpan perubahan...");
        mProgress.show();
        mCurrentUser = ParseUser.getCurrentUser();
        mCurrentUser.put(Constants.ParseTable.TableUser.LONGNAME,nama);
        if(!email.equalsIgnoreCase(mEmail)) {
            mCurrentUser.setEmail(email);
        }
        mCurrentUser.put(Constants.ParseTable.TableUser.GENDER, gender);
        mCurrentUser.put(Constants.ParseTable.TableUser.ADDRESS, address);
        mCurrentUser.put(Constants.ParseTable.TableUser.PHONE, telp);
        mCurrentUser.put(Constants.ParseTable.TableUser.BIO1, bio1);
        mCurrentUser.put(Constants.ParseTable.TableUser.BIO2, bio2);
        mCurrentUser.put(Constants.ParseTable.TableUser.CITY, city);
        mCurrentUser.put(Constants.ParseTable.TableUser.PROVINCE, province);

        // save Parsefile first
        if(mPicProfileAssigned) {
            saveFile();
        } else {
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    mProgress.dismiss();
                    if(e==null) {
                        Toast.makeText(getApplicationContext(), "Profile berhasil disimpan", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Profile tidak berhasil disimpan, silahkan coba kembali",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void saveFile() {
        Bitmap b1;
        ImageView v1 = (ImageView) findViewById(R.id.id_img_profile);
        b1 = ((BitmapDrawable) v1.getDrawable()).getBitmap();
        b1 = Utils.RotateBitmap(b1,(float) mRotation);
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        b1.compress(Bitmap.CompressFormat.JPEG, Constants.JPG_COMPRESSION_QUALITY_MEDIUM, stream1);
        byte[] data1 = stream1.toByteArray();
        String thumbName1 = mCurrentUser.getUsername().replaceAll("\\s+", "");
        final ParseFile pf1 = new ParseFile(thumbName1 + "_thumb.jpg", data1);
        pf1.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mProgress.dismiss();
                if(e==null) {
                    mCurrentUser.put(Constants.ParseTable.TableUser.PROFILE_THUMB, pf1);
                    mCurrentUser.saveEventually();
                    Toast.makeText(getApplicationContext(), "Profile berhasil disimpan", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Profile tidak berhasil disimpan, silahkan coba kembali",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
