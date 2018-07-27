package green_minds.com.finalproject.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import green_minds.com.finalproject.R;
import green_minds.com.finalproject.model.GlideApp;
import green_minds.com.finalproject.model.InfoWindowData;
import green_minds.com.finalproject.model.MyItem;

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private List <MyItem> mItems;

    public MyInfoWindowAdapter(Context ctx, List <MyItem> it){
        context = ctx;
        mItems = it;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.custom_info_window, null);

        for (MyItem item : mItems) {
            TextView title = view.findViewById(R.id.title);
            TextView description = view.findViewById(R.id.description);
            TextView distance = view.findViewById(R.id.distance);
            ImageView img = view.findViewById(R.id.img);


            title.setText(marker.getTitle());
            description.setText(marker.getSnippet());


            // InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();
            distance.setText("distance: " + item.getDistance());


            GlideApp.with(context).load(item.getImageUrl()).centerCrop().into(img);
        }


        return view;
    }
}