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
    
    private HttpClient httpClient;

    private static final String KEY_ENDPOINT = "api.endpoint";
    private static final String KEY_HOST = "api.host";
    private static final String KEY_PORT = "api.port";
    private static final String KEY_EMAIL_SETTINGS = "emailSettings";
    private static final String KEY_MAX_POOLSIZE = "http.conn.poolsize";
    
    

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_POOLSIZE = 20;

    private String host;
    private int port;
    private String endpoint;
    private int maxPoolsize;

    private AppHttpClient() {
    }

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
                    JsonObject eventConfig = config.getJsonObject(KEY_EMAIL_SETTINGS);
                    if (eventConfig == null || eventConfig.isEmpty()) {
                        LOGGER.warn("event config not found");
                        throw new AssertionError("event config not found");
                    }

                    this.host = eventConfig.getString(KEY_HOST);
                    if (this.host == null || this.host.isEmpty()) {
                        LOGGER.warn("api host missing");
                        throw new AssertionError("api host missing");
                    }

                    this.port = eventConfig.getInteger(KEY_PORT, DEFAULT_PORT);
                    this.maxPoolsize = eventConfig.getInteger(KEY_MAX_POOLSIZE, DEFAULT_POOLSIZE);

                    this.endpoint = eventConfig.getString(KEY_ENDPOINT);
                    if (this.endpoint == null || this.endpoint.isEmpty()) {
                        LOGGER.warn("api endpoint missing");
                        throw new AssertionError("api endpoint missing");
                    }

                    this.httpClient = vertx.createHttpClient(new HttpClientOptions().setDefaultHost(this.host)
                        .setDefaultPort(this.port).setMaxPoolSize(this.maxPoolsize));
                    initialized = true;
                    LOGGER.debug("App Http Client initialized successfully");
                }
            }
        }
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public String host() {
        return host;
    }

    public String endpoint() {
        return endpoint;
    }

    @Override
    public void finalizeComponent() {
        this.httpClient.close();
    }

    private static final class Holder {
        private static final AppHttpClient INSTANCE = new AppHttpClient();
    }
}
