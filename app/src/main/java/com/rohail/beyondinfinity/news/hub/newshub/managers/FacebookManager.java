package com.rohail.beyondinfinity.news.hub.newshub.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;
import com.rohail.beyondinfinity.news.hub.newshub.Interfaces.OnFacebookProfileResponse;
import com.rohail.beyondinfinity.news.hub.newshub.R;
import com.rohail.beyondinfinity.news.hub.newshub.models.CustomerInfoModel;
import com.rohail.beyondinfinity.news.hub.newshub.util.AppToastMaker;
import com.rohail.beyondinfinity.news.hub.newshub.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.net.ssl.HttpsURLConnection;

public class FacebookManager {

    private Activity mMain;
    FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {

        @Override
        public void onSuccess(Sharer.Result result) {
            AppToastMaker.showShortToast(mMain, "Successfully Posted");
        }

        @Override
        public void onCancel() {
            AppToastMaker.showShortToast(mMain, "Cancel");
        }

        @Override
        public void onError(FacebookException error) {
            AppToastMaker.showShortToast(mMain, "Error");
        }

    };
    private CustomerInfoModel customerModel;
    private OnFacebookProfileResponse onFacebookProfileResponse;
    private CallbackManager callbackManager;
    private boolean canPresentShareDialog;
    private String fbName = null;

