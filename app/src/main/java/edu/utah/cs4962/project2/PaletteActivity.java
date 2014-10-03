package edu.utah.cs4962.project2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by jesuszarate on 10/3/14.
 */
public class PaletteActivity extends Activity {

    PaintAreaView _paintAreaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _paintAreaView = new PaintAreaView(this);
        _paintAreaView.setBackgroundColor(Color.WHITE);

        final PaletteView paletteLayout = new PaletteView(this);
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams paletteViewLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 90);
        paletteViewLP.gravity = Gravity.CENTER_HORIZONTAL;
        rootLayout.addView(paletteLayout, paletteViewLP);

        // Back to Create Mode button.
        Button createModeButton = new Button(this);
        createModeButton.setText("Create Mode");
        rootLayout.addView(createModeButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 10));
        createModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Include the color the use picked so that it can also be updated in the
                // button preview of the Create Mode.
                Intent resultIntent = new Intent();
                resultIntent.putExtra(CreateModeActivity.BUTTON_COLOR, paletteLayout.get_selectedColor());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        // Determine how many splotches we want on the palette
        for (int splotchIndex = 0; splotchIndex < 6; splotchIndex++) {

            PaintView paintView = new PaintView(this);

            if (splotchIndex == 0) {
                paintView.setColor(Color.RED);
            }
            if (splotchIndex == 1) {
                // Orange
                paintView.setColor(0xFFFFA500);
            }
            if (splotchIndex == 2) {
                paintView.setColor(Color.YELLOW);
            }
            if (splotchIndex == 3) {
                // Blue
                paintView.setColor(0xFF0000FF);
            }
            if (splotchIndex == 4) {
                // Green
                paintView.setColor(0xFF00FF00);
            }
            if (splotchIndex == 5) {
                // Purple
                paintView.setColor(0xFF800080);
            }

            paletteLayout.addView(paintView, new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));

            paintView.setOnSplotchTouchListener(new PaintView.OnSplotchTouchListener() {
                @Override
                public void onSplotchTouched(PaintView v) {
                    _paintAreaView.invalidate();
                }
            });
            setContentView(rootLayout);
        }
    }
}
