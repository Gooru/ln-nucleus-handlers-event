package org.gooru.nucleus.handlers.events.processors.repositories;

import java.util.List;
import io.vertx.core.json.JsonObject;

public interface UsersRepo {

  List<String> getMultipleEmailIds(List<String> userIds);

  String getUsername(String userId);

  String[] getFirstAndLastName(String userId);

  JsonObject userSignin();

  JsonObject userSignup();

  JsonObject userSignout();

  JsonObject userUpdate();

  JsonObject userDelete();

  JsonObject userResetPasswordTrigger();

  JsonObject userResetPassword();

  JsonObject userChangePassword();
}
