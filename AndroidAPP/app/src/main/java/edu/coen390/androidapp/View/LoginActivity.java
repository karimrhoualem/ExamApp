package edu.coen390.androidapp.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.examapp.R;
import com.google.android.material.textfield.TextInputEditText;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.User;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private Button login;
    private DatabaseHelper dbHelper;
    private TextInputEditText loginUserName, loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "Creating DB helper object.");

        dbHelper = new DatabaseHelper(this);
        //dbHelper.testMethod(); // Test method. Must call getWritableDatabase() or getReadableDatabase() for DB to be created.

        Log.d(TAG, "DB helper object created.");

        dbHelper.insertCourse(new Course(-1, 1, "Numerical Methods", "ENGR 391", 100));
        dbHelper.insertCourse(new Course(-1, 2, "Fundamentals of Electrical Power", "ELEC 331", 50));
        dbHelper.insertCourse(new Course(-1, 1, "Digital Systems Design II", "COEN 313", 40));


        newInvigilator();
        setupUI();
        loginUser();
    }

    private void setupUI() {

        loginUserName = findViewById(R.id.txt_user);
        loginPassword = findViewById(R.id.txt_password);
        login = findViewById(R.id.Login);
    }

    private void loginUser() {
        login.setOnClickListener(v -> {
            boolean verification = dbHelper.verifyInvigilator(loginUserName.getText().toString(), loginPassword.getText().toString());
            if (verification) {
                openCourseActivity();
            } else {
                Toast.makeText(LoginActivity.this, "Username or Password is incorrect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void newInvigilator() {
        String firstNameP1 = "John";
        String lastNameP1 = "Doe";
        String userNameP1 = "j_doe";
        String passwordP1 = "1234";
        String firstNameP2 = "Karen";
        String lastNameP2 = "Land";
        String userNameP2 = "k_lan";
        String passwordP2 = "5678";

        long id = -1;
        User user1 = new User(id, firstNameP1, lastNameP1, userNameP1, passwordP1);
        User user2 = new User(id, firstNameP2, lastNameP2, userNameP2, passwordP2);

        // Insert invigilator into database

        dbHelper.addInvigilator(user1);
        dbHelper.addInvigilator(user2);
        dbHelper.close();
    }

    private void openCourseActivity() {

        Intent intent = new Intent(this, CourseActivity.class);
        //intent.putExtra("invigilator_id",id);
        //Log.d(TAG,"after putExtra" + id);
        User user = dbHelper.getInvigilator("j_doe");
        intent.putExtra("invigilatorObject", user);
        startActivity(intent);
    }
}

