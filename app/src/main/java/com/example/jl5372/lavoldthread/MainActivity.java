package com.example.jl5372.lavoldthread;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/***
 * This is the main class that runs the Android Application
 *
 * This app has a progress bar that updates as a file is written or read.
 * It has a List View that displays the contents of the file that is read.
 * It also writes to a file in the internal files system.
 *
 * There are three buttons: Create, Load and Clear.
 * Clicking on Create creates the file.
 * Clicking on Load reads the file and displays the List View
 * Clicking on Clear, clears the List View making it invisible.
 */
public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ListView list;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setMax(10);
            progressBar.setVisibility(View.GONE);
        }
        registerClickCallback();

    }

    /**
     * Sets up an Item OnClick for the List view that displays a toast message when item is clicked
     */
    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.listViewMain);
        try{
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView = (TextView) view;
                    String message = "You clicked # " + position + ", which is string: "
                            + textView.getText().toString();
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * saveToFile:
     * Calls a AsyncTask that saves a file to the internal file system.
     *
     * Makes the progress bar visible for progress in saving a file.
     *
     * @param view passes a reference to the view
     */
    public void saveToFile(View view) {
        progressBar.setVisibility(View.VISIBLE);
        new runTasks().execute(0);
    }

    /**
     * readFile:
     * Calls a AsyncTask that reads a file from the internal file system.
     * Load the contents into a ArrayList and then displays the contents of the
     * ArrayList in a List View.
     *
     * Makes the progress bar visible for progress in saving a file.
     *
     * @param view passes a reference to the view
     */
    public void readFile(View view) {
        progressBar.setVisibility(View.VISIBLE);
        new runTasks().execute(1);
    }

    /**
     * clearListView
     * Calls the clear method of the "adapter" which empties it.  This it displays the empty list
     * which has the effect of Clearing the List View
     *
     * Displays a Toast saying that the list was cleared
     *
     * @param view  passes a reference to the view
     */
    public void clearListView(View view) {
        adapter.clear();
        list.setAdapter(adapter);
        Toast.makeText(MainActivity.this, "List View Cleared!", Toast.LENGTH_SHORT).show();
    }


    /**
     * runTasks is a class that extends AsyncTask which allows for a background thread to run
     * and while running publish progress with allows for the progress bar to be updated.
     *
     * This class has the logic to read and write to the internal file system.
     */
    class runTasks extends AsyncTask<Integer, Integer, String> {

        /**
         * doInBackground:
         * Based on the parameter value this method runs the logic to save or read from
         * a file.
         *
         * @param params  value that specifies which method to run
         * @return   return value
         */
        @Override
        protected String doInBackground(Integer... params) {
            if(params[0] == 0) {
                saveToFileTask();
                return "saved";
            }
            else {
                readFileTask();
                return "read";
            }
        }

        /**
         * readFileTask:
         * This method reads a file from the internal file system and load the adapter and
         * list view with data in preparation to be rendered on the screen.
         */
        protected void readFileTask() {
            String filename = "numbers.txt";
            File file = new File(getFilesDir(),filename);
            List<String> arFileData = new ArrayList<String>();
            int progressCntr = 1;
            try {
                InputStream fis = new FileInputStream(file);

                InputStreamReader inputReader = new InputStreamReader(fis);
                BufferedReader buffReader = new BufferedReader(inputReader);

                String line;
                do {
                    line = buffReader.readLine();
                    if(line != null)
                        arFileData.add(line);
                    publishProgress(progressCntr++);
                    Thread.sleep(250);
                } while (line != null);

            }catch (Exception e) {
                e.printStackTrace();
            }
            adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.view_item, arFileData);
            list = (ListView) findViewById(R.id.listViewMain);
        }

        /**
         * saveToFileTask:
         * This method writes a file to the internal file system.
         * and sends periodic publish progress updates so that the progress bar
         * can be updated.
         */
        protected void saveToFileTask() {
            String filename = "numbers.txt";
            String data;
            FileOutputStream outputStream;
            int progressCntr = 1;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                for (int i = 1; i <= 10; i++) {
                    data = "" + i + "\n";
                    outputStream.write(data.getBytes());
                    publishProgress(progressCntr++);
                    Thread.sleep(250);
                }
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * onPostExecute
         * Takes the return value from doInBackground and does some final activities.
         * such as hiding the progress bar.
         *
         * For saving the file it displays a Toast say the file was saved.
         * For reading the file is does a setAdapter which cause the List View to display on screen
         * and it also displays a Toast saying that the file was read.
         *
         * @param result  result is used to determine what post execute activities should run
         */
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);

            if(result.equals("saved"))
                Toast.makeText(MainActivity.this, "File Saved!", Toast.LENGTH_SHORT).show();
            else if (result.equals("read")) {
                list.setAdapter(adapter);
                Toast.makeText(MainActivity.this, "File Read!", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * onProgressUpdate
         * updates the progress bar.
         *
         * @param values value for the progress bar.  1 - 10
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
    }
}

