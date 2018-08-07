package green_minds.com.finalproject.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.utils.Utils;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.activities.CheckInActivity;
import green_minds.com.finalproject.model.CategoryHelper;
import green_minds.com.finalproject.model.GlideApp;
import green_minds.com.finalproject.model.Pin;
import green_minds.com.finalproject.model.RelativePositionPin;

public class PinAdapter extends RecyclerView.Adapter<PinAdapter.ViewHolder>{
    ArrayList<RelativePositionPin> mPins;
    Context context;
    public int mSelectedPos;
    ParseUser user;

    // initialize with list
    public PinAdapter(ArrayList<RelativePositionPin> pins, Context c) {
        this.context = c;
        Utils.init(context);
        this.mPins = pins;
        this.mSelectedPos = RecyclerView.NO_POSITION;
        this.user = ParseUser.getCurrentUser();
    }

    // creates and inflates a new view
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.item_pin, viewGroup, false);
        return new ViewHolder(postView);
    }

    // binds an inflated view to a new item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        //relative position pin holds the relative position
        RelativePositionPin rp_pin = mPins.get(i);
        final Pin pin = rp_pin.getPin();
        boolean selected = mSelectedPos == i;
        holder.itemView.setSelected(selected);
        if(selected){
            int white = context.getResources().getColor(R.color.white);
            holder.tv_miles_away.setTextColor(white);
            holder.tv_type.setTextColor(white);
            holder.tv_checkin_count.setTextColor(white);
            holder.tv_comment.setTextColor(white);
//            int dp = (int)Utils.convertPixelsToDp(80);
//            holder.itemView.setPadding( dp, 0, dp, 0);
        } else{
            int black = context.getResources().getColor(R.color.black);
            holder.tv_miles_away.setTextColor(black);
            holder.tv_type.setTextColor(black);
            holder.tv_checkin_count.setTextColor(black);
//            holder.tv_comment.setTextColor(black);
//            holder.itemView.setPadding( 0, 0, 0, 0);
        }
        holder.tv_comment.setText(pin.getComment());

        DecimalFormat df = new DecimalFormat("0.00");
        String formatted = df.format(rp_pin.getDistanceAwayinMiles()).replaceAll("\\.00$", "");;
        holder.tv_miles_away.setText(formatted + " miles away");
        holder.tv_type.setText(CategoryHelper.getPinIdentifier(pin.getCategory()));
        holder.tv_checkin_count.setText("Visited " + pin.getCheckincount() + " times.");
        final TextView checkin = holder.tv_checkin_count;

        ParseFile photo = pin.getPhoto();
        if(photo != null){
            String imageUrl = photo.getUrl();
            GlideApp.with(context).load(imageUrl).apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(8))).into(holder.iv_preview);
        }
    }

    // returns the total number of items in the list
    @Override
    public int getItemCount() {
        return mPins.size();
    }

    // create the viewholder as a static inner class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // track view objects
        @BindView(R.id.tv_comment)
        TextView tv_comment;

        @BindView(R.id.tv_type)
        TextView tv_type;

        @BindView(R.id.tv_miles_away)
        TextView tv_miles_away;

        @BindView(R.id.iv_preview)
        ImageView iv_preview;

        @BindView(R.id.tv_checkin_count)
        TextView tv_checkin_count;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mSelectedPos == RecyclerView.NO_POSITION){
                if(context instanceof CheckInActivity){
                    ((CheckInActivity)context).activateButton();
                }
            }
            notifyItemChanged(mSelectedPos);
            mSelectedPos = getLayoutPosition();
            notifyItemChanged(mSelectedPos);
        }

    }
}
