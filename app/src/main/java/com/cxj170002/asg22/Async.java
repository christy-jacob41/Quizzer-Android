package com.cxj170002.asg22;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

/* This class is the async task class. It performs an async task in the background, which in this case, is reading in quizzes
    from online

 */
public class Async extends AsyncTask<String, Void, ArrayList> {

    // variables that will be needed throughout this class
    ArrayList list;
    MainActivity context;

    // cosntructor takes the activity so we can access its UI methods
    public Async(MainActivity s) {context = s;}

    // gets information from the website in the background
    @Override
    protected ArrayList doInBackground(String... uList) {
        // initializing variables for URL, scanner, input stream, etc. so there are no issues in the try catch blocks
        URL u = null;
        String strLine;
        InputStream instream = null;
        Scanner scanner = null;
        int response = 0;
        String url = uList[0];
        list = new ArrayList();

        // practicing defensive programming by using try catch blocks
        try
        {
            // getting a new url
            u = new URL(url);

        } catch (MalformedURLException e) {
            System.out.println("Problem with creating a URL");
        }
        // initialzing a new connection
        URLConnection connection = null;

        try{
            // connecting with open connection
            connection = u.openConnection();
        } catch (IOException e) {
            System.out.println("Problem with making a connection");
        }

        // making a http connection
        HttpURLConnection httpConn = (HttpURLConnection) connection;

        try{
            // getting the response
            response = httpConn.getResponseCode();
        } catch (IOException e) {
            System.out.println("Problem with getting the response code.");
        }

        // if the response is 200 which means it's okay, try to read in data from the txt file
        if(response== HttpURLConnection.HTTP_OK)
        {
            // trying to define input stream and scanner that's connected to the URL through http Connection
            try{
                instream = httpConn.getInputStream();
                scanner = new Scanner(instream);
            } catch (IOException e) {
                System.out.println("Problem with input stream and/ or scanner.");
            }

            // trying to read in each line of the txt file
            try{
                // while loop continues while the file has items remaining in it
                while(scanner.hasNext())
                {
                    strLine = scanner.nextLine();
                    list.add(strLine);
                }
                scanner.close(); // closing the scanner
            } catch (Exception e) {
                System.out.println("Problem with scanner.");
            }
        }

        // disconnecting from connection
        httpConn.disconnect();
        // returning the arraylist of string read in from the file
        return list;
    }

    @Override
    protected void onPostExecute(ArrayList arrayList) {
        // calling updateArray method in the main activity to process the data that was read in
        context.updateArray(arrayList);
    }
}
