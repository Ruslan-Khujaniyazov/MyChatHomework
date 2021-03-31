import chat.MyServer;

import java.io.IOException;

public class ServerApp {

    public static final int DEFAULT_PORT = 8189;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);  //не до конца понял этот блок. без него работает. суть понял, но надо разобрать подробнее
        }

        try {
            new MyServer(port).start();
        } catch (IOException e) {
            System.out.println("Error!");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
