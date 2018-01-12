package com.burhanrashid52.imageeditor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Burhanuddin Rashid on 1/12/2018.
 */

public class BarChart extends View {

    private Paint mGridPaint, mGuideLine, mBarPaint;
    private int mPadding = 4;
    private List<Float> mData = new ArrayList<>();

    public BarChart(Context context) {
        super(context);
        init();
    }

    private void init() {
        mGridPaint = new Paint();
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setColor(Color.BLACK);
        mGridPaint.setStrokeWidth(5);

        mGuideLine = new Paint();
        mGuideLine.setStyle(Paint.Style.STROKE);
        mGuideLine.setColor(Color.GRAY);
        mGuideLine.setStrokeWidth(2);

        mBarPaint = new Paint();
        mBarPaint.setStyle(Paint.Style.FILL);
        mBarPaint.setColor(Color.RED);
        //mGuideLine.setStrokeWidth(2);

        mData.add(0.1f);
        mData.add(0.4f);
        mData.add(0.3f);
        mData.add(0.65f);
        mData.add(0.90f);
        mData.add(0.55f);
        mData.add(0.25f);
    }

    public BarChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        int gridLeft = mPadding;
        int gridRight = width - mPadding;
        int gridBottom = height - mPadding;
        int gridTop = mPadding;

        //Draw Axis
        canvas.drawLine(gridLeft, gridBottom, gridLeft, gridTop, mGridPaint);
        canvas.drawLine(gridLeft, gridBottom, gridRight, gridBottom, mGridPaint);

        //Draw Guidelines
        float spacing = height / 10f;
        for (int i = 0; i < 10; i++) {
            float guideLineHeight = height - (i * spacing);
            canvas.drawLine(gridLeft, guideLineHeight, gridRight, guideLineHeight, mGuideLine);
        }

        // Draw Bars
        float barSpacing = 6;
        float totalColumnSpacing = barSpacing * (mData.size() + 1);
        float columnWidth = (gridRight - gridLeft - totalColumnSpacing) / mData.size();
        float columnLeft = gridLeft + barSpacing;
        float columnRight = columnLeft + columnWidth;

        for (int i = 0; i < mData.size(); i++) {
            // Calculate top of column based on percentage.
            float top = gridTop + height * (1f - mData.get(i));
            Log.e("Draw:", "Left : " + columnLeft + "\nRight : " + columnRight + "\nTop : " + top + "\nBottom : " + gridBottom);
            canvas.drawRect(columnLeft, top, columnRight, gridBottom, mBarPaint);

            // Shift over left/right column bounds
            columnLeft = columnRight + barSpacing;
            columnRight = columnLeft + columnWidth;
        }

       /* float colLeft = mGridLeft;
        float barWidth = mWidth - spacing * 10 - mPadding * 2;
        Log.e("Draw", "Col : " + colLeft + "\nWidth : " + barWidth);
        //Draw Bars
        for (int i = 0; i < mData.size(); i++) {
            Float percentage = mData.get(i);
            float barHeight = mHeight - ((mHeight * percentage) / 100);
            Log.e("Draw", "Height : " + barHeight);
            float colRight = colLeft + barHeight;
            Log.e("Draw", "Col Right: " + colRight);
            canvas.drawRect(colLeft, barHeight, colRight, mGridBottom, mBarPaint);
            colLeft += colRight;
            Log.e("Draw", "Col Left: " + colLeft);
        }*/

    }
}
