package green_minds.com.finalproject.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

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
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.adapters.GoalAdapter;
import green_minds.com.finalproject.model.CategoryHelper;
import green_minds.com.finalproject.model.GlideApp;
import green_minds.com.finalproject.model.Goal;

public class UserInfoFragment extends Fragment {

    public interface OnUserInfoListener {

        void goToEdit();

        void openGoalEditPage(int pos, ArrayList<Goal> goals);

        void goToDetail(Goal g, int c);

        void showProgressBar();

        void hideProgressBar();

        void logout();

        void showNoDataMessage();
    }

    private static final String ARG_PARAM1 = "user";

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

    @BindView(R.id.goal_list)
    ListView goalList;

    @BindView(R.id.btn_add_goal)
    ImageButton btnAddGoal;

    @BindView(R.id.tv_nodata)
    TextView tvNodata;

    private ParseUser mUser;
    private ArrayList<Goal> mGoals;
    private Context mContext;
    private GoalAdapter mGoalAdapter;

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
        PopupMenu popup = new PopupMenu(getContext(), btnPopup);
        popup.getMenuInflater()
                .inflate(R.menu.popup_user, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                //TODO - add to strings.xml
                String[] ids = getResources().getResourceName(item.getItemId()).split("\\/");
                String id = ids[1];
                if (id.equals("mi_logout")) {
                    mListener.logout();
                } else if (id.equals("mi_edit_prof")) {
                    mListener.goToEdit();
                } else {
                    Log.e("USER INFO", "popup menu error");
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
        setUpProfile();
        setUpGraph();
        setUpGoals();

    }

    private void setUpProfile() {
        //set username
        Object username = mUser.get("original_username"); //check if exists first
        if (username != null) {
            tvName.setText((String) username);
        } else {
            tvName.setText(mUser.getUsername());
        }
        //set user pic
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
        //set score
        tvScore.setText(mUser.getInt("points") + "");
        //set connection
        tvConnection.setText(mUser.getString("connection"));
    }

    private void setUpGraph() {

        //set settings for axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (int) value + "";
            }
        });
        YAxis leftAxis = chart.getAxisLeft();
        YAxis rightAxis = chart.getAxisRight();
        leftAxis.setEnabled(false);
        rightAxis.setEnabled(false);

        //add data
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            int checkins = mUser.getInt(CategoryHelper.getTypeKey(i));
            entries.add(new BarEntry(i, checkins));
        }

        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        set.setColor(getResources().getColor(R.color.colorTeal));

        BarData data = new BarData(set);
        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return ((int) value) + "";
            }
        });
        data.setValueTextSize(14);
        data.setBarWidth(0.8f);

        chart.setData(data);

        //chart settings
        chart.getLegend().setEnabled(false);
        chart.setDrawBorders(false);
        chart.setXAxisRenderer(new CustomXAxisRenderer(chart.getViewPortHandler(), chart.getXAxis(), chart.getTransformer(YAxis.AxisDependency.LEFT)));
        chart.setDrawBorders(false);
        chart.setExtraBottomOffset(18);
        chart.setDescription(null);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setPinchZoom(false);
        chart.setFitBars(true);
        chart.invalidate(); // refresh
    }

    public void setGoals(List<Goal> g) {
        mGoals.clear();
        mGoals.addAll(g);
        mGoalAdapter.notifyDataSetChanged();
        if (g.size() <= 0) {
            tvNodata.setVisibility(View.VISIBLE);
        } else {
            tvNodata.setVisibility(View.GONE);
        }
    }

    public void refreshUserData() {
        mUser = ParseUser.getCurrentUser();
        Object username = mUser.get("original_username"); //check if exists first
        if (username != null) {
            tvName.setText((String) username);
        } else {
            tvName.setText(mUser.getUsername());
        }
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

    private void setUpGoals() {
        mGoalAdapter = new GoalAdapter(mContext, mUser, mGoals, mListener);
        goalList.setAdapter(mGoalAdapter);

        btnAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.openGoalEditPage(-1, mGoals);
            }
        });
        if (mGoals.size() <= 0) {
            tvNodata.setVisibility(View.VISIBLE);
        } else {
            tvNodata.setVisibility(View.GONE);
        }
    }

    private class CustomXAxisRenderer extends XAxisRenderer {
        public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
            super(viewPortHandler, xAxis, trans);
        }

        @Override
        protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
            int i = Integer.parseInt(formattedLabel);
            Utils.drawImage(c, CategoryHelper.getIconResource(i, mContext), (int) x, (int) y + 24, 64, 64);
        }
    }

    public void showNoDataMessage() {
        tvNodata.setVisibility(View.VISIBLE);
    }

}
