package green_minds.com.finalproject.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import green_minds.com.finalproject.R;
import green_minds.com.finalproject.model.Category;
import green_minds.com.finalproject.model.Goal;
import green_minds.com.finalproject.model.PinCategoryHelper;

public class EditGoalFragment extends Fragment {

    public interface OnEditGoalListener {
        void onNewGoal(Goal goal);
    }

    private OnEditGoalListener mListener;
    private Context context;
    private ArrayList<Goal> goals;
    private ParseUser user;
    private Goal currentGoal;
    private Date selectedDate;
    private boolean beingEdited;

    private static final String ARG_PARAM1 = "goal";
    private static final String ARG_PARAM2 = "goals";

    @BindView(R.id.calendar)
    CalendarView calendar;

    @BindView(R.id.category_dropdown)
    Spinner dropdown;

    @BindView(R.id.tv_type)
    TextView type;

    @BindView(R.id.tv_text)
    TextView description;

    @BindView(R.id.et_numberof)
    EditText numberOf;

    @BindView(R.id.btn_save)
    Button btnSave;

    public EditGoalFragment() {
    }

    public static EditGoalFragment newInstance() {
        EditGoalFragment fragment = new EditGoalFragment();
        return fragment;
    }

    public static EditGoalFragment newInstance(Goal goal, ArrayList<Goal> goals) {
        EditGoalFragment fragment = new EditGoalFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, goal);
        args.putParcelableArrayList(ARG_PARAM2, goals);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = ParseUser.getCurrentUser();
        currentGoal = null;
        beingEdited = false;
        if (getArguments() != null) {
            beingEdited = true;
            currentGoal = getArguments().getParcelable(ARG_PARAM1);
            goals = getArguments().getParcelableArrayList(ARG_PARAM2);
        }
        else {
            //have to do this weird "include goals" query because otherwise before I get anything in a goal obj I need to call "fetchifneeded"
            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            userQuery.include("goals");
            userQuery.whereEqualTo("objectId", user.getObjectId()).findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, com.parse.ParseException e) {
                    //TODO - deal with exceptions
                    Toast.makeText(context, "done", Toast.LENGTH_SHORT).show();
                    user = objects.get(0);
                    goals = (ArrayList<Goal>)user.get("goals");
                    if( goals == null){
                        goals = new ArrayList<>();
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        final Category[] categories = PinCategoryHelper.categories;
        selectedDate = new Date(calendar.getDate());
        int initialPosition = 0;
        if(currentGoal != null){
            initialPosition = currentGoal.getType();
            numberOf.setText(currentGoal.getGoal() + "");
            selectedDate = currentGoal.getDeadline();
            calendar.setDate(selectedDate.getTime());
            btnSave.setText("Save changes!");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, PinCategoryHelper.listOfCategories);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                description.setText(categories[position].getDescription());
                type.setText(categories[position].getUnit());
            }
        });
        dropdown.setSelection(initialPosition);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                selectedDate = new Date(c.getTimeInMillis()); //this is what you want to use later
            }
        });
    }

    @Override
    public void onAttach(Context c) {
        context = c;
        super.onAttach(context);
        if (context instanceof OnEditGoalListener) {
            mListener = (OnEditGoalListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        //user deleted the object on the list page and navigated back. reset the fields
        if(beingEdited && currentGoal == null){
            beingEdited = false;
            dropdown.setSelection(0);
            numberOf.setText(0);
            calendar.setDate(calendar.getDate());
            btnSave.setText("Make new goal!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @OnClick(R.id.btn_save)
    public void save(){
        try {
            doFieldChecks();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        final Goal goal = beingEdited ? currentGoal : new Goal();
        int type = dropdown.getSelectedItemPosition();
        goal.setType(type);

        if(goals.contains(goal) && !beingEdited){

            AlertDialog.Builder builder;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            //TODO - put into strings.xml later
            String title = "Override";
            String message = "You have already set a goal for this category. Do you want to overwrite your old goal?";

            builder.setTitle(title).setMessage(message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Goal existingGoal = null;
                            for(int i = 0; i < goals.size(); i ++){
                                if(goals.get(i).equals(goal)) existingGoal = goals.get(i);
                            }
                            saveGoal(existingGoal, true);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "Save canceled.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        } else {
            saveGoal(goal, beingEdited);
        }
    }

    private void saveGoal(final Goal goal, boolean overwrite){

        int goalNum = Integer.parseInt(numberOf.getText().toString()); //input validity checked earlier
        goal.setDeadline(selectedDate);
        goal.setGoal(goalNum);
        if(!overwrite){
            goals.add(goal);
        }
        user.put("goals", goals);

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if(e != null){
                    e.printStackTrace();
                } else{
                    mListener.onNewGoal(goal);
                }
            }
        });
    }

    private void doFieldChecks() throws Exception{
        int goalNum = 0;
        String num = numberOf.getText().toString();
        Date today = new Date(calendar.getDate());
        Calendar cToday = Calendar.getInstance();
        Calendar cSelected = Calendar.getInstance();
        cSelected.setTime(selectedDate);
        boolean sameDay = cToday.get(Calendar.YEAR) == cSelected.get(Calendar.YEAR) &&
                cToday.get(Calendar.DAY_OF_YEAR) == cSelected.get(Calendar.DAY_OF_YEAR);
        if(sameDay || cToday.after(cSelected)) {
            throw new Exception("Choose a date in the future!");
        }

        if(num.equals("")) throw new Exception("Fill out the goal field!");
        try{
            goalNum = Integer.parseInt(num);
        } catch(Exception e){
            e.printStackTrace();
            if(e instanceof ParseException){
                throw new Exception("Invalid characters in number field!");
            }
        }
        if(goalNum < 1){
            throw new Exception("Your number needs to be greater than 0!");
        }
    }

}
