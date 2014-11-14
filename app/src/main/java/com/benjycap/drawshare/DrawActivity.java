package com.benjycap.drawshare;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;


public class DrawActivity extends ActionBarActivity {

    public static final String TAG = "DrawActivity";

    private static final String EXTRA_CURRENT_COLOR = "currentColor";

    // Fragments
    private DrawFragment mDrawFragment;
    private PaletteFragment mPaletteFragment;
    private RemoteDrawFragment mRemoteDrawFragment;

    // Activity Views
    private ImageButton mPaletteToggleButton, mRemoteDrawToggleButton;

    // Activity Members
    private Palette mPalette;
    public Palette getActivityPalette() {
        return mPalette;
    }


    /*
    * Android Methods
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        // Create Palette
        mPalette = new Palette();

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_CURRENT_COLOR)) {
            int savedColor = savedInstanceState.getInt(EXTRA_CURRENT_COLOR);
            mPalette.setCurrentPaintColor(savedColor);
        }

        // Create / Assign fragments
        FragmentManager fm = getSupportFragmentManager();
        mDrawFragment = (DrawFragment)fm.findFragmentById(R.id.fragment_draw);
        mPaletteFragment = (PaletteFragment)fm.findFragmentById(R.id.fragment_palette);
        mRemoteDrawFragment = (RemoteDrawFragment)fm.findFragmentById(R.id.fragment_remote_draw);

        // Create fragments
        FragmentTransaction ft = fm.beginTransaction();
        if (mDrawFragment == null) {
            mDrawFragment = new DrawFragment();
            ft.add(R.id.fragment_draw, mDrawFragment);
        }
        if (mPaletteFragment == null) {
            mPaletteFragment = new PaletteFragment();
            ft.add(R.id.fragment_palette, mPaletteFragment);
        }
        if (mRemoteDrawFragment == null) {
            mRemoteDrawFragment = new RemoteDrawFragment();
            ft.add(R.id.fragment_remote_draw, mRemoteDrawFragment);
        }
        ft.commit();


        mPaletteToggleButton = (ImageButton)findViewById(R.id.palette_toggle_button);
        mRemoteDrawToggleButton = (ImageButton)findViewById(R.id.remote_draw_toggle_button);

        setupSaveLoadButtons();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        setupHidableButton(mPaletteToggleButton, mPaletteFragment);
        setupHidableButton(mRemoteDrawToggleButton, mRemoteDrawFragment);

        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(EXTRA_CURRENT_COLOR, mPalette.getCurrentPaint().getColor());
        super.onSaveInstanceState(outState);
    }


    /*
    * Private Methods
    */

    private void setupHidableButton(final ImageButton button, final Fragment ownerFragment) {
        // Put toggle button in correct place
        setButtonTopMargin(button, ownerFragment);
        setButtonPosition(button, ownerFragment, false);

        // Set click listener for show/hide palette button

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start fragment transaction
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
                if (ownerFragment.isHidden()) {
                    ft.show(ownerFragment);
                } else {
                    ft.hide(ownerFragment);
                }
                ft.commit();
                setButtonPosition(button, ownerFragment, true);
            }
        });
    }

    private void setButtonPosition(ImageButton button, Fragment ownerFragment, boolean changePosition) {

        boolean alignButtonParentRight = ownerFragment.isHidden();
        // changePosition is true if we want to swap the location of the button,
        // false if we simply want to reset the position after an orientation change
        if (changePosition)
            alignButtonParentRight = !alignButtonParentRight;

        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams)button.getLayoutParams();
        if (alignButtonParentRight) {
            button.setImageResource(R.drawable.arrow_left_no_tail);
            params.addRule(RelativeLayout.LEFT_OF, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else { // Align button left of fragment
            button.setImageResource(R.drawable.arrow_right_no_tail);
            params.addRule(RelativeLayout.LEFT_OF, ownerFragment.getId());
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        }
    }

    // Set button position programmatically according to position of owner fragment
    private void setButtonTopMargin(ImageButton button, Fragment ownerFragment) {

        FrameLayout fragmentHolder = (FrameLayout)findViewById(ownerFragment.getId());
        int buttonTopMargin, buttonHeight, fragmentTop, fragmentHeight;
        RelativeLayout.LayoutParams buttonParams = (RelativeLayout.LayoutParams)button.getLayoutParams();

        fragmentHeight = fragmentHolder.getHeight();
        fragmentTop = fragmentHolder.getTop();
        buttonHeight = button.getHeight();

        buttonTopMargin = fragmentHeight/2 + fragmentTop - buttonHeight/2;

        buttonParams.topMargin = buttonTopMargin;
    }

    private void setupSaveLoadButtons() {
        Button saveButton = (Button)findViewById(R.id.save_button);
        Button loadButton = (Button)findViewById(R.id.load_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaintedPathList.SerializableInstance saveInstance = mDrawFragment.getDrawViewSerializable();
                SaveLoadHelper.Save(DrawActivity.this, saveInstance, "test");
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaintedPathList.SerializableInstance loadInstance = SaveLoadHelper.Load(DrawActivity.this, "test");
                mDrawFragment.setDrawViewPaintedPathList(PaintedPathList.deserialize(loadInstance));
            }
        });
    }
}
