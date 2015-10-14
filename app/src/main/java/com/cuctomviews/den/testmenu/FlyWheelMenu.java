package com.cuctomviews.den.testmenu;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

public class FlyWheelMenu extends ViewGroup {

    private List<SectorFlyWheelModel> mData = new ArrayList<SectorFlyWheelModel>();

    private float mTotal = 0.0f;

    private float mHighlightStrength = 1.15f;

    private int mPieRotation;

    private boolean mAutoCenterInSlice;

    private SectorFlyWheelModel mSectorFlyWheelModel;

    RectF mBounds;
    private Paint mPiePaint;

    private SectorFlyWheelView mSectorFlyWheelView;
    private Scroller mScroller;
    private ValueAnimator mScrollAnimator;
    private GestureDetector mDetector;
    private ObjectAnimator mAutoCenterAnimator;
    private RectF mPieBounds = new RectF();
    private float mDiameterMax;

    private int mWidth;
    private int mHeight;
    private int mChildCount;
    private float mSweepAngle;

    public FlyWheelMenu(Context context) {
        super(context);
        init();
    }

    public FlyWheelMenu(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FlyWheelMenu,
                0, 0
        );

        try {
            mHighlightStrength = a.getFloat(R.styleable.FlyWheelMenu_highlightStrength, 1.0f);
            mPieRotation = a.getInt(R.styleable.FlyWheelMenu_pieRotation, 0);
            mAutoCenterInSlice = a.getBoolean(R.styleable.FlyWheelMenu_autoCenterPointerInSlice, false);
        } finally {
            a.recycle();
        }

