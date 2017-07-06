package org.gooru.nucleus.handlers.events.processors.repositories;

import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 06-Jul-2017
 */
public interface BookmarkRepo {

    JsonObject createBookmarkEvent();

    JsonObject deleteBookmarkEvent();
}
