package edu.utah.cs4962.project2;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

/**
 * Created by jesuszarate on 10/2/14.
 */
public class MenuBarView extends ViewGroup {

    public MenuBarView(Context context) {
        super(context);
        SeekBar seekBar = new SeekBar(context);
        addView(seekBar, new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
        PaintView p = new PaintView(context);
        addView(p);
    }

    @Override
    protected void onLayout(boolean b, int i, int i2, int i3, int i4) {

    }
}
