package green_minds.com.finalproject.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import green_minds.com.finalproject.model.GlideApp;
import green_minds.com.finalproject.model.Goal;
import green_minds.com.finalproject.model.PinCategoryHelper;

public class UserInfoActivity extends AppCompatActivity {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.btn_edit)
    TextView btnEdit;

    @BindView(R.id.iv_prof_pic)
    ImageView ivProfPic;

    private ParseUser mUser;
    private ArrayList<Goal> mGoals;
    private Context mContext;
    private MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
    }

    private void setupUserInfo(){
        showProgressBar();
        mUser = ParseUser.getCurrentUser();
        if(mUser == null){
            redirectToLogin();
        }

        tvName.setText(mUser.getUsername());

        mContext = this;
        ParseFile photo = mUser.getParseFile("photo");
        String url = photo.getUrl();
        GlideApp.with(mContext).load(url).circleCrop().placeholder(R.drawable.q_mark).into(ivProfPic);
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.include("goals");
        userQuery.whereEqualTo("objectId", mUser.getObjectId()).findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                if(e != null){
                    Toast.makeText(mContext, "Error. Please try again later.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    return;
                }
                if(objects.size() < 1){
                    Toast.makeText(mContext, "Error. Please try again later.", Toast.LENGTH_LONG).show();
                    return;
                }

                mUser = objects.get(0);
                mGoals = (ArrayList<Goal>)mUser.get("goals");
                if( mGoals == null){
                    mGoals = new ArrayList<>();
                }

                ScoreAdapter adapter = new ScoreAdapter(mContext, PinCategoryHelper.getCategoriesArrayList(), mGoals);
                ListView listview = findViewById(R.id.listView);
                listview.setAdapter(adapter);
                hideProgressBar();
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
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        setupUserInfo();
        return super.onPrepareOptionsMenu(menu);
    }

    @OnClick(R.id.btn_edit)
    public void goToEdit(){
        Intent i = new Intent(UserInfoActivity.this, EditProfileActivity.class);
        startActivityForResult(i, 30);
    }

    @OnClick(R.id.btn_goals)
    public void goToGoals(){
        Intent i = new Intent(UserInfoActivity.this, GoalActivity.class);
        startActivityForResult(i, 31);
    }

    private void redirectToLogin(){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
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
