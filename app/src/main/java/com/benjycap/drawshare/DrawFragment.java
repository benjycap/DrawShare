package com.benjycap.drawshare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ben on 04/11/2014.
 */
public class DrawFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_draw, container, false);
        DrawView drawView = (DrawView)v.findViewById(R.id.userDrawView);
        drawView.setPalette(((DrawActivity)getActivity()).getActivityPalette());
        return v;
    }



}
