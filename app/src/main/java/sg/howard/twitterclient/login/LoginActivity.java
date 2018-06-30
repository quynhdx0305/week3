package sg.howard.twitterclient.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import jp.wasabeef.blurry.Blurry;
import sg.howard.twitterclient.R;
import sg.howard.twitterclient.timeline.TimelineActivity;


public class LoginActivity extends AppCompatActivity implements LoginContract.View {
    TwitterLoginButton loginButton;
    LoginContract.Presenter presenter;
    protected ProgressBar loader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        presenter = new LoginPresenter(this);
        setUpView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TwitterCore.getInstance().getSessionManager().getActiveSession() != null){
            saveUserSuccess();
            }
    }

    private void setUpView() {
        loader = findViewById(R.id.loader);
        loginButton = findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                presenter.saveResult(result);
                Toast.makeText(getApplicationContext(), "Welcome " + result.data.getUserName(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(),"Welcome " + presenter.getClass().getName(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("TwitterClient", "Login failed");
                clearTwitter();
                endAuthorizeInProgress();
            }
        });
    }

    private void clearTwitter() {
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
    }

    @Override
    public void saveUserSuccess() {
        startActivity(new Intent(LoginActivity.this, TimelineActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            final TwitterAuthClient twitterAuthClient = new TwitterAuthClient();
            if(twitterAuthClient.getRequestCode()==requestCode) {
                Boolean twitterLoginWasCanceled = (resultCode == RESULT_CANCELED);
                if(!twitterLoginWasCanceled)
                    loginButton.onActivityResult(requestCode, resultCode, data);
                else
                    endAuthorizeInProgress();
            }
        } catch (TwitterAuthException exception) {
            clearTwitter();
        }
    }
    private void endAuthorizeInProgress() {
        try {
            final TwitterAuthClient twitterAuthClient = new TwitterAuthClient();
            Field authStateField = twitterAuthClient.getClass().getDeclaredField("authState");
            authStateField.setAccessible(true);
            Object authState = authStateField.get(twitterAuthClient);
            Method endAuthorize = authState.getClass().getDeclaredMethod("endAuthorize");
            endAuthorize.invoke(authState);
        } catch (NoSuchFieldException | SecurityException | InvocationTargetException |
                NoSuchMethodException | IllegalAccessException e) {
            Log.e("TwitterClient","Couldn't end authorize in progress.", e);
        }
    }
    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showLoading(boolean isShow) {
        loader.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
