package com.moybl.topnumber;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class NumberData {

	private static final String KEY_SOURCE_LEVEL = "source_level";
	private static final String KEY_NUMBER = "number";
	private static final String KEY_LAST_UPDATE_AT = "last_update_at";

	private static NumberData sInstance;

	public synchronized static NumberData getInstance() {
		if (sInstance == null) {
			sInstance = new NumberData();
		}

		return sInstance;
	}

	private double mNumber;
	private List<Source> mSources;
	private double mRate;
	private long mLastUpdateAt;

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

	public void exchange(Source source) {
		if (mNumber < source.getCost()) {
			return;
		}

		mNumber -= source.getCost();
		source.setLevel(source.getLevel() + 1);

		updateRate();
	}

	private void updateRate() {
		mRate = 0.0;

		for (int i = 0; i < Source.COUNT; i++) {
			mRate += mSources.get(i)
					.getRate();
		}
	}

	public double getRate() {
		return mRate;
	}

	public void load(Activity activity) {
		SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);

		mSources = new ArrayList<>();

		for (int i = 0; i < Source.COUNT; i++) {
			int level = prefs.getInt(KEY_SOURCE_LEVEL + i, i == 0 ? 1 : 0);

			mSources.add(new Source(i, level));
		}

		mNumber = Double.parseDouble(prefs.getString(KEY_NUMBER, "0.0"));

		mLastUpdateAt = prefs.getLong(KEY_LAST_UPDATE_AT, -1);
		if (mLastUpdateAt == -1) {
			mLastUpdateAt = System.currentTimeMillis();
			update();
		}

		updateRate();
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

	public void clear(Activity activity) {
		for (int i = 0; i < Source.COUNT; i++) {
			Source source = mSources.get(i);

			source.setLevel(0);
		}

		mNumber = 0.0;
		mLastUpdateAt = System.currentTimeMillis();
		updateRate();
		save(activity);
	}

	public void update() {
		long delta = System.currentTimeMillis() - mLastUpdateAt;
		mLastUpdateAt = System.currentTimeMillis();

		mNumber += mRate * (delta / 1000.0);
	}

}
