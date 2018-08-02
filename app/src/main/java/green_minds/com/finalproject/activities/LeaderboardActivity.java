package green_minds.com.finalproject.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import green_minds.com.finalproject.R;
import green_minds.com.finalproject.fragments.LeaderboardFragment;

public class LeaderboardActivity extends AppCompatActivity implements LeaderboardFragment.OnFragmentInteractionListener {

    private LeaderboardFragment leaderboardFragment;
    private ArrayList<ParseUser> users = new ArrayList<>();
    private MenuItem miActionProgressItem;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(ParseUser.getCurrentUser().getString("connection") + " Leaderboard");
        setContentView(R.layout.activity_leaderboard);

        if (isOnline()) {
            ParseQuery query = ParseUser.getQuery();
            query.orderByAscending("points").addAscendingOrder("pincount")
                    .whereEqualTo("connection", ParseUser.getCurrentUser().getString("connection"))
                    .setLimit(20).findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    ParseUser user = null;
                    if (e == null) {
                        for (int i = objects.size() - 1; i >= 0; i--) {
                            user = objects.get(i);
                            users.add(user);
                        }

                        if (savedInstanceState == null) {
                            leaderboardFragment = leaderboardFragment.newInstance(users);
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.fragment_container, leaderboardFragment);
                            ft.commit();
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        return true;
    }

        @Override
    public void showProgressBar() {
        if (miActionProgressItem != null) miActionProgressItem.setVisible(true);
    }

    @Override
    public void hideProgressBar() {
        if (miActionProgressItem != null) miActionProgressItem.setVisible(false);
    }

    @Override
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
