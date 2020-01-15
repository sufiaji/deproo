package com.deproo.android.deproo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.utils.Constants;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;

public class LoginFacebookActivity extends AppCompatActivity {

    private String mEmail;
    private String mName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseFacebookUtils.initialize(this);
        setContentView(R.layout.activity_login_facebook);
    }

    public void onLoginClick(View v) {
        final ProgressDialog dlg = new ProgressDialog(LoginFacebookActivity.this);
        dlg.setTitle("Mohon tunggu");
        dlg.setMessage("Sedang masuk menggunakan akun Facebook...");
        dlg.show();

        Collection<String> permissions = Arrays.asList("public_profile", "email");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginFacebookActivity.this, permissions, new LogInCallback() {

            @Override
            public void done(ParseUser user, ParseException err) {
                if (err != null) {
                    dlg.dismiss();
                    ParseUser.logOut();
                    Log.e("err", "err", err);
                }
                if (user == null) {
                    dlg.dismiss();
                    ParseUser.logOut();
                    Toast.makeText(LoginFacebookActivity.this, "Login batal.", Toast.LENGTH_LONG).show();
                } else if (user.isNew()) {
                    dlg.dismiss();
                    Toast.makeText(LoginFacebookActivity.this, "Berhasil mendaftar aplikasi Deproo menggunakan Facebook", Toast.LENGTH_SHORT).show();
                    getUserDetailFromFB();
                } else {
                    dlg.dismiss();
                    alertDisplayer("Deproo","Selamat datang kembali, " + user.getUsername());
                }
            }
        });

    }

    private void getUserDetailFromFB(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),new GraphRequest.GraphJSONObjectCallback(){

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try{
                    mName = object.getString("name");
                    mEmail = object.getString("email");
                    JSONObject picture = object.getJSONObject("picture");
                    JSONObject pictureData = picture.getJSONObject("data");
                    String pictureUrl = pictureData.getString("url");
                    //Toast.makeText(getApplicationContext(), "P1: " + pictureUrl, Toast.LENGTH_SHORT).show();
                    new ProfilePhotoAsync(pictureUrl).execute();
                } catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "error getting profile picture url...", Toast.LENGTH_SHORT).show();
                }

            }

        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","name,email,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    class ProfilePhotoAsync extends AsyncTask<String, String, String> {
        public Bitmap bitmap;
        String url;

        public ProfilePhotoAsync(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(String... params) {
            // Fetching data from URI and storing in bitmap
            bitmap = DownloadImageBitmap(url);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //mProfileImage.setImageBitmap(bitmap);
            saveNewUser(bitmap);
        }
    }

    private ParseUser user;
    private void saveNewUser(Bitmap bitmap) {
//        user = ParseUser.getCurrentUser();
        user = new ParseUser();
        user.setUsername(mName);
        user.setEmail(mEmail);
        user.put(Constants.ParseTable.TableUser.LONGNAME, mName);
        user.put(Constants.ParseTable.TableUser.RATING, 0);
        user.put(Constants.ParseTable.TableUser.LOGIN_WITH, "facebook");
        user.put(Constants.ParseTable.TableUser.POINT, 0);
        user.put(Constants.ParseTable.TableUser.STATUS, Constants.BroStatus.STATUS1);
        user.put(Constants.ParseTable.TableUser.RANKING, 0);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.JPG_COMPRESSION_QUALITY_MEDIUM, stream);
        byte[] data = stream.toByteArray();
        String thumbName = user.getUsername().replaceAll("\\s+", "");
        final ParseFile parseFile = new ParseFile(thumbName + "_thumb.jpg", data);

        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                user.put(Constants.ParseTable.TableUser.PROFILE_THUMB, parseFile);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        alertDisplayer("Deproo", "Selamat bergabung di Deproo.");
                    }
                });
            }
        });
    }

    public static Bitmap DownloadImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("IMAGE", "Error getting bitmap", e);
        }
        return bm;
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginFacebookActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        LoginFacebookActivity.this.finish();
                        Intent intent = new Intent(LoginFacebookActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
