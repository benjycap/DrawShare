package com.benjycap.drawshare;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Ben on 12/11/2014.
 */
public class PaintedView extends View {

    public static final String TAG = "PaintedView";

    // Painted Paths drawn by the view
    protected PaintedPathList mPaintedPaths;
    public void setPaintedPaths(PaintedPathList paintedPaths) {
        mPaintedPaths = paintedPaths;
        // View needs redrawing after paths have been changed
        invalidate();
    }

    // Constructors
    public PaintedView(Context context) {
        this(context, null);
    }

    public PaintedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaintedPaths = new PaintedPathList();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (PaintedPath pp : mPaintedPaths) {
            canvas.drawPath(pp.mPath, pp.mPaint);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putSerializable("paintedPathState", mPaintedPaths.getSerializableInstance());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            mPaintedPaths = PaintedPathList.deserialize((PaintedPathList.SerializableInstance)
                    ((Bundle) state).getSerializable("paintedPathState"));
            state = ((Bundle) state).getParcelable("instanceState");

        }
        super.onRestoreInstanceState(state);
    }
}