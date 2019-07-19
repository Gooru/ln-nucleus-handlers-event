package org.gooru.nucleus.handlers.events.processors;

import org.gooru.nucleus.handlers.events.constants.EventRequestConstants;
import org.gooru.nucleus.handlers.events.constants.MessageConstants;
import org.gooru.nucleus.handlers.events.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.handlers.events.processors.partners.ContentCreationEventProcessor;
import org.gooru.nucleus.handlers.events.processors.partners.ContentDeletionEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.core.json.JsonObject;

/**
 * @author gooru on 05-Apr-2018
 */
public class PartnerEventProcessor implements Processor {

  private final static Logger LOGGER = LoggerFactory.getLogger(PartnerEventProcessor.class);

  private final JsonObject request;

  public PartnerEventProcessor(JsonObject request) {
    this.request = request;
  }

  @Override
  public JsonObject process() {
    final String msgOp = request.getString(EventRequestConstants.EVENT_NAME);

    switch (msgOp) {
      case MessageConstants.MSG_OP_EVT_PARTNER_CONTENT_CREATE:
        processContentCreationEvent();
        break;

      case MessageConstants.MSG_OP_EVT_PARTNER_CONTENT_DELETE:
        processContentDeletionEvent();
        break;

      default:
        LOGGER.error("Invalid partner event, unable to handle");
        throw new InvalidRequestException();
    }
    return null;
  }

  private void processContentCreationEvent() {
    new ContentCreationEventProcessor(this.request).process();

  }

  private void processContentDeletionEvent() {
    new ContentDeletionEventProcessor(request).process();
  }
}
