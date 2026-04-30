package com.example.loginscreen;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.util.Patterns;

public class StudentRegistration extends AppCompatActivity {

    EditText first_name,last_name,rollno,department,email,password,confirm_password;
    Button signupButton;
    TextView signin;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_registration);

        dbHelper = new DatabaseHelper(this);

        email = findViewById(R.id.email);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        rollno = findViewById(R.id.roll_no);
        department = findViewById(R.id.department_name);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        signupButton = findViewById(R.id.signupButton);
        signin = findViewById(R.id.signinText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //insert data
                registerStudent();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentRegistration.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(StudentRegistration.this, Register.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
    private void registerStudent() {
        String firstNameValue = first_name.getText().toString().trim();
        String lastNameValue = last_name.getText().toString().trim();
        String rollNoValue = rollno.getText().toString().trim();
        String departmentValue = department.getText().toString().trim();
        String emailValue = email.getText().toString().trim();
        String passwordValue = password.getText().toString();
        String confirmPasswordValue = confirm_password.getText().toString();

        if (validateInput(firstNameValue, lastNameValue, rollNoValue, departmentValue, emailValue, passwordValue, confirmPasswordValue)) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            // Check if department exists
            if (dbHelper.isDepartmentExists(departmentValue)) {
                if (!isEmailRegistered(emailValue, db)) {
                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_FIRST_NAME, firstNameValue);
                    values.put(DatabaseHelper.COLUMN_LAST_NAME, lastNameValue);
                    values.put(DatabaseHelper.COLUMN_ROLL, rollNoValue);
                    values.put(DatabaseHelper.COLUMN_DEPARTMENT, departmentValue);
                    values.put(DatabaseHelper.COLUMN_EMAIL, emailValue);
                    values.put(DatabaseHelper.COLUMN_PASSWORD, passwordValue);

                    long newRowId = db.insert(DatabaseHelper.TABLE_STUDENT, null, values);

                    if (newRowId != -1) {
                        Toast.makeText(StudentRegistration.this, "Student registered successfully", Toast.LENGTH_SHORT).show();
                        clearFields();
                    } else {
                        Toast.makeText(StudentRegistration.this, "Error registering student", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StudentRegistration.this, "Email already registered", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(StudentRegistration.this, "Department does not exist", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateInput(String firstName, String lastName, String rollNo, String department, String email, String password, String confirmPassword) {
        if (firstName.isEmpty() || lastName.isEmpty() || rollNo.isEmpty() || department.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
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
        String[] columns = { DatabaseHelper.COLUMN_EMAIL };
        String selection = DatabaseHelper.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_STUDENT,
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
        rollno.setText("");
        department.setText("");
        email.setText("");
        password.setText("");
        confirm_password.setText("");
    }
}