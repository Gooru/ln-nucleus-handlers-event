package org.gooru.nucleus.handlers.events.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.events.processors.ProcessorContext;
import org.gooru.nucleus.handlers.events.processors.repositories.*;

/**
 * Created by subbu on 06-Jan-2016.
 */
public final class AJRepoBuilder {

  public static ContentRepo buildContentRepo(ProcessorContext context) {
    return new AJContentRepo(context);
  }

  public static CollectionRepo buildCollectionRepo(ProcessorContext context) {
    return new AJCollectionRepo(context);
  }

  public static CourseRepo buildCourseRepo(ProcessorContext context) {
    return new AJCourseRepo(context);
  }

  public static UnitRepo buildUnitRepo(ProcessorContext context) {
    return new AJUnitRepo(context);
  }

  public static LessonRepo buildLessonRepo(ProcessorContext context) {
    return new AJLessonRepo(context);
  }

  public static ClassRepo buildClassRepo(ProcessorContext context) {
    return new AJClassRepo(context);
  }

  public static UsersRepo buildUserRepo(ProcessorContext context) {
    return new AJUsersRepo(context);
  }

  public static ProfileRepo buildProfileRepo(ProcessorContext context) {
    return new AJProfileRepo(context);
  }

  public static RubricRepo buildRubricRepo(ProcessorContext context) {
    return new AJRubricRepo(context);
  }

  public static BookmarkRepo buildBookmarkRepo(ProcessorContext context) {
    return new AJBookmarkRepo(context);
  }

  private AJRepoBuilder() {
    throw new AssertionError();
  }

}
