package com.moybl.numbergettingbigger;

public class Source {

	public static final int COUNT = 50;
	public static final double MULTIPLIER = 1.15;

	private int mIndex;
	private int mLevel;

	public Source(int index, int level){
		mIndex = index;
		mLevel = level;
	}

	public int getIndex() {
		return mIndex;
	}

	public int getLevel() {
		return mLevel;
	}

	public void setLevel(int level) {
		mLevel = level;
	}

	public double getBaseCost(){
		return NumberUtil.prettyNumber(Math.pow(5.0, mIndex));
	}

	public double getBaseRate(){
		return NumberUtil.prettyNumber(Math.pow(3, mIndex));
	}

	public double getRate(){
		return getBaseRate() * mLevel;
	}

	public double getCost(){
		return NumberUtil.prettyNumber(getBaseCost() * Math.pow(MULTIPLIER, mLevel));
	}

}
