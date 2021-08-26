package com.cxj170002.asg22;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/* This class is the second activity which displays a question of the quiz along with its answer choices and a fragment
    that can either be the answer button itself or the feedback text along with the next question button. It keeps track
    of how many questions there are and how many the user got correct to send to the show scores activity when there
    are no more questions left
 */


public class TakeQuiz extends AppCompatActivity {

    // variables that will be needed throughout the class
    TextView questionView;
    TextView answer1View;
    TextView answer2View;
    TextView answer3View;
    TextView answer4View;
    protected String playerName;
    protected String name;
    protected String content;
    protected String answer;
    protected String selectedAnswer;
    protected int questionCount;
    protected int correctCount;
    Button btnAnswerButton;
    Button btnNextButton;
    TextView feedbackResponse;
    boolean fragNull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);
        // defining the different textviews and buttons
        questionView = findViewById(R.id.question);
        answer1View = findViewById(R.id.answer1);
        answer2View = findViewById(R.id.answer2);
        answer3View = findViewById(R.id.answer3);
        answer4View = findViewById(R.id.answer4);
        btnAnswerButton = findViewById(R.id.btnAnswerButton);
        btnNextButton = findViewById(R.id.btnNextButton);
        feedbackResponse = findViewById(R.id.feedback);

        // getting and storing the string extras passed through the intent
        name = (String) getIntent().getStringExtra("quizName");
        content = (String) getIntent().getStringExtra("quizContent");
        playerName = (String) getIntent().getStringExtra("userName");
        answer = "";
        selectedAnswer = "";
        fragNull = true;

        setAnsFrag();

    }

    // method to read in the next question from the quiz content
    protected void readQuestion()
    {
        // if there are more questions, continue to read them in
        if(!content.equals(""))
        {
            // increasing the questionc count
            questionCount++;

            // reading in the question and setting it's textview
            String currentLine = content.substring(0,content.indexOf("\n"));
            questionView.setText(currentLine);
            content = content.substring(content.indexOf("\n")+1);

            // reading in answer choice 1 and checking if it's the correct answer and setting it's textview
            currentLine = content.substring(0,content.indexOf("\n"));
            if(currentLine.startsWith("*"))
            {
                currentLine = currentLine.substring(1);
                answer = currentLine;
            }
            answer1View.setText(currentLine);
            content = content.substring(content.indexOf("\n")+1);

            // reading in answer choice 2 and checking if it's the correct answer and setting it's textview
            currentLine = content.substring(0,content.indexOf("\n"));
            if(currentLine.startsWith("*"))
            {
                currentLine = currentLine.substring(1);
                answer = currentLine;
            }
            answer2View.setText(currentLine);
            content = content.substring(content.indexOf("\n")+1);

            // reading in answer choice 3 and checking if it's the correct answer and setting it's textview
            currentLine = content.substring(0,content.indexOf("\n"));
            if(currentLine.startsWith("*"))
            {
                currentLine = currentLine.substring(1);
                answer = currentLine;
            }
            answer3View.setText(currentLine);
            content = content.substring(content.indexOf("\n")+1);

            // reading in answer choice 4 and checking if it's the correct answer and setting it's textview
            currentLine = content.substring(0,content.indexOf("\n"));
            if(currentLine.startsWith("*"))
            {
                currentLine = currentLine.substring(1);
                answer = currentLine;
            }
            answer4View.setText(currentLine);

            // if there are no more questions after the last answer choice, change content to "" so it will go to show scores activity next
            if(content.substring(content.indexOf("\n")).length() <= 1)
            {
                content = "";
            }
            else // if there are more questions, update content so that the next line will be the next question
                content = content.substring(content.indexOf("\n")+1);
        }
        else // if content is empty send it to setAnsFrag which will take it to the show scores activity
        {
            setAnsFrag();
        }

    }

    // method to switch to the feedback fragment
    public void setFeedFrag() {

        // remove answer fragment if it exists
        if(!fragNull)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            AnswerFragment ansFrag = (AnswerFragment) fragmentManager.findFragmentById(R.id.fragment);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(ansFrag).commit();

        }

        // figure out whether answer is correct to pass to the feedback fragment
        String response = "";
        if(answer.equals(selectedAnswer))
        {
            // if answer is correct, say so and increase correct count
            response = "Congrats, that's correct: " + answer;
            correctCount++;
        }
        else
        {
            // if answer is incorrect, say so
            response = "Incorrect, answer was: " + answer;
        }

        // add feedback fragment and switch to it
        FeedbackFragment feedFrag = FeedbackFragment.newInstance(response,"");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment, feedFrag).addToBackStack(null).commit();
        fragNull = false;

    }

    // method to switch to the answer fragment
    public void setAnsFrag() {

        // remove feedback fragment if it exists
        if(!fragNull)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FeedbackFragment feedFrag = (FeedbackFragment) fragmentManager.findFragmentById(R.id.fragment);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(feedFrag).commit();

        }

        // if there are no more questions left, go to the show scores activity
        if(content.equals(""))
        {
            // creating the third activity, the show scores activity, using an intent
            Intent intent = new Intent(this, ShowScores.class);
            // sending the user name and quiz name selected through extras
            intent.putExtra("userName", playerName);
            intent.putExtra("quizName", name);
            // sending the user's score through extras
            intent.putExtra("score", (Integer.toString(correctCount) + "/" + Integer.toString(questionCount)));
            // starts the third activity using intent
            startActivity(intent);
        }
        else {
            // if there are more questions, read the next one and reset the background color for answer choices to white
            readQuestion();
            answer1View.setBackgroundColor(Color.WHITE);
            answer2View.setBackgroundColor(Color.WHITE);
            answer3View.setBackgroundColor(Color.WHITE);
            answer4View.setBackgroundColor(Color.WHITE);
        }

        // add answer fragment and switch to it
        AnswerFragment ansFrag = AnswerFragment.newInstance("","");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment, ansFrag).addToBackStack(null).commit();
        fragNull = false;

    }

    // onClick handler for the answer choice text views
    public void choiceClick(View view) {

        // resetting all answers to white before changing the selected answer to light blue
        answer1View.setBackgroundColor(Color.WHITE);
        answer2View.setBackgroundColor(Color.WHITE);
        answer3View.setBackgroundColor(Color.WHITE);
        answer4View.setBackgroundColor(Color.WHITE);

        // changing the selected answer to light blue
        view.setBackgroundColor(Color.CYAN);

        // setting the selected answer text to the answer selected
        selectedAnswer = (String) ((TextView) view).getText();
    }
}