package green_minds.com.finalproject.model;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Goal")
public class Goal extends ParseObject {
    private static final String KEY_DEADLINE = "deadline";
    private static final String KEY_GOAL = "goal";
    private static final String KEY_TYPE = "type";

    public Date getDeadline() {return getDate(KEY_DEADLINE);}

    public void setDeadline(Date date) { put(KEY_DEADLINE, date);}

    public int getGoal() {return getInt(KEY_GOAL);}

    public void setGoal(int goal) { put(KEY_GOAL, goal);}

    public int getType() throws ParseException {return fetchIfNeeded().getInt(KEY_TYPE);}

    public void setType(int type) {put(KEY_TYPE, type);}

    @Override
    public boolean equals(Object obj) {
        Goal g = (Goal)obj;
        try {
            return g.getType() == this.getType();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
