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
import green_minds.com.finalproject.model.Pin;
import green_minds.com.finalproject.R;

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

    private File currentFile;
    private Context context;
    private ParseUser currentUser;

    final public static String PIN_KEY = "pin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pin);
        ButterKnife.bind(this);
        currentFile = null;
        context = this;

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCamera();
            }
        });

        btnPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPin();
            }
        });

        if( ParseUser.getCurrentUser() == null){
            redirectToLogin();
        } else{
            currentUser = ParseUser.getCurrentUser();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bmp = null;
        String filepath = data.getStringExtra("image");
        try {
            bmp = BitmapFactory.decodeFile(filepath);
            int wid = bmp.getWidth();
            int hei = bmp.getHeight();

            ivPreview.requestLayout();
            wid = ivPreview.getLayoutParams().width * wid/hei;
            ivPreview.getLayoutParams().width = wid;
            ivPreview.setImageBitmap(bmp);

            currentFile = new File(filepath);
            tvUpload.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadPin(){
        if(currentFile == null){
            Toast.makeText(this,"Please upload an image first!", Toast.LENGTH_LONG).show();
            return;
        }
        if(rbCategories.getCheckedRadioButtonId() == -1){
            Toast.makeText(this,"Please check a category first!", Toast.LENGTH_LONG).show();
            return;
        }
        final Pin pin = new Pin();

        int radioButtonID = rbCategories.getCheckedRadioButtonId();
        View radioButton = rbCategories.findViewById(radioButtonID);
        final int idx = rbCategories.indexOfChild(radioButton);
        Log.i("IDX", idx + "");

        String comment = etComment.getText().toString();
        pin.setCategory(idx);
        pin.setComment(comment);
        pin.setCheckincount(0);

        Double lat = getIntent().getDoubleExtra("latitude", 0.0);
        Double lon = getIntent().getDoubleExtra("longitude", 0.0);
        pin.setLatLng(new ParseGeoPoint(lat, lon));

        pin.setPhoto(new ParseFile(currentFile));

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

    private void loadCamera(){
        Intent i = new Intent(this, CameraActivity.class);
        startActivityForResult(i, 30);
    }

    private void redirectToLogin(){
        Intent i = new Intent(this, UserInfoActivity.class);
        startActivity(i);
    }
}
