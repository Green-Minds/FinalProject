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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.adapters.GoalAdapter;
import green_minds.com.finalproject.model.Goal;

public class GoalListFragment extends Fragment {

    public interface OnGoalListListener {

        void openEditFragment(Goal goal);

        void showProgressBar();

        void hideProgressBar();
    }

    private OnGoalListListener mListener;
    private ArrayList<Goal> mGoals;
    private Context mContext;
    private GoalAdapter mAdapter;
    private static final String ARG_PARAM0 = "goals";

    @BindView(R.id.rv_goals)
    RecyclerView rvGoals;

    public GoalListFragment() {
    }

    public static GoalListFragment newInstance(ArrayList<Goal> mGoals) {
        GoalListFragment fragment = new GoalListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM0, mGoals);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener.showProgressBar();
        mGoals = getArguments().getParcelableArrayList(ARG_PARAM0);
        mAdapter = new GoalAdapter(mGoals, mListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        rvGoals.setLayoutManager(new LinearLayoutManager(mContext));
        rvGoals.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mListener.hideProgressBar();
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
        if (context instanceof OnGoalListListener) {
            mListener = (OnGoalListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGoalListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
