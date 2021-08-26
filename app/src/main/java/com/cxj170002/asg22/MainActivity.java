package com.cxj170002.asg22;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Scanner;

import static com.cxj170002.asg22.R.id.btnOnlineQuizButton;

/* Written by Christy Jacob for CS4301.002, assignment 3 Quiz Program part 2, starting March 12, 2021.
    NetID: cxj170002
    This program takes in files from a directory and filters for quiz files that fit a certain criteria
    After taking in the quizzes and their contents, it shows an activity with a recycler view that lists these quizzes
    The quizzes that are available to the user are both local and online quizzes. There are radio buttons which allow the user
    to choose between the two. Online quiz information is gathered through multithreading, specifically an async task.
    The list of quizzes in the Recycler view is updated whenever the user switches between the radio buttons.
    Users can input their name and select and take a quiz that's either local or from online
    After selecting a quiz and clicking the take quiz button, they are taken to a second activity that displays quiz questions in order one by one
    Users can selected and submit answers and see whether they got it right through fragments
    After finishing all the questions, the user is taken to a third activity that shows their score and allows them to return home to the main activity
    AFter returning to the main activity, user can start all over again
 */

public class MainActivity extends AppCompatActivity {

    // variables that will be used throughout the class
    File fileDir;
    File[] quizzes;
    ArrayList<String> quizNames;
    ArrayList<String> quizContents;
    Button btnTakeQuiz;
    Button btnCreate;
    Button btnEdit;
    RecyclerView quizList;
    protected int quizIndex;
    Async as;
    boolean onlineQuiz;
    RadioButton btnLocalQuiz;
    RadioButton btnOnlineQuiz;
    ArrayList<String> onlineQuizNames;
    ArrayList<String> onlineQuizContents;
    RecycleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setting online quiz to false since local is the default
        onlineQuiz = false;

        // defining the async task
        as = new Async(this);

        // defining the take quiz, local quiz, and online quiz buttons
        btnTakeQuiz = findViewById(R.id.btnTakeQuizButton);
        btnLocalQuiz = findViewById(R.id.btnLocalQuizButton);
        btnOnlineQuiz = findViewById(btnOnlineQuizButton);

        // defining the create and edit buttons and setting them to invisible for now
        btnCreate = findViewById(R.id.btnCreateQuizButton);
        btnEdit = findViewById(R.id.btnEditQuizButton);
        btnCreate.setVisibility(View.INVISIBLE);
        btnEdit.setVisibility(View.INVISIBLE);

