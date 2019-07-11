package org.gooru.nucleus.handlers.events.processors.partners;

import org.gooru.nucleus.handlers.events.processors.ProcessorContext;
import io.vertx.core.json.JsonObject;

/**
 * @author gooru on 23-Apr-2018
 */
public abstract class ContentEventProcessor {

  public abstract void process();

  public ProcessorContext createContext(String eventName, JsonObject eventBody) {
    return new ProcessorContext(eventName, eventBody);
  }
}
