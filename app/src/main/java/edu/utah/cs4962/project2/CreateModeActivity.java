package edu.utah.cs4962.project2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.HashMap;

/**
 * Created by jesuszarate on 10/3/14.
 */
public class CreateModeActivity extends Activity {
    PaintAreaView _paintAreaView;
    public PaintView _paintButton;
    public Button _watchModeButton;
    public static String BUTTON_COLOR = "BUTTON_COLOR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);


        _paintAreaView = new PaintAreaView(this);
        _paintAreaView.setBackgroundColor(Color.WHITE);


        rootLayout.addView(_paintAreaView,
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 85));

        // Menu bar
        LinearLayout MenuBar = new LinearLayout(this);
        MenuBar.setOrientation(LinearLayout.HORIZONTAL);
        MenuBar.setBackgroundColor(Color.DKGRAY);

        // Palette activity button
        _paintButton = new PaintView(this);
        _paintButton.setColor(PaletteView._selectedColor);
        _paintButton.setBackgroundColor(Color.LTGRAY);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 30);
        params.setMargins(10, 10, 10 ,10);
        MenuBar.addView(_paintButton, params);
        _paintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewPaletteActivity();
            }
        });

        // Watch mode button
        _watchModeButton = new Button(this);
        _watchModeButton.setText("Watch Mode");
        _watchModeButton.setTextColor(Color.WHITE);
        _watchModeButton.setBackgroundColor(Color.LTGRAY);
        params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 30);
        params.setMargins(10, 10, 10, 10);
        MenuBar.addView(_watchModeButton, params);
        _watchModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewPaintActivity();
            }
        });

        // Clear button
        Button clearButton = new Button(this);
        clearButton.setText("Clear");
        clearButton.setTextColor(Color.WHITE);
        clearButton.setBackgroundColor(Color.LTGRAY);
        params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 30);
        params.setMargins(10, 10, 10, 10);
        MenuBar.addView(clearButton, params);
        clearButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                _paintAreaView.clearLinePoints();
                _paintAreaView.saveLinePoints(getFilesDir());
                _paintAreaView.invalidate();
            }
        });

        rootLayout.addView(MenuBar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 15));

        setContentView(rootLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _paintAreaView.loadLinePoints(getFilesDir());
    }

    @Override
    protected void onPause() {
        super.onPause();
        _paintAreaView.saveLinePoints(getFilesDir());
    }

    private void startNewPaletteActivity() {
        Intent intent = new Intent(this, PaletteActivity.class);
        this.startActivityForResult(intent, 6);
    }

    private void startNewPaintActivity() {
        Intent intent = new Intent(this, PaintActivity.class);
        this.startActivity(intent);
    }
//    private void startWatchActivity() {
//        Intent intent = new Intent(this, WatchView.class);
//        this.startActivity(intent);
//    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
           int color = data.getIntExtra(BUTTON_COLOR, 0x00000000);

            _paintButton.setColor(color);
            _watchModeButton.setTextColor(color);
        }
    }

}
