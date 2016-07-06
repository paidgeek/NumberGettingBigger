package com.moybl.topnumber;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.moybl.topnumber.backend.ResultCallback;
import com.moybl.topnumber.backend.TopNumberClient;
import com.moybl.topnumber.backend.VoidResult;
import com.moybl.topnumber.backend.topNumber.model.Player;

import java.util.ArrayList;
import java.util.List;

public class NumberData {

	public interface OnChangeListener {
		void onExchange(Source source);
	}

	private static final String KEY_SOURCE_LEVEL = "source_level";
	private static final String KEY_SOURCE_UNLOCKED = "source_unlocked";
	private static final String KEY_NUMBER = "number";
	private static final String KEY_LAST_UPDATE_TIME = "last_update_time";

	private static NumberData sInstance;

	public synchronized static NumberData getInstance() {
		if (sInstance == null) {
			sInstance = new NumberData();
		}

		return sInstance;
	}

	private OnChangeListener mOnChangeListener;
	private List<Source> mSources;
	private Player mPlayer;
	private long mLastUpdateTime;
	private long mTimeOffset;
	private double mRate;

	public List<Source> getSources() {
		return mSources;
	}

	public void setOnChangeListener(OnChangeListener onChangeListener) {
		mOnChangeListener = onChangeListener;
	}

	public void exchange(int index) {
		Source source = mSources.get(index);

		if (mPlayer.getNumber() < source.getCost()) {
			return;
		}

		mPlayer.setNumber(mPlayer.getNumber() - source.getCost());
		source.setLevel(source.getLevel() + 1);
		source.setUnlocked(true);

		updateRate();

		if (mOnChangeListener != null) {
			mOnChangeListener.onExchange(source);
		}
	}

	public double getNumber() {
		return mPlayer.getNumber();
	}

	public void load(Activity activity) {
		SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);

		mSources = new ArrayList<>();

		for (int i = 0; i < Source.COUNT; i++) {
			int level = prefs.getInt(KEY_SOURCE_LEVEL + i, i == 0 ? 1 : 0);
			boolean unlocked = prefs.getBoolean(KEY_SOURCE_UNLOCKED + i, i == 0);

			if (i == 0) {
				if (level == 0) {
					level = 1;
				}
				unlocked = true;
			}

			mSources.add(new Source(i, unlocked, level));
		}

		mPlayer = TopNumberClient.getInstance()
				.getPlayer();
		String prefsNumber = prefs.getString(KEY_NUMBER, null);
		if (prefsNumber != null) {
			mPlayer.setNumber(Double.parseDouble(prefsNumber));
		}

		mLastUpdateTime = prefs.getLong(KEY_LAST_UPDATE_TIME, mPlayer.getLogInTime());
		mTimeOffset = System.currentTimeMillis() - mPlayer.getLogInTime();

		updateRate();
		update();
	}

	public void clear(Activity activity) {
		mPlayer.setNumber(0.0);

		for (int i = 0; i < Source.COUNT; i++) {
			Source source = mSources.get(i);

			source.setLevel(i == 0 ? 1 : 0);
			source.setUnlocked(i == 0);
		}

		save(activity);
	}

	public void save(Activity activity) {
		SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor = prefs.edit();

		for (int i = 0; i < Source.COUNT; i++) {
			Source source = mSources.get(i);

			editor.putInt(KEY_SOURCE_LEVEL + i, source.getLevel());
			editor.putBoolean(KEY_SOURCE_UNLOCKED + i, source.isUnlocked());
		}

		editor.putString(KEY_NUMBER, mPlayer.getNumber()
				.toString());
		editor.putLong(KEY_LAST_UPDATE_TIME, mLastUpdateTime);

		editor.commit();

		TopNumberClient.getInstance()
				.insertNumber(mPlayer.getNumber(), new ResultCallback<VoidResult>() {
					@Override
					public void onResult(@NonNull VoidResult result) {
					}
				});
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

	public void update() {
		long now = System.currentTimeMillis() - mTimeOffset;
		long delta = now - mLastUpdateTime;
		mLastUpdateTime = now;

		mPlayer.setNumber(mPlayer.getNumber() + mRate * (delta / 1000.0));
	}

}
