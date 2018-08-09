package green_minds.com.finalproject.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private ProgressDialog pd;

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

        mUser = ParseUser.getCurrentUser();
        mBeingEdited = getIntent().getBooleanExtra("beingedited", false);

        mGoals = getIntent().getParcelableArrayListExtra("GOALS");

        mCurrentGoal = null;
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
        try {
            doFieldChecks();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mBeingEdited) {
            saveGoal(mCurrentGoal, true);
        } else {
            final Goal goal = new Goal();
            int type = dropdown.getSelectedItemPosition();
            goal.setType(type);
            if (mGoals.contains(goal) && !mBeingEdited) {
                launchOverrideAlert(goal);
            } else {
                saveGoal(goal, false);
            }
        }
    }

    private void saveGoal(final Goal goal, boolean overwrite) {
        showProgressDialog();
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
                hideProgressDialog();
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
        Calendar cToday = Calendar.getInstance();
        Calendar cSelected = Calendar.getInstance();
        cSelected.setTime(mSelectedDate);
        boolean sameDay = cToday.get(Calendar.YEAR) == cSelected.get(Calendar.YEAR) &&
                cToday.get(Calendar.DAY_OF_YEAR) == cSelected.get(Calendar.DAY_OF_YEAR);
        if (sameDay || cToday.after(cSelected)) {
            throw new Exception(getString(R.string.exception_date));
        }

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

    private void launchOverrideAlert(final Goal goal) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setTitle(R.string.alert_dialog_title).setMessage(R.string.alert_dialog_body)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Goal existingGoal = null;
                        for (int i = 0; i < mGoals.size(); i++) {
                            if (mGoals.get(i).equals(goal)) existingGoal = mGoals.get(i);
                        }
                        saveGoal(existingGoal, true);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, getString(R.string.save_canceled), Toast.LENGTH_SHORT).show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    private void showProgressDialog() {
        // Show progress item
        pd = new ProgressDialog(mContext);
        pd.setTitle("Processing...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
    }

    private void hideProgressDialog() {
        // Hide progress item
        pd.dismiss();
    }
}
