package com.deproo.android.deproo.activity;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.application.DeprooApplication;
import com.deproo.android.deproo.model.Broker;
import com.deproo.android.deproo.model.UserReview;
import com.deproo.android.deproo.utils.Constants;
import com.parse.ParseUser;

public class AddReviewActivity extends AppCompatActivity {

    private Broker mBroker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        // get selected card broker
        getSupportActionBar().setTitle(R.string.title_add_review);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBroker = getIntent().getParcelableExtra(Constants.BROKER_OBJECT);
        ((TextView) findViewById(R.id.id_broker_name)).setText(mBroker.getLongname());
        if(DeprooApplication.sharedProfilePic !=null) {
            mBroker.setProfilePicThumb(DeprooApplication.sharedProfilePic);
            ((CircleImageView)findViewById(R.id.id_broker_picture)).setImageBitmap(DeprooApplication.sharedProfilePic);
            DeprooApplication.sharedProfilePic =null;
        }
        if(ParseUser.getCurrentUser()==null) {
            Toasty.info(this, "Anda harus login untuk memberikan ulasan.", Toast.LENGTH_SHORT, true).show();
            ((Button) findViewById(R.id.id_button_send)).setEnabled(false);
        }
    }

    public void onSaveClick(View view) {
        float rating = 0;
        String review = ((EditText) findViewById(R.id.id_edittext_review)).getText().toString();
        rating = ((RatingBar) findViewById(R.id.id_broker_rating)).getRating();
        if(review==null || review.isEmpty()) {
            Toasty.warning(this, "Ulasan tidak boleh kosong", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if(rating==0) {
            Toasty.warning(this, "Rating belum diset", Toast.LENGTH_SHORT, true).show();
            return;
        }
        UserReview userReview = new UserReview();
        userReview.setRating(rating);
        userReview.setReview(review);
        userReview.setBroker(mBroker);
        userReview.setBrokerWhoGiveRating((Broker) ParseUser.getCurrentUser());
        userReview.saveEventually();
        Toasty.info(this, "Ulasan dikirim...", Toast.LENGTH_SHORT, true).show();
        finish();
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
}
