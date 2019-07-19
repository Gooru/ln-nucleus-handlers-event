package org.gooru.nucleus.handlers.events.processors.partners;

import java.util.List;
import org.gooru.nucleus.handlers.events.constants.EventRequestConstants;
import org.gooru.nucleus.handlers.events.constants.EventResponseConstants;
import org.gooru.nucleus.handlers.events.constants.MessageConstants;
import org.gooru.nucleus.handlers.events.processors.MessageDispatcher;
import org.gooru.nucleus.handlers.events.processors.repositories.RepoBuilder;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities.AJEntityCollection;
import org.gooru.nucleus.handlers.events.processors.responseobject.ResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.core.json.JsonObject;

/**
 * @author gooru on 23-Apr-2018
 */
public class ContentCreationEventProcessor extends ContentEventProcessor {

  private final static Logger LOGGER = LoggerFactory.getLogger(ContentCreationEventProcessor.class);

  private final JsonObject request;

  public ContentCreationEventProcessor(JsonObject request) {
    this.request = request;
  }

  @Override
  public void process() {
    JsonObject eventBody = request.getJsonObject(EventRequestConstants.EVENT_BODY);
    String courseId = eventBody.getString(EventRequestConstants.ID);
    JsonObject courseEventBody = new JsonObject().put(EventRequestConstants.ID, courseId);
    JsonObject courseRequest = new JsonObject()
        .put(EventRequestConstants.EVENT_NAME, MessageConstants.MSG_OP_EVT_COURSE_CREATE)
        .put(EventRequestConstants.SESSION_TOKEN,
            request.getString(EventRequestConstants.SESSION_TOKEN))
        .put(EventRequestConstants.EVENT_BODY, courseEventBody);
    JsonObject courseResult = RepoBuilder
        .buildCourseRepo(createContext(MessageConstants.MSG_OP_EVT_COURSE_CREATE, eventBody))
        .createUpdateCourseEvent();

    JsonObject courseEvent =
        ResponseFactory.generateItemCreateResponse(courseRequest, courseResult);
    MessageDispatcher.getInstance().sendMessage2Kafka(EventResponseConstants.EVENT_ITEM_CREATE,
        courseEvent);

    LOGGER.debug("fetching units for course");
    List<String> units = RepoBuilder.buildUnitRepo(null).fetchUnitsByCourse(courseId);
    units.forEach(unitId -> {
      LOGGER.debug("processing unit: {}", unitId);
      JsonObject unitEventBody = new JsonObject().put(EventRequestConstants.ID, unitId);
      JsonObject unitRequest = new JsonObject()
          .put(EventRequestConstants.EVENT_NAME, MessageConstants.MSG_OP_EVT_UNIT_CREATE)
          .put(EventRequestConstants.SESSION_TOKEN,
              request.getString(EventRequestConstants.SESSION_TOKEN))
          .put(EventRequestConstants.EVENT_BODY, unitEventBody);
      JsonObject unitResult = RepoBuilder
          .buildUnitRepo(createContext(MessageConstants.MSG_OP_EVT_UNIT_CREATE, unitEventBody))
          .createUpdateUnitEvent();

      MessageDispatcher.getInstance().sendMessage2Kafka(EventResponseConstants.EVENT_ITEM_CREATE,
          ResponseFactory.generateItemCreateResponse(unitRequest, unitResult));

      LOGGER.debug("fetching lessons");
      List<String> lessons = RepoBuilder.buildLessonRepo(null).fetchLessonsByCU(courseId, unitId);
      lessons.forEach(lessonId -> {
        LOGGER.debug("processing lesson:{}", lessonId);
        JsonObject lessonEventBody = new JsonObject().put(EventRequestConstants.ID, lessonId);
        JsonObject lessonRequest = new JsonObject()
            .put(EventRequestConstants.EVENT_NAME, MessageConstants.MSG_OP_EVT_LESSON_CREATE)
            .put(EventRequestConstants.SESSION_TOKEN,
                request.getString(EventRequestConstants.SESSION_TOKEN))
            .put(EventRequestConstants.EVENT_BODY, lessonEventBody);
        JsonObject lessonResult = RepoBuilder
            .buildLessonRepo(
                createContext(MessageConstants.MSG_OP_EVT_LESSON_CREATE, lessonEventBody))
            .createUpdateLessonEvent();

        MessageDispatcher.getInstance().sendMessage2Kafka(EventResponseConstants.EVENT_ITEM_CREATE,
            ResponseFactory.generateItemCreateResponse(lessonRequest, lessonResult));

        LOGGER.debug("fetching collections");
        List<AJEntityCollection> collections =
            RepoBuilder.buildCollectionRepo(null).fetchCollectionsByCUL(courseId, unitId, lessonId);
        LOGGER.debug("collections found: {}", collections.size());
        collections.forEach(collection -> {
          String collectionId = collection.getString(AJEntityCollection.ID);
          LOGGER.debug("processing collection:{}", collectionId);
          String format = collection.getString(AJEntityCollection.FORMAT);

          String eventName = null;
          JsonObject collectionEventBody =
              new JsonObject().put(EventRequestConstants.ID, collectionId);
          JsonObject collectionResult = null;

          if (format.equalsIgnoreCase(AJEntityCollection.FORMAT_EX_COLLECTION)) {
            eventName = MessageConstants.MSG_OP_EVT_EX_COLLECTION_CREATE;
            collectionResult =
                RepoBuilder.buildCollectionRepo(createContext(eventName, collectionEventBody))
                    .createExtCollectionEvent();
          } else if (format.equalsIgnoreCase(AJEntityCollection.FORMAT_EX_ASSESSMENT)) {
            eventName = MessageConstants.MSG_OP_EVT_EX_ASSESSMENT_CREATE;
            collectionResult =
                RepoBuilder.buildCollectionRepo(createContext(eventName, collectionEventBody))
                    .createExtAssessmentEvent();
          } else if (format.equalsIgnoreCase(AJEntityCollection.FORMAT_COLLECTION)) {
            eventName = MessageConstants.MSG_OP_EVT_COLLECTION_CREATE;
            collectionResult =
                RepoBuilder.buildCollectionRepo(createContext(eventName, collectionEventBody))
                    .createUpdateCollectionEvent();
          } else if (format.equalsIgnoreCase(AJEntityCollection.FORMAT_ASSESSMENT)) {
            eventName = MessageConstants.MSG_OP_EVT_ASSESSMENT_CREATE;
            collectionResult =
                RepoBuilder.buildCollectionRepo(createContext(eventName, collectionEventBody))
                    .createUpdateAssessmentEvent();
          }

          JsonObject collectionRequest =
              new JsonObject().put(EventRequestConstants.EVENT_NAME, eventName)
                  .put(EventRequestConstants.SESSION_TOKEN,
                      request.getString(EventRequestConstants.SESSION_TOKEN))
                  .put(EventRequestConstants.EVENT_BODY, collectionEventBody);

          MessageDispatcher.getInstance().sendMessage2Kafka(
              EventResponseConstants.EVENT_ITEM_CREATE,
              ResponseFactory.generateItemCreateResponse(collectionRequest, collectionResult));
        });

      });
    });

  }
}
