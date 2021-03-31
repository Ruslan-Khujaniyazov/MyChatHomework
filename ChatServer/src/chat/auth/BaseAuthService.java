package chat.auth;

public class BaseAuthService implements AuthService {

    private final SqliteDBConnection sqliteDBConnection = SqliteDBConnection.getInstance();


    @Override
    public void start() {
        System.out.println("Authentication service is running");
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {

        return sqliteDBConnection.findUsernameByLoginAndPassword(login, password);

    }

    @Override
    public void close() {
        System.out.println("Authentication service finished");
    }
}
