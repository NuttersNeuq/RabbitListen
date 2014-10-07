package com.lfl.utils;

import java.util.Stack;

public class MillisecondConvert
{
	public static String convert(int millisecond)
	{
		int A = 0;
		int B = 0;
		String time = null;
		Stack<Integer> stack = new Stack<Integer>();
		int temp = millisecond / 1000;
		while (true)
		{
			if (temp < 60)
			{
				stack.push(temp);
				break;
			} else
			{
				stack.push(temp % 60);
				temp = temp / 60;
			}
		}
		if (stack.isEmpty() == false)
		{
			A = stack.pop();
			if (stack.isEmpty() == true)
			{
				if (A < 10)
				{
					time = "00:" + "0" + A;
				} else
				{
					time = "00:" + A;
				}
			} else
			{
				B = stack.pop();
				String second;
				if (B < 10)
				{
					second = "0" + B;
				} else
				{
					second = B + "";
				}
				if (A < 10)
				{
					time = "0" + A + ":" + second;
				} else
				{
					time = A + ":" + second;
				}
			}
		}
		return time;
	}
}
