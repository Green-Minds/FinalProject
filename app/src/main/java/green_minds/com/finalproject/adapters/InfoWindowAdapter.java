package green_minds.com.finalproject.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import green_minds.com.finalproject.R;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public InfoWindowAdapter(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.custom_info_window, null);

        TextView title = view.findViewById(R.id.title);
        TextView description = view.findViewById(R.id.description);


        title.setText(marker.getTitle());
        description.setText(marker.getSnippet());

        // InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();


        return view;
    }
}