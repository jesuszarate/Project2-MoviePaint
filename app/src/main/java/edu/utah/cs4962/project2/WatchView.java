package edu.utah.cs4962.project2;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by Jesus Zarate on 10/4/14.
 */
public class WatchView extends View {

    static int counter = 0;
    long startTime;
    long animationDuration = 10000; // 10 seconds
    int framesPerSecond = 10;
    boolean _pauseAnimation = false;
    boolean _playAnimation = false;
    int numberOfLinesDrawn = 0;
    boolean SeekBarRequested = false;
    int SeekBarProgress = 0;
    static int _count = 1;

    public WatchView(Context context) {
        super(context);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (SeekBarRequested) {
            drawLinesInPortraitMode(canvas, _count);
            SeekBarRequested = false;
        } else if (!_pauseAnimation && _playAnimation) {

            if (PaintAreaView.totalNumberOfPoint > _count) {
                drawLinesInPortraitMode(canvas, _count);

                double num = (double) _count * 100.0;

                PaintActivity._seekBar.setProgress((int) (num / PaintAreaView.totalNumberOfPoint));
                this.postInvalidateDelayed(50);
                _count += 2;
            } else {
                PauseAnimation();
            }
        } else if (!_pauseAnimation && false) {
            long elapsedTime = System.currentTimeMillis() - startTime;

            drawLinesInPortraitMode(canvas, (int) (elapsedTime / 50));

            if (elapsedTime < animationDuration)
                this.postInvalidateDelayed(1000 / framesPerSecond);
        } else {
            drawLinesInPortraitMode(canvas, _count);
        }

    }

    private void drawLinesInPortraitMode(Canvas canvas, int numOfLinesToDraw) {

        numberOfLinesDrawn = numOfLinesToDraw;
        int count = 0;
        for (int lineIndex = 0; lineIndex < PaintAreaView._linePoints.size(); lineIndex++) {
            Paint polylinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            polylinePaint.setStyle(Paint.Style.STROKE);
            polylinePaint.setStrokeWidth(2.0f);
            Path polylinePath = new Path();
            polylinePaint.setColor(PaintAreaView._linePoints.get(lineIndex).getColor());

            if (!PaintAreaView._linePoints.isEmpty()) {
                try {
                    polylinePath.moveTo(PaintAreaView._linePoints.get(lineIndex).linePoints.get(0).x,
                            PaintAreaView._linePoints.get(lineIndex).linePoints.get(0).y);
                } catch (Exception e) {
                    continue;
                }

                for (PointF point : PaintAreaView._linePoints.get(lineIndex).linePoints) {
                    if (count < numOfLinesToDraw) {
                        count++;
                        polylinePath.lineTo(point.x, point.y);
                    }
                }
            }

            canvas.drawPath(polylinePath, polylinePaint);
        }
    }

    /**
     * Working perfectly Uncomment it if the other one gets messed up.
     * @param canvas
     * @param numOfLinesToDraw
     */
//    private void drawLinesInPortraitMode(Canvas canvas, int numOfLinesToDraw) {
//
//        numberOfLinesDrawn = numOfLinesToDraw;
//        int count = 0;
//        for (int lineIndex = 0; lineIndex < PaintAreaView._linePoints.size(); lineIndex++) {
//            Paint polylinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//            polylinePaint.setStyle(Paint.Style.STROKE);
//            polylinePaint.setStrokeWidth(2.0f);
//            Path polylinePath = new Path();
//            polylinePaint.setColor(PaintAreaView._linePoints.get(lineIndex).getColor());
//
//            if (!PaintAreaView._linePoints.isEmpty()) {
//                try {
//                    polylinePath.moveTo(PaintAreaView._linePoints.get(lineIndex).linePoints.get(0).x,
//                            PaintAreaView._linePoints.get(lineIndex).linePoints.get(0).y);
//                } catch (Exception e) {
//                    continue;
//                }
//
//                for (PointF point : PaintAreaView._linePoints.get(lineIndex).linePoints) {
//                    if (count < numOfLinesToDraw) {
//                        count++;
//                        polylinePath.lineTo(point.x, point.y);
//                    }
//                }
//            }
//
//            canvas.drawPath(polylinePath, polylinePaint);
//        }
//    }

    public void PauseAnimation() {
        _pauseAnimation = true;
        _playAnimation = false;

        postInvalidate();
    }

    public void PlayAnimation() {
        this.startTime = System.currentTimeMillis();
        _pauseAnimation = false;
        _playAnimation = true;
        PaintActivity._touchedSeekBar = false;
        postInvalidate();

    }

    private void drawLinesInLandscapeMode(Canvas canvas, int numOfLinesToDraw) {
        numberOfLinesDrawn = numOfLinesToDraw;
        int count = 0;
        for (int lineIndex = 0; lineIndex < PaintAreaView._linePoints.size(); lineIndex++) {
            Paint polylinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            polylinePaint.setStyle(Paint.Style.STROKE);
            polylinePaint.setStrokeWidth(2.0f);
            Path polylinePath = new Path();
            polylinePaint.setColor(PaintAreaView._linePoints.get(lineIndex).getColor());

            if (!PaintAreaView._linePoints.isEmpty()) {
                try {
                    polylinePath.moveTo(PaintAreaView._linePoints.get(lineIndex).linePoints.get(0).x,
                            PaintAreaView._linePoints.get(lineIndex).linePoints.get(0).y);
                } catch (Exception e) {
                    continue;
                }

                for (PointF point : PaintAreaView._linePoints.get(lineIndex).linePoints) {
                    if (count < numOfLinesToDraw) {
                        count++;
                        polylinePath.lineTo(point.x, point.y);
                    }
                }
            }

            canvas.drawPath(polylinePath, polylinePaint);
        }
    }

    public void saveMoviePostition(File filesDir) {

        try {
            File file = new File(filesDir, "numberOfPointsDrawn.txt");
            FileWriter textWriter = null;

            textWriter = new FileWriter(file);

            BufferedWriter bufferedWriter = new BufferedWriter(textWriter);

            // Write the paint points in json format.
            bufferedWriter.write(_count);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadMoviePostition(File filesDir) {
        try {
            File file = new File(filesDir, "numberOfPointsDrawn.txt");
            FileReader textReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(textReader);
            String lineCount = bufferedReader.readLine();
            bufferedReader.close();
            try {
                _count = Integer.parseInt(lineCount);
            } catch (Exception e) {
                e.printStackTrace();
            }

            postInvalidate();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
