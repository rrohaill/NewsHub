package com.rohail.beyondinfinity.news.hub.newshub.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.rohail.beyondinfinity.news.hub.newshub.Adapters.ArticleAdapter;
import com.rohail.beyondinfinity.news.hub.newshub.Adapters.IOnItemClickCustomListner;
import com.rohail.beyondinfinity.news.hub.newshub.R;
import com.rohail.beyondinfinity.news.hub.newshub.models.ArticleModel;
import com.rohail.beyondinfinity.news.hub.newshub.util.Constants;

public class ArticlesActivity extends BaseActivity implements IOnItemClickCustomListner {

    private RecyclerView gridview;
    private ArticleAdapter imgAdapter;
    private ArticleModel articleModel;
    private AdView adView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawer();
        enableViews(true);
        fetchIntents();
        initView();
        showBannerAd();
        showInterstitialAd();

    }

    private void showInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_id));

        mInterstitialAd.loadAd(new AdRequest.Builder()
//                .addTestDevice("D823DFBE13477D4EF2A8C8EAF73F2E87")
                .build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
//                requestNewInterstitial();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }
        });


    }

    private void showBannerAd() {
        adView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

    }

    private void fetchIntents() {
        articleModel = (ArticleModel) getIntent().getExtras().get(Constants.IntentKeys.ARTICLE_MODEL);
    }

    private void initView() {
        findViewById(R.id.searchView).setVisibility(View.GONE);

        gridview = (RecyclerView) findViewById(R.id.gridView);
        imgAdapter = new ArticleAdapter(this, articleModel.getArticles(), this);
//        imgAdapter.setClickListener(this);
        gridview.setAdapter(imgAdapter);

        gridview.setLayoutManager(new GridLayoutManager(this, 1));

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        gridview.setItemAnimator(itemAnimator);
    }

    @Override
    public void onItemClicked(View view, int position) {
        Intent intent = new Intent(ArticlesActivity.this, ArticleDetailActivity.class);
        intent.putExtra(Constants.IntentKeys.ARTICLE, articleModel.getArticles()[position]);
        startActivity(intent);
    }
}
