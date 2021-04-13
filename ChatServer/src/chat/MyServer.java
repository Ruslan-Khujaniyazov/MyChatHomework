package chat;

import chat.auth.AuthService;
import chat.auth.BaseAuthService;
import chat.auth.SqliteDBConnection;
import chat.handler.ClientHandler;
import chatServerDataObjects.DataObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MyServer {

    private final ServerSocket serverSocket;
    private final AuthService authService;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final List<String> connectedUsernamesList = new ArrayList<>();
    private static final Logger logger = Logger.getLogger(MyServer.class.getName());

    public MyServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.authService = new BaseAuthService();
    }


    public void start() throws IOException {
        logger.info("Server is running!");
        System.out.println("Server is running!");
        authService.start();

        try (SqliteDBConnection sqliteDBConnection = SqliteDBConnection.getInstance()) {
            while (true) {
                waitAndAcceptNewClient();
            }
        } catch (IOException e) {
            logger.severe("Failed to establish a new connection!  " + e.getMessage());
            System.out.println("Failed to establish a new connection!");
            e.printStackTrace();
        } finally {
            serverSocket.close();
        }
    }

    private void waitAndAcceptNewClient() throws IOException {
        System.out.println("Waiting for user connection...");
        Socket clientSocket = serverSocket.accept();
        logger.info("User connected!");
        System.out.println("User connected!");
        clientConnectionProcessing(clientSocket);
    }

    private void clientConnectionProcessing(Socket clientSocket) throws IOException {
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.handle();
    }

    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isUsernameUsed(String username) {
        for (ClientHandler client : clients) {
            if(client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void registration(ClientHandler clientHandler) throws IOException {
        clients.add(clientHandler);
        connectedUsernamesList.add(clientHandler.getUsername());
        //отправление списка уже подключенныхпользователей новому пользователю
        if(!connectedUsernamesList.isEmpty()) {
            clientHandler.sendConnectedUsersList(DataObject.connectedUsersList(connectedUsernamesList));
        }
        //добавление нового пользователя в список каждого пользователя
        broadcastAddUser(clientHandler.getUsername());
    }

    public synchronized void unRegistration(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        connectedUsernamesList.remove(clientHandler.getUsername());
        broadcastRemoveUser(clientHandler.getUsername());
    }
        //удаление старого ника из списков и добавление нового при смене ника
    public synchronized void removeOldAndAddNewUsername (String oldUsername, String newUsername) throws IOException {
        connectedUsernamesList.remove(oldUsername);
        connectedUsernamesList.add(newUsername);
        broadcastRemoveUser(oldUsername);
        broadcastAddUser(newUsername);
    }

    //добавление и удаление пользователя из списка подключенных
    public synchronized void broadcastAddUser(String username) throws IOException {
        for (ClientHandler client : clients) {
            if(!client.getUsername().equals(username)){
            client.sendAddOrRemoveUserRequest(DataObject.addNewUserToListData(username));
            }
        }
    }

    public synchronized void broadcastRemoveUser(String username) throws IOException {
        for (ClientHandler client : clients) {
            client.sendAddOrRemoveUserRequest(DataObject.removeUserFromListData(username));
        }
    }

    public synchronized void broadcastMessage(DataObject dataObject) throws IOException {
        for (ClientHandler client : clients) {
            client.sendMessage(dataObject);
        }
    }

    public synchronized void broadcastServerMessage(ClientHandler sender, DataObject serverMessageData) throws IOException {
        for (ClientHandler client : clients) {
            if(!client.equals(sender)) {
                client.sendMessage(serverMessageData);
            }
        }
    }

    public synchronized void privateMessage(ClientHandler sender, String receiver, DataObject privateMessageData) throws IOException {
        for (ClientHandler client : clients) {

            if(client.equals(sender) || client.getUsername().equals(receiver)) {
                client.sendMessage(privateMessageData);
            }
        }
    }
}
