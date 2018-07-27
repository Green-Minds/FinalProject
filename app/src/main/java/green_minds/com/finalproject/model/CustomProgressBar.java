package green_minds.com.finalproject.model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.parse.ParseUser;

public class CustomProgressBar extends ProgressBar {

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGoal(Goal goal, ParseUser user) {

        String serverKey = CategoryHelper.getTypeKey(goal.getType());
        int goalNum = goal.getGoal();
        int checkins = user.getInt(serverKey);
        this.setMax(goalNum);
        this.setProgress(checkins);
    }
}
