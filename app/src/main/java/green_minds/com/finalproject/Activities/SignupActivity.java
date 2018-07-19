package green_minds.com.finalproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;

public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.etUsernameInput) public EditText etUsernameInput;
    @BindView(R.id.etPasswordInput) public EditText etPasswordInput;
    @BindView(R.id.btnSignup) public Button btnSignup;
    @BindView(R.id.rgSelection) public RadioGroup rgSelection;
    @BindView(R.id.rbWork) public RadioButton rbWork;
    @BindView(R.id.schoolList) public Spinner schoolList;
    @BindView(R.id.etCompany) public EditText etCompany;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        schoolList.setPrompt("Select school");

        List<String> schools = new ArrayList<String>();
        schools.add("Carnegie Mellon University");
        schools.add("California Institute of Technology");
        schools.add("Howard University");

        ArrayAdapter<String> schoolsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, schools);
        schoolsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        schoolList.setAdapter(schoolsAdapter);

        rgSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(rbWork.isChecked()) {
                    schoolList.setVisibility(View.GONE);
                    etCompany.setVisibility(View.VISIBLE);
                }
                else {
                    schoolList.setVisibility(View.VISIBLE);
                    etCompany.setVisibility(View.GONE);
                    getSchoolList();
                }
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsernameInput.getText().toString();
                String password = etPasswordInput.getText().toString();
                String connection = schoolList.getSelectedItem().toString();
                if (rbWork.isChecked()) connection = etCompany.getText().toString();
                signUp (username, password, connection);
            }
        });
    }

    private void getSchoolList() {
        schoolList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
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
                }
                else {
                    e.printStackTrace();
                }
            }
        });
    }
}
