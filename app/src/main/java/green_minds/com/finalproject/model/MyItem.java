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

    public MyItem(double lat, double lng, int pictureResource) {
        mPosition = new LatLng(lat, lng);
        mTitle = null;
        mSnippet = null;
        mTypeIcon = pictureResource;
        mImageUrl = null;
        mDistance = null;
    }

    public MyItem(double lat, double lng, String title, String snippet, int pictureResource) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        mTypeIcon = pictureResource;
        mImageUrl = null;
        mDistance = null;
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

    public int getTypeIcon() {return mTypeIcon; }

    /**
     * Set the title of the marker
     * @param title string to be set as title
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Set the description of the marker
     * @param snippet string to be set as snippet
     */
    public void setSnippet(String snippet) {
        mSnippet = snippet;
    }

    public void setImage(String imageUrl) {mImageUrl = imageUrl; }
    public void setDistance(String distance) {mDistance = distance; }


}