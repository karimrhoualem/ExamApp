package edu.coen390.androidapp.View;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.coen390.androidapp.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Controller.SharedPreferenceHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.HttpRequest;
import edu.coen390.androidapp.Model.Student;

public class CardScanActivity extends AppCompatActivity {
    public static final String KEY_URL_TO_LOAD = "KEY_URL_TO_LOAD";
    //TODO: change URLs
    @VisibleForTesting
//    public static final String JSON_STUDENT_URL = "http://192.168.2.135:5000/person_info";
    public static final String JSON_STUDENT_URL = "http://192.168.0.166:5000";
    private static final String TAG = "CardScanActivity";
    private JSONObject studentInformation;
    private DatabaseHelper dbHelper;
    private Course course;
    private TextView studentName;
    private TextView studentID;
    private TextView seatNumber;
    private ImageView imageView;
    private Button backButton;
    private Timer timer;
    private TimerTask timerTask;
    private SharedPreferences sharedPreferences;
    private SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_scan);

        Log.i(TAG, "on create was accessed");

        setupUI();

        Intent intent = getIntent();
        course = (Course) intent.getSerializableExtra(InvigilatorActivity.COURSE_INTENT);

        sharedPreferenceHelper = new SharedPreferenceHelper(CardScanActivity.this);
        Course retrievedCourse = sharedPreferenceHelper.getProfile(course);
        if (retrievedCourse != null) {
            course = retrievedCourse;
        }
    }

    private void setupUI() {
        studentName = findViewById(R.id.studentNameTextView);
        studentID = findViewById(R.id.studentIDTextView);
        seatNumber = findViewById(R.id.seatNumberTextView);
        imageView = findViewById(R.id.successMessageImageView);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->{
            Toast.makeText(this, "Card Scan Activity Cancelled.", Toast.LENGTH_SHORT).show();
            CardScanActivity.this.finish();
        });

        dbHelper = new DatabaseHelper(this);
        timer = new Timer();
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            timer.scheduleAtFixedRate(timerTask = new TimerTask() {
                @Override
                public void run() {
                try {
                    studentInformation = HttpRequest.getJSONObjectFromURL(JSON_STUDENT_URL);
                    Log.d(TAG, "Student Info Obtained : "+ studentInformation);
                    Student student = HttpRequest.getStudentFromJSONObject(studentInformation);
                    Log.d(TAG, "Student Info Updated : " + student.toString());

                    boolean isStudentConfirmed;
                    if (student != null) {
                        isStudentConfirmed = dbHelper.isStudentRegisteredInCourse(student, course);
                    }
                    else {
                        isStudentConfirmed = false;
                    }

                    boolean isStudentSeated = dbHelper.isStudentSeated(student, course);

                    if (isStudentConfirmed) {
                        if (!isStudentSeated) {
                            int seat = course.getSeats().getNextSeat(student);
                            if (seat != -1) {
                                int status = dbHelper.insertInExamTable(student, course, seat);
                                if (status == 1) {
                                    String name = student.getFirstName() + " " + student.getLastName();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            studentName.setText(name);
                                            studentID.setText(Integer.toString((int)student.getId()));
                                            seatNumber.setText(Integer.toString(seat));
                                            Drawable drawable = ContextCompat.getDrawable(
                                                    CardScanActivity.this, R.drawable.success);
                                            imageView.setImageDrawable(drawable);
                                            Toast.makeText(CardScanActivity.this,
                                                    "Student with ID: " + student.getId() + " has been successfully confirmed. " +
                                                            "Returning to previous page.",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    Thread thread = new Thread(){
                                        @Override
                                        public void run() {
                                        try {
                                            Thread.sleep(3000);

                                            sharedPreferenceHelper.saveProfile(course);

                                            // Cancel the timer thread when a student is correctly identified,
                                            // otherwise it keeps running even when we leave the activity.
                                            cancel();
                                            CardScanActivity.this.finish();
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        }
                                    };
                                    thread.start();
                                    thread.join();
                                }
                                else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(CardScanActivity.this, "Error inserting student in exam table. Please try again.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    Toast.makeText(CardScanActivity.this, "Error assigning seat to student. Please try again.",
                                            Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        else {
                            int seat = dbHelper.getStudentSeat(student, course);
                            if (seat != -1) {
                                String name = student.getFirstName() + " " + student.getLastName();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        studentName.setText(name);
                                        studentID.setText(Integer.toString((int)student.getId()));
                                        seatNumber.setText(Integer.toString(seat));
                                        Drawable drawable = ContextCompat.getDrawable(
                                                CardScanActivity.this, R.drawable.success);
                                        imageView.setImageDrawable(drawable);
                                        Toast.makeText(CardScanActivity.this,
                                                "Student with ID: " + student.getId() + " has has already been confirmed. " +
                                                        "Returning to previous page.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                                Thread thread = new Thread(){
                                    @Override
                                    public void run() {
                                    try {
                                        Thread.sleep(3000);

                                        sharedPreferenceHelper.saveProfile(course);

                                        // Cancel the timer thread when a student is correctly identified,
                                        // otherwise it keeps running even when we leave the activity.
                                        cancel();
                                        CardScanActivity.this.finish();
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    }
                                };
                                thread.start();
                                thread.join();
                            }
                        }
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                studentName.setText("N/A");
                                studentID.setText("N/A");
                                seatNumber.setText("N/A");
                                Drawable drawable = ContextCompat.getDrawable(
                                        CardScanActivity.this, R.drawable.failure);
                                imageView.setImageDrawable(drawable);
                                Toast.makeText(CardScanActivity.this,
                                        "Cannot identify student. Please try again.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                }
            }, 0, 1000);
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }
}
