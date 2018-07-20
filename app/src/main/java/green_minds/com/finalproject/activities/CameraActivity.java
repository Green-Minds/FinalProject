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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.camerakit.CameraKitView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;

public class CameraActivity extends AppCompatActivity {

    private Context context;

    @BindView(R.id.camera)
    CameraKitView camera;

    @BindView(R.id.btn_takepic)
    Button btnTakepic;

    @BindView(R.id.underlay)
    FrameLayout cameraUnderlay;

    @BindView(R.id.iv_preview)
    ImageView ivPreview;

    @BindView(R.id.overlay)
    RelativeLayout previewOverlay;

    @BindView(R.id.btn_newpic)
    Button btnNewpic;

    @BindView(R.id.btn_upload)
    Button btnUpload;

    private byte[] currentBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        context = this;

        ButterKnife.bind(this);

        if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            Log.i("permissions_camera", Manifest.permission.CAMERA);
            Log.i("permissions_read", Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.i("permissions_write", Manifest.permission.WRITE_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            completeSetup();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    completeSetup();
                } else {
                    Toast.makeText(context, "Please enable permissions for this to work.", Toast.LENGTH_LONG).show();
                    return;
                }
                return;
            }

        }
    }

    private void completeSetup(){
        btnTakepic.setOnClickListener(new View.OnClickListener() {
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
        btnNewpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Write file
                    File output = getOutputMediaFile();

                    OutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(output);
                        outputStream.write(currentBytes);
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

                    //Pop intent
                    Intent intent = new Intent(context, NewPinActivity.class);
                    intent.putExtra("image", output.getAbsolutePath());
                    setResult(RESULT_OK, intent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void launchCamera(){
        currentBytes = null;
        camera.onResume();
        previewOverlay.setVisibility(View.GONE);
        cameraUnderlay.setVisibility(View.VISIBLE);
    }

    private void launchPreview(final CameraKitView cameraKitView, byte[] bytes){

        camera.onPause();

        final Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        currentBytes= bytes;

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
                Environment
                        .getExternalStorageDirectory(),
                "ParsaHam");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

}
