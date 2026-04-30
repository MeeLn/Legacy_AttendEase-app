package com.example.loginscreen;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SelectCourse extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout courseLayout;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_course);

        toolbar = findViewById(R.id.toolbar_select_course);
        courseLayout = findViewById(R.id.course_layout); // Reference to the LinearLayout

        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);

        loadCourses();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SelectCourse.this, TeacherDashboard.class);
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
            Intent intent = new Intent(SelectCourse.this, TeacherDashboard.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCourses() {
        Cursor cursor = databaseHelper.getCourseData();
        if (cursor.moveToFirst()) {
            do {
                long courseId = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_ID));
                String courseName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_NAME));
                String courseStatus = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_STATUS));

                Button courseButton = new Button(this);
                courseButton.setText(courseName + " (" + courseStatus + ")");
                courseButton.setOnClickListener(v -> {
                    // Toggle course status
                    boolean success = databaseHelper.toggleCourseStatus(courseId);
                    if (success) {
                        // Update button text to reflect new status
                        Cursor updatedCursor = databaseHelper.getCourseById(courseId);
                        if (updatedCursor.moveToFirst()) {
                            String updatedStatus = updatedCursor.getString(updatedCursor.getColumnIndex(DatabaseHelper.COLUMN_COURSE_STATUS));
                            courseButton.setText(courseName + " (" + updatedStatus + ")");
                        }
                        updatedCursor.close();
                    }
                });
                courseLayout.addView(courseButton);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}