    public FacebookManager(Activity pMain) {
        mMain = pMain;
        FacebookSdk.sdkInitialize(mMain);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void postStatusOnWall(String title, String description, String url) {
        ShareDialog shareDialog;
        // Can we present the share dialog for regular links?
        canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(mMain);
        shareDialog.registerCallback(callbackManager, shareCallback);
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(title)
                .setContentDescription(
                        description)
                .setContentUrl(Uri.parse(url)).build();

        ShareApi.share(linkContent, shareCallback);
    }

    public void postImageOnWall(String title, String description,
                                String imageUrl, final Context context) {

        Bundle params = new Bundle();
        params.putString("message", "Using Falcon"); // to be asked
        params.putString("description", description);
        params.putString("caption", "Powered by Inov8");
        params.putString("name", "At " + title);
        params.putString("picture", imageUrl);
        params.putString("link", Constants.URLs.fbShareUrl);
        /* make the API call */
        new GraphRequest(AccessToken.getCurrentAccessToken(), "me/feed",
                params, HttpMethod.POST, new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                        /* handle the result */
//                MerchantLocatorDetailsActivity activity = (MerchantLocatorDetailsActivity) context;
//                activity.updateSocialMediaStats();

            }
        }).executeAsync();

    }

    public void postStatusDialog(final String title, final String description,
                                 final String url, final FacebookCallback<ShareDialog.Result> callBackListner) {

        final ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle(title)
                .setContentDescription(
                        description)
                .setContentUrl(Uri.parse(url)).build();


        if (isLoggedIn()) {
            ShareDialog shareDialog;
            // Can we present the share dialog for regular links?
            canPresentShareDialog = ShareDialog.canShow(ShareLinkContent.class);
            callbackManager = CallbackManager.Factory.create();
            shareDialog = new ShareDialog(mMain);
            shareDialog.registerCallback(callbackManager,
                    callBackListner);

            Profile profile = Profile.getCurrentProfile();


            if (canPresentShareDialog) {
                shareDialog.show(linkContent);
            } else if (profile != null && hasPublishPermission()) {
                ShareApi.share(linkContent, shareCallback);
            }
        } else {
            loginFacebook(new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult result) {
                    AppToastMaker.showShortToast(mMain, "Logged in");

                    ShareDialog shareDialog;
                    // Can we present the share dialog for regular links?
                    canPresentShareDialog = ShareDialog
                            .canShow(ShareLinkContent.class);
                    callbackManager = CallbackManager.Factory.create();
                    shareDialog = new ShareDialog(mMain);
                    shareDialog.registerCallback(callbackManager, callBackListner);

                    Profile profile = Profile.getCurrentProfile();

                    if (canPresentShareDialog) {
                        shareDialog.show(linkContent);
                    } else if (profile != null && hasPublishPermission()) {
                        ShareApi.share(linkContent, shareCallback);
                    }
                }

                @Override
                public void onError(FacebookException error) {
                    AppToastMaker.showShortToast(mMain, "Error");
                }

                @Override
                public void onCancel() {
                    AppToastMaker.showLongToast(mMain, "Cancel");
                }
            });
        }

    }

    private void postPhotoDialog() {
        ShareDialog shareDialog = new ShareDialog(mMain);
        Bitmap image = BitmapFactory.decodeResource(mMain.getResources(),
                R.mipmap.ic_launcher);
        SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(image)
                .build();
        ArrayList<SharePhoto> photos = new ArrayList<SharePhoto>();
        photos.add(sharePhoto);

        SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                .setPhotos(photos).build();
        shareDialog.show(sharePhotoContent);
    }

    private void postPhoto() {
        Bitmap image = BitmapFactory.decodeResource(mMain.getResources(),
                R.mipmap.ic_launcher);
        SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(image)
                .build();
        ArrayList<SharePhoto> photos = new ArrayList<SharePhoto>();
        photos.add(sharePhoto);

        SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                .setPhotos(photos).build();
        ShareApi.share(sharePhotoContent, shareCallback);
    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null
                && accessToken.getPermissions().contains("publish_actions");
    }

    public void inviteFriend(String appLinkUrl, String previewImageUrl,
                             FacebookCallback<AppInviteDialog.Result> callback) {

        callbackManager = CallbackManager.Factory.create();

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl).build();

            AppInviteDialog appInviteDialog = new AppInviteDialog(mMain);

            if (callback != null) {
                appInviteDialog.registerCallback(callbackManager, callback);
            } else {
                appInviteDialog.registerCallback(callbackManager,
                        new FacebookCallback<AppInviteDialog.Result>() {
                            @Override
                            public void onSuccess(AppInviteDialog.Result result) {
                                Log.d("Invitation",
                                        "Invitation Sent Successfully");
                                AppToastMaker.showLongToast(
                                        mMain.getApplicationContext(),
                                        result.toString());

                                // finish();
                            }

                            @Override
                            public void onCancel() {
                                Log.d("Invitation", "cancel");
                                AppToastMaker.showLongToast(
                                        mMain.getApplicationContext(), "Cancel");
                            }

                            @Override
                            public void onError(FacebookException e) {
                                Log.d("Invitation", "Error Occured");
                                AppToastMaker.showLongToast(
                                        mMain.getApplicationContext(),
                                        e.getMessage());
                            }
                        });
            }

            appInviteDialog.show(content);
        }
    }

    public void postStatusGraph(String caption, String description, String msg, String link) {
        Bundle params = new Bundle();
        params.putString("message", msg);
        params.putString("link", link);
        params.putString("description", description);
        params.putString("caption", caption);
/* make the API call */
        new GraphRequest(
                getCurrentAccessToken(),
                "/me/feed",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        AppToastMaker.showLongToast(mMain, "Posted on Facebook.");
                    }
                }
        ).executeAsync();

