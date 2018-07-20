package green_minds.com.finalproject.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.model.GlideApp;
import green_minds.com.finalproject.model.Pin;
import green_minds.com.finalproject.model.PinCategoryHelper;
import green_minds.com.finalproject.model.RelativePositionPin;
import green_minds.com.finalproject.R;

public class PinAdapter extends RecyclerView.Adapter<PinAdapter.ViewHolder>{
    ArrayList<RelativePositionPin> mPins;
    // context
    Context context;
    ParseUser user;

    // initialize with list
    public PinAdapter(ArrayList<RelativePositionPin> pins) {

        this.mPins = pins;
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
        holder.tv_comment.setText(pin.getComment());

        DecimalFormat df = new DecimalFormat("0.00");
        String formatted = df.format(rp_pin.getDistanceAwayinMiles()).replaceAll("\\.00$", "");;
        holder.tv_miles_away.setText(formatted + " miles away");
        holder.tv_type.setText(PinCategoryHelper.getPinIdentifier(pin.getCategory()));
        holder.tv_checkin_count.setText("Visited " + pin.getCheckincount() + " times.");
        final TextView checkin = holder.tv_checkin_count;

        ParseFile photo = pin.getPhoto();
        if(photo != null){
            String imageUrl = photo.getUrl();
            GlideApp.with(context).load(imageUrl).centerCrop().into(holder.iv_preview);
        }
        holder.btn_checkin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pin.put("checkincount", pin.getCheckincount() + 1);
                pin.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(context, "Checked in!", Toast.LENGTH_LONG).show();
                        int numtimes  = pin.getCheckincount();
                        checkin.setText("Visited " + numtimes + " times.");

                        user.put("points", user.getInt("points") + 1);

                        String cat_key = PinCategoryHelper.getTypeKey(pin.getCategory());
                        user.put(cat_key, user.getInt(cat_key) + 1);

                        user.saveInBackground();
                    }
                });
            }
        });
    }

    // returns the total number of items in the list
    @Override
    public int getItemCount() {
        return mPins.size();
    }

    // create the viewholder as a static inner class
    public class ViewHolder extends RecyclerView.ViewHolder {

        // track view objects
        @BindView(R.id.tv_comment)
        TextView tv_comment;

        @BindView(R.id.tv_type)
        TextView tv_type;

        @BindView(R.id.tv_miles_away)
        TextView tv_miles_away;

        @BindView(R.id.iv_preview)
        ImageView iv_preview;

        @BindView(R.id.btn_checkin)
        Button btn_checkin;

        @BindView(R.id.tv_checkin_count)
        TextView tv_checkin_count;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
