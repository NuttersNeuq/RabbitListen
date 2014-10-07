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
				Color.parseColor("#" + alpha + "00bad2"));
		roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor,
				Color.parseColor("#" + alpha + "04a5ba"));
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
		int center = getWidth() / 2; // ��ȡԲ�ĵ�x����
		int radius = (int) (center - roundWidth / 2); // Բ���İ뾶
		paint.setColor(roundColor); // ����Բ������ɫ
		paint.setStyle(Paint.Style.STROKE); // ���ÿ���
		paint.setStrokeWidth(roundWidth); // ����Բ���Ŀ��
		paint.setAntiAlias(true); // �������
		canvas.drawCircle(center, center, radius, paint); // ����Բ��

		/**
		 * �����Ȱٷֱ�
		 */
		int baifenbiYOffSet = 9;
		int baifenbiTextSize = 95;
		paint.setStrokeWidth(0);
		paint.setColor(textColor);
		paint.setTextSize(baifenbiTextSize);
		paint.setTypeface(Typeface.DEFAULT_BOLD); // ��������
		float percent = ((float) progress / (float) max); // �м�Ľ��Ȱٷֱȣ���ת����float�ڽ��г������㣬��Ȼ��Ϊ0
		int tempo = (int) ((float) max * percent);
		float baidfenbiTextWidth = paint.measureText(tempo + ""); // ���������ȣ�������Ҫ��������Ŀ��������Բ���м�

		canvas.drawText(tempo + "", center - baidfenbiTextWidth / 2, center + baifenbiTextSize / 2 - baifenbiYOffSet,
				paint); // �������Ȱٷֱ�

		/**
		 * ����������������
		 */
		paint.setTextSize(27);
		paint.setTypeface(Typeface.DEFAULT_BOLD); // ��������
		canvas.drawText("�����", center - 85, center - 60, paint);

		/**
		 * ���� min
		 */

		paint.setTextSize(23);
		canvas.drawText("min", center + baidfenbiTextWidth / 2 + 5, center + 50 - baifenbiYOffSet, paint);

		/**
		 * ���� Ŀ�꣺40mins
		 */
		int muBiaoTextSize = 25;
		paint.setTextSize(muBiaoTextSize);
		String muBiaoString = "Ŀ�꣺" + max + "min";
		float muBiaoTextWidth = paint.measureText(muBiaoString);
		canvas.drawText(muBiaoString, center - muBiaoTextWidth / 2, center + baifenbiTextSize - muBiaoTextSize / 2,
				paint);

		/**
		 * ��Բ�� ����Բ���Ľ���
		 */

		// ���ý�����ʵ�Ļ��ǿ���
		paint.setStrokeWidth(roundWidth); // ����Բ���Ŀ��
		paint.setColor(roundProgressColor); // ���ý��ȵ���ɫ
		RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius); // ���ڶ����Բ������״�ʹ�С�Ľ���
		paint.setStyle(Paint.Style.STROKE);
		if (max > 0)
		{
			canvas.drawArc(oval, -90f, 360 * progress / max, false, paint); // ���ݽ��Ȼ�Բ��
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
	 * ���ý��ȵ����ֵ
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
	 * ��ȡ����.��Ҫͬ��
	 * 
	 * @return
	 */
	public synchronized int getProgress()
	{
		return progress;
	}

	/**
	 * ���ý��ȣ���Ϊ�̰߳�ȫ�ؼ������ڿ��Ƕ��ߵ����⣬��Ҫͬ�� ˢ�½������postInvalidate()���ڷ�UI�߳�ˢ��
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
