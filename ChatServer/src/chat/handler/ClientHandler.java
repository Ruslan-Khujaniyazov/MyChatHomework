package chat.handler;

import chat.MyServer;
import chat.auth.AuthService;
import chat.auth.SqliteDBConnection;
import chatServerDataObjects.DataObject;
import chatServerDataObjects.DataObjectTypes;
import chatServerDataObjects.dataObjects.*;


import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ClientHandler {

    private final MyServer myServer;
    private final Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String username;

    private TimerTask closeConnectionOnTimer;
    private boolean isAuthPassed = false;

    private final SqliteDBConnection sqliteDBConnection = SqliteDBConnection.getInstance();
    public static final Logger logger = Logger.getLogger(ClientHandler.class.getName());


    public ClientHandler(MyServer myServer, Socket clientSocket) {
        this.myServer = myServer;
        this.clientSocket = clientSocket;
    }

    public void handle() throws IOException {
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());

        ExecutorService executorService = Executors.newCachedThreadPool();

        closeConnectionOnTimer = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!isAuthPassed) {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    logger.severe(e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        new Timer().schedule(closeConnectionOnTimer, 120000);

        executorService.execute(() -> {
            try {
                authentication();
                //ожидание и чтение сообщений
                readMessage();
            } catch (IOException e) {
                e.printStackTrace();
                logger.severe(e.getMessage());
            }
        });
    }

    private void authentication() throws IOException {
        while (true) {
            DataObject dataObject = readData();

            if (dataObject == null) {
                continue;
            }

            if (dataObject.getType() == DataObjectTypes.AUTH) {
                isAuthPassed = processAuthData(dataObject);

                if (isAuthPassed) {
                    break;
                }

            } else {
                sendMessage(DataObject.authErrorData("Authorization error!"));
                logger.info("Authorization error! User: " + this.username);
            }
        }
    }

    private DataObject readData() throws IOException {
        try {
            return (DataObject) in.readObject();
        } catch (ClassNotFoundException e) {
            String errorMessage = "Unknown object received";
            logger.warning(errorMessage + " " + e.getMessage());
            System.err.println(errorMessage);
            e.printStackTrace();
            return null;
        }
    }

    private boolean processAuthData(DataObject dataObject) throws IOException {
        AuthDataObject authDataObject = (AuthDataObject) dataObject.getData();
        String login = authDataObject.getLogin();
        String password = authDataObject.getPassword();

        AuthService authService = myServer.getAuthService();
        username = authService.getUsernameByLoginAndPassword(login, password);
        if (username != null) {
            //проверка свободен ли логин
            if (myServer.isUsernameUsed(username)) {
                sendMessage(DataObject.errorData("Login is already in use!"));
            }
            //отправка никнейма на клиент
            sendMessage(DataObject.authPassedData(username));
            //сообщить о подключении нового пользователя
            String message = String.format(">>> %s has entered the chat", username);
            myServer.broadcastServerMessage(this, DataObject.serverMessageData(message));
            //регистрация пользователя
            myServer.registration(this);
            return true;
        } else {
            sendMessage(DataObject.authErrorData("Invalid login or password"));
            return false;
        }
    }

    private void readMessage() throws IOException {
        while (true) {
            DataObject dataObject = readData();

            if (dataObject == null) {
                continue;
            }

            switch (dataObject.getType()) {
                //чтобы в случае DISCONNECT закрывал все и на сервере и на клиенте, чтобы не было ошибок при закрытии
                case DISCONNECT: {
                    myServer.unRegistration(this);
                    String message = String.format(">>> %s has left the chat", username);
                    myServer.broadcastServerMessage(this, DataObject.serverMessageData(message));
                    this.closeConnection();
                    break;
                }
                case PUBLIC_MSG: {
                    PublicMessageDataObject publicMessageData = (PublicMessageDataObject) dataObject.getData();
                    String sender = publicMessageData.getSender();
                    String message = publicMessageData.getMessage();
                    myServer.broadcastMessage(DataObject.publicMessageData(sender, message));
                    break;
                }
                case PRIVATE_MSG: {
                    PrivateMessageDataObject privateMessageData = (PrivateMessageDataObject) dataObject.getData();
                    String sender = privateMessageData.getSender();
                    String receiver = privateMessageData.getReceiver();
                    String message = privateMessageData.getMessage();
                    myServer.privateMessage(this, receiver, DataObject.privateMessageData(sender, receiver, message));
                    break;
                }
                case CHANGE_USERNAME: {
                    ChangeUsernameDataObject changeUsernameData = (ChangeUsernameDataObject) dataObject.getData();
                    String currentUsername = username;
                    String newUsername = changeUsernameData.getNewUsername();

                    if (myServer.isUsernameUsed(newUsername)) {
                        sendMessage(DataObject.changeUsernameErrorData("Login is already in use!"));

                    } else if (!sqliteDBConnection.changeUsernameInDB(username, newUsername)) {
                        sendMessage(DataObject.changeUsernameErrorData("Error changing username in Database"));

                        String logMessage = String.format("Error changing username in Database by User \"%s\" to \"%s\" ", username, newUsername);
                        logger.warning(logMessage);

                    } else {
                        sendMessage(DataObject.changeUsernameSuccessData(newUsername));
                        myServer.removeOldAndAddNewUsername(currentUsername, newUsername);
                        this.username = newUsername;
                        String message = String.format(">>> %s has changed the nickname to %s", currentUsername, newUsername);
                        logger.info(message);
                        myServer.broadcastServerMessage(this, DataObject.serverMessageData(message));

                    }

                    break;
                }
                case ERROR: {
                    ErrorDataObject clientErrorData = (ErrorDataObject) dataObject.getData();
                    String errorMessage = clientErrorData.getErrorMessage();
                    System.err.printf("Client side error:\n username: %s\n Error: %s", this.getUsername(), errorMessage);

                    String logMessage = String.format("Client side error:\n username: %s\n Error: %s", this.getUsername(), errorMessage);
                    logger.warning(logMessage);

                    break;
                }
                default:
                    String errorMessage = "Unknown object received" + dataObject.getType();
                    logger.warning("Error reading message: " + errorMessage);
                    System.err.println(errorMessage);
                    sendMessage(DataObject.errorData(errorMessage));
            }
        }
    }

    public String getUsername() {
        return username;
    }


    //отправление списка подключенных пользователей, при подключении данного нового пользователя
    public void sendConnectedUsersList(DataObject dataObject) throws IOException {
        out.writeObject(dataObject);
    }

    //добавление и удаление пользователя из списка подключенных
    public void sendAddOrRemoveUserRequest(DataObject dataObject) throws IOException {
        out.writeObject(dataObject);
    }

    public void sendMessage(DataObject dataObject) throws IOException {
        out.writeObject(dataObject);
    }

    public void closeConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();

        String logMessage = String.format("Connection closed. User \"%s\" disconnected.", this.username);
        logger.warning(logMessage);
    }
}
