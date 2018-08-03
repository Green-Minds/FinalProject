package green_minds.com.finalproject.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;
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
import green_minds.com.finalproject.adapters.GoalAdapter;
import green_minds.com.finalproject.adapters.ScoreAdapter;
import green_minds.com.finalproject.model.CategoryHelper;
import green_minds.com.finalproject.model.GlideApp;
import green_minds.com.finalproject.model.Goal;

public class UserInfoActivity extends AppCompatActivity {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_score)
    TextView tvScore;

    @BindView(R.id.tv_connection)
    TextView tvConnection;

    @BindView(R.id.iv_prof_pic)
    ImageView ivProfPic;

    @BindView(R.id.btn_popup)
    ImageButton btnPopup;

    @BindView(R.id.chart)
    BarChart chart;

    private ParseUser mUser;
    private ArrayList<Goal> mGoals;
    private Context mContext;
    private MenuItem miActionProgressItem;
    private GoalAdapter mGoalAdapter;
    private ScoreAdapter mScoreAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        mContext = this;
        setupUserInfo();
    }

    @OnClick(R.id.btn_popup)
    public void showPopUp() {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(UserInfoActivity.this, btnPopup);
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

    private void setUpGraph(){

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (int)value + "";
            }
        });
        chart.setXAxisRenderer(new CustomXAxisRenderer(chart.getViewPortHandler(), chart.getXAxis(), chart.getTransformer(YAxis.AxisDependency.LEFT)));
        chart.setDrawBorders(false);
        chart.setExtraBottomOffset(18);

        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        leftAxis.setEnabled(false);
        rightAxis.setEnabled(false);
        chart.setDrawBorders(false);

        mUser = ParseUser.getCurrentUser();

        List<BarEntry> entries = new ArrayList<>();

        for(int i = 0; i < 6; i++){
            int checkins = mUser.getInt(CategoryHelper.getTypeKey(i));
            entries.add(new BarEntry(i, checkins));
        }

        chart.getLegend().setEnabled(false);
        chart.setDescription(null);

        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        set.setColor(getResources().getColor(R.color.colorTeal));

        BarData data = new BarData(set);
        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return ((int)value) + "";
            }
        });
        data.setValueTextSize(14);
        data.setBarWidth(0.8f); // set custom bar width
        chart.setData(data);
        chart.setFitBars(true); // make the x-axis fit exactly all bars
        chart.invalidate(); // refresh
    }

    private void setupUserInfo() {
        showProgressBar();
        setUpGraph();
        mContext = this;
        mUser = ParseUser.getCurrentUser();
        if (mUser == null) {
            redirectToLogin();
            return;
        }

        Object username = mUser.get("original_username"); //check if exists first
        if (username != null) {
            tvName.setText((String) username);
        } else {
            tvName.setText(mUser.getUsername());
        }

        tvScore.setText(mUser.getInt("points") + "");

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
                    if (username != null) {
                        tvName.setText((String) username);
                    } else {
                        tvName.setText(mUser.getUsername());
                    }
                    tvScore.setText(mUser.getInt("points") + "");
                    tvConnection.setText(mUser.getString("connection"));

                    mGoals = (ArrayList<Goal>) mUser.get("goals");
                    if (mGoals == null) {
                        mGoals = new ArrayList<>();
                    }

                    mGoalAdapter = new GoalAdapter(mContext, mGoals, null);
                    ListView listview = findViewById(R.id.goal_list);
                    listview.setAdapter(mGoalAdapter);
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

    public void goToEdit() {
        Intent i = new Intent(UserInfoActivity.this, EditProfileActivity.class);
        startActivityForResult(i, 30);
    }

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
        //update user info page with new info
        if (resultCode != RESULT_OK) return;
        if (requestCode == 31 && data != null) {
            List<Goal> g = data.getParcelableArrayListExtra("GOALS");
            mGoals.clear();
            mGoals.addAll(g);
            mGoalAdapter.notifyDataSetChanged();
        } else if (requestCode == 30) {
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
    }

    private void redirectToLogin() {
        Intent i = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(i);
        finish();
    }

    private void showProgressBar() {
        if (miActionProgressItem != null) miActionProgressItem.setVisible(true);
    }

    private void hideProgressBar() {
        if (miActionProgressItem != null) miActionProgressItem.setVisible(false);
    }

    public void logOut() {
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
            ParseUser.logOut();
        } else {
            ParseUser.logOut();
        }
        redirectToLogin();
    }

    public class CustomXAxisRenderer extends XAxisRenderer {
        public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
            super(viewPortHandler, xAxis, trans);
        }

        @Override
        protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
            //String line[] = formattedLabel.split("\n");
            //Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees);
            int i = Integer.parseInt(formattedLabel);
            Utils.drawImage(c, CategoryHelper.getIconResource(i, mContext), (int)x, (int)y + 24, 64, 64);
//            if(line.length > 1){
//                Utils.drawXAxisValue(c, line[1], x + mAxisLabelPaint.getTextSize(), y + mAxisLabelPaint.getTextSize(), mAxisLabelPaint, anchor, angleDegrees);
//            }
        }
    }
}
