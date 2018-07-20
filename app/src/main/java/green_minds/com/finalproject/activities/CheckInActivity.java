package green_minds.com.finalproject.activities;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import green_minds.com.finalproject.adapters.PinAdapter;
import green_minds.com.finalproject.model.Pin;
import green_minds.com.finalproject.model.RelativePositionPin;
import green_minds.com.finalproject.R;

public class CheckInActivity extends AppCompatActivity {

    private Location currLocation;
    private ArrayList<RelativePositionPin> mPins;
    private PinAdapter adapter;
    private RecyclerView rvPins;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        currLocation = getCurrentLocation();
        rvPins = findViewById(R.id.rv_pins);
        mPins = new ArrayList<>();
        adapter = new PinAdapter(mPins);
        rvPins.setLayoutManager(new LinearLayoutManager(this));
        rvPins.setAdapter(adapter);
        getListOfPins();
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
                                RelativePositionPin rpPin = (convert(pin, currLocation));
                                Location pinLoc = getLocationFromPin(pin);
                                rpPin.setDistanceAway((double)currLocation.distanceTo(pinLoc));
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

    private Location getCurrentLocation(){
        Double lat = getIntent().getDoubleExtra("latitude", 0.0);
        Double lon = getIntent().getDoubleExtra("longitude", 0.0);
        Location resLoc = new Location("");
        resLoc.setLatitude(lat);
        resLoc.setLongitude(lon);
        return resLoc;
    }
}
