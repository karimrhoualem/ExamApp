package edu.coen390.androidapp.View;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.examapp.R;
import com.google.android.material.textfield.TextInputEditText;

import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Controller.SharedPreferenceHelper;
import edu.coen390.androidapp.Model.Invigilator;
import edu.coen390.androidapp.Model.Professor;
import edu.coen390.androidapp.Model.User;
import edu.coen390.androidapp.Model.UserType;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private Button login;
    private DatabaseHelper dbHelper;
    private TextInputEditText userNameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "Creating DB helper object.");
        dbHelper = new DatabaseHelper(this);
        Log.d(TAG, "DB helper object created.");

        dbHelper.createTestData();  //TODO: to be replaced when cloud database is setup.
        setupUI();
        setupUserLogin();
    }

    private void setupUI() {
        userNameEditText = findViewById(R.id.txt_user);
        passwordEditText = findViewById(R.id.txt_password);
        login = findViewById(R.id.Login);
    }

    private void setupUserLogin() {
        login.setOnClickListener(v -> {
            Editable username = userNameEditText.getText();
            Editable password = passwordEditText.getText();

            if (username != null && password != null) {
                boolean isInvigilator = dbHelper.verifyInvigilator(username.toString(), password.toString());
                if (isInvigilator) {
                    openCourseActivity(UserType.INVIGILATOR);
                    return;
                }

                boolean isProfessor = dbHelper.verifyProfessor(username.toString(), password.toString());
                if (isProfessor) {
                    openCourseActivity(UserType.PROFESSOR);
                    return;
                }

                username.clear();
                password.clear();
                Toast.makeText(LoginActivity.this, "Username or Password is incorrect", Toast.LENGTH_SHORT).show();
            }
            else {
                if (username != null) {
                    username.clear();
                }
                if (password != null) {
                    password.clear();
                }
                Toast.makeText(LoginActivity.this, "Please enter a valid Username and Password.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openCourseActivity(UserType userType) {
        Intent intent = new Intent(this, CourseActivity.class);
        SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(LoginActivity.this);
        User user = null;
        if (userType == UserType.INVIGILATOR) {
            user = (Invigilator) dbHelper.getInvigilator(userNameEditText.getText().toString());
            intent.putExtra(CourseActivity.CourseIntentKey, user);
        }
        else if (userType == UserType.PROFESSOR){
            user = (Professor) dbHelper.getProfessor(userNameEditText.getText().toString());
            intent.putExtra(CourseActivity.CourseIntentKey, user);
        }
        sharedPreferenceHelper.saveUser(user, userType);
        startActivity(intent);
    }
}

