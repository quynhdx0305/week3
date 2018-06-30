package sg.howard.twitterclient.login;

import android.util.Log;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterSession;

import sg.howard.twitterclient.util.SharedPreferenceHelper;

public class LoginPresenter implements LoginContract.Presenter {
    LoginContract.View view;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void saveResult(Result<TwitterSession> result) {
        SharedPreferenceHelper.getInstance().saveUserName(result.data.getUserName());
        view.saveUserSuccess();
        //Log.d("Result xxx",result.data+"");
    }

    @Override
    public void start() {

    }
}
