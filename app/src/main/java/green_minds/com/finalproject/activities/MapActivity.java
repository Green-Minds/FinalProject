package green_minds.com.finalproject.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.ClusterManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import green_minds.com.finalproject.model.MyItem;
import green_minds.com.finalproject.model.Pin;
import green_minds.com.finalproject.R;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions
public class MapActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener {
    @BindView(R.id.newPinBtn) public Button newPinBtn;
    @BindView(R.id.checkinBtn) public Button checkinBtn;
    @BindView(R.id.fab) public FloatingActionButton fab;
    @BindView(R.id.fab0) public FloatingActionButton fab0;
    @BindView(R.id.fab1) public FloatingActionButton fab1;
    @BindView(R.id.fab2) public FloatingActionButton fab2;
    @BindView(R.id.fab3) public FloatingActionButton fab3;
    @BindView(R.id.fab4) public FloatingActionButton fab4;

    private final int REQUEST_CODE = 20;
    final public static String PIN_KEY = "pin";
    private Pin.Query pinQuery;
    ArrayList<Pin> pins;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private double lat = 0;
    private double lon = 0;
    private int type = 0;
    private boolean isFABOpen = false;
    ParseUser user;

    private final static String KEY_LOCATION = "location";

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ButterKnife.bind(this);

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready

