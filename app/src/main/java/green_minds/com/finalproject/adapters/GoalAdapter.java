package green_minds.com.finalproject.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import green_minds.com.finalproject.model.CustomProgressBar;
import green_minds.com.finalproject.model.Goal;
import green_minds.com.finalproject.model.CategoryHelper;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {
    private ArrayList<Goal> mGoals;
    private Context mContext;
    private ParseUser mUser;
    private GoalListFragment.OnGoalListListener mListener;

    public GoalAdapter(ArrayList<Goal> goals, GoalListFragment.OnGoalListListener listener) {
        mGoals = goals;
        mListener = listener;
    }

    @Override
    @NonNull
    public GoalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        mUser = ParseUser.getCurrentUser();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View goalView = inflater.inflate(R.layout.item_goal, viewGroup, false);
        return new GoalAdapter.ViewHolder(goalView);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalAdapter.ViewHolder holder, int i) {
        final Goal goal = mGoals.get(i);

        holder.tvName.setText(CategoryHelper.listOfCategories[goal.getType()]);
        holder.progressBar.setGoal(goal, mUser);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeGoal(goal);
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.openEditFragment(goal);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGoals.size();
    }

    private void removeGoal(final Goal goal) {
        final ArrayList<Goal> tempGoalArray = mGoals; //keep copy to revert back to in case of error
        mGoals.remove(goal);
        mUser.put("goals", mGoals);
        mUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                goal.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        if (e != null) {
                            mGoals = tempGoalArray;
                            e.printStackTrace();
                            if (e.getCode() == ParseException.CONNECTION_FAILED) {
                                Toast.makeText(mContext, "Network Error. Please try again later!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, "Error. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, "Removed.", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.progress)
        CustomProgressBar progressBar;

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
