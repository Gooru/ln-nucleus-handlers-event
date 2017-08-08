package org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.List;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("original_resource")
public class AJEntityOriginalResource extends Model {

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String URL = "url";
    public static final String PUBLIH_STATUS = "publish_status";
    public static final String METADATA = "metadata";
    public static final String TAXONOMY = "taxonomy";
    public static final String THUMBNAIL = "thumbnail";
    public static final String CREATOR_ID = "creator_id";

    public static final String PUBLISH_DATE = "publish_date";
    public static final String NARRATION = "narration";
    public static final String DESCRIPTION = "description";
    public static final String CONTENT_SUBFORMAT = "content_subformat";
    public static final String IS_COPYRIGHT_OWNER = "is_copyright_owner";
    public static final String COPYRIGHT_OWNER = "copyright_owner";
    public static final String RESOURCE_INFO = "info";
    public static final String VISIBLE_ON_PROFILE = "visible_on_profile";
    public static final String DISPLAY_GUIDE = "display_guide";
    public static final String ACCESSIBILITY = "accessibility";
    public static final String IS_DELETED = "is_deleted";
    public static final String MODIFIER_ID = "modifier_id";
    public static final String TENANT = "tenant";
    public static final String TENANT_ROOT = "tenant_root";

    public static final String SELECT_ORIGINAL_RESOURCE =
        "SELECT id, title, url, creator_id, modifier_id, publish_date, publish_status, narration, description, content_subformat, metadata,"
            + " taxonomy, thumbnail, is_copyright_owner, copyright_owner, info, visible_on_profile, display_guide, accessibility, is_deleted,"
            + " tenant, tenant_root FROM original_resource WHERE id = ?::uuid";

    public static final List<String> ORIGINAL_RESOURCE_FIELDS =
        Arrays.asList(ID, TITLE, URL, CREATOR_ID, MODIFIER_ID, PUBLISH_DATE, PUBLIH_STATUS, NARRATION, DESCRIPTION,
            CONTENT_SUBFORMAT, METADATA, TAXONOMY, THUMBNAIL, IS_COPYRIGHT_OWNER, COPYRIGHT_OWNER,
            RESOURCE_INFO, VISIBLE_ON_PROFILE, DISPLAY_GUIDE, ACCESSIBILITY, IS_DELETED, TENANT, TENANT_ROOT);
}
