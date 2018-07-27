package green_minds.com.finalproject.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.adapters.SchoolAutoCompleteAdapter;
import green_minds.com.finalproject.model.DelayAutoCompleteTextView;

public class SecondSignupActivity extends AppCompatActivity {

    @BindView(R.id.rgSelection)
    public RadioGroup rgSelection;
    @BindView(R.id.rbWork)
    public RadioButton rbWork;
    @BindView(R.id.etCompany)
    public EditText etCompany;
    @BindView(R.id.atvSchoolName)
    public DelayAutoCompleteTextView atvSchoolName;
    @BindView(R.id.btnConnectNext)
    public Button btnConnectNext;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_signup);
        ButterKnife.bind(this);
        intent = getIntent();

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

        atvSchoolName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (atvSchoolName.getText().toString() != null) {
                    btnConnectNext.setEnabled(true);
                }
            }
        });

        rgSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (rbWork.isChecked()) {
                    atvSchoolName.setVisibility(View.GONE);
                    etCompany.setVisibility(View.VISIBLE);
                } else {
                    atvSchoolName.setVisibility(View.VISIBLE);
                    etCompany.setVisibility(View.GONE);
                }
            }
        });

        btnConnectNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String connection = atvSchoolName.getText().toString();
                if (rbWork.isChecked()) connection = etCompany.getText().toString();
                gotoThirdScreen(connection);
            }
        });
    }

    private void gotoThirdScreen(String connection) {
        intent.putExtra("connection", connection);
        intent.setClass(SecondSignupActivity.this, ThirdSignupActivity.class);
        startActivity(intent);
        finish();
    }
}