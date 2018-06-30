package sg.howard.twitterclient.compose;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import sg.howard.twitterclient.R;
import sg.howard.twitterclient.TwitterClientApplication;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ComposeTweetActivity extends AppCompatActivity implements ComposeContract.View{
    Button btnSend,btnBack;
    EditText edtCompose;
    ProgressBar loader;
    ComposeContract.Presenter presenter;
    String userName,userScreenName,userUrlAvatar,userUrlBanner;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        edtCompose = findViewById(R.id.edtCompose);
        loader = findViewById(R.id.loader);
        presenter = new ComposeTweetPresenter(this, TwitterCore.getInstance().getSessionManager().getActiveSession());

        btnSend.setOnClickListener( view -> presenter.sendTweet(edtCompose.getText().toString()));
        btnBack.setOnClickListener(view -> this.finish());
    }
    @Override
    protected void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void setPresenter(ComposeContract.Presenter presenter) { this.presenter = presenter; }

    @Override
    public void showLoading(boolean isShow) {
        loader.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void sendTweetSuccess(Result<Tweet> result) {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void saveUserName(Result<List<Tweet>> result) {
        userName = result.data.get(0).user.name;
        userScreenName = "@"+result.data.get(0).user.screenName;
        userUrlAvatar = result.data.get(0).user.profileImageUrl;
        userUrlBanner = result.data.get(0).user.profileBannerUrl;
        String url_image = userUrlAvatar.split("_normal")[0] + userUrlAvatar.split("_normal")[1];
        fun_showProfile(url_image);
    }

    public void fun_showProfile(String url_image){
        TextView txtName = findViewById(R.id.txtName);
        TextView txtScreenName = findViewById(R.id.txtScreenNameUser);
        ImageView imgAvatar = findViewById(R.id.imgAavatar);
        ImageView imgBanner = findViewById(R.id.imgBanner);

        txtName.setText(userName);
        txtScreenName.setText(userScreenName);
        Glide.with(ComposeTweetActivity.this)
                .load(url_image)
                .apply(bitmapTransform(new RoundedCornersTransformation(90, 0, RoundedCornersTransformation.CornerType.ALL)))
                .into(imgAvatar);
        Glide.with(ComposeTweetActivity.this)
                .load(userUrlBanner)
                .apply(bitmapTransform(new RoundedCornersTransformation(10, 0, RoundedCornersTransformation.CornerType.ALL)))
                .into(imgBanner);
    }
}
