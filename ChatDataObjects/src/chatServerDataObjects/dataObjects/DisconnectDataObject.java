package chatServerDataObjects.dataObjects;

import java.io.Serializable;

public class DisconnectDataObject implements Serializable {
    private final String username;

    public DisconnectDataObject(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
