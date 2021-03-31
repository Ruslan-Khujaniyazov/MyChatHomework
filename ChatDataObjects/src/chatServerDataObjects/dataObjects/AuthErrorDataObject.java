package chatServerDataObjects.dataObjects;

import java.io.Serializable;

public class AuthErrorDataObject implements Serializable {
    private final String errorMessage;

    public AuthErrorDataObject(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
