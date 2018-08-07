package green_minds.com.finalproject.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.model.GlideApp;

import static green_minds.com.finalproject.model.ImageHelper.getParseFile;
import static green_minds.com.finalproject.model.ImageHelper.getSmallerParseFile;

public class ThirdSignupActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private Intent intent;
    private String password;
    private String username;
    private String originalUsername;
    private String email;
    private String connection;
    private ParseFile parseFile;
    private ParseFile smallerParseFile;
    private Bitmap imageBitmap;
    private ProgressDialog pd;
    private android.support.v7.app.ActionBar actionBar;
    @BindView(R.id.ivUserPic)
    public ImageView ivUserPic;
    @BindView(R.id.set_container)
    public LinearLayout set_container;
    @BindView(R.id.btnSignup)
    public Button btnSignup;
    @BindView(R.id.tvUser)
    public TextView tvUser;
    @BindView(R.id.thirdSignupToolbar)
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_signup);
        ButterKnife.bind(this);
        intent = getIntent();

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        username = intent.getStringExtra("username");
        originalUsername = intent.getStringExtra("original_username");
        password = intent.getStringExtra("password");
        email = intent.getStringExtra("email");
        connection = intent.getStringExtra("connection");
        tvUser.setText(originalUsername);
        tvUser.setVisibility(View.VISIBLE);
        GlideApp.with(getApplicationContext())
                .load(R.drawable.placeholder)
                .apply(RequestOptions.circleCropTransform())
                .error(R.drawable.placeholder)
                .into(ivUserPic);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                btnSignup.setEnabled(false);
                signUp();
            }
        });

        ivUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                Intent takePhotoIntent = new Intent(ThirdSignupActivity.this, CameraActivity.class)
                        .putExtra("REQUEST_CODE", 3);

                String pickTitle = "Take picture or upload from device"; // Or get from strings.xml
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePhotoIntent });

                startActivityForResult(chooserIntent, SELECT_PICTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                try {
                    set_container.setVisibility(View.GONE);
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                set_container.setVisibility(View.GONE);
                imageBitmap = BitmapFactory.decodeFile(data.getStringExtra("image"));
            }

            GlideApp.with(getApplicationContext())
                .load(imageBitmap)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(ivUserPic);
        }
    }

    private void signUp() {
        if (!isOnline()) return;
        getLocation();
        if (imageBitmap == null) {
            imageBitmap = ((BitmapDrawable) ivUserPic.getDrawable()).getBitmap();
        }

        smallerParseFile = getSmallerParseFile(imageBitmap);
        smallerParseFile.saveInBackground();
        parseFile = getParseFile(imageBitmap);
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                ParseUser user = (ParseUser) ParseUser.create("_User");
                user.setPassword(password);
                user.setUsername(username);
                user.setEmail(email);
                user.put("original_username", originalUsername);
                user.put("connection", connection);
                user.put("location", getLocation());
                user.put("photo", parseFile);
                user.put("smaller_photo", smallerParseFile);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        String message;

                        if (e == null) {
                            hideProgressDialog();
                            alertDisplayer("Signup Successful","Welcome " + originalUsername + "!");
                        } else {
                            e.printStackTrace();
                            if (e.getCode() == 202 || e.getCode() == 203) {
                                hideProgressDialog();
                                intent.putExtra("code", e.getCode());
                                intent.putExtra("activity", ThirdSignupActivity.class.getName());
                                intent.putExtra("username", username);
                                intent.putExtra("password", password);
                                intent.putExtra("email", email);
                                intent.putExtra("connection", connection);
                                intent.putExtra("location", getLocation());
                                intent.putExtra("ParseFile", parseFile);
                                intent.putExtra("SmallerParseFile", smallerParseFile);
                                intent.setClass(ThirdSignupActivity.this, SignupActivity.class);
                                startActivity(intent);
                            }
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

    private void alertDisplayer(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ThirdSignupActivity.this).setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(ThirdSignupActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
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
}
