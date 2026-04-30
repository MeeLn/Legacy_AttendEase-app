package com.example.loginscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TFLiteFaceRecognition {

    private Interpreter interpreter;
    private static final int INPUT_SIZE = 112; // Size used by MobileFaceNet
    private static final int OUTPUT_SIZE = 192; // Output size used by MobileFaceNet

    public TFLiteFaceRecognition(Context context) {
        try {
            interpreter = new Interpreter(loadModelFile(context, "mobilefacenet.tflite"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MappedByteBuffer loadModelFile(Context context, String modelFileName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(context.getAssets().openFd(modelFileName).getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = context.getAssets().openFd(modelFileName).getStartOffset();
        long declaredLength = context.getAssets().openFd(modelFileName).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public float[] recognizeFace(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
        float[][] embeddings = new float[1][OUTPUT_SIZE];
        float[][][][] input = new float[1][INPUT_SIZE][INPUT_SIZE][3];

        for (int y = 0; y < INPUT_SIZE; y++) {
            for (int x = 0; x < INPUT_SIZE; x++) {
                int pixel = resizedBitmap.getPixel(x, y);
                input[0][y][x][0] = ((pixel >> 16) & 0xFF) / 255.0f;
                input[0][y][x][1] = ((pixel >> 8) & 0xFF) / 255.0f;
                input[0][y][x][2] = (pixel & 0xFF) / 255.0f;
            }
        }

        interpreter.run(input, embeddings);
        return embeddings[0];
    }
}