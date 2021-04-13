import chat.MyServer;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ServerApp {

    public static final int DEFAULT_PORT = 8189;
    public static final Logger logger = Logger.getLogger(ServerApp.class.getName());

    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);  //не до конца понял этот блок. без него работает. суть понял, но надо разобрать подробнее
        }

        try {
            LogManager.getLogManager().readConfiguration(
                    ServerApp.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }

        try {
            new MyServer(port).start();

        } catch (IOException e) {
            logger.severe("Error!  " + e.getMessage());
            System.out.println("Error!");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
