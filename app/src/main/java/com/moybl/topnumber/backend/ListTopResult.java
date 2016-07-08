package com.moybl.topnumber.backend;

import java.util.List;

public class ListTopResult implements Result {

	private boolean mSuccess;
	private List<PlayerModel> mPlayers;
	private String mNextPageToken;

	public ListTopResult() {
		mSuccess = false;
	}

	public ListTopResult(List<PlayerModel> players, String nextPageToken) {
		mSuccess = true;
		mPlayers = players;
		mNextPageToken = nextPageToken;
	}

	public List<PlayerModel> getPlayers() {
		return mPlayers;
	}

	public String getNextPageToken() {
		return mNextPageToken;
	}

	@Override
	public boolean isSuccess() {
		return mSuccess;
	}

}
