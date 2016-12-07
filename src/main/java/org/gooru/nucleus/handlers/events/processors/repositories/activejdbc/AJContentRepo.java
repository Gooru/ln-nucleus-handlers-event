package org.gooru.nucleus.handlers.events.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.events.app.components.DataSourceRegistry;
import org.gooru.nucleus.handlers.events.constants.EventRequestConstants;
import org.gooru.nucleus.handlers.events.constants.EventResponseConstants;
import org.gooru.nucleus.handlers.events.processors.ProcessorContext;
import org.gooru.nucleus.handlers.events.processors.repositories.ContentRepo;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities.AJEntityContent;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities.AJEntityOriginalResource;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * Created by subbu on 06-Jan-2016.
 */
public class AJContentRepo implements ContentRepo {

    private static final Logger LOGGER = LoggerFactory.getLogger(AJContentRepo.class);
    private final ProcessorContext context;

    public AJContentRepo(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public JsonObject createUpdateResourceEvent() {
        String contentId = context.eventBody().getString(EventRequestConstants.ID);
        return getResource(contentId);
    }

    @Override
    public JsonObject copyResourceEvent() {
        JsonObject response = new JsonObject();
        String targetContentId = context.eventBody().getString(EventRequestConstants.ID);
        JsonObject targetContent = getReferenceResource(targetContentId);
        response.put(EventResponseConstants.TARGET, targetContent);

        String sourceContentId = targetContent.getString(AJEntityContent.ORIGINAL_CONTENT_ID);
        if (sourceContentId != null && !sourceContentId.isEmpty()) {
            JsonObject sourceContent = getResource(sourceContentId);
            response.put(EventResponseConstants.SOURCE, sourceContent);
        }
        return response;
    }

    @Override
    public JsonObject deletedResourceEvent() {
        String contentId = context.eventBody().getString(EventRequestConstants.ID);
        return getResource(contentId);
    }

    @Override
    public JsonObject createUpdateQuestionEvent() {
        String contentId = context.eventBody().getString(EventRequestConstants.ID);
        return getQuestion(contentId);
    }

    @Override
    public JsonObject copyQuestionEvent() {
        JsonObject response = new JsonObject();
        String targetContentId = context.eventBody().getString(EventRequestConstants.ID);
        JsonObject targetContent = getQuestion(targetContentId);
        response.put(EventResponseConstants.TARGET, targetContent);

        String sourceContentId = targetContent.getString(AJEntityContent.ORIGINAL_CONTENT_ID);
        if (sourceContentId != null && !sourceContentId.isEmpty()) {
            JsonObject sourceContent = getQuestion(sourceContentId);
            response.put(EventResponseConstants.SOURCE, sourceContent);
        }
        return response;
    }

    @Override
    public JsonObject deletedQuestionEvent() {
        String contentId = context.eventBody().getString(EventRequestConstants.ID);
        return getQuestion(contentId);
    }

    @Override
    public JsonObject getReferenceResource(String contentId) {
        Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
        LOGGER.debug("getting resource for id {}", contentId);

        JsonObject result = null;
        LazyList<AJEntityContent> resources = AJEntityContent.findBySQL(AJEntityContent.SELECT_RESOURCE, contentId);
        if (!resources.isEmpty()) {
            result = new JsonObject(new JsonFormatterBuilder()
                .buildSimpleJsonFormatter(false, AJEntityContent.RESOURCE_FIELDS).toJson(resources.get(0)));
        }

        Base.close();
        return result;
    }

    @Override
    public JsonObject getQuestion(String contentId) {
        Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
        LOGGER.debug("getting question for id {}", contentId);

        JsonObject result = null;
        LazyList<AJEntityContent> questions = AJEntityContent.findBySQL(AJEntityContent.SELECT_QUESTION, contentId);
        if (!questions.isEmpty()) {
            result = new JsonObject(new JsonFormatterBuilder()
                .buildSimpleJsonFormatter(false, AJEntityContent.QUESTION_FIELDS).toJson(questions.get(0)));
        }

        Base.close();
        return result;
    }

    @Override
    public String getContentFormatById(String contentId) {
        Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
        String contentFormat = null;
        LazyList<AJEntityContent> contents =
            AJEntityContent.findBySQL(AJEntityContent.SELECT_CONTENT_FORMAT, contentId);
        if (!contents.isEmpty()) {
            contentFormat = contents.get(0).getString(AJEntityContent.CONTENT_FORMAT);
        }
        Base.close();
        return contentFormat;
    }

    @Override
    public JsonObject getOriginalResource(String resourceId) {
        Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
        LOGGER.debug("getting original resource for id {}", resourceId);

        JsonObject result = null;
        LazyList<AJEntityOriginalResource> resources = AJEntityOriginalResource.findBySQL(AJEntityOriginalResource.SELECT_ORIGINAL_RESOURCE, resourceId);
        if (!resources.isEmpty()) {
            result = new JsonObject(new JsonFormatterBuilder()
                .buildSimpleJsonFormatter(false, AJEntityOriginalResource.ORIGINAL_RESOURCE_FIELDS).toJson(resources.get(0)));
            String VALUE_NULL = null;
            result.put(AJEntityContent.ORIGINAL_CREATOR_ID, VALUE_NULL);
            result.put(AJEntityContent.ORIGINAL_CONTENT_ID, VALUE_NULL);
            result.put(AJEntityContent.PARENT_CONTENT_ID, VALUE_NULL);
            result.put(AJEntityContent.COURSE_ID, VALUE_NULL);
            result.put(AJEntityContent.UNIT_ID, VALUE_NULL);
            result.put(AJEntityContent.LESSON_ID, VALUE_NULL);
            result.put(AJEntityContent.COLLECTION_ID, VALUE_NULL);
            result.put(AJEntityContent.CONTENT_FORMAT, "resource");
        }

        Base.close();
        return result;
    }
    
    private JsonObject getResource(String id) {
        JsonObject result = getOriginalResource(id);
        if (result == null || result.isEmpty()) {
            result = getReferenceResource(id);
        }
        
        return result;
    }

}
