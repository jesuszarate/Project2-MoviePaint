package edu.utah.cs4962.project2;

import android.app.Activity;
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


public class PaintActivity extends Activity {

    boolean _play = true;
    ImageView _playButton = null;
    WatchView _watchView;
    int numberOfPoints = 0;
    static SeekBar _seekBar;
    static boolean _touchedSeekBar = false;
    int _seekBarPosition = 0;
    boolean _animationFinished = false;

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
        _watchView.setOnAnimationFinishedListener(new WatchView.OnAnimationFinishedListener(){

            @Override
            public void onAnimationFinished(WatchView watchView) {
                _playButton.setImageResource(R.drawable.play);
                _play = true;

                _animationFinished = true;
            }
        });


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
                _seekBarPosition = (int) ((double) numberOfPoints * ((double) progress / 100.0));

                if (_touchedSeekBar) {

                    WatchView._count = _seekBarPosition;

                    _watchView.SeekBarRequested = true;
                    _watchView.invalidate();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                _touchedSeekBar = true;
                _playButton.setImageResource(R.drawable.play);
                _animationFinished = false;
                _play = true;
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
        backToCreateButton.setBackgroundColor(0xFF61D0BE);
        LinearLayout.LayoutParams backToCreateButtonParams;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            backToCreateButtonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 1);
            backToCreateButtonParams.setMargins(10, 10, 0, 10);
            MenuBar.addView(backToCreateButton, backToCreateButtonParams);
        }
        else{
            backToCreateButtonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            backToCreateButtonParams.setMargins(10, 0, 0, 10);
            MenuBar.addView(backToCreateButton, backToCreateButtonParams);
        }
        backToCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _watchView.invalidate();
                WatchView._count = 0;
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
                if (_play) {

                    if(_animationFinished){
                        WatchView._count = 0;
                        _seekBar.setProgress(0);
                        _animationFinished = false;
                    }
                    _playButton.setImageResource(R.drawable.pause);
                    _watchView.PlayAnimation();
                    _play = false;
                } else {
                    _playButton.setImageResource(R.drawable.play);
                    _watchView.PauseAnimation();
                    _play = true;
                }
            }
        });

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rootLayout.addView(MenuBar, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 20));

            LinearLayout watchSeekArea = new LinearLayout(this);
            watchSeekArea.setOrientation(LinearLayout.VERTICAL);

            watchSeekArea.addView(_watchView,
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 80));
            watchSeekArea.addView(SeekBarArea,
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rootLayout.addView(watchSeekArea, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 90));
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



        _watchView.loadMoviePostition(getFilesDir());
        _seekBarPosition = (int) (((double) WatchView._count * 100.0) / PaintAreaView.totalNumberOfPoint);
        _seekBar.setProgress(_seekBarPosition);
    }

    @Override
    protected void onPause() {
        super.onPause();

        _watchView.saveMoviePostition(getFilesDir());
    }


}
