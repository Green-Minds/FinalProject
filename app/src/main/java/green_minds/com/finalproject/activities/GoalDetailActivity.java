package green_minds.com.finalproject.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_detail);
        Goal g = getIntent().getParcelableExtra("GOAL");
        ButterKnife.bind(this);

        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, YY");

        progressBar.setGoal(g);
        int checkins = getIntent().getIntExtra("CHECKINS", 0);
        int goal = g.getGoal();
        Date date = g.getDeadline();
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        double daysBetween = daysBetween(today, date); // add the last day
        double daily = (goal - checkins) / (daysBetween);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        //TODO - move hardcoded strings somewhere else
        String info = "You have checked in " + checkins + " times out of your goal of " + goal;
        String estimate = "You will need to check in roughly " + df.format(daily) + " times per day to reach your goal by your deadline of " + format.format(date);
        tvInformation.setText(info);
        tvEstimate.setText(estimate);
    }

    //doesn't account for daylist savings, will make more precise later
    private double daysBetween(Date d1, Date d2){
        return ( (d2.getTime() - d1.getTime()) / (1000.0 * 60 * 60 * 24));
    }
}
