package com.deproo.android.deproo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.deproo.android.deproo.R;
import com.facebook.login.LoginManager;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class HomeFacebookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_facebook);
    }

    public void onLogoutClick(View v) {
        final ProgressDialog dlg = new ProgressDialog(HomeFacebookActivity.this);
        dlg.setTitle("Please, wait a moment.");
        dlg.setMessage("Logging out...");
        dlg.show();

        // logging out of Facebook
        LoginManager.getInstance().logOut();

        // logging out of Parse
        ParseUser.logOut();

        alertDisplayer("So, you're going...", "Ok...Bye-bye then");
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeFacebookActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(HomeFacebookActivity.this, LoginFacebookActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
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
