package com.benjycap.drawshare;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Ben on 30/10/2014.
 */
public class Palette {

    public static List<Integer> colors = Collections.unmodifiableList((Arrays.asList(
            Color.BLUE, Color.RED, Color.YELLOW, Color.BLACK, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.DKGRAY)));

    private Paint mPaint;

    public Palette() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5f);
        mPaint.setAntiAlias(true);
    }

    public Paint getCurrentPaint () {
        return new Paint(mPaint);
    }

    public void setCurrentPaintColor(int color) {
        mPaint.setColor(color);
    }

    public void setCurrentStrokeWidth(float width) {
        mPaint.setStrokeWidth(width);
    }

    public static Paint getDefaultPaint(int color, float strokeWidth) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(true);

        return paint;
    }

}
