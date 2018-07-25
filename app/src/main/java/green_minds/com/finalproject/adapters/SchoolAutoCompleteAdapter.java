package green_minds.com.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import green_minds.com.finalproject.R;

public class SchoolAutoCompleteAdapter extends BaseAdapter implements Filterable{

    private Context mContext;
    private List<String> schools = new ArrayList<>();
    private final static String API_BASE_URL = "https://api.data.gov/ed/collegescorecard/";
    private SyncHttpClient client;

    public SchoolAutoCompleteAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return schools.size();
    }

    @Override
    public String getItem(int position) {
        return schools.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_dropdown_item_1line, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<String> schoolNames = findSchools(mContext, constraint.toString());
                    filterResults.values = schoolNames;
                    filterResults.count = schoolNames.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    schools = (List<String>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private List<String> findSchools(Context context, String schoolName) {

        client = new SyncHttpClient();
        final List<String> resultList = new ArrayList<>();
        String url = API_BASE_URL + "v1/schools";
        RequestParams params = new RequestParams();

        params.put("api_key", context.getString(R.string.school_list_api_key));
        params.put("fields", "school.name");
        params.put("school.name", schoolName);
        params.put("per_page", 10);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        String schoolName = results.getJSONObject(i).getString("school.name");
                        resultList.add(schoolName);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
        return resultList;
    }
}

