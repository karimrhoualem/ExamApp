package edu.coen390.androidapp.View;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.examapp.R;
import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.User;



public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private WebView myWebView;
    private Button login;
    private DatabaseHelper dbHelper;
    private EditText loginUserName, loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "Creating DB helper object.");

        dbHelper = new DatabaseHelper(this);
        //dbHelper.testMethod(); // Test method. Must call getWritableDatabase() or getReadableDatabase() for DB to be created.

        Log.d(TAG, "DB helper object created.");

        dbHelper.insertCourse(new Course(-1, 1,"Numerical Methods", "ENGR 391", 100));
        dbHelper.insertCourse(new Course(-1, 2,"Fundamentals of Electrical Power", "ELEC 331", 50));
        dbHelper.insertCourse(new Course(-1, 1,"Digital Systems Design II", "COEN 313", 40));


        newInvigilator();
        setupUI();
        loginUser();
    }

    private void setupUI (){

        loginUserName = findViewById(R.id.UserName);
        loginPassword = findViewById(R.id.Password);
        login = findViewById(R.id.Login);
    }

    private void loginUser(){
        login.setOnClickListener(v -> {
            boolean verification = dbHelper.verifyInvigilator(loginUserName.getText().toString() , loginPassword.getText().toString());
            if (verification){
                openCourseActivity();
            }
            else{
                Toast.makeText(LoginActivity.this, "User Name or Password is incorrect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void newInvigilator(){
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

        // Testing  getInvigilator function - It works

        //user1 = dbHelper.getInvigilator(1);
        //Log.d(TAG, "User: " + " " + user1.getFirstName() + " " + user1.getLastName() + " " + user1.getUserName()
        //    + " " + user1.getPassword());

        dbHelper.close();
    }
    private void openCourseActivity() {
        int id = 1;
        Intent intent = new Intent (this, CourseActivity.class);
        //intent.putExtra("invigilator_id",id);
        //Log.d(TAG,"after putExtra" + id);
        User user = dbHelper.getInvigilator(1);
        intent.putExtra("invigilatorObject", user);
        startActivity(intent);
    }
}

