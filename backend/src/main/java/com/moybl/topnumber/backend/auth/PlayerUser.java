package com.moybl.topnumber.backend.auth;

import com.google.api.server.spi.auth.common.User;

import com.moybl.topnumber.backend.model.Player;

public class PlayerUser extends User {

	private Player player;

	public PlayerUser(Player player) {
		super(null, null);

		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
