package com.moybl.topnumber;

import android.content.Context;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberUtil {

	private static DecimalFormat sDecimalFormat = new DecimalFormat("#.###");
	private static Context sContext;

	public static void setContext(Context context) {
		sContext = context;
	}

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
		return sDecimalFormat.format(x);
	}

	public static String format(int x) {
		return NumberFormat.getNumberInstance()
				.format(x);
	}

	public static String formatNumber(double x) {
		double firstDigits = firstDigits(x);
		int power = powerOf(x);

		if (power >= 3) {
			return sDecimalFormat.format(firstDigits) + " " + powerName(x);
		} else {
			return NumberFormat.getNumberInstance()
					.format(x);
		}
	}

	public static String formatNumberWithNewLine(double x) {
		double firstDigits = firstDigits(x);
		int power = powerOf(x);

		if (power >= 3) {
			return sDecimalFormat.format(firstDigits) + "\n" + powerName(x);
		} else {
			return NumberFormat.getNumberInstance()
					.format(x);
		}
	}

	public static String powerName(double x) {
		String[] numbers = sContext.getResources()
				.getStringArray(R.array.numbers);
		int power = powerOf(x);

		if (power >= 3) {
			return numbers[Math.min(power / 3 - 1, numbers.length - 1)];
		}

		return "";
	}

	public static String formatFull(double x) {
		double first = firstDigits(x);

		return format(first) + " " + powerName(x);
	}

}
