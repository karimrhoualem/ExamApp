package edu.coen390.androidapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.coen390.androidapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Controller.SharedPreferenceHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.Source;
import edu.coen390.androidapp.Model.Student;

public class ManualVerificationActivity extends AppCompatActivity {
    private static final String TAG = "ManualVerificationActivity";
    private TextView studentName;
    private TextView studentID;
    private TextView seatNumber;
    private ImageView imageView;
    private TextInputEditText studId;
    private Button verifyButton;
    private Button backButton;
    private Course course;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private Thread thread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_verification);
        setupUI();

        Intent intent = getIntent();
        course = (Course) intent.getSerializableExtra(InvigilatorActivity.COURSE_INTENT);

        sharedPreferenceHelper =
                new SharedPreferenceHelper(ManualVerificationActivity.this);
        Course retrievedCourse = sharedPreferenceHelper.getProfile(course);
        if (retrievedCourse != null) {
            course = retrievedCourse;
        }
    }

    @Override
    protected void onStart () {
        super.onStart();

        Log.d(TAG, "On Start");
        searchStudent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item){
        switch (item.getItemId())
        {
            case R.id.logout:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            case android.R.id.home:
                endActivity(course);
                return false;
            default:
                return  super.onOptionsItemSelected(item);
        }

    }

    private void setupUI() {
        studId = findViewById(R.id.txt_studentId);
        verifyButton = findViewById(R.id.verifyButton);
        studentName = findViewById(R.id.studentNameTextView);
        studentID = findViewById(R.id.studentIDTextView);
        seatNumber = findViewById(R.id.seatNumberTextView);
        imageView = findViewById(R.id.successMessageImageView);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->{
            Toast.makeText(this,
                    "Manual Verification Cancelled.", Toast.LENGTH_SHORT).show();
            endActivity(course);
        });
        dbHelper = new DatabaseHelper(this);
    }

    private void searchStudent () {
        verifyButton.setOnClickListener(v -> {
            boolean isStudentConfirmed;

            try {
                long Id = Integer.parseInt(studId.getText().toString());
                Student student = dbHelper.getStudent(Id);

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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        studentName.setText(
                                                student.getFirstName()
                                                        + " " + student.getLastName());
                                        studentID.setText(Integer.toString((int)student.getId()));
                                        seatNumber.setText(Integer.toString(seat));
                                        Drawable drawable = ContextCompat.getDrawable(
                                                ManualVerificationActivity.this,
                                                R.drawable.success);
                                        imageView.setImageDrawable(drawable);
                                        Toast.makeText(ManualVerificationActivity.this,
                                                "Student with ID: " + student.getId()
                                                        + " has been successfully confirmed. " +
                                                        "Returning to previous page.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                                thread = new Thread(() -> {
                                    try {
                                        Thread.sleep(3000);
                                        endActivity(course);
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                                thread.start();
                            }
                        }
                    }
                    else {
                        int seat = dbHelper.getStudentSeat(student, course);
                        if (seat != -1) {
                            runOnUiThread(() -> {
                                studentName.setText(student.getFirstName() + " "
                                        + student.getLastName());
                                studentID.setText(Integer.toString((int)student.getId()));
                                seatNumber.setText(Integer.toString(seat));
                                Drawable drawable = ContextCompat.getDrawable(
                                        ManualVerificationActivity.this,
                                        R.drawable.success);
                                imageView.setImageDrawable(drawable);
                                Toast.makeText(ManualVerificationActivity.this,
                                        "Student with ID: " + student.getId()
                                                + " has has already been confirmed. " +
                                                "Returning to previous page.",
                                        Toast.LENGTH_LONG).show();
                            });
                            thread = new Thread(() -> {
                                try {
                                    Thread.sleep(3000);
                                    endActivity(course);
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            thread.start();
                        }
                    }
                }
                else {
                    runOnUiThread(() -> {
                        studentName.setText("N/A");
                        studentID.setText("N/A");
                        seatNumber.setText("N/A");
                        Drawable drawable = ContextCompat.getDrawable(
                                ManualVerificationActivity.this, R.drawable.failure);
                        imageView.setImageDrawable(drawable);
                        Toast.makeText(ManualVerificationActivity.this,
                                "Cannot identify student. Please try again.",
                                Toast.LENGTH_SHORT).show();
                    });
                    thread = new Thread(){
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                endActivity(course);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                }
            }
            catch(NumberFormatException e){
                Log.d(TAG, "Exception: " + e.getMessage());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        saveInfo(course);
    }

    private void endActivity(Course course) {
        saveInfo(course);
        ManualVerificationActivity.this.finish();
    }

    private void saveInfo(Course course) {
        sharedPreferenceHelper.saveProfile(course);
        sharedPreferenceHelper.saveSource(Source.MANUALVERIFICATION_ACTIVITY);
        sharedPreferenceHelper.saveCourseCode(course.getCode());
    }
}