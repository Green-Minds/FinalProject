package green_minds.com.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.model.GlideApp;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.splashscreen) public ImageView splashscreen;
    @BindView(R.id.btnLoginPage) public Button btnLoginPage;
    @BindView(R.id.btnSignupPage) public Button btnSignupPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        GlideApp.with(this).load(R.drawable.spheremedium).into(splashscreen);

        if (ParseUser.getCurrentUser() != null) {
            scheduleSplashScreen(1000);
        } else {
            btnLoginPage.setVisibility(View.VISIBLE);
            btnSignupPage.setVisibility(View.VISIBLE);
        }
    }

    private void scheduleSplashScreen(int duration) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LeaderboardActivity.class));
                finish();
            }
        }, duration);
    }

    public void gotoSignup(View v) {
        v.setEnabled(false);
        startActivity(new Intent(this, SignupActivity.class));
    }

    public void gotoLogin(View v) {
        v.setEnabled(false);
        startActivity(new Intent(this, LoginActivity.class));
    }
}
