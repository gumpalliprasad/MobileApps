/*
 * *
 *  * Created by SriRamaMurthy A on 3/9/19 5:44 PM
 *  * Copyright (c) 2019 . All rights reserved.
 *  * Last modified 28/8/19 5:40 PM
 *
 */

package myschoolapp.com.gsnedutech.Util;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by tapanpatro on 13/12/17.
 */

public class MyCustomProgressBar extends View {

    public static final int default_background_stroke_width = 0x7f080046;
    public static final int default_stroke_width = 0x7f080047;

    // Properties
    private float progress = 0;
    private float progress_MAX = 0;
    private float strokeWidth = default_stroke_width;
    private float backgroundStrokeWidth = default_background_stroke_width;
    private int color = Color.WHITE;
    private int backgroundColor = Color.BLACK;

    // Object used to draw
    private int startAngle = 270;
    private RectF rectF;
    private Paint backgroundPaint;
    private Paint foregroundPaint;

    //region Constructor & Init Method
    public MyCustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();
        final int[] CircularProgressBar = {0x7f0100c1, 0x7f0100c2, 0x7f0100c3, 0x7f0100c4, 0x7f0100c5};
        final int CircularProgressBar_cpb_background_progressbar_color = 2;
        final int CircularProgressBar_cpb_background_progressbar_width = 4;
        final int CircularProgressBar_cpb_progress = 0;
        final int CircularProgressBar_cpb_progressbar_color = 1;
        final int CircularProgressBar_cpb_progressbar_width = 3;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, CircularProgressBar, 0, 0);
        //Reading values from the XML layout
        try {
            // Value
            progress = typedArray.getFloat(CircularProgressBar_cpb_progress, progress);
            // StrokeWidth
            strokeWidth = typedArray.getDimension(CircularProgressBar_cpb_progressbar_width, strokeWidth);
            backgroundStrokeWidth = typedArray.getDimension(CircularProgressBar_cpb_background_progressbar_width, backgroundStrokeWidth);
            // Color
            color = typedArray.getInt(CircularProgressBar_cpb_progressbar_color, color);
            backgroundColor = typedArray.getInt(CircularProgressBar_cpb_background_progressbar_color, backgroundColor);
        } finally {
            typedArray.recycle();
        }

        // Init Background
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(backgroundStrokeWidth);

        // Init Foreground
        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(color);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(strokeWidth);
    }
    //endregion

    //region Draw Method
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(rectF, backgroundPaint);
        float angle = -(360 * progress / progress_MAX);
        canvas.drawArc(rectF, startAngle, angle, false, foregroundPaint);
    }
    //endregion

    //region Mesure Method
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        float highStroke = (strokeWidth > backgroundStrokeWidth) ? strokeWidth : backgroundStrokeWidth;
        rectF.set(0 + highStroke / 2, 0 + highStroke / 2, min - highStroke / 2, min - highStroke / 2);
    }
    //endregion

    //region Method Get/Set
    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = (progress <= progress_MAX) ? progress : progress_MAX;
        invalidate();
    }

    private float getProgressMax() {
        return progress_MAX;
    }

    public void setProgressMax(float progress) {
        progress_MAX = progress;
    }

    public float getProgressBarWidth() {
        return strokeWidth;
    }

    public void setProgressBarWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        foregroundPaint.setStrokeWidth(strokeWidth);
        requestLayout();//Because it should recalculate its bounds
        invalidate();
    }

    public float getBackgroundProgressBarWidth() {
        return backgroundStrokeWidth;
    }

    public void setBackgroundProgressBarWidth(float backgroundStrokeWidth) {
        this.backgroundStrokeWidth = backgroundStrokeWidth;
        backgroundPaint.setStrokeWidth(backgroundStrokeWidth);
        requestLayout();//Because it should recalculate its bounds
        invalidate();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        foregroundPaint.setColor(color);
        invalidate();
        requestLayout();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        backgroundPaint.setColor(backgroundColor);
        invalidate();
        requestLayout();
    }
    //endregion

    public void setProgressWithAnimation(float progress) {
        setProgressWithAnimation(progress, 1500);
    }

    //    /**
//     * Set the progress with an animation.
//     * Note that the {@link ObjectAnimator} Class automatically set the progress
//     * so don't call the {@link CircularProgressBar#setProgress(float)} directly within this method.
//     *
//     * @param progress The progress it should animate to it.
//     * @param duration The length of the animation, in milliseconds.
//     */
    public void setProgressWithAnimation(float progress, int duration) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }
    //endregion


}
