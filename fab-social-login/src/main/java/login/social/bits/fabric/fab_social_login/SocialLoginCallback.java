package login.social.bits.fabric.fab_social_login;

public interface SocialLoginCallback {
    public void onSocialLogin(SocialAuthType authType, SocialLoginResult socialLoginResult);
}
