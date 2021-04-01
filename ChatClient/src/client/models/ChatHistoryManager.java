package client.models;


import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ChatHistoryManager {

    private final static int LINES_TO_READ = 99;

    private static Path historyPath;

    public static void writeToHistory(String message) {

        try {
            Files.write(historyPath, (message + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //чтение всего файла, а затем последних строк
/*    public static String readLastLinesFromHistory() {
        StringBuilder lastHistoryLines = new StringBuilder();

        try {
            List<String> allHistoryLines = Files.readAllLines(historyPath, StandardCharsets.UTF_8);
            int endIndex = allHistoryLines.size() - 1;
            int startIndex;

            if(allHistoryLines.size() == 0) {
                return null;

            } else if (allHistoryLines.size() < LINES_TO_READ) {
                startIndex = 0;
            } else {
                startIndex = endIndex - LINES_TO_READ;
            }

            for (int i = startIndex; i <= endIndex ; i++) {
                lastHistoryLines.append(allHistoryLines.get(i)).append(System.lineSeparator());
            }

            //return lastHistoryLines.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return String.valueOf(lastHistoryLines);
    }*/

    //чтение последних строк без чтения всего файла
    public static String readLastLinesFromHistory() {
        int linesToRead = LINES_TO_READ;

        StringBuilder builder = new StringBuilder();
        try(RandomAccessFile raf = new RandomAccessFile(historyPath.toString(), "r")) {
            long pos = Files.size(historyPath) - 1;
            raf.seek(pos);

            for (long i = pos - 1; i >= 0; i--) {
                raf.seek(i);
                if((char) raf.read() == '\n' && --linesToRead == 0) {
                    break;
                }
            }

            byte[] buffer = new byte[8192];
            int countRead;
            while((countRead = raf.read(buffer)) > 0) {
                builder.append(new String(buffer, 0, countRead, StandardCharsets.UTF_8)).append("\n");
            }

            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(builder);
    }


    public static void setHistoryPath(String login) {
        historyPath = Paths.get(String.format("ChatClient/src/client/history/%s-history.txt", login));
    }


}
    //private static String login;

//historyPath = Paths.get(String.format("ChatClient/src/client/history/%s-history.txt", login)); - in writeToHistory method

    /*public static void setLogin(String login) {
        ChatHistoryManager.login = login;

    }*/
