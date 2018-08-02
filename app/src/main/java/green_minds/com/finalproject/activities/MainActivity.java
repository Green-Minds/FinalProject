package green_minds.com.finalproject.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.adapters.ScoreAdapter;
import green_minds.com.finalproject.fragments.LeaderboardFragment;
import green_minds.com.finalproject.fragments.UserInfoFragment;
import green_minds.com.finalproject.model.Goal;

public class MainActivity extends AppCompatActivity implements LeaderboardFragment.OnFragmentInteractionListener,
        UserInfoFragment.OnUserInfoListener {

    private LeaderboardFragment leaderboardFragment;
    private FragmentTransaction ft;
    private UserInfoFragment userInfoFragment;
    private ArrayList<ParseUser> users = new ArrayList<>();
    private MenuItem miActionProgressItem;
    private ParseUser parseUser;
    private ArrayList<Goal> goals;
    private Context context;
    private ScoreAdapter scoreAdapter;
    @BindView(R.id.bottomNavigation) public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadUsers();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_map:
                        startActivity(new Intent(MainActivity.this, MapActivity.class));
                        finish();
                        return true;
                    case R.id.navigation_user:
                        getUserInfo();
                        return true;
                    case R.id.navigation_board:
                        loadUsers();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
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

    public void loadUsers() {
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

                        leaderboardFragment = leaderboardFragment.newInstance(users);
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, leaderboardFragment);
                        ft.commit();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
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

    @Override
    public void goToGoals(ArrayList<Goal> g) {
        Intent i = new Intent(MainActivity.this, GoalActivity.class);
        if (goals == null) {
            Toast.makeText(context, getString(R.string.wait_content), Toast.LENGTH_SHORT).show();
            return;
        }
        i.putExtra("GOALS", goals);
        startActivityForResult(i, 31);
    }

    @Override
    public void goToEdit(ArrayList<Goal> g) {
        Intent i = new Intent(MainActivity.this, EditProfileActivity.class);
        startActivityForResult(i, 30);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //update user info page with new info
        if (resultCode != RESULT_OK) return;
        if (requestCode == 31 && data != null) {
            List<Goal> g = data.getParcelableArrayListExtra("GOALS");
            goals.clear();
            goals.addAll(g);
            scoreAdapter.notifyDataSetChanged();
        } else if (requestCode == 30) {
            parseUser = ParseUser.getCurrentUser();
            //tvName.setText(parseUser.getUsername());
            ParseFile smallerPhoto = parseUser.getParseFile("smaller_photo");
            if (smallerPhoto != null) {
                String url = smallerPhoto.getUrl();
                //GlideApp.with(context).load(url).circleCrop().placeholder(R.drawable.anon).into(ivProfPic);
            } else {
                ParseFile photo = parseUser.getParseFile("photo");
                if (photo != null) {
                    String url = photo.getUrl();
                    //GlideApp.with(context).load(url).circleCrop().placeholder(R.drawable.anon).into(ivProfPic);
                } else {
                    //GlideApp.with(context).load(R.drawable.anon).circleCrop().into(ivProfPic);
                }
            }
        }
    }

    private void getUserInfo() {
        parseUser = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.include("goals");

        userQuery.whereEqualTo("objectId", parseUser.getObjectId()).findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                if (e == null) {
                    if (objects.size() < 1) {
                        Toast.makeText(context, getString(R.string.misc_error), Toast.LENGTH_LONG).show();
                        return;
                    }
                    parseUser = objects.get(0);

                    userInfoFragment = userInfoFragment.newInstance(parseUser);
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, userInfoFragment);
                    ft.commit();

                } else if (e.getCode() == ParseException.INVALID_SESSION_TOKEN) {
                    Toast.makeText(context, getString(R.string.session_error), Toast.LENGTH_LONG).show();
                } else if (e.getCode() == ParseException.CONNECTION_FAILED) {
                    Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, getString(R.string.misc_error), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }
}
