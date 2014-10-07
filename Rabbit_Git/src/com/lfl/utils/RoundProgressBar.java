package com.lfl.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.nut.activity.R;

public class RoundProgressBar extends View
{

	private Paint paint;
	private int roundColor;
	private int roundProgressColor;
	private int textColor;
	private float textSize;
	private float roundWidth;
	private int max;
	private int progress;

	private String alpha = "80";

	public RoundProgressBar(Context context)
	{
		this(context, null);
	}

	public RoundProgressBar(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public RoundProgressBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		paint = new Paint();

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
		roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor,
				Color.parseColor("#" + alpha + "bc8f8f"));
		roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor,
				Color.parseColor("#" + alpha + "cd5c5c"));
		textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.WHITE);
		textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 15);
		roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 45);
		max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);

		mTypedArray.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		int center = getWidth() / 2; // 获取圆心的x坐标
		int radius = (int) (center - roundWidth / 2); // 圆环的半径
		paint.setColor(roundColor); // 设置圆环的颜色
		paint.setStyle(Paint.Style.STROKE); // 设置空心
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setAntiAlias(true); // 消除锯齿
		canvas.drawCircle(center, center, radius, paint); // 画出圆环

		/**
		 * 画进度百分比
		 */
		int baifenbiYOffSet = 9;
		int baifenbiTextSize = 95;
		paint.setStrokeWidth(0);
		paint.setColor(textColor);
		paint.setTextSize(baifenbiTextSize);
		paint.setTypeface(Typeface.DEFAULT_BOLD); // 设置字体
		float percent = ((float) progress / (float) max); // 中间的进度百分比，先转换成float在进行除法运算，不然都为0
		int tempo = (int) ((float) max * percent);
		float baidfenbiTextWidth = paint.measureText(tempo + ""); // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间

		canvas.drawText(tempo + "", center - baidfenbiTextWidth / 2, center + baifenbiTextSize / 2 - baifenbiYOffSet,
				paint); // 画出进度百分比

		/**
		 * 画出“今日听力”
		 */
		paint.setTextSize(27);
		paint.setTypeface(Typeface.DEFAULT_BOLD); // 设置字体
		canvas.drawText("已完成", center - 85, center - 60, paint);

		/**
		 * 画出 min
		 */

		paint.setTextSize(23);
		canvas.drawText("min", center + baidfenbiTextWidth / 2 + 5, center + 50 - baifenbiYOffSet, paint);

		/**
		 * 画出 目标：40mins
		 */
		int muBiaoTextSize = 25;
		paint.setTextSize(muBiaoTextSize);
		String muBiaoString = "目标：" + max + "min";
		float muBiaoTextWidth = paint.measureText(muBiaoString);
		canvas.drawText(muBiaoString, center - muBiaoTextWidth / 2, center + baifenbiTextSize - muBiaoTextSize / 2,
				paint);

		/**
		 * 画圆弧 ，画圆环的进度
		 */

		// 设置进度是实心还是空心
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setColor(roundProgressColor); // 设置进度的颜色
		RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius); // 用于定义的圆弧的形状和大小的界限
		paint.setStyle(Paint.Style.STROKE);
		if (max > 0)
		{
			canvas.drawArc(oval, -90f, 360 * progress / max, false, paint); // 根据进度画圆弧
		}
		else
		{
			canvas.drawArc(oval, -90f, 0, false, paint);
		}

	}
	

	public synchronized int getMax()
	{
		return max;
	}

	/**
	 * 设置进度的最大值
	 * 
	 * @param max
	 */
	public synchronized void setMax(int max)
	{
		if (max < 0)
		{
			throw new IllegalArgumentException("max not less than 0");
		}
		this.max = max;
	}

	/**
	 * 获取进度.需要同步
	 * 
	 * @return
	 */
	public synchronized int getProgress()
	{
		return progress;
	}

	/**
	 * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
	 * 
	 * @param progress
	 */
	public synchronized void setProgress(int progress)
	{
		if (progress < 0)
		{
			throw new IllegalArgumentException("progress not less than 0");
		}
		if (progress > max)
		{
			progress = max;
		}
		if (progress <= max)
		{
			this.progress = progress;
			postInvalidate();
		}

	}

	public int getCricleColor()
	{
		return roundColor;
	}

	public void setCricleColor(int cricleColor)
	{
		this.roundColor = cricleColor;
	}

	public int getCricleProgressColor()
	{
		return roundProgressColor;
	}

	public void setCricleProgressColor(int cricleProgressColor)
	{
		this.roundProgressColor = cricleProgressColor;
	}

	public int getTextColor()
	{
		return textColor;
	}

	public void setTextColor(int textColor)
	{
		this.textColor = textColor;
	}

	public float getTextSize()
	{
		return textSize;
	}

	public void setTextSize(float textSize)
	{
		this.textSize = textSize;
	}

	public float getRoundWidth()
	{
		return roundWidth;
	}

	public void setRoundWidth(float roundWidth)
	{
		this.roundWidth = roundWidth;
	}
}
