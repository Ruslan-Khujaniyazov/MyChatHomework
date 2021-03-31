package chatServerDataObjects.dataObjects;

import java.io.Serializable;

public class PublicMessageDataObject implements Serializable {
    private final String sender;
    private final String message;

    public PublicMessageDataObject(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
