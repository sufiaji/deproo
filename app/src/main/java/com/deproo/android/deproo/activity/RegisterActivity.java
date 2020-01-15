package com.deproo.android.deproo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.utils.Constants;
import com.deproo.android.deproo.utils.Utils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextRePassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_2);

        editTextEmail = (EditText) findViewById(R.id.id_edittext_emailr);
        editTextPassword = (EditText) findViewById(R.id.id_edittext_password_1);
        editTextRePassword = (EditText) findViewById(R.id.id_edittext_password_2);
        CheckBox mCkbox = (CheckBox) findViewById(R.id.id_cbox_showpassr);

        mCkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editTextRePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    editTextRePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

    }

    public void onRegister(View v) {
        String emailString = editTextEmail.getText().toString().toLowerCase();
        String passwordString = editTextPassword.getText().toString();
        String repasswordString = editTextRePassword.getText().toString();
        if(emailString.isEmpty() || passwordString.isEmpty()) {
            Toast.makeText(this,"Email/Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Utils.ValidateEmail(emailString)){
            Toast.makeText(this,"Email tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!repasswordString.equals(passwordString)) {
            Toast.makeText(this,"Password yang Anda ketik tidak sama", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog dlg = new ProgressDialog(this);
//        dlg.setTitle("Mohon tunggu.");
        dlg.setMessage("Mohon tunggu, sedang mendaftarkan...");
        dlg.show();

        ParseUser user = new ParseUser();
        user.setUsername(emailString);
        user.setEmail(emailString);
        user.setPassword(passwordString);
        user.put(Constants.ParseTable.TableUser.LONGNAME, emailString);
        user.put(Constants.ParseTable.TableUser.RATING, 0);
        user.put(Constants.ParseTable.TableUser.POINT, 0);
        user.put(Constants.ParseTable.TableUser.STATUS, Constants.BroStatus.STATUS1);
        user.put(Constants.ParseTable.TableUser.RANKING, 99);
        user.put(Constants.ParseTable.TableUser.LOGIN_WITH, "email");
        user.put(Constants.ParseTable.TableUser.PHONE, "");
        user.put(Constants.ParseTable.TableUser.BIO1, "");
        user.put(Constants.ParseTable.TableUser.BIO2, "");
        user.put(Constants.ParseTable.TableUser.GENDER, "");
        user.put(Constants.ParseTable.TableUser.ADDRESS, "");
        user.put(Constants.ParseTable.TableUser.NUMBER_OF_FOLLOWER, 0);
        user.put(Constants.ParseTable.TableUser.NUMBER_OF_REVIEW, 0);
        user.put(Constants.ParseTable.TableUser.CITY, "");
        user.put(Constants.ParseTable.TableUser.PROVINCE, "");

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    dlg.dismiss();
                    alertDisplayer("Deproo","Akun berhasil didaftarkan. " +
                            "Silahkan tunggu email konfirmasi untuk mengaktifkan akun Anda");
                } else {
                    dlg.dismiss();
                    Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

}
