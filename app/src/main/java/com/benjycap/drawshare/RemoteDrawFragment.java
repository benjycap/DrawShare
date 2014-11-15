package com.benjycap.drawshare;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Ben on 12/11/2014.
 */
public class RemoteDrawFragment extends Fragment {

    RemoteDrawReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_remote_draw, container, false);
        PaintedView remoteDrawView = (PaintedView)v.findViewById(R.id.userRemoteDrawView);

        IntentFilter intentFilter = new IntentFilter(DrawActivity.ACTION_SEND_PAINTED_PATH_DATA);
        mReceiver = new RemoteDrawReceiver(remoteDrawView);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);

        return v;
    }

    @Override
    public void onDetach() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onDetach();
    }
}
