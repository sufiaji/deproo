package com.deproo.android.deproo.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.deproo.android.deproo.R;
import com.deproo.android.deproo.model.Article;
import com.deproo.android.deproo.model.EndlessRecyclerViewScrollListener;
import com.deproo.android.deproo.model.News;
import com.deproo.android.deproo.model.NewsAdapter;
import com.deproo.android.deproo.utils.NewsApiClient;
import com.deproo.android.deproo.utils.NewsApiInterface;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsFragment extends Fragment {

    public static final String NEWSAPI_KEY = "0f666d6930b541b89dc80c038d231b68";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> mArticles = new ArrayList<>();
    private NewsAdapter adapter;
    private ProgressBar progress_big;
    private ImageView ivResult;
    private int mPage;

    public NewsFragment() {
        mPage = 1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        progress_big = view.findViewById(R.id.progress_news_big);
        progress_big.setVisibility(View.VISIBLE);

        ivResult = view.findViewById(R.id.id_img_result_news);
        ivResult.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.recyclerviewNews);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new NewsAdapter(mArticles, getActivity());
        recyclerView.setAdapter(adapter);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mPage += 1;
                loadJson(mPage);
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        loadJson(mPage);

//        loadJSON();

        return view;
    }

    private void loadJson(int page) {
        NewsApiInterface apiInterface = NewsApiClient.getApiClient().create(NewsApiInterface.class);
        Call<News> call = apiInterface.getNews("properti", "id", 10, page, NEWSAPI_KEY);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if(response.isSuccessful() && response.body().getArticles() != null) {
                    List<Article> articles = new ArrayList<>();
                    articles = response.body().getArticles();
                    if(articles.isEmpty() || articles.size()==0) {
                        ivResult.setVisibility(View.VISIBLE);
                        Toasty.info(getActivity(),"No result").show();
                    } else {
                        int adapterSize = adapter.getItemCount();
                        mArticles.addAll(articles);
                        adapter.notifyItemRangeInserted(adapterSize, articles.size());
                    }
                } else {
                    if(mArticles.size()==0 || mArticles.isEmpty()) {
                        ivResult.setVisibility(View.VISIBLE);
                        Toasty.warning(getActivity(), "Fail to get news feed").show();
                    } else {
                        ivResult.setVisibility(View.GONE);
                        Toasty.info(getActivity(), "End of feeds").show();
                    }
                }
                progress_big.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                ivResult.setVisibility(View.VISIBLE);
                progress_big.setVisibility(View.GONE);
                Toasty.error(getActivity(), "Error occured, please try again later").show();
            }
        });
    }

    public void loadJSON() {
        NewsApiInterface apiInterface = NewsApiClient.getApiClient().create(NewsApiInterface.class);

        Call<News> call;
        call = apiInterface.getNews2("properti", "id", 100, NEWSAPI_KEY);

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if(response.isSuccessful() && response.body().getArticles() != null) {
                    if(!mArticles.isEmpty()) {
                        mArticles.clear();
                    }
                    mArticles = response.body().getArticles();
                    if(mArticles.isEmpty() || mArticles.size()==0) {
                        // no result
                        ivResult.setVisibility(View.VISIBLE);
                        Toasty.info(getActivity(),"No result").show();
                    } else {
                        adapter = new NewsAdapter(mArticles, getActivity());
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                } else {
                    // fail
                    ivResult.setVisibility(View.VISIBLE);
                    Toasty.warning(getActivity(),"Fail to get news feed").show();
                }
                progress_big.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                // fail
                ivResult.setVisibility(View.VISIBLE);
                progress_big.setVisibility(View.GONE);
                Toasty.error(getActivity(), "Error occured, please try again later").show();
            }
        });
    }
}
