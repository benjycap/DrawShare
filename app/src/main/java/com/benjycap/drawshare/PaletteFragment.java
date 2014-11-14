package com.benjycap.drawshare;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

/**
 * Created by Ben on 04/11/2014.
 */
public class PaletteFragment extends Fragment {

    private static final int SELECTED_ALPHA = 255;
    private static final int UNSELECTED_ALPHA = 75;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final TableLayout v = (TableLayout)inflater.inflate(R.layout.fragment_palette, container, false);

        if (getActivity() instanceof DrawActivity) {
            final Palette palette = ((DrawActivity) getActivity()).getActivityPalette();

            // Set up buttons
            int numRows = v.getChildCount();
            for (int r = 0; r < numRows; r++) {
                TableRow row = (TableRow) v.getChildAt(r);
                int numButtons = row.getChildCount();
                for (int b = 0; b < numButtons; b++) {
                    // For the first 2 rows:
                    if (r <= 1) {
                        final int selectedButtonIndex = r * numButtons + b;
                        final int color = Palette.colors.get(selectedButtonIndex);
                        final ImageButton imageButton = (ImageButton) row.getChildAt(b);
                        // Set color and alpha
                        int currentPaletteColor = palette.getCurrentPaint().getColor();
                        imageButton.setBackgroundColor(color);
                        if (color == currentPaletteColor) {
                            imageButton.getBackground().setAlpha(SELECTED_ALPHA);
                            imageButton.setId(R.id.currently_selected_color);
                        } else {
                            imageButton.getBackground().setAlpha(UNSELECTED_ALPHA);
                        }
                        // On click:
                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                palette.setCurrentPaintColor(color);
                                imageButton.getBackground().setAlpha(SELECTED_ALPHA);
                                // Return previously selected color to original alpha state
                                ImageButton prevButton = (ImageButton)v.findViewById(R.id.currently_selected_color);
                                if (prevButton != null) {
                                    prevButton.getBackground().setAlpha(UNSELECTED_ALPHA);
                                    prevButton.setId(View.NO_ID);
                                }
                                imageButton.setId(R.id.currently_selected_color);
                            }
                        });
                    }
                    // For the final row:
                    if (r == 2) {
                        final int width = 5 * (b + 1);
                        ImageButton imageButton = (ImageButton) row.getChildAt(b);
                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                palette.setCurrentStrokeWidth(width);
                            }
                        });
                    }
                }
            }
        }

        return v;
    }
}
