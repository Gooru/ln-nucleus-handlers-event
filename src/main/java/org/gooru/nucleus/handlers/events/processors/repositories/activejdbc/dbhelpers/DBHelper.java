package org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.dbhelpers;

import org.gooru.nucleus.handlers.events.app.components.DataSourceRegistry;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities.AJEntityUserState;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author szgooru Created On: 07-Dec-2017
 */
public final class DBHelper {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHelper.class);

    private DBHelper() {
        throw new AssertionError();
    }
    
    public static void updateWelcomeEmailState(String userId) {
        try {
            Base.open(DataSourceRegistry.getInstance().getDataSourceByName(DataSourceRegistry.WRITABLE_DATE_SOURCE));
            Base.exec(AJEntityUserState.UPDATE_WELCOME_EMAIL_STATE, userId);
        } catch (Throwable t) {
            LOGGER.error("unable to update welcome email state of user : {}", userId, t);
        } finally {
            Base.close();
        }
    }
}
