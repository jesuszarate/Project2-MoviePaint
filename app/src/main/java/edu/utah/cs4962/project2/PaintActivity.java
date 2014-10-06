package edu.utah.cs4962.project2;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.gson.Gson;


public class PaintActivity extends Activity {

    Gson _gson = new Gson();
    boolean _play = false;
    ImageView _playButton = null;
    WatchView _watchView;
    int numberOfPoints = 0;
    static SeekBar _seekBar;
    static boolean _touchedSeekBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(CreateModeActivity.NUMBER_OF_POINTS_EXTRA)) {
            numberOfPoints = getIntent().getExtras().getInt(CreateModeActivity.NUMBER_OF_POINTS_EXTRA);
        }
        _playButton = new ImageView(this);

        LinearLayout rootLayout = new LinearLayout(this);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            rootLayout.setOrientation(LinearLayout.HORIZONTAL);
        else
            rootLayout.setOrientation(LinearLayout.VERTICAL);

        _watchView = new WatchView(this);
        _watchView.setBackgroundColor(Color.WHITE);

        // Seek bar
        LinearLayout SeekBarArea = new LinearLayout(this);
        SeekBarArea.setOrientation(LinearLayout.VERTICAL);
        _seekBar = new SeekBar(this);
        _seekBar.setBackgroundColor(Color.WHITE);
        SeekBarArea.addView(_seekBar);
        _seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                // Must divide by one hundred because there is only one hundred points
                // int the seek bar.
                int num = (int) Math.ceil(numberOfPoints / 100.0);
                if (_touchedSeekBar) {
//                int num = (int) Math.ceil(_watchView._count / 100.0);

//                _watchView.SeekBarProgress = progress * num;

                    _watchView._count = progress * num;

                    _watchView.SeekBarRequested = true;
                    _watchView.invalidate();
                    //touchedSeekBar = false;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.w("Seek Track Touched", "Touched");
                _touchedSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();


                // Process here, for example using AsyncTask
            }
        });


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
                _watchView.invalidate();
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
                    _playButton.setImageResource(R.drawable.pause);
                    _watchView.PauseAnimation();
                    _play = false;
                } else {
                    toast = Toast.makeText(getBaseContext(), "Pause", Toast.LENGTH_SHORT);
                    _playButton.setImageResource(R.drawable.play);
                    _watchView.PlayAnimation();
                    _play = true;
                }

                toast.show();
            }
        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rootLayout.addView(MenuBar, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 10));

            LinearLayout watchSeekArea = new LinearLayout(this);
            watchSeekArea.setOrientation(LinearLayout.VERTICAL);

            watchSeekArea.addView(_watchView,
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 90));
            watchSeekArea.addView(SeekBarArea,
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rootLayout.addView(watchSeekArea, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 90));
//            rootLayout.addView(_watchView,
//                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 90));
//            rootLayout.addView(SeekBarArea,
//                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            rootLayout.addView(_watchView,
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 90));
            rootLayout.addView(SeekBarArea,
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rootLayout.addView(MenuBar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 10));
        }

        setContentView(rootLayout);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //_paintAreaView.loadLinePoints(getFilesDir());

    }

    @Override
    protected void onPause() {
        super.onPause();

        //_paintAreaView.saveLinePoints(getFilesDir());

    }


}
