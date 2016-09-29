package com.moybl.topnumber;

public class Source {

  public static final int COUNT = 50;
  public static final double MULTIPLIER = 1.15;

  private int mIndex;
  private boolean mUnlocked;
  private int mLevel;

  public Source(int index, boolean unlocked, int level) {
    mIndex = index;
    mUnlocked = unlocked;
    mLevel = level;
  }

  public int getIndex() {
    return mIndex;
  }

  public int getLevel() {
    return mLevel;
  }

  public boolean isUnlocked() {
    return mUnlocked;
  }

  public void setUnlocked(boolean unlocked) {
    mUnlocked = unlocked;
  }

  public void setLevel(int level) {
    mLevel = level;
  }

  public double getBaseCost() {
    return NumberUtil.prettyNumber(Math.pow(5.0 - Math.round(mIndex / 10.0) / 5.0, mIndex));
  }

  public double getBaseRate() {
    return NumberUtil.prettyNumber(Math.pow(3.5, mIndex));
  }

  public double getRate() {
    return getBaseRate() * mLevel;
  }

  public double getCost() {
    return NumberUtil.prettyNumber(getBaseCost() * Math.pow(MULTIPLIER, mLevel));
  }

}
