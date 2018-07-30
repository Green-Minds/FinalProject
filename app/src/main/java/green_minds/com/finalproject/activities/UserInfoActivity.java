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
import green_minds.com.finalproject.model.CategoryHelper;

public class UserInfoActivity extends AppCompatActivity {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_score)
    TextView tvScore;

    @BindView(R.id.btn_edit)
    TextView btnEdit;

    @BindView(R.id.iv_prof_pic)
    ImageView ivProfPic;

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
    }

    private void setupUserInfo() {
        showProgressBar();
        mContext = this;
        mUser = ParseUser.getCurrentUser();
        if (mUser == null) {
            redirectToLogin();
            return;
        }

        tvName.setText(mUser.getUsername());
        int test = mUser.getInt("points");
        tvScore.setText(mUser.getInt("points") + "");
        ParseFile smallerPhoto = mUser.getParseFile("smaller_photo");
        if (smallerPhoto != null) {
            String url = smallerPhoto.getUrl();
            GlideApp.with(mContext).load(url).circleCrop().placeholder(R.drawable.anon).into(ivProfPic);
        } else {
            ParseFile photo = mUser.getParseFile("photo");
            if(photo != null){
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
                if (e != null) {
                    Toast.makeText(mContext, getString(R.string.misc_error), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    return;
                }
                if (objects.size() < 1) {
                    Toast.makeText(mContext, getString(R.string.misc_error), Toast.LENGTH_LONG).show();
                    return;
                }

                mUser = objects.get(0);
                mGoals = (ArrayList<Goal>) mUser.get("goals");
                if (mGoals == null) {
                    mGoals = new ArrayList<>();
                }

                mAdapter = new ScoreAdapter(mContext, CategoryHelper.categories, mGoals);
                ListView listview = findViewById(R.id.listView);
                listview.setAdapter(mAdapter);
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
        if(requestCode == 31 && data!=null){
            List<Goal> g = data.getParcelableArrayListExtra("GOALS");
            mGoals.clear();
            mGoals.addAll(g);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void redirectToLogin() {
        Intent i = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(i);
        finish();
    }

    public void showProgressBar() {
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        miActionProgressItem.setVisible(false);
    }
}
