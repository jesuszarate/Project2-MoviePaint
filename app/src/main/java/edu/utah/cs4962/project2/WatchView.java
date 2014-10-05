package edu.utah.cs4962.project2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;

/**
 * Created by Jesus Zarate on 10/4/14.
 */
public class WatchView extends View {

    static int counter = 0;
    long startTime;
    long animationDuration = 10000; // 10 seconds
    int framesPerSecond = 10;
    boolean _pauseAnimation = false;
    int numberOfLinesDrawn = 0;

    public WatchView(Context context) {
        super(context);

        //this.startTime = System.currentTimeMillis();
        //this.postInvalidate();
        //init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!_pauseAnimation) {
            long elapsedTime = System.currentTimeMillis() - startTime;

            Log.w("elapsedTime", elapsedTime + "");

            //canvas.drawPath(path, paint);

//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            drawLinesInLandscapeMode(canvas);
//
//        } else {


            drawLinesInPortraitMode(canvas, (int) (elapsedTime / 50));

            if (elapsedTime < animationDuration)
                this.postInvalidateDelayed(1000 / framesPerSecond);
//
//        }
        }
        else{
            drawLinesInPortraitMode(canvas, numberOfLinesDrawn);
        }
    }

//    private void drawAnimatedLines(){
//        for (int lineIndex = 0; lineIndex < PaintAreaView._linePoints.size(); lineIndex++) {
//            Paint polylinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//            polylinePaint.setStyle(Paint.Style.STROKE);
//            polylinePaint.setStrokeWidth(2.0f);
//            Path polylinePath = new Path();
//            polylinePaint.setColor(PaintAreaView._linePoints.get(lineIndex).getColor());
//
//
//        }
//    }

    private void drawLinesInPortraitMode(Canvas canvas, int numOfLinesToDraw) {

        numberOfLinesDrawn = numOfLinesToDraw;
        int count = 0;
        for (int lineIndex = 0; lineIndex < PaintAreaView._linePoints.size(); lineIndex++) {
//        for (int lineIndex = 0; lineIndex < 1; lineIndex++) {
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
//                for (int index = 0; index < PaintAreaView._linePoints.get(lineIndex).linePoints.size(); index++) {
//                for (int index = 0; index < 10; index++) {
                    if (count < numOfLinesToDraw) {
                        //PointF point = PaintAreaView._linePoints.get(lineIndex).linePoints.get(index);
                        count++;
                        polylinePath.lineTo(point.x, point.y);
                    }
                }
            }

            canvas.drawPath(polylinePath, polylinePaint);
        }
    }

    public void PauseAnimation() {
        _pauseAnimation = true;
        postInvalidate();
    }

    public void PlayAnimation() {
        this.startTime = System.currentTimeMillis();
        _pauseAnimation = false;
        postInvalidate();

//        //int w = canvas.getWidth();
////        int h = canvas.getHeight();
////        canvas.drawLine(w/2, 0, w/2, h-1, paint);
//
//        // PAUSE FIVE SECONDS
//        new CountDownTimer(5000, 1000) {
//
//            @Override
//            public void onTick(long miliseconds) {
////                    if(miliseconds % 1000 == 0){
//                counter++;
//                Log.d("WatchView", counter + "");
////                }
//            }
//
//            @Override
//            public void onFinish() {
//                //after 5 seconds draw the second line
////                canvas.drawLine(0, h/2, w-1, h/2, paint);
//                //Log.d("WatchView", "Finished");
//
//
//                //drawLinesInPortraitMode(canv);
//
//
//                //invalidate();
//            }
//        }.start();

    }

    private void drawLinesInLandscapeMode(Canvas canvas) {
        for (int lineIndex = 0; lineIndex < 5; lineIndex++) {
//        for (int lineIndex = 0; lineIndex < PaintAreaView._linePoints.size(); lineIndex++) {
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
                    polylinePath.lineTo(point.x, point.y);
                }
            }
            canvas.drawPath(polylinePath, polylinePaint);
        }
    }
}
