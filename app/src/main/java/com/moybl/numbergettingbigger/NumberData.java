package com.moybl.numbergettingbigger;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class NumberData {

	private static final String KEY_SOURCE_LEVEL = "source_level";
	private static final String KEY_NUMBER = "number";

	private static NumberData sInstance;

	public synchronized static NumberData getInstance() {
		if (sInstance == null) {
			sInstance = new NumberData();
		}

		return sInstance;
	}

	private double mNumber;
	private List<Source> mSources;

	public double getNumber() {
		return mNumber;
	}

	public void setNumber(double number) {
		mNumber = number;
	}

	public List<Source> getSources() {
		return mSources;
	}

	public void setSources(List<Source> sources) {
		mSources = sources;
	}

	public void load(Activity activity) {
		SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);

		List<Source> sources = new ArrayList<>();

		for (int i = 0; i < Source.COUNT; i++) {
			int level = prefs.getInt(KEY_SOURCE_LEVEL + i, 0);

			sources.add(new Source(1, level));
		}

		mNumber = Double.parseDouble(prefs.getString(KEY_NUMBER, "0.0"));
	}

	public void save(Activity activity) {
		SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();

		for (int i = 0; i < Source.COUNT; i++) {
			Source source = mSources.get(i);

			editor.putInt(KEY_SOURCE_LEVEL + i, source.getLevel());
		}

		editor.putString(KEY_NUMBER, Double.toString(mNumber));

		editor.commit();
	}

}
