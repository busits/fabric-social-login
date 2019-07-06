package login.social.bits.fabric.fab_social_login;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GoogleOAuth {

    final String TAG = "GoogleOAuth";
    final int RC_SIGN_IN = 301;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    SocialLoginCallback callback;

    public GoogleOAuth(Activity activity, String client_id, SocialLoginCallback callback) {
        this.callback = callback;
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(client_id)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public void signIn(Activity activity) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if (account != null) {
            buildResult(account);
        } else {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            activity.startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    public void signOut(Activity activity) {
        mGoogleSignInClient.signOut().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.w(TAG, "logout successfully!");
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void buildResult(GoogleSignInAccount account) {
        Log.w(TAG, "login successfully with token: " + account.getIdToken());
        if (callback != null) {
            SocialLoginResult result = new SocialLoginResult();
            result.access_token = account.getIdToken();
            result.first_name = account.getGivenName();
            result.name = account.getDisplayName();
            result.email = account.getEmail();
            result.last_name = account.getFamilyName();
            result.picture = account.getPhotoUrl();
            callback.onSocialLogin(SocialAuthType.google, result);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            buildResult(account);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

}
