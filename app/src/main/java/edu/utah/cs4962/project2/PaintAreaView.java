package edu.utah.cs4962.project2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.ViewGroup;

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
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jesus Zarate on 9/19/14.
 */
public class PaintAreaView extends ViewGroup {

    private int _lineCount = -1;
    public static HashMap<Integer, Line> _linePoints = new HashMap<Integer, Line>();
    private int _lineColor = Color.BLACK;
    Gson _gson = new Gson();

    public PaintAreaView(Context context) {
        super(context);
        setBackgroundColor(Color.WHITE);
    }

    public HashMap<Integer, Line> get_linePoints() {
        return _linePoints;
    }

    public void set_linePoints(HashMap<Integer, Line> _linePoints) {
        this._linePoints = _linePoints;
    }

    public void clearLinePoints() {
        this._linePoints.clear();
        this._lineCount = -1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4) {
        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            PaintView child = (PaintView) getChildAt(childIndex);
            if (child.isActive) {
                _lineColor = child.getColor();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            _lineCount++;

            Line line = new Line();
            line.setColor(PaletteView._selectedColor);

            _linePoints.put(_lineCount, line);
        }
        _linePoints.get(_lineCount).linePoints.add(new PointF(x, y));

        invalidate();

        return true;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int lineIndex = 0; lineIndex < _linePoints.size(); lineIndex++) {
            Paint polylinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            polylinePaint.setStyle(Paint.Style.STROKE);
            polylinePaint.setStrokeWidth(2.0f);
            Path polylinePath = new Path();
            polylinePaint.setColor(_linePoints.get(lineIndex).getColor());

            if (!_linePoints.isEmpty()) {
                try {
                    polylinePath.moveTo(_linePoints.get(lineIndex).linePoints.get(0).x,
                            _linePoints.get(lineIndex).linePoints.get(0).y);
                } catch (Exception e) {
                    continue;
                }

                for (PointF point : _linePoints.get(lineIndex).linePoints) {
                    polylinePath.lineTo(point.x, point.y);
                }
            }

            canvas.drawPath(polylinePath, polylinePaint);
        }
    }

    public void saveLinePoints(File filesDir) {
        String jsonPaintPoints = _gson.toJson(_linePoints);

        try {
            File file = new File(filesDir, "paintPoints.txt");
            FileWriter textWriter = null;
            textWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(textWriter);

            // Write the paint points in json format.
            bufferedWriter.write(jsonPaintPoints);
            bufferedWriter.close();

            // Also save the line count to keep track of the amount of lines that
            // have already been drawn.
            file = new File(filesDir, "lineCount.txt");
            textWriter = new FileWriter(file);
            BufferedWriter bufferedWriter1 = new BufferedWriter(textWriter);
            bufferedWriter1.write(_lineCount + "\n");
            bufferedWriter1.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadLinePoints(File filesDir) {
        try {
            File file = new File(filesDir, "paintPoints.txt");
            FileReader textReader = new FileReader(file);
            BufferedReader bufferedTextReader = new BufferedReader(textReader);
            String jsonBookList = null;
            jsonBookList = bufferedTextReader.readLine();

            Type booklistType = new TypeToken<HashMap<Integer, Line>>() {
            }.getType();
            HashMap<Integer, Line> linePoints = _gson.fromJson(jsonBookList, booklistType);
            _linePoints = linePoints;
            bufferedTextReader.close();

            file = new File(filesDir, "lineCount.txt");
            textReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(textReader);
            String lineCount = bufferedReader.readLine();
            bufferedReader.close();
            try {
                _lineCount = Integer.parseInt(lineCount);
            }catch (Exception e){
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class Line {
        ArrayList<PointF> linePoints = new ArrayList<PointF>();
        private int _color;

        public int getColor() {
            return _color;
        }

        public void setColor(int _color) {
            this._color = _color;
        }

    }
}

