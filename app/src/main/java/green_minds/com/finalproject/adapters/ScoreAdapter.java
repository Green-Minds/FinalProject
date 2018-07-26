package green_minds.com.finalproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.activities.GoalDetailActivity;
import green_minds.com.finalproject.model.Category;
import green_minds.com.finalproject.model.CustomProgressBar;
import green_minds.com.finalproject.model.Goal;
import green_minds.com.finalproject.model.PinCategoryHelper;

public class ScoreAdapter extends ArrayAdapter<Category> {

    // creates and inflates a new view
    private Context mContext;
    private ParseUser mUser;
    private ArrayList<Goal> mGoals;
    private ArrayList<Category> mCategories;

    public ScoreAdapter(Context context, List<Category> items, ArrayList<Goal> goals) {
        super(context, R.layout.item_score, items);
        this.mContext = context;
        this.mUser = ParseUser.getCurrentUser();
        this.mCategories = (ArrayList<Category>) items;
        this.mGoals = goals;
    }

//    @Override
//    public int getCount() {
//        return super.getCount();
//    }
//
//    @Nullable
//    @Override
//    public ViewHolder getItem(int position) {
//        return super.getItem(position);
//    }
//
//    @Override
//    public int getPosition(@Nullable ViewHolder item) {
//        return super.getPosition(item);
//    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=vi.inflate(R.layout.item_score, null);
        }

        int type = mCategories.get(position).getTypeKey();

        TextView tvIdentifier = convertView.findViewById(R.id.tv_description);
        tvIdentifier.setText(PinCategoryHelper.getPinIdentifier(type));

        TextView numberOf = convertView.findViewById(R.id.numberOf);
        final int checkins = (int)mUser.get(PinCategoryHelper.getTypeKey(type));
        numberOf.setText(checkins + "");

        Goal goal = null;
        for(Goal g: mGoals){
            if(g.getType() == type) goal = g;
        }

        CustomProgressBar progressBar = convertView.findViewById(R.id.progress);
        if(goal == null){
            progressBar.setVisibility(View.GONE);
            convertView.findViewById(R.id.tv_details).setVisibility(View.GONE);
        } else{
            progressBar.setGoal(goal);
            final Goal finalGoal = goal;
            convertView.findViewById(R.id.tv_details).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, GoalDetailActivity.class);
                    i.putExtra("GOAL", finalGoal);
                    i.putExtra("CHECKINS", checkins);
                    mContext.startActivity(i);
                }
            });

        }

        return convertView;
    }

    // create the viewholder as a static inner class
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
