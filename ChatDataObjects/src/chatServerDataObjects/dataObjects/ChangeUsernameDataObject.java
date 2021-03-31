package chatServerDataObjects.dataObjects;

import java.io.Serializable;

public class ChangeUsernameDataObject implements Serializable {
    private final String newUsername;

    public ChangeUsernameDataObject(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getNewUsername() {
        return newUsername;
    }
}
