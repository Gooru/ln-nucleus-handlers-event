package org.gooru.nucleus.handlers.events.processors.responseobject;

import org.gooru.nucleus.handlers.events.constants.EventResponseConstants;
import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 03-Mar-2017
 */
public class UserEventResponseObjectBuilder extends ResponseObject {

  public UserEventResponseObjectBuilder(JsonObject body, JsonObject response) {
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
    contextStructure.put(EventResponseConstants.CLIENT_SOURCE, populateClientSource());
    return contextStructure;
  }

  private JsonObject createPayLoadObjectStructure() {
    JsonObject payloadStructure = new JsonObject();
    payloadStructure.put(EventResponseConstants.DATA, this.response);
    payloadStructure.put(EventResponseConstants.SUB_EVENT_NAME, getSubEventName());
    return payloadStructure;
  }

  private String populateClientSource() {
    String subEvent = getSubEventName();
    if (subEvent.contains(EventResponseConstants.CLIENT_SOURCE_ROSTER)) {
      return EventResponseConstants.CLIENT_SOURCE_ROSTER;
    }

    return EventResponseConstants.CLIENT_SOURCE_COREAUTH;
  }
}
