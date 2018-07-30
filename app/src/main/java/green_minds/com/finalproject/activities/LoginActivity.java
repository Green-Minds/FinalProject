package green_minds.com.finalproject.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import java.io.ByteArrayOutputStream;
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
import green_minds.com.finalproject.R;

public class LoginActivity extends AppCompatActivity {

    private final List < String > mPermissions = Arrays.asList("email", "public_profile");
    @BindView(R.id.etUsernameLogin) public EditText etUsernameLogin;
    @BindView(R.id.etPasswordLogin) public EditText etPasswordLogin;
    @BindView(R.id.btnLogin) public Button btnLogin;
    @BindView(R.id.tvIncorrectInfo) public TextView tvIncorrectInfo;
    @BindView(R.id.fbLoginButton) public LoginButton fbLoginButton;
    private  Map<String, String> authData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (ParseUser.getCurrentUser() != null) {
            alertDisplayer("Successful Login ","Welcome back " + ParseUser.getCurrentUser().getUsername() + "!");
        } else if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logInWithReadPermissions(this, mPermissions);
            alertDisplayer("Successful Login","Welcome back " + ParseUser.getCurrentUser().getUsername() + "!");
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvIncorrectInfo.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if ((etUsernameLogin.getText().toString() != null) && (etPasswordLogin.getText().toString() != null)){
                    btnLogin.setEnabled(true);
                }
            }
        };
        etUsernameLogin.addTextChangedListener(textWatcher);
        etPasswordLogin.addTextChangedListener(textWatcher);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard(LoginActivity.this);
                if (!isOnline()) return;
                String username = etUsernameLogin.getText().toString();
                String password = etPasswordLogin.getText().toString();
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

                        } else if (!user.isNew()) {
                            btnLogin.setEnabled(false);
                            fbLoginButton.setEnabled(false);
                            getUserDetailsFromParse();
                        } else if (user.isNew()) {
                            btnLogin.setEnabled(false);
                            fbLoginButton.setEnabled(false);
                            getUserDetailFromFB();
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

    private void login(final String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    tvIncorrectInfo.setVisibility(View.GONE);
                    btnLogin.setEnabled(false);
                    fbLoginButton.setEnabled(false);
                    alertDisplayer("Successful Login","Welcome back " + username + "!");
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

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(LoginActivity.this, LeaderboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private TranslateAnimation invalidCredentials() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(7));
        return shake;
    }

    private void getUserDetailsFromParse() {
        ParseUser user = ParseUser.getCurrentUser();
        Map<String, String> authData = new HashMap<>();
        authData.put("token", user.getSessionToken());
        user.put("location", getLocation());
        user.saveInBackground();
        Task<ParseUser> parseUserTask = ParseUser.logInWithInBackground("facebook", authData);
        try {
            parseUserTask.waitForCompletion();
            alertDisplayer("Successful Login","Welcome back " + user.getUsername() + "!");
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
            }
        }
        return point;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        finishActivity(requestCode);
    }

    public void gotoSignup(View v) {
        v.setEnabled(false);
        final Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
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
            bitmap = DownloadImageBitmap(url);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            saveNewUser(bitmap, email, username);
        }
    }

    private static Bitmap DownloadImageBitmap(String url) {
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
        user.setUsername(username);
        user.setEmail(email);
        user.put("location", getLocation());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (image != null) {
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();
            String name = user.getUsername().replaceAll("\\s+", "");
            final ParseFile parseFile = new ParseFile(name + "prof_pic.jpg", data);
            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    user.put("photo", parseFile);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                authData.put("token", user.getSessionToken());
                                Task<ParseUser> parseUserTask = ParseUser.logInWithInBackground("facebook", authData);
                                try {
                                    parseUserTask.waitForCompletion();
                                    alertDisplayer("Successful Login ","Welcome  " + username + "!");
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
    }
}
