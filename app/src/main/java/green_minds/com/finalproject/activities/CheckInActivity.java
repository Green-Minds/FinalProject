package green_minds.com.finalproject.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import green_minds.com.finalproject.R;
import green_minds.com.finalproject.adapters.PinAdapter;
import green_minds.com.finalproject.model.Pin;
import green_minds.com.finalproject.model.RelativePositionPin;

public class CheckInActivity extends AppCompatActivity {

    private ArrayList<RelativePositionPin> mPins;
    private PinAdapter adapter;
    private RecyclerView rvPins;
    private LocationManager locationManager;
    private Location lastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        rvPins = findViewById(R.id.rv_pins);
        mPins = new ArrayList<>();
        adapter = new PinAdapter(mPins);
        rvPins.setLayoutManager(new LinearLayoutManager(this));
        rvPins.setAdapter(adapter);
        getListOfPins();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } else {
                    Toast.makeText(this, "Please enable permissions for this to work.", Toast.LENGTH_LONG).show();
                    return;
                }
                return;
            }
        }
    }

    private void getListOfPins(){

        ParseQuery<Pin> query = ParseQuery.getQuery(Pin.class);
        query.getQuery(Pin.class).findInBackground(
            new FindCallback<Pin>() {
                @Override
                public void done(List<Pin> objects, ParseException e) {

                    if (e == null) {
                        ArrayList<RelativePositionPin> rpList = new ArrayList<>();
                        for(Pin pin: objects){
                            RelativePositionPin rpPin = (convert(pin, lastLocation));
                            Location pinLoc = getLocationFromPin(pin);
                            rpPin.setDistanceAway((double)lastLocation.distanceTo(pinLoc));
                            if(rpPin.getDistanceAwayinMiles() < 0.5) rpList.add(rpPin);
                        }
                        Collections.sort(rpList);
                        mPins.clear();
                        mPins.addAll(rpList);
                        adapter.notifyDataSetChanged();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
    }

    private RelativePositionPin convert(Pin pin, Location currLocation){
        RelativePositionPin rp = new RelativePositionPin(pin);
        Location pinLoc = getLocationFromPin(pin);
        rp.setDistanceAway((double)currLocation.distanceTo(pinLoc));
        return rp;
    }

    private Location getLocationFromPin(Pin pin){
        ParseGeoPoint gp = pin.getLatLng();
        Location resLoc = new Location("");
        resLoc.setLatitude(gp.getLatitude());
        resLoc.setLongitude(gp.getLongitude());
        return resLoc;
    }
}
