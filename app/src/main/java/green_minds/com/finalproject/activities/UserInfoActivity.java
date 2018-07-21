package green_minds.com.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import green_minds.com.finalproject.R;

public class UserInfoActivity extends AppCompatActivity {

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_pin_c)
    TextView tvPin;

    @BindView(R.id.tv_bottle_c)
    TextView tvBottle;

    @BindView(R.id.tv_water_c)
    TextView tvWater;

    @BindView(R.id.tv_bike_c)
    TextView tvBike;

    @BindView(R.id.tv_battery_c)
    TextView tvBattery;

    @BindView(R.id.tv_shop_c)
    TextView tvShop;

    @BindView(R.id.tv_score)
    TextView tvScore;

    @BindView(R.id.btn_edit)
    TextView btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        ParseUser curr_user = ParseUser.getCurrentUser();
        if(curr_user == null){
            redirectToLogin();
        }
        else {
            try {
                tvName.setText(curr_user.fetchIfNeeded().getUsername());
                tvPin.setText(curr_user.fetchIfNeeded().getInt("pincount") + "");
                tvBottle.setText(curr_user.fetchIfNeeded().getInt("bottlecount") + "");
                tvWater.setText(curr_user.fetchIfNeeded().getInt("watercount")+ "");
                tvBike.setText(curr_user.fetchIfNeeded().getInt("bikecount")+ "");
                tvBattery.setText(curr_user.fetchIfNeeded().getInt("batterycount")+ "");
                tvShop.setText(curr_user.fetchIfNeeded().getInt("coffeecount")+ "");
                tvScore.setText("Score: " + curr_user.fetchIfNeeded().getInt("points"));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.btn_edit)
    public void finish(){
        Intent i = new Intent(UserInfoActivity.this, EditProfileActivity.class);
        startActivityForResult(i, 30);
    }

    private void redirectToLogin(){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
