package com.moybl.topnumber.backend.model;

import java.util.List;

public class ListPlayersResponse {

	private List<Player> players;
	private String nextPageToken;

	public ListPlayersResponse(List<Player> players, String nextPageToken) {
		this.players = players;
		this.nextPageToken = nextPageToken;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public String getNextPageToken() {
		return nextPageToken;
	}

}
