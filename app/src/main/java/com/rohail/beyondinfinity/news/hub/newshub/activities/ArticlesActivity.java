package com.rohail.beyondinfinity.news.hub.newshub.activities;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rohail.beyondinfinity.news.hub.newshub.Adapters.ArticleAdapter;
import com.rohail.beyondinfinity.news.hub.newshub.Adapters.IOnItemClickCustomListner;
import com.rohail.beyondinfinity.news.hub.newshub.R;
import com.rohail.beyondinfinity.news.hub.newshub.models.ArticleModel;
import com.rohail.beyondinfinity.news.hub.newshub.util.Constants;

public class ArticlesActivity extends BaseActivity implements IOnItemClickCustomListner {

    private RecyclerView gridview;
    private ArticleAdapter imgAdapter;
    private ArticleModel articleModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDrawer();

        fetchIntents();

        initView();

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

    }
}