        init();
    }

    public int getPieRotation() {
        return mPieRotation;
    }

    public void setPieRotation(int rotation) {
        rotation = (rotation % 360 + 360) % 360;
        mPieRotation = rotation;

        for (int i = 0; i < mChildCount; i++) {
            mSectorFlyWheelView = (SectorFlyWheelView) getChildAt(i);
            mSectorFlyWheelView.rotateTo(rotation);
        }
    }

    public int addItem(float value, int sliceColor, int strokeColor) {
        mSectorFlyWheelModel = new SectorFlyWheelModel();
        mSectorFlyWheelModel.mSliceColor = sliceColor;
        mSectorFlyWheelModel.mStrokeColor = strokeColor;

        mSectorFlyWheelModel.mValue = value;

        mTotal += value;

        mData.add(mSectorFlyWheelModel);


        return mData.size() - 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 300;
        int desiredHeight = 300;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        mHeight = height;
        mWidth = width;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mChildCount = getChildCount();

        float sweepAngle = 360f/mChildCount;

        int j = 0;
        for (int i = 0; i < mChildCount; i++) {
            mSectorFlyWheelView = (SectorFlyWheelView) getChildAt(i);
            mSectorFlyWheelView.setSweepAngle(sweepAngle);

            mSectorFlyWheelView.setCount(i+1);

            mSectorFlyWheelView.layout(l, t, r, b);
        }
    }


    private void onDataChanged() {
        float currentAngle = 0;
        for (SectorFlyWheelModel it : mData) {
            it.mStartAngle = currentAngle;
            it.mEndAngle = (currentAngle + it.mValue * 360.0f / mTotal);
            currentAngle = it.mEndAngle;
        }
    }

    private void init() {
        setWillNotDraw(false);
        mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBounds = new RectF(0, 0, mWidth, mHeight);

        if (Build.VERSION.SDK_INT >= 11) {
            mAutoCenterAnimator = ObjectAnimator.ofInt(FlyWheelMenu.this, "PieRotation", 0);

            mAutoCenterAnimator.addListener(new Animator.AnimatorListener() {
                public void onAnimationStart(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                }

                public void onAnimationCancel(Animator animator) {
                }

                public void onAnimationRepeat(Animator animator) {
                }
            });
        }

        if (Build.VERSION.SDK_INT < 11) {
            mScroller = new Scroller(getContext());
        } else {
            mScroller = new Scroller(getContext(), null, true);
        }
        if (Build.VERSION.SDK_INT >= 11) {
            mScrollAnimator = ValueAnimator.ofFloat(0, 1);
            mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    tickScrollAnimation();
                }
            });
        }

        mDetector = new GestureDetector(getContext(), new GestureListener());
        mDetector.setIsLongpressEnabled(false);

    }
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        int width = mWidth;
//        int height = mHeight;
//        int radius;
//
//        if (mWidth > height) {
//            radius = height / 2;
//        } else {
//            radius = width / 2;
//        }
//
//        mBounds.set(width / 2 - radius + 20, height / 2 - radius + 20, width / 2 + radius - 20, height / 2 + radius - 20);
//
//        //Ring1
//        mPiePaint.setColor(getResources().getColor(R.color.strokeColor));
//        mPiePaint.setStrokeWidth(10);
//        mPiePaint.setAntiAlias(true);
//        mPiePaint.setStrokeCap(Paint.Cap.ROUND);
//        mPiePaint.setStyle(Paint.Style.STROKE);
//        canvas.drawArc(mBounds, 0, 360, false, mPiePaint);
//
//        canvas.save();
//    }


    private void setLayerToSW(View v) {
        if (!v.isInEditMode() && Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(pointInCircle(event)) {

            boolean result = mDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
            }
            if (!result) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    result = true;
                }
            }
            return result;
        }
        return super.onTouchEvent(event);
    }

    public double getAngle(float pointX, float pointY){

        double angle = Math.toDegrees(Math.atan2(mPieBounds.centerY() - pointY, mPieBounds.centerX() - pointX)) +90;

        Log.d("ANGLE", " angle touch: " + angle + " ,mPieRotate: " + mPieRotation);

        if (angle < 0) {
            angle += 360;
        }
        return 360 - angle;
    }

    private void tickScrollAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.computeScrollOffset();
            setPieRotation(mScroller.getCurrY());
        } else {
            if (Build.VERSION.SDK_INT >= 11) {
                mScrollAnimator.cancel();
            }
        }
    }

    public void rotateTo(float pieRotation) {
//        mRotation = pieRotation;
        if (Build.VERSION.SDK_INT >= 11) {
            setRotation(pieRotation);
        } else {
            invalidate();
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

//            Get pressed item and his angle
            float angle = (float) getAngle(e.getY(),e.getX()) ;

            float an = mPieRotation - (90-360f/mData.size()) - 360f/mData.size()/2;

            if (an<0) an += 360;

            for (int i = mData.size() -1 ; i >=0  ; i --) {
                an = an + (360f/mData.size());

                if (an > 360) {an = an - 360;}
                if ((an-(360f/mData.size())) > 0) {

                    if ((angle <= an) && (angle > (an - (360f / mData.size())))) {

                        moveToCenterCurrentItem(an);
                        setPressedColor(i);
                    }
                } else{
                    if ((angle < 360f) && (angle > (360 + an - 360f / mData.size()))) {

                        moveToCenterCurrentItem(an);
                        setPressedColor(i);

                    }else if (angle<=360 && angle > (360-(360f/mData.size()-an)) || angle <= an && an >= 0){

                        moveToCenterCurrentItem(an);
                        setPressedColor(i);
                    }
                }
            }

            return true;
        }

        public void moveToCenterCurrentItem(float angle){

            float targetAngle;

            Log.d("ROTATE", " angle touch: " + angle + " ,mPieRotate: " + mPieRotation);

            if (angle < (360 / mData.size() / 2)){
                targetAngle = 360 + (angle - 360 / mData.size() / 2);
            } else{
                targetAngle = angle - 360 / mData.size() / 2;
            }

            if (targetAngle <= 270 && targetAngle > 90
                    || targetAngle > 270 && targetAngle <= 360) {
                targetAngle = mPieRotation + (270 - targetAngle);

            } else if(targetAngle <= 90 && targetAngle >= 0 ){
                targetAngle = mPieRotation - (targetAngle + 90);
            }

            if (Build.VERSION.SDK_INT >= 11) {
                mAutoCenterAnimator.setIntValues((int) targetAngle);
                mAutoCenterAnimator.setDuration(250).start();
            } else {
                mSectorFlyWheelView.rotateTo(targetAngle);
            }

            invalidate();
        }

        public void setPressedColor(int i){
            SectorFlyWheelModel current;
            SectorFlyWheelModel it;

            Log.d("COLOR: ", " Item: " + i);

            for(int j = 0; j < mData.size(); j++){
                if (j == i){
                    current = mData.get(j);
                    current.mSliceColor = getResources().getColor(R.color.pressedSectorColor);
                    Log.d("COLOR1: ", " Item: " + (j));

                    if((j-1) < 0){
                        it = mData.get(mData.size() - 2);
                        current = mData.get(mData.size() - 1);
                        Log.d("COLOR2: ", " Item: " + (mData.size() - 1));

                    }else {
                        Log.d("COLORIN: ", " Item: " + (j));

                        current = mData.get(j-1);
                        Log.d("COLOR3: ", " Item: " + (j - 1));

                        if (j-2 < 0){
                            it = mData.get(mData.size()-1);
                            Log.d("COLOR4: ", " Item: " + (mData.size() - 1));

                        } else {
                            it = mData.get(j-2);
                            Log.d("COLOR5: ", " Item: " + (mData.size() - 2));

                        }
                    }
                    current.mStrokeColor = getResources().getColor(R.color.pressedStrokeColor);
                    it.mStrokeColor = getResources().getColor(R.color.pressedStrokeColor);
                }
                else {
                    current = mData.get(j);
                    current.mSliceColor = getResources().getColor(R.color.fillSector);

                    if((j-1)<0){
                        current = mData.get(mData.size()-1);
                        Log.d("COLOR6: ", " Item: " + (mData.size()-1));

                    }else {
                        current = mData.get(j-1);
                        Log.d("COLOR7: ", " Item: " + (j));
                    }
                    current.mStrokeColor = getResources().getColor(R.color.strokeColor);
                }
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float scrollTheta = vectorToScalarScroll(
                    distanceX,
                    distanceY,
                    e2.getX() - mWidth/2,
                    e2.getY() - mHeight/2);
            setPieRotation(getPieRotation() - (int) scrollTheta / 4);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float scrollTheta = vectorToScalarScroll(
                    velocityX,
                    velocityY,
                    e2.getX() - mWidth/2,
                    e2.getY() - mHeight/2);
            mScroller.fling(
                    0,
                    (int) getPieRotation(),
                    0,
                    (int) scrollTheta / 4,
                    0,
                    0,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE);

            if (Build.VERSION.SDK_INT >= 11) {
                mScrollAnimator.setDuration(mScroller.getDuration());
                mScrollAnimator.start();
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    public boolean pointInCircle(MotionEvent e){
        if(Math.sqrt(Math.pow((e.getX() - mSectorFlyWheelView.getBounds().centerX()), 2) +
                Math.pow((e.getY() - mSectorFlyWheelView.getBounds().centerY()), 2))
                <= Math.min(mWidth,mHeight)/2){
            return true;
        }
        return false;
    }

    private static float vectorToScalarScroll(float dx, float dy, float x, float y) {
        float l = (float) Math.sqrt(dx * dx + dy * dy);

        float crossX = -y;
        float crossY = x;

        float dot = (crossX * dx + crossY * dy);
        float sign = Math.signum(dot);

        return l * sign;
    }
}


