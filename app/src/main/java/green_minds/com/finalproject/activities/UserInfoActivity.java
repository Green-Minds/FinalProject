package green_minds.com.finalproject.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        mUser = ParseUser.getCurrentUser();
        if(mUser == null){
            redirectToLogin();
        }

        tvName.setText(mUser.getUsername());

        mContext = this;
        ParseFile photo = mUser.getParseFile("photo");
        String url = photo.getUrl();
        GlideApp.with(mContext).load(url).circleCrop().into(ivProfPic);
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.include("goals");
        userQuery.whereEqualTo("objectId", mUser.getObjectId()).findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, com.parse.ParseException e) {
                //TODO - deal with exceptions
                mUser = objects.get(0);
                mGoals = (ArrayList<Goal>)mUser.get("goals");
                if( mGoals == null){
                    mGoals = new ArrayList<>();
                }

                ScoreAdapter adapter = new ScoreAdapter(mContext, PinCategoryHelper.getCategoriesArrayList(), mGoals);
                ListView listview = findViewById(R.id.listView);
                listview.setAdapter(adapter);
            }
        });

    }

    @OnClick(R.id.btn_edit)
    public void finish(){
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
}
