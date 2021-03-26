package edu.coen390.androidapp.View;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.graphics.drawable.Drawable;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.examapp.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import edu.coen390.androidapp.Controller.DatabaseHelper;
import edu.coen390.androidapp.Model.Course;
import edu.coen390.androidapp.Model.HttpRequest;
import edu.coen390.androidapp.Model.Student;
import static android.app.PendingIntent.getActivity;
import edu.coen390.androidapp.Model.Course;


public class LiveFeedActivity extends AppCompatActivity {

    /**
     * Tag used for logger.
     */
    private static final String TAG = "LiveFeedActivity";
    public static final String KEY_URL_TO_LOAD = "KEY_URL_TO_LOAD";


    //TODO: change URLs
    @VisibleForTesting
    public static final String WEB_FORM_URL = "http://192.168.0.167:5000/";
    public static final String JSON_STUDENT_URL = "http://192.168.0.167:5000/";

    private JSONObject studentInformation;
    private WebView myWebView;
    private DatabaseHelper dbHelper;
    private Course course;
    private TextView studentName;
    private TextView studentID;
    private TextView successMessage;
    private TextView seatNumber;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_feed);

        studentName = findViewById(R.id.studentNameTextView);
        studentID = findViewById(R.id.studentIDTextView);
        successMessage = findViewById(R.id.succesMessageTextView);
        seatNumber = findViewById(R.id.seatNumberTextView);
        imageView = findViewById(R.id.successMessageImageView);

        // Receive Course object when CourseActivity intent begins LiveFeedActivity
        // For now, just using a manually created Course object
        // https://stackoverflow.com/a/7827593/12044281
        Intent intent = getIntent();
        course = (Course)intent.getSerializableExtra(VerificationModeActivity.COURSE_INTENT);

        Log.d(TAG,"after getIntent "+ course.toString());

        dbHelper = new DatabaseHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        launchWebView();

        // TODO: Remove this manual student entry later
        Student me = new Student(26603157, new String[]{"ENGR 391", "COMP 472"}, "Karim", "Rhoualem");
        long result = dbHelper.insertStudent(me);

        if (result == -1) {
            Log.d(TAG, "Error inserting student into DB table.");
        }
        else {
            Log.d(TAG, "Student successfully inserted into DB: \n"
                    + me.toString());
        }

        try {
            // Get student information via an asynchronous JSON http request
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try  {
                        studentInformation = HttpRequest.getJSONObjectFromURL(JSON_STUDENT_URL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            thread.join();

            Student student = getStudentFromJSONObject(studentInformation);

            // Search for the student in the Students DB table and verify whether they are enrolled in this course.
            boolean isStudentConfirmed;
            if (student != null) {
                isStudentConfirmed = dbHelper.isStudentRegisteredInCourse(student, course);
            } else {
                isStudentConfirmed = false;
            }

            // Display student information and confirmation status
            if (isStudentConfirmed) {
                studentName.setText(student.getFirstName() + " " + student.getLastName());
                studentID.setText(Integer.toString(student.getID()));
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.success);
                imageView.setImageDrawable(drawable);
            } else {
                studentName.setText("N/A");
                studentID.setText("N/A");
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.failure);
                imageView.setImageDrawable(drawable);
            }

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void launchWebView(){
        myWebView = (WebView) findViewById(R.id.webView);

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
            String studentFirstName = studentInformation.getString("firstName");
            String studentLastName = studentInformation.getString("lastName");

            return new Student(studentID, null, studentFirstName, studentLastName);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}