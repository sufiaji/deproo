package com.deproo.android.deproo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.utils.Utils;

public class NewAssetDescActivity extends AppCompatActivity {

    String mOriginalString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_asset_desc);
        mOriginalString = getIntent().getStringExtra(NewAssetActivity.EXISTING_DESC);
        ((EditText)findViewById(R.id.id_edittext_desc_long)).setText(mOriginalString);
        getSupportActionBar().setTitle(R.string.title_asset_descr);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

    public void onSaveDescrClick(View v) {
        String desc = ((EditText)findViewById(R.id.id_edittext_desc_long)).getText().toString().trim();
        if(desc.isEmpty()) {
            Utils.CommonToastDisplayerLong(this, "Anda belum mengisi deskripsi dari properti");
            return;
        }

        Intent intent = getIntent();
        intent.putExtra(NewAssetActivity.DESCRIPTION, desc);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        String descr = ((EditText) findViewById(R.id.id_edittext_desc_long))
                .getText().toString().trim();
        if(mOriginalString != null) {
            if (!mOriginalString.isEmpty() && !descr.isEmpty()) {
                if (!mOriginalString.equalsIgnoreCase(descr)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewAssetDescActivity.this)
//                .setTitle("Deproo")
                            .setMessage("Batal mengisi deskripsi Asset Anda?")
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
            } else if (mOriginalString.isEmpty() && !descr.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewAssetDescActivity.this)
//                .setTitle("Deproo")
                        .setMessage("Batal mengisi deskripsi Asset Anda?")
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
            } else if (!mOriginalString.isEmpty() && descr.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewAssetDescActivity.this)
//                .setTitle("Deproo")
                        .setMessage("Batal mengisi deskripsi Asset Anda?")
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
            } else if(mOriginalString.isEmpty() && descr.isEmpty()) {
                onBackPressedCustom();
            }
        } else {
            if(!descr.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewAssetDescActivity.this)
//                .setTitle("Deproo")
                        .setMessage("Batal mengisi deskripsi Asset Anda?")
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
            } else {
                onBackPressedCustom();
            }
        }
    }

    private void onBackPressedCustom() {
        super.onBackPressed();
    }
}
