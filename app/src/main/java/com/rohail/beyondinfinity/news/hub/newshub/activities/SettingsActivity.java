package com.rohail.beyondinfinity.news.hub.newshub.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.rohail.beyondinfinity.news.hub.newshub.R;
import com.rohail.beyondinfinity.news.hub.newshub.util.AppToastMaker;
import com.rohail.beyondinfinity.news.hub.newshub.util.PreferenceConnector;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import static com.rohail.beyondinfinity.news.hub.newshub.activities.ArticleDetailActivity.WEBVIEW_REQUEST_CODE;
import static com.rohail.beyondinfinity.news.hub.newshub.util.PreferenceConnector.PREF_KEY_OAUTH_SECRET;
import static com.rohail.beyondinfinity.news.hub.newshub.util.PreferenceConnector.PREF_KEY_OAUTH_TOKEN;
import static com.rohail.beyondinfinity.news.hub.newshub.util.PreferenceConnector.PREF_KEY_TWITTER_LOGIN;
import static com.rohail.beyondinfinity.news.hub.newshub.util.PreferenceConnector.PREF_PROFILE_IMAGE;
import static com.rohail.beyondinfinity.news.hub.newshub.util.PreferenceConnector.PREF_USER_NAME;

public class SettingsActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {


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

    private SwitchCompat swFB;
    private FacebookCallback<LoginResult> shareCallback = new FacebookCallback<LoginResult>() {

        @Override
        public void onSuccess(LoginResult result) {
            swFB.setChecked(getFacebookManger().isLoggedIn());
        }

        @Override
        public void onCancel() {
            AppToastMaker.showShortToast(SettingsActivity.this, "Cancel");
            swFB.setChecked(getFacebookManger().isLoggedIn());
        }

        @Override
        public void onError(FacebookException error) {
            AppToastMaker.showShortToast(SettingsActivity.this, "Error");
            swFB.setChecked(getFacebookManger().isLoggedIn());
        }

    };
    private SwitchCompat swTwitter;
    private AdView adView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initDrawer();
        enableViews(true);

        initViews();
    }

    private void initViews() {
        swFB = (SwitchCompat) findViewById(R.id.sw_fb);
        swTwitter = (SwitchCompat) findViewById(R.id.sw_twitter);

        swFB.setOnCheckedChangeListener(this);
        swTwitter.setOnCheckedChangeListener(this);

        swFB.setChecked(getFacebookManger().isLoggedIn());

        swTwitter.setChecked(isUserLoggedIn());


    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();

        switch (id) {
            case R.id.sw_fb:

                if (swFB.isChecked()) {
                    if (!getFacebookManger().isLoggedIn())
                        getFacebookManger().loginFacebook(shareCallback);
                } else {
                    getFacebookManger().callFacebookLogout();
                    swFB.setChecked(false);
                }

                break;
            case R.id.sw_twitter:
                if (swTwitter.isChecked()) {
                    if (!isUserLoggedIn())
                        loginUser();
                } else {
                    logoutUser();
                }
                break;
        }
    }

    public void loginUser() {

        new TokenGet().execute();

    }

    private boolean isUserLoggedIn() {
        // return twitter login status from Shared Preferences
        return PreferenceConnector.readBoolean(SettingsActivity.this, PREF_KEY_TWITTER_LOGIN, false);
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
                            SettingsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    swTwitter.setChecked(true);
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

    private void saveTwitterInfo(AccessToken accessToken) {

        long userID = accessToken.getUserId();

        User user;
        try {
            user = mTwitter.showUser(userID);

            String username = user.getName();
            String profilePicture = user.getOriginalProfileImageURL();
            /* Storing oAuth tokens to shared preferences */
            PreferenceConnector.writeString(SettingsActivity.this, PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            PreferenceConnector.writeString(SettingsActivity.this, PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            PreferenceConnector.writeBoolean(SettingsActivity.this, PREF_KEY_TWITTER_LOGIN, true);
            PreferenceConnector.writeString(SettingsActivity.this, PREF_USER_NAME, username);
            PreferenceConnector.writeString(SettingsActivity.this, PREF_PROFILE_IMAGE, profilePicture);

        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
    }

    public void logoutUser() {
        // Clear the shared preferences
        PreferenceConnector.writeString(SettingsActivity.this, PREF_KEY_OAUTH_TOKEN, "");
        PreferenceConnector.writeString(SettingsActivity.this, PreferenceConnector.PREF_KEY_OAUTH_SECRET, "");
        PreferenceConnector.writeBoolean(SettingsActivity.this, PreferenceConnector.PREF_KEY_TWITTER_LOGIN, false);
        PreferenceConnector.writeString(SettingsActivity.this, PreferenceConnector.PREF_USER_NAME, "");
        PreferenceConnector.writeString(SettingsActivity.this, PreferenceConnector.PREF_PROFILE_IMAGE, "");
        swTwitter.setChecked(false);
    }

    private class TokenGet extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SettingsActivity.this);
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

                auth_dialog = new Dialog(SettingsActivity.this);
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
                            swTwitter.setChecked(false);
                            Toast.makeText(SettingsActivity.this, "Sorry !, Permission Denied", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
                auth_dialog.show();
                auth_dialog.setCancelable(true);


            } else {
                swTwitter.setChecked(false);
                Toast.makeText(SettingsActivity.this, "Sorry !, Network Error or Invalid Credentials", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private class AccessTokenGet extends AsyncTask<String, String, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SettingsActivity.this);
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
                SettingsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swTwitter.setChecked(true);
                    }
                });

            }
        }


    }

}
