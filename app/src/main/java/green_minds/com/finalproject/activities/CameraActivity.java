package green_minds.com.finalproject.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.camerakit.CameraKitView;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.model.ImageHelper;

public class CameraActivity extends AppCompatActivity {

    private Context context;

    @BindView(R.id.camera)
    CameraKitView camera;

    @BindView(R.id.underlay)
    FrameLayout cameraUnderlay;

    @BindView(R.id.iv_preview)
    CropImageView ivPreview;

    @BindView(R.id.overlay)
    RelativeLayout previewOverlay;

    private byte[] currentBytes;
    private int requestCode;
    private Class parentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        context = this;
        requestCode = getIntent().getIntExtra("REQUEST_CODE", 0);

        try {
            switch (requestCode) {
                case 3:
                    parentActivity = ThirdSignupActivity.class;
                    break;
                case 31:
                    parentActivity = NewPinActivity.class;
                    break;
                case 32:
                    parentActivity = EditProfileActivity.class;
                    break;
                default:
                    parentActivity = null;
                    throw new Exception("invalid request code.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!(parentActivity == NewPinActivity.class)) {
            ivPreview.setAspectRatio(1, 1);
            ivPreview.setFixedAspectRatio(true);
            ivPreview.setCropShape(CropImageView.CropShape.OVAL);
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Please enable permissions for this to work.", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.btn_takepic)
    public void takePicture() {
        camera.captureImage(new CameraKitView.ImageCallback() {
            @Override
            public void onImage(CameraKitView cameraKitView, byte[] bytes) {
                launchPreview(cameraKitView, bytes);
            }
        });
    }

    @OnClick(R.id.btn_upload)
    public void upload() {

        Bitmap cropped = ivPreview.getCroppedImage();
        byte[] bytes = ImageHelper.getByteArray(cropped, cropped.getWidth(), 100);

        File output = getOutputMediaFile();
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(output);
            outputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent(context, parentActivity);
        intent.putExtra("image", output.getAbsolutePath());
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.btn_newpic)
    public void launchCamera() {
        currentBytes = null;
        camera.onResume();
        previewOverlay.setVisibility(View.GONE);
        cameraUnderlay.setVisibility(View.VISIBLE);
    }

    private void launchPreview(final CameraKitView cameraKitView, byte[] bytes) {

        camera.onPause();

        final Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        currentBytes = bytes;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivPreview.setImageBitmap(bm);
                cameraUnderlay.setVisibility(View.GONE);
                previewOverlay.setVisibility(View.VISIBLE);

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

    private File getOutputMediaFile() {

        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(), "ParsaHam");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Green App", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
