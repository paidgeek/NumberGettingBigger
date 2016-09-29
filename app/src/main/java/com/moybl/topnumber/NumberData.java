package com.moybl.topnumber;

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

  public static final String KEY_SOURCE_LEVEL = "source_level";
  public static final String KEY_SOURCE_UNLOCKED = "source_unlocked";
  public static final String KEY_NUMBER = "number";
  public static final String KEY_LAST_UPDATE_TIME = "last_update_time";
  public static final String KEY_RESET = "reset";

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

  public void load() {
    mSources = new ArrayList<>();

    for (int i = 0; i < Source.COUNT; i++) {
      int level = Prefs.getInt(KEY_SOURCE_LEVEL + i, i == 0 ? 1 : 0);
      boolean unlocked = Prefs.getBoolean(KEY_SOURCE_UNLOCKED + i, i == 0);

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
    mPlayer.setNumber(Prefs.getDouble(KEY_NUMBER, mPlayer.getNumber()));

    mLastUpdateTime = Prefs.getLong(KEY_LAST_UPDATE_TIME, mPlayer.getLogInTime());
    mTimeOffset = System.currentTimeMillis() - mPlayer.getLogInTime();

    updateRate();
    update();
  }

  public void clear() {
    Prefs.removeAll();
    Prefs.setBoolean(KEY_RESET, true);
    Prefs.save();
  }

  public void save() {
    for (int i = 0; i < Source.COUNT; i++) {
      Source source = mSources.get(i);

      Prefs.setInt(KEY_SOURCE_LEVEL + i, source.getLevel());
      Prefs.setBoolean(KEY_SOURCE_UNLOCKED + i, source.isUnlocked());
    }

    Prefs.setDouble(KEY_NUMBER, mPlayer.getNumber());
    Prefs.setLong(KEY_LAST_UPDATE_TIME, mLastUpdateTime);

    Prefs.save();

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
      Source s = mSources.get(i);

      if (s.isUnlocked()) {
        mRate += s.getRate();
      }
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

  public void setLastUpdateTime(long lastUpdateTime) {
    mLastUpdateTime = lastUpdateTime;
  }

  public long getLastUpdateTime() {
    return mLastUpdateTime;
  }

}
