package com.benjycap.drawshare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Ben on 28/10/2014.
 */
public class DrawView extends PaintedView {

    public static final String TAG = "DrawView";

    // TODO Implement dynamic height/width detection into PaintedPathList.deserializeForDimension
    public static final float height = 850;
    public static final float width = 540;

    // Palette to be manipulated by the user
    private Palette mPalette;
    public void setPalette(Palette palette) {
        mPalette = palette;
    }

    // Gesture Detector & Gesture Listener
    private class DoubleTapOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        // Boolean to represent if finger is still down from double tap
        private boolean isUserDownFromDoubleTap = false;
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            isUserDownFromDoubleTap = true;
            mPaintedPaths.removeLast();
            return true;
        }
    }
    private DoubleTapOnGestureListener mGestureListener = new DoubleTapOnGestureListener();
    private GestureDetector mGestureDetector = new GestureDetector(getContext(), mGestureListener);

    private boolean isUserDrawingPath;

    // Constructors
    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        isUserDrawingPath = false;
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
                if (!mGestureListener.isUserDownFromDoubleTap) {
                    // Create new path if digit not already down
                    if (!isUserDrawingPath)
                        mPaintedPaths.startNewPath(mPalette);
                    // Set down flag as true
                    isUserDrawingPath = true;

                    PaintedPath currentPaintedPath = mPaintedPaths.getCurrent();
                    Path currentPath = currentPaintedPath.mPath;
                    // Draw path
                    if (currentPath.isEmpty())
                        currentPath.moveTo(event.getX(), event.getY());
                    else
                        currentPath.lineTo(event.getX(), event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                mGestureListener.isUserDownFromDoubleTap = false;
                isUserDrawingPath = false;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Intent i = new Intent(RemoteDrawReceiver.ACTION_SEND_PAINTED_PATH_DATA);
        i.putExtra(RemoteDrawReceiver.EXTRA_PAINTED_PATH_DATA, mPaintedPaths.getSerializableInstance());

        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(i);
    }
}