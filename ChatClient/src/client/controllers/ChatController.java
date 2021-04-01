package client.controllers;

import chatServerDataObjects.DataObject;
import client.NetworkClient;
import client.models.ChatHistoryManager;
import client.models.Network;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class ChatController {

    @FXML
    private ListView<String> usersList;

    @FXML
    private Button sendButton;
    @FXML
    private TextArea chatHistory;
    @FXML
    private TextField textField;
    @FXML
    private Label usernameTitle;
    @FXML
    private Button connectToServerButton;
    @FXML
    private Button disconnectFromServerButton;
    @FXML
    private Button changeNameButton;



    private Network network;
    private NetworkClient networkClient;
    private String selectedRecipient;

    private static final ObservableList<String> connectedUsersList = FXCollections.observableArrayList();

    public static ObservableList<String> getConnectedUsersList() {
        return connectedUsersList;
    }

    public void setLabel(String usernameTitle) {
        this.usernameTitle.setText(usernameTitle);
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setChatHistory(String lastLinesOfHistory) {
        if(lastLinesOfHistory != null) {

            chatHistory.appendText(lastLinesOfHistory);
        }
    }

    public void setNetworkClient(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    @FXML
    public void initialize() {
        usersList.setItems(connectedUsersList);
        sendButton.setOnAction(event -> ChatController.this.sendMessage());
        textField.setOnAction(event -> ChatController.this.sendMessage());

        changeNameButton.setOnAction(event -> {
            try {
                ChatController.this.openChangeUsernameWindow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        connectToServerButton.setOnAction(event -> {
            try {
                ChatController.this.connectToServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        disconnectFromServerButton.setOnAction(event -> {
            try {
                ChatController.this.disconnectFromServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        //todo понял что делает эта фабрика, но пока не до конца понял суть.. кода, что, за чем и откуда берется. Преподаватель сказал что писал не сам, а скопировал откуда то, и подробно каждая строчка не разбиралась, только общий принцип работы. Нужно разобрать.
        usersList.setCellFactory(lv -> {
            MultipleSelectionModel<String> selectionModel = usersList.getSelectionModel();
            ListCell<String> cell = new ListCell<>();
            cell.textProperty().bind(cell.itemProperty());
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                usersList.requestFocus();
                if (!cell.isEmpty()) {
                    int index = cell.getIndex();
                    if (selectionModel.getSelectedIndices().contains(index)) {
                        selectionModel.clearSelection(index);
                        selectedRecipient = null;
                    } else {
                        selectionModel.select(index);
                        selectedRecipient = cell.getItem();
                    }
                    event.consume();
                }
            });
            return cell;
        });
    }

    private void sendMessage() {
        String message = textField.getText();
        if (!message.isEmpty()) {
            //appendMessage("You: " + message); - сообщение должно проходить через сервер, чтобы не было путаницы.. Или нет?
            textField.clear();

            try {
                if (selectedRecipient != null) {
                    network.sendPrivateMessage(message, selectedRecipient);

                } else {
                    network.sendMessage(message);
                }

            } catch (IOException e) {
                e.printStackTrace();
                NetworkClient.showErrorMessage("Connection error!", "Sending message error", e.getMessage());
            }
        }
    }

    public void appendMessage(String message) {
        String timestamp = DateFormat.getInstance().format(new Date());
        chatHistory.appendText(timestamp);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(message);
        chatHistory.appendText(System.lineSeparator());
        chatHistory.appendText(System.lineSeparator());
        ChatHistoryManager.writeToHistory(timestamp + System.lineSeparator() + message);
    }

    @FXML
    private void openChangeUsernameWindow() throws IOException {
        networkClient.openChangeUsernameWindow();
    }

    @FXML
    private void connectToServer() throws IOException {
        if (!network.isConnected()) {
            if (network.connect()) {
                networkClient.reOpenAuthWindow();

            }
        }
    }
    @FXML
    public void disconnectFromServer() throws IOException {
        if(network.isConnected()) {
        network.sendMessage(DataObject.disconnectData(network.getUsername()));
        network.closeConnection();
        appendMessage(">>> You have disconnected from server...");
        connectedUsersList.clear();
        }
    }

}