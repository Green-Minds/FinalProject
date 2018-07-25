package green_minds.com.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.etUsernameLogin) public EditText etUsernameLogin;
    @BindView(R.id.etPasswordLogin) public EditText etPasswordLogin;
    @BindView(R.id.btnLogin) public Button btnLogin;
    @BindView(R.id.tvIncorrectInfo) public TextView tvIncorrectInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (ParseUser.getCurrentUser() != null) {
            startActivity(new Intent(this, MapActivity.class));
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsernameLogin.getText().toString();
                String password = etPasswordLogin.getText().toString();
                btnLogin.setEnabled(false);
                Toast.makeText(getApplicationContext(),"Logging you in", Toast.LENGTH_SHORT).show();
                login(username, password);
            }
        });
    }

    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    tvIncorrectInfo.setVisibility(View.GONE);
                    final Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    etPasswordLogin.startAnimation(invalidCredentials());
                    tvIncorrectInfo.setText(e.getMessage().toString());
                    tvIncorrectInfo.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    public void gotoSignup(View v) {
        final Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    private TranslateAnimation invalidCredentials() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }
}
