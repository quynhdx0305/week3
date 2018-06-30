package sg.howard.twitterclient.compose;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import androidx.annotation.NonNull;
import sg.howard.twitterclient.base.BaseView;


public class ComposeTweetPresenter implements ComposeContract.Presenter {

    private final ComposeContract.View mView;
    private TwitterSession mSession = null;
    TwitterApiClient user = null;

    public ComposeTweetPresenter(@NonNull ComposeContract.View view, TwitterSession session) {
        this.mView = view;
        mSession = session;
        mView.setPresenter(this);
        user = new TwitterApiClient(session);
    }

    @Override
    public void sendTweet(String text) {
        mView.showLoading(true);
        TwitterApiClient client = new TwitterApiClient(mSession);
        client.getStatusesService().update(text, null, null, null, null, null, null, null, null)
                .enqueue(new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        mView.showLoading(false);
                        mView.sendTweetSuccess(result);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        mView.showLoading(false);
                        mView.showError(exception.getMessage());
                    }
                });
    }

    //get User Timeline
    @Override
    public void start() {
        user.getStatusesService().userTimeline(null,null,null,null,null,null,null,null,null)
                .enqueue(new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        mView.saveUserName(result);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.d("User name","khong lay dc user name");
                    }
                });
    }
}
