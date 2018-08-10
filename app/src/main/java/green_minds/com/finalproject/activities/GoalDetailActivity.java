package green_minds.com.finalproject.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.model.CustomProgressBar;
import green_minds.com.finalproject.model.Goal;

public class GoalDetailActivity extends AppCompatActivity {

    @BindView(R.id.progress)
    CustomProgressBar progressBar;

    @BindView(R.id.tv_information)
    TextView tvInformation;

    @BindView(R.id.tv_estimate)
    TextView tvEstimate;

    @BindView(R.id.tvCreatedAt)
    TextView tvCreatedAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_detail);
        Goal g = getIntent().getParcelableExtra("GOAL");
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar a = getSupportActionBar();
        // Enable the Up button
        a.setDisplayHomeAsUpEnabled(true);
        a.setTitle("Viewing Goal Details");

        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");

        progressBar.setGoal(g);
        int checkins = g.getPoints();
        int goal = g.getGoal();
        Date date = g.getDeadline();
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        double daysBetween = daysBetween(today, date) + 1; // add the last day
        double daily = (goal - checkins) / (daysBetween);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        String estimate = "";
        if(daily <= 1){
            estimate = getString(R.string.goal_details_estimate_one, format.format(date));
        } else{
            estimate = getString(R.string.goal_details_estimate, df.format(daily) , format.format(date));
        }
        String info;
        if(checkins == 1){
            info = getString(R.string.goal_details_one, checkins, goal);
        } else {
            info = getString(R.string.goal_details, checkins, goal);
        }

        if((goal - checkins) <=0) estimate = getString(R.string.goal_congrats);
        else if(daysBetween < 0) estimate = getString(R.string.goal_fail);
        tvInformation.setText(info);
        tvEstimate.setText(estimate);

        String updatedAt = format.format(g.getUpdatedAt());
        tvCreatedAt.setText("Goal last edited on " + updatedAt + ".");
    }

    public double daysBetween(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
}
