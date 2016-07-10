package com.moybl.topnumber;

import android.content.Context;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class NumberUtil {

	private static DecimalFormat sDecimalFormat = new DecimalFormat("#.###");
	private static NumberFormat sNumberFormat = NumberFormat.getNumberInstance();
	private static Context sContext;
	private static String[] sNumberNames;

	public static void setContext(Context context) {
		sContext = context;

		sNumberNames = sContext.getResources()
				.getStringArray(R.array.numbers);
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
		if (Double.isNaN(x) || Double.isInfinite(x)) {
			return sContext.getString(R.string.infinity);
		}

		return sDecimalFormat.format(x);
	}

	public static String format(int x) {
		return sNumberFormat.format(x);
	}

	public static String formatNumber(double x) {
		double firstDigits = firstDigits(x);
		int power = powerOf(x);

		if (power >= 3) {
			return format(firstDigits) + " " + powerName(x);
		} else {
			return sNumberFormat.format(x);
		}
	}

	public static String formatNumberWithNewLine(double x) {
		double firstDigits = firstDigits(x);
		int power = powerOf(x);

		if (power >= 3) {
			return format(firstDigits) + "\n" + powerName(x);
		} else {
			return sNumberFormat.format(x);
		}
	}

	public static String powerName(double x) {
		int power = powerOf(x);

		if (power >= 3) {
			return sNumberNames[Math.min(power / 3 - 1, sNumberNames.length - 1)];
		}

		return "";
	}

	public static String formatFull(double x) {
		double first = firstDigits(x);

		return format(first) + " " + powerName(x);
	}

}
