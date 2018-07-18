package green_minds.com.finalproject.Model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


@ParseClassName("Pin")
public class Pin extends ParseObject{
    private static final String KEY_LATLNG = "latlng";
    private static final String KEY_USER = "user";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_CHECKINCOUNT = "checkincount";
    private static final String KEY_PHOTO = "photo";

    public ParseGeoPoint getLatLng() { return getParseGeoPoint(KEY_LATLNG);}

    public void setLatLng(ParseGeoPoint latLng) { put(KEY_LATLNG, latLng);}

    public ParseUser getUser() {return getParseUser(KEY_USER);}

    public void setUser(ParseUser user) { put(KEY_USER, user);}

    public String getComment() {return getString(KEY_COMMENT);}

    public void setComment(String comment) {put(KEY_COMMENT, comment);}

    public String getCategory() {return getString(KEY_CATEGORY);}

    public void setCategory(String category) {put(KEY_CATEGORY, category);}

    public Integer getCheckincount() {return getInt(KEY_CHECKINCOUNT);}

    public void updateCheckincount() { put(KEY_CHECKINCOUNT, getCheckincount() + 1);}

    public ParseFile getPhoto() { return getParseFile(KEY_PHOTO);}

    public void setPhoto(ParseFile photo) { put(KEY_PHOTO, photo);}


    public static class Query extends ParseQuery<Pin> {

        public Query() {
            super(Pin.class);
        }

        public Query getTop() {
            setLimit(50);
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }
    }

}
