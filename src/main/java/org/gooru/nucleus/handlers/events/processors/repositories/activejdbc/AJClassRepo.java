package org.gooru.nucleus.handlers.events.processors.repositories.activejdbc;

import org.gooru.nucleus.handlers.events.processors.ProcessorContext;
import org.gooru.nucleus.handlers.events.processors.repositories.ClassRepo;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities.AJEntityClass;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class AJClassRepo implements ClassRepo {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(AJClassRepo.class);

  public AJClassRepo(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public JsonObject craeteUpdateClassEvent() {
    return getClassById();
  }

  @Override
  public JsonObject deleteClassEvent() {
    return getClassById();
  }

  @Override
  public JsonObject updateClassCollaboratorEvent() {
    return new JsonObject();
  }

  @Override
  public JsonObject joinClassEvent() {
    return new JsonObject();
  }

  @Override
  public JsonObject inviteStudentToClassEvent() {
    return new JsonObject();
  }

  @Override
  public JsonObject assignClassToCourseEvent() {
    return new JsonObject();
  }

  @Override
  public JsonObject classContentVisibleEvent() {
    return new JsonObject();
  }

  private JsonObject getClassById() {
    LazyList<AJEntityClass> classes = AJEntityClass.where(AJEntityClass.SELECT_QUERY, context.id());
    if (classes.isEmpty()) {
      LOGGER.warn("Not able to find class '{}'", this.context.id());
      return null;
    }

    return new JsonObject(new JsonFormatterBuilder().buildSimpleJsonFormatter(false, AJEntityClass.ALL_FIELDS).toJson(classes.get(0)));
  }

}