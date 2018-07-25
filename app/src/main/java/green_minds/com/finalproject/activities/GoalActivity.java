package green_minds.com.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        ButterKnife.bind(this);
        if(savedInstanceState == null){
            mEditGoalFragment = EditGoalFragment.newInstance();
            mGoalListFragment = GoalListFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, mEditGoalFragment);
            ft.commit();
        }
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
}
