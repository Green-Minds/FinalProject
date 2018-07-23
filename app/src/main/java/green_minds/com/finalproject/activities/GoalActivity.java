package green_minds.com.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.fragments.EditGoalFragment;
import green_minds.com.finalproject.fragments.NewGoalFragment;

public class GoalActivity extends AppCompatActivity {

    private NewGoalFragment newGoalFragment;
    private EditGoalFragment editGoalFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        ButterKnife.bind(this);
        if(savedInstanceState == null){
            //newGoalFragment = newGoalFragment.newInstance();
            //editGoalFragment = editGoalFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, newGoalFragment);
            ft.commit();
        }
    }
    @OnClick(R.id.btn_new)
    public void openNewFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, newGoalFragment);
    }
    @OnClick(R.id.btn_edit)
    public void openEditFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, editGoalFragment);
    }
    @OnClick(R.id.btn_return)
    public void returnToProfile(){
        Intent i = new Intent(this, UserInfoActivity.class);
        startActivity(i);
        finish();
    }
}
