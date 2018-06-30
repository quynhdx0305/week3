package sg.howard.twitterclient.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import com.twitter.sdk.android.core.models.User;

/** Singletone class helper to handle shared preference, based on this tutorial below
 * https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples
 */
public class SharedPreferenceHelper {
    private SharedPreferences mSharedPreference = null;
    private Context mContext = null;
    @SuppressLint("StaticFieldLeak")
    private static SharedPreferenceHelper instance = null;

    private SharedPreferenceHelper() {
    }

    public static SharedPreferenceHelper getInstance(){
        if(instance == null){
            synchronized(SharedPreferenceHelper.class){
                if(instance == null)
                instance = new SharedPreferenceHelper();
            }
        }
        return instance;
    }

     public SharedPreferenceHelper setContext(Context context)  {
        mContext = context.getApplicationContext();
        return this;
    }

    public SharedPreferences getSharedPreferences() {
        mSharedPreference = mContext.getSharedPreferences("TwitterClient", Context.MODE_PRIVATE);
        return mSharedPreference;
    }
    public SharedPreferences.Editor editor(){
        return mSharedPreference.edit();
    }

    public void saveUserName(String userName){
        getSharedPreferences().edit().putString("userName", userName!= null ?userName : "").commit();
    }
}
