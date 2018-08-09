package green_minds.com.finalproject.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;


public class SignupActivity extends AppCompatActivity {

    private ProgressDialog pd;
    private Intent intent;
    private android.support.v7.app.ActionBar actionBar;

    @BindView(R.id.etUsernameInput)
    public EditText etUsernameInput;
    @BindView(R.id.etPasswordInput)
    public EditText etPasswordInput;
    @BindView(R.id.etEmailInput)
    public EditText etEmailInput;
    @BindView(R.id.btnInfoNext)
    public Button btnInfoNext;
    @BindView(R.id.tvUsernameTaken)
    public TextView tvUsernameTaken;
    @BindView(R.id.signupToolbar)
    public Toolbar toolbar;
    @BindView(R.id.usernameInputWrapper)
    public TextInputLayout usernameInputWrapper;
    @BindView(R.id.passwordInputWrapper)
    public TextInputLayout passwordInputWrapper;
    @BindView(R.id.emailInputWrapper)
    public TextInputLayout emailInputWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        intent = getIntent();

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (intent.getStringExtra("activity") != null
                && intent.getStringExtra("activity").equals(ThirdSignupActivity.class.getName())) {
            int code = intent.getIntExtra("code", 0);
            btnInfoNext.setText("Complete Sign Up");

            if (code == ParseException.USERNAME_TAKEN) {
                etEmailInput.setText(intent.getStringExtra("email").toString());
                usernameInputWrapper.setError("Username taken");
            } else if (code == ParseException.EMAIL_TAKEN) {
                etUsernameInput.setText(intent.getStringExtra("username"));
                emailInputWrapper.setError("Email address taken");
            }
            tvUsernameTaken.setVisibility(View.VISIBLE);
            etPasswordInput.setText(intent.getStringExtra("password"));
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usernameInputWrapper.setErrorEnabled(false);
                emailInputWrapper.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etUsernameInput.getText().toString().length() > 1
                        && etPasswordInput.getText().toString().length() > 1
                        && etEmailInput.getText().toString().length() > 1) {

                    btnInfoNext.setEnabled(true);
                } else
                    btnInfoNext.setEnabled(false);
            }
        };
        etUsernameInput.addTextChangedListener(textWatcher);
        etPasswordInput.addTextChangedListener(textWatcher);
        etEmailInput.addTextChangedListener(textWatcher);

        btnInfoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseQuery usernameQuery = ParseUser.getQuery();
                ParseQuery emailQuery = ParseUser.getQuery();
                List<ParseQuery<ParseUser>> queries = new ArrayList<>();

                hideSoftKeyboard(SignupActivity.this);
                if (!isOnline()) return;
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmailInput.getText().toString()).matches()) {
                    emailInputWrapper.setError("Invalid email address");
                    return;
                }

                btnInfoNext.setEnabled(false);
                usernameQuery.whereEqualTo("username", etUsernameInput.getText().toString().toLowerCase().trim());
                emailQuery.whereEqualTo("email", etEmailInput.getText().toString());
                queries.add(usernameQuery);
                queries.add(emailQuery);
                ParseQuery<ParseUser> mainQuery = ParseQuery.or(queries);

                mainQuery.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() == 0) {
                                showProgressDialog();
                                tvUsernameTaken.setVisibility(View.GONE);
                                if (intent.getStringExtra("activity") != null &&
                                        intent.getStringExtra("activity").equals(ThirdSignupActivity.class.getName())) {
                                    completeSignup();
                                    return;
                                }
                                gotoSecondScreen();
                            } else {
                                btnInfoNext.setEnabled(true);
                                usernameInputWrapper.setError("Username/Email address taken");
                                emailInputWrapper.setError("Username/Email address taken");
                                return;
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void gotoSecondScreen() {
        intent = new Intent(SignupActivity.this, SecondSignupActivity.class);
        intent.putExtra("username", etUsernameInput.getText().toString().toLowerCase().trim());
        intent.putExtra("password", etPasswordInput.getText().toString());
        intent.putExtra("email", etEmailInput.getText().toString());
        intent.putExtra("original_username", etUsernameInput.getText().toString().trim());
        hideProgressDialog();
        startActivity(intent);
    }

    private void showProgressDialog() {
        // Show progress item
        pd = new ProgressDialog(this);
        pd.setTitle("Processing...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
    }

    private void hideProgressDialog() {
        // Hide progress item
        pd.dismiss();
    }

    private void completeSignup() {
        ParseUser user = (ParseUser) ParseUser.create("_User");

        user.setPassword(intent.getStringExtra("password"));
        user.setUsername(etUsernameInput.getText().toString().toLowerCase().trim());
        user.setEmail(etEmailInput.getText().toString());
        user.put("original_username", etUsernameInput.getText().toString().trim());
        user.put("connection", intent.getStringExtra("connection"));
        user.put("location", intent.getParcelableExtra("location"));
        user.put("photo", intent.getParcelableExtra("ParseFile"));
        user.put("smaller_photo", intent.getParcelableExtra("SmallerParseFile"));

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {
                    hideProgressDialog();
                    alertDisplayer("Signup Successful","Welcome " + etUsernameInput.getText().toString().trim() + "!");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public ParseGeoPoint getLocation() {

        ParseGeoPoint point = null;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                point = new ParseGeoPoint(latitude, longitude);
            }
        }
        return point;
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this).setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}


