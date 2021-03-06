package org.gooru.nucleus.handlers.events.processors.repositories.activejdbc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.gooru.nucleus.handlers.events.app.components.DataSourceRegistry;
import org.gooru.nucleus.handlers.events.constants.EventRequestConstants;
import org.gooru.nucleus.handlers.events.constants.EventResponseConstants;
import org.gooru.nucleus.handlers.events.processors.ProcessorContext;
import org.gooru.nucleus.handlers.events.processors.repositories.CollectionRepo;
import org.gooru.nucleus.handlers.events.processors.repositories.RepoBuilder;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities.AJEntityCollection;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by Subbu on 12-Jan-2016.
 */
public class AJCollectionRepo implements CollectionRepo {

  private static final Logger LOGGER = LoggerFactory.getLogger(AJCollectionRepo.class);
  private final ProcessorContext context;

  public AJCollectionRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public JsonObject createUpdateCollectionEvent() {
    String contentId = context.eventBody().getString(EventRequestConstants.ID);
    return getCollection(contentId, AJEntityCollection.FORMAT_COLLECTION);
  }

  @Override
  public JsonObject copyCollectionEvent() {
    JsonObject response = new JsonObject();
    String targetContentId = context.eventBody().getString(EventRequestConstants.ID);
    JsonObject targetContent = getCollection(targetContentId, AJEntityCollection.FORMAT_COLLECTION);
    response.put(EventResponseConstants.TARGET, targetContent);

    String sourceContentId = targetContent.getString(AJEntityCollection.ORIGINAL_COLLECTION_ID);
    if (sourceContentId != null && !sourceContentId.isEmpty()) {
      JsonObject sourceContent =
          getCollection(sourceContentId, AJEntityCollection.FORMAT_COLLECTION);
      response.put(EventResponseConstants.SOURCE, sourceContent);
    }
    return response;
  }

  @Override
  public JsonObject deleteCollectionEvent() {
    String contentId = context.eventBody().getString(EventRequestConstants.ID);
    return getCollection(contentId, AJEntityCollection.FORMAT_COLLECTION);
  }

  @Override
  public JsonObject reorderCollectionContentEvent() {
    return new JsonObject();
  }

  @Override
  public JsonObject addContentToCollectionEvent() {
    String contentId = context.eventBody().getString(EventRequestConstants.ID);
    return getCollection(contentId, AJEntityCollection.FORMAT_COLLECTION);
  }

  @Override
  public JsonObject updateCollectionCollaboratorEvent() {
    try {
      Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
      String contentId = context.eventBody().getString(EventRequestConstants.ID);
      JsonObject result = context.eventBody();
      LazyList<AJEntityCollection> collections =
          AJEntityCollection.findBySQL(AJEntityCollection.SELECT_COLLABORATOR, contentId);
      if (!collections.isEmpty()) {
        String collaborators = collections.get(0).getString(AJEntityCollection.COLLABORATOR);
        if (collaborators != null && !collaborators.isEmpty()) {
          result.put(EventRequestConstants.COLLABORATORS, new JsonArray(collaborators));
        }
      }
      return result;
    } catch (Throwable t) {
      LOGGER.error("error while getting the data from database:", t);
      return null;
    } finally {
      Base.close();
    }
  }

  @Override
  public JsonObject moveCollectionEvent() {
    JsonObject response = new JsonObject();
    JsonObject target = context.eventBody().getJsonObject(EventRequestConstants.TARGET);
    if (target == null || target.isEmpty()) {
      LOGGER.error("no target exists in move collection event");
      return response;
    }

    String targetLessonId = target.getString(EventRequestConstants.LESSON_ID);
    JsonObject targetLesson = RepoBuilder.buildLessonRepo(null).getLesson(targetLessonId);
    response.put(EventResponseConstants.TARGET, targetLesson);

    JsonObject source = context.eventBody().getJsonObject(EventRequestConstants.SOURCE);
    if (source == null || source.isEmpty()) {
      LOGGER.error("no source exists in move collection event");
      return response;
    }

    String sourceLessonId = source.getString(EventRequestConstants.LESSON_ID);
    JsonObject sourceLesson = RepoBuilder.buildLessonRepo(null).getLesson(sourceLessonId);
    response.put(EventResponseConstants.SOURCE, sourceLesson);
    return response;
  }

  @Override
  public JsonObject createUpdateAssessmentEvent() {
    String contentId = context.eventBody().getString(EventRequestConstants.ID);
    return getAssessment(contentId, AJEntityCollection.FORMAT_ASSESSMENT);
  }

