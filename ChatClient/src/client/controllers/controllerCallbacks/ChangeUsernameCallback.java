package client.controllers.controllerCallbacks;

public interface ChangeUsernameCallback {
    void changeUsername();

    void errorNotification(String errorText, boolean visibility);

    void successNotification(String text, boolean visibility);
}
