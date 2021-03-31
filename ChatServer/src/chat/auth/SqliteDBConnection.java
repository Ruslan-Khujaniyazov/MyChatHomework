package chat.auth;

import java.sql.*;

public class SqliteDBConnection implements AutoCloseable {
    private static SqliteDBConnection instance;
    private static Connection connection;

    private SqliteDBConnection() { }

    public static SqliteDBConnection getInstance() {
      if(instance == null) {
          loadDriverAndOpenConnection();
          instance = new SqliteDBConnection();
      }

      return instance;
    }

    private static void loadDriverAndOpenConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:User.db");
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
    }

    public String findUsernameByLoginAndPassword(String login, String password) {
        String query = String.format("SELECT * FROM user WHERE LOWER(login) = LOWER(\"%s\") AND password = \"%s\"", login, password);

        try (Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)){
            if(resultSet.next()) {
                return resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean changeUsernameInDB(String currentUsername, String newUsername) {
        String query = String.format("UPDATE user SET username = \"%s\" WHERE username = \"%s\"", newUsername, currentUsername);

        try (Statement statement = connection.createStatement()) {
            int linesChanged = statement.executeUpdate(query);
            if(linesChanged == 1) {
                return true;
            }
        } catch (SQLException e) {
        e.printStackTrace();
    }
        return false;
    }


    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
