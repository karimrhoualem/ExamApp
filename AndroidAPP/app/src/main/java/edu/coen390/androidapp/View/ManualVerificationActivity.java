package edu.coen390.androidapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.examapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Model.Course;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_verification);
        setupUI();

        Intent intent = getIntent();
        course = (Course) intent.getSerializableExtra(InvigilatorActivity.COURSE_INTENT);

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(course.getCode(), "");
        if (!json.equals("")) {
            Gson gson = new Gson();
            course = gson.fromJson(json, Course.class);
        }
    }

    protected void onStart () {
        super.onStart();

        Log.d(TAG, "On Start");
        searchStudent();
    }

    private void setupUI() {
        studId = findViewById(R.id.txt_studentId);
        verifyButton = (Button) findViewById(R.id.verifyButton);
        studentName = findViewById(R.id.studentNameTextView);
        studentID = findViewById(R.id.studentIDTextView);
        seatNumber = findViewById(R.id.seatNumberTextView);
        imageView = findViewById(R.id.successMessageImageView);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v ->{
            Toast.makeText(this, "Card Scan Activity Cancelled.", Toast.LENGTH_SHORT).show();
            ManualVerificationActivity.this.finish();
        });

        dbHelper = new DatabaseHelper(this);
    }

    private void searchStudent () {
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                    studentName.setText(student.getFirstName() + " " + student.getLastName());
                                    studentID.setText(Integer.toString((int)student.getId()));
                                    seatNumber.setText(Integer.toString(seat));
                                    Drawable drawable = ContextCompat.getDrawable(
                                            ManualVerificationActivity.this, R.drawable.success);
                                    imageView.setImageDrawable(drawable);
                                    Toast.makeText(ManualVerificationActivity.this,
                                            "Student with ID: " + student.getId() + " has been successfully confirmed. " +
                                                    "Returning to previous page.",
                                            Toast.LENGTH_LONG).show();
                                    Thread thread = new Thread(){
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(3000);

                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                Gson gson = new Gson();
                                                String json = gson.toJson(course);
                                                editor.putString(course.getCode(), json);
                                                editor.apply();

                                                ManualVerificationActivity.this.finish();
                                            }
                                            catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    thread.start();
                                }
                            }
                        }
                        else {
                            int seat = dbHelper.getStudentSeat(student, course);
                            if (seat != -1) {
                                studentName.setText(student.getFirstName() + " " + student.getLastName());
                                studentID.setText(Integer.toString((int)student.getId()));
                                seatNumber.setText(Integer.toString(seat));
                                Drawable drawable = ContextCompat.getDrawable(
                                        ManualVerificationActivity.this, R.drawable.success);
                                imageView.setImageDrawable(drawable);
                                Toast.makeText(ManualVerificationActivity.this,
                                        "Student with ID: " + student.getId() + " has has already been confirmed. " +
                                                "Returning to previous page.",
                                        Toast.LENGTH_LONG).show();
                                Thread thread = new Thread(){
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(3000);

                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            Gson gson = new Gson();
                                            String json = gson.toJson(course);
                                            editor.putString(course.getCode(), json);
                                            editor.apply();
                                            ManualVerificationActivity.this.finish();
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                thread.start();
                            }
                        }
                    }
                    else {
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
                }
                catch(NumberFormatException e){
                    Log.d(TAG, "Exception: " + e.getMessage());
                }
            }
        });
    }
}

