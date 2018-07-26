package green_minds.com.finalproject.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;

public class LoginActivity extends AppCompatActivity {

    public static final List<String> mPermissions = new ArrayList<String>() {{ add("email"); }};
    @BindView(R.id.etUsernameLogin) public EditText etUsernameLogin;
    @BindView(R.id.etPasswordLogin) public EditText etPasswordLogin;
    @BindView(R.id.btnLogin) public Button btnLogin;
    @BindView(R.id.tvIncorrectInfo) public TextView tvIncorrectInfo;
    @BindView(R.id.fbLoginButton) public LoginButton fbLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (ParseUser.getCurrentUser() != null) {
            ParseUser.logOut();
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

        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, mPermissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");

                        } else if (!user.isNew()) {
                            Log.d("MyApp", "User logged in through Facebook!");
                            getUserDetailsFromParse();
                        }
                    }
                });
            }
        });
    }

    private void getUserDetailsFromParse() {
        ParseUser user = ParseUser.getCurrentUser();
        Map<String, String> authData = new HashMap<>();
        authData.put("token", user.getSessionToken());
        user.put("location", getLocation());
        user.saveInBackground();

        etUsernameLogin.setText(user.getUsername());
        Task<ParseUser> parseUserTask = ParseUser.logInWithInBackground("facebook", authData);
        try {
            parseUserTask.waitForCompletion();
            final Intent intent = new Intent(LoginActivity.this, MapActivity.class);
            startActivity(intent);
            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ParseGeoPoint getLocation() {

        ParseGeoPoint point = null;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                point = new ParseGeoPoint(latitude, longitude);
            }
        }
        return point;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
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
