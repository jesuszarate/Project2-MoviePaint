package edu.utah.cs4962.project2;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Jesus Zarate on 9/8/14.
 */
public class PaintView extends View {

    RectF _contentRect;
    float _radius;
    int _color = 0;
    Boolean isActive = false;

    public interface OnSplotchTouchListener {
        public void onSplotchTouched(PaintView v);
    }

    OnSplotchTouchListener _onSplotchTouchListener = null;

    public PaintView(Context context) {
        super(context);
        setMinimumHeight(1000);
        setMinimumWidth(1000);

        // Colors: OPACITY/RED/GREEN/BLUE
        //this.setBackgroundColor(0XFF228844);
    }

    public int getColor() {
        return _color;
    }

    public void setColor(int _color) {
        this._color = _color;

        // Redraws the circle so it can be the new color.(Marks it for redraw,
        // then generates an onDraw() when it's ready)
        invalidate();
    }

    public float getRadius() {
        return _radius;
    }

    public RectF getContentRect() {
        return _contentRect;
    }

    // The parameter is the interface type.
    public void setOnSplotchTouchListener(OnSplotchTouchListener listener) {
        _onSplotchTouchListener = listener;
    }

    public OnSplotchTouchListener getOnSplotchTouchListener() {
        return _onSplotchTouchListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        // Check if click is inside the circle, by measuring the distance
        //  between the center of the circle and the radius of the circle.
        // -> If the point clicked is less than the radius of the circle
        //    then it is a click.
        float CircleCenterX = _contentRect.centerX();
        float CircleCenterY = _contentRect.centerY();

        // Distance formula-> sqrt((x1 - x2)^2 + (y1 - y2)^2)
        float distance = (float) Math.sqrt(Math.pow(CircleCenterX - x, 2) + Math.pow(CircleCenterY - y, 2));
        if (distance < _radius) {

            if (_onSplotchTouchListener != null && !isActive) {
                isActive = true;
                makeOtherSplotchesInactive();
                invalidate();
                _onSplotchTouchListener.onSplotchTouched(this);
            }
        }
        return super.onTouchEvent(event);
    }

    public void makeOtherSplotchesInactive() {
        ArrayList<View> Children = PaletteView._children;

        PaintView child;
        for (int childIndex = 0; childIndex < Children.size(); childIndex++) {
            child = ((PaintView) Children.get(childIndex));
            if (child != this && child.isActive) {
                child.isActive = false;
                child.invalidate();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(_color);
        Path path = new Path();
        Paint highLightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highLightPaint.setColor(Color.GRAY);
        Path highLightPath = new Path();

        _contentRect = new RectF();
        _contentRect.left = getPaddingLeft();
        _contentRect.top = getPaddingTop();
        _contentRect.right = getWidth() - getPaddingRight();
        _contentRect.bottom = getHeight() - getPaddingBottom();

        PointF circleCenter = new PointF(_contentRect.centerX(), _contentRect.centerY());
        float maxRadius = Math.min(_contentRect.width() * 0.5f, _contentRect.height() * 0.5f);
        float minRadius = 0.5f * maxRadius;
        _radius = minRadius + (maxRadius - minRadius) * 0.5f;
        int pointCount = 43;

        for (int pointIndex = 0; pointIndex < pointCount; pointIndex += 3) {

            PointF point = new PointF();
            point.x = circleCenter.x + _radius * (float) Math.cos(((double) pointIndex /
                    (double) pointCount) * 2.0 * Math.PI);
            point.y = circleCenter.y + _radius * (float) Math.sin(((double) pointIndex /
                    (double) pointCount) * 2.0 * Math.PI);

            PointF control1 = new PointF();
            float control1Radius = _radius + (float) (Math.random() - 0.5) * 2.0f * 30.0f;
            control1.x = circleCenter.x + control1Radius * (float) Math.cos(((double) pointIndex /
                    (double) pointCount) * 2.0 * Math.PI);
            control1.y = circleCenter.y + control1Radius * (float) Math.sin(((double) pointIndex /
                    (double) pointCount) * 2.0 * Math.PI);

            PointF control2 = new PointF();
            float control2Radius = _radius + (float) (Math.random() - 0.5) * 2.0f * 30.0f;
            control2.x = circleCenter.x + control2Radius * (float) Math.cos(((double) pointIndex /
                    (double) pointCount) * 2.0 * Math.PI);

            control2.y = circleCenter.y + control2Radius * (float) Math.sin(((double) pointIndex /
                    (double) pointCount) * 2.0 * Math.PI);

            // If is active create a blob which is just a few pixes bigger to show the gray
            //  behind the blob creating the effect of a shadow and making it the selected blob.
            if (isActive) {
                if (pointIndex == 0) {
                    highLightPath.moveTo(point.x, point.y);
                } else {
                    highLightPath.cubicTo(control1.x + 15, control1.y + 15, control2.x + 15, control2.y + 15, point.x + 15, point.y + 15);
                }
            } else {
                if (pointIndex == 0) {
                    highLightPath.moveTo(0, 0);
                } else {
                    highLightPath.cubicTo(0, 0, 0, 0, 0, 0);
                }
            }

            if (pointIndex == 0) {
                path.moveTo(point.x, point.y);
            } else {
                //path.lineTo(point.x, point.y);
                path.cubicTo(control1.x, control1.y, control2.x, control2.y, point.x, point.y);
            }
        }
        canvas.drawPath(highLightPath, highLightPaint);

        canvas.drawPath(path, paint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // First four lines are extract the bit mask
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // Pull the information associated with the Mode.
        // -> Unspecified - widthSpec, heightSpec contain no value, usually 0.
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);

        int width = getSuggestedMinimumWidth();
        int height = getSuggestedMinimumHeight();

        if (widthMode == MeasureSpec.AT_MOST) {
            width = widthSpec;
        }
        if (heightMode == MeasureSpec.AT_MOST) {
            height = heightSpec;
        }

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSpec;
            height = width;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSpec;
            width = height;
        }

        // TODO; RESPECT THE PADDING!
        if (width > height && widthMode != MeasureSpec.EXACTLY) {
            width = height;
        }
        if (height > width && heightMode != MeasureSpec.EXACTLY) {
            height = width;
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // resolveSizeAndState(int size, int measureSpec, int childMeasuredState)
            // -> childMeasuredState - boolean asks if you are happy with the size or not.
            setMeasuredDimension(resolveSizeAndState((int)(width * 0.65), widthMeasureSpec,
                            width < getSuggestedMinimumWidth() ? MEASURED_STATE_TOO_SMALL : 0),
                    resolveSizeAndState((int)(height * 0.65), heightMeasureSpec,
                            height < getSuggestedMinimumHeight() ? MEASURED_STATE_TOO_SMALL : 0));
        }else{
            setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec,
                            width < getSuggestedMinimumWidth() ? MEASURED_STATE_TOO_SMALL : 0),
                    resolveSizeAndState(height, heightMeasureSpec,
                            height < getSuggestedMinimumHeight() ? MEASURED_STATE_TOO_SMALL : 0));
        }
    }
}
