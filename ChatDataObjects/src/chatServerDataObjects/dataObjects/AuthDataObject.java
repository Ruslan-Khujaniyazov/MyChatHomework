package chatServerDataObjects.dataObjects;

import java.io.Serializable;

public class AuthDataObject implements Serializable {

    private final String login;
    private final String password;


    public AuthDataObject(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
