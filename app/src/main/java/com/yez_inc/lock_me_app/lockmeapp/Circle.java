package com.yez_inc.lock_me_app.lockmeapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;

public class Circle extends View {
    private Paint mCircleYellow;
    private float mRadius;
    private RectF mArcBounds = new RectF();
    Float drawUpTo = 0f;
    Random random = new Random();
    public void setDrawUpTo(Float Pls_drawUpTo) {
        this.drawUpTo += Pls_drawUpTo;
        if (Pls_drawUpTo==0)
            drawUpTo = 0f;
        else
        if (drawUpTo>360f)
            drawUpTo = 360f;
        invalidate();
    }

    public Circle(Context context) {
        super(context);
    }

    public Circle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints();
    }

    public Circle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void initPaints() {
        mCircleYellow = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleYellow.setStyle(Paint.Style.STROKE);
        mCircleYellow.setStrokeWidth(4 * getResources().getDisplayMetrics().density);
        mCircleYellow.setStrokeCap(Paint.Cap.SQUARE);
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = Math.min(w, h) / 2f;
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float mouthInset = mRadius / 6f;
        mArcBounds.set(mouthInset, mouthInset, (mRadius * 2) - mouthInset, (mRadius * 2) - mouthInset);
        mCircleYellow.setColor(Color.rgb(random.nextInt(256),random.nextInt(256),random.nextInt(256)));
        canvas.drawArc(mArcBounds, 280f, drawUpTo, false, mCircleYellow);
    }
}
