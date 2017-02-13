package com.rohail.beyondinfinity.news.hub.newshub.util;

/**
 * Created by rohail on 11/11/2016.
 */

public class Constants {

    public static final String API_KEY = "&apiKey=8343dd4efbea4c8bb31e6aef5e47b9b4";
    public static final String baseUrl = "http://newsapi.org/v1/";
    public static final String articles = "articles?source=";
    public static final String sources = "sources/";

    public static final String subProductUrl = "product.asmx/Getproduct?subcatid=";
    public static final String loginUrl = "login.asmx/Getloginresponse?";
    public static final String registerUrl = "register.asmx/processregister?";
    public static final String checkoutUrl = "checkout.asmx/Getcheckout?";
    public static final String historyUrl = "history.asmx/Gethistory?userid=";

    public static final String KEY_SUB_CATEGORIES = "key_sub_categories";
    public static final String KEY_HEADER_NAME = "key_header_name";
    public static final String KEY_SUB_PRODUCT = "key_sub_product";
    public static final String KEY_HISTORY_LIST = "key_history_list";

    public class IntentKeys {
        public static final String ARTICLE_MODEL = "article_model_key";
    }
}
