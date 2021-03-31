package chatServerDataObjects.dataObjects;

import java.io.Serializable;

public class ChangeUsernameErrorDataObject  implements Serializable {
    private final String errorMessage;

    public ChangeUsernameErrorDataObject(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
