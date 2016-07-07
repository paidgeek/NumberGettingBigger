package com.moybl.topnumber;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;

public class Prefs {

	private static final String FILE_NAME = "prefs";
	private static JSONObject sValues;
	private static Context sContext;

	public static void save() {
		Log.d("PREFS", sValues.toString());

		try {
			OutputStream os = sContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
			os.write(sValues.toString()
					.getBytes());
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void remove(String key) {
		sValues.remove(key);
	}

	public static void removeAll() {
		sValues = new JSONObject();
	}

	public static void load(Context context) {
		sContext = context;

		try {
			InputStream is = sContext.openFileInput(FILE_NAME);
			byte[] buf = new byte[is.available()];
			is.read(buf);
			is.close();

			sValues = new JSONObject(new String(buf));
		} catch (Exception e) {
			e.printStackTrace();

			sValues = new JSONObject();
		}

		Log.d("PREFS", sValues.toString());
	}

	private static Object getObject(String key, Object defValue) {
		if (sValues.has(key)) {
			return sValues.opt(key);
		} else {
			return defValue;
		}
	}

	private static void put(String key, Object value) {
		try {
			sValues.putOpt(key, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getString(String key, String defValue) {
		return (String) getObject(key, defValue);
	}

	public static void setString(String key, String value) {
		put(key, value);
	}

	public static int getInt(String key, int defValue) {
		return (Integer) getObject(key, defValue);
	}

	public static void setInt(String key, int value) {
		put(key, value);
	}

	public static long getLong(String key, long defValue) {
		return (Long) getObject(key, defValue);
	}

	public static void setLong(String key, long value) {
		put(key, value);
	}

	public static double getDouble(String key, double defValue) {
		return (Double) getObject(key, defValue);
	}

	public static void setDouble(String key, double value) {
		put(key, value);
	}

	public static boolean getBoolean(String key, boolean defValue) {
		return (Boolean) getObject(key, defValue);
	}

	public static void setBoolean(String key, boolean value) {
		put(key, value);
	}

}
