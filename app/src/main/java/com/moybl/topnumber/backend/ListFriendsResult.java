package com.moybl.topnumber.backend;

import com.moybl.topnumber.backend.topNumber.model.Player;

import java.util.List;

public class ListFriendsResult implements Result {

	private boolean mSuccess;
	private List<Player> mFriends;

	public ListFriendsResult() {
		mSuccess = false;
	}

	public ListFriendsResult(List<Player> friends) {
		mSuccess = true;
		mFriends = friends;
	}

	public List<Player> getFriends() {
		return mFriends;
	}

	@Override
	public boolean isSuccess() {
		return mSuccess;
	}

}
