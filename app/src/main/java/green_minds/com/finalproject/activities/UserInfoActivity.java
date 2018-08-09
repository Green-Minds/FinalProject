package green_minds.com.finalproject.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
import green_minds.com.finalproject.adapters.ScoreAdapter;
import green_minds.com.finalproject.fragments.UserInfoFragment;
import green_minds.com.finalproject.model.Goal;

public class UserInfoActivity extends AppCompatActivity implements UserInfoFragment.OnUserInfoListener{

//    @BindView(R.id.tv_name)
//    TextView tvName;
//
//    @BindView(R.id.tv_score)
//    TextView tvScore;
//
//    @BindView(R.id.tv_connection)
//    TextView tvConnection;
//
//    @BindView(R.id.iv_prof_pic)
//    ImageView ivProfPic;
//
//    @BindView(R.id.btn_popup)
//    ImageButton btnPopup;
//
//    @BindView(R.id.chart)
//    BarChart chart;
//
    @BindView(R.id.profileToolbar)
    Toolbar toolbar;
//
//    @BindView(R.id.tvTitle)
//    TextView tvTitle;

    private android.support.v7.app.ActionBar actionBar;
    private FragmentTransaction ft;
    private UserInfoFragment userInfoFragment;
    private MenuItem miActionProgressItem;
    private ParseUser parseUser;
    private ArrayList<Goal> goals;
    private Context context;
    private ScoreAdapter scoreAdapter;
    private ProgressDialog pd;

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

//    private void setUpGraph(){
//
//        XAxis xAxis = chart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTextSize(10f);
//        xAxis.setDrawAxisLine(false);
//        xAxis.setDrawGridLines(false);
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return (int)value + "";
//            }
//        });
//        chart.setXAxisRenderer(new CustomXAxisRenderer(chart.getViewPortHandler(), chart.getXAxis(), chart.getTransformer(YAxis.AxisDependency.LEFT)));
//        chart.setDrawBorders(false);
//        chart.setExtraBottomOffset(18);
//
//        YAxis leftAxis = chart.getAxisLeft();
//        YAxis rightAxis = chart.getAxisRight();
//        leftAxis.setEnabled(false);
//        rightAxis.setEnabled(false);
//        chart.setDrawBorders(false);
//
//        List<BarEntry> entries = new ArrayList<>();
//
//        for(int i = 0; i < 6; i++){
//            int checkins = mUser.getInt(CategoryHelper.getTypeKey(i));
//            entries.add(new BarEntry(i, checkins));
//        }
//
//        chart.getLegend().setEnabled(false);
//        chart.setDescription(null);
//
//        BarDataSet set = new BarDataSet(entries, "BarDataSet");
//        set.setColor(getResources().getColor(R.color.colorTeal));
//
//        BarData data = new BarData(set);
//        data.setValueFormatter(new IValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
//                return ((int)value) + "";
//            }
//        });
//        data.setValueTextSize(14);
//        data.setBarWidth(0.8f); // set custom bar width
//        chart.setData(data);
//        chart.setFitBars(true); // make the x-axis fit exactly all bars
//        chart.invalidate(); // refresh
//    }
//
//    private void setupUserInfo() {
//        //showProgressBar();
//        setUpGraph();
//        mContext = this;
//
//        if (mUser == null) {
//            //redirectToLogin();
//            return;
//        }
//
//        Object username = mUser.get("original_username"); //check if exists first
//        if (username != null) {
//            tvName.setText((String) username);
//        } else {
//            tvName.setText(mUser.getUsername());
//        }
//
//        tvScore.setText(mUser.getInt("points") + "");
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
//                    if (username != null) {
//                        tvName.setText((String) username);
//                    } else {
//                        tvName.setText(mUser.getUsername());
//                    }
//                    tvScore.setText(mUser.getInt("points") + "");
//                    tvConnection.setText(mUser.getString("connection"));
//
//                    mGoals = (ArrayList<Goal>) mUser.get("goals");
//                    if (mGoals == null) {
//                        mGoals = new ArrayList<>();
//                    }
//
//                    mGoalAdapter = new GoalAdapter(UserInfoActivity.this, mUser, mGoals, null);
//                    ListView listview = findViewById(R.id.goal_list);
//                    listview.setAdapter(mGoalAdapter);
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
//
//    public class CustomXAxisRenderer extends XAxisRenderer {
//        public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
//            super(viewPortHandler, xAxis, trans);
//        }
//
//        @Override
//        protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
//            int i = Integer.parseInt(formattedLabel);
//            Utils.drawImage(c, CategoryHelper.getIconResource(i, mContext), (int)x, (int)y + 24, 64, 64);
//        }
//    }

    public void setListViewHeight(){
        userInfoFragment.setListViewHeight();
    };
}
