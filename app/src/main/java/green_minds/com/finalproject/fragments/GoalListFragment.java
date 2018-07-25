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

import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.adapters.GoalAdapter;
import green_minds.com.finalproject.model.Goal;

public class GoalListFragment extends Fragment {

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
        user = ParseUser.getCurrentUser();
        mGoals = (ArrayList<Goal>)user.get("goals");
        if( mGoals == null){
            mGoals = new ArrayList<>();
        }
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
        mAdapter = new GoalAdapter(mGoals, mListener);
        rvGoals.setLayoutManager(new LinearLayoutManager(mContext));
        rvGoals.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
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

    public interface OnGoalListListener {
        public void openEditFragment(Goal goal, ArrayList<Goal> goals);
    }

}
