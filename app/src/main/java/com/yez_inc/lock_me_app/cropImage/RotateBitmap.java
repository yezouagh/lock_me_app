package com.yez_inc.lock_me_app.cropImage;

import android.graphics.Bitmap;
import android.graphics.Matrix;

class RotateBitmap {
    private Bitmap mBitmap;
    private int    mRotation;
    RotateBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        mRotation = 0;
    }
    void setRotation(int rotation) {
        mRotation = rotation;
    }
    int getRotation() {
        return mRotation;
    }
    Bitmap getBitmap() {
        return mBitmap;
    }
    void setBitmap(Bitmap bitmap) {

        mBitmap = bitmap;
    }
    Matrix getRotateMatrix() {
        // By default this is an identity matrix.
        Matrix matrix = new Matrix();
        if (mRotation != 0) {
            // We want to do the rotation at origin, but since the bounding
            // rectangle will be changed after rotation, so the delta values
            // are based on old & new width/height respectively.
            int cx = mBitmap.getWidth() / 2;
            int cy = mBitmap.getHeight() / 2;
            matrix.preTranslate(-cx, -cy);
            matrix.postRotate(mRotation);
            matrix.postTranslate(getWidth() / 2, getHeight() / 2);
        }
        return matrix;
    }
    private boolean isOrientationChanged() {
        return (mRotation / 90) % 2 != 0;
    }
    public int getHeight() {
        if (isOrientationChanged()) {
            return mBitmap.getWidth();
        } else {
            return mBitmap.getHeight();
        }
    }
    public int getWidth() {
        if (isOrientationChanged()) {
            return mBitmap.getHeight();
        } else {
            return mBitmap.getWidth();
        }
    }
}

