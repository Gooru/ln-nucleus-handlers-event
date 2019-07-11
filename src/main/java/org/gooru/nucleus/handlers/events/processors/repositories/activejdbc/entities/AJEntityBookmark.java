package org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.List;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author szgooru Created On: 06-Jul-2017
 */
@Table("user_bookmarks")
public class AJEntityBookmark extends Model {
  private static final String ID = "id";
  private static final String CONTENT_ID = "content_id";
  private static final String USER_ID = "user_id";
  private static final String CONTENT_TYPE = "content_type";
  private static final String TITLE = "title";
  private static final String DESCRIPTION = "description";
  private static final String CREATED_AT = "created_at";
  private static final String UPDATED_AT = "updated_at";
  private static final String SEQUENCE_ID = "sequence_id";
  private static final String IS_DELETED = "is_deleted";

  public static final List<String> BOOKMARKS_FIELDS = Arrays.asList(ID, CONTENT_ID, USER_ID,
      CONTENT_TYPE, TITLE, DESCRIPTION, SEQUENCE_ID, IS_DELETED, CREATED_AT, UPDATED_AT);
}
