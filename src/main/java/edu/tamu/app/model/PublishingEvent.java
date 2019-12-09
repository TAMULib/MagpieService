package edu.tamu.app.model;

import java.util.Date;

public class PublishingEvent {

    private final PublishingType type;

    private final String message;

    private final Date timestamp;

    public PublishingEvent(PublishingType type, String message) {
        this.type = type;
        this.message = message;
        timestamp =  new Date(System.currentTimeMillis());
    }

    public PublishingType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

}
