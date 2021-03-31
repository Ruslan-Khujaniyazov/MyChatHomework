package chatServerDataObjects.dataObjects;

import java.io.Serializable;
import java.util.List;

public class ConnectedUsersList implements Serializable {
    private final List<String> connectedUsernamesList;

    public ConnectedUsersList(List<String> connectedUsernamesList) {
        this.connectedUsernamesList = connectedUsernamesList;
    }

    public List<String> getConnectedUsersList() {
        return connectedUsernamesList;
    }
}
