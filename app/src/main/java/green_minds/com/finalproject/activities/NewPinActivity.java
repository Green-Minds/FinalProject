package green_minds.com.finalproject.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.fragments.AdjustPinFragment;
import green_minds.com.finalproject.fragments.UserInfoFragment;
import green_minds.com.finalproject.model.GlideApp;
import green_minds.com.finalproject.model.ImageHelper;
import green_minds.com.finalproject.model.Pin;

public class NewPinActivity extends AppCompatActivity implements AdjustPinFragment.OnFragmentInteractionListener{

    @BindView(R.id.btn_camera)
    ImageButton btnCamera;

    @BindView(R.id.btn_pin)
    Button btnPin;

    @BindView(R.id.locBtn)
    Button locBtn;


    @BindView(R.id.et_comment)
    EditText etComment;

    @BindView(R.id.iv_preview)
    ImageView ivPreview;

    @BindView(R.id.tv_upload)
    TextView tvUpload;

    @BindView(R.id.rb_categories)
    RadioGroup rbCategories;

    private Bitmap mCurrentBitmap;
    private Context context;
    private ParseUser currentUser;
    private MenuItem miActionProgressItem;
    private boolean saving;
    private boolean adjusted = false;

    final public static String CODE_KEY = "REQUEST_CODE";
    final public static int REQUEST_CODE = 31;

    private AdjustPinFragment adjustPinFragment;
    private FragmentTransaction ft;

    private Double lat;
    private Double lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pin);
        ButterKnife.bind(this);
        mCurrentBitmap = null;
        context = this;
        saving = false;

        if (ParseUser.getCurrentUser() == null) {
            redirectToLogin();
        } else {
            currentUser = ParseUser.getCurrentUser();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bmp = null;
        String filepath = data.getStringExtra("image");
        bmp = BitmapFactory.decodeFile(filepath);
//        int wid = bmp.getWidth();
//        int hei = bmp.getHeight();
//
//        ivPreview.requestLayout();
//        wid = ivPreview.getLayoutParams().width * wid / hei;
//        ivPreview.getLayoutParams().width = wid;
//        ivPreview.setImageBitmap(bmp);

        GlideApp.with(getApplicationContext())
                .load(bmp)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(ivPreview);

        mCurrentBitmap = bmp;
        tvUpload.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_pin)
    public void uploadPin() {
        if (mCurrentBitmap == null) {
            Toast.makeText(this, "Please upload an image first!", Toast.LENGTH_LONG).show();
            return;
        }
        if (rbCategories.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please check a category first!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!adjusted) {
            Toast.makeText(this, "Please adjust the position of the pin first!", Toast.LENGTH_LONG).show();
            return;
        }

        savePin(new ParseGeoPoint(lat, lon));

    }


    @OnClick(R.id.locBtn)
    public void onLocBtn() {

        lat = getIntent().getDoubleExtra("latitude", 0.0);
        lon = getIntent().getDoubleExtra("longitude", 0.0);
        ParseGeoPoint location = new ParseGeoPoint(lat, lon);
        // savePin(location);

        btnPin.setVisibility(View.GONE);
        locBtn.setVisibility(View.GONE);
        adjustPinFragment= adjustPinFragment.newInstance(lat, lon);
        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, adjustPinFragment);
        ft.commit();
    }

    private void savePin(ParseGeoPoint location) {
        saving = true;
        showProgressBar();
        final Pin pin = new Pin();

        int radioButtonID = rbCategories.getCheckedRadioButtonId();
        View radioButton = rbCategories.findViewById(radioButtonID);
        final int idx = rbCategories.indexOfChild(radioButton);

        String comment = etComment.getText().toString();
        pin.setCategory(idx);
        pin.setComment(comment);
        pin.setCheckincount(0);
        pin.setLatLng(location);
        //saveRestOfPin(pin);

        final ParseFile photo = ImageHelper.getParseFile(mCurrentBitmap);
        final ParseFile smallerPhoto = ImageHelper.getSmallerParseFile(mCurrentBitmap);
        photo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                hideProgressBar();
                if (e != null) {
                    e.printStackTrace();
                    Toast.makeText(context, getString(R.string.misc_error), Toast.LENGTH_SHORT).show();
                } else {
                    smallerPhoto.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            pin.setPhoto(photo);
                            pin.setSmallerPhoto(smallerPhoto);
                            saveRestOfPin(pin);
                        }
                    });

                }
            }
        });

    }

    private void saveRestOfPin(final Pin pin) {
        pin.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(context, getString(R.string.pin_save_failure), Toast.LENGTH_SHORT).show();
                    //maybe use saveEventually here
                } else {
                    savePinToUser(pin);
                }
            }
        });
    }

    private void savePinToUser(final Pin pin){

        currentUser.increment("pincount");
        currentUser.increment("points", 10);

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                hideProgressBar();
                if (e != null) {
                    e.printStackTrace();
                    Toast.makeText(context, "USER" + getString(R.string.pin_save_failure), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "New Pin Complete!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.putExtra("id", pin.getObjectId());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    @OnClick(R.id.btn_camera)
    public void loadCamera() {
        if(saving) return;
        Intent i = new Intent(this, green_minds.com.finalproject.activities.CameraActivity.class);
        i.putExtra(CODE_KEY, REQUEST_CODE);
        startActivityForResult(i, REQUEST_CODE);
    }

    private void redirectToLogin() {
        Intent i = new Intent(this, green_minds.com.finalproject.activities.UserInfoActivity.class);
        startActivity(i);
    }

    //progress icon setup
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        return super.onPrepareOptionsMenu(menu);
    }

    private void showProgressBar() {
        if(miActionProgressItem !=null) miActionProgressItem.setVisible(true);
    }

    private void hideProgressBar() {
        if(miActionProgressItem !=null) miActionProgressItem.setVisible(false);
    }

    @Override
    public void adjustLoc(Double latitude, Double longitude) {
        lat = latitude;
        lon = longitude;
        ft = getSupportFragmentManager().beginTransaction();
        ft.hide(adjustPinFragment);
        ft.commit();
        adjusted = true;
        btnPin.setVisibility(View.VISIBLE);
        locBtn.setVisibility(View.VISIBLE);
    }


}
