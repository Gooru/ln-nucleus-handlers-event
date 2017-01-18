package org.gooru.nucleus.handlers.events.processors.repositories;

import java.util.List;

public interface UsersRepo {

    List<String> getMultipleEmailIds(List<String> userIds);
    
    String getUsername(String userId);
    
    String[] getFirstAndLastName(String userId);
}
