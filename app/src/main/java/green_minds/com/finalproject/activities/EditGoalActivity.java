package green_minds.com.finalproject.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.model.CategoryHelper;
import green_minds.com.finalproject.model.Goal;

public class EditGoalActivity extends AppCompatActivity {

    private ArrayList<Goal> mGoals;
    private Context mContext;
    private ParseUser mUser;
    private Goal mCurrentGoal;
    private ActionBar mActionBar;
    private Date mSelectedDate;
    private boolean mBeingEdited;
    private boolean performingOverwrite;

    @BindView(R.id.calendar)
    CalendarView calendar;

    @BindView(R.id.category_dropdown)
    Spinner dropdown;

    @BindView(R.id.tv_type)
    TextView type;

    @BindView(R.id.tv_text)
    TextView description;

    @BindView(R.id.divider)
    View divider;

    @BindView(R.id.et_numberof)
    EditText numberOf;

    @BindView(R.id.btn_save)
    Button btnSave;

    @BindView(R.id.tv_error)
    TextView tvError;

    @BindView(R.id.tv_date_error)
    TextView tvDateError;

    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goal);
        mContext = this;
        ButterKnife.bind(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        // Get a support ActionBar corresponding to this toolbar
        mActionBar = getSupportActionBar();
        // Enable the Up button
        mActionBar.setDisplayHomeAsUpEnabled(true);
        numberOf.addTextChangedListener(textWatcher);

        mUser = ParseUser.getCurrentUser();
        mBeingEdited = getIntent().getBooleanExtra("beingedited", false);

        mGoals = getIntent().getParcelableArrayListExtra("GOALS");

        mCurrentGoal = null;
        performingOverwrite = false;
        //set values appropriately if editing goal and not making new one
        if (mBeingEdited) {
            int pos = getIntent().getIntExtra("GOAL", 0);
            mCurrentGoal = mGoals.get(pos);
            int position = mCurrentGoal.getType();
            numberOf.setText(mCurrentGoal.getGoal() + "");
            numberOf.setSelection(numberOf.getText().length());
            mSelectedDate = mCurrentGoal.getDeadline();
            calendar.setDate(mSelectedDate.getTime());
            btnSave.setText(R.string.button_save_text);
            description.setText(CategoryHelper.categories[position].getDescription());
            type.setText(CategoryHelper.categories[position].getUnit());
            String title = getString(R.string.edit_goal_title) + " " + CategoryHelper.getPinIdentifier(position);
            mActionBar.setTitle(title);
            //dropdown is turned off - user shouldn't be able to change the category when they're editing the goal.
            dropdown.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        } else {
            //else make the dropdown menu visible and init fields to default
            initFields();
        }
        //set up calendar listener
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                tvDateError.setVisibility(View.GONE);
                c.set(year, month, dayOfMonth);
                mSelectedDate = new Date(c.getTimeInMillis());
            }
        });
    }

    private void initFields() {
        mActionBar.setTitle("New Goal");
        btnSave.setText(R.string.button_new_text);
        mSelectedDate = new Date(calendar.getDate());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CategoryHelper.listOfCategories);
        dropdown.setVisibility(View.VISIBLE);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                description.setText(CategoryHelper.categories[position].getDescription());
                type.setText(CategoryHelper.categories[position].getUnit());
            }
        });
        dropdown.setSelection(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        //might need this???? needed in fragment, not sure if needed here
        //user deleted the object on the list page and navigated back. reset the fields
        if (mBeingEdited && mCurrentGoal == null) {
            Toast.makeText(this, "Resetting fields", Toast.LENGTH_SHORT).show();
            mBeingEdited = false;
            initFields();
            numberOf.setText(0);
            calendar.setDate(calendar.getDate());
        }
    }

    @OnClick(R.id.btn_save)
    public void save() {
        //check if fields are all valid

        Calendar cToday = Calendar.getInstance();
        Calendar cSelected = Calendar.getInstance();
        cSelected.setTime(mSelectedDate);
        boolean sameDay = cToday.get(Calendar.YEAR) == cSelected.get(Calendar.YEAR) &&
                cToday.get(Calendar.DAY_OF_YEAR) == cSelected.get(Calendar.DAY_OF_YEAR);
        if (sameDay || cToday.after(cSelected)) {
            tvDateError.setVisibility(View.VISIBLE);
            return;
        }

        try {
            doFieldChecks();
        } catch (Exception e) {
            tvError.setText(e.getMessage());
            tvError.setVisibility(View.VISIBLE);
            return;
        }
        if (mBeingEdited) {
            saveGoal(mCurrentGoal, true);
        } else {
            final Goal goal = new Goal();
            int type = dropdown.getSelectedItemPosition();
            goal.setType(type);
            if (mGoals.contains(goal) && !mBeingEdited) {
                //launchOverrideAlert(goal);
                showOverwrite(goal);
            } else {
                saveGoal(goal, false);
            }
        }
    }

    private void saveGoal(final Goal goal, boolean overwrite) {
        if (!performingOverwrite) showLoading();

        int goalNum = Integer.parseInt(numberOf.getText().toString()); //input validity checked earlier
        goal.setDeadline(mSelectedDate);
        goal.setGoal(goalNum);
        //if being overwritten, current goal already exists in mGoals & pointer is the same
        if (!overwrite) {
            //if NOT being overwritten, add to array bc it's new
            mGoals.add(goal);
        }

        mUser.put("goals", mGoals);

        mUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                hideLoading();
                if (e != null) {
                    e.printStackTrace();
                    if (e.getCode() == com.parse.ParseException.CONNECTION_FAILED) {
                        Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, R.string.misc_error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    onNewGoal(goal);
                }
            }
        });
    }

    void onNewGoal(Goal goal) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("GOALS", mGoals);
        setResult(RESULT_OK, i);
        finish();
    }

    private void doFieldChecks() throws Exception {
        int goalNum = 0;
        String num = numberOf.getText().toString();

        if (num.isEmpty()) throw new Exception(getString(R.string.exception_empty_string));
        try {
            goalNum = Integer.parseInt(num);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ParseException) {
                throw new Exception(getString(R.string.exception_badchar));
            }
        }
        if (goalNum < 1) {
            throw new Exception(getString(R.string.exception_zero));
        }
    }

    private void showLoading() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Saving New Goal!");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void showOverwrite(final Goal goal) {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText("Overwrite existing goal?")
                .setContentText("You already have a goal with this category.")
                .setConfirmText("Confirm")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        performingOverwrite = true;
                        sDialog.setTitleText("Saving new goal!")
                                .setConfirmText(null)
                                .setContentText("")
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        sDialog.setCancelable(false);
                        Goal existingGoal = null;
                        for (int i = 0; i < mGoals.size(); i++) {
                            if (mGoals.get(i).equals(goal)) existingGoal = mGoals.get(i);
                        }
                        saveGoal(existingGoal, true);
                    }
                })
                .setCancelText("Cancel")
                .showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                 })
                .show();
    }

    private void hideLoading() {
        performingOverwrite = false;
        pDialog.dismiss();
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            tvError.setVisibility(View.GONE);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
}
