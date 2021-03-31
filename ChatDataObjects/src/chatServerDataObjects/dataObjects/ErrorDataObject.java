package chatServerDataObjects.dataObjects;

import java.io.Serializable;

public class ErrorDataObject implements Serializable {
    private final String errorMessage;

    public ErrorDataObject(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
