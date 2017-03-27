package com.saka.pathapplication;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Created by saka on 17-3-27.
 */

public class MyObjectEvaluator implements TypeEvaluator {
    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        float startX=((PointF)startValue).x;
        float startY=((PointF)startValue).y;
        float endX=((PointF)endValue).x;
        float endY=((PointF)endValue).y;
        PointF tempPoint=new PointF(startX+fraction*(endX-startX),startY+fraction*(endY-startY));
        return tempPoint;
    }
}
