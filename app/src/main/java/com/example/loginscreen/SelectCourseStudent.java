package com.example.loginscreen;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SelectCourseStudent extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout courseLayout;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private String studentEmail;
    private long studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_course_student);

        toolbar = findViewById(R.id.toolbar_select_course);
        courseLayout = findViewById(R.id.course_layout);

        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(SelectCourseStudent.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Retrieve student ID from email
        studentEmail = sessionManager.getEmail();
        studentId = databaseHelper.getStudentIdByEmail(studentEmail);

        if (studentId == -1) {
            // Handle case where student ID could not be found
            Intent intent = new Intent(SelectCourseStudent.this, MainActivity.class);
            startActivity(intent);
            finish();
            return; // Exit if ID is invalid
        }

        loadActiveCourses();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SelectCourseStudent.this, StudentDashboard.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.back_button) {
            sessionManager.setRecognized(false);
            Intent intent = new Intent(SelectCourseStudent.this, StudentDashboard.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadActiveCourses() {
        Cursor cursor = databaseHelper.getActiveCourses();
        if (cursor.moveToFirst()) {
            do {
                long courseId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_ID));
                String courseName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_NAME));

                Button courseButton = new Button(this);
                courseButton.setText(courseName);
                courseButton.setOnClickListener(v -> {
                    boolean success = databaseHelper.markAttendance(studentId, courseId);
                    if (success) {
                        if(sessionManager.isRecognized()) {
                            courseButton.setText(courseName + " (Attended)");
                        }else{
                            courseButton.setText(courseName + " (Unattended)");
                        }
                    } else {
                        Toast.makeText(this, "Attendance already taken for " + courseName, Toast.LENGTH_SHORT).show();
                    }
                });
                courseLayout.addView(courseButton);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}