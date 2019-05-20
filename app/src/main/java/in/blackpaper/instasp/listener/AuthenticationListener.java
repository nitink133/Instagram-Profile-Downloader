package in.blackpaper.instasp.listener;

public interface AuthenticationListener {
    void onCodeReceived(String auth_token);
}
