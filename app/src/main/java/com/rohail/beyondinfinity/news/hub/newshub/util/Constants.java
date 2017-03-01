package com.rohail.beyondinfinity.news.hub.newshub.util;

import android.graphics.Bitmap;

import com.rohail.beyondinfinity.news.hub.newshub.models.CustomerInfoModel;

/**
 * Created by rohail on 11/11/2016.
 */

public class Constants {

    public static final String API_KEY = "&apiKey=8343dd4efbea4c8bb31e6aef5e47b9b4";
    public static final String baseUrl = "http://newsapi.org/v1/";
    public static final String articles = "articles?source=";
    public static final String sources = "sources/";
    public static final String TWITTER_APP = "#OfficialNewsHub";

    public static CustomerInfoModel customerInfo = new CustomerInfoModel();
    public static Bitmap fbBitmap = null;

    public class IntentKeys {
        public static final String ARTICLE_MODEL = "article_model_key";
        public static final String ARTICLE = "article_key";
        public static final String BROWSER_URL = "extra_url";
    }

    public class URLs {
        public static final String fbShareUrl = "www.google.com";
    }
}
