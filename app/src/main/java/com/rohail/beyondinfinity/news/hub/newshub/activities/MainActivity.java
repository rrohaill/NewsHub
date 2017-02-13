package com.rohail.beyondinfinity.news.hub.newshub.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.rohail.beyondinfinity.news.hub.newshub.Adapters.IOnItemClickCustomListner;
import com.rohail.beyondinfinity.news.hub.newshub.Adapters.SourcesAdapter;
import com.rohail.beyondinfinity.news.hub.newshub.Interfaces.JSONCommunicationManager;
import com.rohail.beyondinfinity.news.hub.newshub.Interfaces.NetworkCalls;
import com.rohail.beyondinfinity.news.hub.newshub.R;
import com.rohail.beyondinfinity.news.hub.newshub.models.ArticleModel;
import com.rohail.beyondinfinity.news.hub.newshub.models.NewsModel;
import com.rohail.beyondinfinity.news.hub.newshub.util.Constants;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends BaseActivity
        implements IOnItemClickCustomListner {

    private RecyclerView gridview;
    private NewsModel newsModel;
    private SourcesAdapter imgAdapter;
    private ArticleModel articleModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawer();

        magzinesCall();

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
                initView();
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
        imgAdapter = new SourcesAdapter(this, newsModel.getSources(), this);
//        imgAdapter.setClickListener(this);
        gridview.setAdapter(imgAdapter);

        gridview.setLayoutManager(new GridLayoutManager(this, 2));

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        gridview.setItemAnimator(itemAnimator);
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
        }, this).execute(Constants.articles, newsModel.getSources()[position].getId());
    }
}
