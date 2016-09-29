package com.moybl.topnumber.backend;

import com.moybl.topnumber.backend.topNumber.model.Player;

import java.util.List;

public class ListTopResult implements Result {

  private boolean mSuccess;
  private List<Player> mPlayers;
  private String mNextPageToken;

  public ListTopResult() {
    mSuccess = false;
  }

  public ListTopResult(List<Player> players, String nextPageToken) {
    mSuccess = true;
    mPlayers = players;
    mNextPageToken = nextPageToken;
  }

  public List<Player> getPlayers() {
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
