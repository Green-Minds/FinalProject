package green_minds.com.finalproject.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.model.GlideApp;
import green_minds.com.finalproject.model.ImageHelper;

public class EditProfileActivity extends AppCompatActivity {
    public final static int PICK_PHOTO_CODE = 1046;

    private ParseFile mNewPic;
    private ParseFile mSmallerNewPic;
    private ParseUser mUser;
    private Context mContext;
    private MenuItem miActionProgressItem;

    @BindView(R.id.tv_username)
    EditText etUsername;

    @BindView(R.id.img_prof_pic)
    ImageView ivProfPic;

    @BindView(R.id.btn_edit_pic)
    Button btnEditPic;

    @BindView(R.id.btn_save)
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mNewPic = null;
        mUser = ParseUser.getCurrentUser();
        mContext = this;
        ButterKnife.bind(this);
        Object username = mUser.get("original_username"); //check if exists first
        if (username != null) {
            etUsername.setText((String) username);
        } else {
            etUsername.setText(mUser.getUsername());
        }

        ParseFile photo = mUser.getParseFile("photo");
        if (photo == null) {
            GlideApp.with(mContext).load(R.drawable.anon).circleCrop().into(ivProfPic);
        } else {
            String url = photo.getUrl();
            GlideApp.with(mContext).load(url).circleCrop().placeholder(R.drawable.anon).into(ivProfPic);
        }
    }

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

    @OnClick({R.id.btn_save})
    public void save() {
        showProgressBar();
        checkUsernameFirst(etUsername.getText().toString().toLowerCase());
    }

    private void checkUsernameFirst(String username) {

        if (username.equals(mUser.getUsername().toLowerCase())) {
            saveChanges();
        } else if (etUsername.getText().toString().isEmpty()) {
            Toast.makeText(mContext, getString(R.string.username_empty), Toast.LENGTH_SHORT).show();
            return;
        } else {
            ParseQuery query = ParseUser.getQuery();
            query.whereEqualTo("username", username).findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    hideProgressBar();
                    if (e == null) {
                        if (objects.size() == 0) {
                            saveChanges();
                        } else {
                            Toast.makeText(mContext, getString(R.string.username_exists), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        Toast.makeText(mContext, getString(R.string.misc_error), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void saveChanges() {
        mUser.setUsername(etUsername.getText().toString().toLowerCase());
        mUser.put("original_username", etUsername.getText().toString());
        if (mNewPic != null) {
            mNewPic.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        mSmallerNewPic.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    mUser.put("photo", mNewPic);
                                    mUser.put("smaller_photo", mSmallerNewPic);
                                    saveUser();
                                } else {
                                    e.printStackTrace();
                                    Toast.makeText(mContext, "Error saving. Try again later.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Error saving. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            saveUser();
        }
    }

    private void saveUser() {
        mUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                    Toast.makeText(mContext, getString(R.string.misc_error), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, getString(R.string.updated_alert), Toast.LENGTH_SHORT).show();
                    goBackToProfile();
                }
            }
        });
    }


    @OnClick(R.id.btn_cancel)
    public void cancel() {
        if (mNewPic != null || !(etUsername.getText().toString().equals(mUser.getUsername()))) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(mContext);
            }
            builder.setTitle(R.string.discard_changes_title).setMessage(R.string.discard_changes_body)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            goBackToProfile();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            goBackToProfile();
        }
    }

    @OnClick({R.id.btn_edit_pic})
    public void onPickPhoto() {

        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(EditProfileActivity.this, CameraActivity.class)
                .putExtra("REQUEST_CODE", 32);

        String pickTitle = "Take picture or upload from device"; // Or get from strings.xml
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

        startActivityForResult(chooserIntent, PICK_PHOTO_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_CODE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = null;
            if (data.getData() != null) {
                Uri photoUri = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                } catch (IOException e) {
                    Toast.makeText(mContext, "Error. Failed to get image from gallery. Please try again later.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
                int rotation = getRotationFromMediaStore(this, photoUri);
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
            } else {
                imageBitmap = BitmapFactory.decodeFile(data.getStringExtra("image"));
            }

            GlideApp.with(getApplicationContext())
                    .load(imageBitmap)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(ivProfPic);

            mNewPic = ImageHelper.getParseFile(imageBitmap);
            mSmallerNewPic = ImageHelper.getSmallerParseFile(imageBitmap);
        }
    }

    //https://stackoverflow.com/questions/21085105/get-orientation-of-image-from-mediastore-images-media-data/30572852
    public static int getRotationFromMediaStore(Context context, Uri imageUri) {
        String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION};
        Cursor cursor = context.getContentResolver().query(imageUri, columns, null, null, null);
        if (cursor == null) return 0;

        cursor.moveToFirst();

        int orientationColumnIndex = cursor.getColumnIndex(columns[1]);
        return cursor.getInt(orientationColumnIndex);
    }

    private void showProgressBar() {
        if (miActionProgressItem != null) miActionProgressItem.setVisible(true);
    }

    private void hideProgressBar() {
        if (miActionProgressItem != null) miActionProgressItem.setVisible(false);
    }

    private void goBackToProfile() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        cancel();
    }
}
