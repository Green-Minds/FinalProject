package green_minds.com.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.splashscreen) public GifImageView splashscreen;
    @BindView(R.id.btnLoginPage) public Button btnLoginPage;
    @BindView(R.id.btnSignupPage) public Button btnSignupPage;
    @BindView(R.id.appName) public TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        appName.setVisibility(View.VISIBLE);

        if (ParseUser.getCurrentUser() != null) {
            stopLogo(1000);
            scheduleSplashScreen(2000);
        } else {
            btnLoginPage.setVisibility(View.VISIBLE);
            btnSignupPage.setVisibility(View.VISIBLE);
        }
    }

    private void scheduleSplashScreen(int duration) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GifDrawable logo = (GifDrawable)splashscreen.getDrawable();
                logo.stop();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, duration);
    }

    private void stopLogo(int duration) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GifDrawable logo = (GifDrawable)splashscreen.getDrawable();
                logo.stop();
                //appName.setVisibility(View.VISIBLE);
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