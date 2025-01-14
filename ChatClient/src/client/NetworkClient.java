package client;

import client.controllers.AuthController;
import client.controllers.ChangeUsernameController;
import client.controllers.ChatController;
import client.models.ChatHistoryManager;
import client.models.Network;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class NetworkClient extends Application {

    private Stage primaryStage;
    private Stage authStage;
    private Stage changeUsernameStage;
    private Network network;
    private ChatController chatController;


    @Override
    public void start(Stage primaryStage) throws Exception {

        this.primaryStage = primaryStage;

        network = new Network();
        if (!network.connect()) {
            showErrorMessage("Connection error", "", "Server connection failed!");
            return;
        }

        openAuthWindow();
        createMainChatWindow();
    }

    public void openAuthWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(NetworkClient.class.getResource("views/auth-view.fxml"));
        Parent root = loader.load();
        authStage = new Stage();

        authStage.setTitle("Authorization");
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(primaryStage);
        //Scene scene = new Scene(root);
        authStage.setScene(new Scene(root));
        authStage.show();

        AuthController authController = loader.getController();
        authController.setNetwork(network);
        authController.setNetworkClient(this);


    }

    public void createMainChatWindow() throws java.io.IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(NetworkClient.class.getResource("views/chat-view.fxml"));

        Parent root = loader.load();

        primaryStage.setTitle("MyMessenger");
        primaryStage.setScene(new Scene(root, 600, 400));

        chatController = loader.getController();
        chatController.setNetwork(network);
        chatController.setNetworkClient(this);

        primaryStage.setOnCloseRequest(windowEvent -> {
            try {
                chatController.disconnectFromServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void showErrorMessage(String title, String message, String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void openMainChatWindow() {
        authStage.close();
        chatController.setLabel(network.getUsername());
        chatController.setChatHistory(ChatHistoryManager.readLastLinesFromHistory());
        primaryStage.show();
        //primaryStage.setAlwaysOnTop(true);
        network.waitMessage(chatController);
        chatController.appendMessage("Connection established!");
    }

    public void reOpenAuthWindow() throws IOException {
        network = new Network();
        if (!network.connect()) {
            showErrorMessage("Connection error", "", "Server connection failed!");
            return;
        }
        primaryStage.close();
        //primaryStage.hide();
        openAuthWindow();
        createMainChatWindow();
    }

    public void openChangeUsernameWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(NetworkClient.class.getResource("views/changeUsername-view.fxml"));
        Parent root = loader.load();
        changeUsernameStage = new Stage();

        changeUsernameStage.setTitle("Change Name");
        changeUsernameStage.initModality(Modality.WINDOW_MODAL);
        changeUsernameStage.initOwner(primaryStage);
        //Scene scene = new Scene(root);
        changeUsernameStage.setScene(new Scene(root));
        changeUsernameStage.show();

        ChangeUsernameController changeUsernameController = loader.getController();
        changeUsernameController.setNetwork(network);
        changeUsernameController.setNetworkClient(this);
        network.setChangeUsernameController(changeUsernameController); //todo через callback переделать
    }
}
