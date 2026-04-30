package com.example.loginscreen;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ViewAttendance extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editTextRollNo;
    private Button buttonGetAttendance;
    private TextView textViewAttendance;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_attendance);

        toolbar = findViewById(R.id.toolbar_view_attendance);
        setSupportActionBar(toolbar);

        editTextRollNo = findViewById(R.id.edit_text_roll_no);
        buttonGetAttendance = findViewById(R.id.button_get_attendance);
        textViewAttendance = findViewById(R.id.text_view_attendance);

        databaseHelper = new DatabaseHelper(this);

        buttonGetAttendance.setOnClickListener(v -> {
            String rollNo = editTextRollNo.getText().toString().trim();
            if (!rollNo.isEmpty()) {
                long studentId = databaseHelper.getStudentIdByRollNo(rollNo);
                if (studentId != -1) {
                    displayAttendance(studentId);
                } else {
                    textViewAttendance.setText("Student not found.");
                }
            } else {
                textViewAttendance.setText("Please enter a roll number.");
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewAttendance.this, TeacherDashboard.class);
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
            Intent intent = new Intent(ViewAttendance.this, TeacherDashboard.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayAttendance(long studentId) {
        Cursor cursor = databaseHelper.getAttendanceForStudent(studentId);
        if (cursor.moveToFirst()) {
            StringBuilder attendanceDetails = new StringBuilder();
            do {
                long courseId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_ID));
                String courseName = databaseHelper.getCourseNameById(courseId);
                String date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ATTENDANCE_DATE));
                attendanceDetails.append("Course: ").append(courseName).append(", Date: ").append(date).append("\n");
            } while (cursor.moveToNext());
            textViewAttendance.setText(attendanceDetails.toString());
        } else {
            textViewAttendance.setText("No attendance records found.");
        }
        cursor.close();
    }
}