package com.moybl.topnumber.backend;

public class PlayerModel {

	private String mId;
	private int mOrder;
	private boolean mSilhouette;
	private String mName;
	private double mNumber;

	public PlayerModel(String id, int order, double number) {
		mId = id;
		mOrder = order;
		mNumber = number;
		mSilhouette = true;
	}

	public String getId() {
		return mId;
	}

	public int getOrder() {
		return mOrder;
	}

	public boolean isSilhouette() {
		return mSilhouette;
	}

	public void setSilhouette(boolean silhouette) {
		mSilhouette = silhouette;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public double getNumber() {
		return mNumber;
	}

	public void setNumber(double number) {
		this.mNumber = number;
	}
}
