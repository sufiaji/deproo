package com.deproo.android.deproo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.application.DeprooApplication;
import com.deproo.android.deproo.fragment.FavouriteFragment;
import com.deproo.android.deproo.fragment.HomeFragment;
import com.deproo.android.deproo.fragment.InboxFragment;
import com.deproo.android.deproo.fragment.LoginFragment;
import com.deproo.android.deproo.fragment.NewsFragment;
import com.deproo.android.deproo.fragment.ProfileFragment;
import com.deproo.android.deproo.model.Asset;
import com.deproo.android.deproo.model.AssetUploadService;
import com.deproo.android.deproo.model.Broker;
import com.deproo.android.deproo.utils.Constants;
import com.deproo.android.deproo.utils.Utils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String VIDEO_UPLOAD_PATH = "VIDEO_UPLOAD_PATH";
    public static final String IMAGE_UPLOAD_PATH = "IMAGE_UPLOAD_PATH";
    public static final String MAP_BITMAP = "MAP_BITMAP";
    private final int REQUEST_CREATE_ASSET = 9002;
    private final int REQUEST_GOOGLE_SIGN = 9001;

    private Fragment mFragment;
    private GoogleSignInClient mGoogleSignInClient;
    private List<String> mImageUploadPaths = new ArrayList<>();
    private List<String> mVideoUploadPaths = new ArrayList<>();
    private int mUploadSequenceImage;
    private int mUploadSequenceVideo;
    private Asset mAsset;
    BottomNavigationViewEx mBtnNav;
    private Context mContext;
    private int mSelectedFrame = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set Layout
        setContentView(R.layout.activity_main);
        /*
        register Receiver for Upload Iklan
        */
        IntentFilter intentFilter = new IntentFilter(AssetUploadService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(MainBroadcastReceiver, intentFilter);
        // display bottom Tab
        mBtnNav = findViewById(R.id.id_navigation1);
        mBtnNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBtnNav.enableShiftingMode(false);
        mBtnNav.enableItemShiftingMode(false);
        // Hide Action Bar
        getSupportActionBar().hide();
        // load Fragment
        HomeFragment fragment = new HomeFragment();
        loadFragment(fragment);
        mContext = this;
        // get data from parse
//        initData();
    }

    private void loadFragment(Fragment fragment) {
        /*
        @@Deprecated
         */
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    if(mSelectedFrame==0) return true;
                    getSupportActionBar().hide();
                    mFragment = new HomeFragment();
                    loadFragment(mFragment);
                    mSelectedFrame = 0;
                    return true;
                case R.id.nav_newsfeed:
                    if(mSelectedFrame==1) return true;
                    getSupportActionBar().setTitle(R.string.nav_newsfeed);
                    getSupportActionBar().show();
                    mFragment = new NewsFragment();
                    loadFragment(mFragment);
                    mSelectedFrame = 1;
                    return true;
                case R.id.nav_profile:
                    if(mSelectedFrame==4) return true;
                    getSupportActionBar().hide();
                    if(ParseUser.getCurrentUser() != null) {
                        if(ParseUser.getCurrentUser().getBoolean("emailVerified")==false) {
                            mFragment = new LoginFragment();

                        } else {
                            mFragment = new ProfileFragment();
                        }
                    } else {
                        mFragment = new LoginFragment();
                    }
                    loadFragment(mFragment);
                    mSelectedFrame = 4;
                    return true;
                case R.id.nav_inbox:
                    if(mSelectedFrame==2) return true;
                    getSupportActionBar().setTitle(R.string.nav_inbox);
                    getSupportActionBar().show();
                    mFragment = new InboxFragment();
                    loadFragment(mFragment);
                    mSelectedFrame = 2;
                    return true;
                case R.id.nav_favourite:
                    if(mSelectedFrame==3) return true;
                    getSupportActionBar().setTitle(R.string.nav_favourite);
                    getSupportActionBar().show();
                    mFragment = new FavouriteFragment();
                    loadFragment(mFragment);
                    mSelectedFrame = 3;
                    return true;
            }

            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBrokerClick(Broker user) {
        if(ParseUser.getCurrentUser()!=null) {
            if (!user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                Intent intent = new Intent(MainActivity.this, BrokerProfileActivity.class);
                intent.putExtra(Constants.BROKER_OBJECT, user);
                DeprooApplication.sharedProfilePic = user.getProfilePicThumb();
                startActivity(intent);
            } else {
                ProfileFragment fragment = new ProfileFragment();
                loadFragment(fragment);
                mSelectedFrame = 4;
                mBtnNav.getMenu().getItem(4).setChecked(true);
            }
        } else {
            Intent intent = new Intent(MainActivity.this, BrokerProfileActivity.class);
            intent.putExtra(Constants.BROKER_OBJECT, user);
            DeprooApplication.sharedProfilePic = user.getProfilePicThumb();
            startActivity(intent);
        }
    }

    public void onAssetClick(Asset asset) {
        Uri uri = asset.getImageUri();
        if(uri!=null && asset!=null) {
            Intent intent = new Intent(MainActivity.this, AssetDetailActivity.class);
            intent.putExtra(Constants.ASSET_OBJECT, asset);
            intent.putExtra(Constants.URI, asset.getImageUri().toString());
            startActivity(intent);
        }
    }

    public void onSeeAllAssetClick(View view) {
        Intent intent = new Intent(MainActivity.this, AssetListActivity.class);
        startActivity(intent);
    }

    public void onSeeAllBrokerClick(View view) {
        Intent intent = new Intent(MainActivity.this, BrokerListActivity.class);
        startActivity(intent);
    }

    public void onEditProfileClick(View v) {
        Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void onClickCategory(View v) {

    }

    public void onSmall1Click(View view) {
        Intent intent = new Intent(MainActivity.this, AssetListActivity.class);
        intent.putExtra(Constants.FILTER_CAT, "Rumah");
        intent.putExtra(Constants.FILTER_PSERVECAT, "Jual");
        startActivity(intent);
    }

    public void onSmall2Click(View view) {
        Intent intent = new Intent(MainActivity.this, AssetListActivity.class);
        intent.putExtra(Constants.FILTER_CAT, "Rumah");
        intent.putExtra(Constants.FILTER_PSERVECAT, "Sewa");
        startActivity(intent);
    }

    public void onSmall3Click(View view) {
        Intent intent = new Intent(MainActivity.this, AssetListActivity.class);
        intent.putExtra(Constants.FILTER_CAT, "Apartemen");
        intent.putExtra(Constants.FILTER_PSERVECAT, "Jual");
        startActivity(intent);
    }

    public void onSmall4Click(View view) {
        Intent intent = new Intent(MainActivity.this, AssetListActivity.class);
        intent.putExtra(Constants.FILTER_CAT, "Apartemen");
        intent.putExtra(Constants.FILTER_PSERVECAT, "Sewa");
        startActivity(intent);
    }

    public void onSmall5Click(View view) {
        Intent intent = new Intent(MainActivity.this, AssetListActivity.class);
        intent.putExtra(Constants.FILTER_CAT, "Tanah");
        startActivity(intent);
    }

    public void onSmall6Click(View view) {
        Intent intent = new Intent(MainActivity.this, AssetListActivity.class);
        intent.putExtra(Constants.FILTER_CAT, "Kantor");
        startActivity(intent);
    }

    public void onSmall7Click(View view) {
        Intent intent = new Intent(MainActivity.this, AssetListActivity.class);
        intent.putExtra(Constants.FILTER_CAT, "Ruko");
        startActivity(intent);
    }

    public void onSmall8Click(View view) {
        Intent intent = new Intent(MainActivity.this, AssetListActivity.class);
        intent.putExtra(Constants.FILTER_CAT, "Villa");
        startActivity(intent);
    }

    public void onSmall9Click(View view) {
        Intent intent = new Intent(MainActivity.this, AssetListActivity.class);
        intent.putExtra(Constants.FILTER_CAT, "Townhouse");
        startActivity(intent);
    }

    public void onSmall10Click(View view) {
        Intent intent = new Intent(MainActivity.this, AssetListActivity.class);
        intent.putExtra(Constants.FILTER_CAT, "Gudang");
        startActivity(intent);
    }

    public void onSmall11Click(View view) {
        Intent intent = new Intent(MainActivity.this, BrokerListActivity.class);
        startActivity(intent);
    }

    public void onLoginClick(View v) {
        EditText editTextEmail = (EditText) findViewById(R.id.id_edittext_email);
        EditText editTextPassword = (EditText) findViewById(R.id.id_edittext_password);
        String emailString = editTextEmail.getText().toString().toLowerCase().trim();
        String passwordString = editTextPassword.getText().toString();
        if(emailString.isEmpty() || passwordString.isEmpty()) {
            Toast.makeText(this,"Email/Password tidak boleh kosong", Toast.LENGTH_LONG).show();
            return;
        }
        if (!Utils.ValidateEmail(emailString)) {
            Toast.makeText(this,"Email tidak valid", Toast.LENGTH_LONG).show();
            return;
        }
        ParseUser.logInInBackground(emailString, passwordString, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    if(user.getBoolean("emailVerified")) {
                        // Hooray! The user is logged in.
                        alertDisplayerOk("Deproo", "Selamat datang, " + user.getUsername());
                    } else {
                        ParseUser.logOut();
                        alertDisplayerOk("Deproo", "Mohon lakukan konfirmasi akun Anda melalui email.");
                    }

                } else {
                    // Sigin failed. Look at the ParseException to see what happened.
                    ParseUser.logOut();
                    Toast.makeText(getApplicationContext(),"Terjadi kesalahan, email/password salah?", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void onFacebookLoginClick(View v) {
        //((LoginFragment) mFragment).onFacebookLogin(v);
        Intent intent = new Intent(MainActivity.this, LoginFacebookActivity.class);
        startActivity(intent);
    }

    ProgressDialog mDlg;
    public void onGoogleLoginClick(View v) {
        mDlg = new ProgressDialog(MainActivity.this);
        mDlg.setTitle("Mohon tunggu");
        mDlg.setMessage("Sedang masuk menggunakan akun Facebook...");
        mDlg.show();
        //
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_GOOGLE_SIGN);
    }

    public void onForgotPasswordClick(View v) {

    }

    public void onRegisterClick(View v) {
        //((LoginFragment) mFragment).onRegister(v);
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void onSettingClick(View v) {
        Intent intent = new Intent(MainActivity.this, HomeFacebookActivity.class);
        startActivity(intent);
    }

    public void onLogoutClick(View v) {
        alertLogout("Deproo", "Apakah Anda yakin ingin keluar?");
    }

    public void onPasangIklanClick(View v) {
        Intent intent = new Intent(MainActivity.this, NewAssetActivity.class);
        startActivityForResult(intent, REQUEST_CREATE_ASSET);
    }

    private void alertLogout(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
//                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        final ProgressDialog dlg = new ProgressDialog(MainActivity.this);
                        dlg.setTitle("Mohon tunggu");
                        dlg.setMessage("Keluar dari Deproo...");
                        dlg.show();

                        // get current user data
                        ParseUser user = ParseUser.getCurrentUser();
                        String loginWith = (String) user.get(Constants.ParseTable.TableUser.LOGIN_WITH);

                        // logging out of Facebook
                        if(loginWith.contains("facebook")) {
                            LoginManager.getInstance().logOut();
                            ParseUser.logOut();
                            Toast.makeText(getApplicationContext(), "Anda sudah berhasil keluar", Toast.LENGTH_SHORT).show();
                        } else if (loginWith.contains("email")) {
                            ParseUser.logOut();
                            Toast.makeText(getApplicationContext(), "Anda sudah berhasil keluar", Toast.LENGTH_SHORT).show();
                        } else if (loginWith.contains("google")) {
                            mGoogleSignInClient.signOut()
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            ParseUser.logOut();
                                            Toast.makeText(getApplicationContext(), "Anda sudah berhasil keluar (Google)", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        // logging out of Parse

                        //
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dlg.dismiss();
                        //
                        finish();
                    }
                });

        AlertDialog ok = builder.create();
        ok.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                /*String name = account.getDisplayName();
                String email = account.getEmail();
                String id = account.getId();*/
                String idToken = account.getIdToken();
                // pass idToken and google client id to cloud code for backend validation on google server
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("GClientId", getString(R.string.google_client_id));
                params.put("idToken", idToken);
                ParseCloud.callFunctionInBackground("ValidateGoogleToken", params, new FunctionCallback<Map<String, Object>>() {
                    @Override
                    public void done(Map<String, Object> mapObject, ParseException e) {

                        mDlg.dismiss();
                        if(e==null) {
                            String status = mapObject.get("status").toString();
                            if(status != null) {
                                if(status.equals("OK")) {
                                    final String username = mapObject.get("username").toString();
                                    /*String email = mapObject.get("email").toString();
                                    String objectId = mapObject.get("objectId").toString();*/
                                    String password = mapObject.get("password").toString();
                                    final String type = mapObject.get("type").toString();
                                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                                        @Override
                                        public void done(ParseUser user, ParseException e) {
                                            if(e==null) {
                                                if(type.equals("new")) {
                                                    alertDisplayerOk("Deproo", "Selamat bergabung di Deproo");
                                                } else if (type.equals("exist")) {
                                                    alertDisplayerOk("Deproo", "Selamat datang kembali, " + user.getUsername());
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Gagal masuk menggunakan Google Account: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else if (status.equals("ERROR")) {
                                    Toast.makeText(getApplicationContext(), "Gagal masuk menggunakan Google Account: Status Error", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Gagal masuk menggunakan Google Account: Status Undefined.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            mGoogleSignInClient.signOut();
                            Toast.makeText(getApplicationContext(), "Gagal masuk menggunakan Google Account: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    }

                });

            } catch (ApiException e) {
                Toast.makeText(this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == REQUEST_CREATE_ASSET && resultCode == RESULT_OK && data != null) {
            // grab Intent data from NewAssetActivity
            List<String> listImages = data.getExtras().getStringArrayList(IMAGE_UPLOAD_PATH);
            List<String> listVideos = data.getExtras().getStringArrayList(VIDEO_UPLOAD_PATH);
            Asset asset = data.getExtras().getParcelable(Constants.ASSET_OBJECT);
            // prepare for launching the Service (to upload images & videos to Parse)
            startUpload(asset,listImages,listVideos);
        }
    }

    public void startUpload(Asset asset, List<String> imagePaths, List<String> videoPaths) {
        mImageUploadPaths.clear();
        mVideoUploadPaths.clear();
        mImageUploadPaths = imagePaths;
        List<String> newPaths = compressVideo(videoPaths);
        mVideoUploadPaths = videoPaths;
        mAsset = asset;
        mUploadSequenceImage = 0;
        mUploadSequenceVideo = 0;
        // launching the Service
        uploadAssetObject();
    }

    private void uploadAssetObject() {
        if(mImageUploadPaths.size()>0) {
            Log.d("MYSERVICE", "uploadAssetObject Image " + Integer.toString(mUploadSequenceImage));
            Intent intent = new Intent(this, AssetUploadService.class);
            intent.putExtra(AssetUploadService.SEQUENCE, mUploadSequenceImage);
            intent.putExtra(AssetUploadService.PATH, mImageUploadPaths.get(0));
            intent.putExtra(AssetUploadService.TYPE, AssetUploadService.TYPEIMAGE);
            intent.putExtra(AssetUploadService.ASSET_OBJECT, mAsset);
            startService(intent); // run the Service
            mImageUploadPaths.remove(0);
            mUploadSequenceImage = mUploadSequenceImage + 1;
        } else if(mVideoUploadPaths.size()>0) {
            Log.d("MYSERVICE", "uploadAssetObject Video " + Integer.toString(mUploadSequenceImage));
            Intent intent = new Intent(this, AssetUploadService.class);
            intent.putExtra(AssetUploadService.SEQUENCE, mUploadSequenceVideo);
            intent.putExtra(AssetUploadService.PATH, mVideoUploadPaths.get(0));
            intent.putExtra(AssetUploadService.TYPE, AssetUploadService.TYPEVIDEO);
            intent.putExtra(AssetUploadService.ASSET_OBJECT, mAsset);
            startService(intent); // run the Service
            mVideoUploadPaths.remove(0);
            mUploadSequenceVideo = mUploadSequenceVideo + 1;
        }

    }

    private BroadcastReceiver MainBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.CommonToastDisplayerLong(getApplicationContext(), "Broadcast is happening...");
            int resultCode = intent.getIntExtra(AssetUploadService.RESULTCODE, AssetUploadService.ER_CODE);
            if(resultCode == AssetUploadService.OK_CODE) {
//                Log.d("MYSERVICE", "BroadcastReceiver Ok");
//                Utils.CommonToastDisplayerShort(mContext, "BroadcastReceiver Ok");
//                String resultValue = intent.getStringExtra(AssetUploadService.RESULTVALUE);
                uploadAssetObject();
            } else if(resultCode == AssetUploadService.ER_CODE) {
//                Utils.CommonToastDisplayerShort(mContext, "BroadcastReceiver Error");
//                Log.d("MYSERVICE", "BroadcastReceiver Error");
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        IntentFilter intentFilter = new IntentFilter(AssetUploadService.ACTION);
//        LocalBroadcastManager.getInstance(this).registerReceiver(MainBroadcastReceiver, intentFilter);
//        IntentFilter intentFilter2 = new IntentFilter(GetDataService.ACTION);
//        LocalBroadcastManager.getInstance(this).registerReceiver(MainBroadcastReceiver, intentFilter2);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(MainBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*
        we better never unregister the Receiver, because when user pressing back button
        this method will be called
        Once this method is called, any pending task of upload will not be executed
         */
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(MainBroadcastReceiver);
    }

    private void alertDisplayerOk(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
//                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
//                .setTitle("Deproo")
                .setMessage("Keluar aplikasi Deproo?")
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        onBackPressedCustom();
                    }
                });

        AlertDialog ok = builder.create();
        ok.show();
    }

    public void onUserClick(View view) {
        // need to go to Fragment Profile, not calling broker profile public
//        ProfileFragment fragment = new ProfileFragment();
//        loadFragment(fragment);
//        mBtnNav.getMenu().getItem(4).setChecked(true);
        HashMap<String, Object> params = new HashMap<>();
        ParseCloud.callFunctionInBackground("pushsample", params);
    }

    private void onBackPressedCustom() {
        super.onBackPressed();
    }

    private List<String> compressVideo(List<String> videoPaths) {
        List<String> paths = new ArrayList<>();
        ContentResolver resolver = getContentResolver();
        final File file;
        final ParcelFileDescriptor parcelFileDescriptor;
        return paths;
    }

}
