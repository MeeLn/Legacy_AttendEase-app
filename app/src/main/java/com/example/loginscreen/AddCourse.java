package com.example.loginscreen;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddCourse extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText course_name,course_credit;
    private Button add;
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_course);

        dbHelper = new DatabaseHelper(this);

        toolbar = findViewById(R.id.mytoolbar);
        course_name = findViewById(R.id.course_name);
        course_credit = findViewById(R.id.course_credit);
        add = findViewById(R.id.addcourse);

        setSupportActionBar(toolbar);

        add.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View view) {
                //insert course
                addCourse();
                Intent intent = new Intent(AddCourse.this,AdminDashboard.class);
                startActivity(intent);
                finish();
            }
        });

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddCourse.this, AdminDashboard.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.back_button) {
                Intent intent = new Intent(AddCourse.this,AdminDashboard.class);
                startActivity(intent);
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private void addCourse() {
        String courseNameValue = course_name.getText().toString();
        String courseCreditValue = course_credit.getText().toString();


        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_COURSE_NAME, courseNameValue);
        values.put(DatabaseHelper.COLUMN_COURSE_CREDITS, courseCreditValue);

        long newRowId = db.insert(DatabaseHelper.TABLE_COURSE, null, values);

        if (newRowId != -1) {
            Toast.makeText(AddCourse.this, "Course Added successfully", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(AddCourse.this, "Error error adding course", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        course_name.setText("");
        course_credit.setText("");
    }
}