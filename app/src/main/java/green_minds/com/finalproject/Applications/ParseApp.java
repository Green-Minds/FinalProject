package green_minds.com.finalproject.Applications;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseObject;

import green_minds.com.finalproject.Model.Pin;

public class ParseApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Pin.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("greenMindsFBU")
                .clientKey("meatMagic")
                .server("http://greenMindsFBU.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}
