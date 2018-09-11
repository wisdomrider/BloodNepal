package com.wisdomrider.bloodnepal.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.wisdomrider.R;

import java.util.Random;

import static android.view.Gravity.CENTER;

/*
CREated by avi(Wisdomrider)
on 9/1/2018
*/
public class Title extends android.support.v7.widget.AppCompatTextView {
    public Title(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTextColor(Color.WHITE);
        this.setTextSize(25);
        this.setHeight(40);
        this.setAllCaps(true);
        this.setGravity(CENTER);
        this.setBackgroundResource(getColor());
    }

    public int getColor() {
        int[] colors = {R.color.red_primary_dark, R.color.green_primary_dark, R.color.blue_primary_dark, R.color.violate
                , R.color.yellow_primary_dark, R.color.BlueViolet, R.color.Tomato, R.color.Turquoise
                , R.color.teal_primary_dark, R.color.orange_primary_dark, R.color.app_red, R.color.DeepPink};
        return colors[new Random().nextInt(colors.length - 1)];
    }
}
