package org.gooru.nucleus.handlers.events.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.events.app.components.DataSourceRegistry;
import org.gooru.nucleus.handlers.events.constants.EventRequestConstants;
import org.gooru.nucleus.handlers.events.constants.EventResponseConstants;
import org.gooru.nucleus.handlers.events.processors.ProcessorContext;
import org.gooru.nucleus.handlers.events.processors.repositories.RubricRepo;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities.AJEntityContent;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities.AJEntityRubric;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 05-Jun-2017
 */
public class AJRubricRepo implements RubricRepo {

  private static final Logger LOGGER = LoggerFactory.getLogger(AJRubricRepo.class);
  private final ProcessorContext context;

  public AJRubricRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public JsonObject createUpdateRubricEvent() {
    String rubricId = context.eventBody().getString(EventRequestConstants.ID);
    return getRubric(rubricId);
  }

  @Override
  public JsonObject copyRubricEvent() {
    JsonObject response = new JsonObject();
    String targetContentId = context.eventBody().getString(EventRequestConstants.ID);
    JsonObject targetContent = getRubric(targetContentId);
    response.put(EventResponseConstants.TARGET, targetContent);

    String sourceContentId = targetContent.getString(AJEntityRubric.ORIGINAL_RUBRIC_ID);
    if (sourceContentId != null && !sourceContentId.isEmpty()) {
      JsonObject sourceContent = getRubric(sourceContentId);
      response.put(EventResponseConstants.SOURCE, sourceContent);
    }
    return response;
  }

  @Override
  public JsonObject deleteRubricEvent() {
    String rubricId = context.eventBody().getString(EventRequestConstants.ID);
    return getRubric(rubricId);
  }

  @Override
  public JsonObject associateRubricToQuestionEvent() {
    return context.eventBody();
  }

  private JsonObject getRubric(String rubricId) {
    Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
    LOGGER.debug("getting rubric for id {}", rubricId);

    JsonObject result = null;
    LazyList<AJEntityRubric> rubrics =
        AJEntityRubric.findBySQL(AJEntityRubric.FETCH_RUBRIC, rubricId);
    if (!rubrics.isEmpty()) {
      result = new JsonObject(new JsonFormatterBuilder()
          .buildSimpleJsonFormatter(false, AJEntityRubric.FETCH_RUBRIC_FIELDS)
          .toJson(rubrics.get(0)));
    }

    Base.close();
    return result;
  }

}
