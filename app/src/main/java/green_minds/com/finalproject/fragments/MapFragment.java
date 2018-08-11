package green_minds.com.finalproject.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.model.GlideApp;
import green_minds.com.finalproject.model.InfoWindowData;
import green_minds.com.finalproject.model.MyItem;
import green_minds.com.finalproject.model.Pin;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.parse.Parse.getApplicationContext;
import static green_minds.com.finalproject.fragments.MapFragmentPermissionsDispatcher.getMyLocationWithPermissionCheck;
import static green_minds.com.finalproject.fragments.MapFragmentPermissionsDispatcher.startLocationUpdatesWithPermissionCheck;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


@RuntimePermissions
public class MapFragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener {

    private MapFragment.OnFragmentInteractionListener mListener;
    private Context mContext;

    @BindView(R.id.newPinBtn)
    public FloatingActionButton newPinBtn;
    @BindView(R.id.checkinBtn)
    public FloatingActionButton checkinBtn;
    @BindView(R.id.fab)
    public FloatingActionButton fab;
    @BindView(R.id.fab0)
    public FloatingActionButton fab0;
    @BindView(R.id.fab1)
    public FloatingActionButton fab1;
    @BindView(R.id.fab2)
    public FloatingActionButton fab2;
    @BindView(R.id.fab3)
    public FloatingActionButton fab3;
    @BindView(R.id.fab4)
    public FloatingActionButton fab4;
    @BindView(R.id.ivAdjust)
    public ImageView ivAdjust;
    @BindView(R.id.adjustBtn)
    public Button adjustBtn;
    @BindView(R.id.tvAdjust)
    public TextView tvAdjust;

    private final int REQUEST_CODE = 20;
    final public static String PIN_KEY = "pin";
    private Pin.Query pinQuery;
    ArrayList<Pin> pins;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    public Location mCurrentLocation;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private double lat = 0;
    private double lon = 0;
    private int type = 0;
    private boolean isFABOpen = false;
    ParseUser user;
    String image;
    private LatLng currentLoc;
    private final static String KEY_LOCATION = "location";
    private float mZoom = 10;
    private Pin mNewPin;
    private ClusterManager<MyItem> mClusterManager;
    public List<MyItem> items = new ArrayList<MyItem>();

