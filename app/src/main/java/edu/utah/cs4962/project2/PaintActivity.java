package edu.utah.cs4962.project2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;


public class PaintActivity extends Activity {

    Gson _gson = new Gson();
    boolean _play = false;
    ImageView _playButton = null;
    PaintAreaView _paintAreaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _playButton = new ImageView(this);

        LinearLayout rootLayout = new LinearLayout(this);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            rootLayout.setOrientation(LinearLayout.HORIZONTAL);
        else
            rootLayout.setOrientation(LinearLayout.VERTICAL);

        _paintAreaView = new PaintAreaView(this);
        _paintAreaView.setBackgroundColor(Color.WHITE);

        // TODO:    Uncomment Me.
//        // Seek bar
//        LinearLayout SeekBarArea = new LinearLayout(this);
//        SeekBarArea.setOrientation(LinearLayout.VERTICAL);
//        SeekBar seekBar = new SeekBar(this);
//        seekBar.setBackgroundColor(Color.WHITE);
//        SeekBarArea.addView(seekBar);
//        rootLayout.addView(SeekBarArea,
//                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Menu bar
        LinearLayout MenuBar = new LinearLayout(this);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            MenuBar.setOrientation(LinearLayout.VERTICAL);
        else
            MenuBar.setOrientation(LinearLayout.HORIZONTAL);
        MenuBar.setBackgroundColor(Color.WHITE);

        // GO BACK TO THE CREATE MODE.
        Button backToCreateButton = new Button(this);
        backToCreateButton.setText("Create Mode");
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            MenuBar.addView(backToCreateButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        else
            MenuBar.addView(backToCreateButton, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        backToCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _paintAreaView.invalidate();
                finish();
            }
        });

        // Play Button
        _playButton.setImageResource(R.drawable.play);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            MenuBar.addView(_playButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        else
            MenuBar.addView(_playButton, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));

        _playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast;
                if (_play) {

                    toast = Toast.makeText(getBaseContext(), "Play", Toast.LENGTH_SHORT);
                    _playButton.setImageResource(R.drawable.play);
                    _play = false;
                } else {
                    toast = Toast.makeText(getBaseContext(), "Pause", Toast.LENGTH_SHORT);
                    _playButton.setImageResource(R.drawable.pause);
                    _play = true;
                }

                toast.show();
            }
        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rootLayout.addView(MenuBar, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 10));
            rootLayout.addView(_paintAreaView,
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 90));
        }
        else {
            rootLayout.addView(_paintAreaView,
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 90));
            rootLayout.addView(MenuBar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 10));
        }

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


}
