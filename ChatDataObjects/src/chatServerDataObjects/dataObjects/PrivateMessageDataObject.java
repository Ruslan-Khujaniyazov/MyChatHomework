package chatServerDataObjects.dataObjects;

import java.io.Serializable;

public class PrivateMessageDataObject implements Serializable {
    private final String sender;
    private final String receiver;
    private final String message;


    public PrivateMessageDataObject(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }
}
