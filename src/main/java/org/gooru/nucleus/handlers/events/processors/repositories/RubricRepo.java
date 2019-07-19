package org.gooru.nucleus.handlers.events.processors.repositories;

import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 05-Jun-2017
 */
public interface RubricRepo {

  JsonObject createUpdateRubricEvent();

  JsonObject copyRubricEvent();

  JsonObject deleteRubricEvent();

  JsonObject associateRubricToQuestionEvent();
}
