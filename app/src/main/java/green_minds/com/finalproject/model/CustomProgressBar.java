package green_minds.com.finalproject.model;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.parse.ParseUser;

public class CustomProgressBar extends ProgressBar{
//TODO - take in a goal in constructor instead
    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGoal(Goal goal){

        ParseUser user = ParseUser.getCurrentUser();
        String serverKey = PinCategoryHelper.getTypeKey(goal.getType());
        int goalNum = goal.getGoal();
        int checkins = user.getInt(serverKey);
        this.setMax(goalNum);
        this.setProgress(checkins);
    }
}
