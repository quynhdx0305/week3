package sg.howard.twitterclient.timeline;

import android.os.Handler;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import androidx.annotation.NonNull;

public class TimelinePresenter implements TimelineContract.Presenter {
    TwitterApiClient client = null;
    TimelineContract.View mView;

    public TimelinePresenter(@NonNull TimelineContract.View view, TwitterSession session){
        mView= view;
        mView.setPresenter(this);
        client = new TwitterApiClient(session);

    }


    @Override
    public void start() {
        mView.showLoading(true);
        client.getStatusesService()
                .homeTimeline(null, null, null, null, null, null, null)
                .enqueue(new Callback<List<Tweet>>() {
                    @Override
                    public void success(Result<List<Tweet>> result) {
                        mView.showLoading(false);
                        mView.onGetStatusesSuccess(result.data);
                        Log.d("log.d RESULT TIMELINE ",result.data.get(0).user.name);
                        //Log.d("log.d REQUEST TIMELINE",client.getStatusesService().homeTimeline(null, null, null, null, null, null, null).request().toString() );
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        mView.showLoading(false);
                        mView.showError(exception.getMessage());
                    }
                });
    }
}
