package green_minds.com.finalproject.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import green_minds.com.finalproject.model.EndlessRecyclerOnScrollListener;

public class LeaderboardFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String ARG_PARAM1 = "users";
    private static final String ARG_PARAM2 = "skip";
    private OnFragmentInteractionListener mListener;
    private EndlessRecyclerOnScrollListener scrollListener;
    private LeaderboardAdapter leaderboardAdapter;
    private ArrayList<ParseUser> users;
    private Context mContext;
    private Integer skip;

    @BindView(R.id.swipe_container_fragment)
    public SwipeRefreshLayout swipeContainer;
    @BindView(R.id.progressBar)
    public ProgressBar progressBar;
    @BindView(R.id.rvUsersFragment)
    public RecyclerView rvUsers;
    @BindView(R.id.connectionTitle)
    public TextView connectionTitle;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    public static LeaderboardFragment newInstance(ArrayList<ParseUser> users, Integer skip) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, users);
        args.putInt(ARG_PARAM2, skip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        users = getArguments().getParcelableArrayList(ARG_PARAM1);
        skip = getArguments().getInt(ARG_PARAM2);
        leaderboardAdapter = new LeaderboardAdapter(users, mListener);
        super.onCreate(savedInstanceState);
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

        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar_colors));
        connectionTitle.setText(ParseUser.getCurrentUser().getString("connection") + " Leaderboard");
        rvUsers.setLayoutManager(new LinearLayoutManager(mContext));
        rvUsers.setAdapter(leaderboardAdapter);

        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent),
                getResources().getColor(R.color.colorPrimaryDark));

        scrollListener = new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                addDataToList();
            }
        };
        rvUsers.addOnScrollListener(scrollListener);

        if (leaderboardAdapter.mPosition != null) rvUsers.scrollToPosition(leaderboardAdapter.mPosition);
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
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
    }

    @Override
    public void onRefresh() {
        if (!mListener.isOnline()) return;
        new refreshTask().execute();
    }

    public interface OnFragmentInteractionListener {
        boolean isOnline();
    }

    public void showProgressBar() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }


    private void addDataToList() {
        showProgressBar();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ParseQuery query = ParseUser.getQuery();
                query.orderByDescending("points").addDescendingOrder("pincount")
                        .whereEqualTo("connection", ParseUser.getCurrentUser().getString("connection"))
                        .setSkip(skip).setLimit(10).findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if(e == null){
                            users.addAll(objects);
                            leaderboardAdapter.notifyDataSetChanged();
                            if (leaderboardAdapter.mPosition != null && leaderboardAdapter.mPosition >= skip)
                                rvUsers.scrollToPosition(leaderboardAdapter.mPosition);
                            hideProgressBar();
                            skip += objects.size();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 1500);
    }

    private class refreshTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            ParseQuery query = ParseUser.getQuery();
            query.orderByDescending("points").addDescendingOrder("pincount")
                    .whereEqualTo("connection", ParseUser.getCurrentUser().getString("connection"))
                    .setLimit(10).findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if(e == null){
                        leaderboardAdapter.clear();
                        users.addAll(objects);
                        leaderboardAdapter.notifyDataSetChanged();
                        skip = objects.size();
                        swipeContainer.setRefreshing(false);
                        scrollListener.reset(0, true);
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