            UiSettings mapUiSettings = map.getUiSettings();
            mapUiSettings.setZoomControlsEnabled(true);

            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            MapActivityPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
            MapActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);



            newPinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MapActivity.this, NewPinActivity.class);
                    Log.d("MapActivity", "Pin at " + mCurrentLocation.getLatitude());
                    intent.putExtra("latitude", mCurrentLocation.getLatitude());
                    intent.putExtra("longitude", mCurrentLocation.getLongitude());
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });

            checkinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MapActivity.this, CheckInActivity.class);
                    Log.d("MapActivity", "Pin at " + mCurrentLocation.getLatitude());
                    intent.putExtra("latitude", mCurrentLocation.getLatitude());
                    intent.putExtra("longitude", mCurrentLocation.getLongitude());
                    startActivity(intent);
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isFABOpen){
                        showFABMenu();
                    }else{
                        closeFABMenu();
                    }
                }

                private void closeFABMenu() {
                    isFABOpen=false;
                    fab0.animate().translationY(0);
                    fab1.animate().translationY(0);
                    fab2.animate().translationY(0);
                    fab3.animate().translationY(0);
                    fab4.animate().translationY(0);
                }

                private void showFABMenu() {
                    isFABOpen=true;
                    fab0.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
                    fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
                    fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
                    fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_205));
                    fab4.animate().translationY(-getResources().getDimension(R.dimen.standard_255));


                }
            });

            getMyLocation();
            if (mCurrentLocation != null) {
                Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
                LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                map.animateCamera(cameraUpdate);
            }

            pinQuery = new Pin.Query();
            pins = new ArrayList<>();
            pinQuery.getTop();
            pinQuery.findInBackground(new FindCallback<Pin>() {
                @Override
                public void done(List<Pin> objects, ParseException e) {
                    if (e==null){
                        for (int i = objects.size()-1; i >= 0; i--) {

                            if (objects.get(i).has("latlng"))  {
                                lat = objects.get(i).getLatLng().getLatitude();
                                lon = objects.get(i).getLatLng().getLongitude();
                                type = objects.get(i).getCategory();

                                Pin pin = objects.get(i);
                                pins.add(pin);
                                int drawableId = getIcon(type);
                                BitmapDescriptor customMarker =
                                        BitmapDescriptorFactory.fromResource(drawableId);

                                LatLng listingPosition = new LatLng(lat, lon);
                                // Create the marker on the fragment
                                Marker mapMarker = map.addMarker(new MarkerOptions()
                                        .position(listingPosition)
                                        .title("checkins: " + objects.get(i).getCheckincount())
                                        .snippet(objects.get(i).getComment())
                                        .icon(customMarker));
                            }

                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });



        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressWarnings({"MissingPermission"})
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);

        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }


    // Called when the Activity becomes visible.

    @Override
    protected void onStart() {
        super.onStart();
    }

    // Called when the Activity is no longer visible.
    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Display the connection status

        if (mCurrentLocation != null) {
            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate);
        } else {
            Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
        MapActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
    }



    @SuppressLint("MissingPermission")
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }


    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }

        // Report to the UI that the location was updated

        mCurrentLocation = location;
        final ParseUser user;
        user = ParseUser.getCurrentUser();
        if (user != null) {
            final ParseGeoPoint newlocation = new ParseGeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    user.put("location", newlocation);
                    user.saveInBackground();
                    Log.d("MapActivity", "user location updated to " + mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude());
                }
            });
        }

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    // Display the alert that adds the marker
    private void showAlertDialogForPoint(final LatLng point) {
        // inflate message_item.xml view
        View messageView = LayoutInflater.from(MapActivity.this).
                inflate(R.layout.message_item, null);
        // Create alert dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set message_item.xml to AlertDialog builder
        alertDialogBuilder.setView(messageView);

        // Create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // Configure dialog button (OK)
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Define color of marker icon
                        BitmapDescriptor defaultMarker =
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                        // Extract content from alert dialog
                        String title = ((EditText) alertDialog.findViewById(R.id.etTitle)).
                                getText().toString();
                        String snippet = ((EditText) alertDialog.findViewById(R.id.etSnippet)).
                                getText().toString();
                        // Creates and adds marker to the map
                        Marker marker = map.addMarker(new MarkerOptions()
                                .position(point)
                                .title(title)
                                .snippet(snippet)
                                .icon(defaultMarker));

                        dropPinEffect(marker);
                    }
                });

        // Configure dialog button (Cancel)
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { dialog.cancel(); }
                });

        // Display the dialog
        alertDialog.show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        final ParseUser user;
        user = ParseUser.getCurrentUser();

        if (mCurrentLocation != null && user != null) {
            final ParseGeoPoint location = new ParseGeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    user.put("location", location);
                    user.saveInBackground();
                }
            });
        } else {
            Log.d("MapActivity", "Current location is null");
        }
        onResume();
        return true;
    }


    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends android.support.v4.app.DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }


    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 150);
                } else { // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });
    }

    // makes vector images accessible
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // check request code and result code first


        if (resultCode == RESULT_OK) {
            data.getExtras();

            // Use data parameter

            String id = data.getStringExtra("id");
            Log.d("MapActivity", "new pin id " + id);

            pinQuery = new Pin.Query();
            pins = new ArrayList<>();
            pinQuery.whereEqualTo("objectId", id);
            pinQuery.findInBackground(new FindCallback<Pin>() {
                @Override
                public void done(List<Pin> objects, ParseException e) {
                    if (e==null){
                        for (int i = objects.size()-1; i >= 0; i--) {

                            Pin pin = objects.get(0);

                            if (pin.has("latlng"))  {
                                Log.d("MapActivity", "new pin exists?" + (pin != null));
                                lat = pin.getLatLng().getLatitude();
                                lon = pin.getLatLng().getLongitude();
                                type = pin.getCategory();
                                pins.add(pin);
                                Log.d("MapActivity", "added new Pin at " + lat);

                                int drawableId = getIcon(type);
                                BitmapDescriptor customMarker =
                                        BitmapDescriptorFactory.fromResource(drawableId);

                                LatLng listingPosition = new LatLng(lat, lon);
                                // Create the marker on the fragment
                                Marker marker = map.addMarker(new MarkerOptions()
                                        .position(listingPosition)
                                        .title("checkins: " + pin.getCheckincount())
                                        .snippet(objects.get(i).getComment())
                                        .icon(customMarker));

                                dropPinEffect(marker);

                            }
                        }
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private int getIcon(int type) {
        String imageName = "drop";
        switch (type) {
            case 0:
                imageName = "recycling_bin";
                break;
            case 1:
                imageName = "drop";
                break;
            case 2:
                imageName = "bicycle";
                break;
            case 3:
                imageName = "growth";
                break;
            case 4:
                imageName = "battery";
                break;
        }
        int drawableId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        return drawableId;

    };


    @OnClick(R.id.fab0)
    protected void onFab0() {
        onFab(0);
    }


    @OnClick(R.id.fab1)
    protected void onFab1() {
        onFab(1);
    }


    @OnClick(R.id.fab2)
    protected void onFab2() {
        onFab(2);
    }

    @OnClick(R.id.fab3)
    protected void onFab3() {
        onFab(3);
    }

    @OnClick(R.id.fab4)
    protected void onFab4() {
        onFab(4);
    }

    protected void onFab(final int type) {
        map.clear();
        pinQuery = new Pin.Query();
        pins = new ArrayList<>();
        pinQuery.whereEqualTo("category", type);
        pinQuery.findInBackground(new FindCallback<Pin>() {
            @Override
            public void done(List<Pin> objects, ParseException e) {
                if (e==null){
                    for (int i = objects.size()-1; i >= 0; i--) {

                        if (objects.get(i).has("latlng"))  {
                            lat = objects.get(i).getLatLng().getLatitude();
                            lon = objects.get(i).getLatLng().getLongitude();
                            Pin pin = objects.get(i);
                            pins.add(pin);


                            int drawableId = getIcon(type);
                            BitmapDescriptor customMarker =
                                    BitmapDescriptorFactory.fromResource(drawableId);

                            LatLng listingPosition = new LatLng(lat, lon);
                            // Create the marker on the fragment
                            Marker mapMarker = map.addMarker(new MarkerOptions()
                                    .position(listingPosition)
                                    .title("checkins: " + objects.get(i).getCheckincount())
                                    .snippet(objects.get(i).getComment())
                                    .icon(customMarker));
                        }

                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
/* TODO: add multiple categories at the same time functionality
 * buttons have pressed and unpressed state
 * coming back from filter to all categories view
 * distance to a pin
 * pin pic
 * add marker clusterization
 *
 * only download closer based on disctantce
 */
