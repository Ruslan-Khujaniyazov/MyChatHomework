package chatServerDataObjects.dataObjects;

import java.io.Serializable;

public class ServerMessageDataObject implements Serializable {
    private final String message;

    public ServerMessageDataObject(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
