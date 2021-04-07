package edu.coen390.androidapp.View;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.examapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.HttpRequest;
import edu.coen390.androidapp.Model.Student;

public class CardScanActivity extends AppCompatActivity {

    public static final String KEY_URL_TO_LOAD = "KEY_URL_TO_LOAD";
    //TODO: change URLs
    @VisibleForTesting
//    public static final String JSON_STUDENT_URL = "http://192.168.2.135:5000/person_info";
    public static final String JSON_STUDENT_URL = "http://192.168.0.166:5000";

    //*********tested using JSON_test2.py********


    /**
     * Tag used for logger.
     */
    private static final String TAG = "CardScanActivity";
    private JSONObject studentInformation;
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
    private int seat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_scan);

        Log.i(TAG, "on create was accessed");

        studentName = findViewById(R.id.studentNameTextView);
        studentID = findViewById(R.id.studentIDTextView);
        seatNumber = findViewById(R.id.seatNumberTextView);
        imageView = findViewById(R.id.successMessageImageView);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);

        setButtonListeners();

        Intent intent = getIntent();
        course = (Course) intent.getSerializableExtra(InvigilatorActivity.COURSE_INTENT);

        Log.d(TAG, "after getIntent " + course.toString());

        dbHelper = new DatabaseHelper(this);
        timer = new Timer();
    }

    private void setButtonListeners() {
        backButton.setOnClickListener(v -> CardScanActivity.this.finish());

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

        try {
            // Get student information via an asynchronous JSON http request

            //JSONRefresh jsonObject = new JSONRefresh();
            // Thread thread = new Thread(jsonObject);
            // thread.start();

/*
            Thread thread = new Thread(new Runnable() {
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
                        Log.d(TAG, "Student Info Obtained : "+ studentInformation);
                        Student student = getStudentFromJSONObject(studentInformation);
                        Log.d(TAG, "Student Info Updated : " + student.toString());

                        // Search for the student in the Students DB table and verify
                        // whether they are enrolled in this course.

                        boolean isStudentConfirmed;

                        if (student != null) {
                            isStudentConfirmed = dbHelper.isStudentRegisteredInCourse(student, course);
                        }
                        else {
                            isStudentConfirmed = false;
                        }

                        boolean isStudentSeated = dbHelper.isStudentSeated(student, course);
                        seat = 0;
                        String studentSeat = "N/A";

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Display student information and confirmation status
                                if (isStudentConfirmed) {
                                    String name = student.getFirstName() + " " + student.getLastName();
                                    studentName.setText(name);
                                    Log.d(TAG,"student name: " + name);
                                    studentID.setText(Integer.toString((int)student.getId()));
                                    if (!isStudentSeated) {
                                        seat = course.getSeats().getNextSeat(student);
                                        dbHelper.insertStudentSeat(student, course, seat);
                                    } else {
                                        seat = dbHelper.getStudentSeat(student, course);
                                    }
                                    String seat_number = Integer.toString(seat);
                                    Log.d(TAG,"student seat: " + seat_number);
                                    seatNumber.setText(seat_number);
                                    Drawable drawable = ContextCompat.getDrawable(
                                            CardScanActivity.this, R.drawable.success);
                                    imageView.setImageDrawable(drawable);
                                    saveButton.setEnabled(true);
                                    saveButton.setClickable(true);

                                    // Cancel the timer thread when a student is correctly identified,
                                    // otherwise it keeps running even when we leave the activity.
                                    cancel();
                                } else {
                                    studentName.setText("N/A");
                                    studentID.setText("N/A");
                                    seatNumber.setText("N/A");
                                    Drawable drawable = ContextCompat.getDrawable(
                                            CardScanActivity.this, R.drawable.failure);
                                    imageView.setImageDrawable(drawable);
                                    saveButton.setEnabled(false);
                                    saveButton.setClickable(false);
                                }
                            }
                        });

                        /*if (isStudentSeated) {
                            seatNumber.setText(studentSeat);
                        }*/

                        int finalSeat = seat;
                        saveButton.setOnClickListener(v -> {
                            String toastText = String.format("%s %s is verified and seat %d is assigned", student.getFirstName(), student.getLastName(), finalSeat);
                            Toast.makeText(CardScanActivity.this, toastText, Toast.LENGTH_LONG).show();
                            studentName.setText("N/A");
                            studentID.setText("N/A");
                            seatNumber.setText("N/A");
                            Toast.makeText(CardScanActivity.this, "READY TO VERIFY", Toast.LENGTH_LONG).show();
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

            return new Student(studentID, null, studentFirstName, studentLastName, null, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

/*
          class JSONRefresh implements Runnable {
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
