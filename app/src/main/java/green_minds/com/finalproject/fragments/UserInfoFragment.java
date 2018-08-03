package green_minds.com.finalproject.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.parse.ParseFile;
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

public class UserInfoFragment extends Fragment {

    public interface OnUserInfoListener {
        void goToGoals(ArrayList<Goal> g);
        void goToEdit(ArrayList<Goal> g);
        void showProgressBar();
        void hideProgressBar();
        void logout();
    }

    private static final String ARG_PARAM1 = "user";

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_score)
    TextView tvScore;

//    @BindView(R.id.tv_pin)
    TextView tvPin;

    @BindView(R.id.iv_prof_pic)
    ImageView ivProfPic;

    @BindView(R.id.goal_list)
    ListView listView;

    @BindView(R.id.btn_popup)
    ImageButton btnPopup;

    private ParseUser mUser;
    private ArrayList<Goal> mGoals;
    private Context mContext;
    private ScoreAdapter mAdapter;

    private OnUserInfoListener mListener;

    public UserInfoFragment() {
    }

    public static UserInfoFragment newInstance(ParseUser user) {
        UserInfoFragment fragment = new UserInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, user);
        fragment.setArguments(args);
        return fragment;
    }

    @OnClick(R.id.btn_popup)
    public void showPopUp() {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(getContext(), btnPopup);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.popup_user, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                //TODO - add to strings.xml
                String[] ids = getResources().getResourceName(item.getItemId()).split("\\/");
                String id = ids[1];
                if (id.equals("mi_logout")) {
                    logOut();
                } else if (id.equals("mi_edit_prof")) {
                    goToEdit();
                } else {
                    goToGoals();
                }
                return true;
            }
        });

        popup.show(); //showing popup menu
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mUser = getArguments().getParcelable("user");
        mGoals = (ArrayList<Goal>) mUser.get("goals");
        if (mGoals == null) {
            mGoals = new ArrayList<>();
        }
        //mAdapter = new ScoreAdapter(mContext, CategoryHelper.categories, mGoals);

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

        setUp();
        listView.setAdapter(mAdapter);
        //setupUserInfo();
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

    private void setUp() {
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
    }

//    private void setupUserInfo() {
//        mUser = ParseUser.getCurrentUser();
//
//        Object username = mUser.get("original_username"); //check if exists first
//        if(username != null){
//            tvName.setText((String)username);
//        } else{
//            tvName.setText(mUser.getUsername());
//        }
//
//        tvScore.setText(mUser.getInt("points") + "");
//        tvPin.setText(mUser.getInt("pincount") + "");
//
//        ParseFile smallerPhoto = mUser.getParseFile("smaller_photo");
//        if (smallerPhoto != null) {
//            String url = smallerPhoto.getUrl();
//            GlideApp.with(mContext).load(url).circleCrop().placeholder(R.drawable.anon).into(ivProfPic);
//        } else {
//            ParseFile photo = mUser.getParseFile("photo");
//            if (photo != null) {
//                String url = photo.getUrl();
//                GlideApp.with(mContext).load(url).circleCrop().placeholder(R.drawable.anon).into(ivProfPic);
//            } else {
//                GlideApp.with(mContext).load(R.drawable.anon).circleCrop().into(ivProfPic);
//            }
//        }
//
//        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
//        userQuery.include("goals");
//        userQuery.whereEqualTo("objectId", mUser.getObjectId()).findInBackground(new FindCallback<ParseUser>() {
//
//            @Override
//            public void done(List<ParseUser> objects, com.parse.ParseException e) {
//                if (e == null) {
//                    if (objects.size() < 1) {
//                        Toast.makeText(mContext, getString(R.string.misc_error), Toast.LENGTH_LONG).show();
//                        return;
//                    }
//
//                    mUser = objects.get(0);
//                    Object username = mUser.get("original_username"); //check if exists first
//                    if(username != null){
//                        tvName.setText((String)username);
//                    } else{
//                        tvName.setText(mUser.getUsername());
//                    }
//                    tvScore.setText(mUser.getInt("points") + "");
//                    tvPin.setText(mUser.getInt("pincount") + "");
//
//                    mGoals = (ArrayList<Goal>) mUser.get("goals");
//                    if (mGoals == null) {
//                        mGoals = new ArrayList<>();
//                    }
//                    mAdapter = new ScoreAdapter(mContext, CategoryHelper.categories, mGoals);
//                    listView.setAdapter(mAdapter);
//                    mListener.hideProgressBar();
//                } else if (e.getCode() == ParseException.INVALID_SESSION_TOKEN) {
//                    Toast.makeText(mContext, getString(R.string.session_error), Toast.LENGTH_LONG).show();
//                } else if (e.getCode() == ParseException.CONNECTION_FAILED) {
//                    Toast.makeText(mContext, getString(R.string.network_error), Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(mContext, getString(R.string.misc_error), Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

//    @OnClick(R.id.btn_goals)
    public void goToGoals() {
        if (mGoals == null) {
            Toast.makeText(mContext, getString(R.string.wait_content), Toast.LENGTH_SHORT).show();
            return;
        }
        mListener.goToGoals(mGoals);
    }

//    @OnClick(R.id.btnLogout)
//    public void logout() {
//        mListener.logout();
//    }

    public void logOut() {
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
            ParseUser.logOut();
        } else {
            ParseUser.logOut();
        }
        redirectToLogin();
    }

    private void redirectToLogin() {
        //do something
    }

}
