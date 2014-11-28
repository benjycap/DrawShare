package com.benjycap.drawshare;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ben on 15/11/2014.
 */
public class LoadListFragment extends ListFragment {

    private static final String TAG = "LoadListFragment";

    private PaintedPathListLoadAdapter mListAdapter;
    private Set<String> mContextMenuSelectedItems;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getResources().getString(R.string.empty_load_adapter));

        mListAdapter = new PaintedPathListLoadAdapter();
        setListAdapter(mListAdapter);

        if (Build.VERSION.SDK_INT >= 11) {
            enableContextualActionBar();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String fileName = mListAdapter.getItem(position);

        PaintedPathList.SerializableInstance serializedPaintedPathCollection =
                FileHelper.Load(getActivity(), fileName);
        Intent loadIntent = new Intent(getActivity(), DrawActivity.class);

        loadIntent.setAction(DrawActivity.ACTION_SEND_PAINTED_PATH_DATA);
        loadIntent.putExtra(DrawActivity.EXTRA_PAINTED_PATH_DATA, serializedPaintedPathCollection);
        loadIntent.putExtra(DrawActivity.EXTRA_FILE_NAME, fileName);

        startActivity(loadIntent);
    }

    private class PaintedPathListLoadAdapter extends ArrayAdapter<String> {

        private PaintedPathListLoadAdapter() {
            super(getActivity(), R.layout.load_adapter_list_view, R.id.load_list_text_view, new ArrayList<String>(Arrays.asList(getActivity().getFilesDir().list())));
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

            // Set TextView as file name
            textView.setText(getItem(position));
            PaintedPathList.SerializableInstance serializedPreview = FileHelper.Load(getActivity().getApplicationContext(), getItem(position));
            PaintedPathList preview = PaintedPathList.deserializeForDimension(serializedPreview, paintedView.getLayoutParams().width, paintedView.getLayoutParams().height);
            // Set preview PaintedView
            paintedView.setPaintedPaths(preview);
            // Set background to transparent
            paintedView.setBackgroundColor(getResources().getColor(android.R.color.transparent));

            return v;
        }

        @Override
        public void remove(String object) {
            boolean deleteSuccessful = FileHelper.Delete(getActivity(), object);
            if (!deleteSuccessful) {
                Log.e(TAG, "Error deleting file "+object);
            }
            super.remove(object);
        }

    }

    private void enableContextualActionBar() {
        mContextMenuSelectedItems = new HashSet<String>();
        final ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int pos, long id, boolean checked) {
                View checkedView = listView.getChildAt(pos);
                String fileName = mListAdapter.getItem(pos);

                int color = getResources().getColor(
                        checked ? R.color.item_selected : android.R.color.transparent);
                checkedView.setBackgroundColor(color);

                if (checked) {
                    mContextMenuSelectedItems.add(fileName);
                } else {
                    mContextMenuSelectedItems.remove(fileName);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater menuInflater = actionMode.getMenuInflater();
                menuInflater.inflate(R.menu.load_activity_contextual_actions, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                deleteSelectedItems();
                actionMode.finish();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                makeViewBackgroundsTransparent();
            }
        });
    }

    private void deleteSelectedItems() {
        for (String fileName : mContextMenuSelectedItems) {
            mListAdapter.remove(fileName);
        }
        mContextMenuSelectedItems.clear();
    }

    private void makeViewBackgroundsTransparent() {
        for (int i = 0; i < getListView().getChildCount(); i++) {
            View v = getListView().getChildAt(i);
            v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }

}