  @Override
  public JsonObject copyAssessmentEvent() {
    JsonObject response = new JsonObject();
    String targetContentId = context.eventBody().getString(EventRequestConstants.ID);
    JsonObject targetContent = getAssessment(targetContentId, AJEntityCollection.FORMAT_ASSESSMENT);
    response.put(EventResponseConstants.TARGET, targetContent);

    String sourceContentId = targetContent.getString(AJEntityCollection.ORIGINAL_COLLECTION_ID);
    if (sourceContentId != null && !sourceContentId.isEmpty()) {
      JsonObject sourceContent =
          getAssessment(sourceContentId, AJEntityCollection.FORMAT_ASSESSMENT);
      response.put(EventResponseConstants.SOURCE, sourceContent);
    }
    return response;
  }

  @Override
  public JsonObject deleteAssessmentEvent() {
    String contentId = context.eventBody().getString(EventRequestConstants.ID);
    return getAssessment(contentId, AJEntityCollection.FORMAT_ASSESSMENT);
  }

  @Override
  public JsonObject addQuestionToAssessmentEvent() {
    String contentId = context.eventBody().getString(EventRequestConstants.ID);
    return getAssessment(contentId, AJEntityCollection.FORMAT_ASSESSMENT);
  }

  @Override
  public JsonObject reorderAssessmentContentEvent() {
    return new JsonObject();
  }

  @Override
  public JsonObject updateAssessmentCollaboratorEvent() {
    try {
      Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
      String contentId = context.eventBody().getString(EventRequestConstants.ID);
      JsonObject result = context.eventBody();
      LazyList<AJEntityCollection> collections =
          AJEntityCollection.findBySQL(AJEntityCollection.SELECT_COLLABORATOR, contentId);
      if (!collections.isEmpty()) {
        result.put(EventRequestConstants.COLLABORATORS,
            new JsonArray(collections.get(0).getString(AJEntityCollection.COLLABORATOR)));
      }
      return result;
    } catch (Throwable t) {
      LOGGER.error("error while getting the data from database:", t);
      return null;
    } finally {
      Base.close();
    }
  }

  @Override
  public JsonObject getCollection(String contentId, String format) {
    try {
      Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
      LOGGER.debug("getting collection for id {}", contentId);

      JsonObject result = null;
      LazyList<AJEntityCollection> collections =
          AJEntityCollection.findBySQL(AJEntityCollection.SELECT_COLLECTION, contentId, format);
      LOGGER.debug("number of collections found {}", collections.size());
      if (!collections.isEmpty()) {
        result = new JsonObject(new JsonFormatterBuilder()
            .buildSimpleJsonFormatter(false, AJEntityCollection.COLLECTION_FIELDS)
            .toJson(collections.get(0)));
      }
      return result;
    } catch (Throwable t) {
      LOGGER.error("error while getting the data from database:", t);
      return null;
    } finally {
      Base.close();
    }
  }

  private JsonObject getAssessment(String contentId, String format) {
    try {
      Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
      LOGGER.debug("getting assessment for id {}", contentId);

      JsonObject result = null;
      LazyList<AJEntityCollection> assessments =
          AJEntityCollection.findBySQL(AJEntityCollection.SELECT_ASSESSMENT, contentId, format);
      if (!assessments.isEmpty()) {
        result = new JsonObject(new JsonFormatterBuilder()
            .buildSimpleJsonFormatter(false, AJEntityCollection.ASSESSMENT_FIELDS)
            .toJson(assessments.get(0)));
      }

      return result;
    } catch (Throwable t) {
      LOGGER.error("error while getting the data from database:", t);
      return null;
    } finally {
      Base.close();
    }
  }

  @Override
  public List<String> getOwnerAndCreatorIds(JsonArray refCollectionIds) {
    try {
      Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
      Set<String> uniqueOwnerCreatorIds = new HashSet<>();

      LazyList<AJEntityCollection> ownerCreatorIdsFromDB = AJEntityCollection.findBySQL(
          AJEntityCollection.SELECT_OWNER_CREATOR, toPostgresArrayString(refCollectionIds));
      ownerCreatorIdsFromDB.stream().forEach(collection -> {
        uniqueOwnerCreatorIds.add(collection.getString(AJEntityCollection.OWNER_ID));
      });

      List<String> ownerCreatorIds = new ArrayList<>();
      ownerCreatorIds.addAll(uniqueOwnerCreatorIds);
      return ownerCreatorIds;
    } catch (Throwable t) {
      LOGGER.error("error while getting the data from database:", t);
      return null;
    } finally {
      Base.close();
    }
  }

  private String toPostgresArrayString(JsonArray input) {
    int approxSize = ((input.size() + 1) * 36); // Length of UUID is around
    // 36
    // chars
    if (input.isEmpty()) {
      return "{}";
    }

    StringBuilder sb = new StringBuilder(approxSize);
    sb.append('{');
    int cnt = 0;
    for (;;) {
      String s = input.getString(cnt);
      sb.append('"').append(s).append('"');
      if (cnt == input.size() - 1) {
        return sb.append('}').toString();
      }
      sb.append(',');
      cnt = cnt + 1;
    }
  }

