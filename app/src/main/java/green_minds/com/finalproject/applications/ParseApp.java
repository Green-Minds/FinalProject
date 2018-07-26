package green_minds.com.finalproject.applications;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

import green_minds.com.finalproject.model.Goal;
import green_minds.com.finalproject.model.Pin;

public class ParseApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Pin.class);
        ParseObject.registerSubclass(Goal.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("greenMindsFBU")
                .clientKey("meatMagic")
                .server("http://greenMindsFBU.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
        ParseFacebookUtils.initialize(this);


    }
}
