package edu.coen390.androidapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.examapp.R;

public class ProfessorActivity extends AppCompatActivity {
    public static final String COURSE_INTENT = "COURSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor);
    }
}