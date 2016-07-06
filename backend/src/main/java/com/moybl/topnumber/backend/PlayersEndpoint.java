package com.moybl.topnumber.backend;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.repackaged.com.google.common.base.Flag;

import com.moybl.topnumber.backend.auth.FacebookAuthenticator;
import com.moybl.topnumber.backend.auth.PlayerUser;
import com.moybl.topnumber.backend.auth.TopNumberAuthenticator;
import com.moybl.topnumber.backend.model.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayersEndpoint extends TopNumberEndpoint {

	@ApiMethod(
			name = "players.logInWithFacebook",
			httpMethod = ApiMethod.HttpMethod.POST,
			authenticators = FacebookAuthenticator.class
	)
	public Player logInWithFacebook(User user,
											  @Nullable @Named("reset") boolean reset) throws UnauthorizedException, IOException {
		if (user == null) {
			throw new UnauthorizedException("unauthorized");
		}

		Player playerUser = ((PlayerUser) user).getPlayer();

		Player player = OfyService.ofy()
				.load()
				.type(Player.class)
				.id(playerUser.getId())
				.now();

		if (player == null || reset) {
			player = new Player();
			player.setId(playerUser.getId());
			player.setNumber(0.0);
		}

		player.setSessionToken(Util.generateSessionToken());

		OfyService.ofy()
				.save()
				.entity(player)
				.now();

		player.setLogInTime(System.currentTimeMillis());

		return player;
	}

	@ApiMethod(
			name = "players.insertTestPlayers",
			httpMethod = ApiMethod.HttpMethod.POST,
			authenticators = TopNumberAuthenticator.class
	)
	public void insertTestPlayers(User user) throws UnauthorizedException, IOException {
		if (user == null) {
			throw new UnauthorizedException("unauthorized");
		}

		List<Player> players = new ArrayList<>();
		Random random = new Random();

		for (int i = 0; i < 100; i++) {
			Player player = new Player();

			player.setId(random.nextLong() + "");
			player.setSessionToken(Util.generateSessionToken());
			player.setNumber(random.nextInt(100000));

			players.add(player);
		}

		OfyService.ofy()
				.save()
				.entities(players)
				.now();
	}

}
