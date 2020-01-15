package com.deproo.android.deproo.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.model.AsyncGetBrokerThumb;
import com.deproo.android.deproo.model.Broker;
import com.deproo.android.deproo.model.BrokerAdapter;
import com.deproo.android.deproo.model.EndlessRecyclerViewScrollListener;
import com.deproo.android.deproo.utils.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;


import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by Pradhono Rakhmono Aji on $(DATE)
 */
public class BrokerListActivity extends AppCompatActivity {

    private Context mContext;
    private ProgressBar progressBar;
    private ArrayList<Broker> mBrokers;
    private BrokerAdapter mAdapter;
    private int mSkip = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_broker_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Broker");

        progressBar = findViewById(R.id.id_progress_broker_list);

        mBrokers = new ArrayList<>();
        mAdapter = new BrokerAdapter(mContext, mBrokers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        RecyclerView recyclerView = findViewById(R.id.id_recycler_broker);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getBroker();
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        getBroker();
    }

    private void getBroker() {
        ParseQuery<Broker> query = ParseQuery.getQuery(Broker.class);
        query.addDescendingOrder(Constants.ParseTable.TableUser.CREATED_AT);
        query.setLimit(5);
        query.setSkip(mSkip);
        query.findInBackground(new FindCallback<Broker>() {
            @Override
            public void done(List<Broker> brokers, ParseException e) {
                progressBar.setVisibility(View.GONE);
                if(e==null && brokers.size()>0) {
                    int curSize = mAdapter.getItemCount();
                    mBrokers.addAll(brokers);
                    mAdapter.notifyItemRangeInserted(curSize, brokers.size());
                    mSkip = mSkip + 5;
                    for(int i=0;i<brokers.size();i++) {
                        String objectID = brokers.get(i).getObjectId();
                        AsyncBrokerThumb task = new AsyncBrokerThumb(objectID);
                        task.execute(brokers.get(i));
                    }
                } else {
                    Toasty.info(getApplicationContext(),"Broker tidak ditemukan");
                }
            }
        });
    }

    private class AsyncBrokerThumb extends AsyncTask<Broker, Integer, Bitmap> {

        private String mObjectId;
        public AsyncBrokerThumb(String objectId) {
            mObjectId = objectId;
        }

        @Override
        protected Bitmap doInBackground(Broker... brokers) {
            Broker broker = brokers[0];
            ParseFile parseFile = broker.getParseFile(Constants.ParseTable.TableUser.PROFILE_THUMB);
            Bitmap bitmap = null;
            try {
                byte[] data = parseFile.getData();
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null) {
                for(int i=0;i<mAdapter.getItemCount();i++) {
                    if(mBrokers.get(i).getObjectId().equals(mObjectId)) {
                        mBrokers.get(i).setProfilePicThumb(bitmap);
                        mAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
