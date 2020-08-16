package com.lscofield.nodeurlsresolver.widget;

import android.view.ScaleGestureDetector;

import com.lscofield.nodeurlsresolver.services.PinchListener;

public class CustomOnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    private PinchListener pinchListener;

    public CustomOnScaleGestureListener(PinchListener pinchListener){
        this.pinchListener = pinchListener;
    }
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        if (scaleFactor > 1)
            pinchListener.onZoomOut();
        else pinchListener.onZoomIn();
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }
}