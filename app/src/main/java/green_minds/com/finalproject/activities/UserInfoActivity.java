package green_minds.com.finalproject.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import green_minds.com.finalproject.fragments.UserInfoFragment;
import green_minds.com.finalproject.model.Goal;

public class UserInfoActivity extends AppCompatActivity implements UserInfoFragment.OnUserInfoListener{

    private android.support.v7.app.ActionBar actionBar;
    private FragmentTransaction ft;
    private UserInfoFragment userInfoFragment;
    private ParseUser parseUser;
    private Context context;
    private ProgressDialog pd;

    @BindView(R.id.profileToolbar)
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        context = this;

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        parseUser = getIntent().getParcelableExtra("user");
        actionBar.setTitle(parseUser.get("original_username").toString());
        setupUserInfo();
    }

    private void setupUserInfo() {
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
                    ft.add(R.id.fragment_container_user, userInfoFragment);
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

    @Override
    public void goToEdit() {
    }

    @Override
    public void openGoalEditPage(int pos, ArrayList<Goal> goals) {

    }

    @Override
    public void goToDetail(Goal g, int c) {

    }

    @Override
    public void showProgressBar() {
        pd = new ProgressDialog(this);
        pd.setTitle("Processing...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
    }

    @Override
    public void hideProgressBar() {
        pd.dismiss();
    }

    @Override
    public void logout() {

    }

    @Override
    public void showNoDataMessage() {
        userInfoFragment.showNoDataMessage();
    }

    @Override
    public void setListViewHeight() {

    }
}
