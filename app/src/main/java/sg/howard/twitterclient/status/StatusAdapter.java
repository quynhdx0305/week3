package sg.howard.twitterclient.status;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.twitter.sdk.android.core.models.Tweet;
import com.varunest.sparkbutton.SparkButton;

import org.joda.time.DateTime;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import sg.howard.twitterclient.R;
import sg.howard.twitterclient.timeline.TimelineContract;
import sg.howard.twitterclient.util.TimelineConverter;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.centerCropTransform;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder> {
    List<Tweet> data;
    Context context;
    //animation
    Animation anim_fadein,anim_fad,anim_blik,anim_bound,anim_move,anim_moveup,anim_zoom_out;
    TimelineContract.View mView;

    public StatusAdapter( Context context,TimelineContract.View view) {
        this.context = context;
        //data=new LinkedList<>();
        this.mView = view;
    }
    public void setData(List<Tweet> list){
        data = list;
        notifyDataSetChanged();
    }
    public void clearData(){
        data.clear();
        notifyDataSetChanged();
    }
    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.status,parent,false);
        //Tweet animation
        Load_Animation();
        return new ViewHolder(itemView);// goi class ViewHolder ben duoi
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet dataX = data.get(position);
        // hien thi ra man hinh
        holder.txtTimeAgo.setText("."+TimelineConverter.dateToAge(dataX.createdAt, DateTime.now()));
        holder.txtNameClient.setText(dataX.user.name);
        holder.txtIDClient.setText("@" + dataX.user.screenName);
        holder.txtStatusClient.setText(dataX.text);
        //.......................................................................Show Avartar
        Glide.with(context).load(dataX.user.profileImageUrl)
                .apply(bitmapTransform(new RoundedCornersTransformation(90, 0, RoundedCornersTransformation.CornerType.ALL)))
                .into(holder.imgAvatarClient);
        //.......................................................................Show Imgage
        if(dataX.entities.media.size() != 0){
            Glide.with(context).load(dataX.entities.media.get(0).mediaUrl)
                    .apply(centerCropTransform()) //full kich thuoc khung hinh
                    .into(holder.imgHinhClient);
        }else { holder.imgHinhClient.setVisibility(View.GONE); }
        //.......................................................................Show Count Like,share..................comment
        holder.txtCountTweet.setText(dataX.retweetCount + "");
        holder.txtCountLike.setText(dataX.favoriteCount + "");
        //........................................................................send reques + like and animation
        holder.imgTweet.setOnClickListener(view -> {
            holder.imgTweet.startAnimation(anim_zoom_out);
            mView.showDialogTweet();
        });
        holder.imgShare.setOnClickListener(view -> mView.showDialogShare());
        //Show image hinh Client full screen...............................
        holder.imgHinhClient.setOnClickListener(view -> mView.showDialogHinhClient(dataX.entities.media.get(0).mediaUrl) );
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    //.....................Animation
    private void Load_Animation(){
        anim_fadein = AnimationUtils.loadAnimation(context,R.anim.fade_in);
        anim_fad = AnimationUtils.loadAnimation(context,R.anim.anim_fad);
        anim_blik = AnimationUtils.loadAnimation(context,R.anim.blink);
        anim_bound = AnimationUtils.loadAnimation(context,R.anim.bound);
        anim_move = AnimationUtils.loadAnimation(context,R.anim.move);
        anim_moveup = AnimationUtils.loadAnimation(context,R.anim.move_up);
        anim_zoom_out = AnimationUtils.loadAnimation(context,R.anim.zoom_out);
    }

    //tao clas ViewHolderX anh xa tu ben status.xml
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNameClient,txtStatusClient,txtIDClient,txtTimeAgo;
        ImageView imgAvatarClient,imgHinhClient,imgComment,imgTweet,imgShare;
        //count
        TextView txtCountComment,txtCountTweet,txtCountLike;
        //click buttom
        SparkButton spark_button_like;
        //ViewGroup transitionsContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //transitionsContainer = (ViewGroup) itemView.findViewById(R.id.ctl_Group);
            txtTimeAgo = itemView.findViewById(R.id.txtTimeAgo);
            txtNameClient = itemView.findViewById(R.id.txtNameClient);
            txtIDClient = itemView.findViewById(R.id.txtIDClient);
            imgAvatarClient = itemView.findViewById(R.id.imgAvatarClient);
            txtStatusClient = itemView.findViewById(R.id.txtStatusClien);
            imgHinhClient = itemView.findViewById(R.id.imgHinhClient);
            txtCountComment = itemView.findViewById(R.id.txtCountComment);
            txtCountTweet = itemView.findViewById(R.id.txtCountTweet);
            txtCountLike = itemView.findViewById(R.id.txtCountLike);
            //like comment share tweet
            spark_button_like = (SparkButton)itemView.findViewById(R.id.spark_button_like);
            imgComment = itemView.findViewById(R.id.imgComment);
            imgTweet = itemView.findViewById(R.id.imgTweet);
            imgShare = itemView.findViewById(R.id.imgShare);
        }
    }
}
