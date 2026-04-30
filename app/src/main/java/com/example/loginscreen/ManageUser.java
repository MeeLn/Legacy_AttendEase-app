package com.example.loginscreen;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class ManageUser extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listViewUsers;
    private Button buttonRefresh;
    private DatabaseHelper databaseHelper;
    private ArrayAdapter<String> userAdapter;
    private ArrayList<String> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        toolbar = findViewById(R.id.toolbar_manage);
        listViewUsers = findViewById(R.id.listViewUsers);
        buttonRefresh = findViewById(R.id.buttonRefresh);

        setSupportActionBar(toolbar);

        databaseHelper = new DatabaseHelper(this);

        userList = new ArrayList<>();
        userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        listViewUsers.setAdapter(userAdapter);

        loadUsers();

        buttonRefresh.setOnClickListener(v -> loadUsers());

        listViewUsers.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUser = userList.get(position);
            String[] parts = selectedUser.split(" - ");
            String userType = parts[0];
            long userId = Long.parseLong(parts[1]);

            boolean success = databaseHelper.toggleUserStatus(userType, userId);
            if (success) {
                Toast.makeText(ManageUser.this, "Status updated successfully", Toast.LENGTH_SHORT).show();
                loadUsers(); // Refresh the user list
            } else {
                Toast.makeText(ManageUser.this, "Error updating status", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ManageUser.this, AdminDashboard.class);
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
            Intent intent = new Intent(ManageUser.this, AdminDashboard.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUsers() {
        userList.clear();
        Cursor cursorStudents = databaseHelper.getAllStudents();
        Cursor cursorTeachers = databaseHelper.getAllTeachers();

        while (cursorStudents.moveToNext()) {
            String studentId = cursorStudents.getString(cursorStudents.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_ID));
            String studentName = cursorStudents.getString(cursorStudents.getColumnIndex(DatabaseHelper.COLUMN_FIRST_NAME)) +
                    " " + cursorStudents.getString(cursorStudents.getColumnIndex(DatabaseHelper.COLUMN_LAST_NAME));
            String studentStatus = cursorStudents.getString(cursorStudents.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_STATUS));
            userList.add("Student - " + studentId + " - " + studentName + " (" + studentStatus + ")");
        }
        cursorStudents.close();

        while (cursorTeachers.moveToNext()) {
            String teacherId = cursorTeachers.getString(cursorTeachers.getColumnIndex(DatabaseHelper.COLUMN_TEACHER_ID));
            String teacherName = cursorTeachers.getString(cursorTeachers.getColumnIndex(DatabaseHelper.COLUMN_TEACHER_FIRST_NAME)) +
                    " " + cursorTeachers.getString(cursorTeachers.getColumnIndex(DatabaseHelper.COLUMN_TEACHER_LAST_NAME));
            String teacherStatus = cursorTeachers.getString(cursorTeachers.getColumnIndex(DatabaseHelper.COLUMN_TEACHER_STATUS));
            userList.add("Teacher - " + teacherId + " - " + teacherName + " (" + teacherStatus + ")");
        }
        cursorTeachers.close();

        userAdapter.notifyDataSetChanged();
    }
}
