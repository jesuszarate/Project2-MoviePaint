package edu.utah.cs4962.project2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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
 * Created by jesuszarate on 10/3/14.
 */
public class PaletteActivity extends Activity {

    PaintAreaView _paintAreaView;
    ArrayList<Integer> _paintPaletteColors = new ArrayList<Integer>();
    Gson _gson = new Gson();
    PaletteView _paletteLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _paintPaletteColors.add(Color.RED);
        _paintPaletteColors.add(0xFFFFA500);
        _paintPaletteColors.add(Color.YELLOW);
        _paintPaletteColors.add(0xFF0000FF);
        _paintPaletteColors.add(0xFF00FF00);
        _paintPaletteColors.add(0xFF800080);

        //loadColorPalette(getFilesDir());

        _paintAreaView = new PaintAreaView(this);
        _paintAreaView.setBackgroundColor(Color.WHITE);

        _paletteLayout = new PaletteView(this);
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams paletteViewLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0, 90);
        paletteViewLP.gravity = Gravity.CENTER_HORIZONTAL;
        rootLayout.addView(_paletteLayout, paletteViewLP);

        // Back to Create Mode button.
        Button createModeButton = new Button(this);
        createModeButton.setText("Create Mode");
        rootLayout.addView(createModeButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 10));
        createModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Include the color the use picked so that it can also be updated in the
                // button preview of the Create Mode.
                Intent resultIntent = new Intent();
                resultIntent.putExtra(CreateModeActivity.BUTTON_COLOR, _paletteLayout.get_selectedColor());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        setContentView(rootLayout);

    }

    private void addPaintSplotches() {
        // Determine how many splotches we want on the palette
        for (int splotchIndex = 0; splotchIndex < _paintPaletteColors.size(); splotchIndex++) {

            PaintView paintView = new PaintView(this);

            if(_paintPaletteColors.get(splotchIndex) == _paletteLayout.get_selectedColor()){
                paintView.isActive = true;
            }
            _paletteLayout._colors.add(_paintPaletteColors.get(splotchIndex));
            paintView.setColor(_paintPaletteColors.get(splotchIndex));
//            if (splotchIndex == 0) {
//                paintView.setColor(Color.RED);
//            }
//            if (splotchIndex == 1) {
//                // Orange
//                paintView.setColor(0xFFFFA500);
//            }
//            if (splotchIndex == 2) {
//                paintView.setColor(Color.YELLOW);
//            }
//            if (splotchIndex == 3) {
//                // Blue
//                paintView.setColor(0xFF0000FF);
//            }
//            if (splotchIndex == 4) {
//                // Green
//                paintView.setColor(0xFF00FF00);
//            }
//            if (splotchIndex == 5) {
//                // Purple
//                paintView.setColor(0xFF800080);
//            }
            _paletteLayout.addView(paintView, new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));

            paintView.setOnSplotchTouchListener(new PaintView.OnSplotchTouchListener() {
                @Override
                public void onSplotchTouched(PaintView v) {
                    _paintAreaView.invalidate();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveColorPalette(getFilesDir());
        _paletteLayout._children.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadColorPalette(getFilesDir());
        addPaintSplotches();


    }

    public void saveColorPalette(File filesDir) {

        ColorPalette colorPalette = new ColorPalette();
        colorPalette.setSelectedColor(_paletteLayout.get_selectedColor());
        colorPalette.setPaletteColors(_paletteLayout._colors);

        String jsonColorPalette = _gson.toJson(colorPalette);

        try {
            File file = new File(filesDir, "colorPalette.txt");
            FileWriter textWriter = null;
            textWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(textWriter);

            // Write the paint points in json format.
            bufferedWriter.write(jsonColorPalette);
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadColorPalette(File filesDir) {
        try {
            File file = new File(filesDir, "colorPalette.txt");
            FileReader textReader = new FileReader(file);
            BufferedReader bufferedTextReader = new BufferedReader(textReader);
            String jsonColorPalette = null;
            jsonColorPalette = bufferedTextReader.readLine();

            Type colorPaletteType = new TypeToken<ColorPalette>(){}.getType();
            ColorPalette colorPalette = _gson.fromJson(jsonColorPalette, colorPaletteType);

            if (colorPalette.getPaletteColors() != null && colorPalette.getPaletteColors().size() > 0) {
                _paintPaletteColors = colorPalette.getPaletteColors();
            }
            //_paletteLayout.set_selectedColor(colorPalette.getSelectedColor());
            //_paletteLayout.invalidate();
            bufferedTextReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ColorPalette {
        int _selectedColor;
        ArrayList<Integer> _paletteColors = new ArrayList<Integer>();

        public int getSelectedColor() {
            return _selectedColor;
        }

        public void setSelectedColor(int selectedColor) {
            this._selectedColor = selectedColor;
        }

        public ArrayList<Integer> getPaletteColors() {
            return _paletteColors;
        }

        public void setPaletteColors(ArrayList<Integer> colors) {
//            ArrayList<Integer> colors = new ArrayList<Integer>();
//            for(View v :paletteColors){
//                try {
//                    colors.add(((PaintView) v).getColor());
//                }
//                catch (Exception e){
//                    continue;
//                }
//            }
            this._paletteColors = colors;
        }
    }
}
