package sg.howard.twitterclient.login;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterSession;

import sg.howard.twitterclient.base.BasePresenter;
import sg.howard.twitterclient.base.BaseView;

public interface LoginContract {
    interface View extends BaseView<Presenter>{
        void saveUserSuccess();
    }

    interface Presenter extends BasePresenter{
        void saveResult(Result<TwitterSession> result);
    }
}
