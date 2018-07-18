package green_minds.com.finalproject.Activities;

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

import green_minds.com.finalproject.Adapters.PinAdapter;
import green_minds.com.finalproject.Model.Pin;
import green_minds.com.finalproject.Model.RelativePositionPin;
import green_minds.com.finalproject.R;

public class CheckInActivity extends AppCompatActivity {

    private Location curr_location;
    private ArrayList<RelativePositionPin> mPins;
    private PinAdapter adapter;
    private RecyclerView rvPins;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        curr_location = getCurrentLocation();
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
                            ArrayList<RelativePositionPin> rp_list = new ArrayList<>();
                            for(Pin pin: objects){
                                RelativePositionPin rp_pin = (convert(pin, curr_location));
                                if(rp_pin.getDistanceAwayinMiles() < 0.5) rp_list.add(rp_pin);
                            }
                            Collections.sort(rp_list);
                            mPins.clear();
                            mPins.addAll(rp_list);
                            adapter.notifyDataSetChanged();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private RelativePositionPin convert(Pin pin, Location curr_location){
        RelativePositionPin rp = new RelativePositionPin(pin);
        Location pin_loc = getLocationFromPin(pin);
        rp.setDistanceAway((double)curr_location.distanceTo(pin_loc));
        return rp;
    }

    private Location getLocationFromPin(Pin pin){
        ParseGeoPoint gp = pin.getLatLng();
        Location res_loc = new Location("");
        res_loc.setLatitude(gp.getLatitude());
        res_loc.setLongitude(gp.getLongitude());
        return res_loc;
    }

    private Location getCurrentLocation(){
        Double lat = getIntent().getDoubleExtra("latitude", 0.0);
        Double lon = getIntent().getDoubleExtra("longitude", 0.0);
        Location res_loc = new Location("");
        res_loc.setLatitude(lat);
        res_loc.setLongitude(lon);
        return res_loc;
    }
}
