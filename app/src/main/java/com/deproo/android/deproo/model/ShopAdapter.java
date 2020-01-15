package com.deproo.android.deproo.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deproo.android.deproo.R;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private List<Shop> mShopData;

    public ShopAdapter(List<Shop> data) {
        mShopData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.layout_carousel, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Shop shop = mShopData.get(holder.getAdapterPosition());
        if(shop!=null) {
            ((ViewHolder) holder).image.setImageBitmap(shop.getImage());
        }
//        Glide.with(holder.itemView.getContext())
//                .asBitmap()
//                .load(mShopData.get(position).getImage())
//                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return mShopData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
        }
    }
}
