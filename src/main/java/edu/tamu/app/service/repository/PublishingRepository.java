package edu.tamu.app.service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import edu.tamu.app.model.PublishingEvent;
import edu.tamu.app.model.PublishingType;
import edu.tamu.weaver.response.ApiAction;
import edu.tamu.weaver.response.ApiResponse;
import edu.tamu.weaver.response.ApiStatus;

public abstract class PublishingRepository implements Repository {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    protected String getChannel() {
        return "/channel/publishing";
    }

    protected void broadcastDocument(Long documentId, PublishingType type, String message) {
        ApiResponse response = new ApiResponse(ApiStatus.SUCCESS, ApiAction.BROADCAST, new PublishingEvent(type, message));
        simpMessagingTemplate.convertAndSend(getChannel() + "/document/" + documentId, response);
    }

}
