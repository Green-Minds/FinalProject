package green_minds.com.finalproject.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.adapters.PinAdapter;
import green_minds.com.finalproject.model.CategoryHelper;
import green_minds.com.finalproject.model.Pin;
import green_minds.com.finalproject.model.RelativePositionPin;

public class CheckInActivity extends AppCompatActivity {

    private ArrayList<RelativePositionPin> mPins;
    private PinAdapter adapter;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastLocation;
    private ParseUser user;
    private Context context;
    private MenuItem miActionProgressItem;

    @BindView(R.id.btn_reload)
    ImageView btnReload;

    @BindView(R.id.rv_pins)
    RecyclerView rvPins;

    @BindView(R.id.btn_checkin)
    Button btnCheckin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);



        //for testing purposes
        user = ParseUser.getCurrentUser();
        if (user == null) {
            redirectToLogin();
        }
        ButterKnife.bind(this);
        btnCheckin.bringToFront();
        btnCheckin.setEnabled(false);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar a = getSupportActionBar();
        // Enable the Up button
        a.setDisplayHomeAsUpEnabled(true);
        a.setTitle("Check in to location");

        //set up adapter
        mPins = new ArrayList<>();
        context = this;
        adapter = new PinAdapter(mPins, context);
        rvPins.setLayoutManager(new LinearLayoutManager(this));
        rvPins.setAdapter(adapter);

        //need a location to start with because requestlocationupdates takes ~3 seconds to start up
        Double lat = getIntent().getDoubleExtra("latitude", 0.0);
        Double lon = getIntent().getDoubleExtra("longitude", 0.0);
        lastLocation = new Location("");
        lastLocation.setLatitude(lat);
        lastLocation.setLongitude(lon);

        locationListener = new MyLocationListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //perform permission check at beginning
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        } else {
            //start getting location in background
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
            getListOfPins();
        }
    }

    @OnClick({R.id.btn_reload})
    public void reloadNearbyPins() {
        showProgressBar();
        getListOfPins();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "please enable permissions.", Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, new MyLocationListener());
        getListOfPins();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "please enable permissions.", Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
    }

    private void getListOfPins() {
        ParseGeoPoint current_location = new ParseGeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());
        ParseQuery<Pin> query = ParseQuery.getQuery(Pin.class);
        query.getQuery(Pin.class).whereWithinMiles("latlng", current_location, 0.125)
                .findInBackground(
                        new FindCallback<Pin>() {
                            @Override
                            public void done(List<Pin> objects, ParseException e) {

                                if (e == null) {
                                    ArrayList<RelativePositionPin> rpList = new ArrayList<>();
                                    for (Pin pin : objects) {
                                        RelativePositionPin rpPin = (convert(pin, lastLocation));
                                        Location pinLoc = getLocationFromPin(pin);
                                        rpPin.setDistanceAway((double) lastLocation.distanceTo(pinLoc));
                                        rpList.add(rpPin);
                                    }
                                    Collections.sort(rpList);
                                    mPins.clear();
                                    mPins.addAll(rpList);
                                    adapter.notifyDataSetChanged();
                                    hideProgressBar();
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });
    }

    @OnClick(R.id.btn_checkin)
    public void checkin() {
        final Pin pin = mPins.get(adapter.mSelectedPos).getPin();
        pin.increment("checkincount");
        pin.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                int numtimes = pin.getCheckincount();

                user.increment("points");
                String cat_key = CategoryHelper.getTypeKey(pin.getCategory());
                user.increment(cat_key);

                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(context, "Checked in!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        });
    }

    private RelativePositionPin convert(Pin pin, Location currLocation) {
        RelativePositionPin rp = new RelativePositionPin(pin);
        Location pinLoc = getLocationFromPin(pin);
        rp.setDistanceAway((double) currLocation.distanceTo(pinLoc));
        return rp;
    }

    private Location getLocationFromPin(Pin pin) {
        ParseGeoPoint gp = pin.getLatLng();
        Location resLoc = new Location("");
        resLoc.setLatitude(gp.getLatitude());
        resLoc.setLongitude(gp.getLongitude());
        return resLoc;
    }

    private void redirectToLogin() {
        Intent i = new Intent(this, green_minds.com.finalproject.activities.UserInfoActivity.class);
        startActivity(i);
    }

    public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            // should i update in the background all the time, or only when needed?
            lastLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    //progress icon setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        return super.onPrepareOptionsMenu(menu);
    }

    private void showProgressBar() {
        if (miActionProgressItem != null) miActionProgressItem.setVisible(true);
    }

    private void hideProgressBar() {
        if (miActionProgressItem != null) miActionProgressItem.setVisible(false);
    }

    public void activateButton(){
        btnCheckin.setEnabled(true);
    }

    //TODO - consider what happens if user navigates away while network call still in progress
}