package green_minds.com.finalproject.Activities;

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

import java.io.FileInputStream;

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

    private Bitmap current_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pin);
        ButterKnife.bind(this);
        current_image = null;

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
        String filename = data.getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            int wid = bmp.getWidth();
            int hei = bmp.getHeight();

            iv_preview.requestLayout();
            wid = iv_preview.getLayoutParams().width * wid/hei;
            iv_preview.getLayoutParams().width = wid;
            iv_preview.setImageBitmap(bmp);

            current_image = bmp;
            tv_upload.setVisibility(View.GONE);

            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadPin(){
        if(current_image == null){
            Toast.makeText(this,"Please upload an image first!", Toast.LENGTH_LONG).show();
            return;
        }
        if(rb_categories.getCheckedRadioButtonId() == -1){
            Toast.makeText(this,"Please check a category first!", Toast.LENGTH_LONG).show();
            return;
        }
        Pin pin = new Pin();

        int radioButtonID = rb_categories.getCheckedRadioButtonId();
        View radioButton = rb_categories.findViewById(radioButtonID);
        int idx = rb_categories.indexOfChild(radioButton);
        Log.i("IDX", idx + "");

        //Toast.makeText(this, idx, Toast.LENGTH_LONG).show();
        String comment = et_comment.getText().toString();
        pin.setCategory(idx);
        pin.setComment(comment);
        //pin.setPhoto();
    }

    private void loadCamera(){
        Intent i = new Intent(this, CameraActivity.class);
        startActivityForResult(i, 30);
    }
}
