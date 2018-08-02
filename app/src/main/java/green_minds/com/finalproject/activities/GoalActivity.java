package green_minds.com.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.fragments.EditGoalFragment;
import green_minds.com.finalproject.fragments.GoalListFragment;
import green_minds.com.finalproject.model.Goal;

public class GoalActivity extends AppCompatActivity implements EditGoalFragment.OnEditGoalListener, GoalListFragment.OnGoalListListener {

    private EditGoalFragment mEditGoalFragment;
    private GoalListFragment mGoalListFragment;

    private MenuItem miActionProgressItem;

    private boolean mSavedInstanceNull;
    private ArrayList<Goal> mGoals;
    private boolean saving; //app crashes if user navigates away from activity while fragment is still doing a network call

    @BindView(R.id.navigationView)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        ButterKnife.bind(this);
        mGoals = getIntent().getParcelableArrayListExtra("GOALS");
        mSavedInstanceNull = (savedInstanceState == null);
        saving = false;

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (saving) {
                    Toast.makeText(GoalActivity.this, getString(R.string.network_waiting), Toast.LENGTH_SHORT).show();

                } else {
                    switch (item.getItemId()) {
                        case R.id.navigation_map:
                            Intent intent1 = new Intent(GoalActivity.this, MapActivity.class);
                            startActivity(intent1);
                            return true;
                        case R.id.navigation_user:
                            returnToProfile();
                            return true;
                        case R.id.navigation_board:
                            Intent intent2 = new Intent(GoalActivity.this, LeaderboardActivity.class);
                            startActivity(intent2);
                            return true;
                    }
                }
                return false;
            }
        });
        if (mSavedInstanceNull) {
            mEditGoalFragment = EditGoalFragment.newInstance(mGoals);
            mGoalListFragment = GoalListFragment.newInstance(mGoals);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, mEditGoalFragment);
            ft.commit();
        }
        if (mEditGoalFragment == null || mGoalListFragment == null) {
            mEditGoalFragment = EditGoalFragment.newInstance(mGoals);
            mGoalListFragment = GoalListFragment.newInstance(mGoals);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);

        //need menu to be loaded before we start loading fragments so we can show progress item

        return super.onPrepareOptionsMenu(menu);
    }


    @OnClick(R.id.btn_new)
    public void openNewFragment() {
        if (saving) {
            Toast.makeText(this, getString(R.string.network_waiting), Toast.LENGTH_SHORT).show();
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, mEditGoalFragment);
        ft.commit();
    }

    @OnClick(R.id.btn_edit)
    public void openListFragment() {
        if (saving) {
            Toast.makeText(this, getString(R.string.network_waiting), Toast.LENGTH_SHORT).show();
            return;
        }
        mEditGoalFragment = EditGoalFragment.newInstance(mGoals);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, mGoalListFragment);
        ft.commit();
    }

    @OnClick(R.id.btn_return)
    public void returnToProfile() {
        if (saving) {
            Toast.makeText(this, getString(R.string.network_waiting), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(this, UserInfoActivity.class);
        i.putExtra("GOALS", mGoals);
        setResult(RESULT_OK, i);
        finish();
    }

    public void openEditFragment(Goal goal) {
        if (saving) {
            Toast.makeText(this, getString(R.string.network_waiting), Toast.LENGTH_SHORT).show();
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mEditGoalFragment = EditGoalFragment.newInstance(goal, mGoals);
        ft.replace(R.id.container, mEditGoalFragment);
        ft.commit();
    }

    public void onNewGoal(Goal goal) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mEditGoalFragment = EditGoalFragment.newInstance(mGoals);
        ft.replace(R.id.container, mGoalListFragment);
        ft.commit();
    }

    public void showProgressBar() {
        if (miActionProgressItem != null) miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        if (miActionProgressItem != null) miActionProgressItem.setVisible(false);
    }

    @Override
    public void onBackPressed() {
        if (saving) {
            Toast.makeText(this, getString(R.string.network_waiting), Toast.LENGTH_SHORT).show();
            return;
        }
        if (getCallingActivity() != null && getCallingActivity().getClassName().equals("green_minds.com.finalproject.activities.UserInfoActivity")) {
            returnToProfile();
        } else {
            super.onBackPressed();
        }
    }

    public void setNetworkCallInProgress(boolean flag) {
        saving = flag;
    }
}
