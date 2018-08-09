package green_minds.com.finalproject.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.adapters.ScoreAdapter;
import green_minds.com.finalproject.fragments.LeaderboardFragment;
import green_minds.com.finalproject.fragments.MapFragment;
import green_minds.com.finalproject.fragments.UserInfoFragment;
import green_minds.com.finalproject.model.Goal;
import green_minds.com.finalproject.model.MyItem;

public class MainActivity extends AppCompatActivity implements LeaderboardFragment.OnFragmentInteractionListener,
        UserInfoFragment.OnUserInfoListener, MapFragment.OnFragmentInteractionListener {

    private LeaderboardFragment leaderboardFragment;
    private FragmentTransaction ft;
    private UserInfoFragment userInfoFragment;
    private MapFragment mapFragment;
    private ArrayList<ParseUser> users = new ArrayList<>();
    private Integer skip = null;
    private MenuItem miActionProgressItem;
    private ParseUser parseUser;
    private ArrayList<Goal> goals;
    private Context context;
    private ScoreAdapter scoreAdapter;

    @BindView(R.id.bottomNavigation)
    public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        showMap();
        context = this;

        bottomNavigationView.setSelectedItemId(R.id.navigation_map);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.navigation_map:
                        if (!bottomNavigationView.getMenu().findItem(R.id.navigation_map).isChecked())
                            reloadMap();
                        return true;

                    case R.id.navigation_user:
                        if (!bottomNavigationView.getMenu().findItem(R.id.navigation_user).isChecked())
                            getUserInfo();
                        return true;

                    case R.id.navigation_board:
                        if (!bottomNavigationView.getMenu().findItem(R.id.navigation_board).isChecked())
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

    public void goToEdit() {
        Intent i = new Intent(this, EditProfileActivity.class);
        startActivityForResult(i, 30);
    }

    @Override
    public void goToCheckin(Location currentLocation) {
        parseUser = ParseUser.getCurrentUser();
        if (parseUser == null) {
            Intent log = new Intent(MainActivity.this, LoginActivity.class);
        }

        Intent intent = new Intent(MainActivity.this, CheckInActivity.class);
        intent.putExtra("latitude", currentLocation.getLatitude());
        intent.putExtra("longitude", currentLocation.getLongitude());
        startActivity(intent);
    }

    @Override
    public void goToNewPin(Location currentLocation) {
        parseUser = ParseUser.getCurrentUser();
        if (parseUser == null) {
            Intent log = new Intent(MainActivity.this, LoginActivity.class);
        }


        if (currentLocation != null ) {
            Intent intent = new Intent(MainActivity.this, NewPinActivity.class);
            intent.putExtra("latitude", currentLocation.getLatitude());
            intent.putExtra("longitude", currentLocation.getLongitude());
            startActivityForResult(intent, 20);
        } else {

            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Your location is not available!")
                    .show();

        }

    }

    @Override
    public void goToPinDetails(MyItem myItem) {
        Intent intent = new Intent(MainActivity.this, PinDetailActivity.class);
        intent.putExtra("Title", myItem.getTitle());
        intent.putExtra("comment", myItem.getSnippet());
        intent.putExtra("distance", myItem.getDistance());
        intent.putExtra("image", myItem.getImageUrl());
        intent.putExtra("typeicon", myItem.getTypeIcon());
        intent.putExtra("type", myItem.getType());
        intent.putExtra("checkins" , myItem.getCheckins());
        startActivity(intent);

    }

    @Override
    public void logout() {
        Log.i("Logout", "Logging out");
        if (!isOnline()) return;
        if (AccessToken.getCurrentAccessToken() != null) {
            Log.i("Valid", "loggin out");
            LoginManager.getInstance().logOut();
        }
        ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (ParseUser.getCurrentUser() == null) {
                    Log.i("Logout2", "Logged out");
                    startActivity(new Intent(MainActivity.this, SplashActivity.class));
                    finish();
                }
            }
        });
    }

    private void reloadMap() {
        ft = getSupportFragmentManager().beginTransaction();
        if (leaderboardFragment != null) ft.hide(leaderboardFragment);
        if (userInfoFragment != null) ft.hide(userInfoFragment);
        ft.show(mapFragment);
        ft.commit();
    }

    private void showMap() {
        mapFragment = mapFragment.newInstance();
        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container_map, mapFragment);
        ft.commit();
    }

    public void loadUsers() {
        if (isOnline()) {
            ParseQuery query = ParseUser.getQuery();
            query.orderByDescending("points").addDescendingOrder("pincount")
                    .whereEqualTo("connection", ParseUser.getCurrentUser().getString("connection"))
                    .setLimit(20).findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    ParseUser user = null;
                    if (e == null) {
                        users.clear();
                        users.addAll(objects);
                        skip = objects.size();
                        leaderboardFragment = leaderboardFragment.newInstance(users, skip);
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.hide(mapFragment);
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

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void goToGoals(Goal g) {
        Intent i = new Intent(MainActivity.this, GoalActivity.class);
        if (goals == null) {
            Toast.makeText(context, getString(R.string.wait_content), Toast.LENGTH_SHORT).show();
            return;
        }
        i.putExtra("GOALS", goals);
        startActivityForResult(i, 31);
    }

    public void goToEdit(ArrayList<Goal> g) {
        Intent i = new Intent(MainActivity.this, EditProfileActivity.class);
        startActivityForResult(i, 30);
    }

    public void goToDetail(Goal g, int checkins) {
        Intent i = new Intent(this, GoalDetailActivity.class);
        i.putExtra("GOAL", g);
        i.putExtra("CHECKINS", checkins);
        startActivity(i);
    }

    public void openGoalEditPage(int pos, ArrayList<Goal> goals) {
        Intent i = new Intent(this, EditGoalActivity.class);
        boolean beingEdited = false;
        i.putExtra("GOALS", goals);
        if (pos >= 0) {
            beingEdited = true;
            i.putExtra("GOAL", pos);
        }
        i.putExtra("beingedited", beingEdited);
        startActivityForResult(i, 31);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //update user info page with new info
        if (resultCode != RESULT_OK) return;
        if (requestCode == 31 && data != null) {
            List<Goal> g = data.getParcelableArrayListExtra("GOALS");
            userInfoFragment.setGoals(g);
        } else if (requestCode == 30) {
            userInfoFragment.refreshUserData();
        } else if (requestCode == 20) {
            // set the screen for adjusting pins
            data.getExtras();
            String id = data.getStringExtra("id");
            mapFragment.adjustScreen(id);
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
                    ft.hide(mapFragment);
                    ft.replace(R.id.fragment_container, userInfoFragment);
                    ft.commit();

                } else if (e.getCode() == ParseException.INVALID_SESSION_TOKEN) {
                    Toast.makeText(context, getString(R.string.session_error), Toast.LENGTH_LONG).show();
                    logout();
                } else if (e.getCode() == ParseException.CONNECTION_FAILED) {
                    Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, getString(R.string.misc_error), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    public void showNoDataMessage() {
        userInfoFragment.showNoDataMessage();
    }

    public void setListViewHeight() {
        userInfoFragment.setListViewHeight();
    }
}
