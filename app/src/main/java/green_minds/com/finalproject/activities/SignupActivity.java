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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.adapters.SchoolAutoCompleteAdapter;
import green_minds.com.finalproject.model.DelayAutoCompleteTextView;


public class SignupActivity extends AppCompatActivity {

    public static final List<String> mPermissions = new ArrayList<String>() {{
        add("email");
    }};
    @BindView(R.id.etUsernameInput)
    public EditText etUsernameInput;
    @BindView(R.id.etPasswordInput)
    public EditText etPasswordInput;
    @BindView(R.id.etEmailInput)
    public EditText etEmailInput;
    @BindView(R.id.btnSignup)
    public Button btnSignup;
    @BindView(R.id.rgSelection)
    public RadioGroup rgSelection;
    @BindView(R.id.rbWork)
    public RadioButton rbWork;
    @BindView(R.id.etCompany)
    public EditText etCompany;
    @BindView(R.id.tvUsernameTaken)
    public TextView tvUsernameTaken;
    @BindView(R.id.atvSchoolName)
    public DelayAutoCompleteTextView atvSchoolName;
    @BindView(R.id.fbSignupButton)
    public LoginButton fbSignupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        if (ParseUser.getCurrentUser() != null) {
            ParseUser.logOut();
        }

        atvSchoolName.setThreshold(2);
        atvSchoolName.setAdapter(new SchoolAutoCompleteAdapter(this));
        atvSchoolName.setLoadingIndicator(
                (android.widget.ProgressBar) findViewById(R.id.pb_loading_indicator));
        atvSchoolName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                String school = (String) parent.getItemAtPosition(position);
                atvSchoolName.setText(school);
            }
        });


        rgSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (rbWork.isChecked()) {
                    atvSchoolName.setVisibility(View.GONE);
                    etCompany.setVisibility(View.VISIBLE);
                } else {
                    atvSchoolName.setVisibility(View.VISIBLE);
                    etCompany.setVisibility(View.GONE);
                }
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ParseQuery query = ParseUser.getQuery();
                query.whereEqualTo("username", etUsernameInput.getText().toString()).findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() == 0) {

                                tvUsernameTaken.setVisibility(View.GONE);
                                String username = etUsernameInput.getText().toString();
                                String password = etPasswordInput.getText().toString();
                                String connection = atvSchoolName.getText().toString();
                                String email = etEmailInput.getText().toString();
                                if (rbWork.isChecked()) connection = etCompany.getText().toString();
                                btnSignup.setEnabled(false);
                                Toast.makeText(getApplicationContext(), "Creating user", Toast.LENGTH_SHORT).show();
                                signUp(username, password, connection, email);

                            } else {
                                tvUsernameTaken.setText("Username already exists.");
                                tvUsernameTaken.setVisibility(View.VISIBLE);
                                return;
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        fbSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(SignupActivity.this, mPermissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                            getUserDetailFromFB();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    void getUserDetailFromFB() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    etUsernameInput.setText(object.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    etEmailInput.setText(object.getString("email"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                saveNewUser();
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    void saveNewUser() {

        final ParseUser user = ParseUser.getCurrentUser();
        final Map<String, String> authData = new HashMap<>();
        user.setUsername(etUsernameInput.getText().toString());
        user.setEmail(etEmailInput.getText().toString());
        user.setPassword(etPasswordInput.getText().toString());
        user.put("connection", atvSchoolName.getText().toString());
        user.put("location", getLocation());

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    authData.put("token", user.getSessionToken());
                    Task<ParseUser> parseUserTask = ParseUser.logInWithInBackground("facebook", authData);

                    try {
                        parseUserTask.waitForCompletion();
                        final Intent intent = new Intent(SignupActivity.this, MapActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (InterruptedException err) {
                        err.printStackTrace();
                    }
                }
            }
        });
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


    private void signUp(String username, String password, String connection, String email) {
        ParseUser user = (ParseUser) ParseUser.create("_User");
        user.setPassword(password);
        user.setUsername(username);
        user.setEmail(email);
        user.put("connection", connection);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Welcome " + etUsernameInput.getText().toString(), Toast.LENGTH_SHORT).show();
                    final Intent intent = new Intent(SignupActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
