package green_minds.com.finalproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;

public class UserInfoActivity extends AppCompatActivity {

    @BindView(R.id.tv_name)
    TextView tv_name;

    @BindView(R.id.tv_pin_c)
    TextView tv_pin_c;

    @BindView(R.id.tv_bottle_c)
    TextView tv_bottle_c;

    @BindView(R.id.tv_water_c)
    TextView tv_water_c;

    @BindView(R.id.tv_bike_c)
    TextView tv_bike_c;

    @BindView(R.id.tv_battery_c)
    TextView tv_battery_c;

    @BindView(R.id.tv_shop_c)
    TextView tv_shop_c;

    @BindView(R.id.tv_score)
    TextView tv_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        ParseUser curr_user = ParseUser.getCurrentUser();
        if(curr_user == null){
            redirectToLogin();
        }
        try {
            tv_name.setText(curr_user.fetchIfNeeded().getUsername());
            tv_pin_c.setText(curr_user.fetchIfNeeded().getInt("pincount") + "");
            tv_bottle_c.setText(curr_user.fetchIfNeeded().getInt("bottlecount") + "");
            tv_water_c.setText(curr_user.fetchIfNeeded().getInt("watercount")+ "");
            tv_bike_c.setText(curr_user.fetchIfNeeded().getInt("bikecount")+ "");
            tv_battery_c.setText(curr_user.fetchIfNeeded().getInt("batterycount")+ "");
            tv_shop_c.setText(curr_user.fetchIfNeeded().getInt("coffeecount")+ "");
            Log.i("USER POINTS",curr_user.getInt("points") + "" );
            tv_score.setText("Score: " + curr_user.fetchIfNeeded().getInt("points"));

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void redirectToLogin(){
        Intent i = new Intent(this, UserInfoActivity.class);
        startActivity(i);
    }
}
