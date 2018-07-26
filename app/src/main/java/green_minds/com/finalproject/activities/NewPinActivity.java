package green_minds.com.finalproject.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.model.Pin;

public class NewPinActivity extends AppCompatActivity {

    @BindView(R.id.btn_camera)
    ImageButton btnCamera;

    @BindView(R.id.btn_pin)
    Button btnPin;

    @BindView(R.id.et_comment)
    EditText etComment;

    @BindView(R.id.iv_preview)
    ImageView ivPreview;

    @BindView(R.id.tv_upload)
    TextView tvUpload;

    @BindView(R.id.rb_categories)
    RadioGroup rbCategories;

    private File currentfile;
    private Context context;
    private ParseUser currentUser;

    final public static String PIN_KEY = "pin";
    final public static String CODE_KEY ="REQUEST_CODE";
    final public static int REQUEST_CODE = 31;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pin);
        ButterKnife.bind(this);
        currentfile = null;
        context = this;

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
        int wid = bmp.getWidth();
        int hei = bmp.getHeight();

        ivPreview.requestLayout();
        wid = ivPreview.getLayoutParams().width * wid/hei;
        ivPreview.getLayoutParams().width = wid;
        ivPreview.setImageBitmap(bmp);

        currentfile = new File(filepath);
        tvUpload.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_pin)
    public void uploadPin() {
        if (currentfile == null) {
            Toast.makeText(this, "Please upload an image first!", Toast.LENGTH_LONG).show();
            return;
        }
        if (rbCategories.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please check a category first!", Toast.LENGTH_LONG).show();
            return;
        }

        Double lat = getIntent().getDoubleExtra("latitude", 0.0);
        Double lon = getIntent().getDoubleExtra("longitude", 0.0);
        ParseGeoPoint location = new ParseGeoPoint(lat, lon);
        savePin(location);
    }

    private void savePin(ParseGeoPoint location){
        final Pin pin = new Pin();

        int radioButtonID = rbCategories.getCheckedRadioButtonId();
        View radioButton = rbCategories.findViewById(radioButtonID);
        final int idx = rbCategories.indexOfChild(radioButton);
        Log.i("IDX", idx + "");

        String comment = etComment.getText().toString();
        pin.setCategory(idx);
        pin.setComment(comment);
        pin.setCheckincount(0);

        pin.setLatLng(location);

        pin.setPhoto(new ParseFile(currentfile));

        pin.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null) e.printStackTrace();
                int currPintcount = currentUser.getInt("pincount");
                currentUser.put("pincount", currPintcount + 1);
                currentUser.put("points", currPintcount * 10);
                currentUser.saveInBackground();

                Toast.makeText(context, "new pin complete!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("id", pin.getObjectId());
                Log.d("NewPinActivity", "new pin id " + (String) pin.getObjectId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @OnClick(R.id.btn_camera)
    public void loadCamera(){
        Intent i = new Intent(this, green_minds.com.finalproject.activities.CameraActivity.class);
        i.putExtra(CODE_KEY, REQUEST_CODE);
        startActivityForResult(i, REQUEST_CODE);
    }

    private void redirectToLogin(){
        Intent i = new Intent(this, green_minds.com.finalproject.activities.UserInfoActivity.class);
        startActivity(i);
    }
}