  @Override
  public JsonObject removeCollection() {
    try {
      Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
      String contentId = context.eventBody().getString(EventRequestConstants.ID);
      LOGGER.debug("getting collection/assessment for id {}", contentId);

      JsonObject result = null;
      LazyList<AJEntityCollection> assessments =
          AJEntityCollection.findBySQL(AJEntityCollection.SELECT_QUERY, contentId);
      if (!assessments.isEmpty()) {
        result = new JsonObject(new JsonFormatterBuilder()
            .buildSimpleJsonFormatter(false, AJEntityCollection.ASSESSMENT_FIELDS)
            .toJson(assessments.get(0)));
      }

      return result;
    } catch (Throwable t) {
      LOGGER.error("error while getting the data from database:", t);
      return null;
    } finally {
      Base.close();
    }
  }

  @Override
  public List<AJEntityCollection> fetchCollectionsByCUL(String courseId, String unitId,
      String lessonId) {
    try {
      Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
      LazyList<AJEntityCollection> collections = AJEntityCollection
          .findBySQL(AJEntityCollection.SELECT_COLLECTION_BY_CUL, courseId, unitId, lessonId);
      LOGGER.debug("collections:{}", collections.size());
      return collections;
    } catch (Throwable t) {
      LOGGER.error("error while getting the data from database:", t);
      return null;
    } finally {
      Base.close();
    }
  }

  @Override
  public JsonObject createExtCollectionEvent() {
    String contentId = context.eventBody().getString(EventRequestConstants.ID);
    return getCollection(contentId, AJEntityCollection.FORMAT_EX_COLLECTION);
  }

  @Override
  public JsonObject deleteExtCollectionEvent() {
    String contentId = context.eventBody().getString(EventRequestConstants.ID);
    return getCollection(contentId, AJEntityCollection.FORMAT_EX_COLLECTION);
  }

  @Override
  public JsonObject createExtAssessmentEvent() {
    String contentId = context.eventBody().getString(EventRequestConstants.ID);
    return getAssessment(contentId, AJEntityCollection.FORMAT_EX_ASSESSMENT);
  }

  @Override
  public JsonObject deleteExtAssessmentEvent() {
    String contentId = context.eventBody().getString(EventRequestConstants.ID);
    return getAssessment(contentId, AJEntityCollection.FORMAT_EX_ASSESSMENT);
  }

  @Override
  public JsonObject createOfflineActivityEvent() {
    String contentId = context.eventBody().getString(EventRequestConstants.ID);
    return getOfflineActivity(contentId, AJEntityCollection.FORMAT_OFFLINE_ACTIVITY);
  }

  @Override
  public JsonObject deleteOfflineActivityEvent() {
    String contentId = context.eventBody().getString(EventRequestConstants.ID);
    return getOfflineActivity(contentId, AJEntityCollection.FORMAT_OFFLINE_ACTIVITY);
  }

  private JsonObject getOfflineActivity(String contentId, String format) {
    try {
      Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
      LOGGER.debug("getting offline activity for id {}", contentId);

      JsonObject result = null;
      LazyList<AJEntityCollection> assessments = AJEntityCollection
          .findBySQL(AJEntityCollection.SELECT_OFFLINE_ACTIVITY, contentId, format);
      if (!assessments.isEmpty()) {
        result = new JsonObject(new JsonFormatterBuilder()
            .buildSimpleJsonFormatter(false, AJEntityCollection.OFFLINE_ACTIVITY_FIELDS)
            .toJson(assessments.get(0)));
      }

      return result;
    } catch (Throwable t) {
      LOGGER.error("error while getting the data from database:", t);
      return null;
    } finally {
      Base.close();
    }
  }

  @Override
  public JsonObject copyOfflineActivityEvent() {
    JsonObject response = new JsonObject();
    String targetContentId = context.eventBody().getString(EventRequestConstants.ID);
    JsonObject targetContent =
        getOfflineActivity(targetContentId, AJEntityCollection.FORMAT_OFFLINE_ACTIVITY);
    response.put(EventResponseConstants.TARGET, targetContent);

    String sourceContentId = targetContent.getString(AJEntityCollection.ORIGINAL_COLLECTION_ID);
    if (sourceContentId != null && !sourceContentId.isEmpty()) {
      JsonObject sourceContent =
          getOfflineActivity(sourceContentId, AJEntityCollection.FORMAT_OFFLINE_ACTIVITY);
      response.put(EventResponseConstants.SOURCE, sourceContent);
    }
    return response;
  }
}
