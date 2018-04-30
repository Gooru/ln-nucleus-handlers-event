package org.gooru.nucleus.handlers.events.processors.partners;

import org.gooru.nucleus.handlers.events.constants.EventRequestConstants;
import org.gooru.nucleus.handlers.events.constants.EventResponseConstants;
import org.gooru.nucleus.handlers.events.constants.MessageConstants;
import org.gooru.nucleus.handlers.events.processors.MessageDispatcher;
import org.gooru.nucleus.handlers.events.processors.repositories.RepoBuilder;
import org.gooru.nucleus.handlers.events.processors.responseobject.ResponseFactory;

import io.vertx.core.json.JsonObject;

/**
 * @author gooru on 23-Apr-2018
 */
public class ContentDeletionEventProcessor extends ContentEventProcessor {

	private final JsonObject request;

	public ContentDeletionEventProcessor(JsonObject request) {
		this.request = request;
	}

	@Override
	public void process() {
		JsonObject eventBody = request.getJsonObject(EventRequestConstants.EVENT_BODY);
		String courseId = eventBody.getString(EventRequestConstants.ID);
		JsonObject courseEventBody = new JsonObject().put(EventRequestConstants.ID, courseId);
		JsonObject courseRequest = new JsonObject()
				.put(EventRequestConstants.EVENT_NAME, MessageConstants.MSG_OP_EVT_COURSE_DELETE)
				.put(EventRequestConstants.SESSION_TOKEN, request.getString(EventRequestConstants.SESSION_TOKEN))
				.put(EventRequestConstants.EVENT_BODY, courseEventBody);
		JsonObject courseResult = RepoBuilder
				.buildCourseRepo(createContext(MessageConstants.MSG_OP_EVT_COURSE_DELETE, eventBody))
				.deleteCourseEvent();

		JsonObject courseEvent = ResponseFactory.generateItemCreateResponse(courseRequest, courseResult);
		MessageDispatcher.getInstance().sendMessage2Kafka(EventResponseConstants.EVENT_ITEM_DELETE, courseEvent);
	}
}
