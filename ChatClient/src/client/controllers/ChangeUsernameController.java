package client.controllers;

import client.NetworkClient;
import client.models.Network;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ChangeUsernameController {

    @FXML
    private TextField newUsernameField;
    @FXML
    private Label errorNotification;
    @FXML
    private Label successNotification;


    private Network network;
    private NetworkClient networkClient;


    @FXML
    private void changeUsername() {
        //network.setChangeUsernameController(this);

        String newUsername = newUsernameField.getText();

        if(!newUsername.isEmpty()) {
            network.changeUsername(newUsername);
        } else {
            errorNotification("The field can not be empty!", true);
        }
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setNetworkClient(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    public void errorNotification(String errorText, boolean visibility) {
        successNotification.setVisible(false);
        errorNotification.setText(errorText);
        errorNotification.setVisible(visibility);


    }

    public void successNotification(String text, boolean visibility) {
        errorNotification.setVisible(false);
        successNotification.setText(text);
        successNotification.setVisible(visibility);
    }

}
