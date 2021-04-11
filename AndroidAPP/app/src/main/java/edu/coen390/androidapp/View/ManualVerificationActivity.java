package edu.coen390.androidapp.View;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Controller.SharedPreferenceHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.Source;
import edu.coen390.androidapp.Model.Student;
import edu.coen390.androidapp.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_verification);
        setupUI();

        Intent intent = getIntent();
        course = (Course) intent.getSerializableExtra(InvigilatorActivity.COURSE_INTENT);

        sharedPreferenceHelper = new SharedPreferenceHelper(ManualVerificationActivity.this);
        Course retrievedCourse = sharedPreferenceHelper.getProfile(course);
        if (retrievedCourse != null) {
            course = retrievedCourse;
        }
    }

    protected void onStart() {
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
        backButton.setOnClickListener(v -> {
            Toast.makeText(this, "Manual Verification Cancelled.", Toast.LENGTH_SHORT).show();
            endActivity(course);
        });
        dbHelper = new DatabaseHelper(this);
    }

    private void searchStudent() {
        verifyButton.setOnClickListener(v -> {
            boolean isStudentConfirmed;

            try {
                long Id = Integer.parseInt(studId.getText().toString());
                Student student = dbHelper.getStudent(Id);

                if (student != null) {
                    isStudentConfirmed = dbHelper.isStudentRegisteredInCourse(student, course);
                } else {
                    isStudentConfirmed = false;
                }

                boolean isStudentSeated;
                if(student != null){
                    isStudentSeated  = dbHelper.isStudentSeated(student, course);
                }else{
                    isStudentSeated = false;
                }

                if (isStudentConfirmed) {
                    if (!isStudentSeated) {
                        int seat = course.getSeats().getNextSeat(student);
                        if (seat != -1) {
                            int status = dbHelper.insertInExamTable(student, course, seat);
                            if (status == 1) {
                                studentName.setText(String.format("%s %s", student.getFirstName(), student.getLastName()));
                                studentID.setText(Integer.toString((int) student.getId()));
                                seatNumber.setText(Integer.toString(seat));
                                Drawable drawable = ContextCompat.getDrawable(
                                        ManualVerificationActivity.this, R.drawable.success);
                                imageView.setImageDrawable(drawable);
                                Toast.makeText(ManualVerificationActivity.this,
                                        "Student with ID: " + student.getId() + " has been successfully confirmed. " +
                                                "Returning to previous page.",
                                        Toast.LENGTH_LONG).show();
                                Thread thread = new Thread(() -> {
                                    try {
                                        Thread.sleep(3000);
                                            endActivity(course);


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                                thread.start();
                            }
                        }
                    } else {
                        int seat = dbHelper.getStudentSeat(student, course);
                        if (seat != -1) {
                            studentName.setText(String.format("%s %s", student.getFirstName(), student.getLastName()));
                            studentID.setText(Integer.toString((int) student.getId()));
                            seatNumber.setText(Integer.toString(seat));
                            Drawable drawable = ContextCompat.getDrawable(
                                    ManualVerificationActivity.this, R.drawable.success);
                            imageView.setImageDrawable(drawable);
                            Toast.makeText(ManualVerificationActivity.this,
                                    "Student with ID: " + student.getId() + " has has already been confirmed. " +
                                            "Returning to previous page.",
                                    Toast.LENGTH_LONG).show();
                            Thread thread = new Thread(() -> {
                                try {
                                    Thread.sleep(3000);

                                    endActivity(course);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            thread.start();
                        }
                    }
                } else {
                    studentName.setText("N/A");
                    studentID.setText("N/A");
                    seatNumber.setText("N/A");
                    Drawable drawable = ContextCompat.getDrawable(
                            ManualVerificationActivity.this, R.drawable.failure);
                    imageView.setImageDrawable(drawable);
                    Toast.makeText(ManualVerificationActivity.this,
                            "Cannot identify student. Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Log.d(TAG, "Exception: " + e.getMessage());
            }
        });
    }

    private void endActivity(Course course) {
        sharedPreferenceHelper.saveProfile(course);
        sharedPreferenceHelper.saveSource(Source.MANUALVERIFICATION_ACTIVITY);
        sharedPreferenceHelper.saveCourseCode(course.getCode());
        ManualVerificationActivity.this.finish();
    }
}