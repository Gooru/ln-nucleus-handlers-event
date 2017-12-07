package org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 06-Dec-2017
 */
@Table("user_state")
@IdName("user_id")
public class AJEntityUserState extends Model {

    public static final String USER_ID = "user_id";
    public static final String SYSTEM_STATE = "system_state";
    public static final String CLIENT_STATE = "client_state";

    public static final String WELCOME_EMAIL_SENT_STATE = "welcome.email.sent";

    public static final String UPDATE_WELCOME_EMAIL_STATE =
        "UPDATE user_state SET system_state = jsonb_set(system_state, '{welcome.email.sent}', 'true', false) WHERE user_id = ?::uuid";

    public JsonObject getSystemState() {
        String systemState = this.getString(SYSTEM_STATE);
        return systemState != null && !systemState.isEmpty() ? new JsonObject(systemState) : null;
    }
}