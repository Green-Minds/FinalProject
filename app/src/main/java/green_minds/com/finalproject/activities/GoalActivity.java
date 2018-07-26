package green_minds.com.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import java.util.ArrayList;

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

    private boolean savedInstanceNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        ButterKnife.bind(this);
        savedInstanceNull = (savedInstanceState == null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);

        if(savedInstanceNull){
            mEditGoalFragment = EditGoalFragment.newInstance();
            mGoalListFragment = GoalListFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, mEditGoalFragment);
            ft.commit();
        }

        return super.onPrepareOptionsMenu(menu);
    }


    @OnClick(R.id.btn_new)
    public void openNewFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, mEditGoalFragment);
        ft.commit();
    }
    @OnClick(R.id.btn_edit)
    public void openListFragment(){
        mEditGoalFragment = EditGoalFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, mGoalListFragment);
        ft.commit();
    }
    @OnClick(R.id.btn_return)
    public void returnToProfile(){
        Intent i = new Intent(this, UserInfoActivity.class);
        startActivity(i);
        finish();
    }
    public void openEditFragment(Goal goal, ArrayList<Goal> goals){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mEditGoalFragment = EditGoalFragment.newInstance(goal, goals);
        //mEditGoalFragment.setExistingGoal(goal);
        ft.replace(R.id.container, mEditGoalFragment);
        ft.commit();
    }
    public void onNewGoal(Goal goal){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mEditGoalFragment = EditGoalFragment.newInstance();
        ft.replace(R.id.container, mGoalListFragment);
        ft.commit();
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }
}
