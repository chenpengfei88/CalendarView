package com.fe.calendarview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenpengfei on 2016/12/5.
 */
public class SpinnerView extends View {

    /**
     * 所有数据
     */
    private List<String> mAllDataList = new ArrayList<>();

    /**
     * 显示数据
     */
    private List<String> mVisibleDataList = new ArrayList<>();

    /**
     *  显示的数量
     */
    private int mVisibleItemCount = 5;

    /**
     *  单个item的高度
     */
    private int mItemHeight;

    /**
     * 是否惯性滑动
     */
    private boolean mFling;

    /**
     *  是否重置
     */
    private boolean mIsReset;

    /**
     *  字体大小
     */
    private int mTextSize;

    /**
     *  字大小rect
     */
    private Rect mRect = new Rect();

    /**
     *  base 线
     */
    private int mBaseLine;

    /**
     * 滑动的间距
     */
    private float mSlideOffset;

    /**
     * 初始化y坐标
     */
    private float mInitY;

    /**
     *  移动的y坐标
     */
    private float mMoveY;

    /**
     * 上一个滑动点的y轴
     */
    private int mUpScrollY;

    /**
     *  是否结束
     */
    private boolean mIsEnd;

    /**
     * 动画
     */
    private boolean mIsAnimation;

    /**
     *  padding top bottom
     */
    private int mPaddingTopBottom = 36;

    /**
     *  padding left right
     */
    private int mPaddingLeftRight = 60;

    /**
     *  文本颜色
     */
    private int mTextColor;

    /**
     *  选中文本颜色
     */
    private int mSelectedTextColor;

    /**
     * 选中线的颜色
     */
    private int mSelectedLineColor;

    /**
     *  选中线高度
     */
    private int mSelectedLineHeight = 2;

    /**
     * 文本
     */
    private Paint mTextPaint;

    /**
     * 选中文本
     */
    private Paint mSelectedTextPaint;

    /**
     * 选中线
     */
    private Paint mSelectedLinePaint;

    /**
     *  绘制data数量
     */
    private int mDataCount;

    /**
     *  选中文本的index
     */
    private int mSelectedTextIndex;

    /**
     *  绘制选中文本的index
     */
    private int mDrawSelectedTextIndex;

    /**
     *  单位
     */
    private String mUnit;

    /**
     *  当前选中的数据
     */
    private String mSelectedData;

    private VelocityTracker mVTracker;

    private Scroller mScroller;

    private OnDataSelectedListener mOnDataSelectedListener;

    public SpinnerView(Context context) {
        super(context);
        init(context);
    }

