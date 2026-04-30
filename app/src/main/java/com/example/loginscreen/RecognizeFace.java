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
import android.widget.Button;
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

public class RecognizeFace extends AppCompatActivity {

    private static final String TAG = "RecognizeFace";
    private PreviewView previewView;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private FaceDetector faceDetector;
    private ProcessCameraProvider cameraProvider;

    private TFLiteFaceRecognition faceRecognition;
    private File tempFile;
    private SessionManager sessionManager;
    private DatabaseHelper db;
    private String studentName;

    private boolean isFaceRecognized = false;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize_face);

        previewView = findViewById(R.id.preview_view);

        db = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(RecognizeFace.this, StudentDashboard.class);
            startActivity(intent);
            finish();
        }

        studentName = db.getStudentName(sessionManager.getEmail());

        faceRecognition = new TFLiteFaceRecognition(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            startCamera();
        }

        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build();
        faceDetector = FaceDetection.getClient(options);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RecognizeFace.this, StudentDashboard.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
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
                cameraProvider = cameraProviderFuture.get();

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
                    if (!isFaceRecognized) {
                        Log.d(TAG, "Analyzing image...");
                        @androidx.camera.core.ExperimentalGetImage
                        Image mediaImage = imageProxy.getImage();
                        if (mediaImage != null && mediaImage.getFormat() == ImageFormat.YUV_420_888) {
                            detectFaces(mediaImage, imageProxy);
                        } else {
                            Log.e(TAG, "Media image is null or format is not YUV_420_888");
                            imageProxy.close();
                        }
                    } else {
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
                            saveTempFaceImage(mediaImage, boundingBox, imageProxy);
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

    private void saveTempFaceImage(Image mediaImage, Rect rect, ImageProxy imageProxy) {
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

            tempFile = new File(getCacheDir(), "temp_face.png");
            FileOutputStream fos = new FileOutputStream(tempFile);
            boolean success = resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            if (success) {
                Log.d(TAG, "Temporary image saved successfully: " + tempFile.getAbsolutePath());
                recognizeFace(resizedBitmap);
            } else {
                Log.e(TAG, "Failed to save temporary image");
            }
        } catch (IllegalArgumentException | IOException e) {
            Log.e(TAG, "Failed to save temporary face image", e);
        } finally {
            imageProxy.close();
        }
    }

    private void recognizeFace(Bitmap tempFaceBitmap) {
        File dataDir = new File(getExternalFilesDir(null), "data");
        String registeredFaceFileName = studentName + ".png";

        File registeredFaceFile = new File(dataDir, registeredFaceFileName);
        if (registeredFaceFile.exists()) {
            Bitmap registeredFaceBitmap = BitmapFactory.decodeFile(registeredFaceFile.getAbsolutePath());
            float[] registeredFaceEmbedding = faceRecognition.recognizeFace(registeredFaceBitmap);
            float[] tempFaceEmbedding = faceRecognition.recognizeFace(tempFaceBitmap);

            float distance = calculateEuclideanDistance(registeredFaceEmbedding, tempFaceEmbedding);
            if (distance < 0.6) { // Threshold value
                Toast.makeText(this, "Face Recognized", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Face recognized. Distance: " + distance);

                // Set recognized status to true
                sessionManager.setRecognized(true);
                isFaceRecognized = true;

                // Delete the temporary image file
                if (tempFile != null && tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.d(TAG, "Temporary image file deleted successfully.");
                    } else {
                        Log.e(TAG, "Failed to delete the temporary image file.");
                    }
                }

                // Unbind the ImageAnalysis use case to stop face detection
                if (cameraProvider != null) {
                    cameraProvider.unbindAll();
                }

                Intent intent = new Intent(RecognizeFace.this, SelectCourseStudent.class);
                startActivity(intent);
                finish();

            } else {
                if (!isFaceRecognized) {
                    Toast.makeText(this, "Face Not Recognized", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Face not recognized. Distance: " + distance);
                }
            }
        } else {
            Log.e(TAG, "Registered face image not found");
            Toast.makeText(this, "Registered face image not found", Toast.LENGTH_SHORT).show();
        }
    }

    private float calculateEuclideanDistance(float[] embedding1, float[] embedding2) {
        float sum = 0;
        for (int i = 0; i < embedding1.length; i++) {
            float diff = embedding1[i] - embedding2[i];
            sum += diff * diff;
        }
        return (float) Math.sqrt(sum);
    }
}