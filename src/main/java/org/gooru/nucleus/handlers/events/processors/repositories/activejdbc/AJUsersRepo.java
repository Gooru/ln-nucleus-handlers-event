package org.gooru.nucleus.handlers.events.processors.repositories.activejdbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.gooru.nucleus.handlers.events.app.components.DataSourceRegistry;
import org.gooru.nucleus.handlers.events.constants.EventRequestConstants;
import org.gooru.nucleus.handlers.events.constants.MessageConstants;
import org.gooru.nucleus.handlers.events.processors.ProcessorContext;
import org.gooru.nucleus.handlers.events.processors.repositories.UsersRepo;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities.AJEntityUsers;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class AJUsersRepo implements UsersRepo {

    private final ProcessorContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(AJUsersRepo.class);

    public AJUsersRepo(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public List<String> getMultipleEmailIds(List<String> userIds) {
        Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
        LazyList<AJEntityUsers> emailIdsFromDB = AJEntityUsers
            .findBySQL(AJEntityUsers.SELECT_MULTIPLE_EMAILIDS, listToPostgresArrayString(userIds));
        List<String> emailIds = new ArrayList<>();
        emailIdsFromDB.forEach(email -> emailIds.add(email.getString(AJEntityUsers.EMAIL)));
        Base.close();
        return emailIds;
    }
    
    @Override
    public String getUsername(String userId) {
        Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
        String username = null;
        LazyList<AJEntityUsers> usernames = AJEntityUsers.findBySQL(AJEntityUsers.SELECT_USERNAME, userId);
        if (!usernames.isEmpty()) {
            username = usernames.get(0).getString(AJEntityUsers.USERNAME);
        }
        Base.close();
        return username;
    }
    
    @Override
    public String[] getFirstAndLastName(String userId) {
        Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
        String[] firstLastName = new String[2];
        LazyList<AJEntityUsers> firstLastNames = AJEntityUsers.findBySQL(AJEntityUsers.SELECT_FIRST_LAST_NAME, userId);
        if (!firstLastNames.isEmpty()) {
            firstLastName[0] = firstLastNames.get(0).getString(AJEntityUsers.FIRST_NAME);
            firstLastName[1] = firstLastNames.get(0).getString(AJEntityUsers.LAST_NAME);
        }
        Base.close();
        return firstLastName;
    }

    private String listToPostgresArrayString(List<String> input) {
        int approxSize = ((input.size() + 1) * 36); // Length of UUID is around
                                                    // 36
        // chars
        Iterator<String> it = input.iterator();
        if (!it.hasNext()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder(approxSize);
        sb.append('{');
        for (;;) {
            String s = it.next();
            sb.append('"').append(s).append('"');
            if (!it.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(',');
        }
    }

    @Override
    public JsonObject userSignin() {
        String userId = context.eventBody().getString(EventRequestConstants.ID);
        return getUser(userId);
    }

    @Override
    public JsonObject userSignup() {
        String userId = context.eventBody().getString(EventRequestConstants.ID);
        return getUser(userId);
    }

    @Override
    public JsonObject userSignout() {
        String userId = context.eventBody().getString(EventRequestConstants.ID);
        if (userId.equalsIgnoreCase(MessageConstants.ANONYMOUS_USER)) {
            return null;
        }
        return getUser(userId);
    }

    @Override
    public JsonObject userUpdate() {
        String userId = context.eventBody().getString(EventRequestConstants.ID);
        return getUser(userId);
    }

    @Override
    public JsonObject userResetPasswordTrigger() {
        String userId = context.eventBody().getString(EventRequestConstants.ID);
        return getUser(userId);
    }

    @Override
    public JsonObject userResetPassword() {
        String userId = context.eventBody().getString(EventRequestConstants.ID);
        return getUser(userId);
    }

    @Override
    public JsonObject userChangePassword() {
        // TODO Auto-generated method stub
        return null;
    }


    private JsonObject getUser(String id) {
        Base.open(DataSourceRegistry.getInstance().getDefaultDataSource());
        JsonObject result = null;
        try {
            AJEntityUsers user = AJEntityUsers.findById(UUID.fromString(id));
            if (user != null) {
                result = new JsonObject(
                    new JsonFormatterBuilder().buildSimpleJsonFormatter(false, AJEntityUsers.ALL_FIELDS).toJson(user));
            }
        } catch (IllegalArgumentException iae) {
            LOGGER.error("error while getting user from DB", iae);
            return result;
        } finally {
            Base.close();
        }
        return result;
    }
}
