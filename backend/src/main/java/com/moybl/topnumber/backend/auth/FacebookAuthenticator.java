package com.moybl.topnumber.backend.auth;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Authenticator;
import com.google.appengine.repackaged.org.codehaus.jackson.JsonNode;
import com.google.appengine.repackaged.org.codehaus.jackson.map.ObjectMapper;

import com.moybl.topnumber.backend.Constants;
import com.moybl.topnumber.backend.model.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

public class FacebookAuthenticator implements Authenticator {

	@Override
	public User authenticate(HttpServletRequest request) {
		String accessToken = request.getHeader("Authorization");

		if (accessToken != null) {
			try {
				URL url = new URL(String.format("https://graph.facebook.com/v2.6/debug_token?input_token=%s&access_token=%s",
						accessToken,
						Constants.FACEBOOK_APP_ACCESS_TOKEN));
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
				ObjectMapper objectMapper = new ObjectMapper();

				JsonNode dataNode = objectMapper.readTree(reader)
						.get("data");
				String appId = dataNode.get("app_id")
						.asText();
				String userId = dataNode.get("user_id")
						.asText();

				if (!appId.equals(Constants.FACEBOOK_APP_ID)) {
					return null;
				}

				Player player = new Player();
				player.setId(userId);

				return new PlayerUser(player);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		return null;
	}

}
