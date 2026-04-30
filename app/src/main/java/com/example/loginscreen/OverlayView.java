package com.example.loginscreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class OverlayView extends View {
    private final List<android.graphics.Rect> rects = new ArrayList<>();
    private final Paint paint;

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(android.graphics.Color.RED);
        paint.setStrokeWidth(5);
    }

    public void setRects(List<android.graphics.Rect> rects) {
        this.rects.clear();
        this.rects.addAll(rects);
        invalidate(); // Redraw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (android.graphics.Rect rect : rects) {
            canvas.drawRect(rect, paint);
        }
    }
}