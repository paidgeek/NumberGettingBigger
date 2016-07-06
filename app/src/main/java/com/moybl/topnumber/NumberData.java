package com.moybl.topnumber;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.moybl.topnumber.backend.ResultCallback;
import com.moybl.topnumber.backend.TopNumberClient;
import com.moybl.topnumber.backend.VoidResult;
import com.moybl.topnumber.backend.topNumber.TopNumber;
import com.moybl.topnumber.backend.topNumber.model.Player;

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

	private List<Source> mSources;
	private Player mPlayer;
	private long mLastUpdateAt;
	private long mCurrentTimeOffset;

	public List<Source> getSources() {
		return mSources;
	}

	public void setSources(List<Source> sources) {
		mSources = sources;
	}

	public void exchange(Source source) {
		if (mPlayer.getNumber() < source.getCost()) {
			return;
		}

		mPlayer.setNumber(mPlayer.getNumber() - source.getCost());
		source.setLevel(source.getLevel() + 1);
	}

	public double getNumber() {
		return mPlayer.getNumber();
	}

	public void load(Activity activity) {
		SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);

		mSources = new ArrayList<>();

		for (int i = 0; i < Source.COUNT; i++) {
			int level = prefs.getInt(KEY_SOURCE_LEVEL + i, i == 0 ? 1 : 0);

			if (i == 0 && level == 0) {
				level = 1;
			}

			mSources.add(new Source(i, level));
		}

		mPlayer = TopNumberClient.getInstance()
				.getPlayer();
		String prefsNumber = prefs.getString(KEY_NUMBER, null);
		if (prefsNumber != null) {
			mPlayer.setNumber(Double.parseDouble(prefsNumber));
		}

		mLastUpdateAt = mPlayer.getLastLogInAt()
				.getValue();
		mCurrentTimeOffset = System.currentTimeMillis() - mPlayer.getCurrentLogInTime()
				.getValue();
		update();
	}

	public void save(Activity activity) {
		SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor = prefs.edit();

		for (int i = 0; i < Source.COUNT; i++) {
			Source source = mSources.get(i);

			editor.putInt(KEY_SOURCE_LEVEL + i, source.getLevel());
		}

		editor.putString(KEY_NUMBER, mPlayer.getNumber()
				.toString());

		editor.commit();

		TopNumberClient.getInstance()
				.insertNumber(mPlayer.getNumber(), new ResultCallback<VoidResult>() {
					@Override
					public void onResult(@NonNull VoidResult result) {
					}
				});
	}

	public void update() {
		double rate = 0.0;

		for (int i = 0; i < Source.COUNT; i++) {
			rate += mSources.get(i)
					.getRate();
		}

		long delta = (System.currentTimeMillis() + mCurrentTimeOffset) - mLastUpdateAt;
		mLastUpdateAt = System.currentTimeMillis() + mCurrentTimeOffset;

		mPlayer.setNumber(mPlayer.getNumber() + rate * (delta / 1000.0));
	}

}
