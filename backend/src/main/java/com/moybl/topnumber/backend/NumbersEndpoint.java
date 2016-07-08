package com.moybl.topnumber.backend;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.repackaged.com.google.datastore.v1.Datastore;

import com.googlecode.objectify.cmd.Query;
import com.moybl.topnumber.backend.auth.PlayerUser;
import com.moybl.topnumber.backend.auth.TopNumberAuthenticator;
import com.moybl.topnumber.backend.model.Player;

import java.util.ArrayList;
import java.util.List;

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
	public CollectionResponse<Player> listTop(User user,
															@Nullable @Named("cursor") String cursorString) throws UnauthorizedException {
		if (user == null) {
			throw new UnauthorizedException("unauthorized");
		}

		Query<Player> query = OfyService.ofy()
				.load()
				.type(Player.class)
				.order("-number")
				.limit(20);

		if (cursorString != null) {
			query = query.startAt(Cursor.fromWebSafeString(cursorString));
		}

		List<Player> players = new ArrayList<>();
		QueryResultIterator<Player> i = query.iterator();

		while (i.hasNext()) {
			players.add(i.next());
		}

		Cursor cursor = i.getCursor();

		return CollectionResponse.<Player>builder().setItems(players)
				.setNextPageToken(cursor.toWebSafeString())
				.build();
	}

}
