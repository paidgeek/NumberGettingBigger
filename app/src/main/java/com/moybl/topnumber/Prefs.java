package com.moybl.topnumber;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

	private static final String PREFERENCES_NAME = "prefs";

	private static SharedPreferences sSharedPreferences;
	private static SharedPreferences.Editor sEditor;

	public static void save() {
		sEditor.commit();
	}

	public static void remove(String key) {
		sEditor.remove(key);
	}

	public static void removeAll() {
		sEditor.clear();
	}

	public static String getString(String key, String defValue) {
		return sSharedPreferences.getString(key, defValue);
	}

	public static void setString(String key, String value) {
		sEditor.putString(key, value);
	}

	public static int getInt(String key, int defValue) {
		return sSharedPreferences.getInt(key, defValue);
	}

	public static void setInt(String key, int value) {
		sEditor.putInt(key, value);
	}

	public static long getLong(String key, long defValue) {
		return sSharedPreferences.getLong(key, defValue);
	}

	public static void setLong(String key, long value) {
		sEditor.putLong(key, value);
	}

	public static double getDouble(String key, double defValue) {
		if (sSharedPreferences.contains(key)) {
			return Double.parseDouble(sSharedPreferences.getString(key, defValue + ""));
		}

		return defValue;
	}

	public static void setDouble(String key, double value) {
		sEditor.putString(key, value + "");
	}

	public static boolean getBoolean(String key, boolean defValue) {
		return sSharedPreferences.getBoolean(key, defValue);
	}

	public static void setBoolean(String key, boolean value) {
		sEditor.putBoolean(key, value);
	}

	public static void load(Context context) {
		sSharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		sEditor = sSharedPreferences.edit();
	}

}
