package client.models;

import chatServerDataObjects.DataObject;
import chatServerDataObjects.dataObjects.*;
import client.NetworkClient;
import client.controllers.ChangeUsernameController;
import client.controllers.ChatController;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

public class Network {

    private static final int SERVER_PORT = 8189;
    private static final String SERVER_HOST = "localhost";

    private final int port;

    private final String host;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;
    private String username;

    private ChangeUsernameController changeUsernameController;

    public Network() {
        this(SERVER_HOST, SERVER_PORT);
    }

    public Network(String serverHost, int serverPort) {
        this.host = serverHost;
        this.port = serverPort;
    }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            return true;
        } catch (IOException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
            return false;
        }
    }

    public boolean isConnected() {
        if (socket.isClosed()) {
            return false;
        } else {
            return true;
        }
    }

    public void closeConnection() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public String sendAuthCommand(String login, String password) {
        try {
            DataObject authData = DataObject.authData(login, password);
            try {
                out.writeObject(authData);
            } catch (SocketException e) {
                return "Authentication time out!";
            }

            DataObject dataObject = readData();
            if (dataObject == null) {
                return "Error reading data from server...";
            }

            switch (dataObject.getType()) {
                case AUTH_PASSED: {
                    AuthPassedDataObject authPassedData = (AuthPassedDataObject) dataObject.getData();
                    this.username = authPassedData.getUsername();
                    return null;
                }
                case ERROR: {
                    ErrorDataObject ErrorData = (ErrorDataObject) dataObject.getData();
                    return ErrorData.getErrorMessage();
                }
                case AUTH_ERROR: {
                    AuthErrorDataObject authErrorData = (AuthErrorDataObject) dataObject.getData();
                    return authErrorData.getErrorMessage();
                }
                default: {
                    return "Unknown object received: " + dataObject.getType();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public void waitMessage(ChatController chatController) {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    DataObject dataObject = readData();

                    if (dataObject == null) {
                        NetworkClient.showErrorMessage("Error!", "Server error", "Invalid data received");
                        continue;
                    }

                    switch (dataObject.getType()) {
                        //принятие списка подключенных пользователей, при подключении данного нового пользователя
                        case CONNECTED_USERS_LIST: {
                            ConnectedUsersList connectedUsersList = (ConnectedUsersList) dataObject.getData();
                            List<String> usersList = connectedUsersList.getConnectedUsersList();
                            Platform.runLater(() -> ChatController.getConnectedUsersList().addAll(usersList));
                            break;
                        }
                        case SERVER_MSG: {
                            ServerMessageDataObject serverMessageData = (ServerMessageDataObject) dataObject.getData();
                            String serverMessage = serverMessageData.getMessage();
                            String formattedServerMessage = String.format("SERVER_NOTIFICATION: %s", serverMessage);
                            Platform.runLater(() -> chatController.appendMessage(formattedServerMessage));
                            break;
                        }
                        case ERROR: {
                            ErrorDataObject errorData = (ErrorDataObject) dataObject.getData();
                            String errorMessage = errorData.getErrorMessage();
                            Platform.runLater(() -> NetworkClient.showErrorMessage("Error!", "Server error!", errorMessage));
                            break;
                        }
                        case PRIVATE_MSG: {
                            PrivateMessageDataObject privateMessageData = (PrivateMessageDataObject) dataObject.getData();
                            String sender = privateMessageData.getSender();
                            String receiver = privateMessageData.getReceiver();
                            String message = privateMessageData.getMessage();

                            if (sender.equals(username)) {
                                String privateMessageTo = String.format("You to @%s: %s", receiver, message);
                                Platform.runLater(() -> chatController.appendMessage(privateMessageTo));

                            } else if (receiver.equals(username)) {
                                String privateMessageFrom = String.format("%s to You: %s", sender, message);
                                Platform.runLater(() -> chatController.appendMessage(privateMessageFrom));
                            }
                            break;
                        }
                        case PUBLIC_MSG: {
                            PublicMessageDataObject publicMessageData = (PublicMessageDataObject) dataObject.getData();
                            String sender = publicMessageData.getSender();
                            String message = publicMessageData.getMessage();

                            if (sender.equals(username)) {
                                String publicMessage = String.format("You: %s", message);
                                Platform.runLater(() -> chatController.appendMessage(publicMessage));

                            } else {
                                String publicMessage = String.format("%s: %s", sender, message);
                                Platform.runLater(() -> chatController.appendMessage(publicMessage));

                            }
                            break;
                        }
                        case ADD_NEW_USER_TO_LIST: {
                            AddNewUserToListDataObject addNewUserToListData = (AddNewUserToListDataObject) dataObject.getData();
                            String username = addNewUserToListData.getUsername();
                            Platform.runLater(() -> ChatController.getConnectedUsersList().add(username));
                            break;
                        }
                        case REMOVE_USER_FROM_LIST: {
                            RemoveUserFromListDataObject removeUserFromListData = (RemoveUserFromListDataObject) dataObject.getData();
                            String username = removeUserFromListData.getUsername();
                            Platform.runLater(() -> ChatController.getConnectedUsersList().remove(username));
                            break;
                        }
                        case CHANGE_USERNAME_SUCCESS: {
                            ChangeUsernameSuccessDataObject changeUsernameSuccessData = (ChangeUsernameSuccessDataObject) dataObject.getData();
                            Platform.runLater(() -> {
                                changeUsernameController.successNotification("Your nickname successfully changed!", true);
                                chatController.setLabel(changeUsernameSuccessData.getNewUsername());
                            });
                            break;
                        }
                        case CHANGE_USERNAME_ERROR: {
                            ChangeUsernameErrorDataObject changeUsernameErrorData = (ChangeUsernameErrorDataObject) dataObject.getData();
                            Platform.runLater(() ->changeUsernameController.errorNotification(changeUsernameErrorData.getErrorMessage(), true));
                            break;
                        }
                        case DISCONNECT: {
                            this.closeConnection();
                            break;
                        }
                        default: {
                            Platform.runLater(() -> NetworkClient.showErrorMessage("Error!", "Unknown command from server...", dataObject.getType().toString()));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                NetworkClient.showErrorMessage("Connection error", "Connection lost!", e.getMessage());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void changeUsername(String newUsername) {
        DataObject changeUsernameData = DataObject.changeUsernameData(newUsername);
        try {
            out.writeObject(changeUsernameData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DataObject readData() throws IOException {
        try {
            return (DataObject) in.readObject();
        } catch (ClassNotFoundException e) {
            String errorMessage = "Unknown object received";
            System.err.println(errorMessage);
            sendMessage(DataObject.errorData(errorMessage));
            e.printStackTrace();
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setChangeUsernameController(ChangeUsernameController changeUsernameController) {
        this.changeUsernameController = changeUsernameController;
    }

    public void sendMessage(String message) throws IOException {
        sendMessage(DataObject.publicMessageData(username, message));
    }

    public void sendMessage(DataObject dataObject) throws IOException {
        out.writeObject(dataObject);
    }

    public void sendPrivateMessage(String message, String selectedRecipient) throws IOException {
        sendMessage(DataObject.privateMessageData(username, selectedRecipient, message));
    }
}
