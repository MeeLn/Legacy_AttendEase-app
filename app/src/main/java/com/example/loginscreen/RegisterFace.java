package com.example.loginscreen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RegisterFace extends AppCompatActivity {

    private static final String TAG = "RegisterFace";
    private PreviewView previewView;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private File dataDir;
    private SessionManager sessionManager;
    private DatabaseHelper db;
    private String studentName;
    private FaceDetector faceDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_face);

        previewView = findViewById(R.id.preview_view);

        db = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(RegisterFace.this, StudentDashboard.class);
            startActivity(intent);
            finish();
        }

        studentName = db.getStudentName(sessionManager.getEmail());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            startCamera();
        }

        dataDir = new File(getExternalFilesDir(null), "data");
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            if (created) {
                Log.d(TAG, "Data directory created: " + dataDir.getAbsolutePath());
            } else {
                Log.e(TAG, "Failed to create data directory: " + dataDir.getAbsolutePath());
            }
        } else {
            Log.d(TAG, "Data directory exists: " + dataDir.getAbsolutePath());
        }

        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build();
        faceDetector = FaceDetection.getClient(options);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(640, 480))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), imageProxy -> {
                    Log.d(TAG, "Analyzing image...");
                    @androidx.camera.core.ExperimentalGetImage
                    Image mediaImage = imageProxy.getImage();
                    if (mediaImage != null && mediaImage.getFormat() == ImageFormat.YUV_420_888) {
                        detectFaces(mediaImage, imageProxy);
                    } else {
                        Log.e(TAG, "Media image is null or format is not YUV_420_888");
                        imageProxy.close();
                    }
                });

                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Camera provider error", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void detectFaces(Image mediaImage, ImageProxy imageProxy) {
        InputImage image = InputImage.fromMediaImage(mediaImage, 0);
        Log.d(TAG, "Detecting faces...");

        faceDetector.process(image)
                .addOnSuccessListener(faces -> {
                    Log.d(TAG, "Face detection successful. Number of faces: " + faces.size());
                    if (faces.size() > 0) {
                        for (Face face : faces) {
                            Rect boundingBox = face.getBoundingBox();
                            saveFaceImage(mediaImage, boundingBox, imageProxy);
                            break;
                        }
                    } else {
                        imageProxy.close();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Face detection failed", e);
                    imageProxy.close();
                });
    }

    private void saveFaceImage(Image mediaImage, Rect rect, ImageProxy imageProxy) {
        try {
            ByteBuffer yBuffer = mediaImage.getPlanes()[0].getBuffer();
            ByteBuffer uBuffer = mediaImage.getPlanes()[1].getBuffer();
            ByteBuffer vBuffer = mediaImage.getPlanes()[2].getBuffer();

            int ySize = yBuffer.remaining();
            int uSize = uBuffer.remaining();
            int vSize = vBuffer.remaining();

            byte[] nv21 = new byte[ySize + uSize + vSize];

            yBuffer.get(nv21, 0, ySize);
            vBuffer.get(nv21, ySize, vSize);
            uBuffer.get(nv21, ySize + vSize, uSize);

            YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, mediaImage.getWidth(), mediaImage.getHeight(), null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Rect rectCrop = new Rect(rect.left, rect.top, rect.right, rect.bottom);
            yuvImage.compressToJpeg(rectCrop, 100, out);
            byte[] imageBytes = out.toByteArray();
            Bitmap faceBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(faceBitmap, 112, 112, false);

            String filename = studentName + ".png";
            File file = new File(dataDir, filename);
            FileOutputStream fos = new FileOutputStream(file);
            boolean success = resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            if (success) {
                Log.d(TAG, "Image saved successfully: " + filename);
                Intent intent = new Intent(RegisterFace.this, StudentDashboard.class);
                startActivity(intent);
                Toast.makeText(RegisterFace.this, "Face Registered", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Failed to save image: " + filename);
            }
        } catch (IllegalArgumentException | IOException e) {
            Log.e(TAG, "Failed to save face image", e);
        } finally {
            imageProxy.close();
        }
    }
}