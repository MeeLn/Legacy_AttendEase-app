package com.example.loginscreen;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentDashboard extends AppCompatActivity {
    private Button register_face, take_attendance, view_attendance, logout;
    private SessionManager sessionManager;
    private DatabaseHelper db;
    private String studentName;
    private File dataDir;
    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_dashboard);

        register_face = findViewById(R.id.register_face);
        take_attendance = findViewById(R.id.take_attendance);
        view_attendance = findViewById(R.id.view_attendance);
        logout = findViewById(R.id.logout_student);

        db = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(StudentDashboard.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        studentName = db.getStudentName(sessionManager.getEmail());
        dataDir = new File(getExternalFilesDir(null), "data");

        register_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRegisterFace();

            }
        });

        take_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentDashboard.this, RecognizeFace.class);
                startActivity(intent);
                finish();
            }
        });

        view_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentDashboard.this, ViewAttendanceStudent.class);
                startActivity(intent);
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.logout();
                Intent intent = new Intent(StudentDashboard.this, MainActivity.class);
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

    private void checkAndRegisterFace() {
        File faceFile = new File(dataDir, studentName + ".png");

        if (faceFile.exists()) {
            // Face already registered, show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Face Already Registered")
                    .setMessage("Your face is already registered. Do you want to re-register your face?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            faceFile.delete(); // Delete existing file
                            startRegisterFaceActivity();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        } else {
            // Face not registered, proceed to registration
            startRegisterFaceActivity();
        }
    }

    private void startRegisterFaceActivity() {
        Intent intent = new Intent(StudentDashboard.this, RegisterFace.class);
        startActivity(intent);
        finish();
    }

}