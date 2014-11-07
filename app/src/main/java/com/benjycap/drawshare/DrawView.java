package com.benjycap.drawshare;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Ben on 28/10/2014.
 */
public class DrawView extends View {

    public static final String TAG = "DrawView";

    public static final String EXTRA_PAINTED_PATHS = "paintedPaths";

    // PaintedPath encapsulates a Path, with a Paint to draw it
    public class PaintedPath implements Serializable {
        public final Path mPath;
        public final Paint mPaint;

        public PaintedPath(Path path, Paint paint) {
            mPath = path;
            mPaint = paint;
        }
    }

    // Painted Paths drawn by the user
    private ArrayList<PaintedPath> mPaintedPaths;
    public ArrayList<PaintedPath> getPaintedPaths() { return mPaintedPaths; }
    public void setPaintedPaths(ArrayList<PaintedPath> paintedPaths) { mPaintedPaths = paintedPaths; }

    // Palette to be manipulated by the user
    private Palette mPalette;
    public void setPalette(Palette palette) {
        mPalette = palette;
    }

    // Gesture Detector & Gesture Listener
    private class DoubleTapOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        // Boolean to represent if finger is still down from double tap
        private boolean isDownFromDoubleTap = false;
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            isDownFromDoubleTap = true;

            if (!mPaintedPaths.isEmpty()) {
                mPaintedPaths.remove(Math.max(0, mPaintedPaths.size() - 1));
            }

            return true;
        }
    }
    private DoubleTapOnGestureListener mGestureListener = new DoubleTapOnGestureListener();
    private GestureDetector mGestureDetector = new GestureDetector(getContext(), mGestureListener);

    private boolean isDrawingPath;

    // Constructors
    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaintedPaths = new ArrayList<PaintedPath>();
        isDrawingPath = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Main touch event
        boolean mainEvent = true;
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                // Skip below in the case user digit is down from double tap
                if (!mGestureListener.isDownFromDoubleTap) {
                    // Create new path if digit not already down
                    if (!isDrawingPath)
                        startNewPath();
                    // Set down flag as true
                    isDrawingPath = true;

                    PaintedPath currentPaintedPath = mPaintedPaths.get(mPaintedPaths.size() - 1);
                    Path currentPath = currentPaintedPath.mPath;
                    // Draw path
                    if (currentPath.isEmpty())
                        currentPath.moveTo(event.getX(), event.getY());
                    currentPath.lineTo(event.getX(), event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                mGestureListener.isDownFromDoubleTap = false;
                isDrawingPath = false;
                break;
            default:
                mainEvent = false;
                break;
        }

        // Gesture detector touch event
        boolean gestureResult = mGestureDetector.onTouchEvent(event);

        // Redraw View
        invalidate();

        return gestureResult || mainEvent;
    }

    // To be called after Palette has been updated to user's specifications
    private void startNewPath() {
        // New path only needed if PaintedPath array is empty or if current Path has been used
        if (mPaintedPaths.isEmpty() || !mPaintedPaths.get(mPaintedPaths.size() - 1).mPath.isEmpty())
            mPaintedPaths.add(new PaintedPath(new Path(), mPalette.getCurrentPaint()));
    }

    private void randomisePalette() {
        mPalette.setCurrentPaintColorRandom();
        mPalette.setCurrentStrokeWidth(10 * new Random().nextFloat());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (PaintedPath pp : mPaintedPaths) {
            canvas.drawPath(pp.mPath, pp.mPaint);
        }
    }

}