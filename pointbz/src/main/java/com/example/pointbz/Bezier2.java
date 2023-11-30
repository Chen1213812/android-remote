package com.example.pointbz;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * 绘制四阶或更高阶贝塞尔曲线，使用德卡斯特里奥算法（贝塞尔公式）实现
 */
public class Bezier2 extends View {

    Path path = new Path();
    //多控制点【第一个点和最后一个点则为开始与结束点】
    private ArrayList<PointF> controlPoints = null;
    private Paint paint, linePointPaint;

    public Bezier2(Context context){super(context);}
    public Bezier2(Context context,AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);

        linePointPaint = new Paint();
        linePointPaint.setAntiAlias(true);
        linePointPaint.setStrokeWidth(4);
        linePointPaint.setStyle(Paint.Style.STROKE);
        linePointPaint.setColor(Color.RED);

        controlPoints = new ArrayList<>();
        //init();
    }

    private void init() {
        Random random = new Random();
        for (int i = 0; i < 7; i++) {
            int x = random.nextInt(600) + 100;
            int y = random.nextInt(600) + 100;
            PointF pointF = new PointF(x, y);
            controlPoints.add(pointF);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {//设置触摸事件，手指按下进行记录，手指抬起停止记录
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                invalidate();
                PointF pointF = new PointF(event.getX(), event.getY());
                controlPoints.add(pointF);
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 控制点和控制点连线
        int size = controlPoints.size();
        if(size>=2)
        {
            PointF point;
            for (int i = 0; i < size; i++) {
                point = controlPoints.get(i);
                if (i > 0) {
                    // 控制点连线
                    canvas.drawLine(controlPoints.get(i - 1).x, controlPoints.get(i - 1).y, point.x, point.y,
                            linePointPaint);
                }
                // 控制点
                canvas.drawCircle(point.x, point.y, 12, linePointPaint);
            }
            buildBezierPoints();
            canvas.drawPath(path,paint);
        }
        if(size == 1)
        {
            canvas.drawCircle(controlPoints.get(0).x, controlPoints.get(0).y, 12, linePointPaint);
        }
    }

    private ArrayList<PointF> buildBezierPoints()
    {
        path.reset();
        ArrayList<PointF> points = new ArrayList<>();
        int order = controlPoints.size()-1;

        float dalta = 1.0f/1000;
        for(float t = 0; t <= 1; t+= dalta)
        {
            PointF pointF = new PointF(deCasteljauX(order,0,t),deCasteljauY(order,0,t));
            points.add(pointF);
            if(points.size()==1)
            {
                path.moveTo(points.get(0).x,points.get(0).y);
            }
            else
            {
                path.lineTo(pointF.x, pointF.y);
            }
        }
        return  points;
    }

    /**
     * deCasteljau算法
     * p(i,j) =  (1-t) * p(i-1,j)  +  u * p(i-1,j-1);
     *
     * @param i 阶数   4
     * @param j 控制点 3
     * @param t 时间
     * @return
     */
    private float deCasteljauX(int i, int j, float t) {
        if (i == 1) {
            return (1 - t) * controlPoints.get(j).x + t * controlPoints.get(j + 1).x;
        }
        return (1 - t) * deCasteljauX(i - 1, j, t) + t * deCasteljauX(i - 1, j + 1, t);
    }

    /**
     * deCasteljau算法
     *
     * @param i 阶数
     * @param j 点
     * @param t 时间
     * @return
     */
    private float deCasteljauY(int i, int j, float t) {
        if (i == 1) {
            return (1 - t) * controlPoints.get(j).y + t * controlPoints.get(j + 1).y;
        }
        return (1 - t) * deCasteljauY(i - 1, j, t) + t * deCasteljauY(i - 1, j + 1, t);
    }
}