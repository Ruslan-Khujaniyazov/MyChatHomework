package chatServerDataObjects;

import chatServerDataObjects.dataObjects.*;

import java.io.Serializable;
import java.util.List;

public class DataObject implements Serializable {
    private DataObjectTypes type;
    private Object data;

    public DataObject(DataObjectTypes type) {
        this.type = type;
    }

    public DataObjectTypes getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public static DataObject authData(String login, String password) {
        DataObject dataObject = new DataObject(DataObjectTypes.AUTH);
        dataObject.data = new AuthDataObject(login, password);
        return dataObject;
    }

    public static DataObject authErrorData(String errorMessage) {
        DataObject dataObject = new DataObject(DataObjectTypes.AUTH_ERROR);
        dataObject.data = new AuthErrorDataObject(errorMessage);
        return dataObject;
    }

    public static DataObject authPassedData(String username) {
        DataObject dataObject = new DataObject(DataObjectTypes.AUTH_PASSED);
        dataObject.data = new AuthPassedDataObject(username);
        return dataObject;
    }

    public static DataObject errorData(String errorMessage) {
        DataObject dataObject = new DataObject(DataObjectTypes.ERROR);
        dataObject.data = new ErrorDataObject(errorMessage);
        return dataObject;
    }

    public static DataObject connectedUsersList(List<String> connectedUsernamesList) {
        DataObject dataObject = new DataObject(DataObjectTypes.CONNECTED_USERS_LIST);
        dataObject.data = new ConnectedUsersList(connectedUsernamesList);
        return dataObject;
    }

    public static DataObject serverMessageData(String message) {
        DataObject dataObject = new DataObject(DataObjectTypes.SERVER_MSG);
        dataObject.data = new ServerMessageDataObject(message);
        return dataObject;
    }

    public static DataObject privateMessageData(String sender, String receiver, String message) {
        DataObject dataObject = new DataObject(DataObjectTypes.PRIVATE_MSG);
        dataObject.data = new PrivateMessageDataObject(sender, receiver, message);
        return dataObject;
    }

    public static DataObject publicMessageData(String sender, String message) {
        DataObject dataObject = new DataObject(DataObjectTypes.PUBLIC_MSG);
        dataObject.data = new PublicMessageDataObject(sender, message);
        return dataObject;
    }

    public static DataObject addNewUserToListData(String username) {
        DataObject dataObject = new DataObject(DataObjectTypes.ADD_NEW_USER_TO_LIST);
        dataObject.data = new AddNewUserToListDataObject(username);
        return dataObject;
    }

    public static DataObject removeUserFromListData(String username) {
        DataObject dataObject = new DataObject(DataObjectTypes.REMOVE_USER_FROM_LIST);
        dataObject.data = new RemoveUserFromListDataObject(username);
        return dataObject;
    }

    public static DataObject disconnectData(String username) {
        DataObject dataObject = new DataObject(DataObjectTypes.DISCONNECT);
        dataObject.data = new DisconnectDataObject(username);
        return dataObject;
    }

    public static DataObject changeUsernameData(String newUsername) {
        DataObject dataObject = new DataObject(DataObjectTypes.CHANGE_USERNAME);
        dataObject.data = new ChangeUsernameDataObject(newUsername);
        return dataObject;
    }

    public static DataObject changeUsernameSuccessData(String newUsername) {
        DataObject dataObject = new DataObject(DataObjectTypes.CHANGE_USERNAME_SUCCESS);
        dataObject.data = new ChangeUsernameSuccessDataObject(newUsername);
        return dataObject;
    }

    public static DataObject changeUsernameErrorData(String errorMessage) {
        DataObject dataObject = new DataObject(DataObjectTypes.CHANGE_USERNAME_ERROR);
        dataObject.data = new ChangeUsernameErrorDataObject(errorMessage);
        return dataObject;
    }

}
