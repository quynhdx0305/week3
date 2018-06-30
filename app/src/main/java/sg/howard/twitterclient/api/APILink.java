package sg.howard.twitterclient.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import sg.howard.twitterclient.model.User;

public interface APILink {
    String BASE_URL = "http://api.twitter.com/";

    @GET("/1.1/users/show.json")
    Call<User> show(@Query("user_id") long id);
}
