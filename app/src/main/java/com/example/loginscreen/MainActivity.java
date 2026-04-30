package com.example.loginscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button loginButton;
    TextView signUp;

    DatabaseHelper dbHelper;
    SessionManager sessionManager;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.signupText);


//        if (! Python.isStarted()) {
//            Python.start(new AndroidPlatform(this));
//        }
//        Python py = Python.getInstance();
//        PyObject module = py.getModule("script");
//
//        // Get variables from the Python script
//        int num = module.get("number").toInt();
//        System.out.println("Value of number is " + num);
//
//        String text = module.get("text").toString();
//        System.out.println("Value of text is " + text);
//
//        // Call the factorial function
//        PyObject factorial = module.callAttr("factorial", 4);
//        System.out.println("Factorial of 4 is " + factorial.toInt());

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            navigateToDashboard(sessionManager.getRole());
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameValue = username.getText().toString();
                String passwordValue = password.getText().toString();

                if (usernameValue.equals("admin@admin.com") && passwordValue.equals("admin123")) {
                    Toast.makeText(MainActivity.this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
                    sessionManager.createLoginSession("admin@admin.com","admin");
                    Intent intent = new Intent(MainActivity.this, AdminDashboard.class);
                    startActivity(intent);
                    finish();
                } else {
                    login(usernameValue, passwordValue);
                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000); // 2 seconds delay
    }

    private void login(String usernameValue, String passwordValue) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Check if the user is a registered student
        String studentQuery = "SELECT * FROM " + DatabaseHelper.TABLE_STUDENT + " WHERE " +
                DatabaseHelper.COLUMN_EMAIL + " = ? AND " +
                DatabaseHelper.COLUMN_PASSWORD + " = ?";
        Cursor studentCursor = db.rawQuery(studentQuery, new String[]{usernameValue, passwordValue});

        if (studentCursor.moveToFirst()) {
            String studentName = studentCursor.getString(studentCursor.getColumnIndex(DatabaseHelper.COLUMN_FIRST_NAME)) +
                    " " + studentCursor.getString(studentCursor.getColumnIndex(DatabaseHelper.COLUMN_LAST_NAME));
            String studentStatus = studentCursor.getString(studentCursor.getColumnIndex(DatabaseHelper.COLUMN_STUDENT_STATUS));

            if ("inactive".equals(studentStatus)) {
                Toast.makeText(MainActivity.this, "User is inactive. Admin has not approved.", Toast.LENGTH_SHORT).show();
                studentCursor.close();
                return;
            }

            Toast.makeText(MainActivity.this, "Welcome " + studentName + "!", Toast.LENGTH_SHORT).show();
            sessionManager.createLoginSession(usernameValue, "student");
            Intent intent = new Intent(MainActivity.this, StudentDashboard.class);
            startActivity(intent);
            studentCursor.close();
            finish();
            return;
        }
        studentCursor.close();

        // Check if the user is a registered teacher
        String teacherQuery = "SELECT * FROM " + DatabaseHelper.TABLE_TEACHER + " WHERE " +
                DatabaseHelper.COLUMN_TEACHER_EMAIL + " = ? AND " +
                DatabaseHelper.COLUMN_TEACHER_PASSWORD + " = ?";
        Cursor teacherCursor = db.rawQuery(teacherQuery, new String[]{usernameValue, passwordValue});

        if (teacherCursor.moveToFirst()) {
            String teacherName = teacherCursor.getString(teacherCursor.getColumnIndex(DatabaseHelper.COLUMN_TEACHER_FIRST_NAME)) +
                    " " + teacherCursor.getString(teacherCursor.getColumnIndex(DatabaseHelper.COLUMN_TEACHER_LAST_NAME));
            String teacherStatus = teacherCursor.getString(teacherCursor.getColumnIndex(DatabaseHelper.COLUMN_TEACHER_STATUS));

            if ("inactive".equals(teacherStatus)) {
                Toast.makeText(MainActivity.this, "User is inactive. Admin has not approved.", Toast.LENGTH_SHORT).show();
                teacherCursor.close();
                return;
            }

            Toast.makeText(MainActivity.this, "Welcome " + teacherName + "!", Toast.LENGTH_SHORT).show();
            sessionManager.createLoginSession(usernameValue, "teacher");
            Intent intent = new Intent(MainActivity.this, TeacherDashboard.class);
            startActivity(intent);
            teacherCursor.close();
            finish();
            return;
        }
        teacherCursor.close();

        Toast.makeText(MainActivity.this, "Login failed, either username or password is incorrect!", Toast.LENGTH_SHORT).show();
    }

    private void navigateToDashboard(String role) {
        Intent intent;
        if (role.equals("student")) {
            intent = new Intent(MainActivity.this, StudentDashboard.class);
        } else if (role.equals("teacher")) {
            intent = new Intent(MainActivity.this, TeacherDashboard.class);
        } else {
            intent = new Intent(MainActivity.this, AdminDashboard.class);
        }
        startActivity(intent);
        finish();
    }
}