package green_minds.com.finalproject.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

import green_minds.com.finalproject.R;
import green_minds.com.finalproject.activities.UserInfoActivity;
import green_minds.com.finalproject.fragments.UserInfoFragment;
import green_minds.com.finalproject.model.CategoryHelper;
import green_minds.com.finalproject.model.CustomProgressBar;
import green_minds.com.finalproject.model.Goal;

public class GoalAdapter extends ArrayAdapter<Goal> {

    private Context mContext;
    private ParseUser mUser;
    private ArrayList<Goal> mGoals;
    private UserInfoFragment.OnUserInfoListener mListener;

    public GoalAdapter(Context context, ParseUser user, ArrayList<Goal> items, UserInfoFragment.OnUserInfoListener listener) {
        super(context, R.layout.item_goal, items);
        this.mContext = context;
        this.mUser = user;
        this.mGoals = items;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (mContext.getClass().equals(UserInfoActivity.class)) {
                convertView = vi.inflate(R.layout.item_profile_goal, null);
            } else {
                convertView = vi.inflate(R.layout.item_goal, null);
            }
        }

        Goal goal = mGoals.get(position);
        int type = goal.getType();

        TextView tvIdentifier = convertView.findViewById(R.id.tv_description);
        tvIdentifier.setText(CategoryHelper.getPinIdentifier(type));

        CustomProgressBar progressBar = convertView.findViewById(R.id.progress);
        progressBar.setGoal(goal, mUser);
        progressBar.setVisibility(View.VISIBLE);

        final int checkins = mUser.getInt(CategoryHelper.getTypeKey(type));

        final Goal finalGoal = goal;

        if (!mContext.getClass().equals(UserInfoActivity.class)) {
            final ImageButton btnPopup = convertView.findViewById(R.id.btn_popup);
            btnPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(getContext(), btnPopup);
                    popup.getMenuInflater()
                            .inflate(R.menu.goal_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            //TODO - add to strings.xml
                            String[] ids = mContext.getResources().getResourceName(item.getItemId()).split("\\/");
                            String id = ids[1];
                            if (id.equals("mi_edit")) {
                                mListener.openGoalEditPage(position, mGoals);
                            } else if (id.equals("mi_delete")) {
                                removeGoal(finalGoal);
                            } else {
                                mListener.goToDetail(finalGoal, checkins);
                            }
                            return true;
                        }
                    });
                    popup.show(); //showing popup menu
                }
            });
        }

        return convertView;
    }

    private void removeGoal(final Goal goal) {
        //mListener.setNetworkCallInProgress(true);
        final ArrayList<Goal> tempGoalArray = mGoals; //keep copy to revert back to in case of error
        mGoals.remove(goal);
        mUser.put("goals", mGoals);
        mUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                goal.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(com.parse.ParseException e) {
                        //mListener.setNetworkCallInProgress(false);
                        if (e != null) {
                            mGoals = tempGoalArray; //revert goal array to reflect true state
                            e.printStackTrace();
                            if (e.getCode() == ParseException.CONNECTION_FAILED) {
                                Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, mContext.getString(R.string.misc_error), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.removed), Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                            mListener.setListViewHeight();
                            if(mGoals.size() <= 0){
                                mListener.showNoDataMessage();
                            }
                        }
                    }
                });
            }
        });
    }
}
