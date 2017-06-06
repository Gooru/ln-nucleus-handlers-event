package org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.List;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author szgooru Created On: 05-Jun-2017
 */
@Table("rubric")
public class AJEntityRubric extends Model {

    public static final String ID = "id";
    public static final String ORIGINAL_RUBRIC_ID = "original_rubric_id";

    private static final String TITLE = "title";
    private static final String URL = "url";
    private static final String IS_REMOTE = "is_remote";
    private static final String DESCRIPTION = "description";
    private static final String CATEGORIES = "categories";
    private static final String FEEDBACK_GUIDANCE = "feedback_guidance";
    private static final String OVERALL_FEEDBACK_REQUIRED = "overall_feedback_required";
    private static final String CREATOR_ID = "creator_id";
    private static final String MODIFIER_ID = "modifier_id";
    private static final String ORIGINAL_CREATOR_ID = "original_creator_id";
    private static final String PARENT_RUBRIC_ID = "parent_rubric_id";
    private static final String PUBLISH_DATE = "publish_date";
    private static final String PUBLISH_STATUS = "publish_status";
    private static final String METADATA = "metadata";
    private static final String TAXONOMY = "taxonomy";
    private static final String GUT_CODES = "gut_codes";
    private static final String THUMBNAIL = "thumbnail";
    private static final String COURSE_ID = "course_id";
    private static final String UNIT_ID = "unit_id";
    private static final String LESSON_ID = "lesson_id";
    private static final String COLLECTION_ID = "collection_id";
    private static final String CONTENT_ID = "content_id";
    private static final String IS_RUBRIC = "is_rubric";
    private static final String SCORING = "scoring";
    private static final String MAX_SCORE = "max_score";
    private static final String INCREMENT = "increment";
    private static final String GRADER = "grader";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";
    private static final String TENANT = "tenant";
    private static final String TENANT_ROOT = "tenant_root";
    private static final String VISIBLE_ON_PROFILE = "visible_on_profile";
    private static final String IS_DELETED = "is_deleted";
    private static final String CREATOR_SYSTEM = "creator_system";

    public static final String FETCH_RUBRIC =
        "SELECT id, title, url, is_remote, description, categories, feedback_guidance, overall_feedback_required,"
            + " is_rubric, course_id, unit_id, lesson_id, collection_id, content_id, scoring, max_score, increment,"
            + " creator_id, modifier_id, original_creator_id, original_rubric_id, parent_rubric_id, publish_date, publish_status, metadata, taxonomy,"
            + " gut_codes, thumbnail, created_at, updated_at, tenant, tenant_root, visible_on_profile, is_deleted, creator_system FROM rubric"
            + " WHERE id = ?::uuid";

    public static final List<String> FETCH_RUBRIC_FIELDS = Arrays.asList(ID, TITLE, URL, IS_REMOTE, DESCRIPTION,
        IS_RUBRIC, GRADER, CATEGORIES, FEEDBACK_GUIDANCE, OVERALL_FEEDBACK_REQUIRED, CREATOR_ID, MODIFIER_ID,
        ORIGINAL_CREATOR_ID, ORIGINAL_RUBRIC_ID, PARENT_RUBRIC_ID, PUBLISH_DATE, PUBLISH_STATUS, METADATA, TAXONOMY,
        GUT_CODES, THUMBNAIL, COURSE_ID, UNIT_ID, LESSON_ID, COLLECTION_ID, CONTENT_ID, SCORING, MAX_SCORE, INCREMENT,
        GRADER, CREATED_AT, UPDATED_AT, TENANT, TENANT_ROOT, VISIBLE_ON_PROFILE, IS_DELETED, CREATOR_SYSTEM);

}
