package org.gooru.nucleus.handlers.events.app.components;

import org.gooru.nucleus.handlers.events.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.handlers.events.bootstrap.startup.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 06-Feb-2017
 */
public final class AppHttpClient implements Initializer, Finalizer {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppHttpClient.class);
  private volatile boolean initialized = false;

  private HttpClient httpClientForEmail;
  private HttpClient httpClientForProfile;

  private static final String KEY_ENDPOINT = "api.endpoint";
  private static final String KEY_HOST = "api.host";
  private static final String KEY_PORT = "api.port";
  private static final String KEY_MAX_POOLSIZE = "http.conn.poolsize";

  private static final String KEY_EMAIL_SETTINGS = "emailSettings";
  private static final String KEY_PROFILE_SETTINGS = "profileSettings";

  private static final int DEFAULT_PORT = 8080;
  private static final int DEFAULT_POOLSIZE = 20;

  private String emailEndpoint;
  private String profileEndpoint;

  private AppHttpClient() {}

  public static AppHttpClient getInstance() {
    return Holder.INSTANCE;
  }

  @Override
  public void initializeComponent(Vertx vertx, JsonObject config) {
    LOGGER.debug("Initializing called for http client");
    if (!initialized) {
      LOGGER.debug("may have to initialize http client");
      synchronized (Holder.INSTANCE) {
        if (!initialized) {
          LOGGER.debug("initializing http client after double checking");

          // read email config and initialize http client for email
          JsonObject eventConfig = config.getJsonObject(KEY_EMAIL_SETTINGS);
          if (eventConfig == null || eventConfig.isEmpty()) {
            LOGGER.warn("event config not found");
            throw new AssertionError("event config not found");
          }
          initializeHttpClientForEmail(vertx, eventConfig);

          // read profile config and initialize http client for profile
          JsonObject profileConfig = config.getJsonObject(KEY_PROFILE_SETTINGS);
          if (profileConfig == null || profileConfig.isEmpty()) {
            LOGGER.warn("profile config not found");
            throw new AssertionError("pofile config not found");
          }
          initializeHttpClientForProfile(vertx, profileConfig);

          initialized = true;
          LOGGER.debug("App Http Client initialized successfully");
        }
      }
    }
  }

  private void initializeHttpClientForEmail(Vertx vertx, JsonObject eventConfig) {
    String host = eventConfig.getString(KEY_HOST);
    if (host == null || host.isEmpty()) {
      LOGGER.warn("api host missing");
      throw new AssertionError("api host missing");
    }

    Integer port = eventConfig.getInteger(KEY_PORT, DEFAULT_PORT);
    Integer maxPoolsize = eventConfig.getInteger(KEY_MAX_POOLSIZE, DEFAULT_POOLSIZE);

    this.emailEndpoint = eventConfig.getString(KEY_ENDPOINT);
    if (this.emailEndpoint == null || this.emailEndpoint.isEmpty()) {
      LOGGER.warn("api endpoint missing");
      throw new AssertionError("api endpoint missing");
    }

    this.httpClientForEmail = vertx.createHttpClient(new HttpClientOptions().setDefaultHost(host)
        .setDefaultPort(port).setMaxPoolSize(maxPoolsize));
  }

  private void initializeHttpClientForProfile(Vertx vertx, JsonObject profileConfig) {
    String host = profileConfig.getString(KEY_HOST);
    if (host == null || host.isEmpty()) {
      LOGGER.warn("profile api host missing");
      throw new AssertionError("profile api host missing");
    }

    Integer port = profileConfig.getInteger(KEY_PORT, DEFAULT_PORT);
    Integer maxPoolsize = profileConfig.getInteger(KEY_MAX_POOLSIZE, DEFAULT_POOLSIZE);

    this.profileEndpoint = profileConfig.getString(KEY_ENDPOINT);
    if (this.profileEndpoint == null || this.profileEndpoint.isEmpty()) {
      LOGGER.warn("profile api endpoint missing");
      throw new AssertionError("profile api endpoint missing");
    }

    this.httpClientForProfile = vertx.createHttpClient(new HttpClientOptions().setDefaultHost(host)
        .setDefaultPort(port).setMaxPoolSize(maxPoolsize));
  }

  public HttpClient getEmailHttpClient() {
    return this.httpClientForEmail;
  }

  public String emailEndpoint() {
    return emailEndpoint;
  }

  public HttpClient getProfileHttpClient() {
    return httpClientForProfile;
  }

  public String profileEndpoint() {
    return profileEndpoint;
  }

  @Override
  public void finalizeComponent() {
    this.httpClientForEmail.close();
    this.httpClientForProfile.close();
  }

  private static final class Holder {
    private static final AppHttpClient INSTANCE = new AppHttpClient();
  }
}
