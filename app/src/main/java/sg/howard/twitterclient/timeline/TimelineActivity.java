package sg.howard.twitterclient.timeline;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.steelkiwi.library.SlidingSquareLoaderView;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import jp.wasabeef.blurry.Blurry;
import sg.howard.twitterclient.R;
import sg.howard.twitterclient.compose.ComposeTweetActivity;
import sg.howard.twitterclient.status.StatusAdapter;

import static com.bumptech.glide.request.RequestOptions.fitCenterTransform;

public class TimelineActivity extends AppCompatActivity implements TimelineContract.View {
    private static String TAG = TimelineActivity.class.getSimpleName();
    RecyclerView rvTimeline;
    ProgressBar loader;
    FloatingActionButton fab;
    TimelineContract.Presenter presenter;
    StatusAdapter statusAdapter;
    private SwipeRefreshLayout swipeContainer;
    SlidingSquareLoaderView boxLoader ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        rvTimeline = findViewById(R.id.rvTimeline);
        //loader = findViewById(R.id.loader);
        boxLoader = findViewById(R.id.boxloader);

        fab = findViewById(R.id.fab);
        swipeContainer = findViewById(R.id.swipeContainer);

        presenter = new TimelinePresenter(this, TwitterCore.getInstance().getSessionManager().getActiveSession());
        //Fab
        fun_listenerFab(rvTimeline);

        //Refesh
        swipeContainer.setOnRefreshListener(() -> refeshTimeline() );
    }


    @Override
    protected void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void setPresenter(TimelineContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showLoading(boolean isShow) {
        //loader.setVisibility(isShow ? View.VISIBLE : View.GONE);
        if(isShow) {
            new Handler().postDelayed(() -> boxLoader.show(),1000);
        } else{
            new Handler().postDelayed(() -> boxLoader.hide(),4000);
        }
    }
    //.......................................................................... RecycleView start
    @Override
    public void onGetStatusesSuccess(List<Tweet> data) {

        //list data -> adapter
        statusAdapter = new StatusAdapter(getApplicationContext(),this);
        statusAdapter.setData(data);
        rvTimeline.setAdapter(statusAdapter);
        //recyclerView
        rvTimeline.setHasFixedSize(true);// the nay toi uu hoa du lieu
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvTimeline.setLayoutManager(layoutManager);
        //................Chèn một kẻ ngang giữa các phần tử
        DividerItemDecoration dividerHorizontal =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerHorizontal.setDrawable(getApplicationContext().getDrawable(R.drawable.line_rv_timeline));
        rvTimeline.addItemDecoration(dividerHorizontal);


        Log.d(TAG, "Loaded " + data.size());
        //Log.d(TAG, "Loaded " + data.get(0).createdAt);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //............................................................................ Show Dialog
    @Override
    public void showDialogTweet(){
        final CharSequence[] items = {"Tweet lại", "Tweet bằng bình luận"};

        AlertDialog.Builder builder = new AlertDialog.Builder(TimelineActivity.this);
//        builder.setTitle("Dialog on Android");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Blurry.delete(findViewById(R.id.ctl_timeline));
            }
        });
        //Blur
        builder.setOnDismissListener(dialogInterface -> Blurry.delete(findViewById(R.id.ctl_timeline)));
//        builder.setOnCancelListener(dialogInterface -> Blurry.delete(findViewById(R.id.ctl_timeline)));
        Blurry.with(TimelineActivity.this)
                .radius(25)
                .sampling(2)
                .onto(findViewById(R.id.ctl_timeline));
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void showDialogShare(){
        final CharSequence[] items = {"Gửi qua tin nhắn trực tiếp", "Thêm Tweet vào Dấu trang", "Chia sẻ Tweet qua..."};

        AlertDialog.Builder builder = new AlertDialog.Builder(TimelineActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Blurry.delete(findViewById(R.id.ctl_timeline));
            }
        });
        //Blur
        builder.setOnCancelListener(dialogInterface -> Blurry.delete((ConstraintLayout)findViewById(R.id.ctl_timeline)));
        Blurry.with(TimelineActivity.this)
                .radius(25)
                .sampling(2)
                .onto((ConstraintLayout) findViewById(R.id.ctl_timeline));

        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void showDialogHinhClient(String url_hinh){
        AlertDialog.Builder builder = new AlertDialog.Builder(TimelineActivity.this);
        LayoutInflater factory = LayoutInflater.from(TimelineActivity.this);
        final View view1 = factory.inflate(R.layout.show_image_full_screen, null);
        final ImageView hinhclient = view1.findViewById(R.id.dialog_imageview);

        Glide
                .with(this).load(url_hinh)
                .apply(fitCenterTransform())
                .into(hinhclient);
        builder.setView(view1);
        //Blur
        builder.setOnCancelListener(dialogInterface -> Blurry.delete(findViewById(R.id.ctl_timeline)));
        Blurry.with(TimelineActivity.this)
                .radius(25)
                .sampling(2)
                .onto(findViewById(R.id.ctl_timeline));

        final AlertDialog alert = builder.create();
        alert.show();
    }
    //............................................................................. Refesh Timeline
    public void refeshTimeline(){
        presenter = new TimelinePresenter(this, TwitterCore.getInstance().getSessionManager().getActiveSession());
        presenter.start();
        //loader.setVisibility(View.GONE);
        swipeContainer.setRefreshing(false);
    }
    //............................................................................. Fab
    public void fun_listenerFab(RecyclerView rvTimeline){
        fab.setOnClickListener(view -> {
            //send to ComposeTweet imageUser,nameUser,idUser
            Intent intent = new Intent(this, ComposeTweetActivity.class);
            startActivity(intent);
        });

        rvTimeline.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy<0 && !fab.isShown())
                    fab.show();
                else if(dy>0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }
}
