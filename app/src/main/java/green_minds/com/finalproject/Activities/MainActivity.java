package green_minds.com.finalproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_checkin) public Button btn_checkin;
    @BindView(R.id.tv_signup) public Button btn_signup;
    @BindView(R.id.tv_login) public Button btn_login;
    @BindView(R.id.tv_map) public Button btn_map;
    @BindView(R.id.tv_newpin) public Button btn_newpin;
    @BindView(R.id.tv_userinfo) public Button btn_userinfo;
    @BindView(R.id.tv_leaderboard) public Button btn_leaderboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setGoToActivity(btn_checkin, CheckInActivity.class);
        setGoToActivity(btn_signup, SignupActivity.class);
        setGoToActivity(btn_login, LoginActivity.class);
        setGoToActivity(btn_map, MapActivity.class);
        setGoToActivity(btn_newpin, NewPinActivity.class);
        setGoToActivity(btn_userinfo, UserInfoActivity.class);
        setGoToActivity(btn_leaderboard, LeaderboardActivity.class);

    }

    private void setGoToActivity(Button btn, final Class dest_class){

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, dest_class);
                startActivity(i);
                finish();
            }
        });
    }
}
