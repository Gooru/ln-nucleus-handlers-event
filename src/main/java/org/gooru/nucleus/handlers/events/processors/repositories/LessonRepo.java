package org.gooru.nucleus.handlers.events.processors.repositories;

import java.util.List;
import io.vertx.core.json.JsonObject;

public interface LessonRepo {

  JsonObject createUpdateLessonEvent();

  JsonObject copyLessonEvent();

  JsonObject deleteLessonEvent();

  JsonObject moveLessonEvent();

  JsonObject reorderLessonContentEvent();

  JsonObject getLesson(String lessonId);

  List<String> fetchLessonsByCU(String courseId, String unitId);
}
