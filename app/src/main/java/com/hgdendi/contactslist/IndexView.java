package com.hgdendi.contactslist;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by fei
 * on 2017/1/5.
 * desc:
 */

public class IndexView extends View {

    private final ArrayList<String> DEFAULT_INDEX = new ArrayList<>();
    private final String TAG = IndexView.this.getClass().getSimpleName();

    private Paint mPaint;
    private int mTextSize = 16;

    private int mContentWidth;
    private int mContentHeight;
    private int mCenterX;
    private int mCenterY;
    private int indexHeight;

    private float measureText;

    private OnShowLabelListener mOnShowLabelListener;
    private Canvas mCanvas;
    private int mFirstHighlightPosition = -1;
    private int mLastHighlightPosition = -1;

    public IndexView(Context context) {
        this(context, null);
    }

    public IndexView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = getContext().obtainStyledAttributes(
                attrs, R.styleable.IndexView, defStyleAttr, 0);
        // 默认设置为16sp，TypeValue也可以把sp转化为px
        this.mTextSize = typedArray.getDimensionPixelSize(R.styleable.IndexView_textSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));

        typedArray.recycle();
        initAttr();
    }

    public void setOnShowLabelListener(OnShowLabelListener onShowLabelListener) {
        mOnShowLabelListener = onShowLabelListener;
    }

    private void initAttr() {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        paint.setColor(Color.WHITE);
        paint.setTextSize(mTextSize);
        paint.setTextAlign(Paint.Align.CENTER);

        this.mPaint = paint;

        Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        rectPaint.setColor(Color.YELLOW);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(1.0f);

        // this.mRectPaint = rectPaint;


        // paint.getTextBounds(String.valueOf(DEFAULT_INDEX.get(0)), 0, 1, bounds);

    }

    public void setData(ArrayList<String> labels) {
        ArrayList<String> cacheLabels = this.DEFAULT_INDEX;
        if (cacheLabels.size() > 0)
            cacheLabels.clear();
        cacheLabels.addAll(labels);

        //测量一个字符的宽度
        this.measureText = getSuggestedMinWidth();
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        //内容可显示的区域
        int contentWidth = w - paddingLeft - paddingRight;
        int contentHeight = h - paddingTop - paddingBottom;

        //可显示内容的中心位置
        this.mCenterX = contentWidth >> 1;
        this.mCenterY = contentHeight >> 1;

        this.mContentWidth = contentWidth;
        this.mContentHeight = contentHeight;

        //在内容可显示区域每一个index可以占有的高度
        indexHeight = contentHeight / DEFAULT_INDEX.size();

        // Log.e(TAG, "onSizeChanged: ----->" + indexHeight);

        // Log.e(TAG, "onSizeChanged: ----->w=" + w + "  h=" + h);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);

        int result = getSuggestedMinWidth();

        switch (mode) {
            case MeasureSpec.EXACTLY:
                //Log.e(TAG, "measureWidth: ----------->exactly");
                return size;
            case MeasureSpec.AT_MOST:
                return Math.min(size, result);
            case MeasureSpec.UNSPECIFIED:
                return result;
        }
        return result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        int result;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                // Log.e(TAG, "measureHeight: ------>exactly");
                return size;
            case MeasureSpec.AT_MOST:
                //一个字符所占的大小
                Paint.FontMetrics fm = getFontMetrics();
                float textHeight = fm.bottom - fm.top;
                // 自动测量的总长度
                result = (int) (DEFAULT_INDEX.size() * textHeight + 30);

                // Log.e(TAG, "measureHeight: ----->" + result + " textHeight= " + textHeight);
                return Math.min(result, size);
        }

        return 0;
    }

    private Paint.FontMetrics getFontMetrics() {
        Paint paint = this.mPaint;
        //获取字符的fontMetrics
        return paint.getFontMetrics();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.mCanvas = canvas;

        Paint paint = this.mPaint;

        int centerY = this.mCenterY;
        canvas.drawLine(0, centerY, getWidth(), centerY, paint);

        int centerX = this.mCenterX;

        canvas.drawLine(centerX, 0, centerX, getHeight(), paint);
        mPaint.setTextSize(mTextSize);

        //获取每一个字符的高
        float textHeight = this.indexHeight;

        //Rect bounds = this.TextBounds;
        int firstHighlightPosition = this.mFirstHighlightPosition;

        int lastHighlightPosition = this.mLastHighlightPosition;

        ArrayList<String> cacheLabels = this.DEFAULT_INDEX;
        for (int i = 0, len = cacheLabels.size(); i < len; i++) {

            String text = cacheLabels.get(i);

            if ((firstHighlightPosition != -1 && lastHighlightPosition != -1)
                    && i >= firstHighlightPosition && i <= lastHighlightPosition) {
                paint.setColor(Color.GREEN);
                Log.e(TAG, "onDraw: --->");
            } else {
                paint.setColor(Color.WHITE);
            }
            float baseline = (i + 1) * textHeight - getFontMetrics().bottom;
            canvas.drawText(text, centerX, baseline, paint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        OnShowLabelListener onShowLabelListener = this.mOnShowLabelListener;

        if (onShowLabelListener == null) return super.onTouchEvent(event);

        int actionMasked = event.getActionMasked();

        float eventX = event.getX();
        float eventY = event.getY();

        String label = containsLabel(eventX, eventY);

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                onShowLabelListener.showLabel(label);
                break;
            case MotionEvent.ACTION_MOVE:
                onShowLabelListener.showLabel(label);
                break;
            case MotionEvent.ACTION_UP:
                onShowLabelListener.hideLabel();
                break;
            default:
                break;
        }

        return true;
    }

    private String containsLabel(float upX, float upY) {

        //获取每一个字符的高
        float textHeight = this.indexHeight;

        int index = (int) Math.floor(upY / textHeight);

        //Log.e(TAG, "containsLabel: ------>" + "text= " + text + "  index=" + index + "  /= " + (upY / textHeight));

        return DEFAULT_INDEX.get(index);
    }

    public void isFirstHeightLightLabel(String label) {
        int index = -1;
        ArrayList<String> cacheLabels = this.DEFAULT_INDEX;
        for (int i = 0, len = cacheLabels.size(); i < len; i++) {
            String cacheLabel = cacheLabels.get(i);
            if (cacheLabel.equals(label)) {
                index = i;
                Log.e(TAG, "isLastHeightLightLabel: ---->index= " + index);
                break;
            }
        }

        if (index == -1) return;
        this.mFirstHighlightPosition = index;
        invalidate();
    }

    public void isLastHeightLightLabel(String label) {
        int index = -1;
        ArrayList<String> cacheLabels = this.DEFAULT_INDEX;
        for (int i = 0, len = cacheLabels.size(); i < len; i++) {
            String cacheLabel = cacheLabels.get(i);
            if (cacheLabel.equals(label)) {
                index = i;
                Log.e(TAG, "isLastHeightLightLabel: ---->index= " + index);
                break;
            }
        }

        if (index == -1) return;
        this.mLastHighlightPosition = index;
        invalidate();
    }


    /**
     * 推荐的一个string 的最小宽度
     *
     * @return string width
     */
    private int getSuggestedMinWidth() {
        String maxLengthTag = "";
        for (String tag : DEFAULT_INDEX) {
            if (maxLengthTag.length() < tag.length()) {
                maxLengthTag = tag;
            }
        }

        Paint textPaint = this.mPaint;
        return (int) (textPaint.measureText(maxLengthTag) + 0.5f) * 2;
    }


    public interface OnShowLabelListener {

        void showLabel(String label);

        void hideLabel();

    }


}
