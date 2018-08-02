package green_minds.com.finalproject.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
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

public class UserInfoFragment extends Fragment {

    public interface OnUserInfoListener {
        void goToGoals(ArrayList<Goal> g);
        void goToEdit(ArrayList<Goal> g);
    }

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

    @BindView(R.id.listView)
    ListView listView;

    @BindView(R.id.navigationView)
    BottomNavigationView bottomNavigationView;

    private ParseUser mUser;
    private ArrayList<Goal> mGoals;
    private Context mContext;
    private MenuItem miActionProgressItem;
    private ScoreAdapter mAdapter;

    private OnUserInfoListener mListener;

    public UserInfoFragment() {
    }

    public static UserInfoFragment newInstance() {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_info, container, false);
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
        if (context instanceof OnUserInfoListener) {
            mListener = (OnUserInfoListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserInfoListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
        setupUserInfo();
    }

    @OnClick(R.id.btn_edit)
    public void goToEdit() {
        mListener.goToEdit(mGoals);
    }

    public void setGoals(List<Goal> g){
        mGoals.clear();
        mGoals.addAll(g);
        mAdapter.notifyDataSetChanged();
    }

    public void refreshUserData(){
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

    private void setupUserInfo() {
        mUser = ParseUser.getCurrentUser();

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
                    listView.setAdapter(mAdapter);
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

    @OnClick(R.id.btn_goals)
    public void goToGoals() {
        if (mGoals == null) {
            Toast.makeText(mContext, getString(R.string.wait_content), Toast.LENGTH_SHORT).show();
            return;
        }
        mListener.goToGoals(mGoals);
    }

}
