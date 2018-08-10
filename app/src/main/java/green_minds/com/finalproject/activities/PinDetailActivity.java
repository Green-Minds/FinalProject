package green_minds.com.finalproject.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.model.CategoryHelper;
import green_minds.com.finalproject.model.GlideApp;

public class PinDetailActivity extends AppCompatActivity {

    @BindView(R.id.ivImage) public ImageView ivImage;
    @BindView(R.id.tvPin) public TextView tvPin;
    @BindView(R.id.tvComment) public TextView tvComment;
    @BindView(R.id.tvDistance) public TextView tvDistance;
    @BindView(R.id.tv_checkins) public TextView tvCheckins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_detail);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar a = getSupportActionBar();
        // Enable the Up button
        a.setDisplayHomeAsUpEnabled(true);
        a.setTitle("Pin Detail");

        String mCheckin = getIntent().getStringExtra("Title");
        String mComment = getIntent().getStringExtra("comment");
        String mDistance = getIntent().getStringExtra("distance");
        String mImage = getIntent().getStringExtra("image");
        int mType = getIntent().getIntExtra("type", 0);
        //put title here
        String identifier = "placeholder identifier";

        ButterKnife.bind(this);

        tvPin.setText(CategoryHelper.listOfCategories[mType]);
        tvCheckins.setText(mCheckin);
        tvComment.setText(mComment);
        tvDistance.setText("distance: " + mDistance);

        GlideApp.with(PinDetailActivity.this).load(mImage)
                .thumbnail(Glide.with(this)
                        .load(R.drawable.loading)
                        .apply(new RequestOptions().transforms(new CenterCrop()))).centerCrop()
                .into(ivImage);

    }

}
