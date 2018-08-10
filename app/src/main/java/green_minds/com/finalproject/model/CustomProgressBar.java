package green_minds.com.finalproject.model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class CustomProgressBar extends ProgressBar {

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGoal(Goal goal) {
        int goalNum = goal.getGoal();
        int checkins = goal.getPoints();
        this.setMax(goalNum);
        this.setProgress(checkins);
    }
}
