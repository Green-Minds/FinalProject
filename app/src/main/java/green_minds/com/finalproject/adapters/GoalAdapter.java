package green_minds.com.finalproject.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;

import green_minds.com.finalproject.R;
import green_minds.com.finalproject.model.CategoryHelper;
import green_minds.com.finalproject.model.CustomProgressBar;
import green_minds.com.finalproject.model.Goal;

public class GoalAdapter extends ArrayAdapter<Goal> {

    private Context mContext;
    private ParseUser mUser;
    private ArrayList<Goal> mGoals;

    public GoalAdapter(Context context, ArrayList<Goal> items) {
        super(context, R.layout.item_goal, items);
        this.mContext = context;
        this.mUser = ParseUser.getCurrentUser();
        this.mGoals = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=vi.inflate(R.layout.item_goal, null);
        }

        Goal goal = mGoals.get(position);

        int type = goal.getType();
        TextView tvIdentifier = convertView.findViewById(R.id.tv_description);
        tvIdentifier.setText(CategoryHelper.getPinIdentifier(type));

        final int checkins = mUser.getInt(CategoryHelper.getTypeKey(type));

        CustomProgressBar progressBar = convertView.findViewById(R.id.progress);
        if(goal == null){
            progressBar.setVisibility(View.GONE);
            //convertView.findViewById(R.id.tv_details).setVisibility(View.GONE);
        } else{
            progressBar.setGoal(goal, mUser);
            progressBar.setVisibility(View.VISIBLE);
            //convertView.findViewById(R.id.tv_details).setVisibility(View.VISIBLE);
            final Goal finalGoal = goal;
//            convertView.findViewById(R.id.tv_details).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(mContext, GoalDetailActivity.class);
//                    i.putExtra("GOAL", finalGoal);
//                    i.putExtra("CHECKINS", checkins);
//                    mContext.startActivity(i);
//                }
//            });

        }

        return convertView;
    }
}
