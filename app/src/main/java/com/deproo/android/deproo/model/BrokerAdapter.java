package com.deproo.android.deproo.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.activity.BrokerProfileActivity;
import com.deproo.android.deproo.application.DeprooApplication;
import com.deproo.android.deproo.utils.Constants;
import com.elyeproj.loaderviewlibrary.LoaderTextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pradhono Rakhmono Aji on 17/11/2019
 */
public class BrokerAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<Broker> mBrokers;

    public BrokerAdapter(Context context, ArrayList<Broker> brokers) {
        mContext = context;
        mBrokers = brokers;
    }

    private void openBrokerDetail(Broker broker) {
        DeprooApplication.sharedProfilePic = broker.getProfilePicThumb();
        Intent intent = new Intent(mContext, BrokerProfileActivity.class);
        intent.putExtra(Constants.BROKER_OBJECT, broker);
        mContext.startActivity(intent);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.brokerlist_cardview, parent, false);
        return new BrokerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Broker broker = mBrokers.get(holder.getAdapterPosition());
        if(broker!=null) {
            if(broker.getProfilePicThumb()!=null)
                ((BrokerViewHolder) holder).photo.setImageBitmap(broker.getProfilePicThumb());
            ((BrokerViewHolder) holder).name.setText(broker.getLongname());
            ((BrokerViewHolder) holder).location.setText(broker.getCityProvince());
            if(broker.getTitle()!=null && !broker.getTitle().isEmpty())
                ((BrokerViewHolder) holder).description.setText(broker.getTitle());
            else
                ((BrokerViewHolder) holder).description.setVisibility(View.GONE);
            ((BrokerViewHolder) holder).rating.setRating((float) broker.getRating());
            ((BrokerViewHolder) holder).numAsset.setText(broker.getNumAsset() + " asset");
            ((BrokerViewHolder) holder).numFollower.setText(broker.getNumFollower() + " followers" );
            ((BrokerViewHolder) holder).mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openBrokerDetail(broker);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mBrokers.size();
    }

    public static class BrokerViewHolder extends RecyclerView.ViewHolder {

        CircleImageView photo;
        LoaderTextView name;
        LoaderTextView location;
        AppCompatRatingBar rating;
        LoaderTextView description;
        TextView numAsset;
        TextView numFollower;
        LinearLayout mainLayout;

        public BrokerViewHolder(View view) {
            super(view);
            photo = view.findViewById(R.id.photo);
            name = view.findViewById(R.id.name);
            location = view.findViewById(R.id.location);
            rating = view.findViewById(R.id.rating);
            description = view.findViewById(R.id.description);
            numAsset = view.findViewById(R.id.num_asset);
            numFollower = view.findViewById(R.id.num_folower);
            mainLayout = view.findViewById(R.id.main_layout);
        }
    }
}
