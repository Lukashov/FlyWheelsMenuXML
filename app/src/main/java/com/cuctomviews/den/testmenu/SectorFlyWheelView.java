package com.cuctomviews.den.testmenu;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

;

/**
 * Created by Den on 12.10.15.
 */
public class SectorFlyWheelView extends View {

    // Used for SDK < 11
    private float mRotation = 0;
    private Matrix mTransform = new Matrix();
    private PointF mPivot = new PointF();
    private int mColor;

    private SectorFlyWheelModel mSectorFlyWheelModel;

    RectF mBounds;
    private List<SectorFlyWheelModel> mData = new ArrayList<SectorFlyWheelModel>();
    private Paint mPiePaint;

    private float mSweepAngle;
    private int mCount;

    private Drawable mDrawable;

    public SectorFlyWheelView(Context context) {
        super(context);
//        mData = data;
        init();
    }

    public SectorFlyWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        mData = data;
        init();
    }

    public SectorFlyWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        mData = data;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SectorFlyWheelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
//        mData = data;
        init();
    }


//    int [] icons = {R.drawable.bluetooth,R.drawable.call_transfer,R.drawable.callback, R.drawable.cellular_network,
//            R.drawable.end_call,R.drawable.high_connection, R.drawable.missed_call,R.drawable.mms};

//    public SectorFlyWheelView(Context context, List<SectorFlyWheelModel> data) {
//        super(context);
//        mData = data;
//        init();
//    }

    public void init(){
        mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSectorFlyWheelModel = new SectorFlyWheelModel();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius;

        if (width > height) {
            radius = height / 2;
        } else {
            radius = width / 2;
        }

        canvas.rotate(mCount * mSweepAngle + mRotation, mBounds.centerX(), mBounds.centerY());
        Log.d("CANVAS: ", "Width: " + width + " ,Height: " + height + " ,Rot: " + mRotation);

        float x = (float) (Math.cos(Math.toRadians(270-mSweepAngle/2))*(radius - 20));
        float y = (float) (Math.sin(Math.toRadians(270-mSweepAngle/2))*(radius - 20));

        mBounds.set(width / 2 - radius + 20, height / 2 - radius + 20, width / 2 + radius - 20, height / 2 + radius - 20);

        //Fill Sector
        mPiePaint.setColor(getResources().getColor(R.color.fillSector));
        mPiePaint.setAntiAlias(true);
        mPiePaint.setStyle(Paint.Style.FILL);
        canvas.drawArc(mBounds, 270 - mSweepAngle / 2, mSweepAngle, true, mPiePaint);
//        Log.d("CANVAS: ", "Width: " + width + " ,Height: " + height);

        //Line
        mPiePaint.setColor(getResources().getColor(R.color.strokeColor));
        mPiePaint.setStrokeWidth(5);
        canvas.drawLine(mBounds.centerX(), mBounds.centerY(), mBounds.centerX() + x, mBounds.centerY() + y, mPiePaint);
        canvas.drawLine(mBounds.centerX(), mBounds.centerY(), mBounds.centerX() - x, mBounds.centerY() + y, mPiePaint);

        //Icons
        mDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.ic_launcher);
        int sizeImage = (radius * 20) / 100;
        int positionLeft = (int) (mBounds.centerX() - sizeImage / 2);
        int positionTop = (int) (mBounds.centerY() - (radius * 2 / 3) - sizeImage / 2);
        mDrawable.setBounds(positionLeft, positionTop, positionLeft + sizeImage, positionTop + sizeImage);
        mDrawable.draw(canvas);

//        //Ring1
        mPiePaint.setColor(getResources().getColor(R.color.strokeColor));
        mPiePaint.setStrokeWidth(10);
        mPiePaint.setAntiAlias(true);
        mPiePaint.setStrokeCap(Paint.Cap.ROUND);
        mPiePaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(mBounds, 270 - mSweepAngle/2, mSweepAngle, false, mPiePaint);

        canvas.save();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBounds = new RectF(0, 0, w, h);
        Log.d("SIZE: ", "Width: " + w + " ,Height: " + h);

    }

    public void rotateTo(float pieRotation) {
        mRotation = pieRotation;
        if (Build.VERSION.SDK_INT >= 11) {
            setRotation(pieRotation);
        } else {
            invalidate();
        }
    }

    public void setPivot(float x, float y) {
        mPivot.x = x;
        mPivot.y = y;
        if (Build.VERSION.SDK_INT >= 11) {
            setPivotX(x);
            setPivotY(y);
        } else {
            invalidate();
        }
    }

    public float getSweepAngle() {
        return mSweepAngle;
    }

    public void setSweepAngle(float sweepAngle) {
        this.mSweepAngle = sweepAngle;
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    public RectF getBounds() {
        return mBounds;
    }
}

