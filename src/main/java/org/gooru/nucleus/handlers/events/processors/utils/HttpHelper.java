package org.gooru.nucleus.handlers.events.processors.utils;

import org.gooru.nucleus.handlers.events.app.components.AppHttpClient;
import org.gooru.nucleus.handlers.events.constants.HttpConstants;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities.AJEntityUserState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 11-Dec-2017
 */
public final class HttpHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpHelper.class);

  private HttpHelper() {
    throw new AssertionError();
  }

  public static void updateWelcomeEmailState(String userId, String authHeader) {
    AppHttpClient httpClient = AppHttpClient.getInstance();
    HttpClientRequest httpRequest =
        httpClient.getProfileHttpClient().put(httpClient.profileEndpoint(), responseHandler -> {
          if (responseHandler.statusCode() == HttpConstants.HttpStatus.SUCCESS.getCode()
              || responseHandler.statusCode() == HttpConstants.HttpStatus.NO_CONTENT.getCode()) {
            LOGGER.debug("welcome email state has been updated for user '{}'", userId);
          } else {
            LOGGER.warn("enable to update welcome email state for user '{}'. status code: {}",
                responseHandler.statusCode());
          }
        });

    String data = createSystemStatePayload().toString();
    httpRequest.putHeader(HttpConstants.HEADER_CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
    httpRequest.putHeader(HttpConstants.HEADER_CONTENT_LENGTH,
        String.valueOf(data.getBytes().length));
    httpRequest.putHeader(HttpConstants.HEADER_AUTH, authHeader);
    httpRequest.write(data);
    httpRequest.end();
  }

  private static JsonObject createSystemStatePayload() {
    JsonObject welcomeEmailState =
        new JsonObject().put(AJEntityUserState.WELCOME_EMAIL_SENT_KEY, true);
    return new JsonObject().put(AJEntityUserState.SYSTEM_STATE, welcomeEmailState);
  }

}
