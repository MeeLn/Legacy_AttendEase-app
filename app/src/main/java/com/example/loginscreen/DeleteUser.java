package com.example.loginscreen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class DeleteUser extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseHelper dbHelper;
    private ListView listViewUsers;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> userList;
    private ArrayList<Integer> userIdList;
    private ArrayList<String> userTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_user);

        toolbar = findViewById(R.id.toolbar_delete);
        setSupportActionBar(toolbar);

        listViewUsers = findViewById(R.id.listViewUsers);
        dbHelper = new DatabaseHelper(this);
        userList = new ArrayList<>();
        userIdList = new ArrayList<>();
        userTypeList = new ArrayList<>();

        fetchUsers();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        listViewUsers.setAdapter(adapter);

        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                showConfirmationDialog(position);
            }
        });
    }

    private void fetchUsers() {
        // Fetch students
        Cursor studentCursor = dbHelper.getAllStudents();
        if (studentCursor.moveToFirst()) {
            do {
                int id = studentCursor.getInt(studentCursor.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_ID));
                String name = studentCursor.getString(studentCursor.getColumnIndex(DatabaseHelper.COLUMN_FIRST_NAME)) +
                        " " + studentCursor.getString(studentCursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_NAME));
                userList.add("Student: " + name);
                userIdList.add(id);
                userTypeList.add("student");
            } while (studentCursor.moveToNext());
        }
        studentCursor.close();

        // Fetch teachers
        Cursor teacherCursor = dbHelper.getAllTeachers();
        if (teacherCursor.moveToFirst()) {
            do {
                int id = teacherCursor.getInt(teacherCursor.getColumnIndex(DatabaseHelper.COLUMN_TEACHER_ID));
                String name = teacherCursor.getString(teacherCursor.getColumnIndex(DatabaseHelper.COLUMN_TEACHER_FIRST_NAME)) +
                        " " + teacherCursor.getString(teacherCursor.getColumnIndex(DatabaseHelper.COLUMN_TEACHER_LAST_NAME));
                userList.add("Teacher: " + name);
                userIdList.add(id);
                userTypeList.add("teacher");
            } while (teacherCursor.moveToNext());
        }
        teacherCursor.close();
    }

    private void showConfirmationDialog(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser(position);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteUser(int position) {
        int userId = userIdList.get(position);
        String userType = userTypeList.get(position);

        boolean result = false;
        if ("student".equals(userType)) {
            result = dbHelper.deleteStudent(userId);
        } else if ("teacher".equals(userType)) {
            result = dbHelper.deleteTeacher(userId);
        }

        if (result) {
            Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
            userList.remove(position);
            userIdList.remove(position);
            userTypeList.remove(position);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DeleteUser.this, AdminDashboard.class);
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
            Intent intent = new Intent(DeleteUser.this, AdminDashboard.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
