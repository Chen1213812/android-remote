package com.example.pointbz;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 绘制贝塞尔曲线
 */
public class Bezier extends View {

    //曲线开始点
    private float startX, startY;
    //结束点
    private float endX, endY;
    //控制点
    private float contorlX = 200, contorlY = 60;//默认值
    private Paint paint;
    private Path path;

    public Bezier(Context context) {
        this(context, null);

        startX = 160;
        startY = 350;
        endX = 550;
        endY = 350;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);

        path = new Path();
    }

    public Bezier(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public Bezier(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, -1);
    }

    public Bezier(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();
        paint.setColor(Color.BLACK);
        path.moveTo(startX, startY);
        //二介曲线绘制方法
        path.quadTo(contorlX, contorlY, endX, endY);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            contorlX = event.getX();
            contorlY = event.getY();
            invalidate();
        }
        return true;
    }
}