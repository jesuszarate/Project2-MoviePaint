package edu.utah.cs4962.project2;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jesus Zarate on 9/15/14.
 */
public class PaletteView extends ViewGroup {

    public static ArrayList<View> _children = new ArrayList<View>();
    public ArrayList<Integer> _colors = new ArrayList<Integer>();
    private HashMap<PaintView, PointF> _centerPosOfSplotches = new HashMap<PaintView, PointF>();

    private Rect _layoutRect;

    private int _childrenNotGone = 0;

    private int _childrenGone = 0;

    public static int _selectedColor = Color.BLACK;

    RectF _paletteRect;

    public PaletteView(Context context) {
        super(context);
    }

    public static int get_selectedColor() {
        return _selectedColor;
    }

    public static void set_selectedColor(int _selectedColor) {
        PaletteView._selectedColor = _selectedColor;
    }

    public void addNewColor(PaintView child, PaintView mixWithThisColor) {
        if (_childrenNotGone <= 10) {
            PaintView paintView = new PaintView(getContext());
            int firstColor = child.getColor();
            int secondColor = mixWithThisColor.getColor();
            int red1 = firstColor & 0x00FF0000;
            int red2 = secondColor & 0x00FF0000;

            int green1 = firstColor & 0x0000FF00;
            int green2 = secondColor & 0x0000FF00;

            int blue1 = firstColor & 0x000000FF;
            int blue2 = secondColor & 0x000000FF;

            int red = (red1 + red2) / 2;
            int green = (green1 + green2) / 2;
            int blue = (blue1 + blue2) / 2;

            int color = 0xFF000000 | red | green | blue;

            paintView.setColor(color);
            addView(paintView, new LayoutParams(200, LayoutParams.WRAP_CONTENT));
            _colors.add(color);
            paintView.setOnSplotchTouchListener(new PaintView.OnSplotchTouchListener() {
                @Override
                public void onSplotchTouched(PaintView v) {
                    invalidate();
                }
            });

            this.invalidate();
        }
        else{
            Toast.makeText(getContext(), "Exceeded the number of paints in the palette.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public boolean removeColor(PaintView paintView) {
        if (getChildCount() - _childrenGone > 1) {
            paintView.setVisibility(GONE);

            for(int colorIndex = 0; colorIndex < _colors.size(); colorIndex++) {
                if(_colors.get(colorIndex) == paintView.getColor())
                    _colors.remove(colorIndex);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        PaintView child;
        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            child = (PaintView) getChildAt(childIndex);
            if (child.isActive) {
                child.setX(x - child.getWidth() / 2);
                child.setY(y - child.getHeight() / 2);

                if (event.getActionMasked() == MotionEvent.ACTION_UP) {

                    if (isCorrectDistance(x, y)) {

                        // If the paint is dragged on top of another paint mix them together to
                        // create a new color.
                        PaintView mixWithThisColor = mixPaint(child, x, y);
                        if (mixWithThisColor != null) {
                            addNewColor(child, mixWithThisColor);
                            child.makeOtherSplotchesInactive();
                        }

                        // Return the paint to its original location
                        returnChildToOriginalSpot(child, x, y);

                        //_selectedColor = mixWithThisColor.getColor();
                        _selectedColor = child.getColor();
                        break;
                    } else if (!removeColor(child)) {

                        // If the paint is dragged on top of another paint mix them together to
                        // create a new color.
                        PaintView mixWithThisColor = mixPaint(child, x, y);
                        if (mixWithThisColor != null) {
                            addNewColor(child, mixWithThisColor);
                            child.makeOtherSplotchesInactive();
                        }

                        // Return the paint to its original location
                        returnChildToOriginalSpot(child, x, y);

                        _selectedColor = child.getColor();
                        //_selectedColor = mixWithThisColor.getColor();
                        break;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Animates the paint splotch so that it looks like the child is returning
     * to the spot where it was originally.
     *
     * @param child
     * @param initialX
     * @param initialY
     */
    private void returnChildToOriginalSpot(PaintView child, float initialX, float initialY) {
        try {
            // Otherwise return the paint to its original location
            ObjectAnimator animator = new ObjectAnimator();
            animator.setTarget(child);
            animator.setDuration(200);

            float centerOfChildX = child.getWidth() / 2;
            float centerOfChildY = child.getHeight() / 2;

            // Set the splotch back to its original place. Figure out how to
            //  move from the endpoint back to the original position.
            animator.setValues(
                    PropertyValuesHolder.ofFloat("x",
                            new float[]{initialX - child.getWidth() / 2, _centerPosOfSplotches.get(child).x - centerOfChildX}),
                    PropertyValuesHolder.ofFloat("y",
                            new float[]{initialY - child.getHeight() / 2, _centerPosOfSplotches.get(child).y - centerOfChildY})
            );
            animator.start();
        } catch (Exception e) {
        }
    }

    /**
     * @param SelectedChild
     * @param x             - x point of the position of the selected child.
     * @param y             - y point of the position of the selected child.
     * @return True - If the selected splotch is over another splotch so
     * the colors can be mixed.
     */
    private PaintView mixPaint(PaintView SelectedChild, float x, float y) {

        Iterator itr = _centerPosOfSplotches.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry pairs = (Map.Entry) itr.next();
            if (!pairs.getKey().equals(SelectedChild)) {

                float childCenterX = ((PointF) pairs.getValue()).x;
                float childCenterY = ((PointF) pairs.getValue()).y;
                float distance = (float) Math.sqrt(Math.pow(childCenterX - x, 2) + Math.pow(childCenterY - y, 2));
                if (distance < ((PaintView) pairs.getKey()).getRadius()) {
                    return (PaintView) pairs.getKey();
                }
            }
        }
        return null;
    }

    /**
     * Check if click is inside the circle, by measuring the distance
     * between the center of the circle and the radius of the circle.
     * -> If the point clicked is less than the radius of the circle
     * then it is a click.
     *
     * @param x
     * @param y
     * @return
     */
    private Boolean isCorrectDistance(float x, float y) {

        // The region bounded by the ellipse is given by the equation:
        // [(x - centerX)/(radiusX^2)] + [(y - centerY)/(radiusY^2) <= 1
        float radiusX = _paletteRect.width() / 2;
        float radiusY = _paletteRect.height() / 2;
        PointF circleCenter = new PointF(_paletteRect.centerX(), _paletteRect.centerY());

        float distance = (((float) Math.pow(x - circleCenter.x, 2)) / (radiusX * radiusX)) + (((float) Math.pow((y - circleCenter.y), 2)) / (radiusY * radiusY));

        if (distance <= 1) {
            Log.i("PaletteView", "Touched inside the palette ellipse");
            return true;
        }
        return false;

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        setBackground(new drawable());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpec = MeasureSpec.getSize(heightMeasureSpec);
            int width = Math.max(widthSpec, getSuggestedMinimumWidth());
            int height = Math.max(heightSpec, getSuggestedMinimumHeight());

            int childState = 0;
            for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {

                View child = getChildAt(childIndex);
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }

            setMeasuredDimension(resolveSizeAndState(width / 2, widthMeasureSpec, childState),
                    resolveSizeAndState(height / 2, heightMeasureSpec, childState));
        } else {
            int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpec = MeasureSpec.getSize(heightMeasureSpec);
            int width = Math.max(widthSpec, getSuggestedMinimumWidth());
            int height = Math.max(heightSpec, getSuggestedMinimumHeight());

            int childState = 0;
            for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {

                View child = getChildAt(childIndex);
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }

            setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, childState),
                    resolveSizeAndState(height, heightMeasureSpec, childState));
        }
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4) {

        int childWidthMax = 0;
        int childHeightMax = 0;
        _childrenNotGone = 0;
        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {

            PaintView child = (PaintView) getChildAt(childIndex);

            if (child.getVisibility() == GONE) {
                continue;
            }
            _children.add(child);
            childWidthMax = Math.max(childWidthMax, child.getMeasuredWidth());
            childHeightMax = Math.max(childHeightMax, child.getMeasuredHeight());
            _childrenNotGone++;
        }

        _layoutRect = new Rect();
        _layoutRect.left = getPaddingLeft() + 9 * childWidthMax / 10;
        _layoutRect.top = getPaddingTop() + 9 * childHeightMax / 10;
        _layoutRect.right = getWidth() - getPaddingRight() - 9 * childWidthMax / 10;
        _layoutRect.bottom = getHeight() - getPaddingBottom() - 9 * childHeightMax / 10;

        _centerPosOfSplotches.clear();
        boolean flag = false;
        int index;
        _childrenGone = 0;
        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {

            if (flag) {
                // Increase the subtracting amount as there is more children
                // that are gone.
                index = childIndex - _childrenGone;
            } else {
                index = childIndex;
            }
            double angle = (double) index / (double) _childrenNotGone * 2 * ((Math.PI));
            int childCenterX = (int) (_layoutRect.centerX() + _layoutRect.width() * 0.6 * Math.cos(angle));
            int childCenterY = (int) (_layoutRect.centerY() + _layoutRect.height() * 0.6 * Math.sin(angle));

            View child = getChildAt(childIndex);
            Rect childLayout = new Rect();

            if (child.getVisibility() == GONE) {
                childLayout.left = 0;
                childLayout.top = 0;
                childLayout.right = 0;
                childLayout.bottom = 0;
                flag = true;
                _childrenGone++;
                child.layout(childLayout.left, childLayout.top, childLayout.right, childLayout.bottom);
                continue;
            } else {
                _centerPosOfSplotches.put((PaintView) getChildAt(childIndex), new PointF(childCenterX, childCenterY));
                childLayout.left = childCenterX - childWidthMax / 2;
                childLayout.top = childCenterY - childHeightMax / 2;
                childLayout.right = childCenterX + childWidthMax / 2;
                childLayout.bottom = childCenterY + childHeightMax / 2;
            }
            child.layout(childLayout.left, childLayout.top, childLayout.right, childLayout.bottom);
            returnChildToOriginalSpot((PaintView) child, childCenterX, childCenterY);

        }
    }

    private class drawable extends Drawable {

        public void draw(Canvas canvas) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(0xAADC9D60);
            Path path = new Path();

            _paletteRect = new RectF();
            _paletteRect.left = getPaddingLeft();
            _paletteRect.top = getPaddingTop();
            _paletteRect.right = getWidth() - getPaddingRight();
            _paletteRect.bottom = getHeight() - getPaddingBottom();

            PointF circleCenter = new PointF(_paletteRect.centerX(), _paletteRect.centerY());
            float radius = _paletteRect.height() / 2;
            int points = 50;

            for (int circlePoint = 0; circlePoint < points; circlePoint++) {
                PointF point = new PointF();

                // x = centerX + r * cos(a)
                // y = centerY + r * cos(a)
                float twoPi = (float) (2 * Math.PI);
                point.x = (float) (circleCenter.x + (_paletteRect.width() / 2) * Math.cos(twoPi * ((double) circlePoint / (double) points)));
                point.y = (float) (circleCenter.y + (_paletteRect.height() / 2) * Math.sin(twoPi * ((double) circlePoint / (double) points)));

                if (circlePoint == 0) {
                    path.moveTo(point.x, point.y);
                } else {
                    path.lineTo(point.x, point.y);
                }
            }

            canvas.drawPath(path, paint);
        }

        @Override
        public void setAlpha(int i) {

        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return 0;
        }

    }


}
