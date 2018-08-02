package green_minds.com.finalproject.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseUser;

import green_minds.com.finalproject.R;
import green_minds.com.finalproject.model.Category;
import green_minds.com.finalproject.model.CategoryHelper;

public class ScoreAdapter extends ArrayAdapter<Category> {

    private Context mContext;
    private ParseUser mUser;
    private Category[] mCategories;

    public ScoreAdapter(Context context, Category[] items) {
        super(context, R.layout.item_score, items);
        this.mContext = context;
        this.mUser = ParseUser.getCurrentUser();
        this.mCategories = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=vi.inflate(R.layout.item_score, null);
        }

        int type = mCategories[position].getTypeKey();

        TextView tvIdentifier = convertView.findViewById(R.id.tv_description);
        tvIdentifier.setText(CategoryHelper.getPinIdentifier(type));

        TextView numberOf = convertView.findViewById(R.id.numberOf);
        final int checkins = mUser.getInt(CategoryHelper.getTypeKey(type));
        numberOf.setText(checkins + "");

        return convertView;
    }
}
