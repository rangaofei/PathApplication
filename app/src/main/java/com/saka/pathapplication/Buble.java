package com.saka.pathapplication;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;

/**
 * Created by Administrator on 2017/3/22 0022.
 */

public class Buble extends View {
    private Paint paint;
    private float moveX;
    private float moveY;
    private Path path = new Path();
    private float minR = 50;
    private float maxR = 100;
    private float brokeDistance = 400;
    private boolean canBroke;
    private ValueAnimator valueAnimatorX;
    private ValueAnimator valueAnimatorY;
    private ValueAnimator objectAnimator;
    private AnimatorSet set = new AnimatorSet();
    private boolean touchArea = false;
    private float tempX;
    private float tempY;

    public Buble(Context context) {
        super(context);
        init();
    }

    public Buble(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.moveX = getWidth() / 2;
        this.moveY = getHeight() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOriginalCircle(canvas);
        if (!canBroke) {
            drawMoveCircle(canvas);
            drawBCurve(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.canBroke = false;
                moveX = event.getX();
                moveY = event.getY();
                touchArea = !setCanBroke(moveX, moveY, maxR);
//                setPath(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (touchArea) {
                    moveX = event.getX();
                    moveY = event.getY();
                    if (setCanBroke(moveX, moveY, brokeDistance)) {
                        touchArea = false;
                        this.canBroke = true;
                    } else {
                        setPath(moveX, moveY);
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d("aaa", "actionUp" + touchArea);
                if (touchArea) {
//                    resetCircle(event.getX(), event.getY());
                    newresetCircle(event.getX(), event.getY());
                }
                break;
        }

        return true;
    }

    private void newresetCircle(float x,float y){
        objectAnimator=ValueAnimator.ofObject(new MyObjectEvaluator(),new PointF(x,y),
                new PointF((float)getWidth()/2,(float)getHeight()/2));
        objectAnimator.removeAllUpdateListeners();
        objectAnimator.setInterpolator(new BounceInterpolator());
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tempX=((PointF)animation.getAnimatedValue()).x;
                tempY=((PointF)animation.getAnimatedValue()).y;
                moveX=tempX;
                moveY=tempY;
                setPath(tempX,tempY);
                postInvalidate();
            }
        });
        objectAnimator.start();
    }

    private void resetCircle(float x, float y) {
        valueAnimatorX = ValueAnimator.ofFloat(x, (float) getWidth() / 2);
        valueAnimatorY = ValueAnimator.ofFloat(y, (float) getHeight() / 2);
        valueAnimatorX.removeAllUpdateListeners();
        valueAnimatorY.removeAllUpdateListeners();
        valueAnimatorX.setInterpolator(new BounceInterpolator());
        valueAnimatorY.setInterpolator(new BounceInterpolator());
        valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tempX = (float) animation.getAnimatedValue();
                moveX = tempX;
            }
        });
        valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tempY = (float) animation.getAnimatedValue();
                moveY = tempY;
                setPath(tempX, tempY);
                postInvalidate();
            }
        });
        set.playTogether(valueAnimatorX, valueAnimatorY);
        set.start();
    }

    private void drawOriginalCircle(Canvas canvas) {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, minR, paint);
    }

    private void drawMoveCircle(Canvas canvas) {
        canvas.drawCircle(moveX, moveY, maxR, paint);
    }

    private void drawBCurve(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
    }

    private boolean setCanBroke(float offsetX, float offsetY, float brokeDistance) {
        float minCircleX = (float) getWidth() / 2;
        float minCircleY = (float) getHeight() / 2;
        return (offsetX - minCircleX) * (offsetX - minCircleX) +
                (offsetY - minCircleY) * (offsetY - minCircleY) > brokeDistance * brokeDistance;
    }

    private void setPath(float offsetX, float offsetY) {
        float minCircleX = (float) getWidth() / 2;
        float minCircleY = (float) getHeight() / 2;
        double angle = Math.atan((offsetX - minCircleX) / (offsetY - minCircleY));
        float x1 = (float) (minCircleX + Math.cos(angle) * minR);
        float y1 = (float) (minCircleY - Math.sin(angle) * minR);
        float x2 = (float) (offsetX + Math.cos(angle) * maxR);
        float y2 = (float) (offsetY - Math.sin(angle) * maxR);
        float x3 = (float) (offsetX - Math.cos(angle) * maxR);
        float y3 = (float) (offsetY + Math.sin(angle) * maxR);
        float x4 = (float) (minCircleX - Math.cos(angle) * minR);
        float y4 = (float) (minCircleY + Math.sin(angle) * minR);
        float centerX = minCircleX + (offsetX - minCircleX) / 2;
        float centerY = minCircleY + (offsetY - minCircleY) / 2;
        path.reset();
        path.moveTo(minCircleX, minCircleY);
        path.lineTo(x1, y1);
        path.quadTo(centerX, centerY, x2, y2);
        path.lineTo(x3, y3);
        path.quadTo(centerX, centerY, x4, y4);
        path.lineTo(minCircleX, minCircleY);
        path.close();
    }

    public void setMinR(float minR) {
        this.minR = minR;
    }

    public void setMaxR(float maxR) {
        this.maxR = maxR;
    }

    public void setBrokeDistance(float distance) {
        this.brokeDistance = distance;
    }
}
