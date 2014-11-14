package com.benjycap.drawshare;

import android.graphics.Paint;
import android.graphics.Path;



/**
 *  PaintedPath encapsulates a Path, with a Paint to draw it
 */
public class PaintedPath {

        public final SerializablePath mPath;
        public final Paint mPaint;

        public PaintedPath(SerializablePath path, Paint paint) {
            mPath = path;
            mPaint = paint;
        }

    // Extension to Path class that helps implementation of serializable
    public static class SerializablePath extends Path {

        // The serializable instance to push to, which is owned by the PaintedPathList which created this SerializablePath
        PaintedPathList.SerializableInstance mSerializableInstance;

        public SerializablePath(PaintedPathList.SerializableInstance serializableInstance) {
            mSerializableInstance = serializableInstance;
        }

        @Override
        public void moveTo(float x, float y) {
            mSerializableInstance.pushMoveTo(x, y);
            super.moveTo(x, y);
        }

        @Override
        public void lineTo(float x, float y) {
            mSerializableInstance.pushLineTo(x, y);
            super.lineTo(x, y);
        }
    }
}
