package com.benjycap.drawshare;

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
            remove(size() - 1);
            mSerializableInstance.removeLast();
        }
    }

    // To be called after Palette has been updated to user's specifications
    public void startNewPath(Palette palette) {
        // New path only needed if PaintedPath array is empty or if current Path has been used
        if (isEmpty() || !get(size() - 1).mPath.isEmpty()) {
            add(new PaintedPath(new PaintedPath.SerializablePath(mSerializableInstance), palette.getCurrentPaint()));
            // Serialize parts of the PaintedPath which aren't handled by SerializablePath (i.e. color, width)
            mSerializableInstance.pushColor(palette.getCurrentPaint().getColor());
            mSerializableInstance.pushWidth(palette.getCurrentPaint().getStrokeWidth());
        }
    }

    public static class SerializableInstance implements Serializable {

        private ArrayList<Integer> colors;
        private ArrayList<Float> widths;
        private ArrayList<Float> xMoveTo, yMoveTo;
        private ArrayList<ArrayList<Float>> xLineTo, yLineTo;

        public SerializableInstance() {
            colors = new ArrayList<Integer>();
            widths = new ArrayList<Float>();
            xMoveTo = new ArrayList<Float>();
            yMoveTo = new ArrayList<Float>();
            xLineTo = new ArrayList<ArrayList<Float>>();
            yLineTo = new ArrayList<ArrayList<Float>>();
        }

        public void pushColor(Integer color) {
            colors.add(color);
        }

        public void pushWidth(Float width) {
            widths.add(width);
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
            colors.remove(colors.size() - 1);
            widths.remove(widths.size() - 1);
            xMoveTo.remove(xMoveTo.size() - 1);
            yMoveTo.remove(yMoveTo.size() - 1);
            xLineTo.remove(xLineTo.size() - 1);
            yLineTo.remove(yLineTo.size() - 1);
        }

        public ArrayList<Integer> getColors() {
            return colors;
        }

        public ArrayList<Float> getWidths() {
            return widths;
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

        return deserializeForDimension(instance, DrawView.width, DrawView.height);

    }

    public static PaintedPathList deserializeForDimension(SerializableInstance instance, float containerWidth, float containerHeight) {

        float heightRatio = containerHeight/DrawView.height;
        float widthRatio = containerWidth/DrawView.width;
        float areaRatio = (containerHeight * containerWidth) / (DrawView.height * DrawView.width);

        PaintedPathList list = new PaintedPathList();

        for (int i=0; i<instance.getColors().size(); i++) {
            // Get values
            Integer color = instance.getColors().get(i);
            Float width = instance.getWidths().get(i);
            Float x = instance.getxMoveTo().get(i) * widthRatio;
            Float y = instance.getyMoveTo().get(i) * heightRatio;

            // Re-serialize color & stroke width in new list
            list.getSerializableInstance().pushColor(color);
            list.getSerializableInstance().pushWidth(width);

            float adjustedStrokeWidth = width * areaRatio;
            // Create new PaintedPath, supplying the new List's serializable member and the re-acquired color
            PaintedPath pp = new PaintedPath(new PaintedPath.SerializablePath(list.mSerializableInstance),
                    Palette.getDefaultPaint(color, adjustedStrokeWidth));

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


