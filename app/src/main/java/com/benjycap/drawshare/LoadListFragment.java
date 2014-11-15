package com.benjycap.drawshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Ben on 15/11/2014.
 */
public class LoadListFragment extends ListFragment {

    private static final String TAG = "LoadListFragment";

    private PaintedPathListLoadAdapter mListAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getResources().getString(R.string.empty_load_adapter));

        mListAdapter = new PaintedPathListLoadAdapter();
        setListAdapter(mListAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        PaintedPathList.SerializableInstance serializedPaintedPathCollection =
                SaveLoadHelper.Load(getActivity(), mListAdapter.getItem(position));
        Intent loadIntent = new Intent(getActivity(), DrawActivity.class);
        loadIntent.setAction(DrawActivity.ACTION_SEND_PAINTED_PATH_DATA);
        loadIntent.putExtra(DrawActivity.EXTRA_PAINTED_PATH_DATA, serializedPaintedPathCollection);
        startActivity(loadIntent);
    }

    private class PaintedPathListLoadAdapter extends ArrayAdapter<String> {

        private PaintedPathListLoadAdapter() {
            super(getActivity(), R.layout.load_adapter_list_view, R.id.load_list_text_view, getActivity().getFilesDir().list());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                v = inflater.inflate(R.layout.load_adapter_list_view, parent, false);
            }

            PaintedView paintedView = (PaintedView)v.findViewById(R.id.load_list_painted_view);
            TextView textView = (TextView)v.findViewById(R.id.load_list_text_view);

            textView.setText(getItem(position));
            PaintedPathList.SerializableInstance serializedPreview = SaveLoadHelper.Load(getActivity().getApplicationContext(), getItem(position));
            PaintedPathList preview = PaintedPathList.deserializeForDimension(serializedPreview, paintedView.getLayoutParams().width, paintedView.getLayoutParams().height);
            paintedView.setPaintedPaths(preview);

            return v;
        }

    }

}
