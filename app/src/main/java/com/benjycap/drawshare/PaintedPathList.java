package com.benjycap.drawshare;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ben on 07/11/2014.
 */


// Helper list class to facilitate common tasks and ensure more concise code in DrawView
public class PaintedPathList extends ArrayList<PaintedPath> {

    public static final String TAG = "PaintedPathList";

    private SerializableInstance mSerializableInstance;
    public SerializableInstance getSerializableInstance() { return mSerializableInstance; }

    public PaintedPathList() {
        super();
        mSerializableInstance = new SerializableInstance();
    }

    public PaintedPath getCurrent() {
        return get(size() - 1);
    }

    public void removeLast() {
        if (!isEmpty()) {
            remove(Math.max(0, size() - 1));
            mSerializableInstance.removeLast();
        }
    }

    // To be called after Palette has been updated to user's specifications
    public void startNewPath(Palette palette) {
        // New path only needed if PaintedPath array is empty or if current Path has been used
        if (isEmpty() || !get(size() - 1).mPath.isEmpty()) {
            add(new PaintedPath(new PaintedPath.SerializablePath(mSerializableInstance), palette.getCurrentPaint()));
            // Serialize
            mSerializableInstance.pushColor(palette.getCurrentPaint().getColor());
        }
    }

    public static class SerializableInstance implements Serializable {

        private ArrayList<Integer> colors;
        private ArrayList<Float> xMoveTo, yMoveTo;
        private ArrayList<ArrayList<Float>> xLineTo, yLineTo;

        public SerializableInstance() {
            colors = new ArrayList<Integer>();
            xMoveTo = new ArrayList<Float>();
            yMoveTo = new ArrayList<Float>();
            xLineTo = new ArrayList<ArrayList<Float>>();
            yLineTo = new ArrayList<ArrayList<Float>>();
        }

        public void pushColor(Integer color) {
            colors.add(color);
        }

        public void pushMoveTo(Float x, Float y) {
            xMoveTo.add(x);
            yMoveTo.add(y);
            // Move to means new path, so create new LineTo arrays
            xLineTo.add(new ArrayList<Float>());
            yLineTo.add(new ArrayList<Float>());
        }

        public void pushLineTo(Float x, Float y) {
            xLineTo.get(xLineTo.size()-1).add(x);
            yLineTo.get(yLineTo.size()-1).add(y);
        }

        public void removeLast() {
            colors.remove(Math.max(0, colors.size() - 1));
            xMoveTo.remove(Math.max(0, xMoveTo.size() - 1));
            yMoveTo.remove(Math.max(0, yMoveTo.size() - 1));
            xLineTo.remove(Math.max(0, xLineTo.size() - 1));
            yLineTo.remove(Math.max(0, yLineTo.size() - 1));
        }

        public ArrayList<Integer> getColors() {
            return colors;
        }

        public ArrayList<Float> getxMoveTo() {
            return xMoveTo;
        }

        public ArrayList<Float> getyMoveTo() {
            return yMoveTo;
        }

        public ArrayList<ArrayList<Float>> getxLineTo() {
            return xLineTo;
        }

        public ArrayList<ArrayList<Float>> getyLineTo() {
            return yLineTo;
        }
    }

    public static PaintedPathList deserialize(SerializableInstance instance) {

        PaintedPathList list = new PaintedPathList();

        for (int i=0; i<instance.getxMoveTo().size(); i++) {
            // Get values
            Integer color = instance.getColors().get(i);
            Float x = instance.getxMoveTo().get(i);
            Float y = instance.getyMoveTo().get(i);

            // Re-serialize color in new list
            list.getSerializableInstance().pushColor(color);

            // Create new PaintedPath, supplying the new List's serializable member and the re-acquired color
            PaintedPath pp = new PaintedPath(new PaintedPath.SerializablePath(list.mSerializableInstance),
                    Palette.getDefaultPaint(color));

            pp.mPath.moveTo(x, y);

            for (int j=0; j<instance.getxLineTo().get(i).size(); j++) {
                Float xx = instance.getxLineTo().get(i).get(j);
                Float yy = instance.getyLineTo().get(i).get(j);
                pp.mPath.lineTo(xx, yy);
            }

            // Add de-serialized PaintedPath to List
            list.add(pp);
        }

        return list;
    }

    public static PaintedPathList deserializeForNewDimension(SerializableInstance instance, float containerWidth, float containerHeight) {

        float heightRatio = containerHeight/DrawView.height;
        float widthRatio = containerWidth/DrawView.width;

        PaintedPathList list = new PaintedPathList();

        for (int i=0; i<instance.getColors().size(); i++) {
            // Get values
            Integer color = instance.getColors().get(i);
            Float x = instance.getxMoveTo().get(i) * widthRatio;
            Float y = instance.getyMoveTo().get(i) * heightRatio;

            // Re-serialize color in new list
            list.getSerializableInstance().pushColor(color);

            // Create new PaintedPath, supplying the new List's serializable member and the re-acquired color
            PaintedPath pp = new PaintedPath(new PaintedPath.SerializablePath(list.mSerializableInstance),
                    Palette.getDefaultPaint(color));

            pp.mPath.moveTo(x, y);

            for (int j=0; j<instance.getxLineTo().get(i).size(); j++) {
                Float xx = instance.getxLineTo().get(i).get(j) * widthRatio;
                Float yy = instance.getyLineTo().get(i).get(j) * heightRatio;
                pp.mPath.lineTo(xx, yy);
            }

            // Add de-serialized PaintedPath to List
            list.add(pp);
        }

        return list;
    }

}


