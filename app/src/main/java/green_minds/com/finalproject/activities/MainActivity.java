package green_minds.com.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_checkin) public Button btnCheckin;
    @BindView(R.id.btn_signup) public Button btnSignup;
    @BindView(R.id.btn_login) public Button btnLogin;
    @BindView(R.id.btn_map) public Button btnMap;
    @BindView(R.id.btn_newpin) public Button btnNewpin;
    @BindView(R.id.btn_userinfo) public Button btnUserinfo;
    @BindView(R.id.btn_leaderboard) public Button btnLeaderboard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setGoToActivity(btnCheckin, CheckInActivity.class);
        setGoToActivity(btnSignup, SignupActivity.class);
        setGoToActivity(btnLogin, LoginActivity.class);
        setGoToActivity(btnMap, MapActivity.class);
        setGoToActivity(btnNewpin, NewPinActivity.class);
        setGoToActivity(btnUserinfo, UserInfoActivity.class);
        setGoToActivity(btnLeaderboard, LeaderboardActivity.class);

    }

    private void setGoToActivity(Button btn, final Class dest_class){

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, dest_class);
                startActivity(i);
            }
        });
    }
}
