package com.example.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

public class AdminDashboard extends AppCompatActivity {
    private Button add_course,add_department,manage_user,delete_user,logout;
    private SessionManager sessionManager;
    private boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);

        add_course = findViewById(R.id.add_course);
        add_department = findViewById(R.id.add_department);
        manage_user = findViewById(R.id.manage_user);
        delete_user = findViewById(R.id.delete_user);
        logout = findViewById(R.id.logout_user);

        sessionManager = new SessionManager(this);

        add_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(AdminDashboard.this, "Add course", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminDashboard.this,AddCourse.class);
                startActivity(intent);
                finish();

            }
        });
        add_department.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this,AddDepartment.class);
                startActivity(intent);
                finish();
            }
        });
        manage_user.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this,ManageUser.class);
                startActivity(intent);
                finish();
            }
        });
        delete_user.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this,DeleteUser.class);
                startActivity(intent);
                finish();
            }
        });
        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                sessionManager.logout();
                Intent intent = new Intent(AdminDashboard.this,MainActivity.class);
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
}