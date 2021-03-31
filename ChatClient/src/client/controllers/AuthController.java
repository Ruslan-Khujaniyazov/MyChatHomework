package client.controllers;

import client.NetworkClient;
import client.models.ChatHistoryManager;
import client.models.Network;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AuthController {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;

    private Network network;
    private NetworkClient networkClient;

    @FXML
    private void  checkAuth() {
        String login = loginField.getText();
        String password = passwordField.getText();

        if(login.isEmpty() || password.isEmpty()) {
            NetworkClient.showErrorMessage("Authorization error!", "Input error", "The fields can not be empty!");
            return;
        }

        String authErrorMessage = network.sendAuthCommand(login, password);
         if(authErrorMessage != null) {
            NetworkClient.showErrorMessage("Authorization error!", "Invalid login or password", authErrorMessage);
        } else {
             ChatHistoryManager.setHistoryPath(login);
             networkClient.openMainChatWindow();
         }
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setNetworkClient(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

}
