package com.moybl.topnumber.backend;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.UnauthorizedException;

import com.moybl.topnumber.backend.auth.FacebookAuthenticator;
import com.moybl.topnumber.backend.auth.PlayerUser;
import com.moybl.topnumber.backend.auth.TopNumberAuthenticator;
import com.moybl.topnumber.backend.model.Player;

import java.io.IOException;

public class PlayersEndpoint extends TopNumberEndpoint {

	@ApiMethod(
			name = "players.logInWithFacebook",
			httpMethod = ApiMethod.HttpMethod.POST,
			authenticators = FacebookAuthenticator.class
	)
	public Player logInWithFacebook(User user) throws UnauthorizedException, IOException {
		if (user == null) {
			throw new UnauthorizedException("unauthorized");
		}

		Player playerUser = ((PlayerUser) user).getPlayer();

		Player player = OfyService.ofy()
				.load()
				.type(Player.class)
				.id(playerUser.getId())
				.now();

		if (player == null) {
			player = new Player();
			player.setId(playerUser.getId());
		}

		player.setSessionToken(Util.generateSessionToken());

		OfyService.ofy()
				.save()
				.entity(player)
				.now();

		return player;
	}

}
