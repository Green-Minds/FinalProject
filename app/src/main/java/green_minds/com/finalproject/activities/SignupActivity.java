package green_minds.com.finalproject.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.LoginButton;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.adapters.SchoolAutoCompleteAdapter;
import green_minds.com.finalproject.model.DelayAutoCompleteTextView;


public class SignupActivity extends AppCompatActivity {

    //public final static int RESULT_LOAD_IMAGE = 1;
    @BindView(R.id.etUsernameInput) public EditText etUsernameInput;
    @BindView(R.id.etPasswordInput) public EditText etPasswordInput;
    @BindView(R.id.etEmailInput) public EditText etEmailInput;
    @BindView(R.id.btnSignup) public Button btnSignup;
    @BindView(R.id.rgSelection) public RadioGroup rgSelection;
    @BindView(R.id.rbWork) public RadioButton rbWork;
    @BindView(R.id.etCompany) public EditText etCompany;
    @BindView(R.id.tvUsernameTaken) public TextView tvUsernameTaken;
    @BindView(R.id.atvSchoolName) public DelayAutoCompleteTextView atvSchoolName;
    @BindView(R.id.login_button) public LoginButton fbloginButton;
    public static final List<String> mPermissions = new ArrayList<String>() {{ add("email"); }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        ParseFacebookUtils.initialize(this);

//        if (ParseUser.getCurrentUser() != null) {
//            startActivity(new Intent(this, MapActivity.class));
//            finish();
//        }

        atvSchoolName.setThreshold(2);
        atvSchoolName.setAdapter(new SchoolAutoCompleteAdapter(this));
        atvSchoolName.setLoadingIndicator(
                (android.widget.ProgressBar) findViewById(R.id.pb_loading_indicator));
        atvSchoolName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                String school = (String) parent.getItemAtPosition(position);
                atvSchoolName.setText(school);
            }
        });


        rgSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(rbWork.isChecked()) {
                    atvSchoolName.setVisibility(View.GONE);
                    etCompany.setVisibility(View.VISIBLE);
                } else {
                    atvSchoolName.setVisibility(View.VISIBLE);
                    etCompany.setVisibility(View.GONE);
                }
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ParseQuery query = ParseUser.getQuery();
                query.whereEqualTo("username", etUsernameInput.getText().toString()).findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            if(objects.size() == 0){
                                tvUsernameTaken.setVisibility(View.GONE);
                                String username = etUsernameInput.getText().toString();
                                String password = etPasswordInput.getText().toString();
                                String connection = atvSchoolName.getText().toString();
                                String email = etEmailInput.getText().toString();
                                if (rbWork.isChecked()) connection = etCompany.getText().toString();
                                btnSignup.setEnabled(false);
                                Toast.makeText(getApplicationContext(),"Creating user", Toast.LENGTH_SHORT).show();
                                signUp (username, password, connection, email);
                            } else {
                                tvUsernameTaken.setText("Username already exists.");
                                tvUsernameTaken.setVisibility(View.VISIBLE);
                                return;
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        fbloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(SignupActivity.this, mPermissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                            getUserDetailFromFB();
                        } else {
                            Log.d("MyApp", "User logged in through Facebook!");
                            getUserDetailsFromParse();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    void getUserDetailFromFB(){
         // Suggested by https://disqus.com/by/dominiquecanlas/
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name");


        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {

                            String email = response.getJSONObject().getString("email");
                            String name = response.getJSONObject().getString("name");
                            String password = etPasswordInput.getText().toString();
                            String connection = atvSchoolName.getText().toString();
                            etUsernameInput.setText(name);
                            etEmailInput.setText(email);
                            signUp(name, password, connection, email);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    private void getUserDetailsFromParse() {
        ParseUser user = ParseUser.getCurrentUser();
        etUsernameInput.setText(user.getUsername());

        Toast.makeText(SignupActivity.this, "Welcome back " + etUsernameInput.getText().toString(), Toast.LENGTH_SHORT).show();

    }
//
//    void getUserDetailFromParse(){
//        ParseUser user = ParseUser.getCurrentUser();
//        etUsernameInput.setText(user.getUsername());
//        Toast.makeText(getApplicationContext(), "Welcome Back " + etUsernameInput.getText().toString(), Toast.LENGTH_SHORT).show();
//        //alertDisplayer("Welcome Back","User:"+t_username.getText().toString()+" Login.Email:"+t_email.getText().toString());
//
//    }

    private void signUp(String username, String password, String connection, String email) {
        ParseUser user = (ParseUser) ParseUser.create("_User");
        user.setPassword(password);
        user.setUsername(username);
        user.setEmail(email);
        user.put("connection", connection);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "Welcome " + etUsernameInput.getText().toString(), Toast.LENGTH_SHORT).show();
                    final Intent intent = new Intent(SignupActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
//
//    public void addImage(View v) {
//        Intent i = new Intent(
//                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//        startActivityForResult(i, RESULT_LOAD_IMAGE);
//    }
}
