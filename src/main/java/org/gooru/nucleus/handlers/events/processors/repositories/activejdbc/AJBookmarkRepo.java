package org.gooru.nucleus.handlers.events.processors.repositories.activejdbc;

import java.util.UUID;

import org.gooru.nucleus.handlers.events.app.components.DataSourceRegistry;
import org.gooru.nucleus.handlers.events.constants.EventRequestConstants;
import org.gooru.nucleus.handlers.events.processors.ProcessorContext;
import org.gooru.nucleus.handlers.events.processors.repositories.BookmarkRepo;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities.AJEntityBookmark;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 06-Jul-2017
 */
public class AJBookmarkRepo implements BookmarkRepo {

    private static final Logger LOGGER = LoggerFactory.getLogger(AJBookmarkRepo.class);
    private final ProcessorContext context;

    public AJBookmarkRepo(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public JsonObject createBookmarkEvent() {
        String bookmarkId = context.eventBody().getString(EventRequestConstants.ID);
        return getBookmark(bookmarkId);
    }

    @Override
    public JsonObject deleteBookmarkEvent() {
        String bookmarkId = context.eventBody().getString(EventRequestConstants.ID);
        return getBookmark(bookmarkId);
    }

    private JsonObject getBookmark(String bookmarkId) {
        Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
        LOGGER.debug("getting bookmark for id {}", bookmarkId);

        JsonObject result = null;
        AJEntityBookmark bookmark = AJEntityBookmark.findById(UUID.fromString(bookmarkId));
        if (bookmark != null) {
            result = new JsonObject(new JsonFormatterBuilder()
                .buildSimpleJsonFormatter(false, AJEntityBookmark.BOOKMARKS_FIELDS).toJson(bookmark));
        }
        Base.close();
        return result;
    }

}
