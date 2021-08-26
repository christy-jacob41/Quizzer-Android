package com.cxj170002.asg22;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/* This class is the third activity that shows the name of the player, the name of the quiz, and the player's score.
    It also has a return home button that allows the user to return to the main activity.
 */

public class ShowScores extends AppCompatActivity {

    // textview to hold the user's name, quiz name, and score
    TextView userName;
    TextView quizName;
    TextView scores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_scores);
        // defining the textviews of username, quizname, and score
        userName = findViewById(R.id.playerName);
        quizName = findViewById(R.id.quizName);
        scores = findViewById(R.id.score);

        // getting the string extras passed through the intent of player name, quiz name, and score
        String playerName = (String) getIntent().getStringExtra("userName");
        String nameQuiz = (String) getIntent().getStringExtra("quizName");
        String score = (String) getIntent().getStringExtra("score");

        // setting the text of the textviews to the appropriate string that was passed
        userName.setText(playerName);
        quizName.setText(nameQuiz);
        scores.setText(score);
    }

    public void btnReturnHome(View view) {
        // using intent to return to the main activity
        Intent intent = new Intent(this, MainActivity.class);
        // starting the main activity
        startActivity(intent);
    }
}