        // text listener for professor
        EditText name = findViewById(R.id.nameEdit);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if professior is typed,make the buttons visible
                if(name.getText().toString().equalsIgnoreCase("Professor"))
                {
                    btnCreate.setVisibility(View.VISIBLE);
                    btnEdit.setVisibility(View.VISIBLE);
                }
                else // if professor isn't typed make the buttons invisible
                {
                    btnCreate.setVisibility(View.INVISIBLE);
                    btnEdit.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // creating arraylists to hold the quizNames, quizContents, online quizzes, online quiz names, and online quiz contents
        quizNames = new ArrayList<String>();
        quizContents = new ArrayList<String>();
        onlineQuizNames = new ArrayList<String>();
        onlineQuizContents = new ArrayList<String>();
        // defining variable that will keep track of the quiz that the user selected to pass to the next activity
        quizIndex = 0;

        // getting the files directory
        fileDir = new File("/data/data/com.cxj170002.asg22");
        // getfilesdir was getting the wrong directory for me and wasn't working
 //                fileDir = getFilesDir();

        // specifying how to filter file names
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                // make sure there is a '.' to make sure it has some file extension
                if(name.lastIndexOf('.')>0)
                {
                    // gets last index of '.'
                    int lastIndexPeriod = name.lastIndexOf('.');

                    // get the file extension
                    String fileExtension = name.substring(lastIndexPeriod);

                    // make sure it's a valid quiz by making sure it starts with "Quiz" and ends with "txt"
                    if(name.startsWith("Quiz") && fileExtension.equals(".txt"))
                        return true; // returning true if valid quiz
                }
                // if not valid quiz, return false
                return false;
            }
        };

        // filtering the quizzes in the directory based on the defined filename filter
        quizzes = fileDir.listFiles(filter);

        // looping through the quizzes and reading in their name and contents
        for(int quizNum = 0; quizNum < quizzes.length; quizNum++)
        {
            // int to keep track of the line of the quiz file that we are on
            int line = 0;
            try {
                // creating a scanner to read in the quiz
                Scanner readQuiz = new Scanner(quizzes[quizNum]);

                // while loop that loops through the quiz file while it has a next line
                while(readQuiz.hasNext())
                {
                    // gets the contents of the current line
                    String currentLine = readQuiz.nextLine();
                    if(line == 0) // if it's the first line, add a new quizname to the quiznames arraylist
                    {
                        quizNames.add(currentLine);
                    }
                    else if(line == 1) // if it's the second line, add a new string object to the quiz contents arraylist
                    {
                        quizContents.add(currentLine + "\n");
                    }
                    else // if it's any other line, add it to the quiz contents of the quiz we are currently on plus a new line
                    {
                        quizContents.set(quizNum, quizContents.get(quizNum)+currentLine + "\n");
                    }

                    // updating what line we are on
                    line++;
                }

                // closing the scanner
                readQuiz.close();

            } catch (FileNotFoundException e) { // catching errors if the file is not found
                e.printStackTrace();
            }
        }

        // if there are no quizzes in the directory, change the text of the button to no quizzes available so user knows
        if(quizzes.length == 0)
            btnTakeQuiz.setText("No quizzes available");

        // creating and initializing the Recycler view with row items that show the quiz names
        quizList = findViewById(R.id.quizRecyclerView);
        adapter = new RecycleAdapter(quizNames, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        quizList.setLayoutManager(layoutManager);
        quizList.setItemAnimator(new DefaultItemAnimator());
        quizList.setAdapter(adapter);

        // executing async task
        String url = "https://personal.utdallas.edu/~john.cole/Data/Quizzes.txt";
        as.execute(url);

    }

    // this method brings up the second activity which is the chosen quiz
    public void btnShowQuiz(View view) {

        // getting the player's name to pass
        String playerName = String.valueOf(((EditText)findViewById(R.id.nameEdit)).getText());
        // creating the second activity, the quiz, using an intent
        Intent intent = new Intent(this, TakeQuiz.class);
        // if it's not an online quiz, send the local quiz name and contents
        if(!onlineQuiz)
        {
            // sending the quiz contents and quiz name of the quiz selected through extras
            intent.putExtra("quizContent", quizContents.get(quizIndex));
            intent.putExtra("quizName", quizNames.get(quizIndex));
        }
        else // if it's an online quiz, send the online quiz name and contents
        {
            // sending the quiz contents and quiz name of the quiz selected through extras
            intent.putExtra("quizContent", onlineQuizContents.get(quizIndex));
            intent.putExtra("quizName", onlineQuizNames.get(quizIndex));
        }

        // sending the user's name through extras
        intent.putExtra("userName", playerName);
        // starts the second activity using intent
        startActivity(intent);
    }

    // handles when local quiz button is clicked
    public void btnLocalQuiz(View view) {
        // making online quiz false
        onlineQuiz=false;
        // making local quiz checked
        btnOnlineQuiz.setChecked(false);
        btnLocalQuiz.setChecked(true);

        // updating recycler view quiz names
        adapter.changeDataset(quizNames);

    }

    // processes the arraylist of strings sent from the post execution of async task to store the online quiz names and contents
    public void updateArray(ArrayList l)
    {
        // if there are objects in the array and the first item contains ".txt", it's the quizzes file
        if(l.size()>0 && ((String)l.get(0)).contains(".txt"))
        {
            // read in each quiz in the quizzes file to the online quiz array
            for(int i = 0; i < l.size(); i++)
            {
                // execute async task to read in each quiz
                Async asTemp = new Async(this);
                String url = getString(R.string.homeURL) + l.get(i);
                asTemp.execute(url);
            }

        }
        else if(l.size()>0) // if the first line doesn't contain ".txt" and there are objects in the array, then it is an actual quiz file
        {
            // read each object of the array
            for(int i = 0; i < l.size(); i++)
            {
                if(i==0) // the first object is the quiz's name so add that to the online quiz array list
                {
                    onlineQuizNames.add((String)l.get(i));
                }
                else if(i==1) // the second object will be the first question, so add it to the online quiz contents
                {
                    onlineQuizContents.add((String)l.get(i) + "\n");
                }
                else // all questions after the second object belong to the same quiz so append it to the second object
                {
                    onlineQuizContents.set(onlineQuizContents.size()-1, (String)onlineQuizContents.get(onlineQuizContents.size()-1) + (String)l.get(i) + "\n");
                }
            }
        }
    }


    // handles when online quiz button is clicked
    public void btnOnlineQuiz(View view) {
        // making online quiz true
        onlineQuiz=true;
        // making local quiz checked
        btnOnlineQuiz.setChecked(true);
        btnLocalQuiz.setChecked(false);

        // updating recycler view quiz names
        adapter.changeDataset(onlineQuizNames);
    }

    public void btnEditQuiz(View view) {

        // creating the next activity, the edit quiz activity, using an intent
        Intent intent = new Intent(this, ProfessorActivity.class);
        String contents = quizContents.get(quizIndex);

        // sending the quiz contents and quiz name of the quiz selected through extras
        intent.putExtra("quizContent", quizContents.get(quizIndex));
        intent.putExtra("quizName", quizNames.get(quizIndex));
        // sending boolean that quiz is modified not created
        intent.putExtra("creating", false);
        // sending file name after extracting it
        String filename = quizzes[quizIndex].toString();
        filename = filename.substring(filename.indexOf('Q'), filename.indexOf("txt")-1);
        intent.putExtra("fileName", filename);
        // starts the next activity using intent
        startActivity(intent);

    }

    // on click method for when they click create quiz
    public void btnCreateQuiz(View view) {

        // creating the next activity, the create quiz activity, using an intent
        Intent intent = new Intent(this, ProfessorActivity.class);
        // sending boolean that quiz is being created
        intent.putExtra("creating", true);
        // sending next suggested quiz number
        intent.putExtra("quizNum", quizNames.size()+1);
        // starts the next activity using intent
        startActivity(intent);
    }

    // recycle adapter class
    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleRow>
    {
        // array list to hold all the quizzes
        private ArrayList<String> allQuizzes;
        // int to hold the currently selected item of the recycle view
        private int selectedItem = 0;
        // main activity variable
        MainActivity parent;

        // constructor that takes an array of quiz names and the main activity
        public RecycleAdapter(ArrayList<String> names, MainActivity p)
        {
            // initializing the quizzes to the passed string arraylist
            allQuizzes = names;
            // initializing main activity
            parent = p;
        }

        // function to change the dataset of the adapter
        public void changeDataset(ArrayList<String> updatedData)
        {
            // updates the dataset
            allQuizzes = updatedData;
            notifyDataSetChanged();
        }


        @NonNull
        @Override
        // when view holder is created, return the holder with the view
        public RecycleRow onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_items, parent, false);
            return new RecycleRow(v);
        }

        @Override
        // when view is bound, it gets the data to display which happens once for every list item
        public void onBindViewHolder(@NonNull RecycleRow holder, int position) {
            holder.quizNameText.setText(allQuizzes.get(position));
            if(selectedItem == position)
                holder.itemView.setBackgroundColor(Color.CYAN);
            else
                holder.itemView.setBackgroundColor(Color.WHITE);
        }

        @Override
        // function to determine how many will fit on screen
        public int getItemCount() {
            return allQuizzes == null ? 0 : allQuizzes.size();
        }

        // view holder for each recycle view row
        public class RecycleRow extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            // textview to hold quiz name
            TextView quizNameText;

            public RecycleRow(@NonNull View itemView) {
                super(itemView);
                // initializing quiz name textview
                quizNameText = (TextView) itemView.findViewById(R.id.QuizName);
                // setting on click listener
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                // changing the quiz index which holds what quiz was selected
                quizIndex = quizList.getChildLayoutPosition(v);
                // notifying that item has been changed
                notifyItemChanged(selectedItem);
                // updating the selected item
                selectedItem = getLayoutPosition();
                // notifying that item has been changed
                notifyItemChanged(selectedItem);

            }
        }
    }
}