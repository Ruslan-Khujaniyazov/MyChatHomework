package chatServerDataObjects.dataObjects;

import java.io.Serializable;

public class ChangeUsernameSuccessDataObject implements Serializable {
    private final String newUsername;

    public ChangeUsernameSuccessDataObject(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getNewUsername() {
        return newUsername;
    }
}
