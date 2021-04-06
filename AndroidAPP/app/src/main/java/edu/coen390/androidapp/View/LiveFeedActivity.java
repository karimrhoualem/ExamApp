package edu.coen390.androidapp.View;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

//import com.example.examapp.R;
import edu.coen390.androidapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.HttpRequest;
import edu.coen390.androidapp.Model.Student;


public class LiveFeedActivity extends AppCompatActivity {

    public static final String KEY_URL_TO_LOAD = "KEY_URL_TO_LOAD";
    //TODO: change URLs
    @VisibleForTesting
    public static final String WEB_FORM_URL = "http://192.168.2.135:5000/video_feed";
    public static final String JSON_STUDENT_URL = "http://192.168.2.135:5000/person_info";
    /**
     * Tag used for logger.
     */
    private static final String TAG = "LiveFeedActivity";
    private JSONObject studentInformation;
    private WebView myWebView;
    private DatabaseHelper dbHelper;
    private Course course;
    private TextView studentName;
    private TextView studentID;
    private TextView seatNumber;
    private ImageView imageView;
    private Button backButton;
    private Button saveButton;
    private Timer timer;
    private TimerTask timerTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_feed);

        studentName = findViewById(R.id.studentNameTextView);
        studentID = findViewById(R.id.studentIDTextView);
        seatNumber = findViewById(R.id.seatNumberTextView);
        imageView = findViewById(R.id.successMessageImageView);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);

        setButtonListeners();

        // Receive Course object when CourseActivity intent begins LiveFeedActivity
        // For now, just using a manually created Course object
        // https://stackoverflow.com/a/7827593/12044281
        Intent intent = getIntent();
        course = (Course) intent.getSerializableExtra(VerificationModeActivity.COURSE_INTENT);

        Log.d(TAG, "after getIntent " + course.toString());

        dbHelper = new DatabaseHelper(this);
        timer = new Timer();

    }

    private void setButtonListeners() {
        backButton.setOnClickListener(v -> LiveFeedActivity.this.finish());

        saveButton.setOnClickListener(v -> {
            // TODO: Save student information and seat number in a new DB table for the current course.

            // TODO: Display a toast that shows successful save.

            // TODO: Return to previous activity.
            //LiveFeedActivity.this.finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        launchWebView();

        // TODO: Remove this manual student entry later
        Student obama = new Student(45454545, new String[]{"ENGR 391", "COMP 472"}, "Barack", "Obama");
        Student tawfiq = new Student(40000390, new String[]{"ENGR 391", "COEN 313"}, "Tawfiq", "Jawhar");
        Student hamill = new Student(40102453, new String[]{"ENGR 391", "COEN 313"}, "Mark", "Hamill");
        Student alec = new Student(40103773, new String[]{"ENGR 391", "COEN 313"}, "Alec", "Wolfe");
        long result = dbHelper.insertStudent(obama);
        long result2 = dbHelper.insertStudent(tawfiq);
        long result3 = dbHelper.insertStudent(hamill);
        long result4 = dbHelper.insertStudent(alec);

        if (result == -1 && result2 == -1 && result3 == -1 && result4 == -1) {
            Log.d(TAG, "Error inserting student into DB table.");
        } else {
            Log.d(TAG, "Student successfully inserted into DB: \n"
                    + obama.toString());
            Log.d(TAG, "Student successfully inserted into DB: \n"
                    + tawfiq.toString());
            Log.d(TAG, "Student successfully inserted into DB: \n"
                    + hamill.toString());
            Log.d(TAG, "Student successfully inserted into DB: \n"
                    + alec.toString());
        }


        try {
            // Get student information via an asynchronous JSON http request
/*
            JSONRefresh jsonObject = new JSONRefresh();
            Thread thread = new Thread(jsonObject);
            thread.start();*/


            /*Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        studentInformation = HttpRequest.getJSONObjectFromURL(JSON_STUDENT_URL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
*/


            timer.scheduleAtFixedRate(timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        studentInformation = HttpRequest.getJSONObjectFromURL(JSON_STUDENT_URL);
                        Log.d(TAG, "Student Info Obtained : ");
                        Student student = getStudentFromJSONObject(studentInformation);
                        Log.d(TAG, "Student Info Updated : " + student.toString());

                        // Search for the student in the Students DB table and verify
                        // whether they are enrolled in this course.

                        boolean isStudentConfirmed;

                        if (student != null) {
                            isStudentConfirmed = dbHelper.isStudentRegisteredInCourse(student, course);
                        } else {
                            isStudentConfirmed = false;
                        }

                        boolean isStudentSeated = dbHelper.isStudentSeated(student, course);
                        int seat = 0;
                        String studentSeat = "N/A";

                        // Display student information and confirmation status
                        if (isStudentConfirmed) {
                            studentName.setText(student.getFirstName() + " " + student.getLastName());
                            studentID.setText(Integer.toString(student.getID()));
                            if (!isStudentSeated) {
                                seat = course.getSeats().getNextSeat(student);
                                dbHelper.insertStudentSeat(student, course, seat);
                            } else {
                                seat = dbHelper.getStudentSeat(student, course);
                            }
                            seatNumber.setText(Integer.toString(seat));
                            Drawable drawable = ContextCompat.getDrawable(
                                    LiveFeedActivity.this, R.drawable.success);
                            imageView.setImageDrawable(drawable);
                            saveButton.setEnabled(true);
                            saveButton.setClickable(true);
                        } else {
                            studentName.setText("N/A");
                            studentID.setText("N/A");
                            seatNumber.setText("N/A");
                            Drawable drawable = ContextCompat.getDrawable(
                                    LiveFeedActivity.this, R.drawable.failure);
                            imageView.setImageDrawable(drawable);
                            saveButton.setEnabled(false);
                            saveButton.setClickable(false);
                        }

                        /*if (isStudentSeated) {
                            seatNumber.setText(studentSeat);
                        }*/

                        int finalSeat = seat;
                        saveButton.setOnClickListener(v -> {
                            String toastText = String.format("%s %s is verified and seat %d is assigned", student.getFirstName(), student.getLastName(), finalSeat);
                            Toast.makeText(LiveFeedActivity.this, toastText, Toast.LENGTH_LONG).show();
                            studentName.setText("N/A");
                            studentID.setText("N/A");
                            seatNumber.setText("N/A");
                            Toast.makeText(LiveFeedActivity.this, "READY TO VERIFY", Toast.LENGTH_LONG).show();
                            //recreate();
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }, 0, 1000);//put here time 1000 milliseconds=1 second*/

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void launchWebView() {
        myWebView = findViewById(R.id.webView);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        myWebView.loadUrl(WEB_FORM_URL);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
    }

    private Student getStudentFromJSONObject(JSONObject studentInformation) {
        try {
            int studentID = studentInformation.getInt("ID");
            String studentName = studentInformation.getString("name");
            String studentFirstName;
            String studentLastName;
            if (studentName.contains(" ") && studentID != 0) {
                String[] names = studentName.split(" ", 2);
                studentFirstName = names[0];
                studentLastName = names[1];
            } else if (studentName.contains("_") && studentID != 0) {
                String[] names = studentName.split("_", 2);
                studentFirstName = names[0];
                studentLastName = names[1];

            } else {
                studentFirstName = "N/A";
                studentLastName = "N/A";
            }

            return new Student(studentID, null, studentFirstName, studentLastName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*class JSONRefresh implements Runnable {
        private volatile boolean exit = false;

        @Override
        public void run() {
            try {
                while (!exit) {
                    studentInformation = HttpRequest.getJSONObjectFromURL(JSON_STUDENT_URL);
                    Log.d(TAG, "Student Info Obtained");
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void stop() {
            exit = true;
        }
    }*/
}