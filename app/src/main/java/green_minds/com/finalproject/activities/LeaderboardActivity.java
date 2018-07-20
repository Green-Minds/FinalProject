package green_minds.com.finalproject.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.adapters.LeaderboardAdapter;
import green_minds.com.finalproject.R;

public class LeaderboardActivity extends AppCompatActivity {
    @BindView(R.id.swipe_container) public SwipeRefreshLayout swipeContainer;
    @BindView(R.id.rvUsers) public RecyclerView rvUsers;
    private ParseUser newUser;
    protected LeaderboardAdapter leaderboardAdapter;
    protected ArrayList<ParseUser> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        ButterKnife.bind(this);

        users = new ArrayList<>();
        leaderboardAdapter = new LeaderboardAdapter(users);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(leaderboardAdapter);

        showLeaderboard();
    }

    private void showLeaderboard() {
        ParseQuery query = ParseUser.getQuery();
        query.setLimit(20).findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                ParseUser user = null;
                if (e == null) {
                    for (int i = objects.size() - 1; i >= 0; i--) {
                        user = objects.get(i);
                        users.add(user);
                        leaderboardAdapter.notifyItemChanged(users.size() - 1);
                    }
                }
                else {
                    e.printStackTrace();
                }
            }
        });
    }
}
