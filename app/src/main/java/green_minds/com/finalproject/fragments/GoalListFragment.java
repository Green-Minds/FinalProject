package green_minds.com.finalproject.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.adapters.GoalAdapter;
import green_minds.com.finalproject.model.Goal;

public class GoalListFragment extends Fragment {

    public interface OnGoalListListener {
        public void openEditFragment(Goal goal, ArrayList<Goal> goals);
        public void showProgressBar();
        public void hideProgressBar();
    }

    private OnGoalListListener mListener;
    private ArrayList<Goal> mGoals;
    private ParseUser user;
    private Context mContext;
    private GoalAdapter mAdapter;

    @BindView(R.id.rv_goals)
    RecyclerView rvGoals;

    public GoalListFragment() {
    }

    public static GoalListFragment newInstance() {
        GoalListFragment fragment = new GoalListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        mListener.showProgressBar();

        user = ParseUser.getCurrentUser();
        //need "include goals" query because otherwise before I get anything in a goal obj I need to call "fetchifneeded"
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.include("goals");
        userQuery.whereEqualTo("objectId", user.getObjectId()).findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e != null){
                    Toast.makeText(mContext, "Error. Please try again later.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    return;
                }
                if(objects.size() < 1){
                    Toast.makeText(mContext, "Error. Please try again later.", Toast.LENGTH_LONG).show();
                    return;
                }
                user = objects.get(0);
                mGoals = (ArrayList<Goal>)user.get("goals");
                if( mGoals == null){
                    mGoals = new ArrayList<>();
                }
                mAdapter = new GoalAdapter(mGoals, mListener);
                rvGoals.setLayoutManager(new LinearLayoutManager(mContext));
                rvGoals.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mListener.hideProgressBar();
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
        if (context instanceof OnGoalListListener) {
            mListener = (OnGoalListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
