package green_minds.com.finalproject.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.camerakit.CameraKitView;

import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;

public class CameraActivity extends AppCompatActivity {

    private Context context;

    @BindView(R.id.camera)
    CameraKitView camera;

    @BindView(R.id.btn_takepic)
    Button btn_takepic;

    @BindView(R.id.underlay)
    FrameLayout camera_underlay;

    @BindView(R.id.iv_preview)
    ImageView iv_preview;

    @BindView(R.id.overlay)
    RelativeLayout preview_overlay;

    @BindView(R.id.btn_newpic)
    Button btn_newpic;

    @BindView(R.id.btn_upload)
    Button btn_upload;

    private Bitmap current_picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        context = this;
        current_picture = null;

        ButterKnife.bind(this);

        btn_takepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, byte[] bytes) {
                        launchPreview(cameraKitView, bytes);
                    }
                });
            }
        });
        btn_newpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Write file
                    String filename = "bitmap.png";
                    FileOutputStream stream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                    current_picture.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    //Cleanup
                    stream.close();
                    current_picture.recycle();

                    //Pop intent
                    Intent intent = new Intent(context, NewPinActivity.class);
                    intent.putExtra("image", filename);
                    setResult(RESULT_OK, intent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void launchCamera(){
        current_picture = null;
        camera.onResume();
        preview_overlay.setVisibility(View.GONE);
        camera_underlay.setVisibility(View.VISIBLE);
    }

    private void launchPreview(final CameraKitView cameraKitView, byte[] bytes){

        camera.onPause();

        final Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        current_picture = bm;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv_preview.setImageBitmap(bm);
                camera_underlay.setVisibility(View.GONE);
                preview_overlay.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        camera.onResume();
    }

    @Override
    protected void onPause() {
        camera.onPause();
        super.onPause();
    }

}
