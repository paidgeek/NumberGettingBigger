package com.moybl.topnumber.backend;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.repackaged.org.codehaus.jackson.map.ObjectMapper;

import com.googlecode.objectify.cmd.Query;
import com.moybl.topnumber.backend.auth.PlayerUser;
import com.moybl.topnumber.backend.auth.TopNumberAuthenticator;
import com.moybl.topnumber.backend.model.ListPlayersResponse;
import com.moybl.topnumber.backend.model.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class NumbersEndpoint extends TopNumberEndpoint {

	@ApiMethod(
			name = "numbers.insert",
			httpMethod = ApiMethod.HttpMethod.POST,
			authenticators = TopNumberAuthenticator.class
	)
	public void insertNumber(User user,
									 @Named("number") double number) throws UnauthorizedException {
		if (user == null) {
			throw new UnauthorizedException("unauthorized");
		}

		Player player = ((PlayerUser) user).getPlayer();

		player.setNumber(number);

		OfyService.ofy()
				.save()
				.entity(player)
				.now();
	}

	@ApiMethod(
			name = "numbers.listTop",
			httpMethod = ApiMethod.HttpMethod.GET,
			authenticators = TopNumberAuthenticator.class
	)
	public ListPlayersResponse listTop(User user,
												  @Nullable @Named("nextPageToken") String nextPageToken) throws UnauthorizedException {
		if (user == null) {
			throw new UnauthorizedException("unauthorized");
		}

		Query<Player> query = OfyService.ofy()
				.load()
				.type(Player.class)
				.order("-number")
				.limit(30);

		if (nextPageToken != null) {
			query = query.startAt(Cursor.fromWebSafeString(nextPageToken));
		}

		List<Player> players = new ArrayList<>();
		QueryResultIterator<Player> i = query.iterator();

		while (i.hasNext()) {
			players.add(i.next());
		}

		return new ListPlayersResponse(players, i.getCursor()
				.toWebSafeString());
	}

	@ApiMethod(
			name = "numbers.listPlayers",
			httpMethod = ApiMethod.HttpMethod.POST,
			authenticators = TopNumberAuthenticator.class
	)
	public List<Player> listPlayers(User user, @Named("playerIds") String playerIdsList) throws UnauthorizedException, BadRequestException {
		if (user == null) {
			throw new UnauthorizedException("unauthorized");
		}

		String[] playerIds = playerIdsList.split(",");

		if (playerIds.length == 0) {
			throw new BadRequestException("no player ids specified");
		}

		Collection<Player> result = OfyService.ofy()
				.load()
				.type(Player.class)
				.ids(playerIds)
				.values();
		Iterator<Player> resultIterator = result.iterator();
		List<Player> players = new ArrayList<>(result.size());

		while (resultIterator.hasNext()) {
			players.add(resultIterator.next());
		}

		return players;
	}

}
