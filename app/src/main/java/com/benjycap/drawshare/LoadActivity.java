package com.benjycap.drawshare;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by user on 14/11/2014.
 */
public class LoadActivity extends ActionBarActivity {

    LoadListFragment mLoadListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        FragmentManager fm = getSupportFragmentManager();

        mLoadListFragment = (LoadListFragment)fm.findFragmentById(R.id.fragment_load_list);

        if (mLoadListFragment == null) {
            mLoadListFragment = new LoadListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_load_list, mLoadListFragment)
                    .commit();
        }
    }


}
