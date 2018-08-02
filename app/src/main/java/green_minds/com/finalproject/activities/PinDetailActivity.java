package green_minds.com.finalproject.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.model.GlideApp;

public class PinDetailActivity extends AppCompatActivity {

    @BindView(R.id.ivImage) public ImageView ivImage;
    @BindView(R.id.tvPin) public TextView tvPin;
    @BindView(R.id.tvComment) public TextView tvComment;
    @BindView(R.id.tvDistance) public TextView tvDistance;
    @BindView(R.id.backBtn) public ImageButton backBtn;
    @BindView(R.id.navigationView) public BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_detail);

        String mCheckin = getIntent().getStringExtra("checkins");
        String mComment = getIntent().getStringExtra("comment");
        String mDistance = getIntent().getStringExtra("distance");
        String mImage = getIntent().getStringExtra("image");
        int mType = getIntent().getIntExtra("type", 0);

        ButterKnife.bind(this);

        tvPin.setText(mCheckin);
        tvComment.setText(mComment);
        tvDistance.setText("distance: " + mDistance);
        GlideApp.with(PinDetailActivity.this).load(mImage).centerCrop().into(ivImage);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_map:
                        gotoMap();
                        return true;
                    case R.id.navigation_user:
                        gotoProfile();
                        return true;
                    case R.id.navigation_board:
                        gotoLeaderboard();
                        return true;
                }
                return false;
            }
        });
    }
    private void gotoLeaderboard() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void gotoProfile() {
        startActivity(new Intent(this, UserInfoActivity.class));
    }
    private void gotoMap() {
        startActivity(new Intent(this, MapActivity.class));
    }

    @OnClick(R.id.backBtn)
    protected void back() {
        finish();
    }

}
