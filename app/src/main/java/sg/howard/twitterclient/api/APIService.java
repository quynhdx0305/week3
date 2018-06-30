package sg.howard.twitterclient.api;

import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIService {
    private static APILink instance = null;

    public static APILink getInstance() {
        if (instance == null) {
            synchronized (APILink.class) {
                if (instance == null) {
                    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                    httpClient.writeTimeout(15 * 60 * 1000, TimeUnit.MILLISECONDS);
                    httpClient.readTimeout(60 * 1000, TimeUnit.MILLISECONDS);
                    httpClient.connectTimeout(20 * 1000, TimeUnit.MILLISECONDS);
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
                            new HttpLoggingInterceptor.Logger() {
                                @Override
                                public void log(String message) {
                                    Log.d("API", message);
                                }
                            }
                    );
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                    httpClient.addNetworkInterceptor(logging);
                    httpClient.addNetworkInterceptor(new StethoInterceptor());

                    OkHttpClient client = httpClient.build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(APILink.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(client)
                            .build();
                    instance = retrofit.create(APILink.class);
                }
            }
        }

        return instance;
    }
}
