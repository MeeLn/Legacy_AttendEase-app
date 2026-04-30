package com.example.loginscreen;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddDepartment extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText department_name;
    private Button add_department;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_department);

        toolbar = findViewById(R.id.toolbar_department);
        add_department = findViewById(R.id.add_department);
        department_name = findViewById(R.id.department_name);

        dbHelper = new DatabaseHelper(this);

        setSupportActionBar(toolbar);

        add_department.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //insert department
                addDepartment();
                Intent intent = new Intent(AddDepartment.this,AdminDashboard.class);
                startActivity(intent);
                finish();
            }
        });


    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddDepartment.this, AdminDashboard.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);

        return true;
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.back_button) {
            Intent intent = new Intent(AddDepartment.this,AdminDashboard.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addDepartment() {
        String departmentNameValue = department_name.getText().toString();


        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DEPARTMENT_NAME, departmentNameValue);

        long newRowId = db.insert(DatabaseHelper.TABLE_DEPARTMENT, null, values);

        if (newRowId != -1) {
            Toast.makeText(AddDepartment.this, "Department Added successfully", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(AddDepartment.this, "Error error adding department", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        department_name.setText("");
    }
}