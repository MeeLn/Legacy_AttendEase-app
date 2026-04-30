package com.example.loginscreen;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ViewAttendanceStudent extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editTextCourseName;
    private Button buttonShowAttendance;
    private TextView textViewAttendance;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private long studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance_student);

        toolbar = findViewById(R.id.toolbar_view_attendance);
        editTextCourseName = findViewById(R.id.editTextCourseName);
        buttonShowAttendance = findViewById(R.id.buttonShowAttendance);
        textViewAttendance = findViewById(R.id.textViewAttendance);

        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(ViewAttendanceStudent.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Retrieve student ID from the session
        studentId = databaseHelper.getStudentIdByEmail(sessionManager.getEmail());

        buttonShowAttendance.setOnClickListener(v -> showAttendance());
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewAttendanceStudent.this, StudentDashboard.class);
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
            Intent intent = new Intent(ViewAttendanceStudent.this, StudentDashboard.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAttendance() {
        String courseName = editTextCourseName.getText().toString().trim();

        if (courseName.isEmpty()) {
            textViewAttendance.setText("Please enter a course name.");
            return;
        }

        long courseId = databaseHelper.getCourseIdByName(courseName);
        if (courseId == -1) {
            textViewAttendance.setText("Course not found.");
            return;
        }

        Cursor cursor = databaseHelper.getAttendanceForStudentInCourse(studentId, courseId);
        if (cursor != null && cursor.moveToFirst()) {
            StringBuilder attendanceDetails = new StringBuilder();
            do {
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ATTENDANCE_DATE));
                attendanceDetails.append("Date: ").append(date).append("\n");
            } while (cursor.moveToNext());
            cursor.close();
            textViewAttendance.setText(attendanceDetails.toString());
        } else {
            textViewAttendance.setText("No attendance records found for this course.");
        }
    }
}