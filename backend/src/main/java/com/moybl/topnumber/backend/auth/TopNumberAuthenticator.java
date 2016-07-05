package com.moybl.topnumber.backend.auth;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Authenticator;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.moybl.topnumber.backend.OfyService;
import com.moybl.topnumber.backend.model.Player;

import javax.servlet.http.HttpServletRequest;

public class TopNumberAuthenticator implements Authenticator {

	@Override
	public User authenticate(HttpServletRequest request) {
		String playerId = request.getHeader("X-Player-Id");
		String sessionToken = request.getHeader("X-Session-Token");

		if (playerId == null || sessionToken == null) {
			return null;
		}

		Key playerKey = KeyFactory.createKey("Player", playerId);
		Player player = OfyService.ofy()
				.load()
				.type(Player.class)
				.filterKey(playerKey)
				.first()
				.now();

		if (player == null || !sessionToken.equals(player.getSessionToken())) {
			return null;
		}

		return new PlayerUser(player);
	}

}
