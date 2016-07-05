package com.moybl.topnumber.backend;

public class VoidResult implements Result {

	private boolean mIsSuccess;

	public VoidResult(boolean isSuccess) {
		this.mIsSuccess = isSuccess;
	}

	@Override
	public boolean isSuccess() {
		return false;
	}

}
