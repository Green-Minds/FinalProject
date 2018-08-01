package green_minds.com.finalproject.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import butterknife.OnClick;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.adapters.ScoreAdapter;
import green_minds.com.finalproject.model.CategoryHelper;
import green_minds.com.finalproject.model.GlideApp;
import green_minds.com.finalproject.model.Goal;

public class UserInfoActivity extends AppCompatActivity {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_score)
    TextView tvScore;

    @BindView(R.id.btn_edit)
    TextView btnEdit;

    @BindView(R.id.tv_pin)
    TextView tvPin;

    @BindView(R.id.iv_prof_pic)
    ImageView ivProfPic;

    @BindView(R.id.navigationView)
    BottomNavigationView bottomNavigationView;

    private ParseUser mUser;
    private ArrayList<Goal> mGoals;
    private Context mContext;
    private MenuItem miActionProgressItem;
    private ScoreAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        mContext = this;

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_map:
                        Intent intent1 = new Intent(mContext, MapActivity.class);
                        startActivity(intent1);
                        return true;
                    case R.id.navigation_user:
                        //do nothing, already on page
                        return true;
                    case R.id.navigation_board:
                        Intent intent2 = new Intent(mContext, LeaderboardActivity.class);
                        startActivity(intent2);
                        return true;
                }
                return false;
            }
        });
    }

    private void setupUserInfo() {
        showProgressBar();
        mContext = this;
        mUser = ParseUser.getCurrentUser();
        if (mUser == null) {
            redirectToLogin();
            return;
        }

        Object username = mUser.get("original_username"); //check if exists first
        if(username != null){
            tvName.setText((String)username);
        } else{
            tvName.setText(mUser.getUsername());
        }

        tvScore.setText(mUser.getInt("points") + "");
        tvPin.setText(mUser.getInt("pincount") + "");

        ParseFile smallerPhoto = mUser.getParseFile("smaller_photo");
        if (smallerPhoto != null) {
            String url = smallerPhoto.getUrl();
            GlideApp.with(mContext).load(url).circleCrop().placeholder(R.drawable.anon).into(ivProfPic);
        } else {
            ParseFile photo = mUser.getParseFile("photo");
            if (photo != null) {
                String url = photo.getUrl();
                GlideApp.with(mContext).load(url).circleCrop().placeholder(R.drawable.anon).into(ivProfPic);
            } else {
                GlideApp.with(mContext).load(R.drawable.anon).circleCrop().into(ivProfPic);
            }
        }

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.include("goals");
        userQuery.whereEqualTo("objectId", mUser.getObjectId()).findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                hideProgressBar();
                if (e == null) {
                    if (objects.size() < 1) {
                        Toast.makeText(mContext, getString(R.string.misc_error), Toast.LENGTH_LONG).show();
                        return;
                    }

                    mUser = objects.get(0);
                    Object username = mUser.get("original_username"); //check if exists first
                    if(username != null){
                        tvName.setText((String)username);
                    } else{
                        tvName.setText(mUser.getUsername());
                    }
                    tvScore.setText(mUser.getInt("points") + "");
                    tvPin.setText(mUser.getInt("pincount") + "");

                    mGoals = (ArrayList<Goal>) mUser.get("goals");
                    if (mGoals == null) {
                        mGoals = new ArrayList<>();
                    }

                    mAdapter = new ScoreAdapter(mContext, CategoryHelper.categories, mGoals);
                    ListView listview = findViewById(R.id.listView);
                    listview.setAdapter(mAdapter);
                } else if (e.getCode() == ParseException.INVALID_SESSION_TOKEN) {
                    Toast.makeText(mContext, getString(R.string.session_error), Toast.LENGTH_LONG).show();
                } else if (e.getCode() == ParseException.CONNECTION_FAILED) {
                    Toast.makeText(mContext, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, getString(R.string.misc_error), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        setupUserInfo();
        return super.onPrepareOptionsMenu(menu);
    }

    @OnClick(R.id.btn_edit)
    public void goToEdit() {
        Intent i = new Intent(UserInfoActivity.this, EditProfileActivity.class);
        startActivityForResult(i, 30);
    }

    @OnClick(R.id.btn_goals)
    public void goToGoals() {
        Intent i = new Intent(UserInfoActivity.this, GoalActivity.class);
        if (mGoals == null) {
            Toast.makeText(mContext, getString(R.string.wait_content), Toast.LENGTH_SHORT).show();
            return;
        }
        i.putExtra("GOALS", mGoals);
        startActivityForResult(i, 31);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //update user info page with new info
        if (resultCode != RESULT_OK) return;
        if (requestCode == 31 && data != null) {
            List<Goal> g = data.getParcelableArrayListExtra("GOALS");
            mGoals.clear();
            mGoals.addAll(g);
            mAdapter.notifyDataSetChanged();
        } else if (requestCode == 30) {
            mUser = ParseUser.getCurrentUser();
            tvName.setText(mUser.getUsername());
            ParseFile smallerPhoto = mUser.getParseFile("smaller_photo");
            if (smallerPhoto != null) {
                String url = smallerPhoto.getUrl();
                GlideApp.with(mContext).load(url).circleCrop().placeholder(R.drawable.anon).into(ivProfPic);
            } else {
                ParseFile photo = mUser.getParseFile("photo");
                if (photo != null) {
                    String url = photo.getUrl();
                    GlideApp.with(mContext).load(url).circleCrop().placeholder(R.drawable.anon).into(ivProfPic);
                } else {
                    GlideApp.with(mContext).load(R.drawable.anon).circleCrop().into(ivProfPic);
                }
            }
        }
    }

    private void redirectToLogin() {
        Intent i = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(i);
        finish();
    }

    private void showProgressBar() {
        if (miActionProgressItem != null) miActionProgressItem.setVisible(true);
    }

    private void hideProgressBar() {
        if (miActionProgressItem != null) miActionProgressItem.setVisible(false);
    }
}
