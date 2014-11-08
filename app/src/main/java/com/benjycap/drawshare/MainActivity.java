package com.benjycap.drawshare;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = "MainActivity";

    private DrawFragment mDrawFragment;
    private PaletteFragment mPaletteFragment;
    private ImageButton mPaletteToggleButton;

    private DrawView mDrawView;
    private TableLayout mPaletteTable;

    private Palette mPalette;

    private Bundle mSavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mSavedInstanceState = savedInstanceState;

        mPalette = new Palette();

        // Create / Assign fragments
        FragmentManager fm = getSupportFragmentManager();
        mDrawFragment = (DrawFragment)fm.findFragmentById(R.id.fragment_draw);
        mPaletteFragment = (PaletteFragment)fm.findFragmentById(R.id.fragment_palette);

        if (mDrawFragment == null || mPaletteFragment == null) {
            // Create fragments
            mDrawFragment = new DrawFragment();
            mPaletteFragment = new PaletteFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_draw, mDrawFragment)
                    .add(R.id.fragment_palette, mPaletteFragment)
                    .commit();
            }

        // Set click listener for show/hide palette button
        mPaletteToggleButton = (ImageButton)findViewById(R.id.palette_toggle_button);
        mPaletteToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start fragment transaction
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
                // Get layout params
                RelativeLayout.LayoutParams params =
                        (RelativeLayout.LayoutParams)mPaletteToggleButton.getLayoutParams();
                if (!mPaletteFragment.isHidden()) {
                    ft.hide(mPaletteFragment);
                    params.addRule(RelativeLayout.LEFT_OF, 0);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                } else {
                    ft.show(mPaletteFragment);
                    params.addRule(RelativeLayout.LEFT_OF, R.id.fragment_palette);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                }
                ft.commit();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Assign member views
        mDrawView = (DrawView)findViewById(R.id.userDrawView);
        mPaletteTable = (TableLayout)findViewById(R.id.paletteTable);

        // Pair palette to Draw View
        mDrawView.setPalette(mPalette);
        // Put back painted paths from saved instance state
        if (mSavedInstanceState != null) {
            if (mSavedInstanceState.getSerializable(DrawView.EXTRA_PAINTED_PATHS) != null)
                mDrawView.setPaintedPaths(PaintedPathList.deserializer(
                        (PaintedPathList.SerializableInstance) mSavedInstanceState.getSerializable(DrawView.EXTRA_PAINTED_PATHS)));
        }

        // Set up buttons
        int numRows = mPaletteTable.getChildCount();
        for (int r = 0; r < numRows; r++) {
            TableRow row = (TableRow) mPaletteTable.getChildAt(r);
            int numButtons = row.getChildCount();
            for (int b = 0; b < numButtons; b++) {
                // For the first 2 rows:
                if (r <= 1) {
                    final int color = Palette.colors.get(r * numButtons + b);
                    ImageButton imageButton = (ImageButton) row.getChildAt(b);
                    imageButton.setBackgroundColor(color);
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mPalette.setCurrentPaintColor(color);
                        }
                    });
                }
                // For the final row:
                if (r == 4) {
                    final int width = 5 * (b+1);
                    ImageButton imageButton = (ImageButton) row.getChildAt(b);
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mPalette.setCurrentStrokeWidth(width);
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mDrawView != null)
            outState.putSerializable(DrawView.EXTRA_PAINTED_PATHS, mDrawView.getPaintedPaths().getSerializableInstance());

        super.onSaveInstanceState(outState);
    }

    public Palette getPalette() {
        return mPalette;
    }

}
