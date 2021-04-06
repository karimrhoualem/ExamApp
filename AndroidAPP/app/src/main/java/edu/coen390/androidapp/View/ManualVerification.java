package edu.coen390.androidapp.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
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

import java.util.Timer;
import java.util.TimerTask;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.Student;

public class ManualVerification extends AppCompatActivity {

    /**
     * Tag used for logger.
     */
    private static final String TAG = "ManualVerificationActivity";
    private TextView studentName;
    private TextView studentID;
    private TextView seatNumber;
    private ImageView imageView;
    private TextInputEditText studId;
    private Button verifyButton;
    private Button backButton;
    private Button saveButton;
    private Course course;
    private DatabaseHelper dbHelper;
    private int seat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_verification);
        setupUI();
        setButtonListeners();

    }

    protected void onStart () {
        super.onStart();

        Log.d(TAG, "On Start");
        addStudentToDatabase();
        searchStudent();
        saveStudent();
    }

    private void setButtonListeners() {
        backButton.setOnClickListener(v -> ManualVerification.this.finish());

        saveButton.setOnClickListener(v -> {
            // TODO: Save student information and seat number in a new DB table for the current course.

            // TODO: Display a toast that shows successful save.

            // TODO: Return to previous activity.
            //LiveFeedActivity.this.finish();
        });
    }

    private void setupUI() {
        studId = findViewById(R.id.txt_studentId);
        verifyButton = (Button) findViewById(R.id.verifyButton);
        studentName = findViewById(R.id.studentNameTextView);
        studentID = findViewById(R.id.studentIDTextView);
        seatNumber = findViewById(R.id.seatNumberTextView);
        imageView = findViewById(R.id.successMessageImageView);
        backButton = findViewById(R.id.backButton);
        saveButton = findViewById(R.id.saveButton);

        // Receive Course object when CourseActivity intent begins LiveFeedActivity
        // For now, just using a manually created Course object
        // https://stackoverflow.com/a/7827593/12044281
        Intent intent = getIntent();
        course = (Course) intent.getSerializableExtra(VerificationModeActivity.COURSE_INTENT);
        

        Log.d(TAG, "after getIntent " + course.toString());

        dbHelper = new DatabaseHelper(this);


    }

    protected void addStudentToDatabase() {

        Student obama = new Student(45454545, new String[]{"ENGR 391", "COMP 472"}, "Barack", "Obama");
        Student tawfiq = new Student(40000390, new String[]{"ENGR 391", "COEN 313"}, "Tawfiq", "Jawhar");
        Student hamill = new Student(40102453, new String[]{"ENGR 391", "COEN 313"}, "Mark", "Hamill");
        Student alec = new Student(40103773, new String[]{"ENGR 391", "COEN 313"}, "Alec", "Wolfe");

        long result = dbHelper.insertStudent(obama);
        long result2 = dbHelper.insertStudent(tawfiq);
        long result3 = dbHelper.insertStudent(hamill);
        long result4 = dbHelper.insertStudent(alec);

        Log.d(TAG, "result: " + result);

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

    }


    // TODO ????
    private void saveStudent() {

        int finalSeat = seat;
        saveButton.setOnClickListener(v -> {

            //String toastText = String.format("%s %s is verified and seat %d is assigned", student.getFirstName(), student.getLastName(), finalSeat);
            //Toast.makeText(ManualVerification.this, toastText, Toast.LENGTH_LONG).show();
            studentName.setText("N/A");
            studentID.setText("N/A");
            seatNumber.setText("N/A");
            Toast.makeText(ManualVerification.this, "READY TO VERIFY", Toast.LENGTH_LONG).show();
            //recreate();
        });

    }

    private void searchStudent () {
        
        verifyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Search for the student in the Students DB table and verify
                // whether they are enrolled in this course.

                boolean isStudentConfirmed;


                try {
                    long Id = Integer.parseInt(studId.getText().toString());
                    Log.d(TAG, "Id: " + Id);
                    Student student = dbHelper.getStudent(Id);
                    Log.d(TAG, "Student: " + student.getFirstName());

                    if (student != null) {
                        isStudentConfirmed = dbHelper.isStudentRegisteredInCourse(student, course);
                    } else {
                        isStudentConfirmed = false;
                    }

                    boolean isStudentSeated = dbHelper.isStudentSeated(student, course);
                    String studentSeat = "N/A";
                    seat = 0;
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
                                ManualVerification.this, R.drawable.success);
                        imageView.setImageDrawable(drawable);
                        saveButton.setEnabled(true);
                        saveButton.setClickable(true);
                    } else {
                        studentName.setText("N/A");
                        studentID.setText("N/A");
                        seatNumber.setText("N/A");
                        Drawable drawable = ContextCompat.getDrawable(
                                ManualVerification.this, R.drawable.failure);
                        imageView.setImageDrawable(drawable);
                        saveButton.setEnabled(false);
                        saveButton.setClickable(false);
                    }

                        /*if (isStudentSeated) {
                            seatNumber.setText(studentSeat);
                        }*/

                }catch(NumberFormatException e){
                    Log.d(TAG, "Exception: " + e.getMessage());
                }
            }
        });
    }


}

