package green_minds.com.finalproject.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.adapters.SchoolAutoCompleteAdapter;
import green_minds.com.finalproject.model.DelayAutoCompleteTextView;

import static green_minds.com.finalproject.model.ImageHelper.getParseFile;

public class LoginActivity extends AppCompatActivity {

    private final List < String > mPermissions = Arrays.asList("email", "public_profile");
    private  Map<String, String> authData = new HashMap<>();
    private SweetAlertDialog pd;
    private static String connection;
    private Intent intent;
    private static Boolean needsConnection = false;
    private android.support.v7.app.ActionBar actionBar;

    @BindView(R.id.etUsernameLogin)
    public EditText etUsernameLogin;
    @BindView(R.id.etPasswordLogin)
    public EditText etPasswordLogin;
    @BindView(R.id.btnLogin)
    public Button btnLogin;
    @BindView(R.id.tvIncorrectInfo)
    public TextView tvIncorrectInfo;
    @BindView(R.id.fbLoginButton)
    public LoginButton fbLoginButton;
    @BindView(R.id.loginToolbar)
    public android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.usernameWrapper)
    public TextInputLayout usernameWrapper;
    @BindView(R.id.passwordWrapper)
    public TextInputLayout passwordWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Intent serviceIntent = new Intent(this, ClosingService.class);
        this.startService(serviceIntent);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        if (intent.getStringExtra("activity") != null
                && intent.getStringExtra("activity").equals(this.getClass().getName())) {

            passwordWrapper.setVisibility(View.GONE);
            usernameWrapper.setVisibility(View.GONE);
            btnLogin.setVisibility(View.GONE);

            if (!isOnline()){
                startActivity(new Intent(this, SplashActivity.class));
                finish();
                return;
            }
            ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, mPermissions, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user == null) {

                    } else if (!user.isNew()) {
                        getUserDetailsFromParse();
                    } else if (user.isNew()) {
                        needsConnection = true;
                        facebookSignupDisplay();
                    }
                }
            });
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordWrapper.setErrorEnabled(false);
                usernameWrapper.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if ((etUsernameLogin.getText().toString().length() > 1) && (etPasswordLogin.getText().toString().length() > 1)) {
                    btnLogin.setEnabled(true);
                } else
                    btnLogin.setEnabled(false);
            }
        };
        etUsernameLogin.addTextChangedListener(textWatcher);
        etPasswordLogin.addTextChangedListener(textWatcher);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(LoginActivity.this);
                if (!isOnline()) return;
                String username = etUsernameLogin.getText().toString().toLowerCase().trim();
                String password = etPasswordLogin.getText().toString();
                login(username, password);
            }
        });
    }

    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() == null) return;
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void login(final String username, final String password) {
        showProgressDialog();
        btnLogin.setEnabled(false);

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    hideProgressDialog();
                    Log.i("Login", "Logged in");
                    alertDisplayer("Successful Login","Welcome back " + user.getString("original_username") + "!");
                } else if (e.getCode() == ParseException.OBJECT_NOT_FOUND){
                    hideProgressDialog();
                    btnLogin.setEnabled(true);
                    usernameWrapper.setError("Incorrect username/password");
                    passwordWrapper.setError("Incorrect username/password");
                    e.printStackTrace();
                    return;
                } else if (e.getCode() == ParseException.CONNECTION_FAILED) {
                    isOnline();
                    return;
                } else
                    return;
            }
        });
    }

    private void alertDisplayer(String title,String message){
        pd = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        pd.setTitleText(title);
        pd.setContentText(message);
        pd.setConfirmText("OK");
        pd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                pd.dismissWithAnimation();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }).show();
    }

    private void facebookSignupDisplay() {
        LayoutInflater layoutInflater = LayoutInflater.from(LoginActivity.this);
        View promptView = layoutInflater.inflate(R.layout.custom_alert, null);
        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this);
        sweetAlertDialog.setCustomView(promptView);

        final DelayAutoCompleteTextView atvSchoolNameAlert = promptView.findViewById(R.id.atvSchoolNameAlert);
        final RadioGroup rgSelectionAlert = promptView.findViewById(R.id.rgSelectionAlert);
        final RadioButton rbWorkAlert = promptView.findViewById(R.id.rbWorkAlert);
        final EditText etCompanyAlert = promptView.findViewById(R.id.etCompanyAlert);
        final String[] school = {null};

        atvSchoolNameAlert.setThreshold(2);
        atvSchoolNameAlert.setAdapter(new SchoolAutoCompleteAdapter(this));
        atvSchoolNameAlert.setLoadingIndicator(
                (android.widget.ProgressBar) promptView.findViewById(R.id.pb_loading_indicator_alert));
        atvSchoolNameAlert.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                school[0] = (String) parent.getItemAtPosition(position);
                atvSchoolNameAlert.setText(school[0]);
            }
        });

        rgSelectionAlert.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (rbWorkAlert.isChecked()) {
                    atvSchoolNameAlert.setVisibility(View.GONE);
                    etCompanyAlert.setVisibility(View.VISIBLE);
                } else {
                    atvSchoolNameAlert.setVisibility(View.VISIBLE);
                    etCompanyAlert.setVisibility(View.GONE);
                }
            }
        });

        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                connection = atvSchoolNameAlert.getText().toString();

                if (school[0]== null || !connection.equals(school[0])) {
                    ParseUser.getCurrentUser().deleteInBackground();
                    ParseUser.logOut();
                    startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                    finish();
                    return;
                } else {
                    sweetAlertDialog.dismiss();
                    showProgressDialog();
                    getUserDetailFromFB();
                }
            }
        });
        sweetAlertDialog.create();
        sweetAlertDialog.show();
        sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setEnabled(false);

        atvSchoolNameAlert.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (school[0] != null && school[0].equals(atvSchoolNameAlert.getText().toString())) {
                    sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setEnabled(true);
                } else
                    sweetAlertDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setEnabled(false);
            }
        });
    }

    private void getUserDetailFromFB() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            JSONObject picture = response.getJSONObject().getJSONObject("picture");
                            JSONObject data = picture.getJSONObject("data");

                            String pictureUrl = data.getString("url");
                            new LoginActivity.ProfileAsync(pictureUrl,
                                    object.getString("email"),
                                    object.getString("name")).execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name, email, picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void getUserDetailsFromParse() {
        showProgressDialog();
        ParseUser user = ParseUser.getCurrentUser();
        authData.put("token", user.getSessionToken());
        authData.put("id", user.getObjectId());
        user.put("location", getLocation());
        Task<ParseUser> parseUserTask = ParseUser.logInWithInBackground("facebook", authData);
        try {
            parseUserTask.waitForCompletion();
            hideProgressDialog();
            alertDisplayer("Successful Login","Welcome back " + user.getString("original_username") + "!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ParseGeoPoint getLocation() {

        ParseGeoPoint point = null;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                point = new ParseGeoPoint(latitude, longitude);
            } else {
                point = new ParseGeoPoint();
            }
        }
        return point;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        finishActivity(requestCode);
    }

    private class ProfileAsync extends AsyncTask<String, String, String> {
        public Bitmap bitmap;
        String url, email, username;

        public ProfileAsync(String url, String email, String username) {
            this.url = url;
            this.email = email;
            this.username = username;
        }

        @Override
        protected String doInBackground(String... params) {
            // Fetching data from URI and storing in bitmap
            bitmap = downloadImageBitmap(url);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            saveNewUser(bitmap, email, username);
        }
    }

    private static Bitmap downloadImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("IMAGE", "Error getting bitmap", e);
        }
        return bm;
    }

    private void saveNewUser(Bitmap image, String email, final String username) {

        final ParseUser user = ParseUser.getCurrentUser();
        final ParseFile parseFile = getParseFile(image);
        user.setUsername(username.toLowerCase());
        user.put("original_username", username);
        user.setEmail(email);
        user.put("location", getLocation());
        user.put("connection", connection);

        final ParseFile smallerParseFile = getParseFile(image);
        smallerParseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                user.put("photo", parseFile);
                user.put("smaller_photo", smallerParseFile);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            authData.put("token", user.getSessionToken());
                            authData.put("id", user.getObjectId());
                            Task<ParseUser> parseUserTask = ParseUser.logInWithInBackground("facebook", authData);
                            try {
                                parseUserTask.waitForCompletion();
                                hideProgressDialog();
                                alertDisplayer("Successful Login","Welcome " + user.getString("original_username") + "!");
                            } catch (InterruptedException err) {
                                err.printStackTrace();
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public static class ClosingService extends Service {

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onTaskRemoved(Intent rootIntent) {
            super.onTaskRemoved(rootIntent);

            if ((connection == null) && (needsConnection == true) && (ParseUser.getCurrentUser() != null)) {
                ParseUser.getCurrentUser().deleteInBackground();
                LoginManager.getInstance().logOut();
                ParseUser.logOutInBackground();
                Log.i("Service", "Working");
            }

            // Destroy the service
            stopSelf();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ((connection == null) && (needsConnection == true) && (ParseUser.getCurrentUser() != null)) {
            ParseUser.getCurrentUser().deleteInBackground();
            LoginManager.getInstance().logOut();
            ParseUser.logOutInBackground();
            Log.i("Destroy", "Working");
        }
    }

    private void showProgressDialog() {
        // Show progress item
        pd = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pd.getProgressHelper().setBarColor(R.color.colorPrimary);
        pd.setTitleText("Logging you in...");
        pd.setContentText("Please wait.");
        pd.setCancelable(false);
        pd.show();
    }

    private void hideProgressDialog() {
        // Hide progress item
        pd.dismiss();
    }
}
