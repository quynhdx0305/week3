package sg.howard.twitterclient;

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import sg.howard.twitterclient.util.SharedPreferenceHelper;

public class TwitterClientApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initialize(Stetho.newInitializerBuilder(this).build());
        SharedPreferenceHelper.getInstance().setContext(this);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(BuildConfig.CONSUMER_KEY, BuildConfig.CONSUMER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }
}