    public SpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SpinnerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SpinnerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) getLayoutParams();
        setMeasuredDimension(getViewWidth(lp, width), getViewHeight(lp, height));
        mItemHeight = getMeasuredHeight() / mVisibleItemCount;
    }

    private int getViewWidth(ViewGroup.LayoutParams lp, int pWidth) {
        int width = 0;
        if(lp.width >= 0) {
            width = lp.width;
        } else if(lp.width == ViewGroup.LayoutParams.WRAP_CONTENT){
            width = mRect.width() + mPaddingLeftRight * 2;
        } else if(lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            width = pWidth;
        }
        return width;
    }

    private int getViewHeight(ViewGroup.LayoutParams lp, int pHeight) {
        int height = 0;
        if(lp.height >= 0) {
            height = lp.height;
        } else if(lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            height = (mRect.height()  + mPaddingTopBottom * 2) * mVisibleItemCount;
        } else if(lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            height = pHeight;
        }
        return height;
    }

    public void setPaddingTopBottom(int paddingTopBottom) {
        mPaddingTopBottom = paddingTopBottom;
    }

    public void setOnDataSelectedListener(OnDataSelectedListener onDataSelectedListener) {
        mOnDataSelectedListener = onDataSelectedListener;
    }

    public void setUnit(String unit) {
        mUnit = unit;
    }

    private void init(Context context) {
        mTextPaint = new Paint();
        initPaintStyle(mTextPaint);

        mSelectedTextPaint = new Paint();
        initPaintStyle(mSelectedTextPaint);

        mSelectedLinePaint = new Paint();
        initPaintStyle(mSelectedLinePaint);

        mScroller = new Scroller(context);

        mDataCount = mVisibleItemCount + 2;
        mSelectedTextIndex = mDrawSelectedTextIndex = mDataCount / 2;
    }

    private void initPaintStyle(Paint paint) {
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
        mTextPaint.setTextSize(mTextSize);
        mSelectedTextPaint.setTextSize(mTextSize);
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        mTextPaint.setColor(mTextColor);
    }

    public void setSelectedTextColor(int selectedTextColor) {
        mSelectedTextColor = selectedTextColor;
        mSelectedTextPaint.setColor(mSelectedTextColor);
    }

    public void setSelectLineColor(int selectLineColor) {
        mSelectedLineColor = selectLineColor;
        mSelectedLinePaint.setColor(mSelectedLineColor);
    }

    private int getFontBaseLine() {
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        return (getMeasuredHeight()) / mVisibleItemCount / 2 + (fontMetrics.descent- fontMetrics.ascent) / 2 - fontMetrics.descent;
    }

    public void setAllDataList(List<String> dataList) {
        mAllDataList = dataList;
    }

    public void setCurrentData(String data) {
        if(mAllDataList.contains(data)) {
            mSelectedData = data;
            int centerIndex = mAllDataList.indexOf(data);
            for(int i = centerIndex, total = (mVisibleItemCount / 2) + 1; total >= 0; i++, total--) {
                if(i >= mAllDataList.size()) {
                    i = 0;
                }
                mVisibleDataList.add(mAllDataList.get(i));
            }

            for(int i = centerIndex - 1, total = (mVisibleItemCount / 2) + 1; total > 0; i--, total--) {
                if(i < 0) {
                    i = mAllDataList.size() - 1;
                }
                mVisibleDataList.add(0, mAllDataList.get(i));
            }
            if(mVisibleDataList.size() > 0) {
                String vdata = mVisibleDataList.get(0);
                mTextPaint.getTextBounds(vdata, 0, vdata.length(), mRect);
            }
        }
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mBaseLine == 0) {
            mBaseLine = getFontBaseLine();
        }
        if(mVisibleDataList.size() == 0) return;
        drawText(canvas);
        drawRect(canvas);
        resetData();
    }

    private void drawText(Canvas canvas) {
        //绘制文本
        for(int i = 0; i < mDataCount; i++) {
            String itemData = mVisibleDataList.get(i);
            canvas.drawText(itemData, (getWidth() - mRect.width()) / 2, mBaseLine + mItemHeight * (i - 1) + mSlideOffset, i == mDrawSelectedTextIndex ? mSelectedTextPaint : mTextPaint);
        }
    }

    private void drawRect(Canvas canvas) {
        //绘制横线
        int topLineLeft = mItemHeight * 2;
        int bottomLineLeft = mItemHeight * 3;
        canvas.drawRect(0, topLineLeft, getWidth(), topLineLeft + mSelectedLineHeight, mSelectedLinePaint);
        canvas.drawRect(0, bottomLineLeft, getWidth(), bottomLineLeft + mSelectedLineHeight, mSelectedLinePaint);
    }

    private void resetData() {
        if(mSlideOffset > 0) {
            //上面的临界点
            float upCriticalPoint =  mBaseLine - mItemHeight + mSlideOffset;
            if(upCriticalPoint >= mBaseLine) {
                int firstInAllIndex = mAllDataList.indexOf(mVisibleDataList.get(0));
                if(firstInAllIndex == 0) {
                    firstInAllIndex = mAllDataList.size() - 1;
                } else {
                    firstInAllIndex = firstInAllIndex - 1;
                }
                String firstUpData = mAllDataList.get(firstInAllIndex);
                mVisibleDataList.add(0, firstUpData);
                mVisibleDataList.remove(mVisibleDataList.size() - 1);
                mIsReset = true;
            }
        } else {
            float downCriticalPoint =  mBaseLine + mItemHeight * (mVisibleDataList.size() - 2) + mSlideOffset;
            if(downCriticalPoint <= mBaseLine + mItemHeight * 4) {
                int lastInAllIndex = mAllDataList.indexOf(mVisibleDataList.get(mVisibleDataList.size() - 1));
                if(lastInAllIndex == mAllDataList.size() - 1) {
                    lastInAllIndex = 0;
                } else {
                    lastInAllIndex = lastInAllIndex + 1;
                }
                String lastDownData = mAllDataList.get(lastInAllIndex);
                mVisibleDataList.add(mVisibleDataList.size(), lastDownData);
                mVisibleDataList.remove(0);
                mIsReset = true;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mIsReset) {
            mInitY = event.getY();
            mIsReset = false;
        }
        eventDeal(event);
        return true;
    }

    private void eventDeal(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitY = event.getY();
                mFling = false;
                mIsEnd = false;
                mIsAnimation = false;
                mUpScrollY = 0;
                if(mVTracker == null)
                    mVTracker = VelocityTracker.obtain();
                break;
            case MotionEvent.ACTION_MOVE:
                mVTracker.addMovement(event);
                mMoveY = event.getY();
                if(mMoveY == mInitY) return;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mVTracker.computeCurrentVelocity(1000);
                mFling = true;
                mScroller.fling(0, 0, 0, (int) mVTracker.getYVelocity() / 3, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
                invalidate();
                break;
        }
    }

    @Override
    public void computeScroll() {
        if(mIsAnimation) return;
        if(mFling) {
            if(mScroller.computeScrollOffset()) {
                int currY = mScroller.getCurrY();
                int offsetY = currY - mUpScrollY;
                mMoveY = mMoveY + offsetY;
                if(mIsReset) {
                    mIsReset = false;
                    mInitY = mMoveY;
                }
                mUpScrollY = currY;
                calculationOffset();
                invalidate();
            } else {
                if(!mIsEnd) {
                    mFling = false;
                    mIsEnd = true;
                    mIsAnimation = true;
                    if(Math.abs(mSlideOffset) <= mItemHeight / 2) {
                        selectedAnimation(mSlideOffset, 0, mSlideOffset > 0 ? true : false);
                    } else {
                        boolean isGreater = mSlideOffset > 0 ? true : false;
                        selectedAnimation(mSlideOffset, isGreater ? mItemHeight : -mItemHeight, isGreater);
                    }
                }
            }
            return;
        }
        calculationOffset();
    }

    private void calculationOffset() {
        if(mMoveY - mInitY > 0) {
            mSlideOffset = mMoveY - mInitY;
        } else {
            mSlideOffset = mMoveY - mInitY;
        }
    }

    private void selectedAnimation(final float start, final int end, final boolean isDown) {
        ValueAnimator vAnimator = ValueAnimator.ofFloat(0,1);
        vAnimator.setDuration(300);
        vAnimator.start();
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float lapse = (float) animation.getAnimatedValue();
                if(isDown) {
                    if(end == 0) {
                        mSlideOffset = start - start * lapse;
                        mDrawSelectedTextIndex = mSelectedTextIndex;
                    } else {
                        mSlideOffset = start + (end - start) * lapse;
                        mDrawSelectedTextIndex = mSelectedTextIndex - 1;
                    }
                } else {
                    if(end == 0) {
                        mSlideOffset = start + Math.abs(start) * lapse;
                        mDrawSelectedTextIndex = mSelectedTextIndex;
                    } else{
                        mSlideOffset = start - Math.abs(end - start) * lapse;
                        mDrawSelectedTextIndex = mSelectedTextIndex + 1;
                    }
                }
                if(lapse == 1 && mOnDataSelectedListener != null && mVisibleDataList != null && mVisibleDataList.size() > mDrawSelectedTextIndex && !TextUtils.isEmpty(mUnit)) {
                    mSelectedData = mVisibleDataList.get(mDrawSelectedTextIndex);
                    if(mSelectedData.contains(mUnit)) {
                        mOnDataSelectedListener.onSelected(Integer.valueOf(mSelectedData.replaceAll(mUnit, "")));
                    }
                }
                invalidate();
            }
        });
    }

    public interface OnDataSelectedListener {
        void onSelected(int data);
    }
}
