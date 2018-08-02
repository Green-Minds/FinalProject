package green_minds.com.finalproject.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.adapters.LeaderboardAdapter;

public class LeaderboardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String ARG_PARAM1 = "users";
    private OnFragmentInteractionListener mListener;

    private LeaderboardAdapter leaderboardAdapter;
    private ArrayList<ParseUser> users;
    private Context mContext;
    @BindView(R.id.swipe_container_fragment) public SwipeRefreshLayout swipeContainer;
    @BindView(R.id.rvUsersFragment) public RecyclerView rvUsers;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    public static LeaderboardFragment newInstance(ArrayList<ParseUser> users) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, users);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener.showProgressBar();
        users = getArguments().getParcelableArrayList(ARG_PARAM1);
        leaderboardAdapter = new LeaderboardAdapter(users, mListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        rvUsers.setLayoutManager(new LinearLayoutManager(mContext));
        rvUsers.setAdapter(leaderboardAdapter);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimaryDark));
        mListener.hideProgressBar();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        leaderboardAdapter.clear();
    }

    @Override
    public void onRefresh() {
        if (!mListener.isOnline()) return;
        new refreshTask().execute();
    }

    public interface OnFragmentInteractionListener {
        void showProgressBar();
        void hideProgressBar();
        boolean isOnline();
    }

    private class refreshTask extends AsyncTask<Void, Void, Void> {

        ArrayList<ParseUser> temp_users = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... params) {
            ParseQuery query = ParseUser.getQuery();
            query.orderByAscending("points").addAscendingOrder("pincount")
                    .whereEqualTo("connection", ParseUser.getCurrentUser()
                            .getString("connection")).setLimit(20).findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    ParseUser user = null;
                    if(e == null){
                        for (int i = objects.size() - 1; i >= 0; i--) {
                            user = objects.get(i);
                            temp_users.add(user);
                        }
                        leaderboardAdapter.clear();
                        users.addAll(temp_users);
                        leaderboardAdapter.notifyDataSetChanged();
                        swipeContainer.setRefreshing(false);
                    } else {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
