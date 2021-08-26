package com.cxj170002.asg22;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/*
This class is activity that is started when the edit quiz or create quiz button in the main class is clicked.
If editing a quiz, it takes a previously existing quiz and allows the user to modify it.
If creating a quiz, it creates a new quiz and stores it.
 */

public class ProfessorActivity extends AppCompatActivity {

    // variables that will be needed throughout the class
    RecyclerView questionList;
    RecycleAdapter questionAdapter;
    ArrayList<String> questions;
    ArrayList<ArrayList<String>> answers;
    ArrayList<Integer> corrects;
    protected int questionIndex;
    boolean created;
    boolean createQuiz;
    String quizName;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);

        // getting whether or not we are creating a quiz
        createQuiz = getIntent().getBooleanExtra("creating", false);

        // initializing array list to hold questions, answers, and correct answers
        questions = new ArrayList<>();
        answers = new ArrayList<ArrayList<String>>();
        corrects = new ArrayList<Integer>();



        // create new quiz if not modifying
        if(createQuiz)
        {
            // quiz name can be edited
            ((TextView)findViewById(R.id.quizFileName)).setVisibility(View.INVISIBLE);
            // need to create new quiz if creating quiz and it's not created yet
            created = false;
            // getting the temporary file name and setting the file name to that
            quizName = "Quiz" + getIntent().getIntExtra("quizNum", 0) + "Data";
            ((EditText)findViewById(R.id.editQuizFile)).setText(quizName);
        }
        else
        {
            // getting the quiz name and setting it
            quizName = getIntent().getStringExtra("quizName");
            fileName = getIntent().getStringExtra("fileName");
            ((TextView)findViewById(R.id.quizFileName)).setText(fileName);
            ((TextView)findViewById(R.id.editQuizFile)).setText(fileName);
            // making quiz name unavailable to edit
            ((EditText)findViewById(R.id.editQuizFile)).setVisibility(View.INVISIBLE);
            // don't need to create new quiz if modifying
            created = true;
            // getting the file name
            ((EditText)findViewById(R.id.editQuizTitle)).setText(quizName);
            // getting quiz contents
            String contents = (String) getIntent().getStringExtra("quizContent");

            // processing contents
            while(contents.contains("\n"))
            {
                // reading in the question
                String q = contents.substring(0, contents.indexOf('\n'));
                questions.add(q);
                contents = contents.substring(contents.indexOf('\n')+1);

                // temporary array to hold the answer choices for 2d array list
                ArrayList<String> tempArray = new ArrayList<>();

                // getting answer choice one and seeing if it's the right answer
                String tempAnswer = contents.substring(0, contents.indexOf('\n'));
                if(tempAnswer.contains("*"))
                {
                    corrects.add(1);
                    tempAnswer = tempAnswer.substring(tempAnswer.indexOf("*")+1);
                }
                tempArray.add(tempAnswer);
                contents = contents.substring(contents.indexOf('\n')+1);

                // getting answer choice two and seeing if it's the right answer
                tempAnswer = contents.substring(0, contents.indexOf('\n'));
                if(tempAnswer.contains("*"))
                {
                    corrects.add(2);
                    tempAnswer = tempAnswer.substring(tempAnswer.indexOf("*")+1);
                }
                tempArray.add(tempAnswer);
                contents = contents.substring(contents.indexOf('\n')+1);

                // getting answer choice three and seeing if it's the right answer
                tempAnswer = contents.substring(0, contents.indexOf('\n'));
                if(tempAnswer.contains("*"))
                {
                    corrects.add(3);
                    tempAnswer = tempAnswer.substring(tempAnswer.indexOf("*")+1);
                }
                tempArray.add(tempAnswer);
                contents = contents.substring(contents.indexOf('\n')+1);

                // getting answer choice four and seeing if it's the right answer
                tempAnswer = contents.substring(0, contents.indexOf('\n'));
                if(tempAnswer.contains("*"))
                {
                    corrects.add(4);
                    tempAnswer = tempAnswer.substring(tempAnswer.indexOf("*")+1);
                }
                tempArray.add(tempAnswer);
                if(contents.substring(contents.indexOf("\n")).length() <= 5)
                {
                    contents = "";
                }
                else // if there are more questions, update content so that the next line will be the next question
                    contents = contents.substring(contents.indexOf('\n')+1);

                answers.add(tempArray);
            }

        }

        // no question is selected at first
        questionIndex = -1;

        // creating and initializing the Recycler view with row items that show the quiz names
        questionList = findViewById(R.id.questionRecyclerView);
        questionAdapter = new RecycleAdapter(questions, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        questionList.setLayoutManager(layoutManager);
        questionList.setItemAnimator(new DefaultItemAnimator());
        questionList.setAdapter(questionAdapter);
/*
        ((EditText)findViewById(R.id.txtQuestion)).setText(String.valueOf(answers.size()));
        ((EditText)findViewById(R.id.txtAnswer1)).setText(String.valueOf(corrects.size()));
        ((EditText)findViewById(R.id.txtAnswer2)).setText(String.valueOf(questions.size()));


 */
    }

    // on click handler for the delete button
    public void btnDelete(View view) {

        // if questions can be deleted and there is a selected question delete it
        if(questions.size()>0 && questionIndex >= 0)
        {
            // deleting questions along with answers
            questions.remove(questionIndex);
            answers.remove(questionIndex);
            corrects.remove(questionIndex);
            // updating the recycler view
            questionAdapter.changeDataset(questions);
            // going back to add mode and writing to file
            btnClear(view);
            writeFile();
        }
    }

    // on click handler for clear button
    public void btnClear(View view) {
        // clearing out all the fields
        ((EditText)findViewById(R.id.txtQuestion)).setText("");
        ((EditText)findViewById(R.id.txtAnswer1)).setText("");
        ((EditText)findViewById(R.id.txtAnswer2)).setText("");
        ((EditText)findViewById(R.id.txtAnswer3)).setText("");
        ((EditText)findViewById(R.id.txtAnswer4)).setText("");
        ((RadioButton)findViewById(R.id.btnAnswer1)).setChecked(false);
        ((RadioButton)findViewById(R.id.btnAnswer2)).setChecked(false);
        ((RadioButton)findViewById(R.id.btnAnswer3)).setChecked(false);
        ((RadioButton)findViewById(R.id.btnAnswer4)).setChecked(false);

        // back to add mode and update the recycler view
        questionIndex = -1;
        questionAdapter.changeDataset(questions);

    }

    // on click handler for save button
    public void btnSave(View view) {

        // making sure every field is filled and at least one correct answer marked and file name starts with quiz
        if(String.valueOf(((EditText)findViewById(R.id.txtQuestion)).getText()).length() > 0 && String.valueOf(((EditText)findViewById(R.id.txtAnswer1)).getText()).length() > 0 && String.valueOf(((EditText)findViewById(R.id.txtAnswer2)).getText()).length() > 0 && String.valueOf(((EditText)findViewById(R.id.txtAnswer3)).getText()).length() > 0 && String.valueOf(((EditText)findViewById(R.id.txtAnswer4)).getText()).length() > 0 && String.valueOf(((EditText)findViewById(R.id.editQuizTitle)).getText()).length() > 0 && String.valueOf(((EditText)findViewById(R.id.editQuizFile)).getText()).length() > 0 && (((RadioButton)findViewById(R.id.btnAnswer1)).isChecked() || ((RadioButton)findViewById(R.id.btnAnswer2)).isChecked() || ((RadioButton)findViewById(R.id.btnAnswer3)).isChecked() || ((RadioButton)findViewById(R.id.btnAnswer4)).isChecked()) && (!createQuiz || String.valueOf(((EditText)findViewById(R.id.editQuizFile)).getText()).startsWith("Quiz")))
        {
            // if button doesn't exist add it
            if(questionIndex<0)
            {
                // adding to questions and answer array list
                questions.add(String.valueOf(((EditText)findViewById(R.id.txtQuestion)).getText()));
                ArrayList<String> temp = new ArrayList<>();
                temp.add(String.valueOf(((EditText)findViewById(R.id.txtAnswer1)).getText()));
                temp.add(String.valueOf(((EditText)findViewById(R.id.txtAnswer2)).getText()));
                temp.add(String.valueOf(((EditText)findViewById(R.id.txtAnswer3)).getText()));
                temp.add(String.valueOf(((EditText)findViewById(R.id.txtAnswer4)).getText()));
                answers.add(new ArrayList<>(temp));

                // adding to correct answers array list
                if(((RadioButton)findViewById(R.id.btnAnswer1)).isChecked())
                    corrects.add(1);
                if(((RadioButton)findViewById(R.id.btnAnswer2)).isChecked())
                    corrects.add(2);
                if(((RadioButton)findViewById(R.id.btnAnswer3)).isChecked())
                    corrects.add(3);
                if(((RadioButton)findViewById(R.id.btnAnswer4)).isChecked())
                    corrects.add(4);
            }
            else // if button exists, modify it
            {
                // setting the questions and answer array list
                questions.set(questionIndex, String.valueOf(((EditText)findViewById(R.id.txtQuestion)).getText()));
                ArrayList<String> temp = new ArrayList<>();
                temp.add(String.valueOf(((EditText)findViewById(R.id.txtAnswer1)).getText()));
                temp.add(String.valueOf(((EditText)findViewById(R.id.txtAnswer2)).getText()));
                temp.add(String.valueOf(((EditText)findViewById(R.id.txtAnswer3)).getText()));
                temp.add(String.valueOf(((EditText)findViewById(R.id.txtAnswer4)).getText()));
                answers.set(questionIndex, temp);
                // setting the correct answer array list
                if(((RadioButton)findViewById(R.id.btnAnswer1)).isChecked())
                    corrects.set(questionIndex, 1);
                if(((RadioButton)findViewById(R.id.btnAnswer2)).isChecked())
                    corrects.set(questionIndex, 2);
                if(((RadioButton)findViewById(R.id.btnAnswer3)).isChecked())
                    corrects.set(questionIndex, 3);
                if(((RadioButton)findViewById(R.id.btnAnswer4)).isChecked())
                    corrects.set(questionIndex, 4);
            }

            // updating recycler view
            questionAdapter.changeDataset(questions);
            questionIndex = -1;

            // back to add mode
            ((EditText)findViewById(R.id.txtQuestion)).setText("");
            ((EditText)findViewById(R.id.txtAnswer1)).setText("");
            ((EditText)findViewById(R.id.txtAnswer2)).setText("");
            ((EditText)findViewById(R.id.txtAnswer3)).setText("");
            ((EditText)findViewById(R.id.txtAnswer4)).setText("");
            ((RadioButton)findViewById(R.id.btnAnswer1)).setChecked(false);
            ((RadioButton)findViewById(R.id.btnAnswer2)).setChecked(false);
            ((RadioButton)findViewById(R.id.btnAnswer3)).setChecked(false);
            ((RadioButton)findViewById(R.id.btnAnswer4)).setChecked(false);
        }
        else
        {
            // if everything isn't filled out properly, find what isn't and correct it
            if(String.valueOf(((EditText)findViewById(R.id.txtQuestion)).getText()).length() <= 0)
            {
                ((EditText)findViewById(R.id.txtQuestion)).setHint("Must fill this");
            }
            if(String.valueOf(((EditText)findViewById(R.id.txtAnswer1)).getText()).length() <= 0)
            {
                ((EditText)findViewById(R.id.txtAnswer1)).setHint("Must fill this");
            }
            if(String.valueOf(((EditText)findViewById(R.id.txtAnswer2)).getText()).length() <= 0)
            {
                ((EditText)findViewById(R.id.txtAnswer2)).setHint("Must fill this");
            }
            if(String.valueOf(((EditText)findViewById(R.id.txtAnswer3)).getText()).length() <= 0)
            {
                ((EditText)findViewById(R.id.txtAnswer3)).setHint("Must fill this");
            }
            if(String.valueOf(((EditText)findViewById(R.id.txtAnswer4)).getText()).length() <= 0)
            {
                ((EditText)findViewById(R.id.txtAnswer4)).setHint("Must fill this");
            }
            if(createQuiz && String.valueOf(((EditText)findViewById(R.id.editQuizFile)).getText()).length() <= 0)
            {
                ((EditText)findViewById(R.id.editQuizFile)).setHint("Must fill this");
            }
            else if(createQuiz && !String.valueOf(((EditText)findViewById(R.id.editQuizFile)).getText()).startsWith("Quiz"))
            {
                // if file name doesn't start with quiz, let them know
                ((EditText)findViewById(R.id.editQuizFile)).setText("");
                ((EditText)findViewById(R.id.editQuizFile)).setHint("Must start with Quiz");
            }
            if(String.valueOf(((EditText)findViewById(R.id.editQuizTitle)).getText()).length() <= 0)
            {
                ((EditText)findViewById(R.id.editQuizTitle)).setHint("Must fill this");
            }
        }

        // writing to file
        writeFile();
    }

    // getting question information when modifying a question
    public void modQuestion(int questionIndex)
    {
        // getting question information and filling it in the fields
        ((EditText)findViewById(R.id.txtQuestion)).setText(questions.get(questionIndex));
        ((EditText)findViewById(R.id.txtAnswer1)).setText(answers.get(questionIndex).get(0));
        ((EditText)findViewById(R.id.txtAnswer2)).setText(answers.get(questionIndex).get(1));
        ((EditText)findViewById(R.id.txtAnswer3)).setText(answers.get(questionIndex).get(2));
        ((EditText)findViewById(R.id.txtAnswer4)).setText(answers.get(questionIndex).get(3));

        // getting the correct answer and selecting the proper radio button
        ((RadioButton)findViewById(R.id.btnAnswer1)).setChecked(false);
        ((RadioButton)findViewById(R.id.btnAnswer2)).setChecked(false);
        ((RadioButton)findViewById(R.id.btnAnswer3)).setChecked(false);
        ((RadioButton)findViewById(R.id.btnAnswer4)).setChecked(false);

        // selecting the proper radio button
        if(corrects.get(questionIndex)==1)
            ((RadioButton)findViewById(R.id.btnAnswer1)).setChecked(true);
        else if(corrects.get(questionIndex)==2)
            ((RadioButton)findViewById(R.id.btnAnswer2)).setChecked(true);
        else if(corrects.get(questionIndex)==3)
            ((RadioButton)findViewById(R.id.btnAnswer3)).setChecked(true);
        else
            ((RadioButton)findViewById(R.id.btnAnswer4)).setChecked(true);
    }

    // updating hte index of the recycler view item selected
    public void updateIndex(View v)
    {
        questionIndex = questionList.getChildLayoutPosition(v);
    }

    // saving the quiz file
    public void writeFile()
    {
        // getting the quiz name
        quizName = ((EditText)findViewById(R.id.editQuizTitle)).getText().toString();
        // adding quiz name to first line of file
        String fileContent = quizName + "\n";

        // adding in questions and answer choices along with the correct answer "*" representation through a for loop
        for(int questionNum = 0; questionNum < questions.size(); questionNum++)
        {
            fileContent += questions.get(questionNum) + "\n";
            if(corrects.get(questionNum)==1)
                fileContent += "*";
            fileContent += answers.get(questionNum).get(0) + "\n";
            if(corrects.get(questionNum)==2)
                fileContent += "*";
            fileContent += answers.get(questionNum).get(1) + "\n";
            if(corrects.get(questionNum)==3)
                fileContent += "*";
            fileContent += answers.get(questionNum).get(2) + "\n";
            if(corrects.get(questionNum)==4)
                fileContent += "*";
            fileContent += answers.get(questionNum).get(3) + "\n";
        }

        // we won't allow the user to create the same quiz twice with different file names so only create a new file the first time they save
        if(createQuiz && !created)
        {
            fileName = String.valueOf(((EditText)findViewById(R.id.editQuizFile)).getText());
            created = true;
        }

        // trying to write the string to a text file
        BufferedWriter output = null;
        try {
            // creating a file and writing text to it
            File file = new File("/data/data/com.cxj170002.asg22",fileName + ".txt");
            output = new BufferedWriter(new FileWriter(file));
            output.write(fileContent);
            output.close();
            // delete file automatically if there are no questions
            if(questions.size()<1)
            {
                file.delete();
            }
        }
        catch (Exception e)
        {
            // error message if it failed to write
            System.out.println("Failed Writing File");
        }

    }

    // on click listener for when an answer choice is selected
    public void correctAnswerClick(View view) {
        // get the id of the selected choice
        int checkedID = view.getId();
        // clear all the choices
        ((RadioButton)findViewById(R.id.btnAnswer1)).setChecked(false);
        ((RadioButton)findViewById(R.id.btnAnswer2)).setChecked(false);
        ((RadioButton)findViewById(R.id.btnAnswer3)).setChecked(false);
        ((RadioButton)findViewById(R.id.btnAnswer4)).setChecked(false);

        // check the selected answer button
        if(checkedID == R.id.btnAnswer1)
            ((RadioButton)findViewById(R.id.btnAnswer1)).setChecked(true);
        else if(checkedID == R.id.btnAnswer2)
            ((RadioButton)findViewById(R.id.btnAnswer2)).setChecked(true);
        else if(checkedID == R.id.btnAnswer3)
            ((RadioButton)findViewById(R.id.btnAnswer3)).setChecked(true);
        else
            ((RadioButton)findViewById(R.id.btnAnswer4)).setChecked(true);

    }

    // creating home button handler
    public void btnHome(View view) {
        // using intent to return to the main activity
        Intent intent = new Intent(this, MainActivity.class);
        // starting the main activity
        startActivity(intent);
    }

    // recycle adapter class
    public class RecycleAdapter extends RecyclerView.Adapter<ProfessorActivity.RecycleAdapter.RecycleRow> {
        // array list to hold all the quizzes
        private ArrayList<String> allQuestions;
        // int to hold the currently selected item of the recycle view
        private int selectedItem = -1;
        // main activity variable
        ProfessorActivity parent;

        // constructor that takes an array of quiz names and the main activity
        public RecycleAdapter(ArrayList<String> names, ProfessorActivity p) {
            // initializing the quizzes to the passed string arraylist
            allQuestions = names;
            // initializing main activity
            parent = p;
        }

        // function to change the dataset of the adapter
        public void changeDataset(ArrayList<String> updatedData) {
            // updating the dataset
            allQuestions = updatedData;
            selectedItem = -1; // changing the selected item
            notifyDataSetChanged();
        }


        @NonNull
        @Override
        // when view holder is created, return the holder with the view
        public ProfessorActivity.RecycleAdapter.RecycleRow onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_items, parent, false);
            return new RecycleAdapter.RecycleRow(v);
        }

        @Override
        // when view is bound, it gets the data to display which happens once for every list item
        public void onBindViewHolder(@NonNull RecycleAdapter.RecycleRow holder, int position) {
            holder.questionNameText.setText(allQuestions.get(position));
            if (selectedItem == position)
                holder.itemView.setBackgroundColor(Color.CYAN);
            else
                holder.itemView.setBackgroundColor(Color.WHITE);
        }

        @Override
        // function to determine how many will fit on screen
        public int getItemCount() {
            return allQuestions == null ? 0 : allQuestions.size();
        }

        // view holder for each recycle view row
        public class RecycleRow extends RecyclerView.ViewHolder implements View.OnClickListener {
            // textview to hold quiz name
            TextView questionNameText;

            public RecycleRow(@NonNull View itemView) {
                super(itemView);
                // initializing quiz name textview
                questionNameText = (TextView) itemView.findViewById(R.id.QuizName);
                // setting on click listener
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                // changing the quiz index which holds what quiz was selected
                questionIndex = questionList.getChildAdapterPosition(v);
                // notifying that item has been changed
                notifyItemChanged(selectedItem);
                // updating the selected item
                selectedItem = getLayoutPosition();
                // notifying that item has been changed
                notifyItemChanged(selectedItem);

                updateIndex(v);

//                ((EditText)findViewById(R.id.editQuizTitle)).setText(String.valueOf(answers.size()));
//                ((TextView)findViewById(R.id.quizFileName)).setText(String.valueOf(corrects.size()));

                modQuestion(questionIndex);

            }
        }
    }
}