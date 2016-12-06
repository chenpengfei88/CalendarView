package com.fe.calendarview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenpengfei on 2016/12/5.
 */
public class CalendarView extends LinearLayout {

    /**
     * 年
     */
    private SpinnerView mYearSpinnerView;

    /**
     * 月
     */
    private SpinnerView mMonthSpinnerView;

    /**
     * 日
     */
    private SpinnerView mDaySpinnerView;

    /**
     *  文本颜色
     */
    private int mTextColor;

    /**
     *  文本大小
     */
    private int mTextSize;

    /**
     * 选中文本的颜色
     */
    private int mSelectedTextColor;

    /**
     *  选中线的颜色
     */
    private int mSelectedLineColor;

    private OnDateSelectedListener mOnDateSelectedListener;

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        mOnDateSelectedListener = onDateSelectedListener;
    }


    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        initStyle(context, attrs);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 800);
        mYearSpinnerView = new SpinnerView(context);
        addViewData(mYearSpinnerView, lp, getYearList());

        mMonthSpinnerView = new SpinnerView(context);
        addViewData(mMonthSpinnerView, lp, getMonthList());

        mDaySpinnerView = new SpinnerView(context);
        addViewData(mDaySpinnerView, lp, getDayList(1));

        setListener();
    }

    private void initStyle(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarViewStyle);
        mTextColor = typedArray.getColor(R.styleable.CalendarViewStyle_textColor, Color.parseColor("#999999"));
        mSelectedTextColor = typedArray.getColor(R.styleable.CalendarViewStyle_selectedTextColor, Color.parseColor("#3F51B5"));
        mSelectedLineColor = typedArray.getColor(R.styleable.CalendarViewStyle_selectedLineColor, Color.parseColor("#D6D6D6"));
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.CalendarViewStyle_selectedTextColor, 60);
        typedArray.recycle();
    }

    private void setStyle(SpinnerView spinnerView) {
        spinnerView.setTextColor(mTextColor);
        spinnerView.setTextSize(mTextSize);
        spinnerView.setSelectedTextColor(mSelectedTextColor);
        spinnerView.setSelectLineColor(mSelectedLineColor);
        spinnerView.invalidate();
    }

    private void addViewData(SpinnerView spinnerView, LinearLayout.LayoutParams lp, List<String> dataList) {
        spinnerView.setLayoutParams(lp);
        addView(spinnerView);
        spinnerView.setAllDataList(dataList);

        setStyle(spinnerView);
    }

    private void setListener() {
        mYearSpinnerView.setOnDataSelectedListener(new SpinnerView.OnDataSelectedListener() {
            @Override
            public void onSelected(String data) {

            }
        });
        mMonthSpinnerView.setOnDataSelectedListener(new SpinnerView.OnDataSelectedListener() {
            @Override
            public void onSelected(String data) {

            }
        });
        mMonthSpinnerView.setOnDataSelectedListener(new SpinnerView.OnDataSelectedListener() {
            @Override
            public void onSelected(String data) {

            }
        });
    }

    public void setDate(String date) {
        String[] dateArray = date.split("-");
        int year = 0, month = 0, day = 0;
        switch (dateArray.length) {
            case 3:
                year = Integer.valueOf(dateArray[0]);
                month = Integer.valueOf(dateArray[1]);
                day = Integer.valueOf(dateArray[2]);
                break;
            case 2:
                year = Integer.valueOf(dateArray[0]);
                month = Integer.valueOf(dateArray[1]);
                 break;
            case 1:
                year = Integer.valueOf(dateArray[0]);
                break;
        }
        mYearSpinnerView.setCurrentData(year + "年");
        mMonthSpinnerView.setCurrentData((month > 9 ? month : "0" + month) + "月");
        mDaySpinnerView.setCurrentData((day > 9 ? day : "0" + day) + "日");
    }

    private List<String> getYearList() {
        List<String> dataList = new ArrayList<>();
        for(int i = 1970; i < 2035; i++) {
            dataList.add(i + "年");
        }
        return dataList;
    }

    private List<String> getMonthList() {
        List<String> monthList = new ArrayList<>();
        for(int i = 1; i <= 12; i++) {
            monthList.add((i > 9 ? i : "0" + i) + "月");
        }
        return monthList;
    }

    private List<String> getDayList(int month) {
        List<String> dayList = new ArrayList<>();
        for(int i = 1; i <= 30; i++) {
            dayList.add((i > 9 ? i : "0" + i) + "日");
        }
        return dayList;
    }

    public interface OnDateSelectedListener {
        void onDateSelected();
    }

}
