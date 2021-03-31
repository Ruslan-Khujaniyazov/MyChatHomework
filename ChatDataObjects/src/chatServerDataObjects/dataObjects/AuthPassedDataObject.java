package chatServerDataObjects.dataObjects;

import java.io.Serializable;

public class AuthPassedDataObject implements Serializable {
    private final String username;

    public AuthPassedDataObject(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
