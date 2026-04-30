package com.example.loginscreen;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Patterns;
import android.database.Cursor;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherRegistrationActivity extends AppCompatActivity {

    EditText first_name,last_name,email,password,confirm_password;
    Button signupButton;
    TextView signin;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);

        dbHelper = new DatabaseHelper(this);

        email = findViewById(R.id.email);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        signupButton = findViewById(R.id.signupButton);
        signin = findViewById(R.id.signinText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //insert data
                registerTeacher();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherRegistrationActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TeacherRegistrationActivity.this, Register.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
    private void registerTeacher() {
        String firstNameValue = first_name.getText().toString().trim();
        String lastNameValue = last_name.getText().toString().trim();
        String emailValue = email.getText().toString().trim();
        String passwordValue = password.getText().toString();
        String confirmPasswordValue = confirm_password.getText().toString();

        if (validateInput(firstNameValue, lastNameValue, emailValue, passwordValue, confirmPasswordValue)) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (!isEmailRegistered(emailValue, db)) {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_TEACHER_FIRST_NAME, firstNameValue);
                values.put(DatabaseHelper.COLUMN_TEACHER_LAST_NAME, lastNameValue);
                values.put(DatabaseHelper.COLUMN_TEACHER_EMAIL, emailValue);
                values.put(DatabaseHelper.COLUMN_TEACHER_PASSWORD, passwordValue);

                long newRowId = db.insert(DatabaseHelper.TABLE_TEACHER, null, values);

                if (newRowId != -1) {
                    Toast.makeText(TeacherRegistrationActivity.this, "Teacher registered successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                } else {
                    Toast.makeText(TeacherRegistrationActivity.this, "Error registering teacher", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(TeacherRegistrationActivity.this, "Email already registered", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInput(String firstName, String lastName, String email, String password, String confirmPassword) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isEmailRegistered(String email, SQLiteDatabase db) {
        String[] columns = { DatabaseHelper.COLUMN_TEACHER_EMAIL };
        String selection = DatabaseHelper.COLUMN_TEACHER_EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_TEACHER,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean isRegistered = (cursor.getCount() > 0);
        cursor.close();
        return isRegistered;
    }

    private void clearFields() {
        first_name.setText("");
        last_name.setText("");
        email.setText("");
        password.setText("");
        confirm_password.setText("");
    }
}