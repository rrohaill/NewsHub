package com.rohail.beyondinfinity.news.hub.newshub.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.rohail.beyondinfinity.news.hub.newshub.R;
import com.rohail.beyondinfinity.news.hub.newshub.models.Articles;
import com.rohail.beyondinfinity.news.hub.newshub.util.AppToastMaker;
import com.rohail.beyondinfinity.news.hub.newshub.util.Constants;
import com.rohail.beyondinfinity.news.hub.newshub.util.PreferenceConnector;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import static com.rohail.beyondinfinity.news.hub.newshub.util.Constants.TWITTER_APP;
import static com.rohail.beyondinfinity.news.hub.newshub.util.PreferenceConnector.PREF_KEY_OAUTH_SECRET;
import static com.rohail.beyondinfinity.news.hub.newshub.util.PreferenceConnector.PREF_KEY_OAUTH_TOKEN;
import static com.rohail.beyondinfinity.news.hub.newshub.util.PreferenceConnector.PREF_KEY_TWITTER_LOGIN;
import static com.rohail.beyondinfinity.news.hub.newshub.util.PreferenceConnector.PREF_PROFILE_IMAGE;
import static com.rohail.beyondinfinity.news.hub.newshub.util.PreferenceConnector.PREF_USER_NAME;

public class ArticleDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final int WEBVIEW_REQUEST_CODE = 100;
    static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";
    static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    /**
     * Register your here app https://dev.twitter.com/apps/new and get your
     * consumer key and secret
     */
    static String TWITTER_CONSUMER_KEY = "XMFE0L07p7UQ9D884TlLM9mXZ";
    static String TWITTER_CONSUMER_SECRET = "qexVUtJxuJeMMarsvicScsvycGU73mBNFEgFjmdfEQrFRAmmbJ";
    // Twitter
    private static Twitter mTwitter;
    private static RequestToken requestToken;
    private String oauth_url, oauth_verifier;
    private ProgressDialog pDialog;
    private Dialog auth_dialog;
    private WebView web;
    private Bitmap mProfileBitmap;
    // Shared Preferences
    private ImageView ivArticle;
    private TextView tvTitle;
    private TextView tvDetail;
    private TextView tvPublishTime;
    private TextView tvAuthor;
    private Button btnFB;
    private Button btnTwitter;
    private AppCompatButton btnDetail;
    private Articles article;
    private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {

        @Override
        public void onSuccess(Sharer.Result result) {
        }

        @Override
        public void onCancel() {
            AppToastMaker.showShortToast(ArticleDetailActivity.this, "Cancel");
        }

        @Override
        public void onError(FacebookException error) {
            AppToastMaker.showShortToast(ArticleDetailActivity.this, "Error");
        }

    };
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
        article = (Articles) getIntent().getExtras().get(Constants.IntentKeys.ARTICLE);
    }

    private void initView() {
        findViewById(R.id.searchView).setVisibility(View.GONE);
        findViewById(R.id.gridView).setVisibility(View.GONE);
        findViewById(R.id.detail_layout).setVisibility(View.VISIBLE);

        ivArticle = (ImageView) findViewById(R.id.iv_article);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDetail = (TextView) findViewById(R.id.tv_detail);
        tvPublishTime = (TextView) findViewById(R.id.tv_published);
        tvAuthor = (TextView) findViewById(R.id.tv_author);
        btnDetail = (AppCompatButton) findViewById(R.id.btn_detail);
        btnDetail.setOnClickListener(this);

        Picasso.with(this).load(article.getUrlToImage()).placeholder(R.mipmap.stub).into(ivArticle);
        tvTitle.setText(article.getTitle());
        tvDetail.setText(article.getDescription());
        tvPublishTime.setText("Published at " + article.getPublishedAt());
        tvAuthor.setText("Author: " + article.getAuthor());

        btnFB = (Button) findViewById(R.id.btn_fb);
        btnFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fbShare(article);
            }
        });
        btnTwitter = (Button) findViewById(R.id.btn_twitter);
        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call login twitter function
                if (isUserLoggedIn()) {
                    postTweet();
                } else {
                    loginUser();
                }
            }
        });

    }

    private void fbShare(Articles article) {
        getFacebookManger().postStatusDialog(getString(R.string.app_name), article.getDescription(), article.getUrl(), shareCallback);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(ArticleDetailActivity.this, WebViewActivity.class);
        intent.putExtra(Constants.IntentKeys.BROWSER_URL, article.getUrl());
        startActivity(intent);
    }

    /**
     * Calls when user click tweet button
     */
    public void postTweet() {
        // Call update status function
        // Get the status from EditText

        String tweet = article.getTitle().concat("\n").concat(article.getUrl()).concat(" ").concat(TWITTER_APP);

        if (tweet.length() > 140) {
            int urlLength = article.getUrl().length();
            int titleLength = article.getTitle().length();
            int appNameLegth = TWITTER_APP.length() + 2;
            String title = article.getTitle().substring(0, titleLength - (titleLength - ((urlLength + titleLength + appNameLegth) - 140)) - 3);
            title = title + "...";
            tweet = title.concat("\n").concat(article.getUrl()).concat("\n").concat(TWITTER_APP);
        }

        if (TextUtils.isEmpty(tweet)) {
            // EditText is empty
            Toast.makeText(getApplicationContext(),
                    "Please enter status message", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // update status
        new PostTweetOnTwitter().execute(tweet);

    }

    /**
     * Calls when user click login button
     */
    public void loginUser() {

        new TokenGet().execute();

//        new LoginUserOnTwitter().execute();

//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
//                configurationBuilder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
//                configurationBuilder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
//                Configuration configuration = configurationBuilder.build();
//                mTwitter = new TwitterFactory(configuration).getInstance();
//                try {
//                    requestToken = mTwitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
//
//                    /**
//                     *  Loading twitter login page on webview for authorization
//                     *  Once authorized, results are received at onActivityResult
//                     *  */
//
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setData(Uri
//                            .parse(requestToken.getAuthenticationURL()));
//                    ArticleDetailActivity.this.startActivityForResult(intent, WEBVIEW_REQUEST_CODE);
//
//                } catch (TwitterException e) {
//                    e.printStackTrace();
//                }
//            }
//        });


    }

    /**
     * Calls when user click logout button
     */
    public void logoutUser() {
        // Clear the shared preferences
        PreferenceConnector.writeString(ArticleDetailActivity.this, PREF_KEY_OAUTH_TOKEN, "");
        PreferenceConnector.writeString(ArticleDetailActivity.this, PreferenceConnector.PREF_KEY_OAUTH_SECRET, "");
        PreferenceConnector.writeBoolean(ArticleDetailActivity.this, PreferenceConnector.PREF_KEY_TWITTER_LOGIN, false);
        PreferenceConnector.writeString(ArticleDetailActivity.this, PreferenceConnector.PREF_USER_NAME, "");
        PreferenceConnector.writeString(ArticleDetailActivity.this, PreferenceConnector.PREF_PROFILE_IMAGE, "");
    }

    /**
     * Check user already logged in your application using twitter Login flag is
     * fetched from Shared Preferences
     */
    private boolean isUserLoggedIn() {
        // return twitter login status from Shared Preferences
        return PreferenceConnector.readBoolean(ArticleDetailActivity.this, PREF_KEY_TWITTER_LOGIN, false);
    }

    /**
     * Saving user information, after user is authenticated for the first time.
     * You don't need to show user to login, until user has a valid access toen
     */
    private void saveTwitterInfo(AccessToken accessToken) {

        long userID = accessToken.getUserId();

        User user;
        try {
            user = mTwitter.showUser(userID);

            String username = user.getName();
            String profilePicture = user.getOriginalProfileImageURL();
            /* Storing oAuth tokens to shared preferences */
            PreferenceConnector.writeString(ArticleDetailActivity.this, PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            PreferenceConnector.writeString(ArticleDetailActivity.this, PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            PreferenceConnector.writeBoolean(ArticleDetailActivity.this, PREF_KEY_TWITTER_LOGIN, true);
            PreferenceConnector.writeString(ArticleDetailActivity.this, PREF_USER_NAME, username);
            PreferenceConnector.writeString(ArticleDetailActivity.this, PREF_PROFILE_IMAGE, profilePicture);

        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == WEBVIEW_REQUEST_CODE && data != null) {
                final Uri uri = Uri.parse(data.getStringExtra("KEY_URI"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
                        try {
                            AccessToken accessToken = mTwitter.getOAuthAccessToken(requestToken, verifier);
                            saveTwitterInfo(accessToken);
                            ArticleDetailActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    postTweet();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (e.getMessage() != null) {
                                Log.e("Twitter-->", e.getMessage());

                            } else {
                                Log.e("Twitter-->", "ERROR: Twitter callback failed");
                            }
                        }
                    }
                }).start();
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Function to update status
     */
    class PostTweetOnTwitter extends AsyncTask<String, Void, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ArticleDetailActivity.this);
            pDialog.setMessage("Updating to twitter...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(String... args) {
            Log.d("Tweet Text", "> " + args[0]);
            String status = args[0];
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);

                // Access Token
                String access_token = PreferenceConnector.readString(ArticleDetailActivity.this, PREF_KEY_OAUTH_TOKEN, "");
                // Access Token Secret
                String access_token_secret = PreferenceConnector.readString(ArticleDetailActivity.this, PREF_KEY_OAUTH_SECRET, "");

                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

                // Update status
                twitter4j.Status response = twitter.updateStatus(status);

                Log.d("Status", "> " + response.getText());
            } catch (TwitterException e) {
                // Error in updating status
                Log.d("Twitter Update Error", e.getMessage());
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
        }

    }

    /**
     * Function to load profile picture
     */
    private class LoadProfilePicture extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ArticleDetailActivity.this);
            pDialog.setMessage("Loading profile ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        /**
         * Download image from the url
         **/
        protected Bitmap doInBackground(String... args) {
            try {
                mProfileBitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mProfileBitmap;
        }

        /**
         * After completing background task Dismiss the progress dialog and set bitmap to imageview
         **/
        protected void onPostExecute(Bitmap image) {
            Bitmap image_circle = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);

            BitmapShader shader = new BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Paint paint = new Paint();
            paint.setShader(shader);
            Canvas c = new Canvas(image_circle);
            c.drawCircle(image.getWidth() / 2, image.getHeight() / 2, image.getWidth() / 2, paint);
//            mProfileImage.setImageBitmap(image_circle);

            pDialog.hide();

        }
    }

    private class TokenGet extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ArticleDetailActivity.this);
            pDialog.setTitle("Connecting");
            pDialog.setMessage("Please wait while ...");
            pDialog.show();
            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            configurationBuilder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = configurationBuilder.build();
            mTwitter = new TwitterFactory(configuration).getInstance();
            //   Toast.makeText(getActivity(),"ncjdncj",Toast.LENGTH_SHORT).show();

        }

        @Override
        protected String doInBackground(String... args) {

            try {
                requestToken = mTwitter.getOAuthRequestToken();
                oauth_url = requestToken.getAuthorizationURL();

            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return oauth_url;
        }

        @Override
        protected void onPostExecute(String oauth_url) {
            pDialog.dismiss();
            if (oauth_url != null) {
                Log.e("URL", oauth_url);

                auth_dialog = new Dialog(ArticleDetailActivity.this);
                auth_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                auth_dialog.setContentView(R.layout.auth_dialog);
                web = (WebView) auth_dialog.findViewById(R.id.webv);
                web.getSettings().setJavaScriptEnabled(true);
                web.loadUrl(oauth_url);
                web.setWebViewClient(new WebViewClient() {
                    boolean authComplete = false;

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (url.contains("oauth_verifier") && authComplete == false) {
                            authComplete = true;
                            Log.e("Url", url);
                            Uri uri = Uri.parse(url);
                            oauth_verifier = uri.getQueryParameter("oauth_verifier");

                            auth_dialog.dismiss();

                            new AccessTokenGet().execute();
                        } else if (url.contains("denied")) {
                            auth_dialog.dismiss();
                            Toast.makeText(ArticleDetailActivity.this, "Sorry !, Permission Denied", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
                auth_dialog.show();
                auth_dialog.setCancelable(true);


            } else {

                Toast.makeText(ArticleDetailActivity.this, "Sorry !, Network Error or Invalid Credentials", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private class AccessTokenGet extends AsyncTask<String, String, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ArticleDetailActivity.this);
            pDialog.setMessage("Fetching Data ...");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setIndeterminate(true);
            pDialog.show();

        }


        @Override
        protected Boolean doInBackground(String... args) {
            List<User> friendList = null;
            try {


                saveTwitterInfo(mTwitter.getOAuthAccessToken(requestToken, oauth_verifier));


            } catch (TwitterException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();


            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            pDialog.dismiss();
            if (response) {
                ArticleDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postTweet();
                    }
                });

            }
        }


    }


}
