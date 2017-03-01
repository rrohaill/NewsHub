package com.rohail.beyondinfinity.news.hub.newshub.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.rohail.beyondinfinity.news.hub.newshub.Adapters.IOnItemClickCustomListner;
import com.rohail.beyondinfinity.news.hub.newshub.Adapters.SourcesAdapter;
import com.rohail.beyondinfinity.news.hub.newshub.BuildConfig;
import com.rohail.beyondinfinity.news.hub.newshub.Interfaces.JSONCommunicationManager;
import com.rohail.beyondinfinity.news.hub.newshub.Interfaces.NetworkCalls;
import com.rohail.beyondinfinity.news.hub.newshub.R;
import com.rohail.beyondinfinity.news.hub.newshub.models.ArticleModel;
import com.rohail.beyondinfinity.news.hub.newshub.models.NewsModel;
import com.rohail.beyondinfinity.news.hub.newshub.models.Sources;
import com.rohail.beyondinfinity.news.hub.newshub.util.Constants;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends BaseActivity
        implements IOnItemClickCustomListner, Filterable {

    private RecyclerView gridview;
    private NewsModel newsModel;
    private NewsModel filterList;
    private SourcesAdapter imgAdapter;
    private ArticleModel articleModel;
    private ValueFilter valueFilter;
    private SearchView searchView;
    private boolean doubleBackToExitPressedOnce = false;
    private AdView adView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawer();

        showBannerAd();

        magzinesCall();

        getHashKey();

    }

    private void getHashKey() {
        // temp
        // signed h9EOZq+fJ5ibn+qrmVyTsox3jpE=

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                // Log.d("KeyHash:",
                // Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Log.i("Key Hash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("package name", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.d("package name", e.toString());
        }
    }

    private void magzinesCall() {
        new NetworkCalls(new JSONCommunicationManager() {
            @Override
            public void onResponse(String response, JSONCommunicationManager jsonCommunicationManager) {
                try {

                    Log.i("Response :", response);
                    JSONObject jsonObject = new JSONObject(response);
                    Gson gson = new Gson();


                    if (jsonObject.get("status").equals("ok")) {
                        newsModel = gson.fromJson(jsonObject.toString(), NewsModel.class);
                        filterList = newsModel;
                        onProcessNext(null);
                    } else {
                        showAlert(getString(R.string.title_alert), jsonObject.get("message").toString(), MainActivity.this, "Retry", "Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dismissDialog();
                                magzinesCall();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dismissDialog();
                                finish();
                            }
                        });
                    }

                    hideLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                    hideLoading();
                    showAlert(getString(R.string.title_alert), getString(R.string.FETCHING_DETAILS_PROBLEM), MainActivity.this, "Retry", "Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismissDialog();
                            magzinesCall();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismissDialog();
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onProcessNext(ArrayList<Object> listObject) {
                initView();
                initSearch();
                showInterstitialAd();
            }

            @Override
            public void onPreRequest() {
                showLoading();
            }

            @Override
            public void onError(String s) {
                hideLoading();

                showAlert(getString(R.string.title_alert), s, MainActivity.this);
            }
        }, this).execute(Constants.sources);
    }

    private void initView() {
        gridview = (RecyclerView) findViewById(R.id.gridView);
        imgAdapter = new SourcesAdapter(this, filterList.getSources(), this);
//        imgAdapter.setClickListener(this);
        gridview.setAdapter(imgAdapter);

        gridview.setLayoutManager(new GridLayoutManager(this, 2));

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        gridview.setItemAnimator(itemAnimator);

    }

    private void initSearch() {
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Search");

        searchView.setFocusableInTouchMode(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }


    @Override
    public void onItemClicked(View view, int position) {

        newsSearchCall(position);

    }

    private void newsSearchCall(int position) {
        new NetworkCalls(new JSONCommunicationManager() {
            @Override
            public void onResponse(String response, JSONCommunicationManager jsonCommunicationManager) {
                try {

                    Log.i("Response :", response);
                    JSONObject jsonObject = new JSONObject(response);
                    Gson gson = new Gson();


                    if (jsonObject.get("status").equals("ok")) {
                        articleModel = gson.fromJson(jsonObject.toString(), ArticleModel.class);
                        onProcessNext(null);
                    } else {
                        showAlert(getString(R.string.title_alert), jsonObject.get("message").toString(), MainActivity.this, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dismissDialog();
                            }
                        });
                    }

                    hideLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                    hideLoading();
                    showAlert(getString(R.string.title_alert), getString(R.string.FETCHING_DETAILS_PROBLEM), MainActivity.this);
                }
            }

            @Override
            public void onProcessNext(ArrayList<Object> listObject) {
                Intent intent = new Intent(MainActivity.this, ArticlesActivity.class);
                intent.putExtra(Constants.IntentKeys.ARTICLE_MODEL, articleModel);
                startActivity(intent);
            }

            @Override
            public void onPreRequest() {
                showLoading();
            }

            @Override
            public void onError(String s) {
                hideLoading();

                showAlert(getString(R.string.title_alert), s, MainActivity.this);
            }
        }, this).execute(Constants.articles, filterList.getSources().get(position).getId());
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            showInterstitialAd();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void showBannerAd() {
        adView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

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

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                filterList = new NewsModel();
                ArrayList<Sources> sources = new ArrayList<>();
                for (int i = 0; i < newsModel.getSources().size(); i++) {
                    if ((newsModel.getSources().get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        sources.add(newsModel.getSources().get(i));
                    }
                }
                filterList.setSources(sources);
                results.count = filterList.getSources().size();
                results.values = filterList.getSources();
            } else {
                filterList = newsModel;
                results.count = newsModel.getSources().size();
                results.values = newsModel.getSources();
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            initView();
        }

    }
}
