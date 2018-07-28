package green_minds.com.finalproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;


public class SignupActivity extends AppCompatActivity {

    @BindView(R.id.etUsernameInput)
    public EditText etUsernameInput;
    @BindView(R.id.etPasswordInput)
    public EditText etPasswordInput;
    @BindView(R.id.etEmailInput)
    public EditText etEmailInput;
    @BindView(R.id.btnInfoNext)
    public Button btnInfoNext;
    @BindView(R.id.tvUsernameTaken)
    public TextView tvUsernameTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvUsernameTaken.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etUsernameInput.getText().toString().length() > 1
                        && etPasswordInput.getText().toString().length() > 1
                        && etEmailInput.getText().toString().length() > 1) {

                    btnInfoNext.setEnabled(true);
                }
            }
        };
        etUsernameInput.addTextChangedListener(textWatcher);
        etPasswordInput.addTextChangedListener(textWatcher);
        etEmailInput.addTextChangedListener(textWatcher);

        btnInfoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmailInput.getText().toString()).matches()) {
                    tvUsernameTaken.setText("Invalid email address");
                    tvUsernameTaken.setVisibility(View.VISIBLE);
                    return;
                }
                btnInfoNext.setEnabled(false);
                ParseQuery usernameQuery = ParseUser.getQuery();
                ParseQuery emailQuery = ParseUser.getQuery();
                usernameQuery.whereEqualTo("username", etUsernameInput.getText().toString());
                emailQuery.whereEqualTo("email", etEmailInput.getText().toString());
                List<ParseQuery<ParseUser>> queries = new ArrayList<>();
                queries.add(usernameQuery);
                queries.add(emailQuery);
                ParseQuery<ParseUser> mainQuery = ParseQuery.or(queries);
                mainQuery.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() == 0) {
                                tvUsernameTaken.setVisibility(View.GONE);
                                String username = etUsernameInput.getText().toString();
                                String password = etPasswordInput.getText().toString();
                                String email = etEmailInput.getText().toString();
                                gotoSecondScreen(username, password, email);
                            } else {
                                btnInfoNext.setEnabled(true);
                                tvUsernameTaken.setText("Account with username/ email already exists");
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

    private void gotoSecondScreen(String username, String password, String email) {
        Intent intent = new Intent(SignupActivity.this, SecondSignupActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }
}


