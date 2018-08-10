package green_minds.com.finalproject.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.utils.Utils;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.fragments.AdjustPinFragment;
import green_minds.com.finalproject.model.GlideApp;
import green_minds.com.finalproject.model.ImageHelper;
import green_minds.com.finalproject.model.Pin;

import static android.view.View.GONE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class NewPinActivity extends AppCompatActivity implements AdjustPinFragment.OnFragmentInteractionListener {

    @BindView(R.id.btn_camera)
    ImageButton btnCamera;

    @BindView(R.id.mapBtn)
    Button mapBtn;

    @BindView(R.id.et_comment)
    EditText etComment;

    @BindView(R.id.iv_preview)
    ImageView ivPreview;

    @BindView(R.id.rb_categories)
    RadioGroup rbCategories;

    @BindView(R.id.my_toolbar)
    Toolbar myToolbar;

    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;

    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;

    @BindView(R.id.tv_error)
    TextView tvError;

    @BindView(R.id.btn_adjust)
    FloatingActionButton btnAdjust;

    private Bitmap mCurrentBitmap;
    private Context context;
    private ParseUser currentUser;
    private MenuItem miActionProgressItem;
    private boolean saving;
    private boolean mMapMode;
    private boolean adjusted = false;

    private SweetAlertDialog pDialog;

    final public static String CODE_KEY = "REQUEST_CODE";
    final public static int REQUEST_CODE = 31;

    private AdjustPinFragment adjustPinFragment;
    private FragmentTransaction ft;

    private Double lat;
    private Double lon;
    private MenuItem m;

    //private SweetAlertDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pin);
        ButterKnife.bind(this);
        mCurrentBitmap = null;
        context = this;
        saving = false;
        mMapMode = false;

        rbCategories.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                tvError.setVisibility(View.GONE);
            }
        });

        btnAdjust.setVisibility(GONE);

        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar a = getSupportActionBar();
        // Enable the Up button
        a.setDisplayHomeAsUpEnabled(true);
        a.setTitle("New pin");

        if (ParseUser.getCurrentUser() == null) {
            redirectToLogin();
        } else {
            currentUser = ParseUser.getCurrentUser();
        }

        lat = getIntent().getDoubleExtra("latitude", 0.0);
        lon = getIntent().getDoubleExtra("longitude", 0.0);
        ParseGeoPoint location = new ParseGeoPoint(lat, lon);

        adjustPinFragment = adjustPinFragment.newInstance(lat, lon);
        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, adjustPinFragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_w_checkmark, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        m = menu.findItem(R.id.miConfirm);
        m.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                uploadPin();
                return true;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && data != null){
            tvError.setVisibility(View.GONE);
            Bitmap bmp = null;
            String filepath = data.getStringExtra("image");
            bmp = BitmapFactory.decodeFile(filepath);
            GlideApp.with(getApplicationContext())
                    .load(bmp)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(ivPreview);

            mCurrentBitmap = bmp;
        }
    }


    @OnClick(R.id.mapBtn)
    public void onLocBtn() {
        ViewGroup parent = (ViewGroup) fragmentContainer.getParent();
        parent.removeView(fragmentContainer);

        ActionBar a = getSupportActionBar();
        a.setTitle("Adjust your pin");

        fragmentContainer.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        ViewGroup newParent = findViewById(R.id.mainLayout);
        newParent.addView(fragmentContainer);

        btnAdjust.setVisibility(View.VISIBLE);

        adjustPinFragment.enableGestures(true);
        m.setVisible(false);
        mapBtn.setVisibility(GONE);
        mMapMode = true;
        a.setDisplayHomeAsUpEnabled(false);


    }

    @OnClick(R.id.btn_adjust)
    public void onAdjustBtn() {

        ActionBar a = getSupportActionBar();
        a.setTitle("Please describe your new pin");
        a.setDisplayHomeAsUpEnabled(true);

        btnAdjust.setVisibility(View.GONE);
        ViewGroup parent = (ViewGroup) fragmentContainer.getParent();
        parent.removeView(fragmentContainer);

        adjustPinFragment.enableGestures(false);
        Utils.init(this);
        fragmentContainer.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, (int) Utils.convertDpToPixel(165)));
        ViewGroup newParent = findViewById(R.id.insideLayout);
        newParent.addView(fragmentContainer);
        m.setVisible(true);
        mapBtn.setVisibility(View.VISIBLE);
        mMapMode = false;

    }

    public void uploadPin() {
        if (mCurrentBitmap == null) {
            tvError.setText("Please upload an image first!");
            tvError.setVisibility(View.VISIBLE);
            return;
        }
        if (rbCategories.getCheckedRadioButtonId() == -1) {
            tvError.setText("Please check a category first!");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        adjustPinFragment.onAdjustClick();
        savePin(new ParseGeoPoint(lat, lon));

    }


    private Fragment recreateFragment(Fragment f) {
        try {
            Fragment.SavedState savedState = getSupportFragmentManager().saveFragmentInstanceState(f);

            Fragment newInstance = f.getClass().newInstance();
            newInstance.setInitialSavedState(savedState);

            return newInstance;
        } catch (Exception e) // InstantiationException, IllegalAccessException
        {
            throw new RuntimeException("Cannot reinstantiate fragment " + f.getClass().getName(), e);
        }
    }

    private void savePin(ParseGeoPoint location) {
        showLoading();
        saving = true;
        final Pin pin = new Pin();

        int radioButtonID = rbCategories.getCheckedRadioButtonId();
        View radioButton = rbCategories.findViewById(radioButtonID);
        final int idx = rbCategories.indexOfChild(radioButton);

        String comment = etComment.getText().toString();
        pin.setCategory(idx);
        pin.setComment(comment);
        pin.setCheckincount(0);
        pin.setLatLng(location);

        final ParseFile photo = ImageHelper.getParseFile(mCurrentBitmap);
        final ParseFile smallerPhoto = ImageHelper.getSmallerParseFile(mCurrentBitmap);
        photo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    hideLoading();
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
                    hideLoading();
                    Toast.makeText(context, getString(R.string.pin_save_failure), Toast.LENGTH_SHORT).show();
                    //maybe use saveEventually here
                } else {
                    savePinToUser(pin);
                }
            }
        });
    }

    private void savePinToUser(final Pin pin) {

        currentUser.increment("pincount");
        currentUser.increment("points", 10);

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                hideLoading();
                if (e != null) {
                    e.printStackTrace();
                    Toast.makeText(context, "USER" + getString(R.string.pin_save_failure), Toast.LENGTH_SHORT).show();
                } else {
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
        if (saving) return;
        Intent i = new Intent(this, green_minds.com.finalproject.activities.CameraActivity.class);
        i.putExtra(CODE_KEY, REQUEST_CODE);
        startActivityForResult(i, REQUEST_CODE);
    }

    private void redirectToLogin() {
        Intent i = new Intent(this, green_minds.com.finalproject.activities.UserInfoActivity.class);
        startActivity(i);
    }

    @Override
    public void adjustLoc(Double latitude, Double longitude) {
        lat = latitude;
        lon = longitude;
        adjusted = true;
        // ActionBar a = getSupportActionBar();
        // a.setTitle("Please describe your new pin");
    }

    private void showLoading(){
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Saving new pin, please wait.");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void hideLoading(){
        if(pDialog !=null) pDialog.dismiss();
    }


}
