package org.gooru.nucleus.handlers.events.processors.responseobject;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.handlers.events.constants.EventRequestConstants;
import org.gooru.nucleus.handlers.events.constants.EventResponseConstants;

public class ItemCollaboratorUpdateResponseObjectBuilder extends ResponseObject {

  public ItemCollaboratorUpdateResponseObjectBuilder(JsonObject body, JsonObject response) {
    super(body, response);
  }

  public JsonObject build() {
    JsonObject eventStructure = createGenericStructure();
    eventStructure.put(EventResponseConstants.METRICS, createMetricsStructure());
    eventStructure.put(EventResponseConstants.SESSION, createSessionStructure());
    eventStructure.put(EventResponseConstants.USER, createUserStructure());
    eventStructure.put(EventResponseConstants.VERSION, createVersionStructure());
    eventStructure.put(EventResponseConstants.CONTEXT, createContextStructure());
    eventStructure.put(EventResponseConstants.PAYLOAD_OBJECT, createPayLoadObjectStructure());
    return eventStructure;
  }

  private JsonObject createContextStructure() {
    JsonObject contextStructure = new JsonObject();
    String contentId = this.body.getJsonObject(EventRequestConstants.EVENT_BODY)
        .getString(EventRequestConstants.ID);
    contextStructure.put(EventResponseConstants.CONTENT_GOORU_ID, contentId);
    contextStructure.put(EventResponseConstants.CLIENT_SOURCE, populateClientSource());
    return contextStructure;
  }

  private JsonObject createPayLoadObjectStructure() {
    JsonObject payloadStructure = new JsonObject();
    JsonArray collaboratorsFromResponse =
        response.getJsonArray(EventRequestConstants.COLLABORATORS);
    JsonObject collaborators = new JsonObject().put(EventRequestConstants.COLLABORATORS,
        (collaboratorsFromResponse != null && !collaboratorsFromResponse.isEmpty())
            ? collaboratorsFromResponse
            : new JsonArray());
    payloadStructure.put(EventResponseConstants.DATA, collaborators);
    payloadStructure.put(EventResponseConstants.CONTENT_FORMAT, getContentFormatFromResponse());
    payloadStructure.put(EventResponseConstants.SUB_EVENT_NAME, getSubEventName());
    return payloadStructure;
  }

  private String populateClientSource() {
    String subEvent = getSubEventName();
    if (subEvent.contains(EventResponseConstants.CLIENT_SOURCE_ROSTER)) {
      return EventResponseConstants.CLIENT_SOURCE_ROSTER;
    }

    return EventResponseConstants.CLIENT_SOURCE_CORE;
  }
}
