package chatServerDataObjects.dataObjects;

import java.io.Serializable;

public class AddNewUserToListDataObject implements Serializable {
    private final String username;

    public AddNewUserToListDataObject(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
