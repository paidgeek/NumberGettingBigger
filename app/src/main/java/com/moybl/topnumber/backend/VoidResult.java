package com.moybl.topnumber.backend;

public class VoidResult implements Result {

	private boolean mIsSuccess;

	public VoidResult(boolean isSuccess) {
		mIsSuccess = isSuccess;
	}

	@Override
	public boolean isSuccess() {
		return mIsSuccess;
	}

}
