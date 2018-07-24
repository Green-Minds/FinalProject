package green_minds.com.finalproject.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import green_minds.com.finalproject.R;
import green_minds.com.finalproject.model.PinCategoryHelper;

public class NewGoalFragment extends Fragment {

    public interface OnNewGoalListener {
        void onFragmentInteraction(Uri uri);
    }

    private OnNewGoalListener mListener;
    private Context context;

    public NewGoalFragment() {
    }

    public static NewGoalFragment newInstance() {
        NewGoalFragment fragment = new NewGoalFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Spinner dropdown = view.findViewById(R.id.category_dropdown);
        String[] items = PinCategoryHelper.listOfCategories;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context c) {
        context = c;
        super.onAttach(context);
        if (context instanceof OnNewGoalListener) {
            mListener = (OnNewGoalListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
