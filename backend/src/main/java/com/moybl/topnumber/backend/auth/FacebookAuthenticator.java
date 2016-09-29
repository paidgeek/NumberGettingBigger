package com.moybl.topnumber.backend.auth;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Authenticator;
import com.google.appengine.repackaged.org.codehaus.jackson.JsonNode;
import com.google.appengine.repackaged.org.codehaus.jackson.map.ObjectMapper;

import com.moybl.topnumber.backend.Constants;
import com.moybl.topnumber.backend.model.Player;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

public class FacebookAuthenticator implements Authenticator {

  private static Logger logger = Logger.getLogger(FacebookAuthenticator.class.getName());

  @Override
  public User authenticate(HttpServletRequest request) {
    String accessToken = request.getHeader("X-Access-Token");

    if (accessToken != null) {
      try {
        String batch = URLEncoder.encode(String.format("[{\"method\":\"GET\",\"relative_url\":\"me?fields=first_name\"},{\"method\":\"GET\",\"relative_url\":\"debug_token?input_token=%s&access_token=%s\"}]", accessToken, Constants.FACEBOOK_APP_ACCESS_TOKEN), "UTF-8");
        String params = String.format("batch=%s&access_token=%s", batch, accessToken);
        URL url = new URL("https://graph.facebook.com/v2.6");

        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("POST");
        c.setDoOutput(true);

        DataOutputStream dos = new DataOutputStream(c.getOutputStream());
        dos.write(params.getBytes("UTF-8"));

        if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
          return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode[] r = new JsonNode[2];
        int i = 0;
        for (JsonNode n : objectMapper.readTree(c.getInputStream())) {
          r[i++] = n;
        }

        if (r[0]
            .get("code")
            .asInt() != 200 || r[1]
            .get("code")
            .asInt() != 200) {
          return null;
        }

        JsonNode me = objectMapper.readTree(r[0]
            .get("body")
            .asText());
        JsonNode token = objectMapper.readTree(r[1]
            .get("body")
            .asText())
            .get("data");

        String appId = token.get("app_id")
            .asText();

        if (!appId.equals(Constants.FACEBOOK_APP_ID)) {
          return null;
        }

        String userId = me.get("id")
            .asText();
        String firstName = me.get("first_name")
            .asText();

        Player player = new Player();
        player.setId(userId);
        player.setName(firstName);

        return new PlayerUser(player);
      } catch (Exception e) {
        logger.severe(e.getMessage());

        return null;
      }
    }

    return null;
  }

}
