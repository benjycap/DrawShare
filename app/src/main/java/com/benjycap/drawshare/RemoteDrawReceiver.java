package com.benjycap.drawshare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by Ben on 12/11/2014.
 */
public class RemoteDrawReceiver extends BroadcastReceiver {

    private static final String TAG = "RemoteDrawReceiver";

    PaintedView mPaintedView;

   public RemoteDrawReceiver(PaintedView paintedView) {
       mPaintedView = paintedView;
   }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(DrawActivity.EXTRA_PAINTED_PATH_DATA)) {
            PaintedPathList paintedPaths = PaintedPathList.deserializeForDimension(
                    (PaintedPathList.SerializableInstance) intent.getSerializableExtra(DrawActivity.EXTRA_PAINTED_PATH_DATA),
                    mPaintedView.getWidth(), mPaintedView.getHeight());
            mPaintedView.setPaintedPaths(paintedPaths);
        }
    }


}
