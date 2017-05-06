package com.qinlei.customview4;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by ql on 2017/5/4.
 */

public class FivePointLoadingView extends View {
    private int w, h;

    private Paint pointPaint;
    private int[] colors;
    private int color1;
    private int color2;
    private int color3;
    private int color4;
    private int color5;

    private int alpha = 0;      //画笔透明度
    private int degrees;        //画布旋转角度
    private int radius;         //点到中心的半径
    private int unitTime = 400; //最小的动画时长

    private boolean isEnd;

    //进入动画
    private ValueAnimator scaleStartAnimation;
    private ValueAnimator alphaStartAnimation;

    //loading 动画
    private ValueAnimator scaleLoadingAnimation;
    private ValueAnimator rotateLoadingAnimation;

    //结束动画
    private ValueAnimator scaleEndAnimation;
    private ValueAnimator alphaEndAnimation;

    public FivePointLoadingView(Context context) {
        this(context, null);
    }

    public FivePointLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FivePointLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FivePointLoadingView);
        color1 = ta.getColor(R.styleable.FivePointLoadingView_color1,
                ContextCompat.getColor(getContext(), R.color.color1));
        color2 = ta.getColor(R.styleable.FivePointLoadingView_color2,
                ContextCompat.getColor(getContext(), R.color.color2));
        color3 = ta.getColor(R.styleable.FivePointLoadingView_color3,
                ContextCompat.getColor(getContext(), R.color.color3));
        color4 = ta.getColor(R.styleable.FivePointLoadingView_color4,
                ContextCompat.getColor(getContext(), R.color.color4));
        color5 = ta.getColor(R.styleable.FivePointLoadingView_color5,
                ContextCompat.getColor(getContext(), R.color.color5));
        ta.recycle();
        init();
    }

    private void init() {
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        colors = new int[]{
                color1,
                color2,
                color3,
                color4,
                color5};

        initStartAnimation();
        initLoadimgAnimation();
        initEndAnimation();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = measureHanlder(200, widthMeasureSpec);
        int h = measureHanlder(200, heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    private int measureHanlder(int defaultSize, int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.rotate(degrees, w / 2, h / 2);
        for (int i = 0; i < colors.length; i++) {
            canvas.save();
            canvas.rotate(360 / colors.length * i, w / 2, h / 2);
            pointPaint.setColor(colors[i]);
            pointPaint.setAlpha(alpha);
            canvas.drawCircle(w / 2, h / 2 - radius, 20, pointPaint);
            canvas.restore();
        }
    }

    /**
     * 取消所有动画
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        scaleLoadingAnimation.cancel();
        rotateLoadingAnimation.cancel();
        scaleStartAnimation.cancel();
        alphaStartAnimation.cancel();
        scaleEndAnimation.cancel();
        alphaEndAnimation.cancel();
    }

    /**
     * 初始化开始动画
     */
    private void initStartAnimation() {
        scaleStartAnimation = ValueAnimator.ofInt(0, 70);
        scaleStartAnimation.setDuration(unitTime / 2);
        scaleStartAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                radius = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        scaleStartAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                scaleLoadingAnimation.start();
            }
        });

        alphaStartAnimation = ValueAnimator.ofInt(0, 255);
        alphaStartAnimation.setDuration(unitTime / 2);
        alphaStartAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alpha = (int) animation.getAnimatedValue();
            }
        });
    }

    /**
     * 初始化 loading 动画
     */
    private void initLoadimgAnimation() {
        scaleLoadingAnimation = ValueAnimator.ofInt(70, 100);
        scaleLoadingAnimation.setRepeatCount(ValueAnimator.INFINITE);
        scaleLoadingAnimation.setRepeatMode(ValueAnimator.REVERSE);
        scaleLoadingAnimation.setDuration(unitTime);
        scaleLoadingAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                radius = (int) animation.getAnimatedValue();
                invalidate();
                if (radius >= 99 && isEnd) {
                    scaleLoadingAnimation.cancel();

                    scaleEndAnimation.start();
                    alphaEndAnimation.start();
                }
            }
        });
        rotateLoadingAnimation = ValueAnimator.ofInt(0, 359);
        rotateLoadingAnimation.setInterpolator(new LinearInterpolator());
        rotateLoadingAnimation.setRepeatCount(ValueAnimator.INFINITE);
        rotateLoadingAnimation.setDuration(unitTime * 2);
        rotateLoadingAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degrees = (int) animation.getAnimatedValue();
            }
        });
    }

    /**
     * 初始化结束动画
     */
    private void initEndAnimation() {
        scaleEndAnimation = ValueAnimator.ofInt(100, 0);
        scaleEndAnimation.setDuration(unitTime / 2);
        scaleEndAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                radius = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        scaleEndAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rotateLoadingAnimation.cancel();
            }
        });

        alphaEndAnimation = ValueAnimator.ofInt(255, 0);
        alphaEndAnimation.setDuration(unitTime / 2);
        alphaEndAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alpha = (int) animation.getAnimatedValue();
            }
        });
    }

    /**
     * 结束
     */
    public void end() {
        this.isEnd = true;
    }

    /**
     * 开始
     */
    public void start() {
        isEnd = false;
        scaleStartAnimation.start();
        alphaStartAnimation.start();
        rotateLoadingAnimation.start();
    }

}
