package com.deproo.android.deproo.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.deproo.android.deproo.R;
import com.deproo.android.deproo.activity.MainActivity;
import com.deproo.android.deproo.application.DeprooApplication;
import com.deproo.android.deproo.model.Asset;
import com.deproo.android.deproo.model.AssetImage;
import com.deproo.android.deproo.model.AsyncGetBrokerThumb;
import com.deproo.android.deproo.model.Broker;
import com.deproo.android.deproo.model.Shop;
import com.deproo.android.deproo.model.ShopAdapter;
import com.deproo.android.deproo.utils.Constants;
import com.deproo.android.deproo.utils.Utils;
import com.elyeproj.loaderviewlibrary.LoaderImageView;
import com.elyeproj.loaderviewlibrary.LoaderTextView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener,
        DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder> {

//    public static final String ASSET_OBJECT = "ASSETOBJECT";

    private SliderLayout mDemoSlider;

    private LoaderImageView mNewAssetPic1;
    private LoaderImageView mNewAssetPic2;
    private LoaderImageView mNewAssetPic3;
    private LoaderImageView mNewAssetPic4;
    private LoaderImageView mNewAssetPic5;

    private ArrayList<Broker> mBrokers;
    private ArrayList<Asset> mAssets;
    private Broker mCurrentBroker;
    private Context mContext;
    private DiscreteScrollView itemPicker;
    private ShopAdapter shopAdapter;
    private int mPreviousPosition;

    public HomeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_3, container, false);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = ((MainActivity) getActivity()).getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
        mPreviousPosition = 0;
        itemPicker = v.findViewById(R.id.item_picker);
        itemPicker.setOrientation(DSVOrientation.HORIZONTAL);
        itemPicker.addOnItemChangedListener(this);
        List<Shop> shopList = new ArrayList<>();
        shopList.add(new Shop(1,"Shop1", BitmapFactory.decodeResource(getResources(),R.drawable.slide1)));
        shopList.add(new Shop(2,"Shop2", BitmapFactory.decodeResource(getResources(),R.drawable.slide2)));
        shopList.add(new Shop(3,"Shop3", BitmapFactory.decodeResource(getResources(),R.drawable.slide3)));
        shopList.add(new Shop(4,"Shop4", BitmapFactory.decodeResource(getResources(),R.drawable.slide4)));
        shopList.add(new Shop(4,"Shop5", BitmapFactory.decodeResource(getResources(),R.drawable.slide5)));

        shopAdapter = new ShopAdapter(shopList);
        itemPicker.setAdapter(shopAdapter);
        itemPicker.setItemTransitionTimeMillis(124);

        init(v);
        return v;
    }

    private void initLayout(View v) {
        /*
        New Broker Area
         */

        /*
        New Asset
         */
        mNewAssetPic1 =  v.findViewById(R.id.id_asset_1);
        mNewAssetPic2 =  v.findViewById(R.id.id_asset_2);
        mNewAssetPic3 =  v.findViewById(R.id.id_asset_3);
        mNewAssetPic4 =  v.findViewById(R.id.id_asset_4);
        mNewAssetPic5 =  v.findViewById(R.id.id_asset_5);
    }

    private void getBroker(View v) {
        initBroker(v);
        getNewBrokerX(v);
//        if(DeprooApplication.initState) {
//            mBrokers.clear();
//            DeprooApplication.sharedBrokerList.clear();
//            getNewBrokerX(v);
//        } else {
//            mBrokers.clear();
//            mBrokers.addAll(DeprooApplication.sharedBrokerList);
//            displayBroker(v);
//        }
    }

    private void initBroker(View v) {
        for(int i=0;i<Constants.HOME_SLIDE_NUMBER;i++) {
            String name = "id_nbroker_" + Integer.toString(i+1);
            LoaderImageView iv = v.findViewById(Utils.getResIdOfComponent(mContext,name));
            iv.setImageResource(R.drawable.broker_fake);
        }
    }

    private void displayBroker(View v) {
        int i = 0;
        for(Broker broker: mBrokers) {
            i = i+1;
            String name1 = "id_tnbroker_" + Integer.toString(i) + "_1";
            String name2 = "id_tnbroker_" + Integer.toString(i) + "_2";
            LoaderTextView tv1 = v.findViewById(Utils.getResIdOfComponent(mContext,name1));
            LoaderTextView tv2 = v.findViewById(Utils.getResIdOfComponent(mContext,name2));
            tv1.setText(broker.getLongname());
            if(!broker.getTitle().isEmpty())
                tv2.setText(broker.getTitle());
            else
                tv2.setVisibility(View.GONE);
            String name = "id_nbroker_" + Integer.toString(i);
            if(broker.getProfilePicThumb()!=null) {
                LoaderImageView iv = v.findViewById(Utils.getResIdOfComponent(mContext, name));
                iv.setImageBitmap(broker.getProfilePicThumb());
            }
        }
    }

    private void displayAsset(View v) {
        int i=0;
        for(Asset asset : mAssets) {
            i = i+1;
            String name1 = "id_tasset_" + Integer.toString(i) + "_1";
            String name2 = "id_tasset_" + Integer.toString(i) + "_2";
            LoaderTextView tv1 = v.findViewById(Utils.getResIdOfComponent(mContext,name1));
            LoaderTextView tv2 = v.findViewById(Utils.getResIdOfComponent(mContext,name2));
            tv1.setText(Utils.formatIDR(asset.getPrice()));
            tv2.setText(asset.getTitle());
            String name = "id_asset_" + Integer.toString(i);
            if(asset.getImage()!=null) {
                LoaderImageView iv = v.findViewById(Utils.getResIdOfComponent(mContext, name));
                iv.setImageBitmap(asset.getImage());
            }
        }
    }

    private void initAsset(View v) {
        for(int i=0;i<Constants.HOME_SLIDE_NUMBER;i++) {
            String name = "id_asset_" + Integer.toString(i+1);
            LoaderImageView iv = v.findViewById(Utils.getResIdOfComponent(mContext,name));
            iv.setImageResource(R.drawable.noimage);
        }
    }

    private void getAsset(View v) {
        initAsset(v);
        if(DeprooApplication.initState) {
            mAssets.clear();
            DeprooApplication.sharedAssetList.clear();
            getAssetX(v);
        } else {
            mAssets.clear();
            mAssets.addAll(DeprooApplication.sharedAssetList);
            displayAsset(v);
        }
    }

    private void init(View v) {

        mContext = getContext();
        mBrokers = new ArrayList<>();
        mAssets = new ArrayList<>();
        mCurrentBroker = (Broker) ParseUser.getCurrentUser();

        initLayout(v);

        getBroker(v);

        getAsset(v);

        initProfile(v);

//        initSlider(v);

        initClickListener(v);

        DeprooApplication.initState = false;
    }

    private void initProfile(View v) {
        TextView textBro =  v.findViewById(R.id.id_text_status);
        TextView textPoint =  v.findViewById(R.id.id_text_point);
        TextView textName =  v.findViewById(R.id.id_text_name);
        CircleImageView profilePic =  v.findViewById(R.id.id_pict_profile);
        ImageView statusPic =  v.findViewById(R.id.id_ic_status);
        if (mCurrentBroker != null && mCurrentBroker.getBoolean(Constants
                .ParseTable.TableUser.EMAILVERIFIED)) {
            textName.setText(mCurrentBroker.getString(Constants.ParseTable.TableUser.LONGNAME));
            textBro.setText(mCurrentBroker.get(Constants.ParseTable.TableUser.STATUS).toString());
            textPoint.setText("Points " + mCurrentBroker.get(Constants.ParseTable.TableUser.POINT).toString());

            if ((mCurrentBroker.get(Constants.ParseTable.TableUser.STATUS).toString())
                    .equals(Constants.BroStatus.STATUS1))
                statusPic.setImageResource(R.drawable.diamond1);
            else if ((mCurrentBroker.get(Constants.ParseTable.TableUser.STATUS).toString())
                    .equals(Constants.BroStatus.STATUS2))
                statusPic.setImageResource(R.drawable.diamond2);
            else if ((mCurrentBroker.get(Constants.ParseTable.TableUser.STATUS).toString())
                    .equals(Constants.BroStatus.STATUS3))
                statusPic.setImageResource(R.drawable.diamond3);

            if(DeprooApplication.initState) {
                AsyncGetBrokerThumb task = new AsyncGetBrokerThumb(profilePic);
                task.execute(mCurrentBroker);
            } else {
                profilePic.setImageBitmap(mCurrentBroker.getProfilePicThumb());
            }
        }
    }

    private void initSlider(View v) {
        mDemoSlider = v.findViewById(R.id.slider);
        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Promo Deproo 1",R.drawable.slide1);
        file_maps.put("Promo Deproo 2",R.drawable.slide2);
        file_maps.put("TB Makmur Spesialis Alat Bangunan",R.drawable.slide3);
        file_maps.put("Promo hari ini di Supermarket Bangunan", R.drawable.slide4);

        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(mContext);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra",name);
            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
    }

    private void initClickListener(View v) {
        LinearLayout asset1 =  v.findViewById(R.id.id_ly_p1_1);
        LinearLayout asset2 =  v.findViewById(R.id.id_ly_p1_2);
        LinearLayout asset3 =  v.findViewById(R.id.id_ly_p1_3);
        LinearLayout asset4 =  v.findViewById(R.id.id_ly_p1_4);
        LinearLayout asset5 =  v.findViewById(R.id.id_ly_p1_5);
        asset1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAssets!=null && mAssets.get(0)!=null && mAssets.size()>0) {
                    ((MainActivity) getActivity()).onAssetClick(mAssets.get(0));
                }
            }
        });
        asset2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAssets!=null && mAssets.size()>0 && mAssets.get(1)!=null) {
                    ((MainActivity) getActivity()).onAssetClick(mAssets.get(1));
                }
            }
        });
        asset3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAssets!=null && mAssets.size()>0 && mAssets.get(2)!=null) {
                    ((MainActivity) getActivity()).onAssetClick(mAssets.get(2));
                }
            }
        });
        asset4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAssets!=null && mAssets.size()>0 && mAssets.get(3)!=null) {
                    ((MainActivity) getActivity()).onAssetClick(mAssets.get(3));
                }
            }
        });
        asset5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAssets!=null && mAssets.size()>0 && mAssets.get(4)!=null) {
                    ((MainActivity) getActivity()).onAssetClick(mAssets.get(4));
                }
            }
        });

        LinearLayout lyBroker1 = v.findViewById(R.id.id_ly_b1_1);
        LinearLayout lyBroker2 = v.findViewById(R.id.id_ly_b1_2);
        LinearLayout lyBroker3 = v.findViewById(R.id.id_ly_b1_3);
        LinearLayout lyBroker4 = v.findViewById(R.id.id_ly_b1_4);
        LinearLayout lyBroker5 = v.findViewById(R.id.id_ly_b1_5);
        lyBroker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBrokers !=null && mBrokers.size()>0 && mBrokers.get(0)!=null) {
                    ((MainActivity) getActivity()).onBrokerClick(mBrokers.get(0));
                }
            }
        });
        lyBroker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBrokers !=null && mBrokers.size()>0 && mBrokers.get(1)!=null) {
                    ((MainActivity) getActivity()).onBrokerClick(mBrokers.get(1));
                }
            }
        });
        lyBroker3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBrokers !=null && mBrokers.size()>0 && mBrokers.get(2)!=null) {
                    ((MainActivity) getActivity()).onBrokerClick(mBrokers.get(2));
                }
            }
        });
        lyBroker4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBrokers !=null && mBrokers.size()>0 && mBrokers.get(3)!=null) {
                    ((MainActivity) getActivity()).onBrokerClick(mBrokers.get(3));
                }
            }
        });
        lyBroker5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBrokers !=null && mBrokers.size()>0 && mBrokers.get(4)!=null) {
                    ((MainActivity) getActivity()).onBrokerClick(mBrokers.get(4));
                }
            }
        });
    }

    private void getNewBrokerX(View view) {
        /* New User */
        final View v = view;
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.addAscendingOrder(Constants.ParseTable.TableUser.CREATED_AT);
        query.setLimit(5);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e==null) {
                    if (parseUsers.size() > 0) {
                        for (int i = 0; i < parseUsers.size(); i++) {
                            Broker broker = (Broker) parseUsers.get(i);
                            mBrokers.add(broker);
                            DeprooApplication.sharedBrokerList.add(broker);
                            String namepic = "id_nbroker_" + Integer.toString(i+1);
                            LoaderImageView iv = v.findViewById(Utils.getResIdOfComponent(mContext,namepic));
                            AsyncGetBrokerThumb task = new AsyncGetBrokerThumb(iv);
                            task.execute(mBrokers.get(i));
                            String name1 = "id_tnbroker_" + Integer.toString(i+1) + "_1";
                            String name2 = "id_tnbroker_" + Integer.toString(i+1) + "_2";
                            LoaderTextView tv1 = v.findViewById(Utils.getResIdOfComponent(mContext,name1));
                            LoaderTextView tv2 = v.findViewById(Utils.getResIdOfComponent(mContext,name2));
                            tv1.setText(broker.getLongname());
                            tv2.setText(broker.getTitle());
                        }
                    }
                }
            }
        });
    }

    private void getAssetX(View view) {
        final View v = view;
        ParseQuery<Asset> query = ParseQuery.getQuery(Asset.class);
        query.addDescendingOrder(Constants.ParseTable.TableAsset.CREATED_AT);
        query.include(Constants.ParseTable.TableAsset.USER);
        query.setLimit(5);
        query.findInBackground(new FindCallback<Asset>() {
            @Override
            public void done(List<Asset> assets, ParseException e) {
                if(e==null) {
                    if (assets.size() > 0) {
                        // get Image associate with this Asset
                        mAssets.clear();
                        mAssets.addAll(assets);
                        DeprooApplication.sharedAssetList = mAssets;
                        getImageOfAsset(assets);
                        for (int i = 0; i < assets.size(); i++) {
                            Asset asset = assets.get(i);
                            int price = (int) asset.getPrice();
                            String formattedPrice = Utils.formatIDR((double)price);
                            String title = asset.getTitle();
                            String name1 = "id_tasset_" + Integer.toString(i+1) + "_1";
                            String name2 = "id_tasset_" + Integer.toString(i+1) + "_2";
                            String name = "id_asset_" + Integer.toString(i+1);
                            LoaderTextView tv1 = v.findViewById(Utils.getResIdOfComponent(mContext, name1));
                            LoaderTextView tv2 = v.findViewById(Utils.getResIdOfComponent(mContext, name2));
                            LoaderImageView iv = v.findViewById(Utils.getResIdOfComponent(mContext,name));
                            tv1.setText(formattedPrice);
                            tv2.setText(title);
                        }
                    }
                }
            }
        });
    }

    private void getImageOfAsset(final List<Asset> assets) {
        if (assets.size()==0) return;

        ParseQuery<AssetImage> imageQuery1 = ParseQuery.getQuery(AssetImage.class);
        imageQuery1.whereEqualTo(Constants.ParseTable.TableAssetImage.ASSET, assets.get(0));
        imageQuery1.getFirstInBackground(new GetCallback<AssetImage>() {
            @Override
            public void done(AssetImage assetImage, ParseException e) {
                if(e==null) {
                    AsyncAssetThumbnail task = new AsyncAssetThumbnail(mNewAssetPic1,
                            assets.get(0).getObjectId());
                    task.execute(assetImage);
                }
            }
        });

        ParseQuery<AssetImage> imageQuery2 = ParseQuery.getQuery(AssetImage.class);
        imageQuery2.whereEqualTo(Constants.ParseTable.TableAssetImage.ASSET, assets.get(1));
        imageQuery2.getFirstInBackground(new GetCallback<AssetImage>() {
            @Override
            public void done(AssetImage assetImage, ParseException e) {
                if(e==null) {
                    AsyncAssetThumbnail task = new AsyncAssetThumbnail(mNewAssetPic2,
                            assets.get(1).getObjectId());
                    task.execute(assetImage);
                } else {

                }
            }
        });

        ParseQuery<AssetImage> imageQuery3 = ParseQuery.getQuery(AssetImage.class);
        imageQuery3.whereEqualTo(Constants.ParseTable.TableAssetImage.ASSET, assets.get(2));
        imageQuery3.getFirstInBackground(new GetCallback<AssetImage>() {
            @Override
            public void done(AssetImage assetImage, ParseException e) {
                if(e==null) {
                    AsyncAssetThumbnail task = new AsyncAssetThumbnail(mNewAssetPic3,
                            assets.get(2).getObjectId());
                    task.execute(assetImage);
                } else {

                }
            }
        });

        ParseQuery<AssetImage> imageQuery4 = ParseQuery.getQuery(AssetImage.class);
        imageQuery4.whereEqualTo(Constants.ParseTable.TableAssetImage.ASSET, assets.get(3));
        imageQuery4.getFirstInBackground(new GetCallback<AssetImage>() {
            @Override
            public void done(AssetImage assetImage, ParseException e) {
                if(e==null) {
                    AsyncAssetThumbnail task = new AsyncAssetThumbnail(mNewAssetPic4,
                            assets.get(3).getObjectId());
                    task.execute(assetImage);
                } else {

                }
            }
        });

        ParseQuery<AssetImage> imageQuery5 = ParseQuery.getQuery(AssetImage.class);
        imageQuery5.whereEqualTo(Constants.ParseTable.TableAssetImage.ASSET, assets.get(4));
        imageQuery5.getFirstInBackground(new GetCallback<AssetImage>() {
            @Override
            public void done(AssetImage assetImage, ParseException e) {
                if(e==null) {
                    AsyncAssetThumbnail task = new AsyncAssetThumbnail(mNewAssetPic5,
                            assets.get(4).getObjectId());
                    task.execute(assetImage);
                } else {

                }
            }
        });
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(mContext, "toast", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int i) {
        LinearLayout linearLayout = getView().findViewById(R.id.id_layout_drawable);
        if (i == 0) {
            if(mPreviousPosition==1) {
                linearLayout.setBackgroundResource(R.drawable.top_bg_anim0s);
                TransitionDrawable anim = (TransitionDrawable ) linearLayout.getBackground();
                anim.startTransition(500);
            }
        } else if(i==1) {
            if(mPreviousPosition==0) {
                linearLayout.setBackgroundResource(R.drawable.top_bg_anim0);
                TransitionDrawable anim = (TransitionDrawable ) linearLayout.getBackground();
                anim.startTransition(500);
            } else if(mPreviousPosition==2) {
                linearLayout.setBackgroundResource(R.drawable.top_bg_anim1s);
                TransitionDrawable anim = (TransitionDrawable ) linearLayout.getBackground();
                anim.startTransition(500);
            }
        } else if(i==2) {
            if(mPreviousPosition==1) {
                linearLayout.setBackgroundResource(R.drawable.top_bg_anim1);
                TransitionDrawable anim = (TransitionDrawable ) linearLayout.getBackground();
                anim.startTransition(500);
            } else if(mPreviousPosition==3) {
                linearLayout.setBackgroundResource(R.drawable.top_bg_anim2s);
                TransitionDrawable anim = (TransitionDrawable ) linearLayout.getBackground();
                anim.startTransition(500);
            }
        } else if(i==3) {
            if(mPreviousPosition==2) {
                linearLayout.setBackgroundResource(R.drawable.top_bg_anim2);
                TransitionDrawable anim = (TransitionDrawable ) linearLayout.getBackground();
                anim.startTransition(500);
            } else if(mPreviousPosition==4) {
                linearLayout.setBackgroundResource(R.drawable.top_bg_anim3);
                TransitionDrawable anim = (TransitionDrawable ) linearLayout.getBackground();
                anim.reverseTransition(500);
            }
        } else if(i==4) {
            if(mPreviousPosition==3) {
                linearLayout.setBackgroundResource(R.drawable.top_bg_anim3);
                TransitionDrawable anim = (TransitionDrawable ) linearLayout.getBackground();
                anim.startTransition(500);
            }
        }
        mPreviousPosition = i;
    }

    private class AsyncAssetThumbnail extends AsyncTask<ParseObject, Integer, Bitmap> {

        private final WeakReference<LoaderImageView> loaderImageReference;
        private String mObjectID;
        private Uri mImageUri;

        public AsyncAssetThumbnail(LoaderImageView loaderView, String objectID) {
            loaderImageReference = new WeakReference<>(loaderView);
            mObjectID = objectID;
        }

        @Override
        protected Bitmap doInBackground(ParseObject... parseObjects) {
            ParseObject object = parseObjects[0];
            Bitmap bitmap = null;
            try {
                ParseFile parseFile = object.getParseFile(Constants.ParseTable.TableAssetImage.IMAGE_THUMB);
                byte[] data = parseFile.getData();
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                String imgAddress = parseFile.getUrl();
                mImageUri = Uri.parse(imgAddress);

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(loaderImageReference != null && bitmap != null) {
                final LoaderImageView loaderView = loaderImageReference.get();
                if(loaderView != null) {
                    loaderView.setImageBitmap(bitmap);
                }
                for(int i=0;i<mAssets.size();i++) {
                    if(mAssets.get(i).getObjectId().equals(mObjectID)) {
                        mAssets.get(i).setImage(bitmap);
                        mAssets.get(i).setImageUri(mImageUri);
                        DeprooApplication.sharedAssetList.get(i).setImage(bitmap);
                        DeprooApplication.sharedAssetList.get(i).setImageUri(mImageUri);
                    }
                }
            }
        }
    }

    private class AsyncProfileThumbnail extends AsyncTask<ParseUser, Integer, Bitmap> {

        private final WeakReference<CircleImageView> circleImageReference;

        public AsyncProfileThumbnail(CircleImageView circleImageView) {
            circleImageReference = new WeakReference<>(circleImageView);
        }

        @Override
        protected Bitmap doInBackground(ParseUser... parseUsers) {
            ParseUser user = parseUsers[0];
            ParseFile parseFile = user.getParseFile(Constants.ParseTable.TableUser.PROFILE_THUMB);
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
            if(circleImageReference != null && bitmap != null) {
                final CircleImageView circleImageView = circleImageReference.get();
                if(circleImageView != null) {
                    circleImageView.setImageBitmap(bitmap);
                }
            }
        }
    }

}
