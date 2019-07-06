package login.social.bits.fabric.fab_social_login;

import android.app.Activity;
import android.content.Intent;

import com.facebook.FacebookSdk;

public class SocialOauth {


    GoogleOAuth goa;
    FacebookOAuth foa;



    public static SocialOauth getInstance(Activity activity, String google_client_id,SocialLoginCallback callback){
        return new SocialOauth(activity,google_client_id,callback);
    }



    private SocialOauth(Activity activity, String google_client_id,SocialLoginCallback callback) {
        goa = new GoogleOAuth(activity, google_client_id,callback);
        foa = new FacebookOAuth(callback);
    }

    public void signIn(Activity activity, SocialAuthType socialAuthType) {
        switch (socialAuthType) {
            case google:
                goa.signIn(activity);
                break;
            case facebook:
                foa.signIn(activity);
                break;
        }
    }

    public void signOut(Activity activity,SocialAuthType socialAuthType) {
        switch (socialAuthType) {
            case google:
                goa.signOut(activity);
                break;
            case facebook:
                foa.signOut();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        goa.onActivityResult(requestCode,resultCode,data);
        foa.onActivityResult(requestCode,resultCode,data);
    }


}


