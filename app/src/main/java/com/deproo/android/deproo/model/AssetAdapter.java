package com.deproo.android.deproo.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.deproo.android.deproo.R;
import com.deproo.android.deproo.activity.AssetDetailActivity;
import com.deproo.android.deproo.utils.Constants;
import com.deproo.android.deproo.utils.Utils;
import com.elyeproj.loaderviewlibrary.LoaderImageView;
import com.elyeproj.loaderviewlibrary.LoaderTextView;

import java.util.ArrayList;

public class AssetAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<Asset> mAssets;

    public AssetAdapter(Context context, ArrayList<Asset> data) {
        mContext = context;
        mAssets = data;
    }

    private void openAssetDetail(Asset asset) {
        Uri uri = asset.getImageUri();
        if(uri!=null && asset!=null) {
            Intent intent = new Intent(mContext, AssetDetailActivity.class);
            intent.putExtra(Constants.ASSET_OBJECT, asset);
            intent.putExtra(Constants.URI, asset.getImageUri().toString());
            mContext.startActivity(intent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
//        final Asset asset = mAssets.get(position);
        final Asset asset = mAssets.get(holder.getAdapterPosition());
        if (asset!=null) {
            if(asset.getPropertyType()==Asset.TYPE_HOUSE) {
                if(asset.getImage()!=null) {
                    ((AssetPropertyViewHolder) holder).ivPicture.setImageBitmap(asset.getImage());
                } else {
                    ((AssetPropertyViewHolder) holder).ivPicture.setImageResource(R.drawable.noimage);
                }

                ((AssetPropertyViewHolder) holder).tvTitle.setText(asset.getTitle());
                if(asset.getOwner().getString(Constants.ParseTable.TableUser.LONGNAME)!=null) {
                    ((AssetPropertyViewHolder) holder).tvBroker
                            .setText(asset.getOwner().getString(Constants.ParseTable.TableUser.LONGNAME));
                } else {
                    ((AssetPropertyViewHolder) holder).tvBroker
                            .setText("-");
                }

                ((AssetPropertyViewHolder) holder).tvCategory.setText(asset.getCategory().toUpperCase());
                ((AssetPropertyViewHolder) holder).tvLocation.setText(asset.getConcatAddress());
                ((AssetPropertyViewHolder) holder).tvDiscuss
                        .setText(Integer.toString(asset.getNumDiscussion())+" diskusi");
                ((AssetPropertyViewHolder) holder).tvViewer.setText(Integer.toString(asset.getNumViewer())+" dilihat");
                ((AssetPropertyViewHolder) holder).tvPServe.setText(asset.getTypePServe());
                if(asset.getTypePServe().equalsIgnoreCase("jual")) {
                    ((AssetPropertyViewHolder) holder).tvMonthYear.setVisibility(View.GONE);
                } else {
                    String monthyear = "/" + Integer.toString(asset.getNumPServe()) + " " + asset.getTimePServe();
                    ((AssetPropertyViewHolder) holder).tvMonthYear.setVisibility(View.VISIBLE);
                    ((AssetPropertyViewHolder) holder).tvMonthYear.setText(monthyear);
                }

                ((AssetPropertyViewHolder) holder).tvFurnish.setVisibility(View.GONE);
                ((AssetPropertyViewHolder) holder).tvPrice.setText(Utils.formatIDR(asset.getPrice()));
                ((AssetPropertyViewHolder) holder).tvNumBeds.setText(asset.getNumBedroomFormatted());
                ((AssetPropertyViewHolder) holder).tvNumBaths.setText(asset.getNumBathroomFormatted());
                ((AssetPropertyViewHolder) holder).tvNumGarage.setText(asset.getNumGarageFormatted());
                ((AssetPropertyViewHolder) holder).tvNumFloor.setText(asset.getNumFloorFormatted());

                ((AssetPropertyViewHolder) holder).btnOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openAssetDetail(asset);
                    }
                });

            } else if(asset.getPropertyType()==Asset.TYPE_OFFICE) {
                if(asset.getImage()!=null) {
                    ((AssetOfficeViewHolder) holder).ivPicture.setImageBitmap(asset.getImage());
                } else  {
                    ((AssetOfficeViewHolder) holder).ivPicture.setImageResource(R.drawable.noimage);
                }
                ((AssetOfficeViewHolder) holder).tvTitle.setText(asset.getTitle());
                if(asset.getOwner().getString(Constants.ParseTable.TableUser.LONGNAME)!=null) {
                    ((AssetOfficeViewHolder) holder).tvBroker
                            .setText(asset.getOwner().getString(Constants.ParseTable.TableUser.LONGNAME));
                } else {
                    ((AssetOfficeViewHolder) holder).tvBroker
                            .setText("-");
                }
                ((AssetOfficeViewHolder) holder).tvCategory.setText(asset.getCategory().toUpperCase());
                ((AssetOfficeViewHolder) holder).tvLocation.setText(asset.getConcatAddress());
                ((AssetOfficeViewHolder) holder).tvDiscuss
                        .setText(Integer.toString(asset.getNumDiscussion())+" diskusi");
                ((AssetOfficeViewHolder) holder).tvViewer.setText(Integer.toString(asset.getNumViewer())+" dilihat");
                ((AssetOfficeViewHolder) holder).tvPServe.setText(asset.getTypePServe());
                if(asset.getTypePServe().equalsIgnoreCase("jual")) {
                    ((AssetOfficeViewHolder) holder).tvMonthYear.setVisibility(View.GONE);
                } else {
                    String monthyear = "/" + Integer.toString(asset.getNumPServe()) + " " + asset.getTimePServe();
                    ((AssetOfficeViewHolder) holder).tvMonthYear.setVisibility(View.VISIBLE);
                    ((AssetOfficeViewHolder) holder).tvMonthYear.setText(monthyear);
                }

                ((AssetOfficeViewHolder) holder).tvArea.setText(asset.getSizehouseFormatted());
                ((AssetOfficeViewHolder) holder).tvPrice.setText(Utils.formatIDR(asset.getPrice()));
                ((AssetOfficeViewHolder) holder).tvNumGarage.setText(asset.getNumGarageFormatted());
                ((AssetOfficeViewHolder) holder).tvNumFloor.setText(asset.getNumFloorFormatted());

                ((AssetOfficeViewHolder) holder).btnOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openAssetDetail(asset);
                    }
                });
            } else if(asset.getPropertyType()==Asset.TYPE_LAND) {
                if(asset.getImage()!=null) {
                    ((AssetLandViewHolder) holder).ivPicture.setImageBitmap(asset.getImage());
                } else {
                    ((AssetLandViewHolder) holder).ivPicture.setImageResource(R.drawable.noimage);
                }
                ((AssetLandViewHolder) holder).tvTitle.setText(asset.getTitle());
                if(asset.getOwner().getString(Constants.ParseTable.TableUser.LONGNAME)!=null) {
                    ((AssetLandViewHolder) holder).tvBroker
                            .setText(asset.getOwner().getString(Constants.ParseTable.TableUser.LONGNAME));
                } else {
                    ((AssetLandViewHolder) holder).tvBroker
                            .setText("-");
                }
                ((AssetLandViewHolder) holder).tvCategory.setText(asset.getCategory().toUpperCase());
                ((AssetLandViewHolder) holder).tvLocation.setText(asset.getConcatAddress());
                ((AssetLandViewHolder) holder).tvDiscuss
                        .setText(Integer.toString(asset.getNumDiscussion())+" diskusi");
                ((AssetLandViewHolder) holder).tvViewer.setText(Integer.toString(asset.getNumViewer())+" dilihat");
                ((AssetLandViewHolder) holder).tvPServe.setText(asset.getTypePServe());
                if(asset.getTypePServe().equalsIgnoreCase("jual")) {
                    ((AssetLandViewHolder) holder).tvMonthYear.setVisibility(View.GONE);
                } else {
                    String monthyear = "/" + Integer.toString(asset.getNumPServe()) + " " + asset.getTimePServe();
                    ((AssetLandViewHolder) holder).tvMonthYear.setVisibility(View.VISIBLE);
                    ((AssetLandViewHolder) holder).tvMonthYear.setText(monthyear);
                }
                ((AssetLandViewHolder) holder).tvPrice.setText(Utils.formatIDR(asset.getPrice()));
                ((AssetLandViewHolder) holder).tvArea.setText(Utils.formatDouble(asset.getSizeland())+" m2");
                ((AssetLandViewHolder) holder).btnOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openAssetDetail(asset);
                    }
                });
            } else if(asset.getPropertyType()==Asset.TYPE_APART) {
                if(asset.getImage()!=null) {
                    ((AssetPropertyViewHolder) holder).ivPicture.setImageBitmap(asset.getImage());
                } else {
                    ((AssetPropertyViewHolder) holder).ivPicture.setImageResource(R.drawable.noimage);
                }
                ((AssetPropertyViewHolder) holder).tvTitle.setText(asset.getTitle());
                if(asset.getOwner().getString(Constants.ParseTable.TableUser.LONGNAME)!=null) {
                    ((AssetPropertyViewHolder) holder).tvBroker
                            .setText(asset.getOwner().getString(Constants.ParseTable.TableUser.LONGNAME));
                } else {
                    ((AssetPropertyViewHolder) holder).tvBroker
                            .setText("-");
                }
                ((AssetPropertyViewHolder) holder).tvCategory.setText(asset.getCategory().toUpperCase());
                ((AssetPropertyViewHolder) holder).tvLocation.setText(asset.getConcatAddress());
                ((AssetPropertyViewHolder) holder).tvDiscuss
                        .setText(Integer.toString(asset.getNumDiscussion())+" diskusi");
                ((AssetPropertyViewHolder) holder).tvViewer.setText(Integer.toString(asset.getNumViewer())+" dilihat");
                ((AssetPropertyViewHolder) holder).tvPServe.setText(asset.getTypePServe());
                if(asset.getTypePServe().equalsIgnoreCase("jual")) {
                    ((AssetPropertyViewHolder) holder).tvMonthYear.setVisibility(View.GONE);
                } else {
                    String monthyear = "/" + Integer.toString(asset.getNumPServe()) + " " + asset.getTimePServe();
                    ((AssetPropertyViewHolder) holder).tvMonthYear.setVisibility(View.VISIBLE);
                    ((AssetPropertyViewHolder) holder).tvMonthYear.setText(monthyear);
                }

                ((AssetPropertyViewHolder) holder).tvPrice.setText(Utils.formatIDR(asset.getPrice()));
                ((AssetPropertyViewHolder) holder).tvFurnish.setVisibility(View.VISIBLE);
                ((AssetPropertyViewHolder) holder).tvFurnish.setText(asset.getTypeApart());
                ((AssetPropertyViewHolder) holder).tvNumBeds.setText(asset.getNumBedroomFormatted());
                ((AssetPropertyViewHolder) holder).tvNumBaths.setText(asset.getNumBathroomFormatted());
                ((AssetPropertyViewHolder) holder).tvNumGarage.setText(asset.getNumGarageFormatted());
                ((AssetPropertyViewHolder) holder).tvNumFloor.setText(asset.getNumFloorFormatted());

                ((AssetPropertyViewHolder) holder).btnOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openAssetDetail(asset);
                    }
                });

            } else if(asset.getPropertyType()==Asset.TYPE_TOWNHOUSE) {
                if(asset.getImage()!=null) {
                    ((AssetPropertyViewHolder) holder).ivPicture.setImageBitmap(asset.getImage());
                } else {
                    ((AssetPropertyViewHolder) holder).ivPicture.setImageResource(R.drawable.noimage);
                }
                ((AssetPropertyViewHolder) holder).tvTitle.setText(asset.getTitle());
                if(asset.getOwner().getString(Constants.ParseTable.TableUser.LONGNAME)!=null) {
                    ((AssetPropertyViewHolder) holder).tvBroker
                            .setText(asset.getOwner().getString(Constants.ParseTable.TableUser.LONGNAME));
                } else {
                    ((AssetPropertyViewHolder) holder).tvBroker
                            .setText("-");
                }
                ((AssetPropertyViewHolder) holder).tvCategory.setText(asset.getCategory().toUpperCase());
                ((AssetPropertyViewHolder) holder).tvLocation.setText(asset.getConcatAddress());
                ((AssetPropertyViewHolder) holder).tvDiscuss
                        .setText(Integer.toString(asset.getNumDiscussion())+" diskusi");
                ((AssetPropertyViewHolder) holder).tvViewer.setText(Integer.toString(asset.getNumViewer())+" dilihat");
                ((AssetPropertyViewHolder) holder).tvPServe.setText(asset.getTypePServe());
                if(asset.getTypePServe().equalsIgnoreCase("jual")) {
                    ((AssetPropertyViewHolder) holder).tvMonthYear.setVisibility(View.GONE);
                } else {
                    String monthyear = "/" + Integer.toString(asset.getNumPServe()) + " " + asset.getTimePServe();
                    ((AssetPropertyViewHolder) holder).tvMonthYear.setVisibility(View.VISIBLE);
                    ((AssetPropertyViewHolder) holder).tvMonthYear.setText(monthyear);
                }

                ((AssetPropertyViewHolder) holder).tvFurnish.setVisibility(View.GONE);
                ((AssetPropertyViewHolder) holder).tvPrice.setText(Utils.formatIDR(asset.getPrice()));
                ((AssetPropertyViewHolder) holder).tvNumBeds.setText(asset.getNumBedroomFormatted());
                ((AssetPropertyViewHolder) holder).tvNumBaths.setText(asset.getNumBathroomFormatted());
                ((AssetPropertyViewHolder) holder).tvNumGarage.setText(asset.getNumGarageFormatted());
                ((AssetPropertyViewHolder) holder).tvNumFloor.setText(asset.getNumFloorFormatted());

                ((AssetPropertyViewHolder) holder).btnOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openAssetDetail(asset);
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==Asset.TYPE_HOUSE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_cardview_property,
                    parent, false);
            return new AssetPropertyViewHolder(view);
        } else if(viewType==Asset.TYPE_APART) {
            // to-do: need to change layout
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_cardview_property,
                    parent, false);
            return new AssetPropertyViewHolder(view);
        } else if(viewType==Asset.TYPE_TOWNHOUSE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_cardview_property,
                    parent, false);
            return new AssetPropertyViewHolder(view);
        } else if(viewType==Asset.TYPE_OFFICE) {
            // to-do: need to change layout
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_cardview_office,
                    parent, false);
            return new AssetOfficeViewHolder(view);
        } else if(viewType==Asset.TYPE_LAND) {
            // to-do: need to change layout
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_cardview_land,
                    parent, false);
            return new AssetLandViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        switch (mAssets.get(position).getPropertyType()) {
            case 0:
                return Asset.TYPE_HOUSE;
            case 1:
                return Asset.TYPE_OFFICE;
            case 2:
                return Asset.TYPE_LAND;
            case 3:
                return Asset.TYPE_APART;
            case 4:
                return  Asset.TYPE_TOWNHOUSE;
            default:
                return -1;
        }
    }

    @Override
    public int getItemCount() {
        return mAssets.size();
    }

    public static class AssetPropertyViewHolder extends RecyclerView.ViewHolder {

        LoaderTextView tvCategory;
        LoaderTextView tvFurnish;
        LoaderTextView tvPrice;
        LoaderTextView tvDiscuss;
        LoaderTextView tvViewer;
        LoaderTextView tvLocation;

        LoaderTextView tvNumBeds;
        LoaderTextView tvNumBaths;
        LoaderTextView tvNumGarage;
        LoaderTextView tvNumFloor;
        LoaderTextView tvPServe;
        LoaderTextView tvMonthYear;

        LoaderImageView ivPicture;
        LoaderTextView tvTitle;
        TextView tvBroker;

        Button btnOpen;

        public AssetPropertyViewHolder(View itemView) {
            super(itemView);
            ivPicture = itemView.findViewById(R.id.id_pict_asset);
            tvBroker = itemView.findViewById(R.id.id_card1_broker);
            tvTitle = itemView.findViewById(R.id.id_card1_title);
            tvCategory = itemView.findViewById(R.id.id_card1_category);
            tvFurnish = itemView.findViewById(R.id.id_card1_furnish);
            tvPrice = itemView.findViewById(R.id.id_card1_price);
            tvDiscuss = itemView.findViewById(R.id.id_card1_discuss);
            tvViewer = itemView.findViewById(R.id.id_card1_viewer);
            tvLocation = itemView.findViewById(R.id.id_card1_location);
            tvNumBeds = itemView.findViewById(R.id.id_card1_numbeds);
            tvNumBaths = itemView.findViewById(R.id.id_card1_numbaths);
            tvNumGarage = itemView.findViewById(R.id.id_card1_numgarage);
            tvNumFloor = itemView.findViewById(R.id.id_card1_numfloor);
            tvPServe = itemView.findViewById(R.id.id_card1_pserve);
            tvMonthYear = itemView.findViewById(R.id.id_card1_monthyear);
            btnOpen = itemView.findViewById(R.id.id_card1_button);
        }
    }

    public static class AssetLandViewHolder extends RecyclerView.ViewHolder {

        LoaderTextView tvCategory;
        LoaderTextView tvPrice;
        LoaderTextView tvDiscuss;
        LoaderTextView tvViewer;
        LoaderTextView tvLocation;
        LoaderTextView tvArea;
        LoaderTextView tvPServe;
        LoaderTextView tvMonthYear;

        LoaderImageView ivPicture;
        LoaderTextView tvTitle;
        TextView tvBroker;

        Button btnOpen;

        public AssetLandViewHolder(View itemView) {
            super(itemView);
            ivPicture = (LoaderImageView) itemView.findViewById(R.id.id_pict_asset);
            tvBroker = (TextView) itemView.findViewById(R.id.id_card1_broker);
            tvTitle = (LoaderTextView) itemView.findViewById(R.id.id_card1_title);
            tvCategory = (LoaderTextView) itemView.findViewById(R.id.id_card1_category);
            tvPrice = (LoaderTextView) itemView.findViewById(R.id.id_card1_price);
            tvDiscuss = (LoaderTextView) itemView.findViewById(R.id.id_card1_discuss);
            tvViewer = (LoaderTextView) itemView.findViewById(R.id.id_card1_viewer);
            tvLocation = (LoaderTextView) itemView.findViewById(R.id.id_card1_location);
            tvArea = (LoaderTextView) itemView.findViewById(R.id.id_card1_area);
            tvPServe = (LoaderTextView) itemView.findViewById(R.id.id_card1_pserve);
            tvMonthYear = (LoaderTextView) itemView.findViewById(R.id.id_card1_monthyear);
            btnOpen = (Button) itemView.findViewById(R.id.id_card1_button);
        }
    }

    public static class AssetOfficeViewHolder extends RecyclerView.ViewHolder {

        LoaderTextView tvCategory;
        LoaderTextView tvPrice;
        LoaderTextView tvDiscuss;
        LoaderTextView tvViewer;
        LoaderTextView tvLocation;
        LoaderTextView tvArea;
        LoaderTextView tvPServe;
        LoaderTextView tvMonthYear;
        LoaderTextView tvNumGarage;
        LoaderTextView tvNumFloor;

        LoaderImageView ivPicture;
        LoaderTextView tvTitle;
        TextView tvBroker;

        Button btnOpen;

        public AssetOfficeViewHolder(View itemView) {
            super(itemView);
            ivPicture = (LoaderImageView) itemView.findViewById(R.id.id_pict_asset);
            tvTitle = (LoaderTextView) itemView.findViewById(R.id.id_card1_title);
            tvBroker = (TextView) itemView.findViewById(R.id.id_card1_broker);
            tvCategory = (LoaderTextView) itemView.findViewById(R.id.id_card1_category);
            tvPrice = (LoaderTextView) itemView.findViewById(R.id.id_card1_price);
            tvDiscuss = (LoaderTextView) itemView.findViewById(R.id.id_card1_discuss);
            tvViewer = (LoaderTextView) itemView.findViewById(R.id.id_card1_viewer);
            tvLocation = (LoaderTextView) itemView.findViewById(R.id.id_card1_location);
            tvNumGarage = (LoaderTextView) itemView.findViewById(R.id.id_card1_numgarage);
            tvNumFloor = (LoaderTextView) itemView.findViewById(R.id.id_card1_numfloor);
            tvPServe = (LoaderTextView) itemView.findViewById(R.id.id_card1_pserve);
            tvArea = (LoaderTextView) itemView.findViewById(R.id.id_card1_luas_bangunan);
            tvMonthYear = (LoaderTextView) itemView.findViewById(R.id.id_card1_monthyear);
            btnOpen = (Button) itemView.findViewById(R.id.id_card1_button);
        }
    }


}
