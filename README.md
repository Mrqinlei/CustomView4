# 加载控件------自定义属性学习

## 效果如下 : 
<img src="http://i.imgur.com/M7EIl85.gif" style="zoom:30%" align=left/>
每个球的颜色可以通过自定义属性来设置
github地址: [https://github.com/Mrqinlei/CustomView4](https://github.com/Mrqinlei/CustomView4 "github")
<!-- more -->

## 开始
为了练习自定义 View , 特地从花瓣网那里了找了些图, 准备一个一个练过去. 
原始图如下: 
![](http://i.imgur.com/4M5SC3S.gif)
## 分析
-	先看静态的就是画五个球, 每个球的颜色不同而已, 而且所有的球都在一个圆上. 
-	再看动态图主要就是三部分
	-	进入动画 
		```
		进入动画(小球到中心的半径加大)
		透明动画(颜色逐渐变深)
		旋转动画(小球旋转围绕中心点)
		(我这里可能跟原图有点出路,就不改了)
		```
	-	loading 动画 
		```
		旋转动画(小球旋转围绕中心点)
		起伏的动画(无限循环的让球到中心的半径起伏)
		```
	-	结束动画 
		```
		退出动画(小球到中心的半径减小)
		透明动画(颜色逐渐变浅)
		旋转动画(小球旋转围绕中心点)
		```
## 实现
### 先实现静态图
无非就是依次的旋转画布然后画圆的过程
```
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < colors.length; i++) {
            canvas.save();
            canvas.rotate(360 / colors.length * i, w / 2, h / 2);
            pointPaint.setColor(colors[i]);
            pointPaint.setAlpha(alpha);
            canvas.drawCircle(w / 2, h / 2 - radius, 20, pointPaint);
            canvas.restore();
        }
    }
```
### 动态图实现
#### 进入动画 
通过 ValueAnimator 改变 degrees radius, 然后重绘
```
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.rotate(degrees, w / 2, h / 2);//旋转画布
        for (int i = 0; i < colors.length; i++) {
            canvas.save();
            canvas.rotate(360 / colors.length * i, w / 2, h / 2);
            pointPaint.setColor(colors[i]);
            pointPaint.setAlpha(alpha);//设置画笔的透明度
            canvas.drawCircle(w / 2, h / 2 - radius, 20, pointPaint);
            canvas.restore();
        }
    }
```
ValueAnimator 定义如下:
```
    private void initStartAnimation() {
		//半径变大的动画
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
		//透明度的动画
        alphaStartAnimation = ValueAnimator.ofInt(0, 255);
        alphaStartAnimation.setDuration(unitTime / 2);
        alphaStartAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alpha = (int) animation.getAnimatedValue();
            }
        });
    }
```
#### loading 动画
```
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
                if (radius >= 99 && isEnd) {//当radius大于99 并且 isEnd 为 ture时结束动画 (为100时不知道为什么有问题)
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
```
#### 结束动画
```
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
```
### 两个对外的方法
开始loading
```
    public void start() {
        isEnd = false;
        scaleStartAnimation.start();
        alphaStartAnimation.start();
        rotateLoadingAnimation.start();
    }
```
结束loading
```
    public void end() {
        this.isEnd = true;
    }
```
### 自定义属性
#### 创建 attrs 文件并定义
```
    <declare-styleable name="FivePointLoadingView">
        <attr name="color1" format="color" />
        <attr name="color2" format="color" />
        <attr name="color3" format="color" />
        <attr name="color4" format="color" />
        <attr name="color5" format="color" />
    </declare-styleable>
``` 
#### 自定义 View 中使用
```
    <com.qinlei.customview4.FivePointLoadingView
        android:id="@+id/five"
        android:layout_width="200dp"
        android:layout_height="200dp"
        app:color1="@color/colorAccent"
        app:color2="@color/colorAccent"
        app:color3="@color/colorAccent"
        app:color4="@color/colorAccent"
        app:color5="@color/colorAccent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
```
#### 自定义 View 中获取数据
```
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
```

参考: [http://blog.csdn.net/lmj623565791/article/details/45022631](http://blog.csdn.net/lmj623565791/article/details/45022631)