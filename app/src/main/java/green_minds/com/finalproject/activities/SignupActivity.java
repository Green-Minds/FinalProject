package green_minds.com.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import green_minds.com.finalproject.R;

public class SignupActivity extends AppCompatActivity {

    private final static String API_BASE_URL = "https://api.data.gov/ed/collegescorecard/";
    private AsyncHttpClient client;
    private List<String> schools;
    private ArrayAdapter<String> schoolsAdapter;

    @BindView(R.id.etUsernameInput) public EditText etUsernameInput;
    @BindView(R.id.etPasswordInput) public EditText etPasswordInput;
    @BindView(R.id.btnSignup) public Button btnSignup;
    @BindView(R.id.rgSelection) public RadioGroup rgSelection;
    @BindView(R.id.rbWork) public RadioButton rbWork;
    @BindView(R.id.schoolList) public Spinner schoolList;
    @BindView(R.id.etCompany) public EditText etCompany;
    @BindView(R.id.tvUsernameTaken) public TextView tvUsernameTaken;
    @BindView(R.id.atvSchoolName) public AutoCompleteTextView atvSchoolName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        client = new AsyncHttpClient();
        schools = new ArrayList<String>();
        schoolsAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, schools);
        atvSchoolName.setThreshold(2);
        atvSchoolName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getSchoolList();
                schoolsAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //getSchoolList();


        rgSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(rbWork.isChecked()) {
                    atvSchoolName.setVisibility(View.GONE);
                    etCompany.setVisibility(View.VISIBLE);
                } else {
                    atvSchoolName.setVisibility(View.VISIBLE);
                    etCompany.setVisibility(View.GONE);
                    //getSchoolList();
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
                                String connection = schoolList.getSelectedItem().toString();
                                if (rbWork.isChecked()) connection = etCompany.getText().toString();
                                signUp (username, password, connection);
                            } else {
                                tvUsernameTaken.setText("Username already taken.");
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
    }

    private void getSchoolList() {
        String url = API_BASE_URL + "v1/schools";
        RequestParams params = new RequestParams();
        params.put("api_key", getString(R.string.school_list_api_key));
        params.put("fields", "school.name");
        params.put("page", "5");
        params.put("school.name", atvSchoolName.getText().toString());
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        String schoolName = results.getJSONObject(i).getString("school.name");
                        schools.add(schoolName);
                        //notify adapter that a row was added
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void signUp(String username, String password, String connection) {
        ParseUser user = (ParseUser) ParseUser.create("_User");
        user.setPassword(password);
        user.setUsername(username);
        user.put("connection", connection);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    final Intent intent = new Intent(SignupActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
