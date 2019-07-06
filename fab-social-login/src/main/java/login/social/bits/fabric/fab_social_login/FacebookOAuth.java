package login.social.bits.fabric.fab_social_login;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class FacebookOAuth {


    final String TAG = "FacebookOAuth";
    final List<String> fields = Arrays.asList("email");

    CallbackManager callbackManager;
    SocialLoginCallback callback;

    public FacebookOAuth(final SocialLoginCallback callback) {
        this.callback=callback;
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                       buildResult(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.w(TAG, "cancelled!");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        exception.printStackTrace();
                    }
                });
    }

    public void signIn(final Activity activity) {
        AccessToken.refreshCurrentAccessTokenAsync(new AccessToken.AccessTokenRefreshCallback() {
            @Override
            public void OnTokenRefreshed(AccessToken accessToken) {
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                if (isLoggedIn) {
                    buildResult(accessToken);
                } else {
                    LoginManager.getInstance().logInWithReadPermissions(activity, fields);
                }
            }

            @Override
            public void OnTokenRefreshFailed(FacebookException exception) {
                Log.w(TAG, "OnTokenRefreshFailed!!");
                LoginManager.getInstance().logInWithReadPermissions(activity, fields);
            }
        });
    }


    public void signOut() {
        LoginManager.getInstance().logOut();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void buildResult(AccessToken accessToken) {
        Log.w(TAG, "success=" + accessToken.getToken());
        readUsingToken(accessToken);
    }

    private Uri getPicture(JSONObject object){
        try {
            String url= object.getJSONObject("picture").getJSONObject("data").getString("url");
            if(url!=null)
                return Uri.parse(url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void readUsingToken(final AccessToken accessToken){
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    Log.w(TAG,"GraphRequest Success:"+object.toString());
                    if(callback!=null) {
                        SocialLoginResult result = new SocialLoginResult();
                        result.access_token=accessToken.getToken();
                        result.first_name = object.getString("first_name");
                        result.name = object.getString("name");
                        result.email = object.getString("email");
                        result.last_name = object.getString("last_name");
                        result.picture= getPicture(object);
                        callback.onSocialLogin(SocialAuthType.facebook, result);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email,first_name,last_name,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
