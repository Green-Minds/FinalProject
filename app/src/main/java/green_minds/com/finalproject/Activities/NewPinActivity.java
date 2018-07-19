package green_minds.com.finalproject.Activities;

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
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.Model.Pin;
import green_minds.com.finalproject.R;

public class NewPinActivity extends AppCompatActivity {

    @BindView(R.id.btn_camera)
    ImageButton btn_camera;

    @BindView(R.id.btn_pin)
    Button btn_pin;

    @BindView(R.id.et_comment)
    EditText et_comment;

    @BindView(R.id.iv_preview)
    ImageView iv_preview;

    @BindView(R.id.tv_upload)
    TextView tv_upload;

    @BindView(R.id.rb_categories)
    RadioGroup rb_categories;

    private File current_file;
    private Context context;

    final public static String PIN_KEY = "pin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pin);
        ButterKnife.bind(this);
        current_file = null;
        context = this;

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCamera();
            }
        });

        btn_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPin();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bmp = null;
        String filepath = data.getStringExtra("image");
        try {
            bmp = BitmapFactory.decodeFile(filepath);
            int wid = bmp.getWidth();
            int hei = bmp.getHeight();

            iv_preview.requestLayout();
            wid = iv_preview.getLayoutParams().width * wid/hei;
            iv_preview.getLayoutParams().width = wid;
            iv_preview.setImageBitmap(bmp);

            current_file = new File(filepath);
            tv_upload.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadPin(){
        if(current_file == null){
            Toast.makeText(this,"Please upload an image first!", Toast.LENGTH_LONG).show();
            return;
        }
        if(rb_categories.getCheckedRadioButtonId() == -1){
            Toast.makeText(this,"Please check a category first!", Toast.LENGTH_LONG).show();
            return;
        }
        final Pin pin = new Pin();

        int radioButtonID = rb_categories.getCheckedRadioButtonId();
        View radioButton = rb_categories.findViewById(radioButtonID);
        int idx = rb_categories.indexOfChild(radioButton);
        Log.i("IDX", idx + "");

        String comment = et_comment.getText().toString();
        pin.setCategory(idx);
        pin.setComment(comment);
        pin.setCheckincount(0);

        Double lat = getIntent().getDoubleExtra("latitude", 0.0);
        Double lon = getIntent().getDoubleExtra("longitude", 0.0);
        pin.setLatLng(new ParseGeoPoint(lat, lon));

        pin.setPhoto(new ParseFile(current_file));

        pin.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null) e.printStackTrace();
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
}
