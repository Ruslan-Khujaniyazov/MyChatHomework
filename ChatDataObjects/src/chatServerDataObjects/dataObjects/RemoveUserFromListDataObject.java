package chatServerDataObjects.dataObjects;

import java.io.Serializable;

public class RemoveUserFromListDataObject implements Serializable {
    private final String username;

    public RemoveUserFromListDataObject(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
