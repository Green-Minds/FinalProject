package green_minds.com.finalproject.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.fragments.GoalListFragment;
import green_minds.com.finalproject.model.Goal;
import green_minds.com.finalproject.model.PinCategoryHelper;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder>{
    private ArrayList<Goal> mGoals;
    private Context mContext;
    private ParseUser mUser;
    private GoalListFragment.OnGoalListListener mListener;

    public GoalAdapter(ArrayList<Goal> goals, GoalListFragment.OnGoalListListener listener) {
        mGoals = goals;
        mListener = listener;
    }

    public void clear () {
        mGoals.clear();
    }

    public void addAll (ArrayList<Goal> goal) {
        mGoals.addAll(goal);
    }

    // creates and inflates a new view
    @Override
    @NonNull
    public GoalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        mUser = ParseUser.getCurrentUser();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View goalView = inflater.inflate(R.layout.item_goal, viewGroup, false);
        return new GoalAdapter.ViewHolder(goalView);
    }

    // binds an inflated view to a new item
    @Override
    public void onBindViewHolder(@NonNull GoalAdapter.ViewHolder holder, int i) {
        //relative position Goal holds the relative position
        final Goal goal = mGoals.get(i);
        int checkins = 0;
        try {
            holder.tvName.setText(PinCategoryHelper.listOfCategories[goal.getType()]);
            checkins = mUser.fetchIfNeeded().getInt(PinCategoryHelper.getTypeKey(goal.getType()));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        int goalNum = goal.getGoal();
        holder.progressBar.setMax(goalNum);
        holder.progressBar.setProgress(checkins);


        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 mGoals.remove(goal);
                 mUser.put("goals", mGoals);
                 mUser.saveInBackground(new SaveCallback() {
                     @Override
                     public void done(ParseException e) {
                         goal.deleteInBackground(new DeleteCallback() {
                             @Override
                             public void done(com.parse.ParseException e) {
                                 if(e != null) e.printStackTrace();
                                 else{
                                     Toast.makeText(mContext, "Removed.", Toast.LENGTH_SHORT).show();
                                     notifyDataSetChanged();
                                 }
                             }
                         });
                     }
                 });

             }
         });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.openEditFragment(goal, mGoals);
            }
        });
    }

    // returns the total number of items in the list
    @Override
    public int getItemCount() {
        return mGoals.size();
    }

    // create the viewholder as a static inner class
    public class ViewHolder extends RecyclerView.ViewHolder {

        // track view objects
        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.progress)
        ProgressBar progressBar;

        @BindView(R.id.btn_delete)
        Button btnDelete;

        @BindView(R.id.btn_edit)
        Button btnEdit;

        @BindView(R.id.btn_details)
        Button btnDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
