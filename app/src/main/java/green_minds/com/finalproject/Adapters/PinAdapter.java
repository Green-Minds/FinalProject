package green_minds.com.finalproject.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.Model.PinCategoryHelper;
import green_minds.com.finalproject.Model.RelativePositionPin;
import green_minds.com.finalproject.R;

public class PinAdapter extends RecyclerView.Adapter<PinAdapter.ViewHolder>{
    ArrayList<RelativePositionPin> mPins;
    // context
    Context context;

    // initialize with list
    public PinAdapter(ArrayList<RelativePositionPin> pins) {
        this.mPins = pins;
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
        // get the Post data at the specified position
        RelativePositionPin pin = mPins.get(i);

        holder.tv_comment.setText(pin.getComment());
        holder.tv_miles_away.setText(String.format("{0:0.00} miles away", pin.getDistanceAway()));
        holder.tv_type.setText(PinCategoryHelper.getPinIdentifier(pin.getCategory()));
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


        public ViewHolder(View itemView) {
            super(itemView);
            // lookup view objects by id
            ButterKnife.bind(this, itemView);
        }
    }
}