//        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder().setContentTitle("title").setContentDescription("Description").setContentUrl(Uri.parse(Constants.URLs.fbShareUrl)).build();
//        ShareApi.share(shareLinkContent, shareCallback);
    }

    public void checkIn(String placeId, String title, String description,
                        String link, String imageUrl,
                        FacebookCallback<ShareDialog.Result> callBackListner) {

        ShareDialog shareDialog;
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(mMain);

        // with link checkin

//         ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                .setPlaceId(placeId)
//                .setContentTitle(title)
//                .setContentUrl(Uri.parse(link))
//                .setImageUrl(Uri.parse(imageUrl))
//                .setContentDescription(
//                        description + "\n" + "(" + Utility.getFormattedTime()
//                                + "  " + Utility.getFormattedDate() + ")")
//                .build();

        // simple checkin

        ShareContent linkContent = new ShareLinkContent.Builder()
                .setPlaceId(placeId).build();
        shareDialog.registerCallback(callbackManager, callBackListner);
        shareDialog.show(mMain, linkContent);
    }

    public boolean isLoggedIn() {
        if (AccessToken.getCurrentAccessToken() != null) {
            return true;
        } else {
            return false;
        }
    }

    public void loginFacebook(
            final FacebookCallback<LoginResult> callBackListner) {

        callbackManager = CallbackManager.Factory.create();

        // publish_actions
        LoginManager.getInstance().logInWithPublishPermissions(mMain, Collections.singletonList("publish_actions"));

        LoginManager.getInstance().registerCallback(callbackManager,
                callBackListner);
    }

    public void loginFacebookWithProfileInformation(
            final FacebookCallback<LoginResult> callBackListner) {

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(mMain,
                Arrays.asList("public_profile", "email"));

        LoginManager.getInstance().registerCallback(callbackManager,
                callBackListner);
    }

    public AccessToken getCurrentAccessToken() {
        return AccessToken.getCurrentAccessToken();
    }

    public String getName() {

        new GraphRequest(AccessToken.getCurrentAccessToken(), ""
                + AccessToken.getCurrentAccessToken().getUserId() + "", null,
                HttpMethod.GET, new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                        /* handle the result */
                Log.i("dataArray", response.toString());
                JSONObject jsonObject = response.getJSONObject();
                try {
                    fbName = jsonObject.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    AppToastMaker.showShortToast(mMain, e.getMessage());
                }
            }
        }).executeAsync();

        return fbName;
    }

    public void callFacebookLogout() {
        LoginManager.getInstance().logOut();
    }

    /*
     * Get profile info
     */
    public void getProfileInformation() {

        GraphRequest request = GraphRequest.newMeRequest(
                getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        try {
                            if (android.os.Build.VERSION.SDK_INT > 9) {
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                                Log.i("dataArray", response.toString());
                                customerModel = new CustomerInfoModel();
                                customerModel.setFname(object.getString("first_name"));
                                customerModel.setLname(object.getString("last_name"));
                                customerModel.setEmail(object.getString("email"));
                                customerModel.setGender(object.getString("gender"));
                                customerModel.setUid(object.getString("id"));

                                onFacebookProfileResponse = (OnFacebookProfileResponse) mMain;
                                onFacebookProfileResponse.onProfileResponse(customerModel);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, email, first_name, last_name," +
                " birthday, gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void loadProfilePicture(final ImageView ivProfile) {
//        fb_profile_photo.setProfileId(AccessToken.getCurrentAccessToken()
//                .getUserId());

        GraphRequest request = GraphRequest.newMeRequest(
                getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        response.getError();

                        try {
                            if (android.os.Build.VERSION.SDK_INT > 9) {
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                                String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");

                                URL fb_url = new URL(profilePicUrl);//small | noraml | large
                                HttpsURLConnection conn1 = (HttpsURLConnection) fb_url.openConnection();
                                HttpsURLConnection.setFollowRedirects(true);
                                conn1.setInstanceFollowRedirects(true);
                                Bitmap fb_img = BitmapFactory.decodeStream(conn1.getInputStream());

                                Bitmap circleBitmap = Bitmap.createBitmap(fb_img.getWidth(), fb_img.getHeight(), Bitmap.Config.ARGB_8888);

                                BitmapShader shader = new BitmapShader(fb_img, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                                Paint paint = new Paint();
                                paint.setShader(shader);

                                Canvas c = new Canvas(circleBitmap);
                                c.drawCircle(fb_img.getWidth() / 2, fb_img.getHeight() / 2, fb_img.getWidth() / 2, paint);

                                ivProfile.setImageBitmap(circleBitmap);
                                Constants.fbBitmap = circleBitmap;

                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            ivProfile.setImageDrawable(mMain.getResources().getDrawable(R.mipmap.stub));
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,picture.width(120).height(120)");
        request.setParameters(parameters);
        request.executeAsync();


    }

    private String getNameInitials() {
        return String.valueOf(Constants.customerInfo.getFname().toUpperCase().charAt(0));
    }


}
