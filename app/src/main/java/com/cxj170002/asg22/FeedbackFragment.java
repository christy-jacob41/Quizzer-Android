package com.cxj170002.asg22;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * This class is a fragment class that holds the feedback text which gives the user
 * feedback on their selected answer and the next question button which, if clicked, either
 * takes the user to the next question or takes them to the show score activity if there are
 * no more questions
 */
public class FeedbackFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedbackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedbackFragment newInstance(String param1, String param2) {
        FeedbackFragment fragment = new FeedbackFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getting the string parameter that was passed through the fragment
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
        // initializing and defining the next question button
        Button btnNextButton = (Button) rootView.findViewById(R.id.btnNextButton);
        // intializing and defining the feedback textview to the feedback that was passed through the fragment
        TextView feedbackResponse = (TextView) rootView.findViewById(R.id.feedback);
        feedbackResponse.setText(mParam1);
        // setting the on click listener for the next question button
        btnNextButton.setOnClickListener(new ButtonListener());
        return rootView;
    }

    private class ButtonListener implements Button.OnClickListener
    {
        @Override
        // on click listener for the next question button
        public void onClick(View v)
        {
            ((TakeQuiz) getActivity()).setAnsFrag();
        }

    }

}