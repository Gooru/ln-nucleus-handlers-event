package org.gooru.nucleus.handlers.events.processors;

import org.gooru.nucleus.handlers.events.app.components.AppHttpClient;
import org.gooru.nucleus.handlers.events.constants.EmailConstants;
import org.gooru.nucleus.handlers.events.constants.EventResponseConstants;
import org.gooru.nucleus.handlers.events.constants.HttpConstants;
import org.gooru.nucleus.handlers.events.constants.MessageConstants;
import org.gooru.nucleus.handlers.events.emails.EmailDataBuilder;
import org.gooru.nucleus.handlers.events.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities.AJEntityUserState;
import org.gooru.nucleus.handlers.events.processors.repositories.activejdbc.entities.AJEntityUsers;
import org.gooru.nucleus.handlers.events.processors.utils.HttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class EmailProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailProcessor.class);

    private final JsonObject result;
    private final JsonObject message;
    private String eventName = null;

    public EmailProcessor(Vertx vertx, JsonObject config, JsonObject result, JsonObject message) {
        this.result = result;
        this.message = message;
    }

    @Override
    public JsonObject process() {
        JsonArray emailData = null;
        boolean isPostProcessingNeeded = false;
        try {
            if (!validatePayload()) {
                LOGGER.error("Invalid payload received from the result, can't process to send email");
                throw new InvalidRequestException();
            }
            JsonObject payloadObject = result.getJsonObject(EventResponseConstants.PAYLOAD_OBJECT);
            eventName = payloadObject.getString(EventResponseConstants.SUB_EVENT_NAME);
            if (eventName == null || eventName.isEmpty()) {
                LOGGER.info("No event name found in payload");
                return new JsonObject().put(EmailConstants.EMAIL_SENT, false).put(EmailConstants.STATUS,
                    EmailConstants.STATUS_SUCCESS);
            }

            switch (eventName) {
            case MessageConstants.MSG_OP_EVT_RESOURCE_DELETE:
                emailData = processEmailForResourceDelete();
                break;

            case MessageConstants.MSG_OP_EVT_COLLECTION_COLLABORATOR_UPDATE:
                emailData = processEmailForCollectionCollaboratorUpdate();
                break;

            case MessageConstants.MSG_OP_EVT_COURSE_COLLABORATOR_UPDATE:
                emailData = processEmailForCourseCollaboratorUpdate();
                break;

            case MessageConstants.MSG_OP_EVT_CLASS_COLLABORATOR_UPDATE:
                emailData = processEmailForClassCollaboratorUpate();
                break;

            case MessageConstants.MSG_OP_EVT_CLASS_STUDENT_INVITE:
                emailData = processEmailToInviteStudent();
                break;

            case MessageConstants.MSG_OP_EVT_PROFILE_FOLLOW:
                emailData = processEmailToFollowProfile();
                break;

            case MessageConstants.MSG_OP_EVT_USER_SIGNUP:
            case MessageConstants.MSG_OP_EVT_USER_UPDATE:
                emailData = processEmailForUserSignup();
                isPostProcessingNeeded = true;
                break;

            case MessageConstants.MSG_OP_EVT_USER_PASSWORD_RESET_TRIGGER:
                emailData = processEmailForResetPasswordTrigger();
                break;

            case MessageConstants.MSG_OP_EVT_USER_PASSWORD_RESET:
                emailData = processEmailForResetPassword();
                break;

            default:
                LOGGER.info("event {} does not require to send email", eventName);
                return new JsonObject().put(EmailConstants.EMAIL_SENT, false).put(EmailConstants.STATUS,
                    EmailConstants.STATUS_SUCCESS);
            }
        } catch (Throwable t) {
            LOGGER.error("Something wrong while processing email", t);
            return new JsonObject().put(EmailConstants.EMAIL_SENT, false).put(EmailConstants.STATUS,
                EmailConstants.STATUS_FAIL);
        }

        if (emailData == null || emailData.isEmpty()) {
            return new JsonObject().put(EmailConstants.EMAIL_SENT, false).put(EmailConstants.STATUS,
                EmailConstants.STATUS_SUCCESS);
        }

        AppHttpClient httpClient = AppHttpClient.getInstance();
        emailData.stream().forEach(data -> {
            HttpClientRequest emailRequest = httpClient.getEmailHttpClient().post(httpClient.emailEndpoint(), responseHandler -> {
                if (responseHandler.statusCode() == HttpConstants.HttpStatus.SUCCESS.getCode()) {
                    LOGGER.info("email sent to '{}' for event: {}", data, eventName);
                } else {
                    LOGGER.warn("email not sent for event {}, HttpStatusCode: {}, requestPayload: {}", eventName,
                        responseHandler.statusCode(), data.toString());
                }
            });

            // TODO: check for null payload
            emailRequest.putHeader(HttpConstants.HEADER_CONTENT_TYPE, HttpConstants.CONTENT_TYPE_JSON);
            emailRequest.putHeader(HttpConstants.HEADER_CONTENT_LENGTH,
                String.valueOf(data.toString().getBytes().length));
            emailRequest.putHeader(HttpConstants.HEADER_AUTH, getAuthorizationHeader());
            emailRequest.write(data.toString());
            emailRequest.end();
        });
        
        if (isPostProcessingNeeded && !emailData.isEmpty())
            postProcessing();

        LOGGER.debug("done with sending email.. returning");
        return new JsonObject().put(EmailConstants.EMAIL_SENT, true).put(EmailConstants.STATUS,
            EmailConstants.STATUS_SUCCESS);
    }
    
    private void postProcessing() {
        LOGGER.debug("need post processing");
        if (this.eventName.equalsIgnoreCase(MessageConstants.MSG_OP_EVT_USER_UPDATE)) {
            String userId = getUserId();
            HttpHelper.updateWelcomeEmailState(userId, getAuthorizationHeader());
        }
    }

    private JsonArray processEmailForResetPassword() {
        return new EmailDataBuilder().setEmailTemplate(EmailConstants.TEMPLATE_RESET_PASSWORD).setResultData(result)
            .setEventData(message).build();
    }

    private JsonArray processEmailForResetPasswordTrigger() {
        return new EmailDataBuilder().setEmailTemplate(EmailConstants.TEMPLATE_RESET_PASSWORD_TRG).setResultData(result)
            .setEventData(message).build();
    }

    private JsonArray processEmailForUserSignup() {
        String role = getRole();
        if (!checkWelcomeEmailSent() && role != null) {
            
            String template = null;
            if (role.equalsIgnoreCase(AJEntityUsers.ROLE_STUDENT)) {
                template = EmailConstants.TEMPLATE_USER_SIGNUP_STUDENT;
            } else if (role.equalsIgnoreCase(AJEntityUsers.ROLE_TEACHER)) {
                template = EmailConstants.TEMPLATE_USER_SIGNUP_TEACHER;
            } else {
                template = EmailConstants.TEMPLATE_USER_SIGNUP_OTHER;
            }
            LOGGER.debug("preparing to send welcome email");
            return new EmailDataBuilder().setEmailTemplate(template).setResultData(result).build();
        }
        return new JsonArray();
    }

    private JsonArray processEmailForResourceDelete() {
        return new EmailDataBuilder().setEmailTemplate(EmailConstants.TEMPLATE_RESOURCE_DELETE).setResultData(result)
            .build();
    }

    private JsonArray processEmailForCollectionCollaboratorUpdate() {
        return new EmailDataBuilder().setEmailTemplate(EmailConstants.TEMPLATE_COLLECTION_COLLABORATOR_INVITE)
            .setResultData(result).setEventData(message).build();
    }

    private JsonArray processEmailForCourseCollaboratorUpdate() {
        return new EmailDataBuilder().setEmailTemplate(EmailConstants.TEMPLATE_COURSE_COLLABORATOR_INVITE)
            .setResultData(result).setEventData(message).build();
    }

    private JsonArray processEmailForClassCollaboratorUpate() {
        return new EmailDataBuilder().setEmailTemplate(EmailConstants.TEMPLATE_CLASS_COTEACHER_INVITE)
            .setResultData(result).setEventData(message).build();
    }

    private JsonArray processEmailToInviteStudent() {
        // TODO: check class sharing and call email builder for open or
        // restricted class invite
        return new EmailDataBuilder().setEmailTemplate(EmailConstants.TEMPLATE_USER_INVITE_CLASS).setResultData(result)
            .build();
    }

    private JsonArray processEmailToFollowProfile() {
        return new EmailDataBuilder().setEmailTemplate(EmailConstants.TEMPLATE_PROFILE_FOLLOW).setResultData(result)
            .build();
    }

    private boolean validatePayload() {
        JsonObject payloadObject = result.getJsonObject(EventResponseConstants.PAYLOAD_OBJECT);
        if (payloadObject.isEmpty()) {
            LOGGER.warn("No payload found in event response");
            return false;
        }

        return true;
    }

    private String getAuthorizationHeader() {
        // TODO: check for null session
        JsonObject session = this.result.getJsonObject(EventResponseConstants.SESSION);
        return "Token " + session.getString(EventResponseConstants.SESSION_TOKEN);
    }
    
    private String getUserId() {
        JsonObject payloadObject = result.getJsonObject(EventResponseConstants.PAYLOAD_OBJECT);
        if (payloadObject == null || payloadObject.isEmpty()) {
            return null;
        }
        
        JsonObject data = payloadObject.getJsonObject(EventResponseConstants.DATA);
        return data != null && !data.isEmpty() ? data.getString(AJEntityUsers.ID) : null;
    }
    
    private String getRole() {
        JsonObject payloadObject = result.getJsonObject(EventResponseConstants.PAYLOAD_OBJECT);
        if (payloadObject == null || payloadObject.isEmpty()) {
            return null;
        }
        
        JsonObject data = payloadObject.getJsonObject(EventResponseConstants.DATA);
        return data != null && !data.isEmpty() ? data.getString(AJEntityUsers.USER_CATEGORY) : null; 
    }
    
    private boolean checkWelcomeEmailSent() {
        JsonObject payloadObject = result.getJsonObject(EventResponseConstants.PAYLOAD_OBJECT);
        if (payloadObject == null || payloadObject.isEmpty()) {
            return true;
        }

        JsonObject data = payloadObject.getJsonObject(EventResponseConstants.DATA);
        return data != null && !data.isEmpty()
            ? data.getBoolean(AJEntityUserState.WELCOME_EMAIL_SENT_KEY, true) : true;
    }
}
