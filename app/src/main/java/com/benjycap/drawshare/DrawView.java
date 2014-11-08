package com.benjycap.drawshare;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

/**
 * Created by Ben on 28/10/2014.
 */
public class DrawView extends View {

    public static final String TAG = "DrawView";

    public static final String EXTRA_PAINTED_PATHS = "paintedPaths";

    // Painted Paths drawn by the user
    private PaintedPathList mPaintedPaths;
    public PaintedPathList getPaintedPaths() { return mPaintedPaths; }
    public void setPaintedPaths(PaintedPathList paintedPaths) { mPaintedPaths = paintedPaths; }

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

            mPaintedPaths.removeLast();

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

        mPaintedPaths = new PaintedPathList();
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
                        mPaintedPaths.startNewPath(mPalette);
                    // Set down flag as true
                    isDrawingPath = true;

                    PaintedPath currentPaintedPath = mPaintedPaths.getCurrent();
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