    private Cluster<MyItem> clickedCluster;
    private MyItem clickedClusterItem;
    private static boolean tapEvent = false;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public MapFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);


        user = ParseUser.getCurrentUser();
        ivAdjust.setVisibility(View.GONE);
        adjustBtn.setVisibility(View.GONE);
        tvAdjust.setVisibility(View.GONE);

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            });
        } else {
            // Toast.makeText(, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyItemRenderer extends DefaultClusterRenderer<MyItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());

        public MyItemRenderer() {
            super(mContext, map, mClusterManager);
        }

        @Override
        protected int getColor(int clusterSize) {
            return getResources().getColor(R.color.colorAccent);
        }


        @Override
        protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
            markerOptions.icon(bitmapDescriptorFromVector(mContext, item.getTypeIcon()));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 10;
        }
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready

            mClusterManager = new ClusterManager<MyItem>(mContext, map);
            mClusterManager.setRenderer(new MyItemRenderer());

            map.setOnInfoWindowClickListener(mClusterManager);
            map.setInfoWindowAdapter(mClusterManager.getMarkerManager());

            mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(
                    new MyCustomAdapterForItems());

            map.setOnMarkerClickListener(mClusterManager);
            // mClusterManager.setOnClusterClickListener((ClusterManager.OnClusterClickListener<MyItem>) MapActivity.this);
            // mClusterManager.setOnClusterItemInfoWindowClickListener(this);

            mClusterManager
                    .setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
                        @Override
                        public boolean onClusterItemClick(MyItem item) {
                            clickedClusterItem = item;

                            return false;
                        }
                    });

            mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MyItem>() {
                @Override
                public void onClusterItemInfoWindowClick(MyItem myItem) {
                    clickedClusterItem = myItem;
                    mListener.goToPinDetails(myItem);
                }
            });
            user = ParseUser.getCurrentUser();
            if (user != null) {
                ParseGeoPoint loc = user.getParseGeoPoint("location");
                if (loc != null) {
                    LatLng userloc = new LatLng(loc.getLatitude(), loc.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userloc, 17);
                    map.moveCamera(cameraUpdate);
                }
            }

            UiSettings mapUiSettings = map.getUiSettings();
            mapUiSettings.setZoomControlsEnabled(true);
            map.setMinZoomPreference(6.0f);

            getMyLocationWithPermissionCheck(this);
            startLocationUpdatesWithPermissionCheck(this);

            getMyLocation();
            map.setOnCameraMoveStartedListener(this);
            map.setOnCameraMoveListener(this);
            map.setOnCameraMoveCanceledListener(this);

            getMyLocation();
            if (mCurrentLocation != null) {
                Toast.makeText(mContext, "GPS location was found!", Toast.LENGTH_SHORT).show();
                LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                map.moveCamera(cameraUpdate);
            }

            onCameraIdle();
            if (user != null) {
                ParseGeoPoint loc = user.getParseGeoPoint("location");
                if (loc != null) {
                    LatLng userloc = new LatLng(loc.getLatitude(), loc.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userloc, 17);
                    map.moveCamera(cameraUpdate);
                    showAll();
                }
            }
            map.setOnCameraIdleListener(this);

        } else {
            Toast.makeText(mContext, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.fab)
    protected void onFab() {
        if (!isFABOpen) {
            showFABMenu();
        } else {
            closeFABMenu();
        }
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fab0.animate().translationY(0);
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
        fab4.animate().translationY(0);
    }

    private void showFABMenu() {
        isFABOpen = true;
        fab0.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_115));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_165));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_215));
        fab4.animate().translationY(-getResources().getDimension(R.dimen.standard_265));
    }


    @OnClick(R.id.checkinBtn)
    protected void goToCheckin() {
        mListener.goToCheckin(mCurrentLocation);
    }


    @OnClick(R.id.newPinBtn)
    protected void goToNewPin() {
        mListener.goToNewPin(mCurrentLocation);
    }


    @SuppressWarnings({"MissingPermission"})
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);

        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(mContext);
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(mContext);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        getFusedLocationProviderClient(mContext).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }


    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {

            new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Google Play Services not available!")
                    .show();

            return false;
        }
    }


    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }
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

    @Override
    public void onCameraIdle() {
        // this piece is here because otherwise infowindows instantly dissappear
        if (tapEvent) {
            tapEvent = false;
        } else {
            if (fab0.isSelected()) {
                fab0.setSelected(false);
                onFab0();
            } else if (fab1.isSelected()) {
                fab1.setSelected(false);
                onFab1();
            } else if (fab2.isSelected()) {
                fab2.setSelected(false);
                onFab2();
            } else if (fab3.isSelected()) {
                fab3.setSelected(false);
                onFab3();
            } else if (fab4.isSelected()) {
                fab4.setSelected(false);
                onFab4();
            } else {
                showAll();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Display the connection status

        if (mCurrentLocation != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.moveCamera(cameraUpdate);
        } else {
        }
        startLocationUpdatesWithPermissionCheck(this);
    }

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == GoogleMap.OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
            tapEvent = true;
        }

    }

    // Define a DialogFragment that displays the error dialog
    public static class NewPinDialogFragment extends android.support.v4.app.DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public NewPinDialogFragment() {
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
        final long duration = 5000;

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
                    marker.remove();
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


    public void adjustScreen(String id) {
        Log.d("MapActivity", "new pin id " + id);
        user = ParseUser.getCurrentUser();
        final ParseGeoPoint loc = user.getParseGeoPoint("location");
        pinQuery = new Pin.Query();
        pins = new ArrayList<>();
        pinQuery.whereEqualTo("objectId", id);
        pinQuery.findInBackground(new FindCallback<Pin>() {
            @Override
            public void done(List<Pin> objects, ParseException e) {
                if (e == null) {
                    mNewPin = objects.get(0);

                    if (mNewPin.has("latlng")) {
                        Log.d("MapActivity", "new pin exists?" + (mNewPin != null));
                        lat = mNewPin.getLatLng().getLatitude();
                        lon = mNewPin.getLatLng().getLongitude();
                        type = mNewPin.getCategory();
                        pins.add(mNewPin);

                        CameraUpdate cameraNewPin = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 17);
                        map.moveCamera(cameraNewPin);

                        Log.d("MapFragment", "added new Pin at " + lat);
                        mNewPin.setLatLng(new ParseGeoPoint(lat, lon));


                        new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Good job!")
                                .setContentText("You added a new pin!")
                                .show();
                    }

                } else {
                    e.printStackTrace();
                }
            }
        });
    }


    private int getIcon(int type) {
        String imageName = "drop";
        switch (type) {
            case 0:
                imageName = "ic_recycling_bin";
                break;
            case 1:
                imageName = "ic_drop";

                break;
            case 2:
                imageName = "ic_bicycle";
                break;
            case 3:
                imageName = "ic_coins";
                break;
            case 4:
                imageName = "ic_battery";
                break;
        }
        int drawableId = getResources().getIdentifier(imageName, "drawable", getActivity().getPackageName());
        return drawableId;
    }

    @OnClick(R.id.fab0)
    protected void onFab0() {
        Log.d("MapAcgtivity", "after click fab0 selected " + fab0.isSelected());
        if (!fab0.isSelected()) {
            fab0.setSelected(true);
            fab0.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            onFab(0);
            unselectOthers(0);
        } else if (fab0.isSelected()) {
            fab0.setSelected(false);
            fab0.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white_90_transparent)));
            showAll();
        }
    }

    @OnClick(R.id.fab1)
    protected void onFab1() {
        if (!fab1.isSelected()) {
            fab1.setSelected(true);
            fab1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            onFab(1);
            unselectOthers(1);
        } else if (fab1.isSelected()) {
            fab1.setSelected(false);
            fab1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white_90_transparent)));
            showAll();
        }
    }

    @OnClick(R.id.fab2)
    protected void onFab2() {
        if (!fab2.isSelected()) {
            fab2.setSelected(true);
            fab2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            onFab(2);
            unselectOthers(2);
        } else if (fab2.isSelected()) {
            fab2.setSelected(false);
            fab2.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white_90_transparent)));
            showAll();
        }
    }

    @OnClick(R.id.fab3)
    protected void onFab3() {
        if (!fab3.isSelected()) {
            fab3.setSelected(true);
            fab3.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            onFab(3);
            unselectOthers(3);
        } else if (fab3.isSelected()) {
            fab3.setSelected(false);
            fab3.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white_90_transparent)));
            showAll();
        }
    }

    @OnClick(R.id.fab4)
    protected void onFab4() {
        if (!fab4.isSelected()) {
            fab4.setSelected(true);
            fab4.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            onFab(4);
            unselectOthers(4);
        } else if (fab4.isSelected()) {
            fab4.setSelected(false);
            fab4.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white_90_transparent)));
            showAll();
        }
    }

    protected void onFab(final int type) {
        map.clear();
        mClusterManager.clearItems();
        CameraPosition currentCameraPosition = map.getCameraPosition();
        currentLoc = currentCameraPosition.target;
        mZoom = currentCameraPosition.zoom;
        final ParseGeoPoint loc = new ParseGeoPoint(currentLoc.latitude, currentLoc.longitude);
        pinQuery = new Pin.Query();
        pins = new ArrayList<>();
        pinQuery.whereEqualTo("category", type);
        pinQuery.whereWithinMiles("latlng", loc, radius());
        pinQuery.findInBackground(new FindCallback<Pin>() {
            @Override
            public void done(List<Pin> objects, ParseException e) {
                if (e == null) {
                    for (int i = objects.size() - 1; i >= 0; i--) {

                        if (objects.get(i).has("latlng")) {
                            lat = objects.get(i).getLatLng().getLatitude();
                            lon = objects.get(i).getLatLng().getLongitude();
                            Pin pin = objects.get(i);
                            pins.add(pin);

                            ParseFile photo = pin.getPhoto();
                            if (photo != null) {
                                image = photo.getUrl();
                            }

                            int drawableId = getIcon(type);
                            user = ParseUser.getCurrentUser();
                            final ParseGeoPoint userloc = user.getParseGeoPoint("location");
                            InfoWindowData info = new InfoWindowData();
                            info.setDistance(round(pin.getLatLng().distanceInKilometersTo(userloc), 3) + "km");
                            MyItem item = new MyItem(lat, lon, drawableId);
                            item.setTitle("checkins: " + objects.get(i).getCheckincount());
                            item.setSnippet(objects.get(i).getComment());
                            item.setImage(image);
                            item.setType(type);
                            item.setCheckins(objects.get(i).getCheckincount());
                            item.setDistance(round(pin.getLatLng().distanceInKilometersTo(userloc), 3) + "km");
                            mClusterManager.addItem(item);
                            mClusterManager.cluster();
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }


    private static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    protected void unselectOthers(int n) {
        if (n != 0) {
            fab0.setSelected(false);
            fab0.setBackgroundTintList(ColorStateList.valueOf(-1));
        }

        if (n != 1) {
            fab1.setSelected(false);
            fab1.setBackgroundTintList(ColorStateList.valueOf(-1));
        }

        if (n != 2) {
            fab2.setSelected(false);
            fab2.setBackgroundTintList(ColorStateList.valueOf(-1));
        }
        if (n != 3) {
            fab3.setSelected(false);
            fab3.setBackgroundTintList(ColorStateList.valueOf(-1));
        }
        if (n != 4) {
            fab4.setSelected(false);
            fab4.setBackgroundTintList(ColorStateList.valueOf(-1));
        }
    }

    protected void showAll() {
        CameraPosition currentCameraPosition = map.getCameraPosition();
        currentLoc = currentCameraPosition.target;
        mZoom = currentCameraPosition.zoom;
        mClusterManager.clearItems();
        final ParseGeoPoint loc = new ParseGeoPoint(currentLoc.latitude, currentLoc.longitude);
        pinQuery = new Pin.Query();
        pins = new ArrayList<>();
        pinQuery.getTop();
        pinQuery.whereWithinMiles("latlng", loc, radius());
        pinQuery.findInBackground(new FindCallback<Pin>() {
            @Override
            public void done(List<Pin> objects, ParseException e) {
                if (e == null) {
                    for (int i = objects.size() - 1; i >= 0; i--) {

                        if (objects.get(i).has("latlng")) {
                            lat = objects.get(i).getLatLng().getLatitude();
                            lon = objects.get(i).getLatLng().getLongitude();
                            type = objects.get(i).getCategory();

                            Pin pin = objects.get(i);
                            pins.add(pin);
                            int drawableId = getIcon(type);
                            BitmapDescriptor customMarker =
                                    BitmapDescriptorFactory.fromResource(drawableId);

                            ParseFile photo = pin.getPhoto();
                            if (photo != null) {
                                image = photo.getUrl();
                            }

                            InfoWindowData info = new InfoWindowData();
                            info.setImage(image);
                            user = ParseUser.getCurrentUser();
                            final ParseGeoPoint userloc = user.getParseGeoPoint("location");

                            MyItem item = new MyItem(lat, lon, drawableId);
                            item.setTitle("checkins: " + objects.get(i).getCheckincount());
                            item.setType(type);
                            item.setCheckins(objects.get(i).getCheckincount());
                            item.setSnippet(objects.get(i).getComment());
                            item.setImage(image);
                            item.setDistance(round(pin.getLatLng().distanceInKilometersTo(userloc), 3) + "km");
                            Log.d("MapFragment", "user location lat: " + userloc.getLatitude() + " pin loc " + pin.getLatLng().getLatitude() + " distance in pin " + item.getDistance());
                            Log.d("MapFragment", "does cluster item exist? " + (item != null) + " " + item.getTitle());
                            mClusterManager.addItem(item);
                            items.add(item);
                            mClusterManager.cluster();
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }


    private double radius() {
        double radius = 10;

        VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();

        LatLng farRight = visibleRegion.farRight;
        LatLng farLeft = visibleRegion.farLeft;
        LatLng nearRight = visibleRegion.nearRight;
        LatLng nearLeft = visibleRegion.nearLeft;

        float[] distanceWidth = new float[1];
        float[] distanceHeight = new float[1];

        Location.distanceBetween(
                (farLeft.latitude + nearLeft.latitude) / 2,
                farLeft.longitude,
                (farRight.latitude + nearRight.latitude) / 2,
                farRight.longitude,
                distanceWidth
        );

        Location.distanceBetween(
                farRight.latitude,
                (farRight.longitude + farLeft.longitude) / 2,
                nearRight.latitude,
                (nearRight.longitude + nearLeft.longitude) / 2,
                distanceHeight
        );

        radius = Math.sqrt(Math.pow(distanceWidth[0], 2) + Math.pow(distanceHeight[0], 2)) / 2;
        return radius;

    }

    public class MyCustomAdapterForItems implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyCustomAdapterForItems() {
            myContentsView = getLayoutInflater().inflate(
                    R.layout.custom_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            final Handler handler = new Handler();
            final Marker finalmarker = marker;
            TextView title = myContentsView.findViewById(R.id.title);
            TextView distance = myContentsView.findViewById(R.id.distance);
            ImageView img = myContentsView.findViewById(R.id.img);

            if (clickedClusterItem != null) {
                title.setText(clickedClusterItem.getTitle());
                distance.setText("distance: " + clickedClusterItem.getDistance());
                GlideApp.with(mContext).load(clickedClusterItem.getImageUrl()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                finalmarker.showInfoWindow();
                            }
                        });

                        return false;
                    }
                }).apply(new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(8))).placeholder(R.drawable.placeholder).into(img);
            }
            return myContentsView;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void showProgressBar();

        void hideProgressBar();

        void goToCheckin(Location currentLocation);

        void goToNewPin(Location currentLocation);

        void goToPinDetails(MyItem myItem);

    }
}
