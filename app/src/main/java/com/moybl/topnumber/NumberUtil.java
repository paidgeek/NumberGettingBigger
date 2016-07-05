package com.moybl.topnumber;

import android.content.Context;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberUtil {

	private static DecimalFormat df = new DecimalFormat("#.###");

	public static double prettyNumber(double x) {
		double log = Math.floor(Math.log10(x));
		double pow10 = Math.pow(10.0, log - 1);

		return Math.floor(Math.floor(x / pow10) * pow10);
	}

	public static int powerOf(double x) {
		return (int) Math.log10(x) / 3 * 3;
	}

	public static double firstDigits(double x) {
		int power = powerOf(x);

		if (power >= 3) {
			return x / Math.pow(10.0, power);
		}

		return x;
	}

	public static String format(double x) {
		return df.format(x);
	}

	public static String format(int x) {
		return NumberFormat.getNumberInstance()
				.format(x);
	}

	public static String powerName(Context context, double x) {
		String[] numbers = context.getResources()
				.getStringArray(R.array.numbers);
		int power = powerOf(x);

		if (power >= 3) {
			return numbers[power / 3 - 1];
		}

		return "";
	}

}
