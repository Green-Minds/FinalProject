package green_minds.com.finalproject.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    public String mTitle;
    public String mSnippet;
    private final int mTypeIcon;
    public String mImageUrl;
    public String mDistance;
    private int mType;
    private int mCheckins;

    public MyItem(double lat, double lng, int pictureResource) {
        mPosition = new LatLng(lat, lng);
        mTitle = null;
        mSnippet = null;
        mTypeIcon = pictureResource;
        mImageUrl = null;
        mDistance = null;
        mType = 0;
        mCheckins = 0;
    }

    public MyItem(double lat, double lng, String title, String snippet, int pictureResource) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        mTypeIcon = pictureResource;
        mImageUrl = null;
        mDistance = null;
        mType = 0;
        mCheckins = 0;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() { return mTitle; }

    @Override
    public String getSnippet() { return mSnippet; }

    public String getDistance() {return mDistance; }
    public String getImageUrl() {return mImageUrl; }


    public int getType() {return mType; }
    public int getCheckins() {return mCheckins; }
    public int getTypeIcon() {return mTypeIcon; }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setSnippet(String snippet) {
        mSnippet = snippet;
    }

    public void setImage(String imageUrl) {mImageUrl = imageUrl; }
    public void setDistance(String distance) {mDistance = distance; }
    public void setType(int type) {mType = type; }
    public void setCheckins(int checkins) {mCheckins = checkins; }

}