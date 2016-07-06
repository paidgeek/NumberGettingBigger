package com.moybl.topnumber.backend;

public class ObjectResult<T> implements Result {

	private boolean mIsSuccess;
	private T mData;

	public ObjectResult() {
		mIsSuccess = false;
	}

	public ObjectResult(T data) {
		mIsSuccess = data != null;
		mData = data;
	}

	@Override
	public boolean isSuccess() {
		return mIsSuccess;
	}

	public T getObject() {
		return mData;
	}

}
